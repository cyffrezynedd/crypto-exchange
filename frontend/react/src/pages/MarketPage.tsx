import type { FormEvent } from 'react'
import { Fragment, useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import type { PageResponse, OrderBookLevel, TradingPair } from '../api/types'
import { PAIR_IDS } from '../constants'
import { ContentReveal } from '../components/ContentReveal'
import { Pagination } from '../components/Pagination'
import { PageContent, PageShell, PageTitle, Panel, StatChip } from '../components/PageShell'
import { FoldersIcon } from '../components/icons'
import { Alert } from '../components/ui/Alert'
import { Button } from '../components/ui/Button'
import { Empty } from '../components/ui/Empty'
import { SidePanelField } from '../components/ui/SidePanelField'
import { SearchInput } from '../components/ui/SearchInput'
import { Select } from '../components/ui/Select'
import { SidePanel } from '../components/ui/SidePanel'
import '../styles/market.css'
import '../styles/tables.css'

const BOOK_PAGE_SIZE = 7

const EMPTY_BOOK: PageResponse<OrderBookLevel> = {
  content: [],
  page: 0,
  size: BOOK_PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
}

function formatPair(symbol: string) {
  return symbol.replace('_', '/')
}

const BOOK_SIDE_OPTIONS = [
  { value: '', label: 'Все' },
  { value: 'BUY', label: 'Покупка' },
  { value: 'SELL', label: 'Продажа' },
]

const SIDE_LABELS: Record<OrderBookLevel['side'], string> = {
  BUY: 'Покупка',
  SELL: 'Продажа',
}

export function MarketPage() {
  const [pairs, setPairs] = useState<TradingPair[]>([])
  const [symbol, setSymbol] = useState('BTC_USDT')
  const [bookData, setBookData] = useState<PageResponse<OrderBookLevel> | null>(null)
  const [bookSideFilter, setBookSideFilter] = useState('')
  const [usernameQuery, setUsernameQuery] = useState('')
  const [bookPage, setBookPage] = useState(0)
  const [pending, setPending] = useState(true)
  const [side, setSide] = useState<'BUY' | 'SELL'>('BUY')
  const [price, setPrice] = useState('50000')
  const [quantity, setQuantity] = useState('0.001')
  const [message, setMessage] = useState('')
  const [orderOpen, setOrderOpen] = useState(false)

  useEffect(() => {
    api.pairs().then(setPairs).catch(() => setPairs([{ symbol: 'BTC_USDT' }, { symbol: 'ETH_USDT' }]))
  }, [])

  const loadBook = useCallback(async () => {
    setPending(true)
    try {
      const result = await api.orderBook(symbol, {
        side: bookSideFilter ? (bookSideFilter as 'BUY' | 'SELL') : undefined,
        username: usernameQuery.trim() || undefined,
        page: bookPage,
        size: BOOK_PAGE_SIZE,
      })
      setBookData(result)
    } catch {
      setBookData(EMPTY_BOOK)
    } finally {
      setPending(false)
    }
  }, [symbol, bookSideFilter, usernameQuery, bookPage])

  useEffect(() => {
    loadBook()
  }, [loadBook])

  useEffect(() => {
    setBookPage(0)
  }, [symbol, bookSideFilter, usernameQuery])

  async function onPlace(e: FormEvent) {
    e.preventDefault()
    setMessage('')
    try {
      await api.placeOrder({
        tradingPairId: PAIR_IDS[symbol],
        side,
        type: 'LIMIT',
        price,
        quantity,
      })
      setMessage('Ордер создан')
      await loadBook()
    } catch (err) {
      setMessage(err instanceof Error ? err.message : 'Ошибка')
    }
  }

  const rows = bookData?.content ?? []
  const isBookEmpty = !pending && (bookData?.totalElements ?? 0) === 0
  const showBookTable = !pending && rows.length > 0 && !!bookData
  const bookContentKey = [
    symbol,
    bookSideFilter,
    usernameQuery,
    bookPage,
    pending ? 'loading' : `${rows.map((r) => r.orderId).join(',')}:${isBookEmpty ? 'empty' : 'rows'}`,
  ].join('|')

  return (
    <PageShell>
      <PageTitle
        title="Рынок"
        icon={<FoldersIcon size={22} />}
        action={(
          <Button block={false} onClick={() => setOrderOpen(true)}>
            Новый ордер
          </Button>
        )}
      />

      <PageContent>
        <div className="market-toolbar">
          <div className="pair-tabs" role="tablist" aria-label="Торговые пары">
            {pairs.map((pair, index) => {
              const isActive = pair.symbol === symbol
              return (
                <Fragment key={pair.symbol}>
                  {index > 0 && <span className="pair-tabs__dot" aria-hidden />}
                  <button
                    type="button"
                    role="tab"
                    aria-selected={isActive}
                    className={isActive ? 'pair-tabs__item is-active' : 'pair-tabs__item'}
                    onClick={() => {
                      setPending(true)
                      setSymbol(pair.symbol)
                    }}
                  >
                    {formatPair(pair.symbol)}
                  </button>
                </Fragment>
              )
            })}
          </div>

          <div className="filters-bar market-orderbook-filters">
            <div className="market-orderbook-filters__start">
              <StatChip label="Всего" value={bookData?.totalElements ?? 0} />
            </div>
            <div className="market-orderbook-filters__end">
              <Select
                variant="filter"
                label="Вид"
                value={bookSideFilter}
                onChange={(value) => {
                  setPending(true)
                  setBookSideFilter(value)
                }}
                options={BOOK_SIDE_OPTIONS}
              />
              <SearchInput
                className="market-orderbook-search"
                variant="filter"
                label="Поиск"
                value={usernameQuery}
                onChange={(e) => {
                  setPending(true)
                  setUsernameQuery(e.target.value)
                }}
              />
            </div>
          </div>
        </div>

        <div className="market-layout">
          <Panel grow className="market-orderbook">
            <div className="panel__body panel__body--scroll">
              <ContentReveal resetKey={bookContentKey}>
              {isBookEmpty && !usernameQuery && !bookSideFilter && (
                <Empty
                  icon={FoldersIcon}
                  title="Заявок пока нет"
                  subtitle="Когда появятся активные заявки по этой паре, они отобразятся здесь"
                />
              )}
              {isBookEmpty && (usernameQuery || bookSideFilter) && (
                <Empty
                  icon={FoldersIcon}
                  title="Заявок по фильтру нет"
                  subtitle="Попробуйте изменить фильтры или сбросить поиск"
                  primaryAction={{
                    label: 'Сбросить',
                    onClick: () => {
                      setPending(true)
                      setBookSideFilter('')
                      setUsernameQuery('')
                    },
                  }}
                />
              )}
              {showBookTable && (
                <>
                  <div className="table-wrap">
                    <table className="data-table orderbook-table">
                      <thead>
                        <tr>
                          <th className="orderbook-table__col-side">Вид</th>
                          <th className="orderbook-table__col-nick">Никнейм</th>
                          <th className="orderbook-table__col-qty">Количество</th>
                          <th className="orderbook-table__col-price">Цена</th>
                        </tr>
                      </thead>
                      <tbody>
                        {rows.map((row) => (
                          <tr key={`${row.side}-${row.orderId}`}>
                            <td className={row.side === 'BUY' ? 'buy orderbook-table__col-side' : 'sell orderbook-table__col-side'}>
                              {SIDE_LABELS[row.side]}
                            </td>
                            <td className="orderbook-table__col-nick" title={row.username}>
                              <span className="orderbook-nick">{row.username}</span>
                            </td>
                            <td className="orderbook-table__col-qty">{row.quantity}</td>
                            <td className="orderbook-table__col-price">{row.price}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  <Pagination
                    page={bookData.page}
                    totalPages={bookData.totalPages}
                    totalElements={bookData.totalElements}
                    onPageChange={(next) => {
                      setPending(true)
                      setBookPage(next)
                    }}
                    centered
                    showTotal={false}
                  />
                </>
              )}
              </ContentReveal>
            </div>
          </Panel>
        </div>
      </PageContent>

      <SidePanel
        isOpen={orderOpen}
        title="Новый ордер"
        onClose={() => setOrderOpen(false)}
        primaryLabel="Разместить"
        primaryFormId="place-order-form"
      >
        <form id="place-order-form" className="stack-form side-panel-form order-side-form" onSubmit={onPlace}>
          <div className="order-side-tabs">
            <button
              type="button"
              className={side === 'BUY' ? 'tab buy active' : 'tab buy'}
              onClick={() => setSide('BUY')}
            >
              Покупка
            </button>
            <button
              type="button"
              className={side === 'SELL' ? 'tab sell active' : 'tab sell'}
              onClick={() => setSide('SELL')}
            >
              Продажа
            </button>
          </div>
          <SidePanelField label="Цена">
            <input
              className="side-panel-field__input"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
            />
          </SidePanelField>
          <SidePanelField label="Количество">
            <input
              className="side-panel-field__input"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
            />
          </SidePanelField>
          {message && (
            <Alert variant={message === 'Ордер создан' ? 'success' : 'danger'}>
              {message}
            </Alert>
          )}
        </form>
      </SidePanel>
    </PageShell>
  )
}
