package com.example.sony.mp3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignIn extends AppCompatActivity {
    private Button btnSignIn;
    private Button btnSignUp;
    private EditText edtName;
    private EditText edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn= (Button) findViewById(R.id.btnSignIn);
        btnSignUp= (Button) findViewById(R.id.btnSignUp);
        edtName= (EditText) findViewById(R.id.edtname);
        edtPass= (EditText) findViewById(R.id.edtpass);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  name= edtName.getText().toString();
                String pass= edtPass.getText().toString();
                if(name.length()>0&&pass.length()>0)
                    new DangNhap().execute(MainActivity.IP+"music/api/account?username="+name);
                else Toast.makeText(SignIn.this,"Xin nhập đủ thông tin",Toast.LENGTH_LONG).show();



            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SignIn.this, SignUp.class);
                startActivity(intent);

            }
        });


    }
    public class DangNhap extends AsyncTask<String, String, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
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
                if (finalJson.length() < 10) return null;
                ArrayList<String> data = new ArrayList<>();
             System.out.println(finalJson);

               /* JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("parentObject");
                JSONObject finalObject = parentArray.getJSONObject(0);

                String description = finalObject.getString("description");*/


                // String titre = finalObject.getString("title");
                try {

                    // JSONArray jsonArray = new JSONArray(finalJson);
                    //
                    //3. Duyệt từng đối tượng trong Array và lấy giá trị ra
                    //  for(int i = 0; i < jsonArray.length(); i++) {
                    //    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // String name = jsonObject.optString("user").toString();
                    JSONObject jsonObject = new JSONObject(finalJson);
                    String username = jsonObject.optString("username").toString().trim();
                    data.add(username);
                    String pass = jsonObject.optString("password").toString().trim();
                    data.add(pass);


                    //}
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
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if (strings != null) {
                if(strings.get(1).equals(edtPass.getText().toString()))
                {
                    Intent intent = new Intent( SignIn.this,MainActivity.class);
                    intent.putExtra("username", strings.get(0));
                    startActivity(intent);
                }
                else Toast.makeText(SignIn.this,"Sai thông tin username hoặc mật khẩu, mời nhập lại",Toast.LENGTH_LONG).show();

            }

        }
    }


}

