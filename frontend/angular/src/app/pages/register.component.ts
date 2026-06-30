import { Component, inject } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { Router, RouterLink } from '@angular/router'
import { AuthService } from '../core/auth.service'
import { AuthCardComponent } from '../ui/auth-card.component'
import { AuthShellComponent } from '../ui/auth-shell.component'
import { FieldComponent } from '../ui/field.component'

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, FieldComponent, AuthCardComponent, AuthShellComponent],
  template: `
    <app-auth-shell>
      <app-auth-card title="Регистрация">
        <form class="auth-form" (ngSubmit)="submit()">
          <app-field label="Электронная почта" type="email" [(ngModel)]="email" name="email" autocomplete="email" [required]="true" />
          <app-field label="Никнейм" [(ngModel)]="username" name="username" autocomplete="username" [maxlength]="64" [required]="true" />
          <app-field label="Пароль" [isPassword]="true" [(ngModel)]="password" name="password" autocomplete="new-password" [minlength]="8" [required]="true" />
          @if (error) {
            <p class="auth-error">{{ error }}</p>
          }
          <button type="submit" class="q-btn q-btn-primary q-btn-block" [disabled]="pending">
            {{ pending ? 'Создание…' : 'Создать аккаунт' }}
          </button>
        </form>
        <p auth-footer>Уже есть аккаунт? <a routerLink="/login">Войти</a></p>
      </app-auth-card>
    </app-auth-shell>
  `,
})
export class RegisterComponent {
  private readonly auth = inject(AuthService)
  private readonly router = inject(Router)
  email = ''
  username = ''
  password = ''
  error = ''
  pending = false

  async submit() {
    this.pending = true
    this.error = ''
    try {
      await this.auth.register(this.email, this.username, this.password)
      await this.router.navigateByUrl('/')
    } catch {
      this.error = 'Не удалось зарегистрироваться'
    } finally {
      this.pending = false
    }
  }
}
