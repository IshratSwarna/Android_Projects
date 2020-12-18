package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.app_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_show_output);
                Button sbtn = findViewById(R.id.back_btn);

                sbtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        setContentView(R.layout.activity_main);
                    }
                });
            }
        });
    }
}
