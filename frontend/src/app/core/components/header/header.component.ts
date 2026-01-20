import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  template: `
    <header class="header">
      <div class="header-container">
        <h1 class="header-title">Sistema Bancario Devsu</h1>
        <div class="header-actions">
          <span class="user-info">Usuario: Admin</span>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .header {
      background-color: var(--primary-color);
      color: var(--white);
      box-shadow: var(--shadow-md);
      position: sticky;
      top: 0;
      z-index: 100;
    }

    .header-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }

    .header-title {
      font-size: 1.5rem;
      font-weight: 700;
      margin: 0;
    }

    .user-info {
      font-size: 0.875rem;
    }
  `]
})
export class HeaderComponent {}

