package com.example.blockbuddytfg.entities;

import java.util.ArrayList;

public class Comunidad {
    private String nombre, direccion, codigoComunidad, viviendas, codigoPostal,administrador;
    private ArrayList<String> incidencias, contactos;


    public Comunidad(){}

    public Comunidad(String nombre, String direccion, String codigoComunidad, String viviendas, String codigoPostal, String administrador,ArrayList<String> incidencias, ArrayList<String> Contactos) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.codigoComunidad = codigoComunidad;
        this.viviendas = viviendas;
        this.codigoPostal = codigoPostal;
        this.administrador = administrador;
        this.incidencias = incidencias;
        this.contactos = Contactos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoComunidad() {
        return codigoComunidad;
    }

    public void setCodigoComunidad(String codigoComunidad) {
        this.codigoComunidad = codigoComunidad;
    }

    public String getViviendas() {
        return viviendas;
    }

    public void setViviendas(String viviendas) {
        this.viviendas = viviendas;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getAdministrador() {
        return administrador;
    }

    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }

    public ArrayList<String> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(ArrayList<String> incidencias) {
        this.incidencias = incidencias;
    }

    public ArrayList<String> getContactos() {
        return contactos;
    }

    public void setContactos(ArrayList<String> contactos) {
        this.contactos = contactos;
    }
}
