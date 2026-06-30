import { Component, Input } from '@angular/core'

@Component({
  selector: 'app-auth-card',
  standalone: true,
  template: `
    <div class="auth-card">
      <div class="auth-card-head">
        <h1 class="auth-card-title">{{ title }}</h1>
        <div class="auth-card-divider"></div>
      </div>
      <div class="auth-card-scroll">
        <ng-content />
      </div>
      <div class="auth-card-footer">
        <ng-content select="[auth-footer]" />
      </div>
    </div>
  `,
})
export class AuthCardComponent {
  @Input() title = ''
}
