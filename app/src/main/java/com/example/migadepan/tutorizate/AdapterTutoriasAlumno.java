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

public class AdapterTutoriasAlumno extends BaseAdapter {
    protected Activity activity;
    protected ArrayList<Tutoria> items;

    public AdapterTutoriasAlumno(Activity activity, ArrayList<Tutoria> items) {
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
            v = inf.inflate(R.layout.lista_tutorias_alumno, null);
        }

        Tutoria tutoria = items.get(position);
        System.out.println(tutoria.getNombreProfesor());
        TextView nombreProfesor = (TextView) v.findViewById(R.id.nombreDelProfesor);
        nombreProfesor.setText(tutoria.getNombreProfesor());
        TextView horaInicio = (TextView) v.findViewById(R.id.horaInicio);
        horaInicio.setText(tutoria.getHoraInicio());
        TextView horaFin = (TextView) v.findViewById(R.id.horaFinal);
        horaFin.setText(tutoria.getHoraFinal());
        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        fecha.setText(tutoria.getFecha());

        return v;
    }



}
