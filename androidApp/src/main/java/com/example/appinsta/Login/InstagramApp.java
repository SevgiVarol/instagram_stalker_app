package com.example.appinsta.Login;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class InstagramApp {

    private InstagramSession mSession;
    private InstagramDialog mDialog;
    private InstagramDialog.OAuthDialogListener mListener;
    private ProgressDialog mProgress;
    private HashMap<String, String> userInfo = new HashMap<String, String>();
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private Context mCtx;

    private String mClientId;
    private String mClientSecret;

    public static int WHAT_FINALIZE = 0;
    public static int WHAT_ERROR = 1;
    private static int WHAT_FETCH_INFO = 2;

    /**
     * Callback url, as set in 'Manage OAuth Costumers' page
     * (https://developer.github.com/)
     */

    public static String mCallbackUrl = "";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";

    public InstagramApp(Context context, String clientId, String clientSecret,
                        String callbackUrl, InstagramDialog.OAuthDialogListener listener) {

        mClientId = clientId;
        mClientSecret = clientSecret;
        mCtx = context;
        mListener = listener;
        mSession = new InstagramSession(context);
        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl
                + "&grant_type=authorization_code";

        mAuthUrl = AUTH_URL
                + "?client_id="
                + clientId
                + "&redirect_uri="
                + mCallbackUrl
                + "&response_type=code&display=touch&scope=basic";

        mDialog = new InstagramDialog(context, mAuthUrl, mListener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
        mDialog.show();
    }

    public void reConnect() {
        mDialog.show();
    }

    public void setListener(InstagramDialog.OAuthDialogListener listener) {
        mListener = listener;
    }

}