import { Component, ViewEncapsulation } from '@angular/core'

@Component({
  selector: 'app-auth-shell',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  host: {
    class: 'auth-page',
  },
  template: `
    <div class="auth-page__grid">
      <div class="auth-page__mobile-head">
        <div class="brand-logo">
          <img src="/favicon.svg" alt="" class="brand-logo__mark" aria-hidden="true" />
          <span class="brand-logo__text">CryptoX</span>
        </div>
      </div>

      <aside class="auth-page__hero">
        <img src="/favicon.svg" alt="" class="auth-page__hero-mark" aria-hidden="true" />
        <div class="auth-page__hero-content">
          <h1 class="auth-page__hero-title">Crypto<br />Exchange</h1>
          <p class="auth-page__hero-subtitle">Торгуй спокойно. Следи за рынком. Держи всё под контролем.</p>
        </div>
      </aside>

      <div class="auth-page__form">
        <div class="auth-page__form-inner">
          <ng-content />
        </div>
      </div>
    </div>
  `,
})
export class AuthShellComponent {}
