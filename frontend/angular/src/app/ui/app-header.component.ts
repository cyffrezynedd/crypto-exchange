import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core'
import { RouterLink } from '@angular/router'

@Component({
  selector: 'app-app-header',
  standalone: true,
  imports: [RouterLink],
  template: `
    <header class="app-header">
      <a class="app-header__brand brand-logo" routerLink="/" aria-label="Crypto Exchange">
        <img src="/favicon.svg" alt="" class="brand-logo__mark" aria-hidden="true" />
        <span class="brand-logo__text">CryptoX</span>
      </a>

      <div class="user-menu">
        <button type="button" class="user-menu__trigger" (click)="open = !open">
          <span class="user-menu__avatar">{{ initial }}</span>
          <span class="user-menu__name">{{ username }}</span>
        </button>

        @if (open) {
          <div class="user-menu__panel">
            <div class="user-menu__profile">
              <span class="user-menu__avatar user-menu__avatar--lg">{{ initial }}</span>
              <div>
                <div class="user-menu__name user-menu__name--lg">{{ username }}</div>
                <div class="user-menu__meta">CryptoX</div>
              </div>
            </div>
            <button type="button" class="user-menu__action" (click)="logout.emit(); open = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
              </svg>
              Выйти
            </button>
          </div>
        }
      </div>
    </header>
  `,
})
export class AppHeaderComponent {
  @Input() username = ''
  @Output() logout = new EventEmitter<void>()

  open = false

  get initial() {
    return (this.username.trim()[0] ?? 'U').toUpperCase()
  }

  @HostListener('document:mousedown', ['$event'])
  onDocClick(event: MouseEvent) {
    const target = event.target as HTMLElement
    if (!target.closest('.user-menu')) this.open = false
  }
}
