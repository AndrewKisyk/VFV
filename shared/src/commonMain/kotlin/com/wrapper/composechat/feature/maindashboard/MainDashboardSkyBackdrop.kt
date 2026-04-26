package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.feature.auth.RingOvalStrokeDecoration
import com.wrapper.composechat.feature.auth.blobMagnitude
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─── Screen base colour (below the mask shape) ───────────────────────────────
private val ScreenBase = Color(0xFF020725)

// ─── Gradient colours ─────────────────────────────────────────────────────────
// Top-of-mask colour cycles through 3 stops: #0C012B → #42098F → #B53FFE
private val GradStart0 = Color(0xFF0C012B)
private val GradStart1 = Color(0xFF42098F)
private val GradStart2 = Color(0xFF42098F)
// Bottom colour crossfades between night and dawn (sunrise simulation)
private val NightBottom = Color(0xFF0C012B)
private val NightMedium = Color(0xFF400D91)
private val DawnBottom  = Color(0xFF7A42DA)

// Matching the auth easing so the crossfade feels part of the same world
private val DashGradientEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

/** Smooth 3-stop colour interpolation: a → b (t ∈ 0..0.5) then b → c (t ∈ 0.5..1). */
private fun lerpThree(a: Color, b: Color, c: Color, t: Float): Color =
    if (t <= 0.5f) lerp(a, b, t * 2f) else lerp(b, c, (t - 0.5f) * 2f)

// ─── Cross-sparkle star positions (normalized 0..1) ──────────────────────────
private val SparklePositions = listOf(
    Offset(0.906f, 0.192f),
    Offset(0.952f, 0.307f),
    Offset(0.074f, 0.110f),
)

// ─── Dot-star positions (26 stars, seeded for repeatable layout) ─────────────
private val StarSeeds = intArrayOf(
    3,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,
)
private val StarCount = StarSeeds.size

/**
 * Full-screen night-sky backdrop for the main dashboard.
 *
 *  • Screen below the mask is filled with solid [ScreenBase] (#020725).
 *  • The SVG "Mask group" shape covers the top ~40% of the screen and is filled with an
 *    animated gradient (night → dawn crossfade, same easing as [VfvAuthBackground]).
 *    The vertical gradient runs only inside the mask, **from mask-bottom to mask-top**; night/dawn
 *    crossfade sits at the **bottom**, 3-stop colour animation at the **top**. Area below the mask is
 *    solid [ScreenBase].
 *  • [StarCount] dot-stars, each assigned its own twinkle rate via 6 independent animators.
 *  • 3 cross-sparkle stars with independent blink rhythms.
 *  • 2 soft radial orbs drifting with [blobMagnitude] kinematics.
 *  • 3 sky rings rendered via [RingOvalStrokeDecoration] at the identical positions and sizes
 *    used in the auth screen.
 *  • No bitmap assets — everything drawn in Canvas.
 */
