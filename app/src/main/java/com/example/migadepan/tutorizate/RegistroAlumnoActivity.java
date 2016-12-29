package com.example.migadepan.tutorizate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegistroAlumnoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_alumno);
        getSupportActionBar().hide();
    }
}
