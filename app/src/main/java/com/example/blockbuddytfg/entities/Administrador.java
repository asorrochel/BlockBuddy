package com.example.blockbuddytfg.entities;


import java.util.ArrayList;


public class Administrador {
    private String nombre, telefono,imagen;
    private ArrayList<String> comunidades;

    public Administrador(){}
    public Administrador(String nombre, String telefono , String imagen, ArrayList<String> comunidades) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.imagen = imagen;
        this.comunidades = comunidades;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setComunidades(ArrayList<String> comunidades) {
        this.comunidades = comunidades;
    }
    public ArrayList<String> getComunidades() {
        return comunidades;
    }
}