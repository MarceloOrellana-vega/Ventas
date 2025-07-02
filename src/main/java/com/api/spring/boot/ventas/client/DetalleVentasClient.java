package com.api.spring.boot.ventas.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import com.api.spring.boot.ventas.dto.DetalleVentaDTO;

/**
 * Cliente para comunicarse con la API de Detalle Ventas
 * Permite obtener detalles de ventas desde el microservicio de Detalle Ventas
 */
@Component
public class DetalleVentasClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.detalle-ventas.base-url:http://localhost:8082}")
    private String detalleVentasBaseUrl;

    /**
     * Obtiene todos los detalles de una venta específica
     */
    public List<DetalleVentaDTO> obtenerDetallesPorVenta(Long idVenta) {
        try {
            String url = detalleVentasBaseUrl + "/detalles/venta/" + idVenta;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("_embedded")) {
                Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
                // El nombre puede variar según tu controller, revisa el JSON real
                List<Map<String, Object>> detallesList = (List<Map<String, Object>>) embedded.get("detalleVentaList");

                if (detallesList != null) {
                    // Mapea cada detalle a tu DTO local
                    return detallesList.stream().map(map -> {
                        DetalleVentaDTO dto = new DetalleVentaDTO();
                        dto.setIdDetalle((Integer) map.get("idDetalle"));
                        dto.setIdVenta((Integer) map.get("idVenta"));
                        dto.setIdProducto((Integer) map.get("idProducto"));
                        dto.setCantidad((Integer) map.get("cantidad"));
                        // BigDecimal puede venir como Double, String o Integer, así que conviértelo seguro:
                        Object precio = map.get("precioUnitario");
                        if (precio != null) {
                            dto.setPrecioUnitario(new java.math.BigDecimal(precio.toString()));
                        }
                        return dto;
                    }).toList();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener detalles de venta: " + e.getMessage());
        }
        return List.of(); // Retorna lista vacía si hay error
    }

    /**
     * Obtiene estadísticas de productos desde Detalle Ventas
     */
    public Map<String, Object> obtenerEstadisticasProductos() {
        try {
            String url = detalleVentasBaseUrl + "/detalle-ventas/stats/productos";
            ResponseEntity<Map> response = restTemplate.getForEntity(
                url, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return (Map<String, Object>) response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas de productos: " + e.getMessage());
        }
        return Map.of(); // Retorna mapa vacío si hay error
    }

    /**
     * Obtiene productos más vendidos desde Detalle Ventas
     */
    public List<Map<String, Object>> obtenerProductosMasVendidos() {
        try {
            String url = detalleVentasBaseUrl + "/detalle-ventas/productos/mas-vendidos";
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
        }
        return List.of(); // Retorna lista vacía si hay error
    }

    /**
     * Verifica si el microservicio de Detalle Ventas está disponible
     */
    public boolean isDetalleVentasAvailable() {
        try {
            String url = detalleVentasBaseUrl + "/actuator/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(
                url, 
                Map.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
} 