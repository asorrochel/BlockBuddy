package com.example.blockbuddytfg.entities;

public class Contacto {
    private String nombre, telefono, codComunidad;

    public Contacto(){}

    public Contacto(String nombre, String telefono, String codComunidad) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.codComunidad = codComunidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCodComunidad() {
        return codComunidad;
    }

    public void setCodComunidad(String codComunidad) {
        this.codComunidad = codComunidad;
    }
}
