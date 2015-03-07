package com.render.beardedavenger.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.render.beardedavenger.R;
import com.render.beardedavenger.util.CirclePicture;
import com.render.beardedavenger.util.Constants;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends ActionBarActivity {

    private ImageView imageViewPerfil;
    private TextView textViewUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);

        imageViewPerfil = (ImageView) findViewById(R.id.imageViewPerfil);

        textViewUserName = (TextView) findViewById(R.id.textViewUserName);

        obtainUserInfo();

    }

    private void obtainUserInfo () {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE);

        String urlPicturePerfil = "https://graph.facebook.com/" + sharedPreferences.getString(Constants.USER_ID,"") + "/picture?width=200&height=200";
        Picasso.with(PerfilActivity.this).load(urlPicturePerfil).placeholder(R.drawable.ic_person_white_48dp).transform(new CirclePicture()).into(imageViewPerfil);

        textViewUserName.setText(sharedPreferences.getString(Constants.USER_NAME, ""));


    }


}
