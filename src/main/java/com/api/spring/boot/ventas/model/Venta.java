package com.api.spring.boot.ventas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDate;

@Entity
@Table(name = "boleta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

    @NotNull(message = "id_cliente es obligatorio")
    private Long id_cliente;

    @NotNull(message = "id_vendedor es obligatorio")
    private Long id_vendedor;

    @NotNull(message = "fechaVenta es obligatoria")
    @Column(name = "fecha_venta")
    private LocalDate fechaVenta;

    @NotNull(message = "total es obligatorio")
    @DecimalMin(value = "0.01", message = "total debe ser mayor a 0")
    private Double total;

    @NotNull(message = "id_metodopago es obligatorio")
    private Long id_metodopago;

    // Constructor vacío
    public Venta() {}

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
