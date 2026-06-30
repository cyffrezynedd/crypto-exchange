export function isSessionErrorMessage(message: string): boolean {
  const m = message.toLowerCase()
  return (
    m.includes('сессия недействительна')
    || m.includes('сессия истекла')
    || m.includes('нужно войти в аккаунт')
    || /token/.test(m)
  )
}

export function isBenignListError(message: string): boolean {
  const m = message.toLowerCase()
  return (
    m.includes('not found')
    || m.includes('не найден')
    || m.includes('запрашиваемые данные не найдены')
  )
}
