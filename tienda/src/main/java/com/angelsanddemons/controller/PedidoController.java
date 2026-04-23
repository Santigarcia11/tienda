package com.angelsanddemons.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.angelsanddemons.model.CarritoItem;
import com.angelsanddemons.model.Pedido;
import com.angelsanddemons.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    private static final String NUMERO_WHATSAPP = "573108941971"; // Colombia
    private static final String NOMBRE_NEGOCIO = "Angels & Demons";

    // ===================== IR A CHECKOUT (DESDE CARRITO) =====================
    @PostMapping("/checkout")
    public String checkout(@RequestParam String cliente,
                          @RequestParam String direccion,
                          @RequestParam String celular,
                          HttpSession session,
                          Model model) {

        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }

        // Calcular total
        double total = carrito.stream()
            .mapToDouble(CarritoItem::getSubtotal)
            .sum();

        model.addAttribute("cliente", cliente);
        model.addAttribute("direccion", direccion);
        model.addAttribute("celular", celular);
        model.addAttribute("carrito", carrito);
        model.addAttribute("totalCarrito", total);

        return "checkout";
    }

    // ===================== PROCESAR PEDIDO (DESPUÉS DE SELECCIONAR MÉTODO DE PAGO) =====================
    @PostMapping("/procesar")
    public String procesarPedido(@RequestParam String cliente,
                                @RequestParam String direccion,
                                @RequestParam String celular,
                                @RequestParam double total,
                                @RequestParam String metodoPago,
                                HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }

        // Guardar todos los pedidos del carrito
        for (CarritoItem item : carrito) {
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setDireccion(direccion);
            pedido.setCelular(celular);
            pedido.setProducto(item.getNombre());
            pedido.setPrecio(item.getPrecio());
            pedido.setMetodoPago(metodoPago);
            pedido.setFecha(LocalDateTime.now());
            pedido.setEstado("PENDIENTE");

            pedidoRepository.save(pedido);
        }

        // Limpiar carrito
        session.removeAttribute("carrito");

        // Redirigir según método de pago
        if ("WHATSAPP".equals(metodoPago)) {
            return "redirect:" + generarLinkWhatsApp(cliente, direccion, celular, carrito, total);
        } else {
            return "redirect:/pedido/confirmacion?cliente=" + URLEncoder.encode(cliente, StandardCharsets.UTF_8) 
                + "&total=" + total + "&metodo=EFECTIVO";
        }
    }

    // ===================== PÁGINA DE CONFIRMACIÓN =====================
    @GetMapping("/confirmacion")
    public String confirmacion(@RequestParam String cliente,
                              @RequestParam double total,
                              @RequestParam String metodo,
                              Model model) {
        model.addAttribute("cliente", cliente);
        model.addAttribute("total", total);
        model.addAttribute("metodo", metodo);
        return "confirmacion";
    }

    // ===================== GENERAR LINK DE WHATSAPP =====================
    private String generarLinkWhatsApp(String cliente, String direccion, 
                                       String celular, List<CarritoItem> carrito, double total) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola! 👋%0A");
        mensaje.append("Quisiera hacer un pedido en ").append(NOMBRE_NEGOCIO).append("%0A%0A");
        mensaje.append("*Datos de entrega:*%0A");
        mensaje.append("👤 Cliente: ").append(URLEncoder.encode(cliente, StandardCharsets.UTF_8)).append("%0A");
        mensaje.append("📍 Dirección: ").append(URLEncoder.encode(direccion, StandardCharsets.UTF_8)).append("%0A");
        mensaje.append("📱 Celular: ").append(celular).append("%0A%0A");
        mensaje.append("*Mi pedido:*%0A");

        for (CarritoItem item : carrito) {
            mensaje.append("• ").append(URLEncoder.encode(item.getNombre(), StandardCharsets.UTF_8))
                .append(" x").append(item.getCantidad())
                .append(" = $").append((long)item.getSubtotal()).append("%0A");
        }

        mensaje.append("%0A*Total: $").append((long)total).append("*");

        return "https://wa.me/" + NUMERO_WHATSAPP + "?text=" + mensaje.toString();
    }

    // ===================== PEDIDO DIRECTO (SIN CARRITO) =====================
    @PostMapping
    public String crearPedido(@RequestParam String cliente,
                              @RequestParam String direccion,
                              @RequestParam String celular,
                              @RequestParam String producto,
                              @RequestParam double precio) {

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDireccion(direccion);
        pedido.setCelular(celular);
        pedido.setProducto(producto);
        pedido.setPrecio(precio);
        pedido.setMetodoPago("PENDIENTE");
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");

        pedidoRepository.save(pedido);

        return "redirect:/pedido/confirmacion?cliente=" + URLEncoder.encode(cliente, StandardCharsets.UTF_8) 
            + "&total=" + precio + "&metodo=PENDIENTE";
    }

    // ===================== LISTAR PEDIDOS (ADMIN) =====================
    @GetMapping("/listado")
    public String listar(Model model) {
        List<Pedido> pedidos = pedidoRepository.findAll();
        model.addAttribute("pedidos", pedidos);
        return "pedidos";
    }

    // ===================== ACTUALIZAR ESTADO DEL PEDIDO =====================
    @PostMapping("/estado")
    public String actualizarEstado(@RequestParam Long id,
                                  @RequestParam String estado,
                                  @RequestParam(required = false) String tiempoEntrega) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setEstado(estado);
            if (tiempoEntrega != null && !tiempoEntrega.isEmpty()) {
                pedido.setTiempoEntrega(tiempoEntrega);
            }
            pedidoRepository.save(pedido);
        }
        return "redirect:/pedido/listado";
    }
}