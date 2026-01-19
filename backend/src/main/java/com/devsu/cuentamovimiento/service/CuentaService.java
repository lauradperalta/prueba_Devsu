package com.devsu.cuentamovimiento.service;

import com.devsu.cuentamovimiento.dto.CuentaDTO;
import com.devsu.cuentamovimiento.entity.Cliente;
import com.devsu.cuentamovimiento.entity.Cuenta;
import com.devsu.cuentamovimiento.exception.BusinessException;
import com.devsu.cuentamovimiento.exception.ResourceNotFoundException;
import com.devsu.cuentamovimiento.repository.ClienteRepository;
import com.devsu.cuentamovimiento.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaService {
    
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    
    @Transactional
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(cuentaDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", cuentaDTO.getClienteId()));
        
        // Validar que no exista una cuenta con el mismo número
        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
        }
        
        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(cuentaDTO.getNumeroCuenta())
                .tipoCuenta(cuentaDTO.getTipoCuenta())
                .saldoInicial(cuentaDTO.getSaldoInicial())
                .estado(cuentaDTO.getEstado())
                .cliente(cliente)
                .build();
        
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return convertirEntidadADTO(cuentaGuardada);
    }
    
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll().stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        return convertirEntidadADTO(cuenta);
    }
    
    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "número", numeroCuenta));
        return convertirEntidadADTO(cuenta);
    }
    
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasPorCliente(Long clienteId) {
        // Verificar que el cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", "id", clienteId);
        }
        
        return cuentaRepository.findByClienteClienteId(clienteId).stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        
        // Validar que si se cambia el número de cuenta, no exista otra con ese número
        if (!cuenta.getNumeroCuenta().equals(cuentaDTO.getNumeroCuenta()) &&
            cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
        }
        
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        cuenta.setEstado(cuentaDTO.getEstado());
        
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertirEntidadADTO(cuentaActualizada);
    }
    
    @Transactional
    public CuentaDTO actualizarCuentaParcial(Long id, CuentaDTO cuentaDTO) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        
        if (cuentaDTO.getNumeroCuenta() != null) {
            if (!cuenta.getNumeroCuenta().equals(cuentaDTO.getNumeroCuenta()) &&
                cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
                throw new BusinessException("Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
            }
            cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        }
        if (cuentaDTO.getTipoCuenta() != null) {
            cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        }
        if (cuentaDTO.getSaldoInicial() != null) {
            cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        }
        if (cuentaDTO.getEstado() != null) {
            cuenta.setEstado(cuentaDTO.getEstado());
        }
        
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertirEntidadADTO(cuentaActualizada);
    }
    
    @Transactional
    public void eliminarCuenta(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        cuentaRepository.delete(cuenta);
    }
    
    private CuentaDTO convertirEntidadADTO(Cuenta cuenta) {
        return CuentaDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getCliente().getClienteId())
                .nombreCliente(cuenta.getCliente().getNombre())
                .build();
    }
}


