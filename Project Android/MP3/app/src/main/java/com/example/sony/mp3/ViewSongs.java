package com.example.sony.mp3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

public class ViewSongs extends AppCompatActivity implements SearchView.OnQueryTextListener {
    protected SearchView searchView;
    protected ListView listview;
    // tạo string array Name cho listview

    public static   ArrayList<Song> data= new ArrayList<>();
   SongAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setView();
        new FindList().execute(MainActivity.IP+"music/api/song");

        // khởi tạo adapter
        listview = (ListView) findViewById(R.id.lvData);

        searchView= (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        ListViewClick();
    }
public void ListViewClick(){
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //  new FindInfo().execute("http://192.168.1.108/alumniserver/api/alumni?nameinfo="+listview.getItemAtPosition(position).toString());
            Song song= new Song();
            song=(Song) listview.getItemAtPosition(position);
            int idx= song.getId();
            Intent intent = new Intent(ViewSongs.this, PlaySong.class);
            intent.putExtra("id", idx);
            intent.putExtra("name", song.getName());
            intent.putExtra("singer",song.getSinger());
            intent.putExtra("link",song.getLink());
            startActivity(intent);
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
        if (TextUtils.isEmpty(newText)){
            adapter.getFilter().filter("");
            listview.clearTextFilter();
        }else {
            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }
    public class FindList extends AsyncTask<String,String,ArrayList<Song>> {
        @Override
        protected ArrayList<Song> doInBackground(String... params) {
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

                    JSONArray jsonArray = new JSONArray(finalJson);
                    //3. Duyệt từng đối tượng trong Array và lấy giá trị ra
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int id=  Integer.parseInt(jsonObject.optString("id").toString());
                        String name1 = jsonObject.optString("name").toString() ;
                        String singer= jsonObject.optString("singer").toString();
                        String link=jsonObject.optString("link").toString();
                        Song song= new Song(id,name1,singer,link);
                        data.add(song);
                        System.out.println(name1);

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
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Song> result) {
            super.onPostExecute(result);
            adapter = new SongAdapter(ViewSongs.this,R.layout.view_song_item, result);
            listview.setAdapter(adapter);
        }
    }
    public void setView(){
        setContentView(R.layout.activity_view_songs);

    }
}