export interface Reporte {
  Fecha: Date;
  Cliente: string;
  'Numero Cuenta': string;
  Tipo: string;
  'Saldo Inicial': number;
  Estado: boolean;
  Movimiento: number;
  'Saldo Disponible': number;
}

export interface ReporteRequest {
  fechaInicio: string;
  fechaFin: string;
  clienteId: number;
  formato: 'json' | 'pdf';
}

