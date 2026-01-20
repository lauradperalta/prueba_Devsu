import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-search',
  template: `
    <div class="search-container">
      <input 
        type="text"
        [placeholder]="placeholder"
        [(ngModel)]="searchTerm"
        (input)="onSearch()"
        class="search-input">
      <span class="search-icon"></span>
    </div>
  `,
  styles: [`
    .search-container {
      position: relative;
      margin-bottom: var(--spacing-lg);
    }

    .search-input {
      padding-left: 2.5rem;
    }

    .search-icon {
      position: absolute;
      left: 0.75rem;
      top: 50%;
      transform: translateY(-50%);
      pointer-events: none;
    }
  `]
})
export class SearchComponent {
  @Input() placeholder: string = 'Buscar...';
  @Output() search = new EventEmitter<string>();

  searchTerm: string = '';

  onSearch() {
    this.search.emit(this.searchTerm);
  }
}

