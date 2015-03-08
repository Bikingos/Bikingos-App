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
import com.render.beardedavenger.models.ModelMedail;

import java.util.List;

/**
 * Created by sati on 07/03/2015.
 */
public class MedalsAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    private Context context;
    private List<ModelMedail> modelMedails;

    public MedalsAdapter(Context context, List<ModelMedail> modelMedails) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.modelMedails = modelMedails;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(position>0?R.layout.item_medail:R.layout.item_medail_top, parent, false);

        ModelMedail modelMedail = modelMedails.get(position);

        TextView textViewTitleMedail = (TextView) view.findViewById(R.id.textViewUserName);
        textViewTitleMedail.setText(modelMedail.getTitleMedail());

        ImageView imageViewMedail = (ImageView) view.findViewById(R.id.imageViewFriend);
        selectMadail(imageViewMedail,position);


        ImageView imageViewLock = (ImageView) view.findViewById(R.id.imageViewLock);
        TextView textViewProgress = (TextView) view.findViewById(R.id.textViewNivel);
        DonutProgress progressMadail = (DonutProgress) view.findViewById(R.id.progressExp);

        if (modelMedail.isUnlocked()) {
            imageViewLock.setVisibility(View.GONE);
            textViewProgress.setText(modelMedail.getProgress() + "");
        } else {
            textViewProgress.setText(modelMedail.getProgressInitial() + "/" + modelMedail.getMaxProgres());
        }

        progressMadail.setProgress(modelMedail.getProgressInitialPecentaje());

        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewExp);
        textViewDescription.setText(modelMedail.getDescription());

        return view;
    }


    private void selectMadail(ImageView imageView, int position) {

        int id = 0;


        switch (position)
        {
            case 0:
                id = R.drawable.rsz_viajero3;
                break;
            case 1:
                id = R.drawable.rsz_kilometros;
                break;
            case 2:
                id = R.drawable.rsz_contaminantes;
                break;
            case 3:
                id = R.drawable.rsz_1bases;
                break;
            case 4:
                id = R.drawable.rsz_unibases;
                break;
            case 5:
                id = R.drawable.rsz_conquistador;
                break;
            case 6:
                id = R.drawable.fortified;
                break;
            case 7:
                id = R.drawable.rsz_destroyer;
                break;
            case 8:
                id = R.drawable.rsz_warrior;
                break;

        }

        ApiClient.loadImageResource(context, imageView, id);

    }


    public void setNewProgress(int progressInitial) {

        for (int i = 0; i < modelMedails.size(); i++) {
            modelMedails.get(i).setProgressInitialPecentaje(progressInitial);

        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return modelMedails.size();
    }

    @Override
    public Object getItem(int position) {
        return modelMedails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
