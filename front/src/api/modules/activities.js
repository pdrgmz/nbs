import { apiClient } from '../http'

export const fetchActivities = async () => {
  const { data } = await apiClient.get('/activities')
  return data
}

export const fetchActivityById = async (activityId) => {
  const { data } = await apiClient.get(`/activities/${activityId}`)
  return data
}

export const fetchActivityStream = async (activityId, type = 'velocity_smooth') => {
  const { data } = await apiClient.get(`/streams/${activityId}/${type}`)
  return data
}
