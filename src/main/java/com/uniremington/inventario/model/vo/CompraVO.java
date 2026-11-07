package com.uniremington.inventario.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompraVO {

    private int idCompra;
    private int idProveedor;
    private int idUsuario;
    private LocalDateTime fechaCompra;
    private String numeroFactura;
    private BigDecimal total;
    private String estado;

    public CompraVO() {
    }

    public CompraVO(int idCompra, int idProveedor, int idUsuario,
            LocalDateTime fechaCompra, String numeroFactura, BigDecimal total,
            String estado) {
        this.idCompra = idCompra;
        this.idProveedor = idProveedor;
        this.idUsuario = idUsuario;
        this.fechaCompra = fechaCompra;
        this.numeroFactura = numeroFactura;
        this.total = total;
        this.estado = estado;
    }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}