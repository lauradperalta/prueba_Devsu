export interface Movimiento {
  id?: number;
  fecha?: Date;
  tipoMovimiento: string;
  valor: number;
  saldo?: number;
  numeroCuenta: string;
}

