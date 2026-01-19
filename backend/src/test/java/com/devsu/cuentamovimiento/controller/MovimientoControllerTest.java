package com.devsu.cuentamovimiento.controller;

import com.devsu.cuentamovimiento.dto.MovimientoDTO;
import com.devsu.cuentamovimiento.exception.BusinessException;
import com.devsu.cuentamovimiento.service.MovimientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoController.class)
class MovimientoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MovimientoService movimientoService;
    
    private MovimientoDTO movimientoDTO;
    
    @BeforeEach
    void setUp() {
        movimientoDTO = MovimientoDTO.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento("Crédito")
                .valor(new BigDecimal("100.00"))
                .saldo(new BigDecimal("2100.00"))
                .numeroCuenta("478758")
                .build();
    }
    
    @Test
    void testCrearMovimiento_DeberiaRetornarMovimientoCreado() throws Exception {
        MovimientoDTO nuevoMovimiento = MovimientoDTO.builder()
                .tipoMovimiento("Crédito")
                .valor(new BigDecimal("100.00"))
                .numeroCuenta("478758")
                .build();
        
        when(movimientoService.crearMovimiento(any(MovimientoDTO.class)))
                .thenReturn(movimientoDTO);
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoMovimiento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoMovimiento").value("Crédito"))
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.saldo").value(2100.00));
    }
    
    @Test
    void testCrearMovimiento_ConSaldoInsuficiente_DeberiaRetornarError() throws Exception {
        MovimientoDTO movimientoDebito = MovimientoDTO.builder()
                .tipoMovimiento("Débito")
                .valor(new BigDecimal("-5000.00"))
                .numeroCuenta("478758")
                .build();
        
        when(movimientoService.crearMovimiento(any(MovimientoDTO.class)))
                .thenThrow(new BusinessException("Saldo no disponible"));
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoDebito)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }
    
    @Test
    void testCrearMovimiento_ConCupoDiarioExcedido_DeberiaRetornarError() throws Exception {
        MovimientoDTO movimientoDebito = MovimientoDTO.builder()
                .tipoMovimiento("Débito")
                .valor(new BigDecimal("-1500.00"))
                .numeroCuenta("478758")
                .build();
        
        when(movimientoService.crearMovimiento(any(MovimientoDTO.class)))
                .thenThrow(new BusinessException("Cupo diario excedido"));
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoDebito)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cupo diario excedido"));
    }
    
    @Test
    void testObtenerTodosLosMovimientos_DeberiaRetornarListaDeMovimientos() throws Exception {
        MovimientoDTO movimiento2 = MovimientoDTO.builder()
                .id(2L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento("Débito")
                .valor(new BigDecimal("-50.00"))
                .saldo(new BigDecimal("2050.00"))
                .numeroCuenta("478758")
                .build();
        
        List<MovimientoDTO> movimientos = Arrays.asList(movimientoDTO, movimiento2);
        when(movimientoService.obtenerTodosLosMovimientos()).thenReturn(movimientos);
        
        mockMvc.perform(get("/movimientos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipoMovimiento").value("Crédito"))
                .andExpect(jsonPath("$[1].tipoMovimiento").value("Débito"));
    }
    
    @Test
    void testObtenerMovimientosPorCuenta_DeberiaRetornarMovimientosDeLaCuenta() throws Exception {
        List<MovimientoDTO> movimientos = Arrays.asList(movimientoDTO);
        when(movimientoService.obtenerMovimientosPorCuenta("478758")).thenReturn(movimientos);
        
        mockMvc.perform(get("/movimientos/cuenta/478758")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numeroCuenta").value("478758"));
    }
}


