import { Component, OnInit, inject } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { AuthService } from '../core/auth.service'
import { CURRENCY_LABELS } from '../core/constants'
import { Wallet } from '../core/models'
import { SidePanelComponent } from '../ui/side-panel.component'
import { SidePanelFieldComponent } from '../ui/side-panel-field.component'
import { SidebarSelectComponent } from '../ui/sidebar-select.component'
import { ContentRevealComponent } from '../ui/content-reveal.component'
import { PageTitleComponent } from '../ui/page-title.component'

const CURRENCY_OPTIONS = [
  { value: '3', label: 'USDT' },
  { value: '1', label: 'BTC' },
  { value: '2', label: 'ETH' },
]

@Component({
  selector: 'app-wallets',
  standalone: true,
  imports: [FormsModule, SidePanelComponent, SidePanelFieldComponent, SidebarSelectComponent, ContentRevealComponent, PageTitleComponent],
  template: `
    <div class="page-shell">
      <app-page-title title="Кошельки" icon="wallet">
        <button page-action type="button" class="q-btn q-btn-primary" (click)="openDeposit()">Пополнить</button>
      </app-page-title>

      <div class="page-body scroll-hidden">
        <div class="page-divider" aria-hidden></div>
        <section class="card panel panel--grow">
          <div class="panel__head">
            <h2 class="panel__title">Балансы</h2>
            <span class="stat-chip">Валют <strong>{{ wallets.length }}</strong></span>
          </div>
          <div class="panel__body panel__body--scroll" [class.panel__body--center]="isEmpty">
            <app-content-reveal [resetKey]="walletsContentKey">
            @if (isEmpty) {
              <div class="ui-empty">
                <div class="ui-empty__text">
                  <p class="ui-empty__title">Здесь будут ваши балансы</p>
                  <p class="ui-empty__subtitle">Пополните кошелёк USDT, чтобы начать торговать</p>
                </div>
                <div class="ui-empty__actions">
                  <button type="button" class="q-btn q-btn-primary" (click)="openDeposit()">Пополнить USDT</button>
                </div>
              </div>
            }
            @if (wallets.length > 0) {
              <div class="wallet-grid">
                @for (w of wallets; track w.id) {
                  <article class="wallet-card">
                    <span class="wallet-code">{{ label(w.currencyId) }}</span>
                    <strong>{{ w.availableBalance }}</strong>
                    <small>Заблокировано: {{ w.lockedBalance }}</small>
                  </article>
                }
              </div>
            }
            </app-content-reveal>
          </div>
        </section>
      </div>
    </div>

    <app-side-panel
      title="Депозит"
      [isOpen]="depositOpen"
      primaryLabel="Пополнить"
      primaryFormId="deposit-form"
      (onClose)="depositOpen = false"
    >
      <form id="deposit-form" class="stack-form side-panel-form" (ngSubmit)="deposit()">
        <app-side-panel-field label="Валюта">
          <app-sidebar-select
            [options]="currencyOptions"
            [(ngModel)]="currencyId"
            name="currencyId"
          />
        </app-side-panel-field>
        <app-side-panel-field label="Сумма">
          <input class="side-panel-field__input" [(ngModel)]="amount" name="amount" />
        </app-side-panel-field>
        @if (message) {
          <p class="ui-alert" [class.ui-alert--success]="messageVariant === 'success'" [class.ui-alert--danger]="messageVariant === 'danger'">
            {{ message }}
          </p>
        }
      </form>
    </app-side-panel>
  `,
})
export class WalletsComponent implements OnInit {
  private readonly auth = inject(AuthService)

  readonly currencyOptions = CURRENCY_OPTIONS

  wallets: Wallet[] = []
  currencyId = '3'
  amount = '1000'
  depositOpen = false
  message = ''
  messageVariant: 'success' | 'danger' = 'success'
  loading = true

  get walletsContentKey() {
    return this.wallets.map((w) => w.id).join(',') || (this.loading ? 'loading' : 'empty')
  }

  get isEmpty() {
    return !this.loading && this.wallets.length === 0
  }

  ngOnInit() {
    void this.load()
  }

  label(id: number) {
    return CURRENCY_LABELS[id] ?? id
  }

  async load() {
    try {
      this.wallets = await this.auth.wallets()
    } catch {
      this.wallets = []
    } finally {
      this.loading = false
    }
  }

  openDeposit() {
    this.message = ''
    this.depositOpen = true
  }

  async deposit() {
    this.message = ''
    try {
      await this.auth.deposit({
        eventId: crypto.randomUUID(),
        currencyId: Number(this.currencyId),
        amount: this.amount,
      })
      this.messageVariant = 'success'
      this.message = 'Депозит зачислен'
      await this.load()
      this.depositOpen = false
    } catch (err) {
      this.messageVariant = 'danger'
      this.message = err instanceof Error ? err.message : 'Ошибка депозита'
    }
  }
}
