import { ref } from 'vue'

export const useAsyncState = (request) => {
  const data = ref(null)
  const error = ref('')
  const isLoading = ref(false)

  const execute = async (...args) => {
    isLoading.value = true
    error.value = ''

    try {
      data.value = await request(...args)
      return data.value
    } catch (requestError) {
      error.value = requestError.message || 'Ha ocurrido un error inesperado.'
      throw requestError
    } finally {
      isLoading.value = false
    }
  }

  return {
    data,
    error,
    isLoading,
    execute,
  }
}
