import { useMemo } from 'react'
import { ChevronLeftIcon, ChevronRightIcon } from './icons'
import { buildPageItems, resolvePageFromItem } from '../utils/pagination'
import './pagination.css'

type Props = {
  page: number
  totalPages: number
  totalElements: number
  onPageChange: (page: number) => void
  centered?: boolean
  showTotal?: boolean
}

export function Pagination({
  page,
  totalPages,
  totalElements,
  onPageChange,
  centered = false,
  showTotal = true,
}: Props) {
  const pages = useMemo(() => buildPageItems(page, totalPages), [page, totalPages])
  const isPrevDisabled = page <= 0
  const isNextDisabled = page >= totalPages - 1

  if (totalPages <= 1) return null

  return (
    <div className={['pagination', centered ? 'pagination--centered' : ''].filter(Boolean).join(' ')}>
      {showTotal && <span className="pagination-meta">Всего: {totalElements}</span>}
      <div className="pagination-controls" role="navigation" aria-label="Пагинация">
        <button
          type="button"
          className="page-btn page-btn--nav"
          aria-label="Предыдущая страница"
          disabled={isPrevDisabled}
          onClick={() => onPageChange(page - 1)}
        >
          <ChevronLeftIcon size={16} />
        </button>

        {pages.map((item, index) => {
          if (item === '...') {
            return (
              <button
                key={`gap-${index}`}
                type="button"
                className="page-btn page-btn--page page-btn--ellipsis"
                aria-label="Следующие страницы"
                onClick={() => onPageChange(resolvePageFromItem(item, page, totalPages))}
              >
                …
              </button>
            )
          }
          const pageIndex = item - 1
          const isActive = pageIndex === page
          return (
            <button
              key={item}
              type="button"
              className={['page-btn', 'page-btn--page', isActive ? 'active' : ''].filter(Boolean).join(' ')}
              aria-label={`Страница ${item}`}
              aria-current={isActive ? 'page' : undefined}
              onClick={() => {
                if (!isActive) onPageChange(pageIndex)
              }}
            >
              {item}
            </button>
          )
        })}

        <button
          type="button"
          className="page-btn page-btn--nav"
          aria-label="Следующая страница"
          disabled={isNextDisabled}
          onClick={() => onPageChange(page + 1)}
        >
          <ChevronRightIcon size={16} />
        </button>
      </div>
    </div>
  )
}
