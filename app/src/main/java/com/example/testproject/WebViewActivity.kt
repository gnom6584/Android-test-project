package com.example.testproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.testproject.databinding.ActivityMainBinding
import com.example.testproject.databinding.ActivityWebViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun openUrlInWebViewActivity(currentActivity: Activity, url : String){
    val intent = Intent(currentActivity, WebViewActivity::class.java)
    intent.putExtra(URL_INTENT_KEY, url)
    currentActivity.startActivity(intent)
}

class WebViewActivity : AppCompatActivity() {

    val binding: ActivityWebViewBinding by lazy { DataBindingUtil.setContentView<ActivityWebViewBinding>(this, R.layout.activity_web_view)}

    val webView by lazy { binding.root as WebView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        val url = intent.getStringExtra(URL_INTENT_KEY)
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
        }
        super.onBackPressed()
    }
}
