package com.devsu.cuentamovimiento.controller;

import com.devsu.cuentamovimiento.dto.ClienteDTO;
import com.devsu.cuentamovimiento.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ClienteService clienteService;
    
    private ClienteDTO clienteDTO;
    
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
    }
    
    @Test
    void testCrearCliente_DeberiaRetornarClienteCreado() throws Exception {
        when(clienteService.crearCliente(any(ClienteDTO.class))).thenReturn(clienteDTO);
        
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.identificacion").value("1234567890"))
                .andExpect(jsonPath("$.estado").value(true));
    }
    
    @Test
    void testObtenerTodosLosClientes_DeberiaRetornarListaDeClientes() throws Exception {
        ClienteDTO cliente2 = ClienteDTO.builder()
                .clienteId(2L)
                .nombre("Marianela Montalvo")
                .genero("Femenino")
                .edad(28)
                .identificacion("0987654321")
                .direccion("Amazonas y NNUU")
                .telefono("097548965")
                .contrasena("5678")
                .estado(true)
                .build();
        
        List<ClienteDTO> clientes = Arrays.asList(clienteDTO, cliente2);
        when(clienteService.obtenerTodosLosClientes()).thenReturn(clientes);
        
        mockMvc.perform(get("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Jose Lema"))
                .andExpect(jsonPath("$[1].nombre").value("Marianela Montalvo"));
    }
    
    @Test
    void testObtenerClientePorId_DeberiaRetornarCliente() throws Exception {
        when(clienteService.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        
        mockMvc.perform(get("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.nombre").value("Jose Lema"));
    }
    
    @Test
    void testActualizarCliente_DeberiaRetornarClienteActualizado() throws Exception {
        ClienteDTO clienteActualizado = ClienteDTO.builder()
                .clienteId(1L)
                .nombre("Jose Lema Actualizado")
                .genero("Masculino")
                .edad(36)
                .identificacion("1234567890")
                .direccion("Nueva Dirección")
                .telefono("098254785")
                .contrasena("1234")
                .estado(true)
                .build();
        
        when(clienteService.actualizarCliente(eq(1L), any(ClienteDTO.class)))
                .thenReturn(clienteActualizado);
        
        mockMvc.perform(put("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Jose Lema Actualizado"))
                .andExpect(jsonPath("$.direccion").value("Nueva Dirección"));
    }
    
    @Test
    void testEliminarCliente_DeberiaRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}


