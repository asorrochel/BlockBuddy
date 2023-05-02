package com.example.blockbuddytfg.entities;

public class Anuncio {

    private String codComunidad, titulo, descripcion, fecha;

    public Anuncio() {
    }

    public Anuncio(String codComunidad, String titulo, String descripcion, String fecha) {
        this.codComunidad = codComunidad;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getCodComunidad() {
        return codComunidad;
    }

    public void setCodComunidad(String codComunidad) {
        this.codComunidad = codComunidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
}
