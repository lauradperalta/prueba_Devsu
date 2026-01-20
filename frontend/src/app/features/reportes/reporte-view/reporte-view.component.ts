import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Reporte } from '../../../core/models/reporte.model';
import { Cliente } from '../../../core/models/cliente.model';
import { ReporteService } from '../../../core/services/reporte.service';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  selector: 'app-reporte-view',
  templateUrl: './reporte-view.component.html',
  styleUrls: ['./reporte-view.component.css']
})
export class ReporteViewComponent implements OnInit {
  reporteForm!: FormGroup;
  clientes: Cliente[] = [];
  reportes: Reporte[] = [];
  loading = false;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteService,
    private clienteService: ClienteService
  ) {}

  ngOnInit() {
    this.inicializarFormulario();
    this.cargarClientes();
  }

  inicializarFormulario() {
    this.reporteForm = this.fb.group({
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      clienteId: ['', Validators.required]
    });
  }

  cargarClientes() {
    this.clienteService.getAll().subscribe({
      next: (data) => {
        console.log('CLIENTES PARA REPORTE', data);
        this.clientes = data.filter(c => c.estado);
      },
      error: () => this.error = 'Error al cargar clientes'
    });
  }

  generarReporte() {
    if (this.reporteForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    
    const fechaInicio = this.formatearFecha(this.reporteForm.get('fechaInicio')?.value);
    const fechaFin = this.formatearFecha(this.reporteForm.get('fechaFin')?.value);
    const clienteId = this.reporteForm.get('clienteId')?.value;

    this.reporteService.generarReporte(fechaInicio, fechaFin, clienteId).subscribe({
      next: (data) => {
        console.log('REPORTE GENERADO', data);
        this.reportes = data;
        this.loading = false;
        if (data.length === 0) {
          this.error = 'No se encontraron movimientos en el rango de fechas';
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al generar reporte';
        this.loading = false;
      }
    });
  }

  descargarPDF() {
    if (this.reporteForm.invalid) {
      return;
    }

    this.loading = true;
    
    const fechaInicio = this.formatearFecha(this.reporteForm.get('fechaInicio')?.value);
    const fechaFin = this.formatearFecha(this.reporteForm.get('fechaFin')?.value);
    const clienteId = this.reporteForm.get('clienteId')?.value;

    this.reporteService.descargarPDF(fechaInicio, fechaFin, clienteId).subscribe({
      next: (response) => {
        console.log('PDF DESCARGADO', response);
        this.decodificarYDescargarPDF(response.pdf, response.filename);
        this.success = 'PDF descargado exitosamente';
        this.loading = false;
        setTimeout(() => this.success = '', 3000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al descargar PDF';
        this.loading = false;
      }
    });
  }

  private formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const dia = String(date.getDate()).padStart(2, '0');
    const mes = String(date.getMonth() + 1).padStart(2, '0');
    const anio = date.getFullYear();
    return `${dia}/${mes}/${anio}`;
  }

  private decodificarYDescargarPDF(base64: string, filename: string) {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);
    
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: 'application/pdf' });
    
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename || 'reporte.pdf';
    link.click();
    
    window.URL.revokeObjectURL(url);
  }

  calcularTotalDebitos(): number {
    return this.reportes
      .filter(r => r.Movimiento < 0)
      .reduce((sum, r) => sum + Math.abs(r.Movimiento), 0);
  }

  calcularTotalCreditos(): number {
    return this.reportes
      .filter(r => r.Movimiento > 0)
      .reduce((sum, r) => sum + r.Movimiento, 0);
  }

  get f() {
    return this.reporteForm.controls;
  }
}

