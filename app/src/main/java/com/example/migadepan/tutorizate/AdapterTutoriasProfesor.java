package com.example.migadepan.tutorizate;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by migadepan on 05/01/2017.
 */

public class AdapterTutoriasProfesor extends BaseAdapter {
    protected Activity activity;
    protected ArrayList<Tutoria> items;

    public AdapterTutoriasProfesor(Activity activity, ArrayList<Tutoria> items) {
        this.activity = activity;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    public void clear(){
        items.clear();
    }

    public void addAll(ArrayList<Tutoria> tutorias){
        for (Tutoria tuto: tutorias){
            items.add(tuto);
        }
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.lista_tutorias_profesor, null);
        }

        Tutoria tutoria = items.get(position);
        TextView diaTutoria = (TextView) v.findViewById(R.id.miHorario_dia);
        diaTutoria.setText(tutoria.getDiaSemana());
        TextView horaInicio = (TextView) v.findViewById(R.id.miHorario_horaInicio);
        horaInicio.setText(tutoria.getHoraInicio());
        TextView horaFin = (TextView) v.findViewById(R.id.miHorario_horaFinal);
        horaFin.setText(tutoria.getHoraFinal());

        return v;
    }



}
