package com.example.appinsta.Login;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class InstagramDialog extends Dialog {

    static final float[] DIMENSIONS_LANDSCAPE = {500, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 1;


    private String mUrl;
    private OAuthDialogListener mListener;

    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;
    public String cookies = null;


    private static final String TAG = "Instagram-WebView";

    public InstagramDialog(Context context, String url, OAuthDialogListener listener) {
        super(context);

        mUrl = url;
        mListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpTitle();
        setUpWebView();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;

        addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1] * scale + 0.5f)));
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private void setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTitle = new TextView(getContext());
        mTitle.setText("Instagram");
        mTitle.setTextColor(Color.WHITE);
        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mTitle.setBackgroundColor(Color.BLACK);
        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        mContent.addView(mTitle);
    }

    private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new OAuthWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirecting URL " + url);

            if (url.startsWith(InstagramApp.mCallbackUrl)) {
                cookies = CookieManager.getInstance().getCookie("https://api.instagram.com");


                mListener.onComplete(cookies);
                InstagramDialog.this.dismiss();




//                    InstagramDialog.this.dismiss();
//                Pattern userIdPattern = Pattern.compile("ds_user_id=(.*?);");
//                Pattern sessionIdPattern = Pattern.compile("sessionid=(.*?);");
//                Pattern csrfTokenPattern = Pattern.compile("csrftoken=(.*?);");
//
//                Matcher userIdMatcher = userIdPattern.matcher(mCookies);
//                Matcher sessionIdMatcher = sessionIdPattern.matcher(mCookies);
//                Matcher csrfTokenMatcher = csrfTokenPattern.matcher(mCookies);
//                if (userIdMatcher.find() && sessionIdMatcher.find() && csrfTokenMatcher.find()) {
//                    Long userId = Long.parseLong(userIdMatcher.group(1));
//                    String sessionId = sessionIdMatcher.group(1);
//                    String csrfToken = csrfTokenMatcher.group(1);
//                    Log.d(TAG, "All the mCookies in a string:" + mCookies);
//                    mListener.onComplete(userId, sessionId, csrfToken, mWebView.coo);
//                    InstagramDialog.this.dismiss();
//                } else {
//                    Toast.makeText(getContext(), "Oturum başlatılamadı.", Toast.LENGTH_LONG).show();
//                    dismiss();
//                }

                return true;
            } return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "Page error: " + description);

            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(description);
            InstagramDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Loading URL: " + url);

            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }



        }

    }

    public interface OAuthDialogListener {
        public abstract void onComplete(String cookies);
        //public abstract void onComplete(Long userId, String sessionId, String csrfToken);

        public abstract void onError(String error);
    }

}