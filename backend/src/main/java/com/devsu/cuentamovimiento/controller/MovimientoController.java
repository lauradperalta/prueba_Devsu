package com.devsu.cuentamovimiento.controller;

import com.devsu.cuentamovimiento.dto.MovimientoDTO;
import com.devsu.cuentamovimiento.service.MovimientoService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovimientoController {
    
    private final MovimientoService movimientoService;
    
    @PostMapping
    public ResponseEntity<MovimientoDTO> crearMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO nuevoMovimiento = movimientoService.crearMovimiento(movimientoDTO);
        return new ResponseEntity<>(nuevoMovimiento, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> obtenerTodosLosMovimientos() {
        List<MovimientoDTO> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerMovimientoPorId(@PathVariable Long id) {
        MovimientoDTO movimiento = movimientoService.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(movimiento);
    }
    
    @GetMapping("/cuenta/{numeroCuenta}")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientosPorCuenta(@PathVariable String numeroCuenta) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerMovimientosPorCuenta(numeroCuenta);
        return ResponseEntity.ok(movimientos);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return ResponseEntity.noContent().build();
    }
}


