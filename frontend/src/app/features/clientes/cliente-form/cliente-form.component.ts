import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  selector: 'app-cliente-form',
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css']
})
export class ClienteFormComponent implements OnInit {
  @Input() cliente: Cliente | null = null;
  @Output() clienteGuardado = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();

  clienteForm!: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService
  ) {}

  ngOnInit() {
    this.inicializarFormulario();
    
    if (this.cliente) {
      this.clienteForm.patchValue(this.cliente);
    }
  }

  inicializarFormulario() {
    this.clienteForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      genero: ['', Validators.required],
      edad: ['', [Validators.required, Validators.min(18), Validators.max(100)]],
      identificacion: ['', [Validators.required, Validators.minLength(10)]],
      direccion: ['', [Validators.required, Validators.minLength(10)]],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{9,10}$/)]],
      contrasena: ['', [Validators.required, Validators.minLength(4)]],
      estado: [true, Validators.required]
    });
  }

  guardar() {
    if (this.clienteForm.invalid) {
      Object.keys(this.clienteForm.controls).forEach(key => {
        this.clienteForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.error = '';
    const clienteData = this.clienteForm.value;

    const request = this.cliente
      ? this.clienteService.update(this.cliente.clienteId!, clienteData)
      : this.clienteService.create(clienteData);

    request.subscribe({
      next: (data) => {
        console.log('CLIENTE GUARDADO', data);
        this.loading = false;
        this.clienteGuardado.emit();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al guardar cliente';
        this.loading = false;
      }
    });
  }

  onCancelar() {
    this.cancelar.emit();
  }

  get f() {
    return this.clienteForm.controls;
  }
}

