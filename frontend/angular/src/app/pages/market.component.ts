import { Component, OnInit, inject } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { AuthService } from '../core/auth.service'
import { PAIR_IDS } from '../core/constants'
import { OrderBookLevel, PageResponse, TradingPair } from '../core/models'
import { FilterSelectComponent } from '../ui/filter-select.component'
import { PaginationComponent } from '../ui/pagination.component'
import { SidePanelComponent } from '../ui/side-panel.component'
import { SidePanelFieldComponent } from '../ui/side-panel-field.component'
import { ContentRevealComponent } from '../ui/content-reveal.component'
import { PageTitleComponent } from '../ui/page-title.component'

const BOOK_PAGE_SIZE = 7

const EMPTY_BOOK: PageResponse<OrderBookLevel> = {
  content: [],
  page: 0,
  size: BOOK_PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
}

const SIDE_LABELS: Record<OrderBookLevel['side'], string> = {
  BUY: 'Покупка',
  SELL: 'Продажа',
}

@Component({
  selector: 'app-market',
  standalone: true,
  imports: [
    FormsModule,
    FilterSelectComponent,
    PaginationComponent,
    SidePanelComponent,
    SidePanelFieldComponent,
    ContentRevealComponent,
    PageTitleComponent,
  ],
  template: `
    <div class="page-shell">
      <app-page-title title="Рынок" icon="folders">
        <button page-action type="button" class="q-btn q-btn-primary" (click)="orderOpen = true">Новый ордер</button>
      </app-page-title>

      <div class="page-content">
        <div class="page-divider" aria-hidden></div>
        <div class="market-toolbar">
          <div class="pair-tabs" role="tablist" aria-label="Торговые пары">
            @for (pair of pairs; track pair.symbol; let index = $index) {
              @if (index > 0) {
                <span class="pair-tabs__dot" aria-hidden></span>
              }
              <button
                type="button"
                role="tab"
                [attr.aria-selected]="pair.symbol === symbol"
                [class.is-active]="pair.symbol === symbol"
                class="pair-tabs__item"
                (click)="selectPair(pair.symbol)"
              >
                {{ formatPair(pair.symbol) }}
              </button>
            }
          </div>

          <div class="filters-bar market-orderbook-filters">
            <div class="market-orderbook-filters__start">
              <span class="stat-chip">Всего <strong>{{ bookData.totalElements }}</strong></span>
            </div>
            <div class="market-orderbook-filters__end">
              <app-filter-select
                label="Вид"
                [options]="bookSideOptions"
                [ngModel]="bookSideFilter"
                (ngModelChange)="onBookSideFilter($event)"
              />
              <div class="search-input-wrap search-input-wrap--filter market-orderbook-search">
                <div class="search-input-shell" [class.is-active]="!!usernameQuery">
                  <span class="search-input__icon" aria-hidden>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                      <circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="1.5" />
                      <path d="M20 20l-3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                    </svg>
                  </span>
                  <input
                    class="search-input__control"
                    placeholder="Поиск"
                    [ngModel]="usernameQuery"
                    (ngModelChange)="onUsernameQuery($event)"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="market-layout">
          <section class="card panel panel--grow market-orderbook">
            <div class="panel__body panel__body--scroll">
              <app-content-reveal [resetKey]="bookContentKey">
              @if (isBookEmpty && !usernameQuery && !bookSideFilter) {
                <div class="ui-empty">
                  <div class="ui-empty__text">
                    <p class="ui-empty__title">Заявок пока нет</p>
                    <p class="ui-empty__subtitle">Когда появятся активные заявки по этой паре, они отобразятся здесь</p>
                  </div>
                </div>
              }
              @if (isBookEmpty && (usernameQuery || bookSideFilter)) {
                <div class="ui-empty">
                  <div class="ui-empty__text">
                    <p class="ui-empty__title">Заявок по фильтру нет</p>
                    <p class="ui-empty__subtitle">Попробуйте изменить фильтры или сбросить поиск</p>
                  </div>
                  <div class="ui-empty__actions">
                    <button type="button" class="q-btn q-btn-primary" (click)="resetFilters()">Сбросить</button>
                  </div>
                </div>
              }
              @if (showBookTable) {
                <div class="table-wrap">
                  <table class="data-table orderbook-table">
                    <thead>
                      <tr>
                        <th class="orderbook-table__col-side">Вид</th>
                        <th class="orderbook-table__col-nick">Никнейм</th>
                        <th class="orderbook-table__col-qty">Количество</th>
                        <th class="orderbook-table__col-price">Цена</th>
                      </tr>
                    </thead>
                    <tbody>
                      @for (row of rows; track row.orderId) {
                        <tr>
                          <td [class]="row.side === 'BUY' ? 'buy orderbook-table__col-side' : 'sell orderbook-table__col-side'">
                            {{ sideLabel(row.side) }}
                          </td>
                          <td class="orderbook-table__col-nick" [title]="row.username">
                            <span class="orderbook-nick">{{ row.username }}</span>
                          </td>
                          <td class="orderbook-table__col-qty">{{ row.quantity }}</td>
                          <td class="orderbook-table__col-price">{{ row.price }}</td>
                        </tr>
                      }
                    </tbody>
                  </table>
                </div>
                <app-pagination
                  [page]="bookData.page"
                  [totalPages]="bookData.totalPages"
                  [totalElements]="bookData.totalElements"
                  [centered]="true"
                  [showTotal]="false"
                  (pageChange)="onBookPageChange($event)"
                />
              }
              </app-content-reveal>
            </div>
          </section>
        </div>
      </div>
    </div>

    <app-side-panel
      title="Новый ордер"
      [isOpen]="orderOpen"
      primaryLabel="Разместить"
      primaryFormId="place-order-form"
      (onClose)="orderOpen = false"
    >
      <form id="place-order-form" class="stack-form side-panel-form order-side-form" (ngSubmit)="place()">
        <div class="order-side-tabs">
          <button type="button" class="tab buy" [class.active]="side === 'BUY'" (click)="side = 'BUY'">Покупка</button>
          <button type="button" class="tab sell" [class.active]="side === 'SELL'" (click)="side = 'SELL'">Продажа</button>
        </div>
        <app-side-panel-field label="Цена">
          <input class="side-panel-field__input" [(ngModel)]="price" name="price" />
        </app-side-panel-field>
        <app-side-panel-field label="Количество">
          <input class="side-panel-field__input" [(ngModel)]="quantity" name="quantity" />
        </app-side-panel-field>
        @if (message) {
          <p class="ui-alert" [class.ui-alert--success]="message === 'Ордер создан'" [class.ui-alert--danger]="message !== 'Ордер создан'">
            {{ message }}
          </p>
        }
      </form>
    </app-side-panel>
  `,
})
export class MarketComponent implements OnInit {
  private readonly auth = inject(AuthService)

