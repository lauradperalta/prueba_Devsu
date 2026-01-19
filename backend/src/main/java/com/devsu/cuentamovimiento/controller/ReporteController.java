package com.devsu.cuentamovimiento.controller;

import com.devsu.cuentamovimiento.dto.ReporteDTO;
import com.devsu.cuentamovimiento.dto.ReporteRequestDTO;
import com.devsu.cuentamovimiento.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {
    
    private final ReporteService reporteService;
    
    @GetMapping
    public ResponseEntity<?> generarReporte(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaFin,
            @RequestParam Long clienteId,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        
        ReporteRequestDTO request = ReporteRequestDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .clienteId(clienteId)
                .formato(formato)
                .build();
        
        if ("pdf".equalsIgnoreCase(formato)) {
            String pdfBase64 = reporteService.generarReportePDF(request);
            
            Map<String, String> response = new HashMap<>();
            response.put("pdf", pdfBase64);
            response.put("filename", "reporte_" + clienteId + "_" + 
                    fechaInicio.toString() + "_" + fechaFin.toString() + ".pdf");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } else {
            List<ReporteDTO> reportes = reporteService.generarReporte(request);
            return ResponseEntity.ok(reportes);
        }
    }
}


