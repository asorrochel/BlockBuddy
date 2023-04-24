package com.example.blockbuddytfg.entities;

public class Incidencia {
    private String nombre, descripcion, fecha, imagen,usuario,codComunidad,estado, usuarioNombre, cod_validada, cod_estado, cod_validada_estado;
    private Boolean validada;

    public Incidencia(){}

    public Incidencia(String nombre, String descripcion, String fecha, String imagen, String usuario,String usuarioNombre, String codComunidad,String estado,Boolean validada, String cod_validada, String cod_estado, String cod_validada_estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.imagen = imagen;
        this.usuario = usuario;
        this.codComunidad = codComunidad;
        this.estado = estado;
        this.usuarioNombre = usuarioNombre;
        this.validada = validada;
        this.cod_validada = cod_validada;
        this.cod_estado = cod_estado;
        this.cod_validada_estado = cod_validada_estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCodComunidad() {
        return codComunidad;
    }
    public void setCodComunidad(String codComunidad) {
        this.codComunidad = codComunidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public Boolean getValidada() {
        return validada;
    }

    public void setValidada(Boolean validada) {
        this.validada = validada;
    }

    public String getCod_validada() {
        return cod_validada;
    }

    public void setCod_validada(String cod_validada) {
        this.cod_validada = cod_validada;
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getCod_validada_estado() {
        return cod_validada_estado;
    }

    public void setCod_validada_estado(String cod_validada_estado) {
        this.cod_validada_estado = cod_validada_estado;
    }
}
