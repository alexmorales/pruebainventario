package com.uniremington.inventario.controller;

import com.uniremington.inventario.model.dao.CategoriaDAO;
import com.uniremington.inventario.model.dao.CompraDAO;
import com.uniremington.inventario.model.dao.MovimientoInventarioDAO;
import com.uniremington.inventario.model.dao.ProductoDAO;
import com.uniremington.inventario.model.vo.CompraVO;
import com.uniremington.inventario.model.vo.ProductoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InventarioWebControllerTest {

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;
    private MovimientoInventarioDAO movimientoDAO;
    private CompraDAO compraDAO;
    private Model model;
    private InventarioWebController controller;

    @BeforeEach
    void setUp() {
        productoDAO = mock(ProductoDAO.class);
        categoriaDAO = mock(CategoriaDAO.class);
        movimientoDAO = mock(MovimientoInventarioDAO.class);
        compraDAO = mock(CompraDAO.class);
        model = mock(Model.class);

        controller = new InventarioWebController(productoDAO, categoriaDAO, movimientoDAO, compraDAO);
    }

    @Test
    void testInicio() {
        // Arrange
        List<ProductoVO> productosActivos = Arrays.asList(new ProductoVO(), new ProductoVO());
        List<CompraVO> compras = Arrays.asList(
                new CompraVO("PENDIENTE"),
                new CompraVO("COMPLETADO"));
        List<Object> movimientos = Collections.singletonList(new Object());

        when(productoDAO.listarActivos()).thenReturn(productosActivos);
        when(productoDAO.listarBajoStock()).thenReturn(Collections.emptyList());
        when(categoriaDAO.listar()).thenReturn(Collections.singletonList(new Object()));
        when(compraDAO.listar()).thenReturn(compras);
        when(movimientoDAO.listar()).thenReturn(movimientos);

        // Act
        String viewName = controller.inicio(model);

        // Assert
        assertEquals("inicio", viewName);
        verify(model).addAttribute("totalProductos", productosActivos.size());
        verify(model).addAttribute("bajoStock", Collections.emptyList());
        verify(model).addAttribute("totalCategorias", 1);
        verify(model).addAttribute("comprasPendientes", 1L);
        verify(model).addAttribute("ultimosMovimientos", movimientos);
    }
}
