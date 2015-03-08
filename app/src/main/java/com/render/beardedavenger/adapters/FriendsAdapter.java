package com.render.beardedavenger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.render.beardedavenger.R;
import com.render.beardedavenger.io.ApiClient;
import com.render.beardedavenger.models.ModelFriends;

import java.util.List;

/**
 * Created by sati on 07/03/2015.
 */
public class FriendsAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    private Context context;

    private List<ModelFriends> modelFriendses;


    public FriendsAdapter(Context context, List<ModelFriends> modelFriendses) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.modelFriendses = modelFriendses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ModelFriends modelFriends = modelFriendses.get(position);

        View view = inflater.inflate(modelFriends.getTeam() == 1 ? R.layout.item_friend_green : R.layout.item_friend_red, parent, false);

        ImageView imageViewFriend = (ImageView) view.findViewById(R.id.imageViewFriend);
        ApiClient.loadImage(context, imageViewFriend, modelFriends.getUrlImage());

        TextView textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewUserName.setText(modelFriends.getUserName());

        TextView textViewExp = (TextView) view.findViewById(R.id.textViewExp);
        textViewExp.setText(modelFriends.getProgressInitial() + "/" + modelFriends.getMaxExp() + " EXP");

        DonutProgress donutProgress = (DonutProgress) view.findViewById(R.id.progressExp);
        donutProgress.setProgress(modelFriends.getProgressInitialPecentaje());


        TextView textViewNivel = (TextView) view.findViewById(R.id.textViewNivel);
        textViewNivel.setText("Nivel " + modelFriends.getNivel());


        return view;
    }

    public void setNewProgress(int progressInitial) {

        for (int i = 0; i < modelFriendses.size(); i++) {
            modelFriendses.get(i).setProgressInitialPecentaje(progressInitial);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return modelFriendses.size();
    }

    @Override
    public Object getItem(int position) {
        return modelFriendses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
