package com.example.sony.mp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sony.mp3.datamodel.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PlayPlaylist extends AppCompatActivity  {

    private Button btnBackward, btnPause, btnPlay, btnForward, btnBack, btnNext;
    private ImageView imageView;
    public static MediaPlayer mediaPlayer= new MediaPlayer();
    public double startTime = 0;
    public double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private int point=0;

    private TextView tx1,tx2,tx3, tx4;
    private ProgressDialog progressDialog;
    public static boolean flag=false;
    public int f=0;
    private ListView listView;
    private ArrayList<Song> songList= new ArrayList<>();
    private SongAdapter adapter;
    private ArrayList<String> listURL= new ArrayList<>();
    private int index=0;
    public static boolean fl=false;
    public String x="";
    public final String def="0 min, 0 sec";
    public int fla=0;
    public static boolean c=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_playlist);
        listView = findViewById(R.id.llvList1);
        if (PlaySong.mediaPlayer.isPlaying()) {
            PlaySong.mediaPlayer.stop();
            PlaySong.mediaPlayer= new MediaPlayer();

        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
        }

        adapter = new SongAdapter(PlayPlaylist.this, R.layout.view_song_item, songList);
        listView.setAdapter(adapter);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnBackward = (Button) findViewById(R.id.btnBackward);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnForward = (Button) findViewById(R.id.btnForward);
        btnBack = (Button) findViewById(R.id.btnBack);
        imageView = (ImageView) findViewById(R.id.imageView);
        tx1 = (TextView) findViewById(R.id.textView2);
        tx3 = (TextView) findViewById(R.id.textView4);
        tx4 = (TextView) findViewById(R.id.textView5);

         Intent intent= getIntent();
        int playlistid= intent.getIntExtra("playlistid",0);
        int size = intent.getIntExtra("size",0);
        new PlayPlaylist.FindList().execute(MainActivity.IP+"music/api/playlist?playlistid=" + playlistid);
        for (int i=0;i<size;i++){
            listURL.add(intent.getStringExtra("URL "+i));
        }

       mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);

        tx1.setText(String.format("%d :, %d :",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );

        System.out.println(def);


        myHandler.postDelayed(UpdateSongTime,1000);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fla=1;
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                x=tx1.getText().toString();
                fla=2;
                mediaPlayer.pause();

            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;
                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+50000);
                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                if(index+1>=songList.size()) index=0;
                else index=index+1;
                Start();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                if(index-1<0) index=songList.size();
                else index=index-1;
                System.out.println(songList.get(index));
                Start();
            }
        });


}


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            String x2= tx1.getText().toString();
            tx1.setText(String.format("%d min, %d sec",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );


            myHandler.postDelayed(this, 1000);
            if (!mediaPlayer.isPlaying() && fla!=2)
            if (x2.equals(tx1.getText().toString()) && (!x2.equals(x)) && (!x2.equals(def)))
            {
                if(index+1>=songList.size()) index=0;
                else index=index+1;
                System.out.println(songList.get(index).getName());
                Start();
            }
        }
    };

    public void Start(){
        if (c=true)
        if (PlaySong.mediaPlayer.isPlaying()) {
            PlaySong.mediaPlayer.stop();
            PlaySong.mediaPlayer= new MediaPlayer();

        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
        }
        x="";
//        Song song= songList.get(index);
  //      tx4.setText(song.getName());
    //    tx3.setText(song.getSinger());
        Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        new  PlayPlaylist.Player().execute("https://drive.google.com/uc?id="+listURL.get(index));

    }

    @Override
    protected void onPause() {
        super.onPause();
        c=true;
    }

    public class FindList extends AsyncTask<String, String, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                //       connection.setRequestMethod("GET");
                connection.connect();
                System.out.println(url.toString());

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                ArrayList<String> s= new ArrayList<>();
                try {

                    JSONArray jsonArray = new JSONArray(finalJson);
                    //3. Duyệt từng đối tượng trong Array và lấy giá trị ra
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        String songid = jsonObject.optString("songid").toString();
                        s.add(songid);
                        System.out.println(songid);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return s;
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
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            f=result.size();
            for (String id: result){
                new PlayPlaylist.FindSong().execute(MainActivity.IP+"music/api/song?id="+id);

            }
            Start();
        }
    }
    public class FindSong extends AsyncTask<String,String,Song> {
        Song song= new Song();
        @Override
        protected Song doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                //       connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line="";

                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                try {


                    JSONObject jsonObject = new JSONObject(finalJson);

                    int id=  Integer.parseInt(jsonObject.optString("id").toString());
                    String name1 = jsonObject.optString("name").toString() ;
                    String singer= jsonObject.optString("singer").toString();
                    String link=jsonObject.optString("link").toString();
                    song= new Song(id,name1,singer,link);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return song;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if(connection!=null){
                    connection.disconnect();
                }

                if(reader!=null){
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
        protected void onPostExecute(Song result) {
            super.onPostExecute(result);
            songList.add(result);
            adapter.notifyDataSetChanged();
        }
    }
    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;

            try {
                mediaPlayer= new MediaPlayer();
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
                System.out.println(strings[0]);

            } catch (Exception e) {
                System.out.println("MyAudioStreamingApp  "+ e.getMessage());
                prepared = false;
            }


            return prepared;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            mediaPlayer.start();
            flag=true;
            fl=true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }


    }
}
