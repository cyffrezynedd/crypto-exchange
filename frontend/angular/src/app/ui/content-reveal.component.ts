import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  ViewChild,
} from '@angular/core'
import gsap from 'gsap'
import { prefersReducedMotion } from '../motion/prefers-reduced-motion'

@Component({
  selector: 'app-content-reveal',
  standalone: true,
  template: `<div class="content-reveal" #host><ng-content /></div>`,
})
export class ContentRevealComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input({ required: true }) resetKey!: string
  @ViewChild('host', { static: true }) host!: ElementRef<HTMLElement>

  private lastAnimatedKey: string | null = null
  private ctx?: gsap.Context

  ngAfterViewInit() {
    this.animate()
  }

  ngOnChanges() {
    queueMicrotask(() => this.animate())
  }

  ngOnDestroy() {
    this.ctx?.revert()
  }

  private animate() {
    const el = this.host?.nativeElement
    if (!el || prefersReducedMotion()) return
    if (this.lastAnimatedKey === this.resetKey) return

    const items = el.querySelectorAll<HTMLElement>('tbody tr, .ui-empty, .wallet-card')
    const isInitial = this.lastAnimatedKey === null

    if (isInitial || items.length === 0) {
      this.lastAnimatedKey = this.resetKey
      return
    }

    this.lastAnimatedKey = this.resetKey

    this.ctx?.revert()
    this.ctx = gsap.context(() => {
      gsap.fromTo(
        el,
        { autoAlpha: 0 },
        { autoAlpha: 1, duration: 0.22, ease: 'power2.out', overwrite: 'auto' },
      )

      gsap.fromTo(
        items,
        { autoAlpha: 0 },
        {
          autoAlpha: 1,
          duration: 0.18,
          stagger: 0.025,
          ease: 'power2.out',
          delay: 0.04,
          overwrite: 'auto',
        },
      )
    }, el)
  }
}
