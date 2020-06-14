package com.example.testproject


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.testproject.databinding.ActivityLauncherBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


const val URL_INTENT_KEY = "URL"

private const val DELAY = 5000L

private const val ANIMATION_DURATION = 500L

private const val PREFERENCES = "URL_STORAGE"

private const val PREFERENCES_URL_KEY = "URL_KEY"

class LauncherActivity : AppCompatActivity() {

    val binding: ActivityLauncherBinding by lazy { DataBindingUtil.setContentView<ActivityLauncherBinding>(this, R.layout.activity_launcher) }

    val preferences by lazy {  getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)}

    var loadUrl = String()

    override fun onCreate(savedInstanceState: Bundle?) {

        Glide.with(this).load(APPLICATION_ICON_URL).into(binding.imageView)

        GlobalScope.launch(Dispatchers.Main) {
            delay(DELAY)
            with(binding){
                buttons.animate().alpha(1.0f).setDuration(ANIMATION_DURATION).start()
                startButton.isClickable = true
                privacyTextButton.isClickable = true
            }
        }

        loadUrl = preferences.getString(PREFERENCES_URL_KEY, String())!!

        binding.editText.text = loadUrl

        binding.editText.setOnClickListener{
            preferences.edit().clear().apply()
        }

        super.onCreate(savedInstanceState)
    }

    fun privacyPolicy(view: View){
        openUrlInWebViewActivity(this, PRIVACY_POLICY_URL)
    }

    fun throwDialog(){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.noInternetConnection))
            .setMessage(getString(R.string.applicationWillBeTerminated))
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show().setOnCancelListener {
             //   finish()
           //     exitProcess(1)
            }
    }

    fun start(view: View){
        openNative(this)
        if(loadUrl.isEmpty()) {
            val data = getUrlRawData(EXAMPLE_URL)
            if (data == null) {
                throwDialog()
            }
            else if (data.isNotEmpty()){
                preferences.edit().putString(PREFERENCES_URL_KEY, data).apply()
                openUrlInWebViewActivity(this, data)
            }
        }
        else{
            openUrlInWebViewActivity(this, binding.editText.text.toString())
        }
        finish()
    }
}
