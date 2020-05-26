package hu.bme.aut.android.tourguide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)

        val url = arguments!!.getString("url")

        webView = view.findViewById(R.id.web_view)
        webView.loadUrl(url)

        //val webSettings = webView.settings
        //webSettings.javaScriptEnabled = true

        webView.webViewClient = WebViewClient()

        return view
    }
}
