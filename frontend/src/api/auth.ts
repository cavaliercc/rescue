import http from './http'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  username: string
}

export const login = (params: LoginParams) =>
  http.post<never, { code: number; msg: string; data: LoginResult }>('/api/auth/login', params)
