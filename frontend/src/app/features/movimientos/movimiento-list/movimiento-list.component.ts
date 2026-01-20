import { Component, OnInit } from '@angular/core';
import { Movimiento } from '../../../core/models/movimiento.model';
import { MovimientoService } from '../../../core/services/movimiento.service';

@Component({
  selector: 'app-movimiento-list',
  templateUrl: './movimiento-list.component.html',
  styleUrls: ['./movimiento-list.component.css']
})
export class MovimientoListComponent implements OnInit {
  movimientos: Movimiento[] = [];
  movimientosFiltrados: Movimiento[] = [];
  mostrarModal = false;
  loading = false;
  error = '';
  success = '';

  constructor(private movimientoService: MovimientoService) {}

  ngOnInit() {
    this.cargarMovimientos();
  }

  cargarMovimientos() {
    this.loading = true;
    this.movimientoService.getAll().subscribe({
      next: (data) => {
        console.log('MOVIMIENTOS', data);
        this.movimientos = data;
        this.movimientosFiltrados = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar movimientos';
        this.loading = false;
      }
    });
  }

  buscar(termino: string) {
    if (!termino) {
      this.movimientosFiltrados = this.movimientos;
      return;
    }

    this.movimientosFiltrados = this.movimientos.filter(m =>
      m.numeroCuenta.includes(termino) ||
      m.tipoMovimiento.toLowerCase().includes(termino.toLowerCase())
    );
  }

  abrirModal() {
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  eliminarMovimiento(id: number) {
    if (confirm('¿Está seguro de eliminar este movimiento?')) {
      this.movimientoService.delete(id).subscribe({
        next: () => {
          console.log('MOVIMIENTO ELIMINADO', id);
          this.success = 'Movimiento eliminado exitosamente';
          this.cargarMovimientos();
          setTimeout(() => this.success = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Error al eliminar movimiento';
        }
      });
    }
  }

  onMovimientoGuardado() {
    this.cerrarModal();
    this.cargarMovimientos();
    this.success = 'Movimiento creado exitosamente';
    setTimeout(() => this.success = '', 3000);
  }
}

