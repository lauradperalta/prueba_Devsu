import { Component, OnInit } from '@angular/core';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  selector: 'app-cliente-list',
  templateUrl: './cliente-list.component.html',
  styleUrls: ['./cliente-list.component.css']
})
export class ClienteListComponent implements OnInit {
  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  clienteSeleccionado: Cliente | null = null;
  mostrarModal = false;
  loading = false;
  error = '';
  success = '';

  constructor(private clienteService: ClienteService) {}

  ngOnInit() {
    this.cargarClientes();
  }

  cargarClientes() {
    this.loading = true;
    this.clienteService.getAll().subscribe({
      next: (data) => {
        console.log('CLIENTES', data);
        this.clientes = data;
        this.clientesFiltrados = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar clientes';
        this.loading = false;
      }
    });
  }

  buscar(termino: string) {
    if (!termino) {
      this.clientesFiltrados = this.clientes;
      return;
    }

    const terminoLower = termino.toLowerCase();
    this.clientesFiltrados = this.clientes.filter(c =>
      c.nombre.toLowerCase().includes(terminoLower) ||
      c.identificacion.includes(terminoLower) ||
      c.telefono.includes(terminoLower)
    );
  }

  abrirModal(cliente?: Cliente) {
    this.clienteSeleccionado = cliente || null;
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
    this.clienteSeleccionado = null;
  }

  eliminarCliente(id: number) {
    if (confirm('¿Está seguro de eliminar este cliente?')) {
      this.clienteService.delete(id).subscribe({
        next: () => {
          console.log('CLIENTE ELIMINADO', id);
          this.success = 'Cliente eliminado exitosamente';
          this.cargarClientes();
          setTimeout(() => this.success = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Error al eliminar cliente';
        }
      });
    }
  }

  onClienteGuardado() {
    this.cerrarModal();
    this.cargarClientes();
    this.success = 'Cliente guardado exitosamente';
    setTimeout(() => this.success = '', 3000);
  }
}

