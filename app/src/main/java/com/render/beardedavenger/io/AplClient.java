package com.render.beardedavenger.io;

import android.content.Context;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.render.beardedavenger.R;
import com.render.beardedavenger.util.CirclePicture;
import com.squareup.picasso.Picasso;

/**
 * Created by sati on 08/03/2015.
 */
public class AplClient {

    public static void loadImage(Context context, ImageView imageView, String url) {

        Picasso.with(context).load(url).placeholder(R.drawable.ic_person_white_48dp).transform(new CirclePicture()).into(imageView);

    }

    public static Future requestWebGet(Context context, String url, FutureCallback<JsonArray> futureCallback) {

        return Ion.with(context)
                .load(url)
                .asJsonArray()
                .setCallback(futureCallback);
    }


}
