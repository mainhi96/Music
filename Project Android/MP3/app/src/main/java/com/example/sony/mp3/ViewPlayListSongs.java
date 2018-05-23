package com.example.sony.mp3;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sony.mp3.datamodel.Playlist;
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

public class ViewPlayListSongs extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private ListView listview;
    private Button btnAddSong, btnDelSong, btnPlay;
    private TextView plname;
    public boolean flag = false;
    public int po;
    private Playlist playlist;
    public  ArrayList<Song> dataSong = new ArrayList<>();
    SongAdapter adapter;
    private Button btnChange;
    private Dialog dialog;
    public String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_playlist);

        searchView = (SearchView) findViewById(R.id.searchSong);
        listview = (ListView) findViewById(R.id.lvPlaylistSongs);
        btnAddSong = (Button) findViewById(R.id.addSong);
        btnDelSong = (Button) findViewById(R.id.delSong);
        plname = (TextView) findViewById(R.id.plname);
        btnPlay= (Button) findViewById(R.id.btnPlayPl);
        btnChange= (Button) findViewById(R.id.btnChange);
        Intent intent = getIntent();
        playlist = new Playlist(intent.getIntExtra("playlistid", 0), MainActivity.Usernamemain, intent.getStringExtra("name"));
        plname.setText("Playlist: " + playlist.getName());
        new ViewPlayListSongs.FindList().execute(MainActivity.IP+"music/api/playlist?playlistid=" + playlist.getPlaylistid());

        // khởi tạo adapter

        adapter = new SongAdapter(ViewPlayListSongs.this,R.layout.view_song_item, dataSong);
        listview.setAdapter(adapter);
        searchView.setOnQueryTextListener(this);

        btnDelSong.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    if (!flag){
                        btnDelSong.setText("Hủy chế độ xóa");
                        flag=true;
                    }
                    else {
                        btnDelSong.setText("Xóa bài hát");
                        flag=false;
                    }
            }

        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              po=position;
                Song song = new Song();
                song = (Song) listview.getItemAtPosition(position);
                if (flag){
                        new ViewPlayListSongs.DelSong().execute(MainActivity.IP+"music/api/playlist?playlist="+playlist.getPlaylistid()+"&song="+song.getId());
                }
                else {

                    int idx= song.getId();
                    Intent intent = new Intent(ViewPlayListSongs.this, PlaySong.class);
                    intent.putExtra("id", idx);
                    intent.putExtra("name", song.getName());
                    intent.putExtra("singer",song.getSinger());
                    intent.putExtra("link",song.getLink());
                    startActivity(intent);
                }

            }
        });
        btnAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=0;
                Toast.makeText(ViewPlayListSongs.this,playlist.getPlaylistid()+"",Toast.LENGTH_LONG).show();
               Intent intent1=new Intent(ViewPlayListSongs.this,AddSongToPlaylist.class);
               intent1.putExtra("id",playlist.getPlaylistid());
               startActivity(intent1);
               finish();
            }
        });

    btnPlay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (dataSong.size()>0){
                Intent intent2= new Intent(ViewPlayListSongs.this, PlayPlaylist.class);
                intent2.putExtra("playlistid",playlist.getPlaylistid());
                intent2.putExtra("size", dataSong.size());
                for (int i=0; i<dataSong.size();i++){
                    intent2.putExtra("URL "+i,dataSong.get(i).getLink());
                }
                startActivity(intent2);
                finish();
            }
            else Toast.makeText(ViewPlayListSongs.this,"Playlist chưa có bài hát",Toast.LENGTH_LONG).show();
        }
    });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(ViewPlayListSongs.this);
                dialog.setContentView(R.layout.dialog_add_playlist);
                // xét layout cho dialog
                dialog.setTitle("Change Playlist Name");
                dialog.show();
                // xét tiêu đề cho dialog

                Button dialogButton = (Button) dialog.findViewById(R.id.submitAddpl);
                // khai báo control trong dialog để bắt sự kiện
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edtAdd;
                        edtAdd= (EditText) dialog.findViewById(R.id.namepl);

                        text=edtAdd.getText().toString();
                        new ViewPlayListSongs.ChangeName().execute(MainActivity.IP+"music/api/playlist?name="+edtAdd.getText().toString()+"&id="+playlist.getPlaylistid());
                        dialog.dismiss();
                    }
                });
                // bắt sự kiện cho nút đăng kí

            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    //phương thúc lọc khi search
    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
            listview.clearTextFilter();
        } else {
            adapter.getFilter().filter(newText.toString());
        }
        return true;
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
            for (String id: result){
                new ViewPlayListSongs.FindSong().execute(MainActivity.IP+"music/api/song?id="+id);

            }
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
           dataSong.add(result);
           adapter.notifyDataSetChanged();
        }
    }
    public class DelSong extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                System.out.println(url.toString());

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
            System.out.println(result);

            if (result.equals("true")){


                Toast.makeText(ViewPlayListSongs.this,"Xóa bài hát thành công",Toast.LENGTH_LONG).show();
                Song pl= dataSong.get(po);
                dataSong.remove(po);
                adapter.notifyDataSetChanged();
            }

            else   Toast.makeText(ViewPlayListSongs.this,"Xóa Playlist không thành công",Toast.LENGTH_LONG).show();

        }
    }
    public class ChangeName extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");

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


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //   tvKq.setText(result);
                System.out.println(result);

                Toast.makeText(ViewPlayListSongs.this,"Đổi tên thành công",Toast.LENGTH_LONG);
                plname.setText("Playlist: " + text);
                playlist.setName(text);



        }
    }
}