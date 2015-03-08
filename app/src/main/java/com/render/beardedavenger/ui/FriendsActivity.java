package com.render.beardedavenger.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.render.beardedavenger.R;
import com.render.beardedavenger.adapters.FriendsAdapter;
import com.render.beardedavenger.io.ApiClient;
import com.render.beardedavenger.models.ModelFriends;
import com.render.beardedavenger.util.Constants;

import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class FriendsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private int auxProgress;
    private ListView listViewFriends;
    private FriendsAdapter friendsAdapter;
    private Future futureFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);

        listViewFriends = (ListView) findViewById(R.id.listViewFriends);

        obtainFriends();

    }


    private void obtainFriends () {

        final ProgressDialog progressDialog = new ProgressDialog(FriendsActivity.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage(getString(R.string.text_loading));
        progressDialog.show();

        futureFriends = ApiClient.requestWebGet(FriendsActivity.this, Constants.URL_FRIENDS, new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                if (result != null) {

                    Log.d("TAG", result.toString());
                    Gson gson = new Gson();
                    List<ModelFriends> modelFriendses = gson.fromJson(result, new TypeToken<List<ModelFriends>>() {
                    }.getType());

                    friendsAdapter = new FriendsAdapter(FriendsActivity.this, modelFriendses);
                    listViewFriends.setAdapter(friendsAdapter);
                    listViewFriends.setOnItemClickListener(FriendsActivity.this);
                    listViewFriends.setOnScrollListener(FriendsActivity.this);

                    new ProgressDataPerfil().execute();
                } else {
                    if (!futureFriends.isCancelled()) {
                        Toast.makeText(FriendsActivity.this, R.string.message_error, Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.dismiss();

            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (futureFriends!=null && !futureFriends.isDone()) {
            futureFriends.cancel();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {




    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d("TAG_SCROOL", view.getScrollY() + "");
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
                    Thread.sleep(10 + auxProgress);
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
            friendsAdapter.setNewProgress(values[0]);

        }
    }


}
