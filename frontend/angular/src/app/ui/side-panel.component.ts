import { DOCUMENT } from '@angular/common'
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  Renderer2,
  SimpleChanges,
  ViewEncapsulation,
} from '@angular/core'

@Component({
  selector: 'app-side-panel',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  host: { class: 'side-panel-host' },
  template: `
    @if (mounted) {
      <div
        class="side-panel__root side-panel__root--flush-bottom"
        [class.is-open]="shown"
        role="presentation"
        (click)="onClose.emit()"
      >
        <div class="side-panel__backdrop" aria-hidden></div>
        <aside
          class="side-panel__panel"
          role="dialog"
          aria-modal="true"
          [attr.aria-label]="title"
          (click)="$event.stopPropagation()"
        >
          <div class="side-panel__head">
            <h2 class="side-panel__title">{{ title }}</h2>
            <button type="button" class="side-panel__close" aria-label="Закрыть" (click)="onClose.emit()">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <path d="M6 6l12 12M18 6 6 18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
              </svg>
            </button>
          </div>
          <div class="side-panel__body scroll-hidden">
            <ng-content />
          </div>
          @if (primaryLabel) {
            <div class="side-panel__footer">
              <div class="side-panel__actions">
                <button type="button" class="q-btn q-btn-ghost q-btn-block side-panel__cancel" (click)="onClose.emit()">
                  {{ cancelLabel }}
                </button>
                <button type="submit" class="q-btn q-btn-primary q-btn-block" [attr.form]="primaryFormId">
                  {{ primaryLabel }}
                </button>
              </div>
            </div>
          }
        </aside>
      </div>
    }
  `,
})
export class SidePanelComponent implements OnChanges, OnDestroy {
  @Input({ required: true }) title!: string
  @Input() isOpen = false
  @Input() primaryLabel = ''
  @Input() primaryFormId = ''
  @Input() cancelLabel = 'Отменить'
  @Output() onClose = new EventEmitter<void>()

  mounted = false
  shown = false

  private portaled = false
  private bodyOverflow = ''
  private closeTimer: ReturnType<typeof setTimeout> | null = null

  constructor(
    private readonly host: ElementRef<HTMLElement>,
    private readonly renderer: Renderer2,
    private readonly cdr: ChangeDetectorRef,
    @Inject(DOCUMENT) private readonly document: Document,
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    if (!('isOpen' in changes)) return

    if (this.isOpen) {
      if (this.closeTimer) {
        clearTimeout(this.closeTimer)
        this.closeTimer = null
      }
      this.mounted = true
      this.cdr.detectChanges()
      this.portalToBody()
      this.lockBodyScroll()
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          this.shown = true
        })
      })
      return
    }

    this.shown = false
    this.unlockBodyScroll()
    this.closeTimer = setTimeout(() => {
      this.mounted = false
      this.closeTimer = null
    }, 200)
  }

  ngOnDestroy() {
    if (this.closeTimer) clearTimeout(this.closeTimer)
    this.unlockBodyScroll()
    if (this.portaled && this.host.nativeElement.parentNode === this.document.body) {
      this.renderer.removeChild(this.document.body, this.host.nativeElement)
    }
  }

  private portalToBody() {
    if (this.portaled || this.host.nativeElement.parentNode === this.document.body) return
    this.renderer.appendChild(this.document.body, this.host.nativeElement)
    this.portaled = true
  }

  private lockBodyScroll() {
    this.bodyOverflow = this.document.body.style.overflow
    this.document.body.style.overflow = 'hidden'
  }

  private unlockBodyScroll() {
    this.document.body.style.overflow = this.bodyOverflow
  }
}
