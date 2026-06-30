import { useLayoutEffect } from 'react'
import gsap from 'gsap'
import { prefersReducedMotion } from './prefersReducedMotion'

type Options = {
  bannerRef: React.RefObject<HTMLElement | null>
  formRef: React.RefObject<HTMLElement | null>
  resetKey?: string
}

export function useAuthReveal({ bannerRef, formRef, resetKey }: Options) {
  useLayoutEffect(() => {
    if (prefersReducedMotion()) return

    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })
      const hero = bannerRef.current
      const form = formRef.current
      const mobileHead = document.querySelector<HTMLElement>('.auth-page__mobile-head')

      if (mobileHead && window.matchMedia('(max-width: 899px)').matches) {
        tl.fromTo(
          mobileHead,
          { autoAlpha: 0, y: -10 },
          { autoAlpha: 1, y: 0, duration: 0.38 },
          0,
        )
      }

      if (hero) {
        tl.fromTo(
          hero,
          { autoAlpha: 0, scale: 0.97 },
          { autoAlpha: 1, scale: 1, duration: 0.65 },
          0,
        )

        const mark = hero.querySelector<HTMLElement>('.auth-page__hero-mark')
        const title = hero.querySelector<HTMLElement>('.auth-page__hero-title')
        const subtitle = hero.querySelector<HTMLElement>('.auth-page__hero-subtitle')

        if (mark) {
          tl.fromTo(
            mark,
            { autoAlpha: 0, scale: 0.88 },
            { autoAlpha: 0.34, scale: 1, duration: 0.75, ease: 'power3.out' },
            0.08,
          )
        }

        if (title) {
          tl.fromTo(title, { autoAlpha: 0, y: 18 }, { autoAlpha: 1, y: 0, duration: 0.45 }, 0.22)
        }
        if (subtitle) {
          tl.fromTo(subtitle, { autoAlpha: 0, y: 12 }, { autoAlpha: 1, y: 0, duration: 0.4 }, 0.3)
        }
      }

      if (form) {
        tl.fromTo(
          form,
          { autoAlpha: 0, y: 28, scale: 0.98 },
          { autoAlpha: 1, y: 0, scale: 1, duration: 0.6, ease: 'expo.out' },
          0.18,
        )

        const items = form.querySelectorAll<HTMLElement>('[data-reveal]')
        if (items.length > 0) {
          tl.fromTo(
            items,
            { autoAlpha: 0, y: 12 },
            { autoAlpha: 1, y: 0, duration: 0.35, stagger: 0.055, ease: 'power2.out' },
            0.38,
          )
        }
      }
    })

    return () => ctx.revert()
  }, [resetKey, bannerRef, formRef])
}
