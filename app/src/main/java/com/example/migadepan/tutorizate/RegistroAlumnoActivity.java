package com.example.migadepan.tutorizate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegistroAlumnoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_alumno);
        getSupportActionBar().hide();

        final String correo = getIntent().getExtras().getString("correo");
        final String pass = getIntent().getExtras().getString("password");

        final EditText nombre = (EditText) findViewById(R.id.caja_nombre_alumno);
        final EditText apellidos = (EditText) findViewById(R.id.caja_apellidos_alumno);
        final EditText dni = (EditText) findViewById(R.id.caja_dni_alumno);
        final EditText facultad = (EditText) findViewById(R.id.caja_facultad_alumno);
        Button btnFinRegistro = (Button) findViewById(R.id.btn_registro_alumno);

        btnFinRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreAlumno = nombre.getText().toString();
                String apellidosAlumno = apellidos.getText().toString();
                String dniAlumno = dni.getText().toString();
                String facultadAlumno = facultad.getText().toString();

                if(!nombreAlumno.isEmpty() && !apellidosAlumno.isEmpty() && !dniAlumno.isEmpty() && !facultadAlumno.isEmpty()){
                    insertarUsuario(nombreAlumno,apellidosAlumno,dniAlumno,correo,pass);

                }else{
                    Toast aviso = Toast.makeText(getApplicationContext(), "Deben completarse todos los campos", Toast.LENGTH_SHORT);
                    aviso.show();
                }
            }
        });

        ImageView btnAtras = (ImageView) findViewById(R.id.btn_atras_reg_alumno);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registro = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(registro);
            }
        });
    }


    private void insertarUsuario(String nombre, String apellido, String dni, String email, String pass){
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // crear una peticion http GET.
            try {
                URL url = new URL("http://tutorizate.migadepan.es/index.php?action=insertarUsuario");
                String urlParameters = "dni="+dni+"&nombre="+nombre+"&apellidos="+apellido+"&dni="+dni+"&email="+email+"&password="+pass+"&alumno=true";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                urlConnection.setUseCaches(false);
                try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write(postData);
                }
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());


                String result = getStringFromInputStream(inputStream);
                JSONObject jsonObject = new JSONObject(result);
                String estado = jsonObject.getString("estado");

                if (estado.equals("correcto")) {
                    JSONObject usuario = jsonObject.getJSONObject("usuario");
                    String dniN = usuario.getString("dni");
                    String nombreN = usuario.getString("nombre");
                    String apellidosN = usuario.getString("apellidos");
                    String correoN = usuario.getString("email");
                    String passN = usuario.getString("password");

                    Usuario nuevoUsuario = Usuario.getUsuario(dniN,nombreN,apellidosN,correoN,passN);

                    Intent actividad_inicio = new Intent(getApplicationContext(), MenuAlumnoActivity.class);
                    startActivity(actividad_inicio);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    //Funcion para convertir un InputStream a String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
