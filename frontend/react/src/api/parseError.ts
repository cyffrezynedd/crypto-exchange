const CODE_MESSAGES: Record<string, string> = {
  ALREADY_EXISTS: 'Аккаунт с таким email или никнеймом уже существует',
  NOT_FOUND: 'Запрашиваемые данные не найдены',
  ORDER_NOT_FOUND: 'Ордер не найден',
  WALLET_NOT_FOUND: 'Кошелёк не найден',
  VALIDATION_FAILED: 'Проверьте правильность введённых данных',
  INVALID_ARGUMENT: 'Проверьте правильность введённых данных',
  UNAUTHENTICATED: 'Нужно войти в аккаунт',
  UNAUTHORIZED: 'Нужно войти в аккаунт',
  TOKEN_INVALID: 'Сессия недействительна. Войдите снова',
  TOKEN_EXPIRED: 'Сессия истекла. Войдите снова',
  PERMISSION_DENIED: 'Недостаточно прав для этого действия',
  FORBIDDEN: 'Недостаточно прав для этого действия',
  INSUFFICIENT_FUNDS: 'Недостаточно средств на балансе',
  SERVICE_UNAVAILABLE: 'Сервис временно недоступен. Попробуйте позже',
  INTERNAL: 'Внутренняя ошибка сервера. Попробуйте позже',
  UNKNOWN: 'Внутренняя ошибка сервера. Попробуйте позже',
  UPSTREAM_TIMEOUT: 'Сервер не ответил вовремя. Попробуйте позже',
  RATE_LIMITED: 'Слишком много запросов. Подождите немного',
}

const STATUS_MESSAGES: Record<number, string> = {
  400: 'Проверьте правильность введённых данных',
  401: 'Нужно войти в аккаунт',
  403: 'Недостаточно прав для этого действия',
  404: 'Запрашиваемые данные не найдены',
  409: 'Аккаунт с таким email или никнеймом уже существует',
  422: 'Не удалось выполнить операцию',
  500: 'Внутренняя ошибка сервера. Попробуйте позже',
  502: 'Не удалось подключиться к API. Убедитесь, что gateway запущен.',
  503: 'Сервис временно недоступен. Попробуйте позже',
  504: 'Сервер не ответил вовремя. Попробуйте позже',
}

const DETAIL_PATTERNS: Array<[RegExp, string]> = [
  [/invalid email or password/i, 'Неверный email или пароль'],
  [/email already exists/i, 'Этот email уже зарегистрирован'],
  [/username already exists/i, 'Этот никнейм уже занят'],
  [/favorite pair not found/i, 'Эта пара не найдена в избранном'],
  [/pair already in favorites/i, 'Эта пара уже в избранном'],
  [/order not found/i, 'Ордер не найден'],
  [/wallet not found/i, 'Кошелёк не найден'],
  [/trading pair not found/i, 'Торговая пара не найдена'],
  [/temporarily unavailable/i, 'Сервис ещё запускается. Подождите несколько секунд и попробуйте снова'],
  [/circuit_open/i, 'Сервис ещё запускается. Подождите несколько секунд и попробуйте снова'],
]

const ENGLISH_DETAIL_MESSAGES: Record<string, string> = {
  'internal server error': STATUS_MESSAGES[500]!,
  'bad request': STATUS_MESSAGES[400]!,
  'unauthorized': STATUS_MESSAGES[401]!,
  'forbidden': STATUS_MESSAGES[403]!,
  'not found': STATUS_MESSAGES[404]!,
  'conflict': STATUS_MESSAGES[409]!,
  'service unavailable': STATUS_MESSAGES[503]!,
  'gateway timeout': STATUS_MESSAGES[504]!,
}

function translateEnglishDetail(detail: string): string | undefined {
  const normalized = detail.trim().toLowerCase()
  return ENGLISH_DETAIL_MESSAGES[normalized]
}

function messageForStatus(status?: number): string | undefined {
  if (status == null) return undefined
  return STATUS_MESSAGES[status]
}

function isHumanReadable(text: string): boolean {
  const trimmed = text.trim()
  if (!trimmed || trimmed.startsWith('{') || trimmed.startsWith('[')) return false
  return trimmed.length <= 200
}

function isGatewayUnreachable(status?: number, raw?: string): boolean {
  if (status !== 502) return false
  const body = raw?.trim() ?? ''
  if (!body) return true
  return !body.startsWith('{')
}

export function parseApiError(raw: string, status?: number): string {
  if (isGatewayUnreachable(status, raw)) {
    return STATUS_MESSAGES[502]
  }

  if (!raw?.trim()) {
    return messageForStatus(status) ?? 'Что-то пошло не так. Попробуйте ещё раз'
  }

  try {
    const data = JSON.parse(raw) as {
      code?: string
      title?: string
      detail?: string
      message?: string
      error?: string
      status?: number | string
    }

    if (data.status === 'CIRCUIT_OPEN') {
      return 'Сервис ещё запускается. Подождите несколько секунд и попробуйте снова'
    }

    const detail = data.detail ?? data.message ?? data.error
    if (typeof detail === 'string') {
      const translated = translateEnglishDetail(detail)
      if (translated) return translated

      for (const [pattern, message] of DETAIL_PATTERNS) {
        if (pattern.test(detail)) return message
      }

      if (isHumanReadable(detail) && /[а-яё]/i.test(detail)) {
        return detail
      }
    }

    if (typeof data.title === 'string') {
      const fromTitle = translateEnglishDetail(data.title)
      if (fromTitle) return fromTitle
    }

    const code = data.code
    if (code && CODE_MESSAGES[code]) return CODE_MESSAGES[code]

    const responseStatus = typeof data.status === 'number' ? data.status : status
    const statusMessage = messageForStatus(responseStatus)
    if (statusMessage) return statusMessage
  } catch {
    const translated = translateEnglishDetail(raw)
    if (translated) return translated
    if (isHumanReadable(raw) && /[а-яё]/i.test(raw)) return raw.trim()
  }

  return messageForStatus(status) ?? 'Что-то пошло не так. Попробуйте ещё раз'
}
