package com.devsu.cuentamovimiento.service;

import com.devsu.cuentamovimiento.dto.MovimientoDTO;
import com.devsu.cuentamovimiento.entity.Cliente;
import com.devsu.cuentamovimiento.entity.Cuenta;
import com.devsu.cuentamovimiento.entity.Movimiento;
import com.devsu.cuentamovimiento.exception.BusinessException;
import com.devsu.cuentamovimiento.repository.CuentaRepository;
import com.devsu.cuentamovimiento.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {
    
    @Mock
    private MovimientoRepository movimientoRepository;
    
    @Mock
    private CuentaRepository cuentaRepository;
    
    @InjectMocks
    private MovimientoService movimientoService;
    
    private Cuenta cuenta;
    private Cliente cliente;
    private MovimientoDTO movimientoDTO;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(movimientoService, "limiteDiarioRetiro", new BigDecimal("1000.00"));
        
        cliente = Cliente.builder()
                .clienteId(1L)
                .nombre("Jose Lema")
                .genero("Masculino")
                .edad(35)
                .identificacion("1234567890")
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .contrasena("1234")
                .estado(true)
                .build();
        
        cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta("Ahorro")
                .saldoInicial(new BigDecimal("2000.00"))
                .estado(true)
                .cliente(cliente)
                .movimientos(new ArrayList<>())
                .build();
        
        movimientoDTO = MovimientoDTO.builder()
                .tipoMovimiento("Crédito")
                .valor(new BigDecimal("100.00"))
                .numeroCuenta("478758")
                .build();
    }
    
    @Test
    void testCrearMovimiento_Credito_DeberiaCrearExitosamente() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaIdOrderByFechaDesc(1L)).thenReturn(new ArrayList<>());
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento mov = invocation.getArgument(0);
            mov.setId(1L);
            return mov;
        });
        
        MovimientoDTO resultado = movimientoService.crearMovimiento(movimientoDTO);
        
        assertNotNull(resultado);
        assertEquals("Crédito", resultado.getTipoMovimiento());
        assertEquals(new BigDecimal("2100.00"), resultado.getSaldo());
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }
    
    @Test
    void testCrearMovimiento_DebitoConSaldoInsuficiente_DeberiaLanzarExcepcion() {
        movimientoDTO.setValor(new BigDecimal("-3000.00"));
        
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaIdOrderByFechaDesc(1L)).thenReturn(new ArrayList<>());
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            movimientoService.crearMovimiento(movimientoDTO);
        });
        
        assertEquals("Saldo no disponible", exception.getMessage());
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }
    
    @Test
    void testCrearMovimiento_DebitoConCupoDiarioExcedido_DeberiaLanzarExcepcion() {
        movimientoDTO.setValor(new BigDecimal("-600.00"));
        
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaIdOrderByFechaDesc(1L)).thenReturn(new ArrayList<>());
        when(movimientoRepository.calcularRetirosDiarios(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("500.00"));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            movimientoService.crearMovimiento(movimientoDTO);
        });
        
        assertEquals("Cupo diario excedido", exception.getMessage());
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }
    
    @Test
    void testCrearMovimiento_EnCuentaInactiva_DeberiaLanzarExcepcion() {
        cuenta.setEstado(false);
        
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            movimientoService.crearMovimiento(movimientoDTO);
        });
        
        assertEquals("La cuenta está inactiva", exception.getMessage());
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }
}


