package com.uniremington.inventario.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.inventario.model.vo.ProductoVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoDAO extends AbstractJdbcDAO {

    public ProductoDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ProductoVO> listar() {
        return jdbcTemplate.query("SELECT * FROM productos ORDER BY nombre",
                (rs, row) -> mapearProducto(rs));
    }

    public List<ProductoVO> listarActivos() {
        return jdbcTemplate.query("SELECT * FROM productos WHERE estado = TRUE ORDER BY nombre",
                (rs, row) -> mapearProducto(rs));
    }

    public List<ProductoVO> listarBajoStock() {
        return jdbcTemplate.query("SELECT * FROM productos "
                        + "WHERE estado = TRUE AND stock <= stock_minimo ORDER BY nombre",
                (rs, row) -> mapearProducto(rs));
    }

    public Optional<ProductoVO> buscarPorId(int idProducto) {
        return jdbcTemplate.query("SELECT * FROM productos WHERE id_producto = ?",
                (rs, row) -> mapearProducto(rs), idProducto).stream().findFirst();
    }

    public Optional<ProductoVO> buscarPorCodigo(String codigo) {
        return jdbcTemplate.query("SELECT * FROM productos WHERE codigo = ?",
                (rs, row) -> mapearProducto(rs), codigo).stream().findFirst();
    }

    public int insertar(ProductoVO producto) {
        int id = insertarYObtenerId("INSERT INTO productos "
                        + "(codigo, nombre, descripcion, id_categoria, id_proveedor, "
                        + "precio_compra, precio_venta, stock, stock_minimo, unidad_medida, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                producto.getCodigo(), producto.getNombre(), producto.getDescripcion(),
                producto.getIdCategoria(), producto.getIdProveedor(),
                producto.getPrecioCompra(), producto.getPrecioVenta(), producto.getStock(),
                producto.getStockMinimo(), producto.getUnidadMedida(), producto.isEstado());
        producto.setIdProducto(id);
        return id;
    }

    public boolean actualizar(ProductoVO producto) {
        return jdbcTemplate.update("UPDATE productos SET codigo = ?, nombre = ?, "
                        + "descripcion = ?, id_categoria = ?, id_proveedor = ?, "
                        + "precio_compra = ?, precio_venta = ?, stock_minimo = ?, "
                        + "unidad_medida = ?, estado = ? WHERE id_producto = ?",
                producto.getCodigo(), producto.getNombre(), producto.getDescripcion(),
                producto.getIdCategoria(), producto.getIdProveedor(),
                producto.getPrecioCompra(), producto.getPrecioVenta(),
                producto.getStockMinimo(), producto.getUnidadMedida(), producto.isEstado(),
                producto.getIdProducto()) > 0;
    }

    public boolean cambiarEstado(int idProducto, boolean estado) {
        return jdbcTemplate.update("UPDATE productos SET estado = ? WHERE id_producto = ?",
                estado, idProducto) > 0;
    }

    private ProductoVO mapearProducto(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new ProductoVO(rs.getInt("id_producto"), rs.getString("codigo"),
                rs.getString("nombre"), rs.getString("descripcion"),
                enteroNullable(rs, "id_categoria"), enteroNullable(rs, "id_proveedor"),
                rs.getBigDecimal("precio_compra"), rs.getBigDecimal("precio_venta"),
                rs.getInt("stock"), rs.getInt("stock_minimo"),
                rs.getString("unidad_medida"), rs.getBoolean("estado"),
                fecha(rs, "fecha_creacion"));
    }
}