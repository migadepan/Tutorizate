package com.example.migadepan.tutorizate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        final EditText correo_login = (EditText) findViewById(R.id.correo_login);
        final EditText password_login = (EditText) findViewById(R.id.password_login);

        Button btn_entrar = (Button) findViewById(R.id.entrar_login);
        btn_entrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    // crear una peticion http GET.
                    try {
                        URL url = new URL("http://tutorizate.migadepan.es/index.php?action=login");
                        String urlParameters = "correo="+correo_login.getText().toString()+"&password="+password_login.getText().toString();
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
                        System.out.println(estado);

                        if (estado.equals("correcto")) {
                            String msg = jsonObject.getString("msg");
                            JSONObject usuario = jsonObject.getJSONObject("usuario");
                            String dni = usuario.getString("DNI");
                            String nombre = usuario.getString("nombre");
                            String apellidos = usuario.getString("apellidos");
                            String email = usuario.getString("email");
                            String password = usuario.getString("password");
                            //Creamos el usuario
                            Usuario usuarioConectado = Usuario.getUsuario(dni,nombre,apellidos,email,password);
                            String tipoUsuario = getTipoUsuario(dni);
                            usuarioConectado.setTipo(tipoUsuario);
                            urlConnection.disconnect();

                            //Entramos en la actividad según sea alumno o profesor
                            if(tipoUsuario.equals("alumno")){
                                Intent actividad_menu_alumno = new Intent(getApplicationContext(), MenuAlumnoActivity.class);
                                startActivity(actividad_menu_alumno);
                            }else{
                                Intent actividad_menu_profesor = new Intent(getApplicationContext(), MenuProfesorActivity.class);
                                startActivity(actividad_menu_profesor);
                            }

                        } else {
                            Toast avisoPassDiferente = Toast.makeText(getApplicationContext(), "Usuario o password incorrecto", Toast.LENGTH_SHORT);
                            avisoPassDiferente.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        TextView btn_registro = (TextView) findViewById(R.id.btn_registro);
        btn_registro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent actividad_registro = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(actividad_registro);
            }
        });





    }


    private String getTipoUsuario(String dni){
        System.out.println("Entro a ver tipo");
        String tipoUsuario = "";
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // crear una peticion http GET.
            try {
                URL url = new URL("http://tutorizate.migadepan.es/index.php?action=tipoUsuario");
                String urlParameters = "dni="+dni;
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
                    tipoUsuario = jsonObject.getString("tipo");
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoUsuario;
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
