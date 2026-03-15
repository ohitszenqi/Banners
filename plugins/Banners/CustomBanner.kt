package com.example.custombanner

import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.ReflectUtils
import com.discord.stores.StoreStream
import com.discord.models.user.User
import com.discord.widgets.user.profile.UserProfileHeaderView
import com.discord.databinding.WidgetUserSheetBinding

@AliucordPlugin
class CustomBanner : Plugin() {

    // Default banner URL - users can change this in plugin settings
    private val DEFAULT_BANNER = "https://i.imgur.com/your_banner_here.gif"

    override fun start(context: Context) {
        val bannerUrl = settings.getString("bannerUrl", DEFAULT_BANNER)
        val enabled = settings.getBool("enabled", true)

        if (!enabled || bannerUrl.isNullOrBlank()) return

        val meId = try {
            StoreStream.getUsers().me?.id
        } catch (e: Exception) {
            logger.error("Failed to get current user ID", e)
            return
        }

        // Hook into UserProfileHeaderView to inject our banner
        patcher.patch(
            UserProfileHeaderView::class.java.getDeclaredMethod("updateUserBanner", User::class.java),
            Hook { param ->
                val user = param.args[0] as? User ?: return@Hook
                if (user.id != meId) return@Hook

                try {
                    val view = param.thisObject as UserProfileHeaderView
                    // Find the banner ImageView via binding or tag
                    val binding = ReflectUtils.getField(view, "binding") as? WidgetUserSheetBinding
                    val bannerView = binding?.let {
                        ReflectUtils.getField(it, "userBanner")
                    } as? com.facebook.drawee.view.SimpleDraweeView

                    bannerView?.let {
                        it.visibility = View.VISIBLE
                        com.aliucord.utils.GifUtils.loadGif(it, bannerUrl)
                    }
                } catch (e: Exception) {
                    logger.error("Failed to inject custom banner", e)
                }
            }
        )

        logger.info("CustomBanner started - using: $bannerUrl")
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

    // Register settings so users can change the URL in-app
    override fun getSettingsPage(): PluginSettings {
        return CustomBannerSettings(settings)
    }
}
