package com.angelsanddemons.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angelsanddemons.model.Producto;
import com.angelsanddemons.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // ===================== LISTAR =====================
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    // ===================== BUSCAR POR ID =====================
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // ===================== GUARDAR =====================
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    // ===================== ACTUALIZAR =====================
    public Producto actualizar(Long id, Producto nuevoProducto) {

        return productoRepository.findById(id).map(p -> {

            p.setNombre(nuevoProducto.getNombre());
            p.setDescripcion(nuevoProducto.getDescripcion());
            p.setImagen(nuevoProducto.getImagen());
            p.setCategoria(nuevoProducto.getCategoria());
            p.setLicor(nuevoProducto.getLicor());

            p.setPrecioPequeno(nuevoProducto.getPrecioPequeno());
            p.setPrecioMediano(nuevoProducto.getPrecioMediano());
            p.setPrecioGrande(nuevoProducto.getPrecioGrande());

            return productoRepository.save(p);

        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // ===================== ELIMINAR =====================
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}