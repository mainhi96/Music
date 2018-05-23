package com.example.sony.mp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import java.util.concurrent.TimeUnit;

import static java.lang.Double.NaN;

public class PlaySong extends AppCompatActivity {
    protected Button btnBackward, btnPause, btnPlay, btnForward, btnBack, btnNext;
    protected ImageView imageView;
    public static MediaPlayer mediaPlayer= new MediaPlayer();
    public double startTime = 0;
    public double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private int point=0;

    public TextView tx1,tx2,tx3, tx4;
    private ProgressDialog progressDialog;
    public static boolean flag=false;
    public boolean f=false;
    public static Song song;

    protected EditText edComment14;
   protected Button btnComment14, btnVote;
    protected ListView listView;
     private ArrayAdapter<String> adapter;
    ArrayList<String>listComment= new ArrayList<>();
    private TextView tvPoint;
    private RatingBar rate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();

        progressDialog = new ProgressDialog(this);
         Play(0);
        init();


        tx1.setText(String.format("%d :, %d :",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );
        myHandler.postDelayed(UpdateSongTime,100);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;
                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
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
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );


            myHandler.postDelayed(this, 100);
        }
    };

    public void setView(){
        setContentView(R.layout.activity_play_song);
        if (PlayPlaylist.mediaPlayer.isPlaying()) {
            PlayPlaylist.mediaPlayer.stop();
            PlayPlaylist.mediaPlayer= new MediaPlayer();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
        }
        btnNext =(Button)findViewById(R.id.btnNext1);
        btnBackward = (Button) findViewById(R.id.btnBackward1);
        btnPause = (Button) findViewById(R.id.btnPause1);
        btnPlay =(Button)findViewById(R.id.btnPlay1);
        btnForward =(Button)findViewById(R.id.btnForward1);
        btnBack =(Button)findViewById(R.id.btnBack1);
        imageView =(ImageView)findViewById(R.id.imageView1);
        tx1=(TextView)findViewById(R.id.textView21);
        tx3=(TextView)findViewById(R.id.textView41);
        tx4=(TextView)findViewById(R.id.textView51);
        listView=findViewById(R.id.llvList);
    }
    public void init(){
        tvPoint= (TextView) findViewById(R.id.tvPoint) ;
        rate= (RatingBar) findViewById(R.id.rd);
        edComment14=findViewById(R.id.edComment14);
        btnComment14=findViewById(R.id.btnComment14);
        btnVote= (Button) findViewById(R.id.btnVote);
        new PlaySong.ListComment().execute(song.getId()+"");
        btnComment14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edComment14.getText().equals(""))
                    new PlaySong.AddComment().execute(MainActivity.IP+"music/api/vote?songid="+song.getId()+"&username="+MainActivity.Usernamemain+"&comment="+edComment14.getText());
                System.out.println(edComment14.getText());
            }
        });

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rating=String.valueOf(rate.getRating());
                Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();


                new PlaySong.Vote().execute(MainActivity.IP+"music/api/vote?songid="+song.getId()
                        +"&username="+MainActivity.Usernamemain+
                        "&point="+rating);



            }
        });
    }
    public void Play(int a){


        Intent intent= getIntent();
        song=new Song(intent.getIntExtra("id",0),intent.getStringExtra("name"),intent.getStringExtra("singer"),intent.getStringExtra("link"));


        tx4.setText(song.getName());
        tx3.setText(song.getSinger());
        Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        Player  player= new Player();
        player.execute("https://drive.google.com/uc?id="+song.getLink());

    }
    class Player extends AsyncTask<String, Void, Boolean> {
            @Override
            protected Boolean doInBackground(String... strings) {
                Boolean prepared = false;

                try {

                    mediaPlayer.setDataSource(strings[0]);
                    mediaPlayer.prepare();
                    prepared = true;
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
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.setMessage("Buffering...");
                progressDialog.show();
            }


    }
    public class ListComment extends AsyncTask<String,String,ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            ArrayList<String> data= new ArrayList<>();

            try {
                URL url = new URL(MainActivity.IP+"music/api/vote?songidp="+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line="";
                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                System.out.println("DIEM" +finalJson);
                publishProgress(finalJson);

                 url = new URL(MainActivity.IP+"music/api/vote?songid="+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();
                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                finalJson = buffer.toString();


                try {

                    JSONArray jsonArray = new JSONArray(finalJson);
                    //3. Duyệt từng đối tượng trong Array và lấy giá trị ra
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String name1 = jsonObject.optString("username").toString()+": "+ jsonObject.optString("content").toString();
                        data.add(name1);
                         System.out.println("Cmt :"+ name1);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return data;





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


            return data;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (!values[0].equals("NaN"))
            tvPoint.setText("Rate:  "+values[0]);
            else tvPoint.setText("Rate:  0.0");

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);


           listComment=result;
            adapter = new ArrayAdapter<String>(PlaySong.this,android.R.layout.simple_list_item_1, listComment);
            listView.setAdapter(adapter);

        }

    }

    public class AddComment extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line="";

                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                return finalJson;





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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //   tvKq.setText(result);
                String i= MainActivity.Usernamemain+":  "+ edComment14.getText().toString();
                listComment.add(i);
               adapter.notifyDataSetChanged();
               edComment14.setText("");



         //   else   Toast.makeText(PlaySong.this,"Comment không thành công",Toast.LENGTH_LONG).show();

        }
    }
    public class Vote extends AsyncTask<String, String, String> {
        @Override
        protected String  doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                System.out.println(url);

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


            return "";

        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            double i = Double.parseDouble(value);
            if (i<=0) Toast.makeText(PlaySong.this,"Mỗi người chỉ được đánh gía 1 lần", Toast.LENGTH_LONG).show();
            else tvPoint.setText("VOTE:  "+i);
            }

        }
    }

