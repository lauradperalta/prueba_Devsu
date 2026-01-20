import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reporte } from '../models/reporte.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${environment.apiUrl}/reportes`;

  constructor(private http: HttpClient) {}

  generarReporte(fechaInicio: string, fechaFin: string, clienteId: number): Observable<Reporte[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin)
      .set('clienteId', clienteId.toString())
      .set('formato', 'json');

    return this.http.get<Reporte[]>(this.apiUrl, { params });
  }

  descargarPDF(fechaInicio: string, fechaFin: string, clienteId: number): Observable<any> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin)
      .set('clienteId', clienteId.toString())
      .set('formato', 'pdf');

    return this.http.get<any>(this.apiUrl, { params });
  }
}

