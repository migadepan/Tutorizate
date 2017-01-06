package com.example.migadepan.tutorizate;

/**
 * Created by migadepan on 05/01/2017.
 */

public class Tutoria {
    private String idTutoria;
    private String dniProfesor;
    private String nombreAlumno;
    private String fecha;
    private String horaInicio;
    private String horaFinal;
    private String diaSemana;

    public Tutoria(String idTutoria, String dniProfesor, String horaInicio, String horaFinal, String diaSemana) {
        this.idTutoria = idTutoria;
        this.dniProfesor = dniProfesor;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.diaSemana = diaSemana;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(String idTutoria) {
        this.idTutoria = idTutoria;
    }

    public String getDniProfesor() {
        return dniProfesor;
    }

    public void setDniProfesor(String dniProfesor) {
        this.dniProfesor = dniProfesor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }
}
