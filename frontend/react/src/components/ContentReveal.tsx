import { useRef, type ReactNode } from 'react'
import { useContentReveal } from '../motion/useContentReveal'

type Props = {
  children: ReactNode
  resetKey: string
  className?: string
}

export function ContentReveal({ children, resetKey, className }: Props) {
  const ref = useRef<HTMLDivElement>(null)

  useContentReveal({ root: ref, resetKey })

  return (
    <div ref={ref} className={['content-reveal', className ?? ''].filter(Boolean).join(' ')}>
      {children}
    </div>
  )
}
