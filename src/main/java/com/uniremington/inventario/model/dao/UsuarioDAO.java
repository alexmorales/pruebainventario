package com.uniremington.inventario.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.uniremington.inventario.model.vo.UsuarioVO;

@Repository
public class UsuarioDAO extends AbstractJdbcDAO {

    public UsuarioDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<UsuarioVO> listar() {
        return jdbcTemplate.query("SELECT * FROM usuarios ORDER BY id_usuario",
                (rs, row) -> new UsuarioVO(rs.getInt("id_usuario"),
                        rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("correo"), rs.getString("contrasena"),
                        rs.getString("rol"), rs.getBoolean("estado"),
                        fecha(rs, "fecha_creacion")));
    }

    public Optional<UsuarioVO> buscarPorId(int idUsuario) {
        return jdbcTemplate.query("SELECT * FROM usuarios WHERE id_usuario = ?",
                (rs, row) -> new UsuarioVO(rs.getInt("id_usuario"),
                        rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("correo"), rs.getString("contrasena"),
                        rs.getString("rol"), rs.getBoolean("estado"),
                        fecha(rs, "fecha_creacion")), idUsuario)
                .stream().findFirst();
    }

    public Optional<UsuarioVO> buscarPorCorreo(String correo) {
        return jdbcTemplate.query("SELECT * FROM usuarios WHERE correo = ?",
                (rs, row) -> new UsuarioVO(rs.getInt("id_usuario"),
                        rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("correo"), rs.getString("contrasena"),
                        rs.getString("rol"), rs.getBoolean("estado"),
                        fecha(rs, "fecha_creacion")), correo)
                .stream().findFirst();
    }

    public int insertar(UsuarioVO usuario) {
        int id = insertarYObtenerId(
                "INSERT INTO usuarios (nombre, apellido, correo, contrasena, rol, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                usuario.getNombre(), usuario.getApellido(), usuario.getCorreo(),
                usuario.getContrasena(), usuario.getRol(), usuario.isEstado());
        usuario.setIdUsuario(id);
        return id;
    }

    public boolean actualizar(UsuarioVO usuario) {
        return jdbcTemplate.update("UPDATE usuarios SET nombre = ?, apellido = ?, "
                        + "correo = ?, contrasena = ?, rol = ?, estado = ? "
                        + "WHERE id_usuario = ?",
                usuario.getNombre(), usuario.getApellido(), usuario.getCorreo(),
                usuario.getContrasena(), usuario.getRol(), usuario.isEstado(),
                usuario.getIdUsuario()) > 0;
    }

    public boolean cambiarEstado(int idUsuario, boolean estado) {
        return jdbcTemplate.update("UPDATE usuarios SET estado = ? WHERE id_usuario = ?",
                estado, idUsuario) > 0;
    }
}