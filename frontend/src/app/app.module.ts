import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Core
import { HeaderComponent } from './core/components/header/header.component';
import { SidebarComponent } from './core/components/sidebar/sidebar.component';

// Shared
import { SearchComponent } from './shared/components/search/search.component';
import { ModalComponent } from './shared/components/modal/modal.component';
import { AlertComponent } from './shared/components/alert/alert.component';

// Features - Clientes
import { ClienteListComponent } from './features/clientes/cliente-list/cliente-list.component';
import { ClienteFormComponent } from './features/clientes/cliente-form/cliente-form.component';

// Features - Cuentas
import { CuentaListComponent } from './features/cuentas/cuenta-list/cuenta-list.component';
import { CuentaFormComponent } from './features/cuentas/cuenta-form/cuenta-form.component';

// Features - Movimientos
import { MovimientoListComponent } from './features/movimientos/movimiento-list/movimiento-list.component';
import { MovimientoFormComponent } from './features/movimientos/movimiento-form/movimiento-form.component';

// Features - Reportes
import { ReporteViewComponent } from './features/reportes/reporte-view/reporte-view.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SidebarComponent,
    SearchComponent,
    ModalComponent,
    AlertComponent,
    ClienteListComponent,
    ClienteFormComponent,
    CuentaListComponent,
    CuentaFormComponent,
    MovimientoListComponent,
    MovimientoFormComponent,
    ReporteViewComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

