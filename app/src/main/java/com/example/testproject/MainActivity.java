package com.example.testproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    Button bt_press;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_press = (Button) findViewById(R.id.bt_press);
        bt_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
                    @Override
                    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                        showAlert("onCustomTabsServiceConnected");
                        mClient = client;
                        CustomTabsSession session = getSession();
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
                        builder.setToolbarColor(Color.RED);
                        CustomTabsIntent customTabsIntent = builder.build();
                        CustomTabsHelper.addKeepAliveExtra(MainActivity.this, customTabsIntent.intent);
                        String url = "https://nexxeqc.gep.com/";
                        customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        showAlert("onServiceDisconnected");
                    }
                };


                String packagename = "com.android.chrome";

                boolean ok = CustomTabsClient.bindCustomTabsService(getApplicationContext(), packagename, connection);
                showAlert("Ok" + ok);
            }
        });
    }
    private static class NavigationCallback extends CustomTabsCallback {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            Log.e("Tag", "onNavigationEvent: Code = " + navigationEvent);
        }
    }
    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new NavigationCallback() {
                @Override
                public void onNavigationEvent(int navigationEvent, Bundle extras) {
                    super.onNavigationEvent(navigationEvent, extras);
                    String msg=extras.getString("Result");
                    Log.e("Event", ""+new Gson().toJson(extras));
                }
            });
            SessionHelper.setCurrentSession(mCustomTabsSession);
        }
        return mCustomTabsSession;
    }

    public void showAlert(String msg) {
        Log.e("Alert", "HIi " + msg);
//        new AlertDialog.Builder(MainActivity.this).setMessage(msg).show();

    }

    public static class SessionHelper {
        private static WeakReference<CustomTabsSession> sCurrentSession;

        /**
         * @return The current {@link CustomTabsSession} object.
         */
        public static @Nullable
        CustomTabsSession getCurrentSession() {
            return sCurrentSession == null ? null : sCurrentSession.get();
        }

        /**
         * Sets the current session to the given one.
         *
         * @param session The current session.
         */
        public static void setCurrentSession(CustomTabsSession session) {
            sCurrentSession = new WeakReference<CustomTabsSession>(session);
            Log.e("Alert", "setCurrentSession");
        }
    }


}
