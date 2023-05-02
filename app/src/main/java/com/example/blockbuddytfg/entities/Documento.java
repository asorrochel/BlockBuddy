package com.example.blockbuddytfg.entities;

public class Documento {

    private String codComunidad, titulo, descripcion, fecha, url;

    public Documento() {
    }

    public Documento(String codComunidad, String titulo, String descripcion, String fecha, String url) {
        this.codComunidad = codComunidad;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
