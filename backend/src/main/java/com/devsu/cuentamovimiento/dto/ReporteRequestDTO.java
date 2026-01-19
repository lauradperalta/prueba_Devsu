package com.devsu.cuentamovimiento.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteRequestDTO {
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaFin;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    private String formato; // "json" o "pdf"
}


