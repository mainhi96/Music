package com.example.sony.mp3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangePass extends AppCompatActivity {
    EditText confirm;
    EditText passwords;
    private Button btnSub;
    private TextView tvKq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        btnSub = (Button) findViewById(R.id.btnSub);


        passwords = findViewById(R.id.edtChange);
        confirm = findViewById(R.id.edtChange2);

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String confrirm=confirm.getText().toString();
                String pass=passwords.getText().toString();
                if (confrirm.equals("")||pass.equals("")) return;
                if (confrirm.equals(pass))
                    new ChangePass.Change().execute(MainActivity.IP+"music/api/account?name="+ MainActivity.Usernamemain+"&pass="+pass);
                else Toast.makeText(ChangePass.this,"Mật khẩu không trùng khớp",Toast.LENGTH_LONG).show();
            }
        });
    }
    public class Change extends AsyncTask<String,String,String> {

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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //   tvKq.setText(result);

            if (result.equals("true")){
                Toast.makeText(ChangePass.this,"Cập nhật thành công, mời đăng nhập lại",Toast.LENGTH_LONG).show();
                Intent intent = new Intent( ChangePass.this,SignIn.class);
                startActivity(intent);
            }
            else tvKq.setText(result);


        }
    }

}