@Composable
fun MainDashboardSkyBackdrop(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "dashSky")

    // ── Start-colour animation: #0C012B → #42098F → #B53FFE (and back) ────
    // LinearEasing gives an even pace across all three stops; Reverse makes it
    // retrace the same path so #42098F is always visited in both directions.
    val startColorT by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "startColorT",
    )

    // ── Bottom-colour sunrise crossfade ────────────────────────────────────
    val gradBlend by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(18_000, easing = DashGradientEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradBlend",
    )

    // ── Shared morphing phases (rings + orbs) ──────────────────────────────
    // Reverse (not Restart): 0 → 2π then back without snapping — matches auth’s morphA/morphB.
    // Restart caused a one-frame jump at 2π→0, which [blobMagnitude] does not make continuous.
    val phA by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "phA",
    )
    val phB by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10_400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "phB",
    )

    // ── 6 independent star twinkle animators ──────────────────────────────
    val tw0 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(1_100), RepeatMode.Reverse), "tw0")
    val tw1 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(1_650), RepeatMode.Reverse), "tw1")
    val tw2 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(2_280), RepeatMode.Reverse), "tw2")
    val tw3 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(890),   RepeatMode.Reverse), "tw3")
    val tw4 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(3_150), RepeatMode.Reverse), "tw4")
    val tw5 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(1_820), RepeatMode.Reverse), "tw5")
    val twinkles = floatArrayOf(tw0, tw1, tw2, tw3, tw4, tw5)

    // ── 3 independent cross-sparkle blink animators ──────────────────────
    val sp0 by inf.animateFloat(0.20f, 1.00f, infiniteRepeatable(tween(2_400), RepeatMode.Reverse), "sp0")
    val sp1 by inf.animateFloat(0.15f, 0.90f, infiniteRepeatable(tween(3_800), RepeatMode.Reverse), "sp1")
    val sp2 by inf.animateFloat(0.10f, 0.85f, infiniteRepeatable(tween(1_920), RepeatMode.Reverse), "sp2")
    val sparkleAlphas = floatArrayOf(sp0, sp1, sp2)

    // Fixed star positions and radii (computed once)
    val starPositions = remember {
        List(StarCount) { i ->
            val s = StarSeeds[i]
            Offset(
                0.04f + 0.92f * ((s * 17 + i * 13) % 100) / 100f,
                0.03f + 0.44f * ((s *  7 + i * 11) % 100) / 100f,
            )
        }
    }
    val starRadii = remember { FloatArray(StarCount) { i -> 0.9f + 0.75f * (i % 4) * 0.33f } }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBase),   // solid base colour for the whole screen
    ) {
        // ── Canvas: gradient mask shape + stars + orbs + sparkles ──────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── 1. SVG "Mask group" shape ─────────────────────────────────
            // SVG viewBox 375×240; top ~40% of screen; wavy bottom edge.
            // Vertical gradient: bottom → top: crossfade at bottom, 3-stop animation at top.
            val shapeH = h * 0.40f
            val sx = w / 375f
            val sy = shapeH / 240f
            val maskPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, 214.737f * sy)
                lineTo(228.894f * sx, 234.423f * sy)
                cubicTo(
                    201.422f * sx, 238.124f * sy,
                    173.578f * sx, 238.124f * sy,
                    146.106f * sx, 234.423f * sy,
                )
                lineTo(0f, 214.737f * sy)
                close()
            }
            // maskBotY is the actual pixel y of the lowest point of the wavy edge
            val maskBotY = 234.423f * sy
            clipPath(maskPath) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to lerpThree(NightBottom, NightMedium,DawnBottom, gradBlend),
                            1f to lerpThree(GradStart0, GradStart1, GradStart2, startColorT),
                        ),
                        // Fraction 0 = mask bottom: crossfade; 1 = mask top: 3-colour animation
                        startY = maskBotY,
                        endY   = 0f,
                    ),
                )
            }

            // ── 2. Radial glow centred at the mask's bottom edge ───────────
            val glowA = 0.22f + 0.10f * gradBlend
            val glowR = w * 0.78f * (1f + 0.06f * sin(phA * 0.4f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        lerp(Color(0xFF400D91), Color(0xFF6C28CC), gradBlend).copy(alpha = glowA),
                        Color(0xFF400D91).copy(alpha = 0f),
                    ),
                    center = Offset(w * 0.5f, maskBotY),
                    radius = glowR,
                ),
                radius = glowR,
                center = Offset(w * 0.5f, maskBotY),
            )

            // ── 3. Soft radial orbs (auth-style drift) ─────────────────────
            drawSkyOrb(
                cx     = w * 0.80f + 14f * blobMagnitude(phA, 1f, 0f),
                cy     = h * 0.28f + 10f * blobMagnitude(phB, 0.9f, 0.1f),
                radius = w * 0.48f * (1f + 0.08f * blobMagnitude(phA + 0.4f, 1f, 0f)),
                color  = Color(0xFFD83AFF),
                alpha  = 0.16f,
            )
            drawSkyOrb(
                cx     = w * 0.18f + 10f * blobMagnitude(phB, 1.1f, 0.15f),
                cy     = h * 0.20f + 8f  * blobMagnitude(phA, 0.85f, 0.2f),
                radius = w * 0.38f * (1f + 0.09f * blobMagnitude(phB * 1.1f, 1.15f, 0f)),
                color  = Color(0xFF3AE7FF),
                alpha  = 0.12f,
            )

            // ── 4–5. Stars + cross sparkles — clipped to mask only (nothing below the wavy edge)
            clipPath(maskPath) {
                for (i in 0 until StarCount) {
                    val tw    = twinkles[i % 6]
                    val phase = tw + i * 0.43f
                    val alpha = (0.22f + 0.73f * (0.5f + 0.5f * sin(phase * PI.toFloat())))
                        .coerceIn(0.04f, 0.96f)
                    val r     = starRadii[i] * (0.60f + 0.60f * tw)
                    drawCircle(
                        color  = Color.White.copy(alpha = alpha),
                        radius = r,
                        center = Offset(
                            starPositions[i].x * w + 1.8f * sin(phA + i * 0.44f),
                            starPositions[i].y * h + 1.6f * cos(phB + i * 0.40f),
                        ),
                    )
                }
                SparklePositions.forEachIndexed { i, p ->
                    drawCrossSparkle(
                        center = Offset(p.x * w, p.y * h),
                        size   = 10f + 4f * (i % 3),
                        alpha  = sparkleAlphas[i],
                    )
                }
            }
        }

        // ── Rings: exact same composable + positions as VfvAuthBackground ──
        // Large ring — top-start, partially off-screen (auth "bigRing" layout)
        RingOvalStrokeDecoration(
            phA   = phA,
            phB   = phB,
            large = true,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset((-100).dp, (-100).dp)
                .size(280.dp),
        )
        // Medium ring — top-end (auth second ring)
        RingOvalStrokeDecoration(
            phA   = phA,
            phB   = phB,
            large = false,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 132.dp, end = 8.dp)
                .size(90.dp),
        )
    }
}

// ─── Draw helpers ─────────────────────────────────────────────────────────────

private fun DrawScope.drawSkyOrb(cx: Float, cy: Float, radius: Float, color: Color, alpha: Float) {
    val c = Offset(cx, cy)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)),
            center = c, radius = radius,
        ),
        radius = radius, center = c,
    )
}

/**
 * Four-point cross sparkle with fade-to-transparent arms + a bright centre dot.
 * Replicates the `blend-mode:plus-lighter` cross specks from the reference SVG.
 */
private fun DrawScope.drawCrossSparkle(center: Offset, size: Float, alpha: Float) {
    if (alpha <= 0.02f) return
    val a  = alpha.coerceIn(0f, 1f)
    val sw = (size * 0.13f).coerceAtLeast(0.8f)
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(0f), Color.White.copy(a), Color.White.copy(0f)),
            start  = Offset(center.x - size, center.y),
            end    = Offset(center.x + size, center.y),
        ),
        start = Offset(center.x - size, center.y),
        end   = Offset(center.x + size, center.y),
        strokeWidth = sw, cap = StrokeCap.Round,
    )
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(0f), Color.White.copy(a), Color.White.copy(0f)),
            start  = Offset(center.x, center.y - size),
            end    = Offset(center.x, center.y + size),
        ),
        start = Offset(center.x, center.y - size),
        end   = Offset(center.x, center.y + size),
        strokeWidth = sw, cap = StrokeCap.Round,
    )
    drawCircle(color = Color.White.copy(alpha = a * 0.88f), radius = sw * 1.4f, center = center)
}
