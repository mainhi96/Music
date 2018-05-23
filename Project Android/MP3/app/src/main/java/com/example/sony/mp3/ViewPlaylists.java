package com.example.sony.mp3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.sony.mp3.datamodel.Playlist;

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

public class ViewPlaylists extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private ListView listview;
    private Button btnAddPlaylist, btnDelPlaylist;
    // tạo string array Name cho listview

    public ArrayList<Playlist> data = new ArrayList<>();
    ArrayAdapter<String> adapter;
    public String text;
    public Dialog dialog;
    public boolean flag=false;
    public int po;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);

        new ViewPlaylists.FindList().execute(MainActivity.IP+"/music/api/playlist?username=" + MainActivity.Usernamemain);

        // khởi tạo adapter
        listview = (ListView) findViewById(R.id.lvPlaylist);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
               final Playlist p= data.get(position);
               po=position;
                //  new FindInfo().execute("http://192.168.1.108/alumniserver/api/alumni?nameinfo="+listview.getItemAtPosition(position).toString());
              if (!flag) {

                  Intent intent= new Intent(ViewPlaylists.this, ViewPlayListSongs.class);
                  intent.putExtra("playlistid", p.getPlaylistid());
                  intent.putExtra("name",p.getName());
                  startActivity(intent);
              }
            else {
                   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewPlaylists.this);
                  alertDialogBuilder.setMessage("Delete this playlist");
                  alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface arg0, int arg1) {
                        new ViewPlaylists.DelPlaylist().execute(MainActivity.IP+"music/api/playlist?id="+p.getPlaylistid());


                      }
                  });

                  alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {

                      }
                  });

                  AlertDialog alertDialog = alertDialogBuilder.create();
                  alertDialog.show();
              }
            }
        });
        btnAddPlaylist = (Button) findViewById(R.id.addPlaylist);
        btnDelPlaylist = (Button) findViewById(R.id.delPlaylist);
        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(ViewPlaylists.this);
                dialog.setContentView(R.layout.dialog_add_playlist);
                // xét layout cho dialog
                dialog.setTitle("Add Playlist");
                dialog.show();
                // xét tiêu đề cho dialog
             final EditText editText;
             editText= (EditText) findViewById(R.id.namepl);

                Button dialogButton = (Button) dialog.findViewById(R.id.submitAddpl);
                // khai báo control trong dialog để bắt sự kiện
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edtAdd;
                        edtAdd= (EditText) dialog.findViewById(R.id.namepl);
                        text=edtAdd.getText().toString();
                       new ViewPlaylists.AddPlaylist().execute(MainActivity.IP+"music/api/playlist?name="+edtAdd.getText().toString()+"&user="+MainActivity.Usernamemain);
                        dialog.dismiss();
                    }
                });
                // bắt sự kiện cho nút đăng kí

            }
        });
        btnDelPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag){
                    btnDelPlaylist.setText("Hủy chế độ xóa Playlist");
                    flag=true;
                }
                else {
                    btnDelPlaylist.setText("Xóa Playlist");
                    flag=false;
                }
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


    //phương thúc lọc khi search

    public class FindList extends AsyncTask<String, String, ArrayList<Playlist>> {
        @Override
        protected ArrayList<Playlist> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                //       connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                try {

                    JSONArray jsonArray = new JSONArray(finalJson);
                    //3. Duyệt từng đối tượng trong Array và lấy giá trị ra
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int id = Integer.parseInt(jsonObject.optString("id").toString());
                        String name1 = jsonObject.optString("name").toString();
                        String username = jsonObject.optString("singer").toString();
                        Playlist playlist = new Playlist(id, username, name1);
                        data.add(playlist);
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
        protected void onPostExecute(ArrayList<Playlist> result) {
            super.onPostExecute(result);
            data=result;
            ArrayList<String> namelist = new ArrayList<>();
            for (Playlist playlistx : result) {
                namelist.add(playlistx.getName());
            }
            adapter = new ArrayAdapter<String>(ViewPlaylists.this, android.R.layout.simple_list_item_1,namelist);
            listview.setAdapter(adapter);
        }
    }

    public class AddPlaylist extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
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
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            System.out.println(string);
            if (string.length() > 10|| string.equals("0")) {
                Toast.makeText(ViewPlaylists.this, "Thêm playlist không thành công", Toast.LENGTH_LONG).show();
                return;
            } else {
                int i= Integer.parseInt(string);
                Playlist p= new Playlist(i,MainActivity.Usernamemain,text);
                adapter.add(p.getName());
                adapter.notifyDataSetChanged();
                data.add(p);
                Toast.makeText(ViewPlaylists.this, "Thêm playlist thành công", Toast.LENGTH_LONG).show();

            }

        }
    }
    public class DelPlaylist extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");

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


                Toast.makeText(ViewPlaylists.this,"Xóa Playlist thành công",Toast.LENGTH_LONG).show();
                Playlist pl= data.get(po);
                adapter.remove(pl.getName());
                adapter.notifyDataSetChanged();
                data.remove(po);
            }

            else   Toast.makeText(ViewPlaylists.this,"Xóa Playlist không thành công",Toast.LENGTH_LONG).show();

        }
    }
}

