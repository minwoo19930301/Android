package com.motizensoft.android.httptest2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText etLoginId;
    TextView tvResult;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                tvResult.setText("Request Fail..");
            } else if(msg.what == 1) {
                tvResult.setText((String)msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etLoginId = findViewById(R.id.et_login_id);
        tvResult = findViewById(R.id.tv_result);
        TextView btnRequest = findViewById(R.id.btn_request);
        HttpThread httpThread = new HttpThread("http://api.openweathermap.org/data/2.5/weather?q=Seoul,KR&APPID=76d042c17320259413cd6a9a2788caa5&units=metric",
                etLoginId.getText().toString());
        httpThread.start();
    } // end oncreate
    class HttpThread extends Thread {
        private String url;
        private String loginId;

        public HttpThread(String url, String loginId) {
            this.url = url;
            this.loginId = loginId;
        }
        @Override
        public void run() {
            try {
                URL serverUrl = new URL(this.url + "?login_id="+this.loginId);
                HttpURLConnection http = (HttpURLConnection)serverUrl.openConnection();
                http.setRequestMethod("GET");
                http.setConnectTimeout(10*1000);
                http.setReadTimeout(10*1000);
                http.setDoInput(true);
                http.setDoOutput(false);

                BufferedReader in = new BufferedReader(
                                                         new InputStreamReader(
                                                    http.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                String strLine = null;
                while((strLine = in.readLine()) != null) {
                    sb.append(strLine);
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = sb.toString();
                handler.sendMessage(msg);
            } catch (Exception e) {
                handler.sendEmptyMessage(0);
            }
        }
    }
}







