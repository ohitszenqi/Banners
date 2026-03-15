package com.example.banners

import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.ReflectUtils
import com.discord.stores.StoreStream
import com.discord.models.user.User
import com.discord.widgets.user.profile.UserProfileHeaderView

@AliucordPlugin
class Banners : Plugin() {

    override fun start(context: Context) {
        val bannerUrl = settings.getString("bannerUrl", "")
        val enabled = settings.getBool("enabled", true)

        if (!enabled || bannerUrl.isNullOrBlank()) return

        val meId = try {
            StoreStream.getUsers().me?.id
        } catch (e: Exception) {
            logger.error("Failed to get current user ID", e)
            return
        }

        patcher.patch(
            UserProfileHeaderView::class.java.getDeclaredMethod(
                "updateUserBanner",
                User::class.java
            ),
            Hook { param ->
                val user = param.args[0] as? User ?: return@Hook
                if (user.id != meId) return@Hook

                try {
                    val view = param.thisObject as UserProfileHeaderView
                    val bannerView = ReflectUtils.getField(view, "bannerImage")
                            as? com.facebook.drawee.view.SimpleDraweeView

                    bannerView?.let {
                        it.visibility = View.VISIBLE
                        com.aliucord.utils.GifUtils.loadGif(it, bannerUrl)
                    }
                } catch (e: Exception) {
                    logger.error("Failed to inject banner", e)
                }
            }
        )
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

    override fun getSettingsPage() = BannersSettings(settings)
}
