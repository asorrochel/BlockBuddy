package com.example.blockbuddytfg.entities;

import java.io.Serializable;

public class Reunion implements Serializable {
    private String descripcion, fecha, codComunidad;

    public Reunion(){}

    public Reunion(String descripcion, String fecha, String codComunidad) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.codComunidad = codComunidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCodComunidad() {
        return codComunidad;
    }

    public void setCodComunidad(String codComunidad) {
        this.codComunidad = codComunidad;
    }
}
