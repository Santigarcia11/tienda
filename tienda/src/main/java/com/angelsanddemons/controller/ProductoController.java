package com.angelsanddemons.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.angelsanddemons.model.Producto;
import com.angelsanddemons.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ===================== LISTAR (INDEX) =====================
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "index";
    }

    // ===================== FORM CREAR =====================
    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/form-producto";
    }

    // ===================== GUARDAR =====================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    // ===================== EDITAR =====================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Producto producto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        model.addAttribute("producto", producto);

        return "admin/form-producto";
    }

    // ===================== ACTUALIZAR =====================
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Producto producto) {

        productoService.actualizar(id, producto);

        return "redirect:/productos";
    }

    // ===================== ELIMINAR =====================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}