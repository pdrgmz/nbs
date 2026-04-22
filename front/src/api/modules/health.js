import { apiClient } from '../http'

export const fetchHealth = async () => {
  const { data } = await apiClient.get('/health')
  return data
}
