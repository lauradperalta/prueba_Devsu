package com.devsu.cuentamovimiento.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class Persona {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @NotBlank(message = "El género es obligatorio")
    @Column(nullable = false, length = 10)
    private String genero;
    
    @NotNull(message = "La edad es obligatoria")
    @Column(nullable = false)
    private Integer edad;
    
    @NotBlank(message = "La identificación es obligatoria")
    @Column(nullable = false, unique = true, length = 20)
    private String identificacion;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false, length = 200)
    private String direccion;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false, length = 20)
    private String telefono;
}


