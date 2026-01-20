import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Cuenta } from '../../../core/models/cuenta.model';
import { Cliente } from '../../../core/models/cliente.model';
import { CuentaService } from '../../../core/services/cuenta.service';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  selector: 'app-cuenta-form',
  templateUrl: './cuenta-form.component.html',
  styleUrls: ['./cuenta-form.component.css']
})
export class CuentaFormComponent implements OnInit {
  @Input() cuenta: Cuenta | null = null;
  @Output() cuentaGuardada = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();

  cuentaForm!: FormGroup;
  clientes: Cliente[] = [];
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private cuentaService: CuentaService,
    private clienteService: ClienteService
  ) {}

  ngOnInit() {
    this.inicializarFormulario();
    this.cargarClientes();
    
    if (this.cuenta) {
      this.cuentaForm.patchValue(this.cuenta);
    }
  }

  inicializarFormulario() {
    this.cuentaForm = this.fb.group({
      numeroCuenta: ['', [Validators.required, Validators.minLength(6)]],
      tipoCuenta: ['', Validators.required],
      saldoInicial: [0, [Validators.required, Validators.min(0)]],
      clienteId: ['', Validators.required],
      estado: [true, Validators.required]
    });
  }

  cargarClientes() {
    this.clienteService.getAll().subscribe({
      next: (data) => {
        console.log('CLIENTES PARA CUENTA', data);
        this.clientes = data.filter(c => c.estado);
      },
      error: () => this.error = 'Error al cargar clientes'
    });
  }

  guardar() {
    if (this.cuentaForm.invalid) {
      Object.keys(this.cuentaForm.controls).forEach(key => {
        this.cuentaForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.error = '';
    const cuentaData = this.cuentaForm.value;

    const request = this.cuenta
      ? this.cuentaService.update(this.cuenta.id!, cuentaData)
      : this.cuentaService.create(cuentaData);

    request.subscribe({
      next: (data) => {
        console.log('CUENTA GUARDADA', data);
        this.loading = false;
        this.cuentaGuardada.emit();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al guardar cuenta';
        this.loading = false;
      }
    });
  }

  onCancelar() {
    this.cancelar.emit();
  }

  get f() {
    return this.cuentaForm.controls;
  }
}

