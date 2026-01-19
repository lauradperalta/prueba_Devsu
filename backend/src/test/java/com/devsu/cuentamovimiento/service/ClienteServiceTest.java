package com.devsu.cuentamovimiento.service;

import com.devsu.cuentamovimiento.dto.ClienteDTO;
import com.devsu.cuentamovimiento.entity.Cliente;
import com.devsu.cuentamovimiento.exception.BusinessException;
import com.devsu.cuentamovimiento.exception.ResourceNotFoundException;
import com.devsu.cuentamovimiento.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @InjectMocks
    private ClienteService clienteService;
    
    private ClienteDTO clienteDTO;
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        clienteDTO = ClienteDTO.builder()
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
    }
    
    @Test
    void testCrearCliente_DeberiaCrearClienteExitosamente() {
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        ClienteDTO resultado = clienteService.crearCliente(clienteDTO);
        
        assertNotNull(resultado);
        assertEquals("Jose Lema", resultado.getNombre());
        assertEquals("1234567890", resultado.getIdentificacion());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
    
    @Test
    void testCrearCliente_ConIdentificacionDuplicada_DeberiaLanzarExcepcion() {
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(true);
        
        assertThrows(BusinessException.class, () -> {
            clienteService.crearCliente(clienteDTO);
        });
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void testObtenerClientePorId_DeberiaRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        
        ClienteDTO resultado = clienteService.obtenerClientePorId(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getClienteId());
        assertEquals("Jose Lema", resultado.getNombre());
    }
    
    @Test
    void testObtenerClientePorId_ConIdInexistente_DeberiaLanzarExcepcion() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.obtenerClientePorId(999L);
        });
    }
}


