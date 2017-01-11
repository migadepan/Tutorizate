package com.example.migadepan.tutorizate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
        if(misTutorias.size()!=0){
            ArrayList<Tutoria> reservadas = getTutoriasReservadas(misTutorias);

            if(reservadas.size()!=0){
                System.out.println("Lista");
                for (Tutoria tuto: reservadas){
                    getDatosAlumno(tuto);
                    System.out.println(tuto.getDniAlumno()+" "+tuto.getNombreAlumno()+" - "+tuto.getIdTutoria()+"-"+tuto.getDiaSemana()+"-"+tuto.getFecha()+" "+tuto.getHoraInicio());
                }

                //A ver si conseguimos mostrar algo en la lista
                ListView lista = (ListView) findViewById(R.id.listViewProfesor);
                AdapterItem adapter = new AdapterItem(this, reservadas);
                lista.setAdapter(adapter);
            }
        }
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

        final MenuItem searchItem = menu.findItem(R.id.action_search_p);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //permite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint(getText(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //se oculta el EditText
                searchView.setQuery("", false);
                searchView.setIconified(true);

                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio_p) {
            //Recargo el activity con la lsita de reservas para hoy
            Intent tutos_profe = new Intent(getApplicationContext(), MenuProfesorActivity.class);
            startActivity(tutos_profe);
        } else if (id == R.id.nav_horario) {
            //Mostrar las tutorias en un fragment
            FragmentManager FM = getSupportFragmentManager();
            FragmentTransaction FT = FM.beginTransaction();
            Fragment fragment = new HorarioTutoriaFragment();
            FT.replace(R.id.content_menu_profesor, fragment);
            FT.commit();
        } else if (id == R.id.nav_info_personal){

        } else if (id == R.id.nav_estado){

        } else if (id == R.id.nav_asignaturas){
            //Mostrar las tutorias en un fragment
            FragmentManager FM = getSupportFragmentManager();
            FragmentTransaction FT = FM.beginTransaction();
            Fragment fragment = new HorarioTutoriaFragment();
            FT.replace(R.id.fragment_asignaturas_profesor, fragment);
            FT.commit();
        } else if (id == R.id.nav_cerrar_sesion){
            Intent actividad_logout = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(actividad_logout);
            finish();
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
                if (estado.equals("correcto")) {
                    JSONArray tutorias = jsonObject.getJSONArray("tutorias");
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
        ArrayList<Tutoria> lasReservadas = new ArrayList<Tutoria>();
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
                if (estado.equals("correcto")) {
                    JSONArray reservas = jsonObject.getJSONArray("reservas");
                    for (int i=0; i<reservas.length();i++){
                        JSONObject reserva = reservas.getJSONObject(i);
                        String idReserva = reserva.getString("idTutoria");
                        String dniAlumno = reserva.getString("dniAlumno");
                        String fecha = reserva.getString("fecha");

                        for (Tutoria tutori: misTutorias){
                            if(tutori.getIdTutoria().equals(idReserva)){
                                Tutoria nueva = new Tutoria(tutori.getIdTutoria(),tutori.getNombreAlumno(),tutori.getFecha());
                                nueva.setHoraInicio(tutori.getHoraInicio());
                                nueva.setHoraFinal(tutori.getHoraFinal());
                                nueva.setDiaSemana(tutori.getDiaSemana());
                                nueva.setDniAlumno(dniAlumno);
                                System.out.println("Dni alumno " +dniAlumno);
                                nueva.setFecha(fecha);
                                lasReservadas.add(nueva);
                            }
                        }
                    }
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lasReservadas;
    }



    private void getDatosAlumno(Tutoria tutoria){
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String dni = tutoria.getDniAlumno();
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
                    tutoria.setNombreAlumno(nombre+" "+apellidos);
                    tutoria.setMailAlumno(email);
                    urlConnection.disconnect();

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
