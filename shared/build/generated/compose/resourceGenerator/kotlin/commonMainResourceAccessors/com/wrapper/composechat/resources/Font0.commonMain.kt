@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package com.wrapper.composechat.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainFont0 {
  public val sfui_display_bold: FontResource by 
      lazy { init_sfui_display_bold() }

  public val sfui_display_light: FontResource by 
      lazy { init_sfui_display_light() }

  public val sfui_display_regular: FontResource by 
      lazy { init_sfui_display_regular() }
}

@InternalResourceApi
internal fun _collectCommonMainFont0Resources(map: MutableMap<String, FontResource>) {
  map.put("sfui_display_bold", CommonMainFont0.sfui_display_bold)
  map.put("sfui_display_light", CommonMainFont0.sfui_display_light)
  map.put("sfui_display_regular", CommonMainFont0.sfui_display_regular)
}

public val Res.font.sfui_display_bold: FontResource
  get() = CommonMainFont0.sfui_display_bold

private fun init_sfui_display_bold(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:sfui_display_bold",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/font/sfui_display_bold.ttf", -1, -1),
    )
)

public val Res.font.sfui_display_light: FontResource
  get() = CommonMainFont0.sfui_display_light

private fun init_sfui_display_light(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:sfui_display_light",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/font/sfui_display_light.ttf", -1, -1),
    )
)

public val Res.font.sfui_display_regular: FontResource
  get() = CommonMainFont0.sfui_display_regular

private fun init_sfui_display_regular(): FontResource =
    org.jetbrains.compose.resources.FontResource(
  "font:sfui_display_regular",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/font/sfui_display_regular.ttf", -1, -1),
    )
)
