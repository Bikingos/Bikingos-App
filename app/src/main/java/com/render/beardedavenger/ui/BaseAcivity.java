package com.render.beardedavenger.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.render.beardedavenger.util.Constants;

/**
 * Created by sati on 08/03/2015.
 */
public class BaseAcivity extends ActionBarActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE);

        Intent intent;

        if (sharedPreferences.getBoolean(Constants.IS_USER_LOGIN, false)) {
            intent = new Intent(BaseAcivity.this, HomeActivity.class);
        }
        else {
            intent = new Intent(BaseAcivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
