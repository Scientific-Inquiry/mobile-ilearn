package teamfive.milearn;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class WebsiteActivity extends Activity {
    WebView browser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        browser = (WebView) findViewById(R.id.webView1);
        browser.setWebViewClient(new Browser());
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.loadUrl("http://crumblords.s3-website-us-west-2.amazonaws.com/");


    }
    private class Browser extends WebViewClient  //If you click on any link inside the webpage of the WebView , that page will not be loaded inside your WebView. In order to do that you need to extend your class from WebViewClient by using the below function.
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
