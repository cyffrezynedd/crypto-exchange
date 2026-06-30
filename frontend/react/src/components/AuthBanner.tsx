import { forwardRef } from 'react'
import { BrandLogo } from './BrandLogo'

export const AuthBanner = forwardRef<HTMLElement>(function AuthBanner(_, ref) {
  return (
    <>
      <div className="auth-page__mobile-head">
        <BrandLogo />
      </div>

      <aside ref={ref} className="auth-page__hero" aria-hidden={false}>
        <img src="/favicon.svg" alt="" className="auth-page__hero-mark" aria-hidden />
        <div className="auth-page__hero-content">
          <h1 className="auth-page__hero-title">
            Crypto
            <br />
            Exchange
          </h1>
          <p className="auth-page__hero-subtitle">
            Торгуй спокойно. Следи за рынком. Держи всё под контролем.
          </p>
        </div>
      </aside>
    </>
  )
})
