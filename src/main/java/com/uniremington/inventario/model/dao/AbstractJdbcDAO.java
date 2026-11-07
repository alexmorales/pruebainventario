package com.uniremington.inventario.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

abstract class AbstractJdbcDAO {

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractJdbcDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected int insertarYObtenerId(String sql, Object... valores) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < valores.length; i++) {
                statement.setObject(i + 1, valores[i]);
            }
            return statement;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id == null) {
            throw new IllegalStateException("La base de datos no devolvio el ID generado.");
        }
        return id.intValue();
    }

    protected static LocalDateTime fecha(ResultSet resultSet, String columna)
            throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columna);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    protected static Integer enteroNullable(ResultSet resultSet, String columna)
            throws SQLException {
        return resultSet.getObject(columna, Integer.class);
    }
}