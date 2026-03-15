package com.example.banners

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.TextInput
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.api.SettingsAPI

class BannersSettings(private val settings: SettingsAPI) : SettingsPage() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setActionBarTitle("Banners")

        val ctx = requireContext()
        val layout = linearLayout

        val enableSwitch = Switch(ctx).apply {
            text = "Enable Custom Banner"
            isChecked = settings.getBool("enabled", true)
            setOnCheckedChangeListener { _, checked ->
                settings.setBool("enabled", checked)
            }
        }
        layout.addView(enableSwitch)

        val urlInput = TextInput(ctx).apply { hint = "Banner URL (.gif / .png / .jpg)" }
        val urlEditText = EditText(ctx).apply {
            setText(settings.getString("bannerUrl", ""))
            hint = "https://i.imgur.com/example.gif"
        }
        urlInput.addView(urlEditText)
        layout.addView(urlInput)

        val saveBtn = com.aliucord.views.Button(ctx).apply {
            text = "Save"
            setOnClickListener {
                val url = urlEditText.text.toString().trim()
                settings.setString("bannerUrl", url)
                com.aliucord.utils.RxUtils.showToast(ctx, "Saved! Restart Discord to apply.")
            }
        }
        layout.addView(saveBtn)

        val hint = TextView(ctx).apply {
            text = "Only you will see this banner (client-side).\n" +
                   "Use a direct image link ending in .gif, .png, or .jpg.\n" +
                   "Imgur, GitHub raw, and Catbox links all work."
            setPadding(0, 12.dp, 0, 0)
        }
        layout.addView(hint)
    }
}
