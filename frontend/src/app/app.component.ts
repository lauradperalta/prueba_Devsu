import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="app-container">
      <app-header></app-header>
      <div class="main-container">
        <app-sidebar></app-sidebar>
        <main class="content">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .main-container {
      display: flex;
      flex: 1;
    }

    .content {
      flex: 1;
      padding: 2rem;
      overflow-y: auto;
    }
  `]
})
export class AppComponent {
  title = 'Sistema Bancario';
}

