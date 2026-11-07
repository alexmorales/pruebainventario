package com.uniremington.inventario.controller;

import java.util.List;

import com.uniremington.inventario.model.dao.CategoriaDAO;
import com.uniremington.inventario.model.dao.CompraDAO;
import com.uniremington.inventario.model.dao.MovimientoInventarioDAO;
import com.uniremington.inventario.model.dao.ProductoDAO;
import com.uniremington.inventario.model.vo.CompraVO;
import com.uniremington.inventario.model.vo.ProductoVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventarioWebController {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;
    private final MovimientoInventarioDAO movimientoDAO;
    private final CompraDAO compraDAO;

    public InventarioWebController(ProductoDAO productoDAO,
            CategoriaDAO categoriaDAO, MovimientoInventarioDAO movimientoDAO,
            CompraDAO compraDAO) {
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
        this.movimientoDAO = movimientoDAO;
        this.compraDAO = compraDAO;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<ProductoVO> productosActivos = productoDAO.listarActivos();
        List<CompraVO> compras = compraDAO.listar();

        model.addAttribute("totalProductos", productosActivos.size());
        model.addAttribute("bajoStock", productoDAO.listarBajoStock());
        model.addAttribute("totalCategorias", categoriaDAO.listar().size());
        model.addAttribute("comprasPendientes", compras.stream()
                .filter(compra -> "PENDIENTE".equals(compra.getEstado())).count());
        model.addAttribute("ultimosMovimientos", movimientoDAO.listar().stream()
                .limit(6).toList());
        return "inicio";
    }
}