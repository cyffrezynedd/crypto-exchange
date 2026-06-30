import { CommonModule } from '@angular/common'
import { Component, OnInit, inject } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { AuthService } from '../core/auth.service'
import { PAIR_IDS } from '../core/constants'
import { FavoritePair, PageResponse } from '../core/models'
import { FilterSelectComponent } from '../ui/filter-select.component'
import { PaginationComponent } from '../ui/pagination.component'
import { SidePanelComponent } from '../ui/side-panel.component'
import { SidePanelFieldComponent } from '../ui/side-panel-field.component'
import { SidebarSelectComponent } from '../ui/sidebar-select.component'
import { ContentRevealComponent } from '../ui/content-reveal.component'
import { PageTitleComponent } from '../ui/page-title.component'

const PAIR_OPTIONS = Object.entries(PAIR_IDS).map(([sym, id]) => ({
  value: String(id),
  label: sym.replace('_', '/'),
}))

const PAIR_FILTER_OPTIONS = [{ value: '', label: 'Все' }, ...PAIR_OPTIONS]

const EMPTY_FAVORITES: PageResponse<FavoritePair> = {
  content: [],
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
}

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    FilterSelectComponent,
    PaginationComponent,
    SidePanelComponent,
    SidePanelFieldComponent,
    SidebarSelectComponent,
    ContentRevealComponent,
    PageTitleComponent,
  ],
  template: `
    <div class="page-shell">
      <app-page-title title="Главная" icon="house">
        <button page-action type="button" class="q-btn q-btn-primary" (click)="openAdd()">Добавить пару</button>
      </app-page-title>

      <div class="page-body scroll-hidden">
        <div class="page-divider" aria-hidden></div>
        <section class="card panel">
          <div class="filters-bar">
            <app-filter-select
              label="Торговая пара"
              [options]="pairFilterOptions"
              [ngModel]="tradingPairId"
              (ngModelChange)="onTradingPairFilter($event)"
            />
          </div>
        </section>

        <section class="card panel panel--grow favorites-panel">
          <div class="surface favorites-panel__surface">
            <div class="panel__head">
              <h2 class="panel__title">Избранные пары</h2>
              <span class="stat-chip">Пар <strong>{{ data.totalElements }}</strong></span>
            </div>
            <div class="panel__body panel__body--scroll" [class.panel__body--center]="isEmpty">
              <app-content-reveal [resetKey]="favoritesContentKey">
              @if (isEmpty) {
                <div class="ui-empty">
                  <div class="ui-empty__text">
                    <p class="ui-empty__title">Избранных пар пока нет</p>
                    <p class="ui-empty__subtitle">Добавьте пару, чтобы быстро возвращаться к ней</p>
                  </div>
                  <div class="ui-empty__actions">
                    <button type="button" class="q-btn q-btn-primary" (click)="openAdd()">Добавить пару</button>
                  </div>
                </div>
              }
              @if (showTable) {
                <div class="table-wrap">
                  <table class="data-table">
                    <thead>
                      <tr>
                        <th>Пара</th>
                        <th>ID пары</th>
                        <th>Добавлено</th>
                        <th class="data-table__col-action" aria-label="Действия"></th>
                      </tr>
                    </thead>
                    <tbody>
                      @for (row of data.content; track row.tradingPairId) {
                        <tr>
                          <td><strong>{{ row.symbol.replace('_', '/') }}</strong></td>
                          <td>{{ row.tradingPairId }}</td>
                          <td>{{ row.addedAt | date: 'short' }}</td>
                          <td class="data-table__col-action">
                            <button type="button" class="q-btn q-btn-ghost danger" (click)="remove(row.tradingPairId)">
                              Удалить
                            </button>
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
                  (pageChange)="onPageChange($event)"
                />
              }
              </app-content-reveal>
            </div>
          </div>
        </section>
      </div>
    </div>

    <app-side-panel
      title="Добавить в избранное"
      [isOpen]="addOpen"
      primaryLabel="Добавить"
      primaryFormId="add-favorite-form"
      (onClose)="addOpen = false"
    >
      <form id="add-favorite-form" class="stack-form side-panel-form" (ngSubmit)="add()">
        <app-side-panel-field label="Торговая пара">
          <app-sidebar-select
            [options]="pairOptions"
            [(ngModel)]="addPairId"
            name="addPairId"
          />
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
export class FavoritesComponent implements OnInit {
  private readonly auth = inject(AuthService)

  readonly pairOptions = PAIR_OPTIONS
  readonly pairFilterOptions = PAIR_FILTER_OPTIONS

  data: PageResponse<FavoritePair> = EMPTY_FAVORITES
  tradingPairId = ''
  page = 0
  addPairId = '1'
  addOpen = false
  pending = true
  message = ''
  messageVariant: 'success' | 'danger' = 'success'

  get isEmpty() {
    return !this.pending && this.data.content.length === 0
  }

  get showTable() {
    return !this.pending && this.data.content.length > 0
  }

  get favoritesContentKey() {
    return [
      this.tradingPairId,
      this.page,
      this.pending ? 'loading' : `${this.data.content.map((r) => `${r.userId}-${r.tradingPairId}`).join(',')}:${this.isEmpty ? 'empty' : 'rows'}`,
    ].join('|')
  }

  ngOnInit() {
    this.pending = true
    void this.load()
  }

  async load() {
    try {
      this.data = await this.auth.favorites({
        tradingPairId: this.tradingPairId ? Number(this.tradingPairId) : undefined,
        page: this.page,
        size: 10,
      })
    } catch {
      this.data = EMPTY_FAVORITES
    } finally {
      this.pending = false
    }
  }

  onPageChange(next: number) {
    this.pending = true
    this.page = next
    void this.load()
  }

  onTradingPairFilter(value: string) {
    this.pending = true
    this.tradingPairId = value
    this.page = 0
    void this.load()
  }

  openAdd() {
    this.message = ''
    this.addOpen = true
  }

  async add() {
    this.message = ''
    try {
      await this.auth.addFavorite(Number(this.addPairId))
      this.messageVariant = 'success'
      this.message = 'Пара добавлена в избранное'
      this.page = 0
      await this.load()
      this.addOpen = false
    } catch (err) {
      this.messageVariant = 'danger'
      this.message = err instanceof Error ? err.message : 'Не удалось добавить пару'
    }
  }

  async remove(tradingPairId: number) {
    await this.auth.removeFavorite(tradingPairId)
    await this.load()
  }
}
