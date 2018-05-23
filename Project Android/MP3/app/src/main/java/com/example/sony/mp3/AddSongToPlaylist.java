package com.example.sony.mp3;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sony.mp3.datamodel.Playlist;
import com.example.sony.mp3.datamodel.Song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.widget.LinearLayout.*;

/**
 * Created by SONY on 5/18/2018.
 */

public class AddSongToPlaylist extends ViewSongs {
    private Button btnAdd;
    public int playlistid;

    @Override
    public void setView() {
        super.setView();
        setContentView(R.layout.activity_view_songs);

        Intent intent = getIntent();
        playlistid = intent.getIntExtra("id", 0);
        Toast.makeText(AddSongToPlaylist.this, playlistid+"", Toast.LENGTH_LONG).show();
        btnAdd = new Button(this);
        btnAdd.setText("Xong");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(btnAdd, layoutParams);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(AddSongToPlaylist.this, ViewPlayListSongs.class);
                intent1.putExtra("playlistid",playlistid);
                startActivity(intent1);
                finish();
            }
        });

    }

    @Override
    public void ListViewClick() {
        super.ListViewClick();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song= new Song();
                song=(Song) listview.getItemAtPosition(position);

               new AddSongToPlaylist.AddSongToList().execute(MainActivity.IP+"music/api/playlist?playlistid="+playlistid+"&songid="+song.getId());

            }
        });

    }

    public class AddSongToList extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                System.out.println(url.toString());
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                return finalJson;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //   tvKq.setText(result);
            System.out.println(result);

            if (result.equals("true")) {
                Toast.makeText(AddSongToPlaylist.this, "Thêm bài hát thành công", Toast.LENGTH_LONG).show();

            } else
                Toast.makeText(AddSongToPlaylist.this, "Thêm bài hát không thành công", Toast.LENGTH_LONG).show();

        }
    }
}
