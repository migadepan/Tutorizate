package com.example.migadepan.tutorizate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
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

/**
 * Created by migadepan on 30/12/2016.
 */

public class Conexion {
/*
    public void conectar(String laurl, String parametros){
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // crear una peticion http GET.
            try {
                URL url = new URL(laurl);
                String urlParameters = parametros;
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
                    String msg = jsonObject.getString("msg");
                    JSONObject usuario = jsonObject.getJSONObject("usuario");
                    String id = usuario.getString("id");
                    String nombre = usuario.getString("nombre");
                    String correo = usuario.getString("correo");
                    String preferencia = usuario.getString("preferencia");
                    String idioma = usuario.getString("idioma");
                    //Creamos el usuario
                    Usuario usuarioConectado = Usuario.getUsuario(id,nombre,correo,preferencia,idioma);
                    usuarioConectado.setPassword(pass.getText().toString());
                    urlConnection.disconnect();

                    //Entramos en la actividad
                    Intent actividad_menu = new Intent(getApplicationContext(), MenuLatActivity.class);
                    startActivity(actividad_menu);
                } else {
                    Toast avisoPassDiferente = Toast.makeText(getApplicationContext(), "Usuario o pass incorrecto", Toast.LENGTH_SHORT);
                    avisoPassDiferente.show();
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

    }*/
}
