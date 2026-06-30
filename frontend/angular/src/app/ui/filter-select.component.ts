import { Component, EventEmitter, HostListener, Input, Output, forwardRef } from '@angular/core'
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms'

export type SelectOption = { value: string; label: string }

@Component({
  selector: 'app-filter-select',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => FilterSelectComponent),
      multi: true,
    },
  ],
  template: `
    <div
      class="ui-select ui-select--filter"
      [class.is-open]="open"
      [class.has-value]="!!value"
    >
      <button
        type="button"
        class="ui-select__trigger"
        [attr.aria-label]="label"
        (click)="open = !open"
      >
        <span class="ui-select__value" [class.is-placeholder]="!value">{{ displayText }}</span>
        <span class="ui-select__chevron" aria-hidden>
          <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
            <path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
          </svg>
        </span>
      </button>
      @if (open) {
        <ul class="ui-select__menu ui-select__menu--filter scroll-hidden" role="listbox">
          @for (option of options; track option.value) {
            <li role="presentation">
              <button
                type="button"
                role="option"
                class="ui-select__option"
                [class.is-selected]="option.value === value"
                (click)="select(option.value)"
              >
                <span class="ui-select__option-label">{{ option.label }}</span>
              </button>
            </li>
          }
        </ul>
      }
    </div>
  `,
  styles: `
    :host {
      display: inline-flex;
      max-width: 100%;
    }

    .ui-select {
      position: relative;
    }

    .ui-select__menu {
      position: absolute;
      top: calc(100% + 4px);
      left: 0;
      z-index: 40;
      min-width: 100%;
      margin: 0;
    }
  `,
})
export class FilterSelectComponent implements ControlValueAccessor {
  @Input() label = ''
  @Input() options: SelectOption[] = []
  @Output() valueChange = new EventEmitter<string>()

  value = ''
  open = false
  private onChange: (v: string) => void = () => {}
  private onTouched: () => void = () => {}

  get displayText() {
    if (!this.value) return this.label
    return this.options.find((o) => o.value === this.value)?.label ?? this.label
  }

  @HostListener('document:mousedown', ['$event'])
  onDocClick(event: MouseEvent) {
    const target = event.target as HTMLElement
    if (!target.closest('.ui-select')) this.open = false
  }

  writeValue(value: string | null): void {
    this.value = value ?? ''
  }

  registerOnChange(fn: (v: string) => void): void {
    this.onChange = fn
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn
  }

  select(next: string) {
    this.value = next
    this.onChange(next)
    this.onTouched()
    this.valueChange.emit(next)
    this.open = false
  }
}
