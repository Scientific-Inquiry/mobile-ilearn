package teamfive.milearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity
{
    static String user;
    WebView browser;
    public static void setUser(String username)    {
        user = username;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);

        final SwipeRefreshLayout swipeView =(SwipeRefreshLayout)findViewById(R.id.swipe);
        browser = (WebView)findViewById(R.id.webView1);
        browser.setWebViewClient(new Browser());
        browser.getSettings().setJavaScriptEnabled(true);
        browser.loadUrl("http://milearn.s3-website-us-west-2.amazonaws.com/");

        browser.addJavascriptInterface(new LogoutInterface(this), "Android");
        //Toast.makeText(MainActivity.this, user, Toast.LENGTH_LONG).show();
        S3Manager s3 = new S3Manager();
        DBManager db = new DBManager();
        Login.set_uid();
        System.out.println(s3.bucketName);
        db.init_Grades(s3.connection, db.find_aid(s3.connection, "Investigation Article 3", "CRWT177"), Login.login);
        db.init_Assignments(s3.connection,db.find_aid(s3.connection, "Investigation Article 3", "CRWT177"), Login.login);
        //DBManager.init_Assignments(s3.connection, Login.login);


        //swipeView.setColorScheme(android.R.color.holo_blue_dark,android.R.color.holo_blue_light, android.R.color.holo_green_light,android.R.color.holo_green_dark);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        browser.loadUrl("http://milearn.s3-website-us-west-2.amazonaws.com/");
                    }
                }, 4000);
            }
        });
    }


    private class Browser extends WebViewClient  //If you click on any link inside the webpage of the WebView , that page will not be loaded inside your WebView. In order to do that you need to extend your class from WebViewClient by using the below function.
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
    }

    public static String getUser() {
        return user;
    }
}