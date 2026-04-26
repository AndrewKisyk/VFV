package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val SmallMarksColor = Color(0xFF793BC4)
private val LargeMarksColor = Color(0xFF9760D1)

private const val Vb = 171f
private const val RSmallOuter = 84f
private const val RLargeInner = 81.7778f
private const val SmallStroke = 4.66667f
private const val SmallDashOn = 0.34f
private const val SmallGap = 0.89f

/** 24 large tick marks, centre every 15° (12h = 270° in [drawArc] space, 0 = 3h, CW+). */
private const val LargeMarkCount = 24
private const val LargeMarkStepDeg = 15f

/**
 * Label placement: [drawArc] degrees (0 = 3h, CW+), from [progress_view.svg] cardinals.
 * Distance from artboard centre (85.33, 85.33) to each numeral group, in 171 viewBox units —
 * slightly asymmetric like the SVG, further **in** from the rings (r≈81.8) than before.
 */
private val MarkLabelSpecs = listOf(
    "0" to 270f,
    "25" to 0f,
    "50" to 90f,
    "75" to 180f,
)

/** per-label radius in viewBox (≈ path centres in [progress_view.svg]). */
private val LabelRadiusInViewBox: Map<String, Float> = mapOf(
    "0" to 64.5f,
    "25" to 70.5f,
    "50" to 66.5f,
    "75" to 64.5f,
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun ProgressScaleDial(
    modifier: Modifier = Modifier,
    labelTextSize: TextUnit = 12.sp,
) {
    val family = LocalVfvDisplayFontFamily.current
    val labelColor = Color.White.copy(alpha = 0.8f)
    val textStyle = TextStyle(
        fontFamily = family,
        fontSize = labelTextSize,
        fontWeight = FontWeight.Light,
        color = labelColor,
        textAlign = TextAlign.Center,
    )
    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    BoxWithConstraints(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                val lPx = this.size.minDimension
                if (lPx <= 0f) return@Canvas
                val s = lPx / Vb
                val cc = this.center
                val rOut = (RSmallOuter / Vb) * lPx
                val rIn = (RLargeInner / Vb) * lPx
                val wStroke = SmallStroke * s
                val wStrokeLarge = wStroke * 2
                // Small dashes: 2× longer on/gap vs SVG base; large tick **length** = 2× small (see below).
                val d1 = SmallDashOn * s
                val g1 = SmallGap * s
                val dSmall = 2f * d1
                val gSmall = 2f * g1
                // Tangent length of one large tick = 2 × (tangent length of one small tick = dSmall).
                val largeSweepDeg = (
                        (2f * dSmall / rIn) * (180.0 / PI).toFloat()
                        ).coerceAtMost(14.5f)
                val half = largeSweepDeg * 0.5f
                fun ringPathOpenRight(radius: Float, gapDeg: Float) = Path().apply {
                    addArc(
                        oval = Rect(
                            left = cc.x - radius,
                            top = cc.y - radius,
                            right = cc.x + radius,
                            bottom = cc.y + radius,
                        ),
                        // Gap centred on 0° (3h / “right”): no small tick where it would coincide with 25.
                        startAngleDegrees = (gapDeg * 0.5f).coerceAtMost(85f),
                        sweepAngleDegrees = (360f - gapDeg).coerceAtLeast(1f),
                    )
                }

                val ovalIn = Rect(
                    left = cc.x - rIn,
                    top = cc.y - rIn,
                    right = cc.x + rIn,
                    bottom = cc.y + rIn,
                )
                // First large tick centred on 12h (270°), then +15° … 24 steps.
                val largeStrokes = Path().apply {
                    repeat(LargeMarkCount) { i ->
                        val centerDeg = (270f + i * LargeMarkStepDeg) % 360f
                        val start = (centerDeg - half + 360f) % 360f
                        addArc(
                            oval = ovalIn,
                            startAngleDegrees = start,
                            sweepAngleDegrees = largeSweepDeg
                        )
                    }
                }
                drawPath(
                    path = largeStrokes,
                    color = LargeMarksColor,
                    style = Stroke(
                        width = wStrokeLarge,
                        cap = StrokeCap.Butt,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dSmall, gSmall), 0f)
                        ),
                )
                // Angular width of one small dash: gap at 0° so no dashed segment sits on the right.
                val gapAtRightDeg = ((dSmall / rOut) * (180.0 / PI)).toFloat() * 1.05f
                drawPath(
                    path = ringPathOpenRight(rOut, gapAtRightDeg),
                    color = SmallMarksColor,
                    style = Stroke(
                        width = wStroke,
                        cap = StrokeCap.Butt,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dSmall, gSmall), 0f),
                    ),
                )
                for ((label, deg) in MarkLabelSpecs) {
                    val rLabelPx = lPx * (LabelRadiusInViewBox[label]!! / Vb)
                    val layout = textMeasurer.measure(
                        text = AnnotatedString(label),
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                    val rad = deg * (PI / 180.0)
                    val baseX = (cc.x + (rLabelPx * cos(rad)).toFloat())
                    val baseY = (cc.y + (rLabelPx * sin(rad)).toFloat())
                    // “25” at 3h: right margin — anchor is the right edge of the numeral, text extends left.
                    val (cx, cy) = if (label == "25") {
                        (baseX - layout.size.width) to (baseY - layout.size.height * 0.5f)
                    } else {
                        (baseX - layout.size.width * 0.5f) to (baseY - layout.size.height * 0.5f)
                    }
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(cx, cy),
                    )
                }
            }
        }
    }
}
