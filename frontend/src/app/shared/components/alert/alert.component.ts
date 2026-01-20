import { Component, Input, Output, EventEmitter } from '@angular/core';

export type AlertType = 'success' | 'error' | 'warning' | 'info';

@Component({
  selector: 'app-alert',
  template: `
    <div *ngIf="show" [class]="'alert alert-' + type">
      <span>{{ message }}</span>
      <button class="alert-close" (click)="onClose()">×</button>
    </div>
  `,
  styles: [`
    .alert {
      position: relative;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .alert-close {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      padding: 0;
      margin-left: var(--spacing-md);
      opacity: 0.7;
    }

    .alert-close:hover {
      opacity: 1;
    }
  `]
})
export class AlertComponent {
  @Input() message: string = '';
  @Input() type: AlertType = 'info';
  @Input() show: boolean = false;
  @Output() showChange = new EventEmitter<boolean>();

  onClose() {
    this.show = false;
    this.showChange.emit(this.show);
  }
}

