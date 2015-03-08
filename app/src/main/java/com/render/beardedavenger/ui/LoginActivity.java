package com.render.beardedavenger.ui;

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
import com.render.beardedavenger.R;
import com.render.beardedavenger.util.Constants;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends ActionBarActivity implements LoginButton.OnErrorListener, Session.StatusCallback, View.OnClickListener {

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

                                Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
                                startActivity(intent);

                            } else {
                                Log.d("Error", "1");
                                Toast.makeText(LoginActivity.this, R.string.message_error, Toast.LENGTH_SHORT).show();
                                callFacebookLogout();
                            }
                        }
                    }).executeAsync();
        }


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
}
