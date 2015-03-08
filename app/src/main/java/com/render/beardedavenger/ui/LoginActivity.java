package com.render.beardedavenger.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.render.beardedavenger.R;
import com.render.beardedavenger.io.ApiClient;
import com.render.beardedavenger.util.Constants;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends ActionBarActivity implements LoginButton.OnErrorListener, Session.StatusCallback, View.OnClickListener {

    private Future futureFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        LoginButton loginButton = (LoginButton) findViewById(R.id.buttonLoginFacebook);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        loginButton.setOnErrorListener(this);
        loginButton.setSessionStatusCallback(this);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
    }


    @Override
    public void call(Session session, SessionState sessionState, Exception e) {

        if (sessionState.isOpened()) {

            Request.newMeRequest(session,
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user,
                                                Response response) {
                            if (user != null) {

                                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE).edit();
                                editor.putBoolean(Constants.IS_USER_LOGIN, true);
                                editor.putBoolean(Constants.IS_LOGIN_FACEBOOK, true);
                                editor.putString(Constants.USER_ID, user.getId());
                                editor.putString(Constants.USER_NAME, user.getName());
                                editor.putString(Constants.USER_EMAIL, user.getProperty("email").toString());
                                editor.apply();
                                obtainInfoUser();
                            } else {
                                Log.d("Error", "1");
                                Toast.makeText(LoginActivity.this, R.string.message_error, Toast.LENGTH_SHORT).show();
                                callFacebookLogout();
                            }
                        }
                    }).executeAsync();
        }


    }


    private void obtainInfoUser()
    {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage(getString(R.string.text_loading_user));
        progressDialog.show();

        futureFriends = ApiClient.requestWebGetObjetJson(LoginActivity.this, Constants.URL_LONGIN, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (result != null) {


                    JsonObject jsonObject = result.get("data").getAsJsonObject();

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE).edit();
                    editor.putString(Constants.USER_NAME, jsonObject.get("username").getAsString());
                    editor.putInt(Constants.USER_LEVEL, jsonObject.get("level").getAsInt());
                    editor.putInt(Constants.USER_EXPERENCE, jsonObject.get("stats").getAsJsonObject().get("experience").getAsInt());
                    editor.putInt(Constants.USER_MAX_EXPERENCE, jsonObject.get("nextLevel").getAsInt());
                    editor.apply();


                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    if (!futureFriends.isCancelled()) {
                        Toast.makeText(LoginActivity.this, R.string.message_error, Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.dismiss();

            }
        });
    }

    @Override
    public void onError(FacebookException e) {

    }


    private void callFacebookLogout() {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(LoginActivity.this);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (futureFriends!=null && !futureFriends.isDone()) {
            futureFriends.cancel();
        }
    }

}
