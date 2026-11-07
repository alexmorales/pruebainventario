package com.uniremington.inventario.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.inventario.model.vo.ProveedorVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProveedorDAO extends AbstractJdbcDAO {

    public ProveedorDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ProveedorVO> listar() {
        return jdbcTemplate.query("SELECT * FROM proveedores ORDER BY id_proveedor",
                (rs, row) -> new ProveedorVO(rs.getInt("id_proveedor"),
                        rs.getString("nombre"), rs.getString("nit"),
                        rs.getString("telefono"), rs.getString("correo"),
                        rs.getString("direccion"), rs.getBoolean("estado"),
                        fecha(rs, "fecha_creacion")));
    }

    public Optional<ProveedorVO> buscarPorId(int idProveedor) {
        return jdbcTemplate.query("SELECT * FROM proveedores WHERE id_proveedor = ?",
                (rs, row) -> new ProveedorVO(rs.getInt("id_proveedor"),
                        rs.getString("nombre"), rs.getString("nit"),
                        rs.getString("telefono"), rs.getString("correo"),
                        rs.getString("direccion"), rs.getBoolean("estado"),
                        fecha(rs, "fecha_creacion")), idProveedor)
                .stream().findFirst();
    }

    public int insertar(ProveedorVO proveedor) {
        int id = insertarYObtenerId("INSERT INTO proveedores "
                        + "(nombre, nit, telefono, correo, direccion, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                proveedor.getNombre(), proveedor.getNit(), proveedor.getTelefono(),
                proveedor.getCorreo(), proveedor.getDireccion(), proveedor.isEstado());
        proveedor.setIdProveedor(id);
        return id;
    }

    public boolean actualizar(ProveedorVO proveedor) {
        return jdbcTemplate.update("UPDATE proveedores SET nombre = ?, nit = ?, "
                        + "telefono = ?, correo = ?, direccion = ?, estado = ? "
                        + "WHERE id_proveedor = ?",
                proveedor.getNombre(), proveedor.getNit(), proveedor.getTelefono(),
                proveedor.getCorreo(), proveedor.getDireccion(), proveedor.isEstado(),
                proveedor.getIdProveedor()) > 0;
    }

    public boolean cambiarEstado(int idProveedor, boolean estado) {
        return jdbcTemplate.update("UPDATE proveedores SET estado = ? WHERE id_proveedor = ?",
                estado, idProveedor) > 0;
    }
}