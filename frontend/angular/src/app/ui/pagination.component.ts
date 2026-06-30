import { Component, EventEmitter, Input, Output } from '@angular/core'

type PageItem = number | '...'

function buildPageItems(page: number, totalPages: number): PageItem[] {
  const current = page + 1
  if (totalPages <= 5) {
    return Array.from({ length: totalPages }, (_, index) => index + 1)
  }
  if (current > totalPages - 4) {
    return [
      totalPages - 4,
      totalPages - 3,
      totalPages - 2,
      totalPages - 1,
      totalPages,
    ]
  }
  return [current, current + 1, current + 2, '...', totalPages]
}

function resolvePageFromItem(item: PageItem, page: number, totalPages: number): number {
  if (item === '...') {
    return Math.min(page + 3, totalPages - 1)
  }
  return item - 1
}

@Component({
  selector: 'app-pagination',
  standalone: true,
  template: `
    @if (totalPages > 1) {
      <div [class]="centered ? 'pagination pagination--centered' : 'pagination'">
        @if (showTotal) {
          <span class="pagination-meta">Всего: {{ totalElements }}</span>
        }
        <div class="pagination-controls" role="navigation" aria-label="Пагинация">
          <button
            type="button"
            class="page-btn page-btn--nav"
            aria-label="Предыдущая страница"
            [disabled]="isPrevDisabled"
            (click)="onPage(page - 1)"
          >
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
              <path d="M10 3L5 8L10 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </button>

          @for (item of pages; track $index) {
            @if (item === '...') {
              <button
                type="button"
                class="page-btn page-btn--page page-btn--ellipsis"
                aria-label="Следующие страницы"
                (click)="onPage(resolvePage(item))"
              >
                …
              </button>
            } @else {
              <button
                type="button"
                class="page-btn page-btn--page"
                [class.active]="item - 1 === page"
                [attr.aria-label]="'Страница ' + item"
                [attr.aria-current]="item - 1 === page ? 'page' : null"
                (click)="onPage(item - 1)"
              >
                {{ item }}
              </button>
            }
          }

          <button
            type="button"
            class="page-btn page-btn--nav"
            aria-label="Следующая страница"
            [disabled]="isNextDisabled"
            (click)="onPage(page + 1)"
          >
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
              <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </button>
        </div>
      </div>
    }
  `,
})
export class PaginationComponent {
  @Input({ required: true }) page!: number
  @Input({ required: true }) totalPages!: number
  @Input({ required: true }) totalElements!: number
  @Input() centered = false
  @Input() showTotal = true
  @Output() pageChange = new EventEmitter<number>()

  get pages() {
    return buildPageItems(this.page, this.totalPages)
  }

  get isPrevDisabled() {
    return this.page <= 0
  }

  get isNextDisabled() {
    return this.page >= this.totalPages - 1
  }

  resolvePage(item: PageItem) {
    return resolvePageFromItem(item, this.page, this.totalPages)
  }

  onPage(next: number) {
    if (next !== this.page && next >= 0 && next < this.totalPages) {
      this.pageChange.emit(next)
    }
  }
}
