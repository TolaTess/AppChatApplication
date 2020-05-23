package com.example.appchatapplication.utils.offline;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class AppChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Firebase offline capabilities
        //must save class as app name
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picasso offline capabilities
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}

