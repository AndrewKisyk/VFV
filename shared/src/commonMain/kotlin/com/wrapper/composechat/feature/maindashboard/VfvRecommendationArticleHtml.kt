package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private sealed interface ArticleBlock {
    data class Heading(val level: Int, val spans: List<InlineSpan>) : ArticleBlock
    data class Paragraph(
        val spans: List<InlineSpan>,
        val centered: Boolean = false,
    ) : ArticleBlock
    data class BulletItem(val spans: List<InlineSpan>) : ArticleBlock
    data class ImageBlock(val assetName: String) : ArticleBlock
}

private data class InlineSpan(
    val text: String,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val link: String? = null,
)

@Composable
internal fun VfvRecommendationArticleHtml(
    html: String,
    family: FontFamily?,
    modifier: Modifier = Modifier,
) {
    val blocks = remember(html) { parseRecommendationArticleHtml(html) }
    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEach { block ->
            when (block) {
                is ArticleBlock.Heading -> {
                    Spacer(Modifier.height(if (block.level <= 2) 14.dp else 10.dp))
                    ArticleRichText(
                        spans = block.spans,
                        family = family,
                        fontSize = when (block.level) {
                            1 -> 24.sp
                            2 -> 20.sp
                            3 -> 18.sp
                            4 -> 17.sp
                            else -> 16.sp
                        },
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                is ArticleBlock.Paragraph -> {
                    ArticleRichText(
                        spans = block.spans,
                        family = family,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = if (block.centered) TextAlign.Center else TextAlign.Start,
                    )
                    Spacer(Modifier.height(10.dp))
                }
                is ArticleBlock.BulletItem -> {
                    ArticleBulletItem(
                        spans = block.spans,
                        family = family,
                    )
                    Spacer(Modifier.height(6.dp))
                }
                is ArticleBlock.ImageBlock -> {
                    recommendationArticleImage(block.assetName)?.let { drawable ->
                        Spacer(Modifier.height(8.dp))
                        Image(
                            painter = painterResource(drawable),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleBulletItem(
    spans: List<InlineSpan>,
    family: FontFamily?,
) {
    val linkUrl = spans.firstNotNullOfOrNull { it.link }
    val uriHandler = LocalUriHandler.current
    val interactionSource = remember { MutableInteractionSource() }
    ArticleRichText(
        spans = listOf(InlineSpan("• ")) + spans,
        family = family,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(start = 8.dp)
            .then(
                if (linkUrl != null) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = interactionSource,
                    ) { uriHandler.openUri(linkUrl) }
                } else {
                    Modifier
                },
            ),
    )
}

@Composable
private fun ArticleRichText(
    spans: List<InlineSpan>,
    family: FontFamily?,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val linkListener = remember(uriHandler) {
        LinkInteractionListener { annotation ->
            val url = (annotation as? LinkAnnotation.Url)?.url ?: return@LinkInteractionListener
            uriHandler.openUri(url)
        }
    }
    val annotated = remember(spans, linkListener) {
        buildArticleAnnotatedString(spans, linkListener)
    }
    // Use Text (not ClickableText): LinkAnnotation taps are handled natively.
    // ClickableText + getLinkAnnotations is unreliable for list-item-only links (cardio).
    Text(
        text = annotated,
        modifier = modifier.fillMaxWidth(),
        style = androidx.compose.ui.text.TextStyle(
            color = Color.White.copy(alpha = 0.92f),
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = family,
            textAlign = textAlign,
            lineHeight = (fontSize.value * 1.45f).sp,
        ),
    )
}

private fun buildArticleAnnotatedString(
    spans: List<InlineSpan>,
    linkListener: LinkInteractionListener,
): AnnotatedString =
    buildAnnotatedString {
        spans.forEach { span ->
            if (span.text.isEmpty()) return@forEach
            val style = SpanStyle(
                fontWeight = if (span.bold) FontWeight.SemiBold else FontWeight.Normal,
                fontStyle = if (span.italic) FontStyle.Italic else FontStyle.Normal,
                textDecoration = if (span.link != null) TextDecoration.Underline else TextDecoration.None,
                color = if (span.link != null) {
                    Color(0xFFB8C8FF)
                } else {
                    Color.Unspecified
                },
            )
            if (span.link != null) {
                withLink(
                    LinkAnnotation.Url(
                        url = span.link,
                        styles = TextLinkStyles(style = style),
                        linkInteractionListener = linkListener,
                    ),
                ) {
                    withStyle(style) {
                        append(span.text)
                    }
                }
            } else {
                withStyle(style) {
                    append(span.text)
                }
            }
        }
    }

internal fun recommendationArticleImage(assetName: String): DrawableResource? =
    when (assetName) {
        "smart" -> Res.drawable.smart
        "max_rate" -> Res.drawable.max_rate
        "cardio2" -> Res.drawable.cardio2
        "levchenko" -> Res.drawable.levchenko
        "verticall_jump" -> Res.drawable.verticall_jump
        "long_jump_photo" -> Res.drawable.long_jump_photo
        "water_jump" -> Res.drawable.water_jump
        "maze_runner" -> Res.drawable.maze_runner
        "marathon" -> Res.drawable.marathon
        "frank" -> Res.drawable.frank
        "push_duo" -> Res.drawable.push_duo
        "abs" -> Res.drawable.abs
        "knee_rise" -> Res.drawable.knee_rise
        "single_leg" -> Res.drawable.single_leg
        "image_l" -> Res.drawable.image_l
        "img_pullup" -> Res.drawable.img_pullup
        "australian_pull" -> Res.drawable.australian_pull
        "pullover" -> Res.drawable.pullover
        "musclemain" -> Res.drawable.musclemain
        "duo_muckle" -> Res.drawable.duo_muckle
        else -> null
    }

private fun parseRecommendationArticleHtml(rawHtml: String): List<ArticleBlock> {
    val normalized = rawHtml
        .replace("\r\n", "\n")
        // Legacy typos: <\a>, <\h2> → </a>, </h2>
        .replace(Regex("""<\\(/?[a-zA-Z0-9]+)>"""), "</$1>")
        .replace(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("""<\s*br\s*>""", RegexOption.IGNORE_CASE), "\n")
    val blocks = mutableListOf<ArticleBlock>()
    var index = 0
    while (index < normalized.length) {
        val nextTag = normalized.indexOf('<', index)
        if (nextTag == -1) {
            appendPlainParagraph(normalized.substring(index), blocks)
            break
        }
        if (nextTag > index) {
            appendPlainParagraph(normalized.substring(index, nextTag), blocks)
        }
        val tagEnd = normalized.indexOf('>', nextTag)
        if (tagEnd == -1) break
        val tagContent = normalized.substring(nextTag + 1, tagEnd).trim()
        val closing = tagContent.startsWith("/")
        val tagName = tagContent
            .removePrefix("/")
            .substringBefore(' ')
            .substringBefore('\t')
            .lowercase()
        if (tagName == "img") {
            parseImageBlock(tagContent)?.let { blocks += it }
            index = tagEnd + 1
            continue
        }
        if (closing) {
            index = tagEnd + 1
            continue
        }
        val endTag = "</$tagName>"
        val endIndex = findMatchingClose(normalized, tagEnd + 1, tagName)
        val inner = if (endIndex == -1) {
            ""
        } else {
            normalized.substring(tagEnd + 1, endIndex)
        }
        when (tagName) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> {
                blocks += ArticleBlock.Heading(
                    level = tagName.last().digitToInt(),
                    spans = parseInlineSpans(inner),
                )
            }
            "p" -> {
                blocks += parseInnerContentBlocks(inner)
            }
            "li" -> {
                blocks += ArticleBlock.BulletItem(parseInlineSpans(inner))
            }
            "div" -> {
                val centered = tagContent.contains("text-align: center", ignoreCase = true)
                blocks += parseInnerContentBlocks(inner, centered = centered)
            }
            // Recurse into list body so nested <li> become BulletItems (do not skip).
            "ul", "ol" -> {
                blocks += parseRecommendationArticleHtml(inner)
            }
            else -> appendPlainParagraph(inner, blocks)
        }
        index = if (endIndex == -1) tagEnd + 1 else endIndex + endTag.length
    }
    return blocks.filterNot { block ->
        block is ArticleBlock.Paragraph &&
            block.spans.singleOrNull()?.text?.isBlank() != false &&
            block.spans.size <= 1 &&
            block.spans.firstOrNull()?.text.isNullOrBlank()
    }
}

private fun parseInnerContentBlocks(
    inner: String,
    centered: Boolean = false,
): List<ArticleBlock> {
    val blocks = mutableListOf<ArticleBlock>()
    var index = 0
    while (index < inner.length) {
        val imgStart = inner.indexOf("<img", index, ignoreCase = true)
        if (imgStart == -1) {
            val spans = parseInlineSpans(inner.substring(index))
            if (spans.isNotEmpty()) {
                blocks += ArticleBlock.Paragraph(spans, centered = centered)
            }
            break
        }
        if (imgStart > index) {
            val spans = parseInlineSpans(inner.substring(index, imgStart))
            if (spans.isNotEmpty()) {
                blocks += ArticleBlock.Paragraph(spans, centered = centered)
            }
        }
        val tagEnd = inner.indexOf('>', imgStart)
        if (tagEnd == -1) break
        val tagContent = inner.substring(imgStart + 1, tagEnd).trim()
        parseImageBlock(tagContent)?.let { blocks += it }
        index = tagEnd + 1
    }
    return blocks
}

private fun parseImageBlock(tagContent: String): ArticleBlock.ImageBlock? {
    val src = extractAttribute(tagContent, "src")?.let(::normalizeImageAsset)
    return if (src.isNullOrBlank()) null else ArticleBlock.ImageBlock(src)
}

private fun normalizeImageAsset(value: String): String =
    unwrapAttr(value).trim('\'', '"')

private fun appendPlainParagraph(text: String, blocks: MutableList<ArticleBlock>) {
    val cleaned = decodeHtmlEntities(text).trim()
    if (cleaned.isNotEmpty()) {
        blocks += ArticleBlock.Paragraph(listOf(InlineSpan(cleaned)))
    }
}

private fun findMatchingClose(source: String, startIndex: Int, tagName: String): Int {
    val openPattern = Regex("""<${Regex.escape(tagName)}\b""", RegexOption.IGNORE_CASE)
    val closeTag = "</$tagName>"
    var depth = 1
    var searchFrom = startIndex
    while (depth > 0) {
        val nextOpen = openPattern.find(source, searchFrom)?.range?.first ?: -1
        val nextClose = source.indexOf(closeTag, searchFrom, ignoreCase = true)
        if (nextClose == -1) return -1
        if (nextOpen != -1 && nextOpen < nextClose) {
            depth++
            searchFrom = nextOpen + 1
        } else {
            depth--
            if (depth == 0) return nextClose
            searchFrom = nextClose + closeTag.length
        }
    }
    return -1
}

private fun parseInlineSpans(html: String): List<InlineSpan> {
    val spans = mutableListOf<InlineSpan>()
    parseInlineRecursive(html, spans, bold = false, italic = false, link = null)
    return spans.filter { it.text.isNotEmpty() }
}

private fun parseInlineRecursive(
    html: String,
    out: MutableList<InlineSpan>,
    bold: Boolean,
    italic: Boolean,
    link: String?,
) {
    var index = 0
    while (index < html.length) {
        val nextTag = html.indexOf('<', index)
        if (nextTag == -1) {
            appendSpan(out, decodeHtmlEntities(html.substring(index)), bold, italic, link)
            break
        }
        if (nextTag > index) {
            appendSpan(out, decodeHtmlEntities(html.substring(index, nextTag)), bold, italic, link)
        }
        val tagEnd = html.indexOf('>', nextTag)
        if (tagEnd == -1) break
        val tagContent = html.substring(nextTag + 1, tagEnd).trim()
        val closing = tagContent.startsWith("/")
        val tagName = tagContent.removePrefix("/").substringBefore(' ').lowercase()
        if (closing) {
            index = tagEnd + 1
            continue
        }
        if (tagName == "img") {
            index = tagEnd + 1
            continue
        }
        val endTag = "</$tagName>"
        val endIndex = html.indexOf(endTag, tagEnd + 1, ignoreCase = true)
        val inner = if (endIndex == -1) "" else html.substring(tagEnd + 1, endIndex)
        when (tagName) {
            "b", "strong" -> parseInlineRecursive(inner, out, bold = true, italic = italic, link = link)
            "i", "em" -> parseInlineRecursive(inner, out, bold = bold, italic = true, link = link)
            "u" -> parseInlineRecursive(inner, out, bold = bold, italic = italic, link = link)
            "a" -> {
                val href = extractAttribute(tagContent, "href")
                    ?.let(::unwrapAttr)
                    ?.let(::normalizeHref)
                parseInlineRecursive(inner, out, bold = bold, italic = true, link = href ?: link)
            }
            else -> parseInlineRecursive(inner, out, bold = bold, italic = italic, link = link)
        }
        index = if (endIndex == -1) tagEnd + 1 else endIndex + endTag.length
    }
}

private fun appendSpan(
    out: MutableList<InlineSpan>,
    text: String,
    bold: Boolean,
    italic: Boolean,
    link: String?,
) {
    val normalized = text.replace('\n', ' ').replace(Regex("\\s+"), " ")
    if (normalized.isBlank()) return
    out += InlineSpan(normalized, bold = bold, italic = italic, link = link)
}

private fun extractAttribute(tagContent: String, name: String): String? {
    val pattern = Regex("""$name\s*=\s*('([^']*)'|"([^"]*)")""", RegexOption.IGNORE_CASE)
    val match = pattern.find(tagContent) ?: return null
    return match.groupValues.getOrNull(2)?.ifEmpty { null }
        ?: match.groupValues.getOrNull(3)
}

private fun unwrapAttr(value: String): String =
    value.trim().trim('\'', '"')

/** Legacy HTML uses href="'https://...'" — strip leftover quotes / spaces. */
private fun normalizeHref(value: String): String? {
    val cleaned = value.trim().trim('\'', '"', ' ', '\n', '\t')
    if (cleaned.isEmpty()) return null
    return cleaned
}

private fun decodeHtmlEntities(text: String): String =
    text
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("\\'", "'")
        .replace("\\\"", "\"")
