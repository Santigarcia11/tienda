package com.angelsanddemons.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.angelsanddemons.repository.ExtraRepository;
import com.angelsanddemons.service.ProductoService;

@Controller
public class IndexController {

    private final ProductoService service;
    private final ExtraRepository extraRepository;

    public IndexController(ProductoService service, ExtraRepository extraRepository) {
        this.service = service;
        this.extraRepository = extraRepository;
    }

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("productos", service.listar());
        model.addAttribute("extras", extraRepository.findByActivoTrue());

        return "index";
    }
}