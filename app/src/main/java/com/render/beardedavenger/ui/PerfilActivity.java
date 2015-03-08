package com.render.beardedavenger.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.render.beardedavenger.R;
import com.render.beardedavenger.adapters.MedalsAdapter;
import com.render.beardedavenger.models.ModelMedail;
import com.render.beardedavenger.util.CirclePicture;
import com.render.beardedavenger.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class PerfilActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ImageView imageViewPerfil;
    private TextView textViewUserName;
    private int progresExp = 50;
    private int auxProgress;
    private RoundCornerProgressBar roundCornerProgressBar;
    private TextView textViewExpe;
    private ListView listViewMedails;
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
        roundCornerProgressBar = (RoundCornerProgressBar) findViewById(R.id.progressBarExp);
        textViewExpe = (TextView) findViewById(R.id.textViewExpe);

        listViewMedails = (ListView) findViewById(R.id.listViewMedails);

        List<ModelMedail> modelMedails = new ArrayList<>();
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));
        modelMedails.add(new ModelMedail("Atleta", 50, 100, false, 1, "Kilometros Recorridos"));

        medalsAdapter = new MedalsAdapter(PerfilActivity.this, modelMedails);
        listViewMedails.setAdapter(medalsAdapter);
        listViewMedails.setOnItemClickListener(this);


        obtainUserInfo();

    }

    private void obtainUserInfo () {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_USER, MODE_PRIVATE);

        String urlPicturePerfil = "https://graph.facebook.com/" + sharedPreferences.getString(Constants.USER_ID,"") + "/picture?width=200&height=200";
        Picasso.with(PerfilActivity.this).load(urlPicturePerfil).placeholder(R.drawable.ic_person_white_48dp).transform(new CirclePicture()).into(imageViewPerfil);

        textViewUserName.setText(sharedPreferences.getString(Constants.USER_NAME, ""));


        new ProgressDataPerfil().execute();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                if (auxProgress <= progresExp) {
                    publishProgress(auxProgress);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            roundCornerProgressBar.setProgress(values[0]);
            textViewExpe.setText(values[0] + "/100 Exp");
            medalsAdapter.setNewProgress(values[0]);

        }
    }


}
