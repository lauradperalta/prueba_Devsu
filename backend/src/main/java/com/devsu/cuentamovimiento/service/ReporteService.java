package com.devsu.cuentamovimiento.service;

import com.devsu.cuentamovimiento.dto.ReporteDTO;
import com.devsu.cuentamovimiento.dto.ReporteRequestDTO;
import com.devsu.cuentamovimiento.entity.Cliente;
import com.devsu.cuentamovimiento.entity.Cuenta;
import com.devsu.cuentamovimiento.entity.Movimiento;
import com.devsu.cuentamovimiento.exception.ResourceNotFoundException;
import com.devsu.cuentamovimiento.repository.ClienteRepository;
import com.devsu.cuentamovimiento.repository.CuentaRepository;
import com.devsu.cuentamovimiento.repository.MovimientoRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {
    
    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    
    @Transactional(readOnly = true)
    public List<ReporteDTO> generarReporte(ReporteRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));
        
        List<Cuenta> cuentas = cuentaRepository.findByClienteClienteId(request.getClienteId());
        
        if (cuentas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cuentas para el cliente: " + cliente.getNombre());
        }
        
        LocalDateTime fechaInicio = request.getFechaInicio().atStartOfDay();
        LocalDateTime fechaFin = request.getFechaFin().atTime(23, 59, 59);
        
        return cuentas.stream()
                .flatMap(cuenta -> obtenerMovimientosCuenta(cuenta, fechaInicio, fechaFin).stream()
                        .map(movimiento -> construirReporteDTO(cliente, cuenta, movimiento)))
                .collect(Collectors.toList());
    }
    
    private List<Movimiento> obtenerMovimientosCuenta(Cuenta cuenta, LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByCuentaIdAndFechaBetween(cuenta.getId(), inicio, fin);
    }
    
    private ReporteDTO construirReporteDTO(Cliente cliente, Cuenta cuenta, Movimiento movimiento) {
        return ReporteDTO.builder()
                .fecha(movimiento.getFecha())
                .cliente(cliente.getNombre())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipo(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .movimiento(movimiento.getValor())
                .saldoDisponible(movimiento.getSaldo())
                .build();
    }
    
    @Transactional(readOnly = true)
    public String generarReportePDF(ReporteRequestDTO request) {
        List<ReporteDTO> reportes = generarReporte(request);
        
        if (reportes.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron movimientos en el rango de fechas especificado");
        }
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Título
            Paragraph titulo = new Paragraph("Estado de Cuenta")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);
            
            // Información del cliente
            Cliente cliente = clienteRepository.findById(request.getClienteId()).orElseThrow();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            document.add(new Paragraph("Cliente: " + cliente.getNombre()));
            document.add(new Paragraph("Período: " + request.getFechaInicio().format(formatter) + 
                    " - " + request.getFechaFin().format(formatter)));
            document.add(new Paragraph(" "));
            
            // Agrupar por cuenta
            Map<String, List<ReporteDTO>> reportesPorCuenta = reportes.stream()
                    .collect(Collectors.groupingBy(ReporteDTO::getNumeroCuenta));
            
            for (Map.Entry<String, List<ReporteDTO>> entry : reportesPorCuenta.entrySet()) {
                String numeroCuenta = entry.getKey();
                List<ReporteDTO> movimientosCuenta = entry.getValue();
                
                // Información de la cuenta
                ReporteDTO primerMovimiento = movimientosCuenta.get(0);
                document.add(new Paragraph("Cuenta: " + numeroCuenta + " - " + primerMovimiento.getTipo())
                        .setBold());
                document.add(new Paragraph("Saldo Inicial: $" + primerMovimiento.getSaldoInicial()));
                document.add(new Paragraph(" "));
                
                // Tabla de movimientos
                Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2}));
                table.setWidth(UnitValue.createPercentValue(100));
                
                // Encabezados
                table.addHeaderCell("Fecha");
                table.addHeaderCell("Tipo");
                table.addHeaderCell("Movimiento");
                table.addHeaderCell("Saldo Disponible");
                
                // Datos
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                BigDecimal totalCreditos = BigDecimal.ZERO;
                BigDecimal totalDebitos = BigDecimal.ZERO;
                
                for (ReporteDTO reporte : movimientosCuenta) {
                    table.addCell(reporte.getFecha().format(dateFormatter));
                    table.addCell(reporte.getMovimiento().compareTo(BigDecimal.ZERO) > 0 ? "Crédito" : "Débito");
                    table.addCell("$" + reporte.getMovimiento().toString());
                    table.addCell("$" + reporte.getSaldoDisponible().toString());
                    
                    if (reporte.getMovimiento().compareTo(BigDecimal.ZERO) > 0) {
                        totalCreditos = totalCreditos.add(reporte.getMovimiento());
                    } else {
                        totalDebitos = totalDebitos.add(reporte.getMovimiento().abs());
                    }
                }
                
                document.add(table);
                
                // Totales
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Total Créditos: $" + totalCreditos));
                document.add(new Paragraph("Total Débitos: $" + totalDebitos));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("---------------------------------------------------"));
                document.add(new Paragraph(" "));
            }
            
            document.close();
            
            // Convertir a Base64
            byte[] pdfBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(pdfBytes);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }
}
