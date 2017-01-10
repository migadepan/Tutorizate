package com.example.migadepan.tutorizate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();

        final Switch opc_alumno = (Switch) findViewById(R.id.opc_alumno);
        final Switch opc_profe = (Switch) findViewById(R.id.opc_profesor);
        final EditText caja_email = (EditText) findViewById(R.id.caja_email);
        final EditText caja_psw1 = (EditText) findViewById(R.id.caja_password1);
        final EditText caja_psw2 = (EditText) findViewById(R.id.caja_password2);


        opc_alumno.setChecked(true);
        if (opc_alumno != null) {
            opc_alumno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        opc_profe.setChecked(false);
                    } else {
                        // The toggle is disabled
                        opc_profe.setChecked(true);
                    }
                }
            });
        }

        if(opc_profe != null){
            opc_profe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        // The toggle is enabled
                        opc_alumno.setChecked(false);
                    } else {
                        // The toggle is disabled
                        opc_alumno.setChecked(true);
                    }
                }
            });
        }


        Button btn_siguiente_reg = (Button) findViewById(R.id.btn_sig_registro);
        btn_siguiente_reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                boolean pasaCorreo = pattern.matcher(caja_email.getText()).matches();

                if(!pasaCorreo){
                    Toast avisoCorreo = Toast.makeText(getApplicationContext(),"Escriba un correo válido", Toast.LENGTH_SHORT);
                    avisoCorreo.show();
                }

                String contra1= caja_psw1.getText().toString();
                String contra2= caja_psw2.getText().toString();
                if (!contra1.equals(contra2)){
                    Toast avisoPassDiferente = Toast.makeText(getApplicationContext(),"Las contraseñas deben coincidir", Toast.LENGTH_SHORT);
                    avisoPassDiferente.show();
                }else{
                    if(opc_alumno.isChecked()){
                        Intent actividad_registro_alumno = new Intent(getApplicationContext(), RegistroAlumnoActivity.class);
                        actividad_registro_alumno.putExtra("correo",caja_email.getText().toString());
                        actividad_registro_alumno.putExtra("password",caja_psw1.getText().toString());
                        startActivity(actividad_registro_alumno);
                    }else{
                        Intent actividad_registro_profe = new Intent(getApplicationContext(), RegistroProfesorActivity.class);
                        actividad_registro_profe.putExtra("correo",caja_email.getText().toString());
                        actividad_registro_profe.putExtra("password",caja_psw1.getText().toString());
                        startActivity(actividad_registro_profe);
                    }

                }
            }
        });


        ImageView btnAtras = (ImageView) findViewById(R.id.btn_atras_registro);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        });

    }
}
