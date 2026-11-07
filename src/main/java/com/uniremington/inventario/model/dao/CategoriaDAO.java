package com.uniremington.inventario.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.inventario.model.vo.CategoriaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriaDAO extends AbstractJdbcDAO {

    public CategoriaDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CategoriaVO> listar() {
        return jdbcTemplate.query("SELECT * FROM categorias ORDER BY id_categoria",
                (rs, row) -> new CategoriaVO(rs.getInt("id_categoria"),
                        rs.getString("nombre"), rs.getString("descripcion"),
                        rs.getBoolean("estado")));
    }

    public Optional<CategoriaVO> buscarPorId(int idCategoria) {
        return jdbcTemplate.query("SELECT * FROM categorias WHERE id_categoria = ?",
                (rs, row) -> new CategoriaVO(rs.getInt("id_categoria"),
                        rs.getString("nombre"), rs.getString("descripcion"),
                        rs.getBoolean("estado")), idCategoria)
                .stream().findFirst();
    }

    public int insertar(CategoriaVO categoria) {
        int id = insertarYObtenerId(
                "INSERT INTO categorias (nombre, descripcion, estado) VALUES (?, ?, ?)",
                categoria.getNombre(), categoria.getDescripcion(), categoria.isEstado());
        categoria.setIdCategoria(id);
        return id;
    }

    public boolean actualizar(CategoriaVO categoria) {
        return jdbcTemplate.update("UPDATE categorias SET nombre = ?, descripcion = ?, "
                        + "estado = ? WHERE id_categoria = ?",
                categoria.getNombre(), categoria.getDescripcion(), categoria.isEstado(),
                categoria.getIdCategoria()) > 0;
    }

    public boolean cambiarEstado(int idCategoria, boolean estado) {
        return jdbcTemplate.update("UPDATE categorias SET estado = ? WHERE id_categoria = ?",
                estado, idCategoria) > 0;
    }
}