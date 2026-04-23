package com.angelsanddemons.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private String imagen;

    private String categoria;

    private String licor;

    // precios por tamaño
    private Double precioPequeno;
    private Double precioMediano;
    private Double precioGrande;

    public Producto() {
    }

    // ================= GETTERS =================

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getLicor() {
        return licor;
    }

    public Double getPrecioPequeno() {
        return precioPequeno;
    }

    public Double getPrecioMediano() {
        return precioMediano;
    }

    public Double getPrecioGrande() {
        return precioGrande;
    }

    // ================= SETTERS =================

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setLicor(String licor) {
        this.licor = licor;
    }

    public void setPrecioPequeno(Double precioPequeno) {
        this.precioPequeno = precioPequeno;
    }

    public void setPrecioMediano(Double precioMediano) {
        this.precioMediano = precioMediano;
    }

    public void setPrecioGrande(Double precioGrande) {
        this.precioGrande = precioGrande;
    }
}
