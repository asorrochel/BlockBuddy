package com.example.blockbuddytfg.entities;

public class Usuario {
    private String nombre, correo, contraseña,puerta,codComunidad, telefono, piso, categoria, imagen;

    public Usuario(){}
    public Usuario(String nombre, String correo, String telefono , String puerta, String codComunidad, String contraseña, String piso, String categoria, String imagen) {
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.puerta = puerta;
        this.codComunidad = codComunidad;
        this.telefono = telefono;
        this.piso = piso;
        this.categoria = categoria;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getPuerta() {
        return puerta;
    }
    public void setPuerta(String puerta) {
        this.puerta = puerta;
    }

    public String getCodComunidad() {
        return codComunidad;
    }
    public void setCodComunidad(String codComunidad) {
        this.codComunidad = codComunidad;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPiso() {
        return piso;
    }
    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
