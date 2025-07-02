package com.api.spring.boot.ventas.dto;

import com.api.spring.boot.ventas.model.Venta;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

/**
 * DTO para Venta con soporte HATEOAS
 * Extiende RepresentationModel para agregar enlaces hipermedia
 */
@Relation(collectionRelation = "ventas", itemRelation = "venta")
public class VentaDTO extends RepresentationModel<VentaDTO> {
    
    private Long id_venta;
    private Long id_cliente;
    private Long id_vendedor;
    private LocalDate fechaVenta;
    private Double total;
    private Long id_metodopago;

    // Constructor vacío
    public VentaDTO() {}

    // Constructor desde entidad Venta
    public VentaDTO(Venta venta) {
        this.id_venta = venta.getId_venta();
        this.id_cliente = venta.getId_cliente();
        this.id_vendedor = venta.getId_vendedor();
        this.fechaVenta = venta.getFechaVenta();
        this.total = venta.getTotal();
        this.id_metodopago = venta.getId_metodopago();
    }

    // Getters y setters
    public Long getId_venta() {
        return id_venta;
    }

    public void setId_venta(Long id_venta) {
        this.id_venta = id_venta;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Long getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(Long id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Long getId_metodopago() {
        return id_metodopago;
    }

    public void setId_metodopago(Long id_metodopago) {
        this.id_metodopago = id_metodopago;
    }
} 