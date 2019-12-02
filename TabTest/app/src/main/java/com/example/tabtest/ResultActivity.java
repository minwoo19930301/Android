package com.example.tabtest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        // 아래 부분은 fragment에서 데이터를 넘기는 방법이다. 생략 가능.
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if (bundle.getString("some") != null){
                Toast.makeText(getApplicationContext(),
                        "data:" +bundle.getString("some"),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
