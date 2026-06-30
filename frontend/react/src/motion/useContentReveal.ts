import { useLayoutEffect, useRef } from 'react'
import gsap from 'gsap'
import { prefersReducedMotion } from './prefersReducedMotion'

type Options = {
  root: React.RefObject<HTMLElement | null>
  resetKey: string
}

export function useContentReveal({ root, resetKey }: Options) {
  const lastAnimatedKey = useRef<string | null>(null)

  useLayoutEffect(() => {
    const el = root.current
    if (!el || prefersReducedMotion()) return

    if (lastAnimatedKey.current === resetKey) return

    const items = el.querySelectorAll<HTMLElement>('tbody tr, .ui-empty, .wallet-card')
    const isInitial = lastAnimatedKey.current === null

    if (isInitial || items.length === 0) {
      lastAnimatedKey.current = resetKey
      return
    }

    lastAnimatedKey.current = resetKey

    const ctx = gsap.context(() => {
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

    return () => ctx.revert()
  }, [resetKey])
}
