package com.angelsanddemons.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.angelsanddemons.model.CarritoItem;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    // ===================== AGREGAR =====================
    @PostMapping("/agregar")
    @SuppressWarnings("unchecked")
    public String agregar(@RequestParam String nombre,
                          @RequestParam double precio,
                          HttpSession session) {

        List<CarritoItem> carrito =
                (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        carrito.add(new CarritoItem(nombre, precio, 1));

        session.setAttribute("carrito", carrito);

        return "redirect:/carrito";
    }

    // ===================== VER CARRITO =====================
    @GetMapping
    @SuppressWarnings("unchecked")
    public String verCarrito(HttpSession session, org.springframework.ui.Model model) {

        List<CarritoItem> carrito =
                (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        double total = carrito.stream()
            .mapToDouble(CarritoItem::getSubtotal)
            .sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("totalCarrito", total);

        return "carrito";
    }

    // ===================== LIMPIAR =====================
    @GetMapping("/vaciar")
    public String vaciar(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }
}