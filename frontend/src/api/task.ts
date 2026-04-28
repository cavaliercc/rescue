import http from './http'
import type { TaskStartParams, TaskStartResult, TaskStatus, LogsResult, OrderDetail, TechnicianActionRequest } from '@/types/task'

async function req<T>(method: 'get' | 'post', path: string, payload?: unknown, headers?: Record<string, string>): Promise<T> {
  const res = await http.request<{ code: number; msg: string; data: T }>({
    method,
    url: path,
    headers,
    ...(method === 'post' ? { data: payload } : { params: payload }),
  })
  return (res as any).data as T
}

export const taskApi = {
  queryOrder: (caseId: string, token?: string) =>
    req<OrderDetail>('get', `/api/external/order/${caseId}`, undefined,
      token ? { 'X-Sos-Token': token } : undefined),
  start: (params: TaskStartParams) => req<TaskStartResult>('post', '/api/task/start', params),
  pause: () => req<null>('post', '/api/task/pause'),
  resume: () => req<null>('post', '/api/task/resume'),
  stop: () => req<null>('post', '/api/task/stop'),
  getStatus: () => req<TaskStatus>('get', '/api/task/status'),
  getLogs: (page = 1, limit = 20) =>
    req<LogsResult>('get', '/api/task/logs', { page, limit }),
  technicianSign: (caseId: string, body: TechnicianActionRequest, token?: string) =>
    req<null>('post', `/api/cases/${caseId}/technician/sign`, body,
      token ? { 'X-Esb-Token': token } : undefined),
  technicianArrive: (caseId: string, body: TechnicianActionRequest, token?: string) =>
    req<null>('post', `/api/cases/${caseId}/technician/arrive`, body,
      token ? { 'X-Esb-Token': token } : undefined),
  technicianStart: (caseId: string, body: TechnicianActionRequest, token?: string) =>
    req<null>('post', `/api/cases/${caseId}/technician/start`, body,
      token ? { 'X-Esb-Token': token } : undefined),
  technicianComplete: (caseId: string, body: TechnicianActionRequest, token?: string) =>
    req<null>('post', `/api/cases/${caseId}/technician/complete`, body,
      token ? { 'X-Esb-Token': token } : undefined),
}
