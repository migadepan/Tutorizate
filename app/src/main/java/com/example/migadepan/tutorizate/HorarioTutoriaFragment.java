package com.example.migadepan.tutorizate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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
import java.util.ArrayList;


public class HorarioTutoriaFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_horario_tutoria, container, false);

        Spinner diasSemana = (Spinner) rootView.findViewById(R.id.dias_semana);
        ArrayList<String> losValores = new ArrayList<>();
        losValores.add("Lunes");
        losValores.add("Martes");
        losValores.add("Miercoles");
        losValores.add("Jueves");
        losValores.add("Viernes");
        String[] valoresIdioma = new String[losValores.size()];

        for (int i=0; i<losValores.size();i++){
            valoresIdioma[i] = losValores.get(i);
        }

        diasSemana.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, valoresIdioma));

        //Obtengo mi dni
        Usuario usuarioConectado = Usuario.getInstancia();
        String dni = usuarioConectado.getDni();
        //Obtengo mis tutorias
        ArrayList<Tutoria> misTutorias = getTutorias(dni);
        if (misTutorias.size()==0){
            Tutoria nueva = new Tutoria("0","Sin horario","0","0","Ninguna");
            misTutorias.add(nueva);
        }

        //A ver si conseguimos mostrar algo en la lista
        ListView lista = (ListView) rootView.findViewById(R.id.listView_misTutorias);
        AdapterTutoriasProfesor adapter = new AdapterTutoriasProfesor(getActivity(), misTutorias);
        lista.setAdapter(adapter);

        return rootView;
    }

    private ArrayList<Tutoria> getTutorias(String dni){
        ArrayList<Tutoria> misTutorias = new ArrayList();
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
