package com.uniremington.inventario.model.dao;

import java.util.List;

import com.uniremington.inventario.model.vo.MovimientoInventarioVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MovimientoInventarioDAO extends AbstractJdbcDAO {

    public MovimientoInventarioDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<MovimientoInventarioVO> listar() {
        return jdbcTemplate.query("SELECT * FROM movimientos_inventario "
                        + "ORDER BY fecha_movimiento DESC, id_movimiento DESC",
                (rs, row) -> new MovimientoInventarioVO(rs.getInt("id_movimiento"),
                        rs.getInt("id_producto"), rs.getInt("id_usuario"),
                        rs.getString("tipo_movimiento"), rs.getInt("cantidad"),
                        rs.getInt("stock_anterior"), rs.getInt("stock_nuevo"),
                        rs.getString("motivo"), fecha(rs, "fecha_movimiento")));
    }

    public List<MovimientoInventarioVO> listarPorProducto(int idProducto) {
        return jdbcTemplate.query("SELECT * FROM movimientos_inventario "
                        + "WHERE id_producto = ? ORDER BY fecha_movimiento DESC",
                (rs, row) -> new MovimientoInventarioVO(rs.getInt("id_movimiento"),
                        rs.getInt("id_producto"), rs.getInt("id_usuario"),
                        rs.getString("tipo_movimiento"), rs.getInt("cantidad"),
                        rs.getInt("stock_anterior"), rs.getInt("stock_nuevo"),
                        rs.getString("motivo"), fecha(rs, "fecha_movimiento")), idProducto);
    }

    @Transactional
    public int registrar(MovimientoInventarioVO movimiento) {
        Integer stockActual = jdbcTemplate.queryForObject(
                "SELECT stock FROM productos WHERE id_producto = ? FOR UPDATE",
                Integer.class, movimiento.getIdProducto());
        if (stockActual == null) {
            throw new IllegalArgumentException("El producto no tiene un stock valido.");
        }

        int stockNuevo;
        int cantidad;
        switch (movimiento.getTipoMovimiento()) {
            case "ENTRADA" -> {
                cantidad = movimiento.getCantidad();
                stockNuevo = stockActual + cantidad;
            }
            case "SALIDA" -> {
                cantidad = movimiento.getCantidad();
                stockNuevo = stockActual - cantidad;
            }
            case "AJUSTE" -> {
                stockNuevo = movimiento.getStockNuevo();
                cantidad = Math.abs(stockNuevo - stockActual);
            }
            default -> throw new IllegalArgumentException("Tipo de movimiento no valido.");
        }

        if (cantidad <= 0 || stockNuevo < 0) {
            throw new IllegalArgumentException("La cantidad o el stock resultante no son validos.");
        }

        jdbcTemplate.update("UPDATE productos SET stock = ? WHERE id_producto = ?",
                stockNuevo, movimiento.getIdProducto());
        int id = insertarYObtenerId("INSERT INTO movimientos_inventario "
                        + "(id_producto, id_usuario, tipo_movimiento, cantidad, "
                        + "stock_anterior, stock_nuevo, motivo) VALUES (?, ?, ?, ?, ?, ?, ?)",
                movimiento.getIdProducto(), movimiento.getIdUsuario(),
                movimiento.getTipoMovimiento(), cantidad, stockActual, stockNuevo,
                movimiento.getMotivo());

        movimiento.setIdMovimiento(id);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockActual);
        movimiento.setStockNuevo(stockNuevo);
        return id;
    }
}