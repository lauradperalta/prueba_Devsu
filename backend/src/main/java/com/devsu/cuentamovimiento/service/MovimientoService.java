package com.devsu.cuentamovimiento.service;

import com.devsu.cuentamovimiento.dto.MovimientoDTO;
import com.devsu.cuentamovimiento.entity.Cuenta;
import com.devsu.cuentamovimiento.entity.Movimiento;
import com.devsu.cuentamovimiento.exception.BusinessException;
import com.devsu.cuentamovimiento.exception.ResourceNotFoundException;
import com.devsu.cuentamovimiento.repository.CuentaRepository;
import com.devsu.cuentamovimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {
    
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    
    @Value("${app.limite-diario-retiro}")
    private BigDecimal limiteDiarioRetiro;
    
    @Transactional
    public MovimientoDTO crearMovimiento(MovimientoDTO movimientoDTO) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(movimientoDTO.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "número", movimientoDTO.getNumeroCuenta()));
        
        // Validar que la cuenta esté activa
        if (!cuenta.getEstado()) {
            throw new BusinessException("La cuenta está inactiva");
        }
        
        BigDecimal saldoActual = calcularSaldoActual(cuenta);
        
        // Validar el movimiento si es débito
        if (movimientoDTO.getValor().compareTo(BigDecimal.ZERO) < 0) {
            validarDebito(cuenta, movimientoDTO.getValor(), saldoActual);
        }
        
        BigDecimal nuevoSaldo = saldoActual.add(movimientoDTO.getValor());
        String tipoMovimiento = movimientoDTO.getValor().compareTo(BigDecimal.ZERO) > 0 
                ? "Crédito" 
                : "Débito";
        
        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(movimientoDTO.getValor())
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .build();
        
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        return convertirEntidadADTO(movimientoGuardado);
    }
    
    private void validarDebito(Cuenta cuenta, BigDecimal valor, BigDecimal saldoActual) {
        BigDecimal valorAbsoluto = valor.abs();
        
        // Validar saldo disponible
        if (saldoActual.compareTo(valorAbsoluto) < 0) {
            throw new BusinessException("Saldo no disponible");
        }
        
        // Validar límite diario
        BigDecimal retirosDiarios = movimientoRepository.calcularRetirosDiarios(
                cuenta.getId(), 
                LocalDateTime.now()
        );
        
        BigDecimal totalRetirosDia = retirosDiarios.add(valorAbsoluto);
        
        if (totalRetirosDia.compareTo(limiteDiarioRetiro) > 0) {
            throw new BusinessException("Cupo diario excedido");
        }
    }
    
    private BigDecimal calcularSaldoActual(Cuenta cuenta) {
        return movimientoRepository.findByCuentaIdOrderByFechaDesc(cuenta.getId())
                .stream()
                .findFirst()
                .map(Movimiento::getSaldo)
                .orElse(cuenta.getSaldoInicial());
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerTodosLosMovimientos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public MovimientoDTO obtenerMovimientoPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "id", id));
        return convertirEntidadADTO(movimiento);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerMovimientosPorCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "número", numeroCuenta));
        
        return movimientoRepository.findByCuentaIdOrderByFechaDesc(cuenta.getId())
                .stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MovimientoDTO actualizarMovimiento(Long id, MovimientoDTO movimientoDTO) {
        throw new BusinessException("No se permite actualizar movimientos. Debe crear un movimiento de ajuste.");
    }
    
    @Transactional
    public void eliminarMovimiento(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "id", id));
        movimientoRepository.delete(movimiento);
    }
    
    private MovimientoDTO convertirEntidadADTO(Movimiento movimiento) {
        return MovimientoDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
                .build();
    }
}
