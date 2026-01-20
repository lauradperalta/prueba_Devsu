import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ClienteListComponent } from './cliente-list.component';
import { ClienteService } from '../../../core/services/cliente.service';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ClienteListComponent', () => {
  let component: ClienteListComponent;
  let fixture: ComponentFixture<ClienteListComponent>;
  let clienteService: ClienteService;

  const mockClientes = [
    {
      clienteId: 1,
      nombre: 'Jose Lema',
      genero: 'Masculino',
      edad: 35,
      identificacion: '1234567890',
      direccion: 'Otavalo sn y principal',
      telefono: '098254785',
      contrasena: '1234',
      estado: true
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClienteListComponent],
      imports: [HttpClientTestingModule],
      providers: [ClienteService],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ClienteListComponent);
    component = fixture.componentInstance;
    clienteService = TestBed.inject(ClienteService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load clientes on init', () => {
    jest.spyOn(clienteService, 'getAll').mockReturnValue(of(mockClientes));

    component.ngOnInit();

    expect(component.clientes).toEqual(mockClientes);
    expect(component.clientesFiltrados).toEqual(mockClientes);
  });

  it('should filter clientes by search term', () => {
    component.clientes = mockClientes;
    component.clientesFiltrados = mockClientes;

    component.buscar('Jose');

    expect(component.clientesFiltrados.length).toBe(1);
    expect(component.clientesFiltrados[0].nombre).toBe('Jose Lema');
  });

  it('should delete cliente', () => {
    jest.spyOn(clienteService, 'delete').mockReturnValue(of(undefined));
    jest.spyOn(clienteService, 'getAll').mockReturnValue(of(mockClientes));
    jest.spyOn(window, 'confirm').mockReturnValue(true);

    component.eliminarCliente(1);

    expect(clienteService.delete).toHaveBeenCalledWith(1);
  });

  it('should handle error when loading clientes', () => {
    jest.spyOn(clienteService, 'getAll').mockReturnValue(
      throwError(() => ({ error: { message: 'Error de prueba' } }))
    );

    component.cargarClientes();

    expect(component.error).toBe('Error de prueba');
    expect(component.loading).toBe(false);
  });
});

