import { Component, forwardRef, Input } from '@angular/core'
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms'

@Component({
  selector: 'app-field',
  standalone: true,
  imports: [FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => FieldComponent),
      multi: true,
    },
  ],
  template: `
    <div class="field-wrap">
      <label
        class="field-shell"
        [class.is-focused]="focused"
        [class.is-error]="!!error"
        [class.is-password]="isPassword"
      >
        <span class="field-label" [class.is-floated]="focused || value">{{ label }}</span>
        <div class="field-input-row">
          <input
            class="field-input"
            [type]="isPassword && !visible ? 'password' : type"
            [attr.autocomplete]="autocomplete"
            [attr.minlength]="minlength"
            [attr.maxlength]="maxlength"
            [required]="required"
            [value]="value"
            (input)="onInput($event)"
            (focus)="focused = true"
            (blur)="onBlur()"
          />
          @if (isPassword) {
            <button
              type="button"
              class="field-eye"
              tabindex="-1"
              [attr.aria-label]="visible ? 'Скрыть пароль' : 'Показать пароль'"
              (click)="visible = !visible"
            >
              @if (visible) {
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                  <path d="m2 2 20 20M6.7 6.7A10.7 10.7 0 0 0 2 12s3.5 7 10 7c2 0 3.8-.7 5.2-1.8M10.6 10.6a3 3 0 0 0 4.2 4.2M17.3 17.3A10.7 10.7 0 0 0 22 12s-3.5-7-10-7c-1.1 0-2.1.2-3 .5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                </svg>
              } @else {
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                  <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z" stroke="currentColor" stroke-width="1.5" />
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" />
                </svg>
              }
            </button>
          }
        </div>
      </label>
      @if (error) {
        <p class="auth-error">{{ error }}</p>
      }
    </div>
  `,
})
export class FieldComponent implements ControlValueAccessor {
  @Input() label = ''
  @Input() type = 'text'
  @Input() isPassword = false
  @Input() error = ''
  @Input() autocomplete?: string
  @Input() minlength?: number
  @Input() maxlength?: number
  @Input() required = false

  value = ''
  focused = false
  visible = false
  private onChange: (v: string) => void = () => {}
  private onTouched: () => void = () => {}

  writeValue(value: string | null): void {
    this.value = value ?? ''
  }

  registerOnChange(fn: (v: string) => void): void {
    this.onChange = fn
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn
  }

  onInput(event: Event): void {
    const next = (event.target as HTMLInputElement).value
    this.value = next
    this.onChange(next)
  }

  onBlur(): void {
    this.focused = false
    this.onTouched()
  }
}
