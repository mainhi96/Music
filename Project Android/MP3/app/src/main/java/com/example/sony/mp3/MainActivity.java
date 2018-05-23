package com.example.sony.mp3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView tvName;
    public static String Usernamemain;
    public static final String IP= "http://192.168.1.56/";
    private Button btnSong, btnPlaylist, btnChangePass, btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName= (TextView) findViewById(R.id.tvName);
        Intent intentm= getIntent();
        String name = intentm.getStringExtra("username");
        Usernamemain=name;
        tvName.setText(name);
        btnChangePass= (Button) findViewById(R.id.btnPass);
        btnSignOut= (Button) findViewById(R.id.btnOut);

        btnSong= (Button) findViewById(R.id.btnSongs);
        btnSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewSongs.class);
                startActivity(intent);
            }
        });
        btnPlaylist= (Button) findViewById(R.id.btnPlaylist);
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewPlaylists.class);
                startActivity(intent);
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1= new Intent(MainActivity.this, ChangePass.class);
                startActivity(intent1);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }
        });

    }
}
