import { apiClient } from '../http'

export const fetchStats = async (date) => {
  const params = date ? { date } : undefined
  const { data } = await apiClient.get('/stats', { params })
  return data
}
