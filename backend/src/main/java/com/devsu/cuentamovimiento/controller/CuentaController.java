package com.devsu.cuentamovimiento.controller;

import com.devsu.cuentamovimiento.dto.CuentaDTO;
import com.devsu.cuentamovimiento.service.CuentaService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CuentaController {
    
    private final CuentaService cuentaService;
    
    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO nuevaCuenta = cuentaService.crearCuenta(cuentaDTO);
        return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<CuentaDTO>> obtenerTodasLasCuentas() {
        List<CuentaDTO> cuentas = cuentaService.obtenerTodasLasCuentas();
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> obtenerCuentaPorId(@PathVariable Long id) {
        CuentaDTO cuenta = cuentaService.obtenerCuentaPorId(id);
        return ResponseEntity.ok(cuenta);
    }
    
    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> obtenerCuentaPorNumero(@PathVariable String numeroCuenta) {
        CuentaDTO cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        return ResponseEntity.ok(cuenta);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasPorCliente(@PathVariable Long clienteId) {
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasPorCliente(clienteId);
        return ResponseEntity.ok(cuentas);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CuentaDTO> actualizarCuenta(
            @PathVariable Long id,
            @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<CuentaDTO> actualizarCuentaParcial(
            @PathVariable Long id,
            @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuentaParcial(id, cuentaDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }
}


