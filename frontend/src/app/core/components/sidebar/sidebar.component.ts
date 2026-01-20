import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  template: `
    <aside class="sidebar">
      <nav class="sidebar-nav">
        <a 
          routerLink="/clientes" 
          routerLinkActive="active"
          class="sidebar-link">
          <span class="icon"></span>
          <span>Clientes</span>
        </a>
        <a 
          routerLink="/cuentas" 
          routerLinkActive="active"
          class="sidebar-link">
          <span class="icon"></span>
          <span>Cuentas</span>
        </a>
        <a 
          routerLink="/movimientos" 
          routerLinkActive="active"
          class="sidebar-link">
          <span class="icon"></span>
          <span>Movimientos</span>
        </a>
        <a 
          routerLink="/reportes" 
          routerLinkActive="active"
          class="sidebar-link">
          <span class="icon"></span>
          <span>Reportes</span>
        </a>
      </nav>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      background-color: var(--white);
      border-right: 1px solid var(--border-color);
      min-height: calc(100vh - 60px);
    }

    .sidebar-nav {
      padding: var(--spacing-lg) 0;
    }

    .sidebar-link {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      padding: var(--spacing-md) var(--spacing-lg);
      color: var(--text-color);
      text-decoration: none;
      transition: all 0.2s;
    }

    .sidebar-link:hover {
      background-color: var(--gray-50);
    }

    .sidebar-link.active {
      background-color: var(--primary-light);
      color: var(--white);
      border-right: 4px solid var(--primary-dark);
    }

    .icon {
      font-size: 1.25rem;
    }
  `]
})
export class SidebarComponent {}

