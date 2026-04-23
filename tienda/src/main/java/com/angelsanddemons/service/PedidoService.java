package com.angelsanddemons.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angelsanddemons.model.Pedido;
import com.angelsanddemons.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repo;

    // 📦 LISTAR TODOS LOS PEDIDOS
    public List<Pedido> listar() {
        return repo.findAll();
    }

    // 💾 GUARDAR O ACTUALIZAR PEDIDO
    public Pedido guardar(Pedido pedido) {
        return repo.save(pedido);
    }

    // 🔎 BUSCAR POR ID
    public Pedido buscarPorId(Long id) {
        Optional<Pedido> pedido = repo.findById(id);
        return pedido.orElse(null);
    }

    // ❌ ELIMINAR PEDIDO (opcional útil admin)
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}