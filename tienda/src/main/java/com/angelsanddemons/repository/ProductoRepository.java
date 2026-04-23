package com.angelsanddemons.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angelsanddemons.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}