package com.example.pruebatfg.modelo;

public class Restaurante {
    String direccion;
    String nombre;
    String poblacion;
    String precio;
    String tipocomida;
    boolean favorito;

    public Restaurante(){
    }

    public Restaurante(String direccion, String nombre, String poblacion, String precio, String tipocomida) {
        this.direccion = direccion;
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.precio = precio;
        this.tipocomida = tipocomida;
    }

    public String getDireccion() {
        return "Dirección: \n\t\t"+ direccion + "\n";
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return "Nombre: \n\t\t"+ nombre + "\n";
        //return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPoblacion() {
        return "Población: \n\t\t"+ poblacion + "\n";
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getPrecio() {
        return "Precio: \n\t\t"+ precio + "\n";
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTipocomida() {
        return "Tipo de Comida: \n\t\t"+ tipocomida;
    }

    public void setTipocomida(String tipocomida) {
        this.tipocomida = tipocomida;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}