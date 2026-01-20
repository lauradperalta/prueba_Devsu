import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Cuenta } from '../../../core/models/cuenta.model';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { CuentaService } from '../../../core/services/cuenta.service';

@Component({
  selector: 'app-movimiento-form',
  templateUrl: './movimiento-form.component.html',
  styleUrls: ['./movimiento-form.component.css']
})
export class MovimientoFormComponent implements OnInit {
  @Output() movimientoGuardado = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();

  movimientoForm!: FormGroup;
  cuentas: Cuenta[] = [];
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private movimientoService: MovimientoService,
    private cuentaService: CuentaService
  ) {}

  ngOnInit() {
    this.inicializarFormulario();
    this.cargarCuentas();
  }

  inicializarFormulario() {
    this.movimientoForm = this.fb.group({
      numeroCuenta: ['', Validators.required],
      tipoMovimiento: ['Crédito', Validators.required],
      valor: [0, [Validators.required, Validators.min(0.01)]]
    });

    // Actualizar valor según tipo
    this.movimientoForm.get('tipoMovimiento')?.valueChanges.subscribe(tipo => {
      const valorActual = Math.abs(this.movimientoForm.get('valor')?.value || 0);
      if (tipo === 'Débito') {
        this.movimientoForm.get('valor')?.setValue(-valorActual);
      } else {
        this.movimientoForm.get('valor')?.setValue(valorActual);
      }
    });
  }

  cargarCuentas() {
    this.cuentaService.getAll().subscribe({
      next: (data) => {
        console.log('CUENTAS PARA MOVIMIENTO', data);
        this.cuentas = data.filter(c => c.estado);
      },
      error: () => this.error = 'Error al cargar cuentas'
    });
  }

  guardar() {
    if (this.movimientoForm.invalid) {
      Object.keys(this.movimientoForm.controls).forEach(key => {
        this.movimientoForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.error = '';
    
    let valor = Math.abs(parseFloat(this.movimientoForm.get('valor')?.value));
    if (this.movimientoForm.get('tipoMovimiento')?.value === 'Débito') {
      valor = -valor;
    }

    const movimientoData = {
      numeroCuenta: this.movimientoForm.get('numeroCuenta')?.value,
      tipoMovimiento: this.movimientoForm.get('tipoMovimiento')?.value,
      valor: valor
    };

    this.movimientoService.create(movimientoData).subscribe({
      next: (data) => {
        console.log('MOVIMIENTO CREADO', data);
        this.loading = false;
        this.movimientoGuardado.emit();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al crear movimiento';
        this.loading = false;
      }
    });
  }

  onCancelar() {
    this.cancelar.emit();
  }

  get f() {
    return this.movimientoForm.controls;
  }
}

