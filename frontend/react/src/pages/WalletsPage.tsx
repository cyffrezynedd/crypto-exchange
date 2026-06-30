import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'
import { api } from '../api/client'
import type { Wallet } from '../api/types'
import { PageBody, PageShell, PageTitle, Panel, StatChip } from '../components/PageShell'
import { WalletOutlinedIcon } from '../components/icons'
import { Alert } from '../components/ui/Alert'
import { Button } from '../components/ui/Button'
import { Empty } from '../components/ui/Empty'
import { Select } from '../components/ui/Select'
import { SidePanel } from '../components/ui/SidePanel'
import { SidePanelField } from '../components/ui/SidePanelField'
import { CURRENCY_LABELS } from '../constants'
import '../styles/tables.css'

const CURRENCY_OPTIONS = [
  { value: '3', label: 'USDT' },
  { value: '1', label: 'BTC' },
  { value: '2', label: 'ETH' },
]

export function WalletsPage() {
  const [wallets, setWallets] = useState<Wallet[]>([])
  const [currencyId, setCurrencyId] = useState('3')
  const [amount, setAmount] = useState('1000')
  const [depositOpen, setDepositOpen] = useState(false)
  const [message, setMessage] = useState('')
  const [messageVariant, setMessageVariant] = useState<'success' | 'danger'>('success')
  const [loading, setLoading] = useState(true)

  async function load() {
    try {
      setWallets(await api.wallets())
    } catch {
      setWallets([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  function openDeposit() {
    setMessage('')
    setDepositOpen(true)
  }

  async function onDeposit(e: FormEvent) {
    e.preventDefault()
    setMessage('')
    try {
      await api.deposit({
        eventId: crypto.randomUUID(),
        currencyId: Number(currencyId),
        amount,
      })
      setMessageVariant('success')
      setMessage('Депозит зачислен')
      await load()
      setDepositOpen(false)
    } catch (err) {
      setMessageVariant('danger')
      setMessage(err instanceof Error ? err.message : 'Ошибка депозита')
    }
  }

  const isEmpty = !loading && wallets.length === 0

  return (
    <PageShell>
      <PageTitle
        title="Кошельки"
        icon={<WalletOutlinedIcon size={22} />}
        action={(
          <Button block={false} onClick={openDeposit}>
            Пополнить
          </Button>
        )}
      />

      <PageBody>
        <Panel grow>
          <div className="panel__head">
            <h2 className="panel__title">Балансы</h2>
            <StatChip label="Валют" value={wallets.length} />
          </div>
          <div className={`panel__body panel__body--scroll${isEmpty ? ' panel__body--center' : ''}`}>
            {isEmpty && (
              <Empty
                icon={WalletOutlinedIcon}
                title="Здесь будут ваши балансы"
                subtitle="Пополните кошелёк USDT, чтобы начать торговать"
                primaryAction={{ label: 'Пополнить USDT', onClick: openDeposit }}
              />
            )}
            {wallets.length > 0 && (
              <div className="wallet-grid">
                {wallets.map((w) => (
                  <article key={w.id} className="wallet-card">
                    <span className="wallet-code">{CURRENCY_LABELS[w.currencyId] ?? w.currencyId}</span>
                    <strong>{w.availableBalance}</strong>
                    <small>Заблокировано: {w.lockedBalance}</small>
                  </article>
                ))}
              </div>
            )}
          </div>
        </Panel>
      </PageBody>

      <SidePanel
        isOpen={depositOpen}
        title="Депозит"
        onClose={() => setDepositOpen(false)}
        primaryLabel="Пополнить"
        primaryFormId="deposit-form"
      >
        <form id="deposit-form" className="stack-form side-panel-form" onSubmit={onDeposit}>
          <SidePanelField label="Валюта">
            <Select
              variant="sidebar"
              block
              value={currencyId}
              onChange={setCurrencyId}
              options={CURRENCY_OPTIONS}
            />
          </SidePanelField>
          <SidePanelField label="Сумма">
            <input
              className="side-panel-field__input"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
            />
          </SidePanelField>
          {message && <Alert variant={messageVariant}>{message}</Alert>}
        </form>
      </SidePanel>
    </PageShell>
  )
}
