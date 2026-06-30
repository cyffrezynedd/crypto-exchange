import { DOCUMENT } from '@angular/common'
import {
  ApplicationRef,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EmbeddedViewRef,
  EventEmitter,
  HostListener,
  Inject,
  Input,
  OnDestroy,
  Output,
  Renderer2,
  TemplateRef,
  ViewChild,
  forwardRef,
} from '@angular/core'
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms'

export type SelectOption = { value: string; label: string }

type MenuPosition = {
  top: string
  left: string
  width: string
  minWidth: string
}

@Component({
  selector: 'app-sidebar-select',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SidebarSelectComponent),
      multi: true,
    },
  ],
  template: `
    <div
      class="ui-select ui-select--sidebar ui-select--block"
      [class.is-open]="open"
      [class.has-value]="!!value"
    >
      <button type="button" class="ui-select__trigger" (click)="toggle($event)">
        <span class="ui-select__value">{{ displayLabel }}</span>
      </button>
      <span class="ui-select__chevron" aria-hidden>
        <svg class="ui-select__chevron-icon" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path
            d="M6 9l6 6 6-6"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </span>
    </div>

    <ng-template #menuTpl>
      @if (menuPos) {
        <ul
          class="ui-select__menu ui-select__menu--sidebar scroll-hidden"
          role="listbox"
          [style.top]="menuPos.top"
          [style.left]="menuPos.left"
          [style.width]="menuPos.width"
          [style.minWidth]="menuPos.minWidth"
        >
          @for (option of options; track option.value) {
            <li role="presentation">
              <button
                type="button"
                role="option"
                class="ui-select__option"
                [class.is-selected]="isSelected(option)"
                [attr.aria-selected]="isSelected(option)"
                (mousedown)="$event.preventDefault()"
                (click)="select(option.value, $event)"
              >
                <span class="ui-select__option-label">{{ option.label }}</span>
                @if (isSelected(option)) {
                  <svg class="ui-select__option-check" width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
                    <path d="M3 8.5 6.5 12 13 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                }
              </button>
            </li>
          }
        </ul>
      }
    </ng-template>
  `,
  styles: `
    :host {
      display: block;
      width: 100%;
    }
  `,
})
export class SidebarSelectComponent implements ControlValueAccessor, OnDestroy {
  @Input() options: SelectOption[] = []
  @Input() placeholder = 'Выберите'
  @Output() valueChange = new EventEmitter<string>()

  @ViewChild('menuTpl') private menuTpl?: TemplateRef<unknown>

  value = ''
  open = false
  menuPos: MenuPosition | null = null

  private menuEl: HTMLElement | null = null
  private menuView: EmbeddedViewRef<unknown> | null = null
  private onChange: (v: string) => void = () => {}
  private onTouched: () => void = () => {}
  private readonly onReposition = () => this.updateMenuPosition()
  private readonly onDocMouseDown = (event: MouseEvent) => this.handleDocMouseDown(event)

  constructor(
    private readonly el: ElementRef<HTMLElement>,
    private readonly renderer: Renderer2,
    private readonly cdr: ChangeDetectorRef,
    private readonly appRef: ApplicationRef,
    @Inject(DOCUMENT) private readonly document: Document,
  ) {}

  get displayLabel() {
    return this.options.find((o) => this.isSelected(o))?.label ?? this.placeholder
  }

  isSelected(option: SelectOption) {
    return String(option.value) === String(this.value)
  }

  ngOnDestroy() {
    this.destroyMenu()
    this.detachRepositionListeners()
    this.detachDocListener()
  }

  @HostListener('document:keydown', ['$event'])
  onDocKey(event: KeyboardEvent) {
    if (this.open && event.key === 'Escape') this.close()
  }

  writeValue(value: string | null): void {
    this.value = value ?? ''
    this.menuView?.detectChanges()
    this.cdr.markForCheck()
  }

  registerOnChange(fn: (v: string) => void): void {
    this.onChange = fn
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn
  }

  toggle(event: MouseEvent) {
    event.stopPropagation()
    if (this.open) {
      this.close()
      return
    }
    this.open = true
    this.updateMenuPosition()
    this.renderMenu()
    this.attachRepositionListeners()
    this.attachDocListener()
    this.cdr.markForCheck()
  }

  select(next: string, event: MouseEvent) {
    event.preventDefault()
    event.stopPropagation()
    this.value = next
    this.onChange(next)
    this.onTouched()
    this.valueChange.emit(next)
    this.close()
    this.cdr.detectChanges()
  }

  private close() {
    this.open = false
    this.menuPos = null
    this.destroyMenu()
    this.detachRepositionListeners()
    this.detachDocListener()
    this.cdr.markForCheck()
  }

  private renderMenu() {
    if (!this.menuTpl || this.menuView) return

    this.menuView = this.menuTpl.createEmbeddedView({})
    this.appRef.attachView(this.menuView)
    this.menuView.detectChanges()
    this.syncMenuElement()

    if (!this.menuEl) return
    this.renderer.appendChild(this.document.body, this.menuEl)
  }

  private syncMenuElement() {
    this.menuEl = null
    if (!this.menuView) return

    for (const node of this.menuView.rootNodes) {
      if (!(node instanceof HTMLElement)) continue
      if (node.classList.contains('ui-select__menu')) {
        this.menuEl = node
        return
      }
      const nested = node.querySelector('.ui-select__menu')
      if (nested instanceof HTMLElement) {
        this.menuEl = nested
        return
      }
    }
  }

  private destroyMenu() {
    if (this.menuView) {
      this.appRef.detachView(this.menuView)
      this.menuView.destroy()
      this.menuView = null
    }
    this.menuEl = null
  }

  private updateMenuPosition() {
    const anchor =
      this.el.nativeElement.closest('.side-panel-field') ??
      this.el.nativeElement.querySelector<HTMLElement>('.ui-select__trigger')
    if (!anchor) return

    const rect = anchor.getBoundingClientRect()
    this.menuPos = {
      top: `${rect.bottom + 4}px`,
      left: `${rect.left}px`,
      width: `${rect.width}px`,
      minWidth: `${rect.width}px`,
    }
    this.menuView?.detectChanges()
  }

  private attachDocListener() {
    this.document.addEventListener('mousedown', this.onDocMouseDown)
  }

  private detachDocListener() {
    this.document.removeEventListener('mousedown', this.onDocMouseDown)
  }

  private handleDocMouseDown(event: MouseEvent) {
    if (!this.open) return
    const target = event.target as Node
    if (this.el.nativeElement.contains(target)) return
    if (this.menuEl?.contains(target)) return
    this.close()
  }

  private attachRepositionListeners() {
    window.addEventListener('scroll', this.onReposition, true)
    window.addEventListener('resize', this.onReposition)
    window.visualViewport?.addEventListener('resize', this.onReposition)
  }

  private detachRepositionListeners() {
    window.removeEventListener('scroll', this.onReposition, true)
    window.removeEventListener('resize', this.onReposition)
    window.visualViewport?.removeEventListener('resize', this.onReposition)
  }
}
