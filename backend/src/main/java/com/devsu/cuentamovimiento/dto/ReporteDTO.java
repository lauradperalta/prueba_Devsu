package com.devsu.cuentamovimiento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteDTO {
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("Fecha")
    private LocalDateTime fecha;
    
    @JsonProperty("Cliente")
    private String cliente;
    
    @JsonProperty("Numero Cuenta")
    private String numeroCuenta;
    
    @JsonProperty("Tipo")
    private String tipo;
    
    @JsonProperty("Saldo Inicial")
    private BigDecimal saldoInicial;
    
    @JsonProperty("Estado")
    private Boolean estado;
    
    @JsonProperty("Movimiento")
    private BigDecimal movimiento;
    
    @JsonProperty("Saldo Disponible")
    private BigDecimal saldoDisponible;
}


