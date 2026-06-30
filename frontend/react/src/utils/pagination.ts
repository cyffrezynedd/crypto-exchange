export type PageItem = number | '...'

/** page — 0-based index from API; items — 1-based labels for UI */
export function buildPageItems(page: number, totalPages: number): PageItem[] {
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

/** Jump forward on ellipsis click — same as qoopi main-web */
export function resolvePageFromItem(item: PageItem, page: number, totalPages: number): number {
  if (item === '...') {
    return Math.min(page + 3, totalPages - 1)
  }
  return item - 1
}
