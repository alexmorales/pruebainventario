package com.uniremington.inventario.model.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uniremington.inventario.model.vo.CompraVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CompraDAO extends AbstractJdbcDAO {

    public CompraDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CompraVO> listar() {
        return jdbcTemplate.query("SELECT * FROM compras ORDER BY fecha_compra DESC",
                (rs, row) -> new CompraVO(rs.getInt("id_compra"),
                        rs.getInt("id_proveedor"), rs.getInt("id_usuario"),
                        fecha(rs, "fecha_compra"), rs.getString("numero_factura"),
                        rs.getBigDecimal("total"), rs.getString("estado")));
    }

    public Optional<CompraVO> buscarPorId(int idCompra) {
        return jdbcTemplate.query("SELECT * FROM compras WHERE id_compra = ?",
                (rs, row) -> new CompraVO(rs.getInt("id_compra"),
                        rs.getInt("id_proveedor"), rs.getInt("id_usuario"),
                        fecha(rs, "fecha_compra"), rs.getString("numero_factura"),
                        rs.getBigDecimal("total"), rs.getString("estado")), idCompra)
                .stream().findFirst();
    }

    public int insertar(CompraVO compra) {
        BigDecimal total = compra.getTotal() == null ? BigDecimal.ZERO : compra.getTotal();
        String estado = compra.getEstado() == null ? "PENDIENTE" : compra.getEstado();
        int id = insertarYObtenerId("INSERT INTO compras "
                        + "(id_proveedor, id_usuario, numero_factura, total, estado) "
                        + "VALUES (?, ?, ?, ?, ?)",
                compra.getIdProveedor(), compra.getIdUsuario(), compra.getNumeroFactura(),
                total, estado);
        compra.setIdCompra(id);
        compra.setTotal(total);
        compra.setEstado(estado);
        return id;
    }

    public boolean actualizarFactura(CompraVO compra) {
        return jdbcTemplate.update("UPDATE compras SET id_proveedor = ?, "
                        + "numero_factura = ? WHERE id_compra = ? AND estado = 'PENDIENTE'",
                compra.getIdProveedor(), compra.getNumeroFactura(), compra.getIdCompra()) > 0;
    }

    public boolean anularPendiente(int idCompra) {
        return jdbcTemplate.update("UPDATE compras SET estado = 'ANULADA' "
                + "WHERE id_compra = ? AND estado = 'PENDIENTE'", idCompra) > 0;
    }

        @Transactional
        public boolean recibir(int idCompra) {
        List<Integer> usuarios = jdbcTemplate.query("SELECT id_usuario FROM compras "
                        + "WHERE id_compra = ? AND estado = 'PENDIENTE' FOR UPDATE",
                (rs, row) -> rs.getInt("id_usuario"), idCompra);
        if (usuarios.isEmpty()) {
            return false;
        }

        int idUsuario = usuarios.get(0);
        List<DetalleRecepcion> detalles = jdbcTemplate.query(
                "SELECT id_producto, cantidad FROM detalle_compras "
                        + "WHERE id_compra = ? ORDER BY id_producto",
                (rs, row) -> new DetalleRecepcion(
                        rs.getInt("id_producto"), rs.getInt("cantidad")), idCompra);
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede recibir una compra sin detalles.");
        }

        for (DetalleRecepcion detalle : detalles) {
            Integer stockAnterior = jdbcTemplate.queryForObject(
                    "SELECT stock FROM productos WHERE id_producto = ? FOR UPDATE",
                    Integer.class, detalle.idProducto());
            if (stockAnterior == null) {
                throw new IllegalStateException(
                        "No se pudo consultar el stock del producto.");
            }

            int stockNuevo = Math.addExact(stockAnterior, detalle.cantidad());
            jdbcTemplate.update("UPDATE productos SET stock = ? WHERE id_producto = ?",
                    stockNuevo, detalle.idProducto());
            insertarYObtenerId("INSERT INTO movimientos_inventario "
                            + "(id_producto, id_usuario, tipo_movimiento, cantidad, "
                            + "stock_anterior, stock_nuevo, motivo) "
                            + "VALUES (?, ?, 'ENTRADA', ?, ?, ?, ?)",
                    detalle.idProducto(), idUsuario, detalle.cantidad(),
                    stockAnterior, stockNuevo, "Recepcion de compra #" + idCompra);
        }

        return jdbcTemplate.update("UPDATE compras SET estado = 'RECIBIDA' "
                        + "WHERE id_compra = ? AND estado = 'PENDIENTE'", idCompra) > 0;
    }

    private record DetalleRecepcion(int idProducto, int cantidad) {
    }
}