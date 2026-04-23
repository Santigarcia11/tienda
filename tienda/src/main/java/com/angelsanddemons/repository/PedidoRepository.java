package com.angelsanddemons.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angelsanddemons.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}