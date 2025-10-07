//CodigoBarras.java
package com.entities;

import java.time.LocalDate;

public class CodigoBarras {

    private Long id;
    private Boolean eliminado = false; // Valor por defecto para nuevos objetos
    private TipoCodigoBarras tipo;
    private String valor;
    private LocalDate fechaAsignacion;
    private String observaciones;

    // Constructor vacío
    public CodigoBarras() {
    }

    // Constructor completo
    public CodigoBarras(Long id, TipoCodigoBarras tipo, String valor, LocalDate fechaAsignacion, String observaciones) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.fechaAsignacion = fechaAsignacion;
        this.observaciones = observaciones;
    }

    // Getters y Setters
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

    public TipoCodigoBarras getTipo() {
        return tipo;
    }

    public void setTipo(TipoCodigoBarras tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "CodigoBarras{" +
                "id=" + id +
                ", eliminado=" + eliminado +
                ", tipo=" + tipo +
                ", valor='" + valor + '\'' +
                ", fechaAsignacion=" + fechaAsignacion +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}