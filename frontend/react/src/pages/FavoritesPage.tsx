import type { FormEvent } from 'react'
import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import type { FavoritePair, PageResponse } from '../api/types'
import { PAIR_IDS } from '../constants'
import { ContentReveal } from '../components/ContentReveal'
import { Pagination } from '../components/Pagination'
import { PageBody, PageShell, PageTitle, Panel, StatChip } from '../components/PageShell'
import { HouseIcon, StarIcon } from '../components/icons'
import { Alert } from '../components/ui/Alert'
import { Button } from '../components/ui/Button'
import { Empty } from '../components/ui/Empty'
import { Select } from '../components/ui/Select'
import { SidePanelField } from '../components/ui/SidePanelField'
import { SidePanel } from '../components/ui/SidePanel'
import '../styles/tables.css'

const PAIR_OPTIONS = Object.entries(PAIR_IDS).map(([sym, id]) => ({
  value: String(id),
  label: sym.replace('_', '/'),
}))

const PAIR_FILTER_OPTIONS = [
  { value: '', label: 'Все' },
  ...PAIR_OPTIONS,
]

const EMPTY_FAVORITES: PageResponse<FavoritePair> = {
  content: [],
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
}


export function FavoritesPage() {
  const [data, setData] = useState(EMPTY_FAVORITES)
  const [tradingPairId, setTradingPairId] = useState('')
  const [page, setPage] = useState(0)
  const [addPairId, setAddPairId] = useState('1')
  const [addOpen, setAddOpen] = useState(false)
  const [pending, setPending] = useState(true)
  const [message, setMessage] = useState('')
  const [messageVariant, setMessageVariant] = useState<'success' | 'danger'>('success')

  const load = useCallback(async () => {
    try {
      const result = await api.favorites({
        tradingPairId: tradingPairId ? Number(tradingPairId) : undefined,
        page,
        size: 10,
      })
      setData(result)
    } catch {
      setData(EMPTY_FAVORITES)
    } finally {
      setPending(false)
    }
  }, [tradingPairId, page])

  useEffect(() => {
    setPending(true)
    void load()
  }, [load])

  function onTradingPairFilter(value: string) {
    setPending(true)
    setTradingPairId(value)
    setPage(0)
  }

  function openAdd() {
    setMessage('')
    setAddOpen(true)
  }

  async function onAdd(e: FormEvent) {
    e.preventDefault()
    setMessage('')
    try {
      await api.addFavorite(Number(addPairId))
      setMessageVariant('success')
      setMessage('Пара добавлена в избранное')
      setPage(0)
      await load()
      setAddOpen(false)
    } catch {
    }
  }

  async function onRemove(tradingPairIdValue: number) {
    await api.removeFavorite(tradingPairIdValue)
    await load()
  }

  const isEmpty = !pending && (data?.content.length ?? 0) === 0
  const showTable = !pending && data.content.length > 0
  const favoritesContentKey = [
    tradingPairId,
    page,
    pending ? 'loading' : `${data.content.map((r) => `${r.userId}-${r.tradingPairId}`).join(',')}:${isEmpty ? 'empty' : 'rows'}`,
  ].join('|')

  return (
    <PageShell>
      <PageTitle
        title="Главная"
        icon={<HouseIcon size={22} />}
        action={(
          <Button block={false} onClick={openAdd}>
            Добавить пару
          </Button>
        )}
      />

      <PageBody>
        <Panel>
          <div className="filters-bar">
            <Select
              variant="filter"
              label="Торговая пара"
              value={tradingPairId}
              onChange={onTradingPairFilter}
              options={PAIR_FILTER_OPTIONS}
            />
          </div>
        </Panel>

        <Panel grow className="favorites-panel">
          <div className="surface favorites-panel__surface">
            <div className="panel__head">
              <h2 className="panel__title">Избранные пары</h2>
              <StatChip label="Пар" value={data?.totalElements ?? 0} />
            </div>
            <div className={`panel__body panel__body--scroll${isEmpty ? ' panel__body--center' : ''}`}>
              <ContentReveal resetKey={favoritesContentKey}>
              {isEmpty && (
                <Empty
                  icon={StarIcon}
                  title="Избранных пар пока нет"
                  subtitle="Добавьте пару, чтобы быстро возвращаться к ней"
                  primaryAction={{ label: 'Добавить пару', onClick: openAdd }}
                />
              )}
              {showTable && (
                <>
                  <div className="table-wrap">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Пара</th>
                          <th>ID пары</th>
                          <th>Добавлено</th>
                          <th className="data-table__col-action" aria-label="Действия" />
                        </tr>
                      </thead>
                      <tbody>
                        {data.content.map((row) => (
                          <tr key={`${row.userId}-${row.tradingPairId}`}>
                            <td><strong>{row.symbol.replace('_', '/')}</strong></td>
                            <td>{row.tradingPairId}</td>
                            <td>{new Date(row.addedAt).toLocaleString()}</td>
                            <td className="data-table__col-action">
                              <Button
                                type="button"
                                block={false}
                                variant="ghost"
                                className="danger"
                                onClick={() => onRemove(row.tradingPairId)}
                              >
                                Удалить
                              </Button>
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
                  />
                </>
              )}
              </ContentReveal>
            </div>
          </div>
        </Panel>
      </PageBody>

      <SidePanel
        isOpen={addOpen}
        title="Добавить в избранное"
        onClose={() => setAddOpen(false)}
        primaryLabel="Добавить"
        primaryFormId="add-favorite-form"
      >
        <form id="add-favorite-form" className="stack-form side-panel-form" onSubmit={onAdd}>
          <SidePanelField label="Торговая пара">
            <Select
              variant="sidebar"
              block
              value={addPairId}
              onChange={setAddPairId}
              options={PAIR_OPTIONS}
            />
          </SidePanelField>
          {message && <Alert variant={messageVariant}>{message}</Alert>}
        </form>
      </SidePanel>
    </PageShell>
  )
}
