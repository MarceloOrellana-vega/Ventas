package com.api.spring.boot.ventas.repository;

import com.api.spring.boot.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {}
