import { Component, inject } from '@angular/core'
import { ToastService } from '../core/toast.service'

@Component({
  selector: 'app-toast-host',
  standalone: true,
  template: `
    @if (toast.items().length > 0) {
      <div class="toast-host" aria-live="polite">
        @for (item of toast.items(); track item.id) {
          <div
            class="toast-item toast-item--{{ item.variant }}"
            [class.toast-item--has-title]="!!item.title"
            [class.toast-item--solo]="!item.title"
            [class.is-fade-out]="item.isFadeOut"
            role="alert"
          >
            <span class="toast-item__icon" aria-hidden>
              @if (item.variant === 'success') {
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <path d="M20 6 9 17l-5-5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              } @else {
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.5" />
                  <path d="M15 9 9 15M9 9l6 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                </svg>
              }
            </span>
            <div class="toast-item__body">
              @if (item.title) {
                <p class="toast-item__title">{{ item.title }}</p>
              }
              <p [class]="item.title ? 'toast-item__text' : 'toast-item__text toast-item__text--solo'">{{ item.text }}</p>
            </div>
            <button type="button" class="toast-item__close" aria-label="Закрыть" (click)="toast.dismiss(item.id)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <path d="M6 6l12 12M18 6 6 18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
              </svg>
            </button>
          </div>
        }
      </div>
    }
  `,
})
export class ToastHostComponent {
  readonly toast = inject(ToastService)
}
