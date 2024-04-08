package com.example.beertracker.models;

public class Cerveza {
    private int id;
    private String nombre;
    private String pais;
    private String tipo;
    private String marca;
    private double precio;
    private double graduacion;

    public Cerveza(){
    }

    public Cerveza(int id) {
        this.id = id;
    }

    public Cerveza(int id, String nombre, String pais, String tipo, String marca, double precio, double graduacion) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.tipo = tipo;
        this.marca = marca;
        this.precio = precio;
        this.graduacion = graduacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getGraduacion(){return graduacion;}

    public void setGraduacion(double graduacion){
        this.graduacion = graduacion;
    }
}
