@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package com.wrapper.composechat.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.StringResource

private object CommonMainString0 {
  public val app_name: StringResource by 
      lazy { init_app_name() }

  public val auth_age_error: StringResource by 
      lazy { init_auth_age_error() }

  public val auth_age_label: StringResource by 
      lazy { init_auth_age_label() }

  public val auth_continue: StringResource by 
      lazy { init_auth_continue() }

  public val auth_sex_error: StringResource by 
      lazy { init_auth_sex_error() }

  public val auth_sex_female: StringResource by 
      lazy { init_auth_sex_female() }

  public val auth_sex_label: StringResource by 
      lazy { init_auth_sex_label() }

  public val auth_sex_male: StringResource by 
      lazy { init_auth_sex_male() }

  public val auth_swipe_to_continue: StringResource by 
      lazy { init_auth_swipe_to_continue() }

  public val auth_title: StringResource by 
      lazy { init_auth_title() }

  public val main_dashboard_chats: StringResource by 
      lazy { init_main_dashboard_chats() }

  public val main_dashboard_in_progress: StringResource by 
      lazy { init_main_dashboard_in_progress() }

  public val main_dashboard_recommendations_subtitle: StringResource by 
      lazy { init_main_dashboard_recommendations_subtitle() }

  public val main_dashboard_recommendations_title: StringResource by 
      lazy { init_main_dashboard_recommendations_title() }

  public val main_dashboard_requirements_subtitle: StringResource by 
      lazy { init_main_dashboard_requirements_subtitle() }

  public val main_dashboard_requirements_title: StringResource by 
      lazy { init_main_dashboard_requirements_title() }

  public val main_dashboard_settings: StringResource by 
      lazy { init_main_dashboard_settings() }

  public val main_dashboard_vfv_done: StringResource by 
      lazy { init_main_dashboard_vfv_done() }

  public val nav_back: StringResource by 
      lazy { init_nav_back() }
}

@InternalResourceApi
internal fun _collectCommonMainString0Resources(map: MutableMap<String, StringResource>) {
  map.put("app_name", CommonMainString0.app_name)
  map.put("auth_age_error", CommonMainString0.auth_age_error)
  map.put("auth_age_label", CommonMainString0.auth_age_label)
  map.put("auth_continue", CommonMainString0.auth_continue)
  map.put("auth_sex_error", CommonMainString0.auth_sex_error)
  map.put("auth_sex_female", CommonMainString0.auth_sex_female)
  map.put("auth_sex_label", CommonMainString0.auth_sex_label)
  map.put("auth_sex_male", CommonMainString0.auth_sex_male)
  map.put("auth_swipe_to_continue", CommonMainString0.auth_swipe_to_continue)
  map.put("auth_title", CommonMainString0.auth_title)
  map.put("main_dashboard_chats", CommonMainString0.main_dashboard_chats)
  map.put("main_dashboard_in_progress", CommonMainString0.main_dashboard_in_progress)
  map.put("main_dashboard_recommendations_subtitle",
      CommonMainString0.main_dashboard_recommendations_subtitle)
  map.put("main_dashboard_recommendations_title",
      CommonMainString0.main_dashboard_recommendations_title)
  map.put("main_dashboard_requirements_subtitle",
      CommonMainString0.main_dashboard_requirements_subtitle)
  map.put("main_dashboard_requirements_title", CommonMainString0.main_dashboard_requirements_title)
  map.put("main_dashboard_settings", CommonMainString0.main_dashboard_settings)
  map.put("main_dashboard_vfv_done", CommonMainString0.main_dashboard_vfv_done)
  map.put("nav_back", CommonMainString0.nav_back)
}

public val Res.string.app_name: StringResource
  get() = CommonMainString0.app_name

private fun init_app_name(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:app_name", "app_name",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 10, 40),
    )
)

public val Res.string.auth_age_error: StringResource
  get() = CommonMainString0.auth_age_error

private fun init_auth_age_error(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_age_error", "auth_age_error",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 51, 82),
    )
)

public val Res.string.auth_age_label: StringResource
  get() = CommonMainString0.auth_age_label

