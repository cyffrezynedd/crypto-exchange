import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import type { Order, PageResponse } from '../api/types'
import { PAIR_IDS, PAIR_LABELS } from '../constants'
import { ContentReveal } from '../components/ContentReveal'
import { Pagination } from '../components/Pagination'
import { PageBody, PageShell, PageTitle, Panel, StatChip, StatChips } from '../components/PageShell'
import { NoteStackIcon } from '../components/icons'
import { Button } from '../components/ui/Button'
import { Empty } from '../components/ui/Empty'
import { Select } from '../components/ui/Select'
import '../styles/tables.css'

const ORDERS_PAGE_SIZE = 7

const EMPTY_ORDERS: PageResponse<Order> = {
  content: [],
  page: 0,
  size: ORDERS_PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
}

const STATUSES = ['NEW', 'PARTIALLY_FILLED', 'FILLED', 'CANCELLED']

const SIDE_LABELS: Record<'BUY' | 'SELL', string> = {
  BUY: 'Покупка',
  SELL: 'Продажа',
}

const SIDE_OPTIONS = [
  { value: '', label: 'Все' },
  { value: 'BUY', label: SIDE_LABELS.BUY },
  { value: 'SELL', label: SIDE_LABELS.SELL },
]

const STATUS_LABELS: Record<string, string> = {
  NEW: 'Новый',
  PARTIALLY_FILLED: 'Частично исполнен',
  FILLED: 'Исполнен',
  CANCELLED: 'Отменён',
}

const STATUS_OPTIONS = [
  { value: '', label: 'Все' },
  ...STATUSES.map((s) => ({ value: s, label: STATUS_LABELS[s] ?? s })),
]

const PAIR_OPTIONS = [
  { value: '', label: 'Все' },
  ...Object.entries(PAIR_IDS).map(([sym, id]) => ({
    value: String(id),
    label: sym.replace('_', '/'),
  })),
]

export function OrdersPage() {
  const navigate = useNavigate()
  const [data, setData] = useState(EMPTY_ORDERS)
  const [side, setSide] = useState('')
  const [status, setStatus] = useState('')
  const [tradingPairId, setTradingPairId] = useState('')
  const [page, setPage] = useState(0)
  const [pending, setPending] = useState(true)

  const load = useCallback(async () => {
    try {
      const result = await api.orders({
        side: side ? (side as 'BUY' | 'SELL') : undefined,
        status: status || undefined,
        tradingPairId: tradingPairId ? Number(tradingPairId) : undefined,
        page,
        size: ORDERS_PAGE_SIZE,
      })
      setData(result)
    } catch {
      setData(EMPTY_ORDERS)
    } finally {
      setPending(false)
    }
  }, [side, status, tradingPairId, page])

  useEffect(() => {
    setPending(true)
    void load()
  }, [load])

  function onSideFilter(value: string) {
    setPending(true)
    setSide(value)
    setPage(0)
  }

  function onStatusFilter(value: string) {
    setPending(true)
    setStatus(value)
    setPage(0)
  }

  function onTradingPairFilter(value: string) {
    setPending(true)
    setTradingPairId(value)
    setPage(0)
  }

  async function cancel(id: string) {
    await api.cancelOrder(id)
    await load()
  }

  const activeCount = data?.content?.filter(
    (o) => o.status === 'NEW' || o.status === 'PARTIALLY_FILLED',
  ).length ?? 0

  const isEmpty = !pending && (data?.content?.length ?? 0) === 0
  const showTable = !pending && (data.content?.length ?? 0) > 0
  const ordersContentKey = [
    side,
    status,
    tradingPairId,
    page,
    pending ? 'loading' : `${data.content.map((o) => o.id).join(',')}:${isEmpty ? 'empty' : 'rows'}`,
  ].join('|')

  return (
    <PageShell>
      <PageTitle
        title="Ордера"
        icon={<NoteStackIcon size={22} />}
        stats={data && (
          <StatChips>
            <StatChip label="Всего" value={data.totalElements} />
            <StatChip label="Активные" value={activeCount} />
          </StatChips>
        )}
      />

      <PageBody>
        <Panel>
          <div className="filters-bar">
            <Select
              variant="filter"
              label="Вид"
              value={side}
              onChange={onSideFilter}
              options={SIDE_OPTIONS}
            />
            <Select
              variant="filter"
              label="Статус"
              value={status}
              onChange={onStatusFilter}
              options={STATUS_OPTIONS}
            />
            <Select
              variant="filter"
              label="Торговая пара"
              value={tradingPairId}
              onChange={onTradingPairFilter}
              options={PAIR_OPTIONS}
            />
          </div>
        </Panel>

        <Panel grow className="orders-panel">
          <div className={`panel__body panel__body--scroll${isEmpty ? ' panel__body--center' : ''}`}>
            <ContentReveal resetKey={ordersContentKey}>
            {isEmpty && (
              <Empty
                icon={NoteStackIcon}
                title="Здесь будут ваши ордера"
                subtitle="Перейдите на рынок и разместите первый ордер"
                primaryAction={{ label: 'На рынок', onClick: () => navigate('/market') }}
              />
            )}
            {showTable && (
              <>
                <div className="table-wrap">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Пара</th>
                        <th>Вид</th>
                        <th>Цена</th>
                        <th>Кол-во</th>
                        <th>Статус</th>
                        <th className="data-table__col-action" aria-label="Действия" />
                      </tr>
                    </thead>
                    <tbody>
                      {data.content.map((o) => (
                        <tr key={o.id}>
                          <td>{PAIR_LABELS[o.tradingPairId] ?? o.tradingPairId}</td>
                          <td className={o.side === 'BUY' ? 'buy' : 'sell'}>{SIDE_LABELS[o.side]}</td>
                          <td>{o.price ?? '—'}</td>
                          <td>{o.quantity}</td>
                          <td><span className="status-pill">{STATUS_LABELS[o.status] ?? o.status}</span></td>
                          <td className="data-table__col-action">
                            {(o.status === 'NEW' || o.status === 'PARTIALLY_FILLED') && (
                              <Button
                                type="button"
                                block={false}
                                variant="ghost"
                                className="danger"
                                onClick={() => cancel(o.id)}
                              >
                                Отмена
                              </Button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                <Pagination
                  page={data.page}
                  totalPages={data.totalPages}
                  totalElements={data.totalElements}
                  onPageChange={(next) => {
                    setPending(true)
                    setPage(next)
                  }}
                  centered
                  showTotal={false}
                />
              </>
            )}
            </ContentReveal>
          </div>
        </Panel>
      </PageBody>
    </PageShell>
  )
}
