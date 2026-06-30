import type { HTMLAttributes } from 'react'

type Props = HTMLAttributes<HTMLDivElement> & {
  compact?: boolean
}

export function BrandLogo({ compact, className = '', ...rest }: Props) {
  return (
    <div className={`flex items-center gap-2.5 ${className}`} {...rest}>
      <img src="/favicon.svg" alt="" className={compact ? 'h-8 w-8' : 'h-9 w-9'} aria-hidden />
      <span
        className={[
          'font-stapel font-medium tracking-[0.4px] text-base',
          compact ? 'text-lg' : 'text-xl',
        ].join(' ')}
      >
        CryptoX
      </span>
    </div>
  )
}