private fun init_auth_age_label(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_age_label", "auth_age_label",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 134, 26),
    )
)

public val Res.string.auth_continue: StringResource
  get() = CommonMainString0.auth_continue

private fun init_auth_continue(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_continue", "auth_continue",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 161, 33),
    )
)

public val Res.string.auth_sex_error: StringResource
  get() = CommonMainString0.auth_sex_error

private fun init_auth_sex_error(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_sex_error", "auth_sex_error",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 195, 58),
    )
)

public val Res.string.auth_sex_female: StringResource
  get() = CommonMainString0.auth_sex_female

private fun init_auth_sex_female(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_sex_female", "auth_sex_female",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 254, 31),
    )
)

public val Res.string.auth_sex_label: StringResource
  get() = CommonMainString0.auth_sex_label

private fun init_auth_sex_label(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_sex_label", "auth_sex_label",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 286, 30),
    )
)

public val Res.string.auth_sex_male: StringResource
  get() = CommonMainString0.auth_sex_male

private fun init_auth_sex_male(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_sex_male", "auth_sex_male",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 317, 29),
    )
)

public val Res.string.auth_swipe_to_continue: StringResource
  get() = CommonMainString0.auth_swipe_to_continue

private fun init_auth_swipe_to_continue(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:auth_swipe_to_continue", "auth_swipe_to_continue",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 347, 62),
    )
)

public val Res.string.auth_title: StringResource
  get() = CommonMainString0.auth_title

private fun init_auth_title(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:auth_title", "auth_title",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 410, 30),
    )
)

public val Res.string.main_dashboard_chats: StringResource
  get() = CommonMainString0.main_dashboard_chats

private fun init_main_dashboard_chats(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_chats", "main_dashboard_chats",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 441, 36),
    )
)

public val Res.string.main_dashboard_in_progress: StringResource
  get() = CommonMainString0.main_dashboard_in_progress

private fun init_main_dashboard_in_progress(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_in_progress", "main_dashboard_in_progress",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 478, 66),
    )
)

public val Res.string.main_dashboard_recommendations_subtitle: StringResource
  get() = CommonMainString0.main_dashboard_recommendations_subtitle

private fun init_main_dashboard_recommendations_subtitle(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_recommendations_subtitle", "main_dashboard_recommendations_subtitle",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 545, 91),
    )
)

public val Res.string.main_dashboard_recommendations_title: StringResource
  get() = CommonMainString0.main_dashboard_recommendations_title

private fun init_main_dashboard_recommendations_title(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_recommendations_title", "main_dashboard_recommendations_title",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 637, 76),
    )
)

public val Res.string.main_dashboard_requirements_subtitle: StringResource
  get() = CommonMainString0.main_dashboard_requirements_subtitle

private fun init_main_dashboard_requirements_subtitle(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_requirements_subtitle", "main_dashboard_requirements_subtitle",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 714, 76),
    )
)

public val Res.string.main_dashboard_requirements_title: StringResource
  get() = CommonMainString0.main_dashboard_requirements_title

private fun init_main_dashboard_requirements_title(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_requirements_title", "main_dashboard_requirements_title",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 791, 57),
    )
)

public val Res.string.main_dashboard_settings: StringResource
  get() = CommonMainString0.main_dashboard_settings

private fun init_main_dashboard_settings(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_settings", "main_dashboard_settings",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 849, 43),
    )
)

public val Res.string.main_dashboard_vfv_done: StringResource
  get() = CommonMainString0.main_dashboard_vfv_done

private fun init_main_dashboard_vfv_done(): StringResource =
    org.jetbrains.compose.resources.StringResource(
  "string:main_dashboard_vfv_done", "main_dashboard_vfv_done",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 893, 47),
    )
)

public val Res.string.nav_back: StringResource
  get() = CommonMainString0.nav_back

private fun init_nav_back(): StringResource = org.jetbrains.compose.resources.StringResource(
  "string:nav_back", "nav_back",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.wrapper.composechat.resources/values/strings.commonMain.cvr", 941, 24),
    )
)
