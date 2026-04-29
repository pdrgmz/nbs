import { apiClient } from '../http'

export const fetchStats = async (date, type) => {
  const params = {}

  if (date) {
    params.date = date
  }

  if (type) {
    params.type = type
  }

  const { data } = await apiClient.get('/stats', { params })
  return data
}
