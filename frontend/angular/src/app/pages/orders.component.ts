import { FormsModule } from '@angular/forms'
import { Component, OnInit, inject } from '@angular/core'
import { Router } from '@angular/router'
import { AuthService } from '../core/auth.service'
import { PAIR_IDS, PAIR_LABELS } from '../core/constants'
import { Order, PageResponse } from '../core/models'
import { FilterSelectComponent } from '../ui/filter-select.component'
import { PaginationComponent } from '../ui/pagination.component'
import { ContentRevealComponent } from '../ui/content-reveal.component'
import { PageTitleComponent } from '../ui/page-title.component'

const ORDERS_PAGE_SIZE = 7

const EMPTY_ORDERS: PageResponse<Order> = {
  content: [],
  page: 0,
  size: ORDERS_PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
}

const SIDE_LABELS: Record<'BUY' | 'SELL', string> = {
  BUY: 'Покупка',
  SELL: 'Продажа',
}

const STATUS_LABELS: Record<string, string> = {
  NEW: 'Новый',
  PARTIALLY_FILLED: 'Частично исполнен',
  FILLED: 'Исполнен',
  CANCELLED: 'Отменён',
}

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [FormsModule, FilterSelectComponent, PaginationComponent, ContentRevealComponent, PageTitleComponent],
  template: `
    <div class="page-shell">
      <app-page-title title="Ордера" icon="note-stack">
        <div page-aside class="stat-chips">
          <span class="stat-chip">Всего <strong>{{ data.totalElements }}</strong></span>
          <span class="stat-chip">Активные <strong>{{ activeCount }}</strong></span>
        </div>
      </app-page-title>

      <div class="page-body scroll-hidden">
        <div class="page-divider" aria-hidden></div>
        <section class="card panel">
          <div class="filters-bar">
            <app-filter-select
              label="Вид"
              [options]="sideOptions"
              [ngModel]="side"
              (ngModelChange)="onSideFilter($event)"
            />
            <app-filter-select
              label="Статус"
              [options]="statusOptions"
              [ngModel]="status"
              (ngModelChange)="onStatusFilter($event)"
            />
            <app-filter-select
              label="Торговая пара"
              [options]="pairOptions"
              [ngModel]="tradingPairId"
              (ngModelChange)="onTradingPairFilter($event)"
            />
          </div>
        </section>

        <section class="card panel panel--grow orders-panel">
          <div class="panel__body panel__body--scroll" [class.panel__body--center]="isEmpty">
            <app-content-reveal [resetKey]="ordersContentKey">
            @if (isEmpty) {
              <div class="ui-empty">
                <div class="ui-empty__text">
                  <p class="ui-empty__title">Здесь будут ваши ордера</p>
                  <p class="ui-empty__subtitle">Перейдите на рынок и разместите первый ордер</p>
                </div>
                <div class="ui-empty__actions">
                  <button type="button" class="q-btn q-btn-primary" (click)="goMarket()">На рынок</button>
                </div>
              </div>
            }
            @if (showTable) {
              <div class="table-wrap">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>Пара</th>
                      <th>Вид</th>
                      <th>Цена</th>
                      <th>Кол-во</th>
                      <th>Статус</th>
                      <th class="data-table__col-action" aria-label="Действия"></th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (o of data.content; track o.id) {
                      <tr>
                        <td>{{ pairLabel(o.tradingPairId) }}</td>
                        <td [class.buy]="o.side === 'BUY'" [class.sell]="o.side === 'SELL'">{{ sideLabel(o.side) }}</td>
                        <td>{{ o.price ?? '—' }}</td>
                        <td>{{ o.quantity }}</td>
                        <td><span class="status-pill">{{ statusLabel(o.status) }}</span></td>
                        <td class="data-table__col-action">
                          @if (o.status === 'NEW' || o.status === 'PARTIALLY_FILLED') {
                            <button type="button" class="q-btn q-btn-ghost danger" (click)="cancel(o.id)">Отмена</button>
                          }
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
              <app-pagination
                [page]="data.page"
                [totalPages]="data.totalPages"
                [totalElements]="data.totalElements"
                [centered]="true"
                [showTotal]="false"
                (pageChange)="onPageChange($event)"
              />
            }
            </app-content-reveal>
          </div>
        </section>
      </div>
    </div>
  `,
})
export class OrdersComponent implements OnInit {
  private readonly auth = inject(AuthService)
  private readonly router = inject(Router)

  readonly sideOptions = [
    { value: '', label: 'Все' },
    { value: 'BUY', label: SIDE_LABELS.BUY },
    { value: 'SELL', label: SIDE_LABELS.SELL },
  ]

  readonly statusOptions = [
    { value: '', label: 'Все' },
    ...['NEW', 'PARTIALLY_FILLED', 'FILLED', 'CANCELLED'].map((s) => ({
      value: s,
      label: STATUS_LABELS[s] ?? s,
    })),
  ]

  readonly pairOptions = [
    { value: '', label: 'Все' },
    ...Object.entries(PAIR_IDS).map(([sym, id]) => ({
      value: String(id),
      label: sym.replace('_', '/'),
    })),
  ]

  data: PageResponse<Order> = EMPTY_ORDERS
  side = ''
  status = ''
  tradingPairId = ''
  page = 0
  pending = true

  get isEmpty() {
    return !this.pending && this.data.content.length === 0
  }

  get showTable() {
    return !this.pending && this.data.content.length > 0
  }

  get ordersContentKey() {
    return [
      this.side,
      this.status,
      this.tradingPairId,
      this.page,
      this.pending ? 'loading' : `${this.data.content.map((o) => o.id).join(',')}:${this.isEmpty ? 'empty' : 'rows'}`,
    ].join('|')
  }

  get activeCount() {
    return this.data.content.filter((o) => o.status === 'NEW' || o.status === 'PARTIALLY_FILLED').length
  }

  ngOnInit() {
    this.pending = true
    void this.load()
  }

  pairLabel(id: number) {
    return PAIR_LABELS[id] ?? id
  }

  sideLabel(side: 'BUY' | 'SELL') {
    return SIDE_LABELS[side]
  }

  statusLabel(status: string) {
    return STATUS_LABELS[status] ?? status
  }

  async load() {
    try {
      this.data = await this.auth.orders({
        side: this.side ? (this.side as 'BUY' | 'SELL') : undefined,
        status: this.status || undefined,
        tradingPairId: this.tradingPairId ? Number(this.tradingPairId) : undefined,
        page: this.page,
        size: ORDERS_PAGE_SIZE,
      })
    } catch {
      this.data = EMPTY_ORDERS
    } finally {
      this.pending = false
    }
  }

  onPageChange(next: number) {
    this.pending = true
    this.page = next
    void this.load()
  }

  onSideFilter(value: string) {
    this.pending = true
    this.side = value
    this.page = 0
    void this.load()
  }

  onStatusFilter(value: string) {
    this.pending = true
    this.status = value
    this.page = 0
    void this.load()
  }

  onTradingPairFilter(value: string) {
    this.pending = true
    this.tradingPairId = value
    this.page = 0
    void this.load()
  }

  goMarket() {
    void this.router.navigateByUrl('/market')
  }

  async cancel(id: string) {
    await this.auth.cancelOrder(id)
    await this.load()
  }
}
