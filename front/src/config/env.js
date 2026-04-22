const trimTrailingSlash = (value) => value.replace(/\/$/, '')

const defaultApiBaseUrl = import.meta.env.DEV ? '/api' : 'https://nbsb.pdrgmz.uk'

export const API_BASE_URL = trimTrailingSlash(
  import.meta.env.VITE_API_BASE_URL || defaultApiBaseUrl,
)

export const SWAGGER_URL =
  import.meta.env.VITE_SWAGGER_URL || 'https://nbsb.pdrgmz.uk/swagger-ui/index.html#/'

export const API_DOCS_URL = `${API_BASE_URL}/v3/api-docs`
