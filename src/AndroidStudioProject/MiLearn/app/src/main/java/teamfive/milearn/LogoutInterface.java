package teamfive.milearn;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Vincent on 5/12/2016.
 */
public class LogoutInterface    {
    Context mContext;
    LogoutInterface(Context c) {
        mContext = c;
    }

//Binds logout button to javascript button
    @JavascriptInterface
    public void logout() {
           Login.logout();
           Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
    }
    @JavascriptInterface
    public void openWebsite() {
        Intent intent = new Intent(mContext, WebsiteActivity.class);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public String getUser() {
        return MainActivity.getUser();
    }

}
