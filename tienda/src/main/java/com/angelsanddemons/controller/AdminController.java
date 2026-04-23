package com.angelsanddemons.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.angelsanddemons.model.Extra;
import com.angelsanddemons.model.Producto;
import com.angelsanddemons.repository.ExtraRepository;
import com.angelsanddemons.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ExtraRepository extraRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private static final String ADMIN_PASSWORD = "4542984Yady";// Cambiar con tu contraseña

    // ===================== LOGIN ADMIN =====================
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String password, HttpSession session, Model model) {
        if (ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Contraseña incorrecta");
            return "admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminLoggedIn");
        return "redirect:/";
    }

    // ===================== DASHBOARD =====================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        
        List<Producto> productos = productoRepository.findAll();
        List<Extra> extras = extraRepository.findAll();
        
        model.addAttribute("productos", productos);
        model.addAttribute("extras", extras);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("totalExtras", extras.size());
        
        return "admin/dashboard";
    }

    // ===================== GESTIÓN DE PRODUCTOS =====================
    @GetMapping("/productos")
    public String listarProductos(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        return "admin/producto-form";
    }

    @PostMapping("/productos")
    public String crearProducto(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String categoria,
            @RequestParam String licor,
            @RequestParam Double precioPequeno,
            @RequestParam Double precioMediano,
            @RequestParam Double precioGrande,
            @RequestParam(required = false) MultipartFile imagen,
            HttpSession session,
            Model model) throws IOException {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setLicor(licor);
        producto.setPrecioPequeno(precioPequeno);
        producto.setPrecioMediano(precioMediano);
        producto.setPrecioGrande(precioGrande);

        // Manejar imagen
        if (imagen != null && !imagen.isEmpty()) {
            String nombreArchivo = guardarImagen(imagen);
            producto.setImagen("/uploads/" + nombreArchivo);
        } else {
            producto.setImagen("/uploads/default-product.png");
        }

        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/{id}/editar")
    public String editarProductoForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/admin/productos";
        }
        
        model.addAttribute("producto", producto);
        return "admin/producto-form";
    }

    @PostMapping("/productos/{id}")
    public String actualizarProducto(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String categoria,
            @RequestParam String licor,
            @RequestParam Double precioPequeno,
            @RequestParam Double precioMediano,
            @RequestParam Double precioGrande,
            @RequestParam(required = false) MultipartFile imagen,
            HttpSession session) throws IOException {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/admin/productos";
        }

        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setLicor(licor);
        producto.setPrecioPequeno(precioPequeno);
        producto.setPrecioMediano(precioMediano);
        producto.setPrecioGrande(precioGrande);

        if (imagen != null && !imagen.isEmpty()) {
            String nombreArchivo = guardarImagen(imagen);
            producto.setImagen("/uploads/" + nombreArchivo);
        }

        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        
        productoRepository.deleteById(id);
        return "redirect:/admin/productos";
    }

    // ===================== GESTIÓN DE EXTRAS =====================
    @GetMapping("/extras")
    public String listarExtras(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("extras", extraRepository.findAll());
        return "admin/extras";
    }

    @PostMapping("/extras")
    public String crearExtra(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam Double precio,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Extra extra = new Extra(nombre, descripcion, precio);
        extraRepository.save(extra);
        return "redirect:/admin/extras";
    }

    @GetMapping("/extras/{id}/eliminar")
    public String eliminarExtra(@PathVariable Long id, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        
        extraRepository.deleteById(id);
        return "redirect:/admin/extras";
    }

    // ===================== MÉTODOS AUXILIARES =====================
    private String guardarImagen(MultipartFile archivo) throws IOException {
        // Crear directorio si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único
        String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = uploadPath.resolve(nombreArchivo);

        // Guardar archivo
        Files.write(rutaArchivo, archivo.getBytes());

        return nombreArchivo;
    }

    private boolean isAdminLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("adminLoggedIn");
        return loggedIn != null && loggedIn;
    }
}