  readonly bookSideOptions = [
    { value: '', label: 'Все' },
    { value: 'BUY', label: 'Покупка' },
    { value: 'SELL', label: 'Продажа' },
  ]

  pairs: TradingPair[] = []
  symbol = 'BTC_USDT'
  bookData: PageResponse<OrderBookLevel> = EMPTY_BOOK
  bookSideFilter = ''
  usernameQuery = ''
  bookPage = 0
  pending = true
  side: 'BUY' | 'SELL' = 'BUY'
  price = '50000'
  quantity = '0.001'
  message = ''
  orderOpen = false

  get rows() {
    return this.bookData.content
  }

  get isBookEmpty() {
    return !this.pending && this.bookData.totalElements === 0
  }

  get showBookTable() {
    return !this.pending && this.rows.length > 0
  }

  get bookContentKey() {
    return [
      this.symbol,
      this.bookSideFilter,
      this.usernameQuery,
      this.bookPage,
      this.pending ? 'loading' : `${this.rows.map((r) => r.orderId).join(',')}:${this.isBookEmpty ? 'empty' : 'rows'}`,
    ].join('|')
  }

  ngOnInit() {
    void this.init()
  }

  async init() {
    this.pairs = await this.auth.pairs().catch(() => [{ symbol: 'BTC_USDT' }, { symbol: 'ETH_USDT' }])
    await this.loadBook()
  }

  formatPair(symbol: string) {
    return symbol.replace('_', '/')
  }

  sideLabel(side: OrderBookLevel['side']) {
    return SIDE_LABELS[side]
  }

  async selectPair(symbol: string) {
    this.pending = true
    this.symbol = symbol
    this.bookPage = 0
    await this.loadBook()
  }

  onBookSideFilter(value: string) {
    this.pending = true
    this.bookSideFilter = value
    this.bookPage = 0
    void this.loadBook()
  }

  onUsernameQuery(value: string) {
    this.pending = true
    this.usernameQuery = value
    this.bookPage = 0
    void this.loadBook()
  }

  resetFilters() {
    this.pending = true
    this.bookSideFilter = ''
    this.usernameQuery = ''
    this.bookPage = 0
    void this.loadBook()
  }

  onBookPageChange(next: number) {
    this.pending = true
    this.bookPage = next
    void this.loadBook()
  }

  async loadBook() {
    this.pending = true
    try {
      this.bookData = await this.auth.orderBook(this.symbol, {
        side: this.bookSideFilter ? (this.bookSideFilter as 'BUY' | 'SELL') : undefined,
        username: this.usernameQuery.trim() || undefined,
        page: this.bookPage,
        size: BOOK_PAGE_SIZE,
      })
    } catch {
      this.bookData = EMPTY_BOOK
    } finally {
      this.pending = false
    }
  }

  async place() {
    this.message = ''
    try {
      await this.auth.placeOrder({
        tradingPairId: PAIR_IDS[this.symbol],
        side: this.side,
        type: 'LIMIT',
        price: this.price,
        quantity: this.quantity,
      })
      this.message = 'Ордер создан'
      await this.loadBook()
    } catch (err) {
      this.message = err instanceof Error ? err.message : 'Ошибка'
    }
  }
}
