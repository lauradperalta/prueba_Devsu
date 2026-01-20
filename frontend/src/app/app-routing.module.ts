import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ClienteListComponent } from './features/clientes/cliente-list/cliente-list.component';
import { CuentaListComponent } from './features/cuentas/cuenta-list/cuenta-list.component';
import { MovimientoListComponent } from './features/movimientos/movimiento-list/movimiento-list.component';
import { ReporteViewComponent } from './features/reportes/reporte-view/reporte-view.component';

const routes: Routes = [
  { path: '', redirectTo: '/clientes', pathMatch: 'full' },
  { path: 'clientes', component: ClienteListComponent },
  { path: 'cuentas', component: CuentaListComponent },
  { path: 'movimientos', component: MovimientoListComponent },
  { path: 'reportes', component: ReporteViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

