package com.uniremington.inventario.model.vo;

import java.time.LocalDateTime;

public class ProveedorVO {

    private int idProveedor;
    private String nombre;
    private String nit;
    private String telefono;
    private String correo;
    private String direccion;
    private boolean estado;
    private LocalDateTime fechaCreacion;

    public ProveedorVO() {
    }

    public ProveedorVO(int idProveedor, String nombre, String nit,
            String telefono, String correo, String direccion, boolean estado,
            LocalDateTime fechaCreacion) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}