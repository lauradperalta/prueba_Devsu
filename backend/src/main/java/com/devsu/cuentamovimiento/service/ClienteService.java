package com.devsu.cuentamovimiento.service;

import com.devsu.cuentamovimiento.dto.ClienteDTO;
import com.devsu.cuentamovimiento.entity.Cliente;
import com.devsu.cuentamovimiento.exception.BusinessException;
import com.devsu.cuentamovimiento.exception.ResourceNotFoundException;
import com.devsu.cuentamovimiento.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new BusinessException("Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
        }
        
        Cliente cliente = convertirDTOAEntidad(clienteDTO);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertirEntidadADTO(clienteGuardado);
    }
    
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return convertirEntidadADTO(cliente);
    }
    
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        if (!cliente.getIdentificacion().equals(clienteDTO.getIdentificacion()) &&
            clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new BusinessException("Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
        }
        
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setGenero(clienteDTO.getGenero());
        cliente.setEdad(clienteDTO.getEdad());
        cliente.setIdentificacion(clienteDTO.getIdentificacion());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setContrasena(clienteDTO.getContrasena());
        cliente.setEstado(clienteDTO.getEstado());
        
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirEntidadADTO(clienteActualizado);
    }
    
    @Transactional
    public ClienteDTO actualizarClienteParcial(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        if (clienteDTO.getNombre() != null) {
            cliente.setNombre(clienteDTO.getNombre());
        }
        if (clienteDTO.getGenero() != null) {
            cliente.setGenero(clienteDTO.getGenero());
        }
        if (clienteDTO.getEdad() != null) {
            cliente.setEdad(clienteDTO.getEdad());
        }
        if (clienteDTO.getIdentificacion() != null) {
            if (!cliente.getIdentificacion().equals(clienteDTO.getIdentificacion()) &&
                clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
                throw new BusinessException("Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
            }
            cliente.setIdentificacion(clienteDTO.getIdentificacion());
        }
        if (clienteDTO.getDireccion() != null) {
            cliente.setDireccion(clienteDTO.getDireccion());
        }
        if (clienteDTO.getTelefono() != null) {
            cliente.setTelefono(clienteDTO.getTelefono());
        }
        if (clienteDTO.getContrasena() != null) {
            cliente.setContrasena(clienteDTO.getContrasena());
        }
        if (clienteDTO.getEstado() != null) {
            cliente.setEstado(clienteDTO.getEstado());
        }
        
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirEntidadADTO(clienteActualizado);
    }
    
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        clienteRepository.delete(cliente);
    }
    
    private ClienteDTO convertirEntidadADTO(Cliente cliente) {
        return ClienteDTO.builder()
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .contrasena(cliente.getContrasena())
                .estado(cliente.getEstado())
                .build();
    }
    
    private Cliente convertirDTOAEntidad(ClienteDTO dto) {
        return Cliente.builder()
                .clienteId(dto.getClienteId())
                .nombre(dto.getNombre())
                .genero(dto.getGenero())
                .edad(dto.getEdad())
                .identificacion(dto.getIdentificacion())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .contrasena(dto.getContrasena())
                .estado(dto.getEstado())
                .build();
    }
}


