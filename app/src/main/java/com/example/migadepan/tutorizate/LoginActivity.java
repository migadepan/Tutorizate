package com.example.migadepan.tutorizate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        final EditText usuario = (EditText) findViewById(R.id.correo_login);
        final EditText password = (EditText) findViewById(R.id.password_login);

        Button btn_entrar = (Button) findViewById(R.id.entrar_login);
        btn_entrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }
}
