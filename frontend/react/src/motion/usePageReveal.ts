import { useLayoutEffect } from 'react'
import gsap from 'gsap'
import { prefersReducedMotion } from './prefersReducedMotion'

type Options = {
  root: React.RefObject<HTMLElement | null>
  resetKey?: string
}

export function usePageReveal({ root, resetKey }: Options) {
  useLayoutEffect(() => {
    const el = root.current
    if (!el || prefersReducedMotion()) return

    const ctx = gsap.context(() => {
      const blocks = el.querySelectorAll<HTMLElement>('[data-reveal]')

      gsap.fromTo(
        el,
        { autoAlpha: 0, y: 16 },
        { autoAlpha: 1, y: 0, duration: 0.45, ease: 'expo.out' },
      )

      if (blocks.length > 0) {
        gsap.fromTo(
          blocks,
          { autoAlpha: 0, y: 12 },
          {
            autoAlpha: 1,
            y: 0,
            duration: 0.35,
            stagger: 0.055,
            ease: 'power2.out',
            delay: 0.12,
          },
        )
      }
    }, el)

    return () => ctx.revert()
  }, [resetKey])
}
