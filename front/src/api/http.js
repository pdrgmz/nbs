import axios from 'axios'
import { API_BASE_URL } from '../config/env'

const TOKEN_STORAGE_KEY = 'nbs.api.token'
const FIXED_BEARER_TOKEN = '60270508'

export const getStoredToken = () => localStorage.getItem(TOKEN_STORAGE_KEY) || ''

export const setStoredToken = (token) => {
  const normalizedToken = token.trim()

  if (!normalizedToken) {
    localStorage.removeItem(TOKEN_STORAGE_KEY)
    return
  }

  localStorage.setItem(TOKEN_STORAGE_KEY, normalizedToken)
}

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
})

apiClient.interceptors.request.use((config) => {
  config.headers.Authorization = `Bearer ${FIXED_BEARER_TOKEN}`

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const payload = error.response?.data
    const details = typeof payload === 'string' ? payload : payload?.message

    if (status === 401) {
      error.message = 'La API ha respondido 401. Revisa o añade un token Bearer.'
      return Promise.reject(error)
    }

    if (status === 403) {
      error.message = 'La API ha respondido 403. El token no tiene permisos suficientes.'
      return Promise.reject(error)
    }

    if (details) {
      error.message = details
      return Promise.reject(error)
    }

    if (!error.response) {
      error.message = 'No se pudo contactar con la API. Revisa la URL base o CORS.'
    }

    return Promise.reject(error)
  },
)
