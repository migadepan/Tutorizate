package com.example.migadepan.tutorizate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.sql.Array;
import java.util.ArrayList;

public class MenuProfesorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_profesor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Obtengo mi dni
        Usuario usuarioConectado = Usuario.getInstancia();
        String dni = usuarioConectado.getDni();
        //Obtengo mis tutorias
        ArrayList<Tutoria> misTutorias = getTutorias(dni);
        ArrayList<Tutoria> reservadas = getTutoriasReservadas(misTutorias);

        //A ver si conseguimos mostrar algo en la lista
        ListView lista = (ListView) findViewById(R.id.listViewProfesor);
        AdapterItem adapter = new AdapterItem(this, reservadas);
        lista.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profesor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private ArrayList<Tutoria> getTutorias(String dni){
        ArrayList<Tutoria> misTutorias = new ArrayList();
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // crear una peticion http GET.
            try {
                URL url = new URL("http://tutorizate.migadepan.es/index.php?action=tutoriasProfesor");
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

                JSONArray tutorias = jsonObject.getJSONArray("tutorias");


                if (estado.equals("correcto")) {
                    for (int i=0; i<tutorias.length();i++){
                        JSONObject tutoria = tutorias.getJSONObject(i);
                        String idTutoria = tutoria.getString("id");
                        String horaInicio = tutoria.getString("horaInicio");
                        String horaFinal = tutoria.getString("horaFinal");
                        String diaSemana = tutoria.getString("diaSemana");
                        System.out.println(idTutoria + " " + horaInicio + " " + horaFinal + " " + diaSemana);
                        Tutoria nuevaTutoria = new Tutoria(idTutoria,dni,horaInicio,horaFinal,diaSemana);
                        misTutorias.add(nuevaTutoria);
                    }
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return misTutorias;
    }



    private ArrayList<Tutoria> getTutoriasReservadas(ArrayList<Tutoria> misTutorias){
        ArrayList<Tutoria> tutoriasReservadas = new ArrayList<Tutoria>();
        ArrayList<String> idTutorias = new ArrayList<String>();
        //Guardo los id para ver si están reservadas a día de hoy
        for (Tutoria tuto:misTutorias){
            idTutorias.add(tuto.getIdTutoria());
        }

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String tutoriasComparar = "";
        for (String misTutos: idTutorias){
            tutoriasComparar += misTutos + ",";
        }
        tutoriasComparar = tutoriasComparar.substring(0, tutoriasComparar.length()-1);

        if (networkInfo != null && networkInfo.isConnected()) {
            // crear una peticion http GET.
            try {
                URL url = new URL("http://tutorizate.migadepan.es/index.php?action=tutoriasReservadas");
                String urlParameters = "ids="+tutoriasComparar;
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
                JSONArray reservas = jsonObject.getJSONArray("reservas");

                if (estado.equals("correcto")) {
                    for (int i=0; i<reservas.length();i++){
                        JSONObject reserva = reservas.getJSONObject(i);
                        String idReserva = reserva.getString("idTutoria");
                        String dniAlumno = reserva.getString("dniAlumno");
                        String fecha = reserva.getString("fecha");
                        UsuarioAuxiliar alumno = getDatosAlumno(dniAlumno);

                        for (int j=0; j<misTutorias.size();j++){
                            if(misTutorias.get(j).getIdTutoria().equals(idReserva)){
                                System.out.println("nueva reserva");
                                System.out.println(alumno.getNombre()+" "+alumno.getDni());
                                Tutoria tutoriaNueva = misTutorias.get(j);
                                tutoriaNueva.setFecha(fecha);
                                tutoriaNueva.setNombreAlumno(alumno.getNombre());
                                tutoriasReservadas.add(tutoriaNueva);
                                System.out.println("holi "+ tutoriaNueva.getNombreAlumno());
                                System.out.println("array0 " + tutoriasReservadas.get(j).getNombreAlumno() +" "+tutoriasReservadas.get(j).getIdTutoria());
                            }
                        }
                    }
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Lista");
        for (Tutoria tuto: tutoriasReservadas){
            System.out.println(tuto.getNombreAlumno()+" - "+tuto.getIdTutoria()+"-"+tuto.getDiaSemana()+"-"+tuto.getFecha()+" "+tuto.getHoraInicio());
        }

        return tutoriasReservadas;
    }



    private UsuarioAuxiliar getDatosAlumno(String dni){
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // crear una peticion http GET.
            try {
                URL url = new URL("http://tutorizate.migadepan.es/index.php?action=datosUsuario");
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
                    String msg = jsonObject.getString("msg");
                    JSONObject usuario = jsonObject.getJSONObject("usuario");
                    String dniAlumno = usuario.getString("DNI");
                    String nombre = usuario.getString("nombre");
                    String apellidos = usuario.getString("apellidos");
                    String email = usuario.getString("email");
                    UsuarioAuxiliar usuarioAux = new UsuarioAuxiliar(dniAlumno,nombre,apellidos,email);
                    urlConnection.disconnect();
                    return usuarioAux;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
