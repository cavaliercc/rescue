export interface Coordinate {
  address: string
  lon: string
  lat: string
}

export type TaskState = 'idle' | 'running' | 'paused' | 'stopped' | 'completed'

export interface TaskStartParams {
  caseId: string
  workId: string
  origin: Coordinate
  destination: Coordinate
  speed: number
  interval: number
  routePoints?: Array<{ lon: string; lat: string }>
  reportTaskId?: string
  handleUserId?: string
  handleOrgId?: string
  handleUserName?: string
  caseFromOrgId?: number
  deviceToken?: string
}

export interface TaskStartResult {
  taskId: string
  totalPoints: number
  estimatedDuration: number
  routeDistance: number
}

export interface TaskStatus {
  taskId: string
  state: TaskState
  reportedCount: number
  totalPoints: number
  progress: number
  remainingTime: number
  currentPosition: { lon: string; lat: string }
  lastReportTime: string
  lastReportSuccess: boolean
}

export interface ReportLog {
  seq: number
  timestamp: string
  lon: string
  lat: string
  success: boolean
  responseCode: number
  responseMsg: string
}

export interface LogsResult {
  total: number
  logs: ReportLog[]
}

export interface OrderDetail {
  caseId: string
  workId: string
  vehNo: string
  userName: string
  userPhone: string
  rescueAddress: string
  rescueLon: string
  rescueLat: string
  rescueCityName: string
  rescueCountyName: string
  caseFromOrgId: number
  rescueType: number
  rescueCategoryName: string
  caseStatusName: string
  destAddress?: string
  destLon?: string
  destLat?: string
}

export interface TechnicianActionRequest {
  taskId: string
  handleUserId: string
  handleOrgId?: string
  handleUserName?: string
  deviceToken?: string
  dataTransMethod?: number
  caseFromOrgId?: number
  rescueRealAddressLat?: number
  rescueRealAddressLon?: number
}

export interface SavedRoute {
  routeId: string
  name: string
  origin: Coordinate
  destination: Coordinate
  usedCount: number
  lastUsedAt: string
}
