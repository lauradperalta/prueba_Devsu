package com.devsu.cuentamovimiento.repository;

import com.devsu.cuentamovimiento.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    List<Cuenta> findByClienteClienteId(Long clienteId);
    
    boolean existsByNumeroCuenta(String numeroCuenta);
}


