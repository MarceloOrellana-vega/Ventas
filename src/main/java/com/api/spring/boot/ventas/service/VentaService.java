package com.api.spring.boot.ventas.service;

import com.api.spring.boot.ventas.model.Venta;
import com.api.spring.boot.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository repo;

    public List<Venta> listar() {
        return repo.findAll();
    }

    public Venta guardar(Venta venta) {
        return repo.save(venta);
    }

    public Venta obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}