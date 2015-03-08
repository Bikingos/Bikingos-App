package com.render.beardedavenger.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.render.beardedavenger.R;
import com.render.beardedavenger.adapters.MedalsAdapter;
import com.render.beardedavenger.io.ApiClient;
import com.render.beardedavenger.models.ModelMedail;
import com.render.beardedavenger.util.CirclePicture;
import com.render.beardedavenger.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class PerfilActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ImageView imageViewPerfil;
    private TextView textViewUserName;
    private int progresExp;
    private int progressMax;
    private int progresExpPocentaje;
    private int auxProgress;
    private IconRoundCornerProgressBar roundCornerProgressBar;
    private TextView textViewExpe;
    private ListView listViewMedails;
    private Future futureFriends;
    private MedalsAdapter medalsAdapter;


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
        roundCornerProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.progressBarExp);


        textViewExpe = (TextView) findViewById(R.id.textViewExpe);

        listViewMedails = (ListView) findViewById(R.id.listViewFriends);

        listViewMedails.setOnItemClickListener(this);

        obtainUserInfo();

    }


    private void obtainUserInfo () {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE);

        obtainLevel(sharedPreferences.getInt(Constants.USER_LEVEL, 1));

        String urlPicturePerfil = "https://graph.facebook.com/" + sharedPreferences.getString(Constants.USER_ID,"") + "/picture?width=200&height=200";
        Picasso.with(PerfilActivity.this).load(urlPicturePerfil).placeholder(R.drawable.ic_person_white_48dp).transform(new CirclePicture()).into(imageViewPerfil);

        textViewUserName.setText(sharedPreferences.getString(Constants.USER_NAME, ""));

        progresExp = sharedPreferences.getInt(Constants.USER_EXPERENCE, 50);
        progressMax = sharedPreferences.getInt(Constants.USER_MAX_EXPERENCE, 50);
        progresExpPocentaje = 0;

        obtainFriends();
    }


    private void obtainLevel(int level) {

        switch (level) {
            case 1:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_1);
                break;
            case 2:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_2);
                break;
            case 3:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_3);
                break;
            case 4:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_4);
                break;
            case 5:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_5);
                break;
            case 6:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_6);
                break;
            case 7:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_7);
                break;
            case 8:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_8);
                break;
            case 9:
                roundCornerProgressBar.setIconImageResource(R.drawable.ic_9);
                break;
        }
    }


    private void obtainFriends () {

        final ProgressDialog progressDialog = new ProgressDialog(PerfilActivity.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage(getString(R.string.text_loading));
        progressDialog.show();

        futureFriends = ApiClient.requestWebGet(PerfilActivity.this, Constants.URL_PERFIL, new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                if (result != null) {

                    Log.d("TAG", result.toString());
                    Gson gson = new Gson();

                    List<ModelMedail> modelMedails = gson.fromJson(result, new TypeToken<List<ModelMedail>>() {
                    }.getType());

                    medalsAdapter = new MedalsAdapter(PerfilActivity.this, modelMedails);
                    listViewMedails.setAdapter(medalsAdapter);
                    listViewMedails.setOnItemClickListener(PerfilActivity.this);

                    new ProgressDataPerfil().execute();
                } else {
                    if (!futureFriends.isCancelled()) {
                        Toast.makeText(PerfilActivity.this, R.string.message_error, Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.dismiss();

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (futureFriends!=null && !futureFriends.isDone()) {
            futureFriends.cancel();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_perfil, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor edit= getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE).edit();
        edit.clear().apply();

        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(PerfilActivity.this);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }

        Intent intent = new Intent(PerfilActivity.this, BaseAcivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }


    private class ProgressDataPerfil extends AsyncTask<Void, Integer, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            auxProgress = 0;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            while (auxProgress < 100) {
                try {
                    Thread.sleep(10+auxProgress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                auxProgress++;

                publishProgress(auxProgress);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            int newProgress= (int) (values[0]*(progressMax/100.0));

            if (newProgress <= progresExp) {
                roundCornerProgressBar.setProgress(values[0]);
                textViewExpe.setText(newProgress + "/"+progressMax+" Exp");
            }
            medalsAdapter.setNewProgress(values[0]);

        }
    }
    
    
   


}
