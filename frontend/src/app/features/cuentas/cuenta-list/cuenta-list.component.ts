import { Component, OnInit } from '@angular/core';
import { Cuenta } from '../../../core/models/cuenta.model';
import { CuentaService } from '../../../core/services/cuenta.service';

@Component({
  selector: 'app-cuenta-list',
  templateUrl: './cuenta-list.component.html',
  styleUrls: ['./cuenta-list.component.css']
})
export class CuentaListComponent implements OnInit {
  cuentas: Cuenta[] = [];
  cuentasFiltradas: Cuenta[] = [];
  cuentaSeleccionada: Cuenta | null = null;
  mostrarModal = false;
  loading = false;
  error = '';
  success = '';

  constructor(private cuentaService: CuentaService) {}

  ngOnInit() {
    this.cargarCuentas();
  }

  cargarCuentas() {
    this.loading = true;
    this.cuentaService.getAll().subscribe({
      next: (data) => {
        console.log('CUENTAS', data);
        this.cuentas = data;
        this.cuentasFiltradas = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar cuentas';
        this.loading = false;
      }
    });
  }

  buscar(termino: string) {
    if (!termino) {
      this.cuentasFiltradas = this.cuentas;
      return;
    }

    const terminoLower = termino.toLowerCase();
    this.cuentasFiltradas = this.cuentas.filter(c =>
      c.numeroCuenta.includes(termino) ||
      c.tipoCuenta.toLowerCase().includes(terminoLower) ||
      c.nombreCliente?.toLowerCase().includes(terminoLower)
    );
  }

  abrirModal(cuenta?: Cuenta) {
    this.cuentaSeleccionada = cuenta || null;
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
    this.cuentaSeleccionada = null;
  }

  eliminarCuenta(id: number) {
    if (confirm('¿Está seguro de eliminar esta cuenta?')) {
      this.cuentaService.delete(id).subscribe({
        next: () => {
          console.log('CUENTA ELIMINADA', id);
          this.success = 'Cuenta eliminada exitosamente';
          this.cargarCuentas();
          setTimeout(() => this.success = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Error al eliminar cuenta';
        }
      });
    }
  }

  onCuentaGuardada() {
    this.cerrarModal();
    this.cargarCuentas();
    this.success = 'Cuenta guardada exitosamente';
    setTimeout(() => this.success = '', 3000);
  }
}

