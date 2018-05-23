package com.example.sony.mp3;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sony.mp3.datamodel.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONY on 5/15/2018.
 */

public class SongAdapter extends ArrayAdapter<Song> {

        Context context;
        int resource;
        List<Song> objects;


        public SongAdapter(@NonNull Context context, int resource, ArrayList<Song> objects) {
            super(context, resource,objects);
            this.context=context;
            this.resource=resource;
            this.objects=objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView==null){
                LayoutInflater layoutInflater=LayoutInflater.from(context);
                convertView=layoutInflater.inflate(resource,null);
            }

            TextView tvName=convertView.findViewById(R.id.tvSongName);
            TextView tvSinger=convertView.findViewById(R.id.tvSinger);

            Song u=objects.get(position);
            tvName.setText(u.getName());
            tvSinger.setText(u.getSinger());


            return convertView;
        }


    }




