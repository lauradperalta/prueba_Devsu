import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService } from './cliente.service';
import { Cliente } from '../models/cliente.model';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;

  const mockCliente: Cliente = {
    clienteId: 1,
    nombre: 'Jose Lema',
    genero: 'Masculino',
    edad: 35,
    identificacion: '1234567890',
    direccion: 'Otavalo sn y principal',
    telefono: '098254785',
    contrasena: '1234',
    estado: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all clientes', () => {
    const mockClientes: Cliente[] = [mockCliente];

    service.getAll().subscribe((clientes) => {
      expect(clientes).toEqual(mockClientes);
      expect(clientes.length).toBe(1);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/clientes');
    expect(req.request.method).toBe('GET');
    req.flush(mockClientes);
  });

  it('should create a cliente', () => {
    service.create(mockCliente).subscribe((cliente) => {
      expect(cliente).toEqual(mockCliente);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/clientes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockCliente);
    req.flush(mockCliente);
  });

  it('should delete a cliente', () => {
    const clienteId = 1;

    service.delete(clienteId).subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/clientes/${clienteId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

