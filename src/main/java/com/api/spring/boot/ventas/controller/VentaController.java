package com.api.spring.boot.ventas.controller;

import jakarta.validation.Valid;
import com.api.spring.boot.ventas.model.Venta;
import com.api.spring.boot.ventas.dto.VentaDTO;
import com.api.spring.boot.ventas.service.VentaService;
import com.api.spring.boot.ventas.client.DetalleVentasClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Controlador REST para gestionar ventas
 * Incluye soporte HATEOAS y documentación Swagger
 */
@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
@Tag(name = "Ventas", description = "API para gestión de ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private DetalleVentasClient detalleVentasClient;

    /**
     * Obtiene todas las ventas con enlaces HATEOAS
     */
    @GetMapping
    @Operation(summary = "Listar todas las ventas", description = "Retorna una lista de todas las ventas con enlaces HATEOAS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay ventas")
    })
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> listarVentas() {
        List<Venta> ventas = ventaService.listar();
        
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<VentaDTO>> ventasDTO = ventas.stream()
            .map(venta -> {
                VentaDTO dto = new VentaDTO(venta);
                return EntityModel.of(dto,
                    linkTo(methodOn(VentaController.class).obtenerVenta(venta.getId_venta())).withSelfRel(),
                    linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
                    Link.of("http://localhost:8888/ventas/" + venta.getId_venta()).withRel("gateway")
                );
            })
            .collect(Collectors.toList());

        CollectionModel<EntityModel<VentaDTO>> collection = CollectionModel.of(ventasDTO,
            linkTo(methodOn(VentaController.class).listarVentas()).withSelfRel(),
            Link.of("http://localhost:8888/ventas").withRel("gateway")
        );

        return ResponseEntity.ok(collection);
    }

    /**
     * Obtiene una venta específica por ID con enlaces HATEOAS
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Retorna una venta específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta encontrada", 
                    content = @Content(schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<EntityModel<VentaDTO>> obtenerVenta(
            @Parameter(description = "ID de la venta") @PathVariable Long id) {
        Venta venta = ventaService.obtenerPorId(id);
        if (venta != null) {
            VentaDTO dto = new VentaDTO(venta);
            EntityModel<VentaDTO> entityModel = EntityModel.of(dto,
                linkTo(methodOn(VentaController.class).obtenerVenta(id)).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
                linkTo(methodOn(VentaController.class).eliminarVenta(id)).withRel("delete"),
                Link.of("http://localhost:8888/ventas/" + id).withRel("gateway")
            );
            return ResponseEntity.ok(entityModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea una nueva venta
     */
    @PostMapping
    @Operation(summary = "Crear nueva venta", description = "Crea una nueva venta en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venta creada exitosamente",
                    content = @Content(schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de venta inválidos")
    })
    public ResponseEntity<EntityModel<VentaDTO>> crearVenta(
            @Parameter(description = "Datos de la venta") @Valid @RequestBody Venta venta) {
        Venta nueva = ventaService.guardar(venta);
        VentaDTO dto = new VentaDTO(nueva);
        EntityModel<VentaDTO> entityModel = EntityModel.of(dto,
            linkTo(methodOn(VentaController.class).obtenerVenta(nueva.getId_venta())).withSelfRel(),
            linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
            linkTo(methodOn(VentaController.class).eliminarVenta(nueva.getId_venta())).withRel("delete"),
            Link.of("http://localhost:8888/ventas/" + nueva.getId_venta()).withRel("gateway")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    /**
     * Elimina una venta por ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar venta", description = "Elimina una venta por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<?> eliminarVenta(
            @Parameter(description = "ID de la venta a eliminar") @PathVariable Long id) {
        Venta existente = ventaService.obtenerPorId(id);
        if (existente != null) {
            ventaService.eliminar(id);
            return ResponseEntity.ok("Venta eliminada exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venta no encontrada");
        }
    }

    /**
     * Nuevo endpoint: Obtener estadísticas básicas de ventas
     */
    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de ventas", description = "Retorna estadísticas básicas de las ventas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas")
    })
    public ResponseEntity<EntityModel<Object>> obtenerEstadisticas() {
        List<Venta> ventas = ventaService.listar();
        
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        long cantidadVentas = ventas.size();
        
        class EstadisticasVentas {
            public final long cantidadVentas;
            public final double totalVentas;
            public final double promedioVentas;
            
            public EstadisticasVentas(long cantidad, double total) {
                this.cantidadVentas = cantidad;
                this.totalVentas = total;
                this.promedioVentas = cantidad > 0 ? total / cantidad : 0;
            }
        }
        
        EstadisticasVentas stats = new EstadisticasVentas(cantidadVentas, totalVentas);

        EntityModel<Object> entityModel = EntityModel.of(stats,
            linkTo(methodOn(VentaController.class).obtenerEstadisticas()).withSelfRel(),
            linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
            Link.of("http://localhost:8888/ventas/stats").withRel("gateway")
        );

        return ResponseEntity.ok(entityModel);
    }

    /**
     * Nuevo endpoint: Buscar ventas por cliente
     */
    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Buscar ventas por cliente", description = "Retorna todas las ventas de un cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas del cliente encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay ventas para este cliente")
    })
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> buscarPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Long idCliente) {
        List<Venta> ventas = ventaService.listar().stream()
            .filter(venta -> venta.getId_cliente().equals(idCliente))
            .collect(Collectors.toList());

        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<VentaDTO>> ventasDTO = ventas.stream()
            .map(venta -> {
                VentaDTO dto = new VentaDTO(venta);
                return EntityModel.of(dto,
                    linkTo(methodOn(VentaController.class).obtenerVenta(venta.getId_venta())).withSelfRel(),
                    linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
                    Link.of("http://localhost:8888/ventas/" + venta.getId_venta()).withRel("gateway")
                );
            })
            .collect(Collectors.toList());

        CollectionModel<EntityModel<VentaDTO>> collection = CollectionModel.of(ventasDTO,
            linkTo(methodOn(VentaController.class).buscarPorCliente(idCliente)).withSelfRel(),
            linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
            Link.of("http://localhost:8888/ventas/cliente/" + idCliente).withRel("gateway")
        );

        return ResponseEntity.ok(collection);
    }

    /**
     * Endpoint combinado: Obtener venta con sus detalles
     */
    @GetMapping("/{id}/con-detalles")
    @Operation(summary = "Obtener venta con detalles", description = "Retorna una venta específica junto con todos sus detalles de productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta con detalles encontrada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<EntityModel<Map<String, Object>>> obtenerVentaConDetalles(
            @Parameter(description = "ID de la venta") @PathVariable Long id) {
        Venta venta = ventaService.obtenerPorId(id);
        if (venta != null) {
            // Obtener detalles desde el microservicio de Detalle Ventas usando el DTO local
            List<com.api.spring.boot.ventas.dto.DetalleVentaDTO> detalles = detalleVentasClient.obtenerDetallesPorVenta(id);
            
            // Crear respuesta combinada
            Map<String, Object> ventaCompleta = Map.of(
                "venta", new VentaDTO(venta),
                "detalles", detalles,
                "totalDetalles", detalles.size()
            );

            EntityModel<Map<String, Object>> entityModel = EntityModel.of(ventaCompleta,
                linkTo(methodOn(VentaController.class).obtenerVentaConDetalles(id)).withSelfRel(),
                linkTo(methodOn(VentaController.class).obtenerVenta(id)).withRel("venta"),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
                Link.of("http://localhost:8888/ventas/" + id + "/con-detalles").withRel("gateway")
            );

            return ResponseEntity.ok(entityModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * endpoint: Obtener estadísticas combinadas (Ventas + Detalle Ventas)
     */
    @GetMapping("/stats/completas")
    @Operation(summary = "Obtener estadísticas completas", description = "Retorna estadísticas combinadas de ventas y productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas completas obtenidas")
    })
    public ResponseEntity<EntityModel<Map<String, Object>>> obtenerEstadisticasCompletas() {
        // Estadísticas de ventas
        List<Venta> ventas = ventaService.listar();
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        long cantidadVentas = ventas.size();
        
        // Estadísticas de productos desde Detalle Ventas
        Map<String, Object> statsProductos = detalleVentasClient.obtenerEstadisticasProductos();
        
        // Combinar estadísticas
        Map<String, Object> statsCompletas = Map.of(
            "ventas", Map.of(
                "cantidadVentas", cantidadVentas,
                "totalVentas", totalVentas,
                "promedioVentas", cantidadVentas > 0 ? totalVentas / cantidadVentas : 0
            ),
            "productos", statsProductos,
            "microservicios", Map.of(
                "detalleVentasDisponible", detalleVentasClient.isDetalleVentasAvailable()
            )
        );

        EntityModel<Map<String, Object>> entityModel = EntityModel.of(statsCompletas,
            linkTo(methodOn(VentaController.class).obtenerEstadisticasCompletas()).withSelfRel(),
            linkTo(methodOn(VentaController.class).obtenerEstadisticas()).withRel("stats-ventas"),
            linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
            Link.of("http://localhost:8888/ventas/stats/completas").withRel("gateway")
        );

        return ResponseEntity.ok(entityModel);
    }

    /**
     *  endpoint: Obtener productos más vendidos desde Detalle Ventas
     */
    @GetMapping("/productos/mas-vendidos")
    @Operation(summary = "Obtener productos más vendidos", description = "Retorna lista de productos más vendidos desde Detalle Ventas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos más vendidos obtenidos")
    })
    public ResponseEntity<EntityModel<List<Map<String, Object>>>> obtenerProductosMasVendidos() {
        List<Map<String, Object>> productos = detalleVentasClient.obtenerProductosMasVendidos();

        EntityModel<List<Map<String, Object>>> entityModel = EntityModel.of(productos,
            linkTo(methodOn(VentaController.class).obtenerProductosMasVendidos()).withSelfRel(),
            linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
            Link.of("http://localhost:8888/ventas/productos/mas-vendidos").withRel("gateway")
        );

        return ResponseEntity.ok(entityModel);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .findFirst().orElse("Error de validación");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
    }
}
