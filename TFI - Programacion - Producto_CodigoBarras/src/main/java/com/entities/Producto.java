package com.entities;

public class Producto {

    private Long id;
    private Boolean eliminado = false;
    private String nombre;
    private String marca;
    private String categoria;
    private double precio;
    private Double peso; 
    private CodigoBarras codigoBarras; 

    public Producto() {
    }

    public Producto(String nombre, String marca, String categoria, double precio, Double peso) {
        this.id = null; 
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.peso = peso;
        this.codigoBarras = null; 
    }

    public Producto(Long id, String nombre, String marca, String categoria, double precio, Double peso, CodigoBarras codigoBarras) {
        this(nombre, marca, categoria, precio, peso);
        this.id = id;
        this.codigoBarras = codigoBarras;
        
    }

    public Producto(String nombre, String marca, String categoria, double precio, CodigoBarras codigoBarras, Double peso) {
        this(nombre, marca, categoria, precio, peso);
        this.id = null;
        this.codigoBarras = codigoBarras;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public CodigoBarras getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(CodigoBarras codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", eliminado=" + eliminado +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", peso=" + peso +
                ", codigoBarrasId=" + (codigoBarras != null ? codigoBarras.getId() : "null") +
                '}';
    }
}