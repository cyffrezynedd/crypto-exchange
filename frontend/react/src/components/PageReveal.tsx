import { useRef, type ReactNode } from 'react'
import { useLocation } from 'react-router-dom'
import { usePageReveal } from '../motion/usePageReveal'

type Props = {
  children: ReactNode
  className?: string
}

export function PageReveal({ children, className }: Props) {
  const ref = useRef<HTMLDivElement>(null)
  const location = useLocation()

  usePageReveal({ root: ref, resetKey: location.pathname })

  return (
    <div ref={ref} className={className}>
      {children}
    </div>
  )
}
