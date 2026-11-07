package com.uniremington.inventario.model.dao;

import java.util.List;

import com.uniremington.inventario.model.vo.DetalleCompraVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DetalleCompraDAO extends AbstractJdbcDAO {

    public DetalleCompraDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<DetalleCompraVO> listarPorCompra(int idCompra) {
        return jdbcTemplate.query("SELECT * FROM detalle_compras "
                        + "WHERE id_compra = ? ORDER BY id_detalle",
                (rs, row) -> new DetalleCompraVO(rs.getInt("id_detalle"),
                        rs.getInt("id_compra"), rs.getInt("id_producto"),
                        rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("subtotal")), idCompra);
    }

    @Transactional
    public int insertar(DetalleCompraVO detalle) {
        bloquearCompraPendiente(detalle.getIdCompra());
        int id = insertarYObtenerId("INSERT INTO detalle_compras "
                        + "(id_compra, id_producto, cantidad, precio_unitario) "
                        + "VALUES (?, ?, ?, ?)",
                detalle.getIdCompra(), detalle.getIdProducto(), detalle.getCantidad(),
                detalle.getPrecioUnitario());
        detalle.setIdDetalle(id);
        actualizarTotalCompra(detalle.getIdCompra());
        return id;
    }

    @Transactional
    public boolean actualizar(DetalleCompraVO detalle) {
        Integer compraAnterior = jdbcTemplate.query("SELECT id_compra FROM detalle_compras "
                        + "WHERE id_detalle = ? FOR UPDATE",
                (rs, row) -> rs.getInt("id_compra"), detalle.getIdDetalle())
                .stream().findFirst().orElse(null);
        if (compraAnterior == null) {
            return false;
        }

        int[] comprasAValidar = compraAnterior == detalle.getIdCompra()
                ? new int[] { compraAnterior }
                : new int[] { Math.min(compraAnterior, detalle.getIdCompra()),
                        Math.max(compraAnterior, detalle.getIdCompra()) };
        for (int idCompra : comprasAValidar) {
            bloquearCompraPendiente(idCompra);
        }

        int filas = jdbcTemplate.update("UPDATE detalle_compras SET id_compra = ?, "
                        + "id_producto = ?, cantidad = ?, precio_unitario = ? "
                        + "WHERE id_detalle = ?",
                detalle.getIdCompra(), detalle.getIdProducto(), detalle.getCantidad(),
                detalle.getPrecioUnitario(), detalle.getIdDetalle());
        if (filas > 0) {
            actualizarTotalCompra(compraAnterior);
            if (compraAnterior != detalle.getIdCompra()) {
                actualizarTotalCompra(detalle.getIdCompra());
            }
        }
        return filas > 0;
    }

    @Transactional
    public boolean eliminar(int idDetalle) {
        Integer idCompra = jdbcTemplate.query("SELECT id_compra FROM detalle_compras "
                        + "WHERE id_detalle = ? FOR UPDATE",
                (rs, row) -> rs.getInt("id_compra"), idDetalle)
                .stream().findFirst().orElse(null);
        if (idCompra == null) {
            return false;
        }
        bloquearCompraPendiente(idCompra);

        int filas = jdbcTemplate.update("DELETE FROM detalle_compras WHERE id_detalle = ?",
                idDetalle);
        if (filas > 0) {
            actualizarTotalCompra(idCompra);
        }
        return filas > 0;
    }

    private void actualizarTotalCompra(int idCompra) {
        jdbcTemplate.update("UPDATE compras SET total = (SELECT COALESCE(SUM(subtotal), 0) "
                + "FROM detalle_compras WHERE id_compra = ?) WHERE id_compra = ?",
                idCompra, idCompra);
    }

    private void bloquearCompraPendiente(int idCompra) {
        List<String> estados = jdbcTemplate.query("SELECT estado FROM compras "
                        + "WHERE id_compra = ? FOR UPDATE",
                (rs, row) -> rs.getString("estado"), idCompra);
        if (estados.isEmpty()) {
            throw new IllegalArgumentException("La compra indicada no existe.");
        }
        if (!"PENDIENTE".equals(estados.get(0))) {
            throw new IllegalStateException("Solo se pueden modificar compras pendientes.");
        }
    }
}