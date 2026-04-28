import http from './http'
import type { SavedRoute, Coordinate } from '@/types/task'

async function req<T>(method: 'get' | 'post' | 'delete', path: string, payload?: unknown): Promise<T> {
  const res = await http.request<{ code: number; msg: string; data: T }>({
    method,
    url: path,
    ...(method === 'get' ? { params: payload } : { data: payload }),
  })
  return (res as any).data as T
}

export const routesApi = {
  list: () => req<SavedRoute[]>('get', '/api/routes/list'),
  save: (params: { name: string; origin: Coordinate; destination: Coordinate }) =>
    req<{ routeId: string }>('post', '/api/routes/save', params),
  delete: (routeId: string) => req<null>('delete', `/api/routes/${routeId}`),
}
