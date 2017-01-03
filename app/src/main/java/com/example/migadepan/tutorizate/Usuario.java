package com.example.migadepan.tutorizate;

/**
 * Created by migadepan on 30/12/2016.
 */

public class Usuario {

    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String tipo;

    public static Usuario usuarioConectado;

    public static Usuario getUsuario (String dni, String nombre, String apellidos, String email, String password){
        if (usuarioConectado==null) {
            usuarioConectado=new Usuario(dni, nombre, apellidos, email, password);
        }
        return usuarioConectado;
    }

    public static Usuario getInstancia(){
        return usuarioConectado;
    }

    private Usuario (String dni, String nombre, String apellidos, String email, String password){
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
