package com.example.custombanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Switch
import androidx.core.content.ContextCompat
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.TextInput
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.api.SettingsAPI

class CustomBannerSettings(private val settings: SettingsAPI) : SettingsPage() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setActionBarTitle("Custom Banner")

        val ctx = requireContext()
        val layout = linearLayout // from SettingsPage base

        // --- Enable/Disable Toggle ---
        val enableSwitch = Switch(ctx).apply {
            text = "Enable Custom Banner"
            isChecked = settings.getBool("enabled", true)
            setOnCheckedChangeListener { _, checked ->
                settings.setBool("enabled", checked)
            }
        }
        layout.addView(enableSwitch)

        // --- Spacer ---
        layout.addView(View(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 12.dp
            )
        })

        // --- Banner URL Input ---
        val urlLabel = TextView(ctx).apply {
            text = "Banner URL (image or GIF)"
            setTextColor(ContextCompat.getColor(ctx, com.lytefast.flexinput.R.c.primary_light))
        }
        layout.addView(urlLabel)

        val urlInput = TextInput(ctx).apply {
            hint = "https://i.imgur.com/example.gif"
        }
        val urlEditText = EditText(ctx).apply {
            setText(settings.getString("bannerUrl", ""))
            hint = "Paste your image/GIF URL here"
        }
        urlInput.addView(urlEditText)
        layout.addView(urlInput)

        // --- Spacer ---
        layout.addView(View(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 12.dp
            )
        })

        // --- Save Button ---
        val saveBtn = com.aliucord.views.Button(ctx).apply {
            text = "Save"
            setOnClickListener {
                val url = urlEditText.text.toString().trim()
                if (url.isNotEmpty()) {
                    settings.setString("bannerUrl", url)
                    com.aliucord.utils.RxUtils.showToast(ctx, "Banner URL saved! Restart Discord to apply.")
                } else {
                    com.aliucord.utils.RxUtils.showToast(ctx, "Please enter a valid URL.")
                }
            }
        }
        layout.addView(saveBtn)

        // --- Help Text ---
        val helpText = TextView(ctx).apply {
            text = "\nTips:\n" +
                   "• Use direct image links (ending in .gif, .png, .jpg)\n" +
                   "• Imgur: upload → right-click image → Copy image address\n" +
                   "• GitHub raw links work great too\n" +
                   "• Only YOU will see this banner (client-side only)"
            setTextColor(ContextCompat.getColor(ctx, com.lytefast.flexinput.R.c.primary_dark))
            setPadding(0, 16.dp, 0, 0)
        }
        layout.addView(helpText)
    }
}
