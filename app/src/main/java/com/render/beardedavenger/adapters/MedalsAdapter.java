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

        ImageView imageViewMedail = (ImageView) view.findViewById(R.id.imageViewFriend);
        TextView textViewTitleMedail = (TextView) view.findViewById(R.id.textViewUserName);
        textViewTitleMedail.setText(modelMedail.getTitleMedail());

        TextView textViewProgress = (TextView) view.findViewById(R.id.textViewNivel);
        textViewProgress.setText(modelMedail.getProgressInitial() + "/" + modelMedail.getMaxProgres());

        DonutProgress progressMadail = (DonutProgress) view.findViewById(R.id.progressExp);
        progressMadail.setProgress(modelMedail.getProgressInitialPecentaje());

        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewExp);
        textViewDescription.setText(modelMedail.getDescription());

        return view;
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
