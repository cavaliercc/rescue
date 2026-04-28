<template>
  <div class="task-view">
    <!-- Tokens -->
    <el-card class="mb-4" shadow="never" body-style="padding: 10px 16px;">
      <div class="flex flex-col gap-2">
        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-600 font-medium whitespace-nowrap">SOS Token：</span>
          <el-input
            v-model="sosToken"
            placeholder="粘贴登录后的 token（可选，用于查询订单）"
            show-password
            clearable
            class="flex-1"
            @change="saveToken"
          />
        </div>
        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-600 font-medium whitespace-nowrap">ESB Token：</span>
          <el-input
            v-model="esbToken"
            placeholder="粘贴 ESB Token（用于签收/到达/开始/完成）"
            show-password
            clearable
            class="flex-1"
            @change="saveEsbToken"
          />
        </div>
      </div>
    </el-card>

    <!-- 历史路线 -->
    <el-card class="mb-4" shadow="never" body-style="padding: 12px 16px;">
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-600 font-medium whitespace-nowrap">历史路线：</span>
        <el-select
          v-model="selectedRouteId"
          placeholder="从历史路线选择（可跳过）"
          clearable
          class="w-72"
          :disabled="!isIdle"
          @change="applyHistoryRoute"
        >
          <el-option
            v-for="r in savedRoutes"
            :key="r.routeId"
            :label="r.name"
            :value="r.routeId"
          >
            <span>{{ r.name }}</span>
            <span class="text-xs text-gray-400 ml-2">使用 {{ r.usedCount }} 次</span>
          </el-option>
        </el-select>
        <el-button
          v-if="savedRoutes.length > 0 && selectedRouteId"
          type="danger"
          link
          size="small"
          @click="deleteHistoryRoute(selectedRouteId)"
        >删除</el-button>
        <span class="text-xs text-gray-400 ml-auto">共 {{ savedRoutes.length }} 条历史路线</span>
      </div>
    </el-card>

    <!-- 主内容区 -->
    <div class="flex gap-4 items-start">
      <!-- 左侧：配置表单 -->
      <el-card class="w-[380px] flex-shrink-0" shadow="never">
        <template #header>
          <span class="font-semibold text-gray-700">任务配置</span>
          <el-tag class="ml-2" :type="stateTagType" size="small">{{ stateLabel }}</el-tag>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="default"
          :disabled="!isIdle"
        >
          <!-- 订单号 -->
          <el-form-item label="订单号 (caseId)" prop="caseId">
            <el-input v-model="form.caseId" placeholder="如：2604102700015" clearable>
              <template #append>
                <el-button :loading="querying" @click="queryOrder">查询</el-button>
              </template>
            </el-input>
          </el-form-item>

          <!-- 订单摘要 -->
          <div v-if="orderSummary" class="flex flex-col gap-1 -mt-3 mb-3 p-2 bg-blue-50 rounded text-xs text-gray-600">
            <div><span class="font-medium">车牌：</span>{{ orderSummary.vehNo }}</div>
            <div><span class="font-medium">车主：</span>{{ orderSummary.userName }} {{ orderSummary.userPhone }}</div>
            <div><span class="font-medium">救援地址：</span>{{ orderSummary.rescueAddress }}</div>
            <div><span class="font-medium">类型：</span>{{ orderSummary.rescueCategoryName }} · {{ orderSummary.caseStatusName }}</div>
          </div>

          <!-- 起点 -->
          <div class="mb-1 font-medium text-sm text-gray-600">起点</div>
          <el-form-item prop="originAddress">
            <el-input
              v-model="form.originAddress"
              placeholder="输入地址（如：南京南站）或经纬度（118.77,31.97）"
              clearable
              @blur="handleGeocode('origin')"
            >
              <template #append>
                <el-button
                  :loading="geocoding.origin"
                  :icon="Search"
                  @click="handleGeocode('origin')"
                />
              </template>
            </el-input>
          </el-form-item>
          <div v-if="form.originCoord" class="flex items-center gap-1 -mt-3 mb-3">
            <el-icon class="text-green-500"><CircleCheck /></el-icon>
            <span class="text-xs text-gray-500">
              {{ form.originCoord.lon }}, {{ form.originCoord.lat }}
            </span>
            <el-button link size="small" @click="clearCoord('origin')">清除</el-button>
          </div>

          <!-- 终点 -->
          <div class="mb-1 font-medium text-sm text-gray-600">终点</div>
          <el-form-item prop="destAddress">
            <el-input
              v-model="form.destAddress"
              placeholder="输入地址或经纬度"
              clearable
              @blur="handleGeocode('dest')"
            >
              <template #append>
                <el-button
                  :loading="geocoding.dest"
                  :icon="Search"
                  @click="handleGeocode('dest')"
                />
              </template>
            </el-input>
          </el-form-item>
          <div v-if="form.destCoord" class="flex items-center gap-1 -mt-3 mb-3">
            <el-icon class="text-green-500"><CircleCheck /></el-icon>
            <span class="text-xs text-gray-500">
              {{ form.destCoord.lon }}, {{ form.destCoord.lat }}
            </span>
            <el-button link size="small" @click="clearCoord('dest')">清除</el-button>
          </div>

          <!-- 速度 -->
          <el-form-item label="行驶速度 (km/h)" prop="speed">
            <el-radio-group v-model="form.speedPreset" class="mb-2" @change="onSpeedPresetChange">
              <el-radio-button label="40">40</el-radio-button>
              <el-radio-button label="60">60</el-radio-button>
              <el-radio-button label="80">80</el-radio-button>
              <el-radio-button label="custom">自定义</el-radio-button>
            </el-radio-group>
            <el-input-number
              v-if="form.speedPreset === 'custom'"
              v-model="form.speed"
              :min="10"
              :max="200"
              :step="10"
              controls-position="right"
              style="width: 120px"
            />
          </el-form-item>

          <!-- 上报频率 -->
          <el-form-item label="上报频率" prop="interval">
            <el-radio-group v-model="form.interval">
              <el-radio-button :label="10">10秒（短途）</el-radio-button>
              <el-radio-button :label="20">20秒</el-radio-button>
              <el-radio-button :label="30">30秒（默认）</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <!-- 工单号 -->
          <el-form-item label="工单号 (workId)" prop="workId">
            <el-input
              v-model="form.workId"
              placeholder="如：DRTC_RO_20260410_QV0010"
              clearable
            />
          </el-form-item>

          <!-- GPS 上报参数 -->
          <el-divider content-position="left"><span class="text-xs text-gray-400">GPS 上报参数</span></el-divider>

          <el-form-item label="外部任务号 (taskId)">
            <el-input v-model="form.reportTaskId" placeholder="如：26042885000019" clearable />
          </el-form-item>

          <el-form-item label="技师账号 (handleUserId)">
            <el-input v-model="form.handleUserId" placeholder="如：18102999000003" clearable />
          </el-form-item>

          <el-form-item label="机构ID (handleOrgId)">
            <el-input v-model="form.handleOrgId" placeholder="如：1057" clearable />
          </el-form-item>

          <el-form-item label="技师用户名 (handleUserName)">
            <el-input v-model="form.handleUserName" placeholder="如：diaodu123@1057" clearable />
          </el-form-item>

          <el-form-item label="案例来源机构 (caseFromOrgId)">
            <el-input v-model="form.caseFromOrgId" placeholder="如：1069" clearable />
          </el-form-item>

          <el-form-item label="设备Token (deviceToken)">
            <el-input v-model="form.deviceToken" placeholder="如：54424e5f-912b-4aa3-80e0-48706bb82a88" clearable />
          </el-form-item>
        </el-form>

        <!-- 操作按钮 -->
        <div class="flex flex-col gap-2 mt-2">
          <el-button
            class="w-full"
            :loading="previewing"
            :disabled="!canPreview"
            @click="previewRoute"
          >
            <el-icon class="mr-1"><MapLocation /></el-icon>
            预览路线
          </el-button>

          <el-divider class="my-1" />

          <el-button
            v-if="isIdle"
            type="primary"
            class="w-full"
            :loading="starting"
            :disabled="!canStart"
            @click="startTask"
          >
            <el-icon class="mr-1"><VideoPlay /></el-icon>
            开始任务
          </el-button>

          <template v-if="taskState === 'running'">
            <el-button type="warning" class="w-full" :loading="controlling" @click="pauseTask">
              <el-icon class="mr-1"><VideoPause /></el-icon>
              暂停
            </el-button>
            <el-button type="danger" class="w-full" :loading="controlling" @click="stopTask">
              <el-icon class="mr-1"><SwitchButton /></el-icon>
              停止任务
            </el-button>
          </template>

          <template v-if="taskState === 'paused'">
            <el-button type="success" class="w-full" :loading="controlling" @click="resumeTask">
              <el-icon class="mr-1"><VideoPlay /></el-icon>
              继续
            </el-button>
            <el-button type="danger" class="w-full" :loading="controlling" @click="stopTask">
              <el-icon class="mr-1"><SwitchButton /></el-icon>
              停止任务
            </el-button>
          </template>
        </div>

        <!-- 救援节点 -->
        <el-divider content-position="left" class="mt-3">
          <span class="text-xs text-gray-400">救援节点</span>
        </el-divider>
        <div class="grid grid-cols-2 gap-2">
          <el-button
            :loading="rescueActioning === 'sign'"
            :disabled="!canRescueAction"
            size="small"
            @click="technicianAction('sign')"
          >技师签收</el-button>
          <el-button
            :loading="rescueActioning === 'arrive'"
            :disabled="!canRescueAction"
            size="small"
            @click="technicianAction('arrive')"
          >技师到达</el-button>
          <el-button
            :loading="rescueActioning === 'start'"
            :disabled="!canRescueAction"
            size="small"
            @click="technicianAction('start')"
          >技师开始</el-button>
          <el-button
            type="success"
            :loading="rescueActioning === 'complete'"
            :disabled="!canRescueAction"
            size="small"
            @click="technicianAction('complete')"
          >完成</el-button>
        </div>

        <!-- 路线信息 -->
        <div v-if="routeInfo" class="mt-4 p-3 bg-blue-50 rounded-md text-sm">
          <div class="flex justify-between mb-1">
            <span class="text-gray-600">总里程</span>
            <span class="font-medium">{{ (routeInfo.distance / 1000).toFixed(1) }} km</span>
          </div>
          <div class="flex justify-between mb-1">
            <span class="text-gray-600">预计时长</span>
            <span class="font-medium">{{ formatDuration(routeInfo.duration) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">采样点数</span>
            <span class="font-medium">{{ routePoints.length }} 个</span>
          </div>
        </div>
      </el-card>

      <!-- 右侧：地图 + 状态 + 日志 -->
      <div class="flex-1 min-w-0 flex flex-col gap-4">
        <!-- 地图 -->
        <el-card shadow="never" body-style="padding: 0; height: 420px;">
          <MapPicker
            :origin="mapOrigin"
            :destination="mapDest"
            :route-points="routePoints"
            :current-position="taskStatus?.currentPosition ?? null"
          />
        </el-card>

        <!-- 后端连接异常横幅 -->
        <el-alert
          v-if="backendError"
          title="后端连接异常，请检查网络或后端服务"
          type="error"
          :closable="false"
          show-icon
        />

        <!-- 任务状态卡片 -->
        <el-card v-if="taskStatus && !isIdle" shadow="never">
          <template #header>
            <span class="font-semibold text-gray-700">任务进度</span>
            <span class="text-xs text-gray-400 ml-2">{{ taskStatus.taskId }}</span>
          </template>

          <el-progress
            :percentage="taskStatus.progress"
            :status="taskState === 'paused' ? 'warning' : taskState === 'completed' ? 'success' : undefined"
            class="mb-4"
          />

          <div class="grid grid-cols-2 gap-3 text-sm">
            <div class="stat-item">
              <span class="stat-label">已上报</span>
              <span class="stat-value">{{ taskStatus.reportedCount }} / {{ taskStatus.totalPoints }} 次</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">剩余时间</span>
              <span class="stat-value">{{ formatDuration(taskStatus.remainingTime) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">当前位置</span>
              <span class="stat-value text-xs">
                {{ taskStatus.currentPosition?.lon }}, {{ taskStatus.currentPosition?.lat }}
              </span>
            </div>
            <div class="stat-item">
              <span class="stat-label">上次上报</span>
              <span class="stat-value">
                <el-icon v-if="taskStatus.lastReportSuccess" class="text-green-500"><SuccessFilled /></el-icon>
                <el-icon v-else class="text-red-500"><CircleCloseFilled /></el-icon>
                {{ taskStatus.lastReportTime?.slice(11) || '-' }}
              </span>
            </div>
          </div>
        </el-card>

        <!-- 上报日志 -->
        <el-card shadow="never">
          <template #header>
            <span class="font-semibold text-gray-700">上报日志</span>
            <span class="text-xs text-gray-400 ml-2">共 {{ logTotal }} 条</span>
          </template>

          <el-table
            :data="logs"
            size="small"
            max-height="280"
            :row-class-name="logRowClass"
            empty-text="暂无日志"
          >
            <el-table-column prop="seq" label="#" width="50" align="center" />
            <el-table-column prop="timestamp" label="时间" width="160">
              <template #default="{ row }">
                {{ row.timestamp?.slice(11) || row.timestamp }}
              </template>
            </el-table-column>
            <el-table-column label="经纬度" min-width="160">
              <template #default="{ row }">
                <span class="text-xs font-mono">{{ row.lon }}, {{ row.lat }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.success ? 'success' : 'danger'" size="small">
                  {{ row.success ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="responseMsg" label="响应" min-width="80" />
          </el-table>

          <div v-if="logTotal > logsPageSize" class="flex justify-center mt-3">
            <el-pagination
              v-model:current-page="logsPage"
              :page-size="logsPageSize"
              :total="logTotal"
              layout="prev, pager, next"
              small
              @current-change="fetchLogs"
            />
          </div>
        </el-card>
      </div>
    </div>

    <!-- 保存路线对话框 -->
    <el-dialog v-model="saveRouteVisible" title="保存路线" width="400px" :close-on-click-modal="false">
      <el-form @submit.prevent>
        <el-form-item label="路线名称">
          <el-input
            v-model="saveRouteName"
            placeholder="如：南京南站-常州北站"
            maxlength="50"
            show-word-limit
            autofocus
          />
        </el-form-item>
        <div class="text-sm text-gray-500">
          <div>起点：{{ form.originAddress }}</div>
          <div>终点：{{ form.destAddress }}</div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="saveRouteVisible = false">跳过</el-button>
        <el-button type="primary" :loading="savingRoute" @click="saveRoute">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onUnmounted } from 'vue'
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from 'element-plus'
import {
  Search,
  MapLocation,
  VideoPlay,
  VideoPause,
  SwitchButton,
  CircleCheck,
  SuccessFilled,
  CircleCloseFilled,
} from '@element-plus/icons-vue'
import MapPicker from '@/components/MapPicker.vue'
import { taskApi } from '@/api/task'
import { routesApi } from '@/api/routes'
import { geocodeAddress, planDrivingRoute } from '@/utils/amap'
import type { TaskState, TaskStatus, ReportLog, SavedRoute, OrderDetail, TechnicianActionRequest } from '@/types/task'

// ── SOS Token ─────────────────────────────────────────────────────────────────

const sosToken = ref(localStorage.getItem('sos_token') || '')
function saveToken() { localStorage.setItem('sos_token', sosToken.value) }

const esbToken = ref(localStorage.getItem('esb_token') || '')
function saveEsbToken() { localStorage.setItem('esb_token', esbToken.value) }

// ── Order query ───────────────────────────────────────────────────────────────

const querying = ref(false)
const orderSummary = ref<OrderDetail | null>(null)

async function queryOrder() {
  if (!form.caseId.trim()) { ElMessage.warning('请先输入订单号'); return }
  querying.value = true
  try {
    const detail = await taskApi.queryOrder(form.caseId.trim(), sosToken.value || undefined)
    orderSummary.value = detail
    form.originAddress = detail.rescueAddress
    if (detail.rescueLon && detail.rescueLat) {
      form.originCoord = { lon: detail.rescueLon, lat: detail.rescueLat }
    }
    if (detail.workId) form.workId = detail.workId
    if (detail.caseFromOrgId) form.caseFromOrgId = String(detail.caseFromOrgId)
    if (detail.destAddress) form.destAddress = detail.destAddress
    if (detail.destLon && detail.destLat) {
      form.destCoord = { lon: detail.destLon, lat: detail.destLat }
    }
    ElMessage.success('订单信息已填入')
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    querying.value = false
  }
}

// ── Form ──────────────────────────────────────────────────────────────────────

const formRef = ref<FormInstance>()
const form = reactive({
  originAddress: '',
  destAddress: '',
  originCoord: null as { lon: string; lat: string } | null,
  destCoord: null as { lon: string; lat: string } | null,
  speedPreset: '60',
  speed: 60,
  interval: 30,
  caseId: '',
  workId: '',
  reportTaskId: '',
  handleUserId: '',
  handleOrgId: '',
  handleUserName: '',
  caseFromOrgId: '',
  deviceToken: '',
})

const rules: FormRules = {
  originAddress: [{ required: true, message: '请输入起点地址', trigger: 'blur' }],
  destAddress: [{ required: true, message: '请输入终点地址', trigger: 'blur' }],
  caseId: [
    { required: true, message: '请输入订单号', trigger: 'blur' },
    { max: 50, message: '长度不能超过 50 个字符', trigger: 'blur' },
  ],
  workId: [
    { required: true, message: '请输入工单号', trigger: 'blur' },
    { max: 50, message: '长度不能超过 50 个字符', trigger: 'blur' },
  ],
}

// ── Geocoding ─────────────────────────────────────────────────────────────────

const geocoding = reactive({ origin: false, dest: false })

const LAT_LON_RE = /^(-?\d+\.?\d*),\s*(-?\d+\.?\d*)$/

async function handleGeocode(side: 'origin' | 'dest') {
  const address = side === 'origin' ? form.originAddress : form.destAddress
  if (!address.trim()) return

  const match = address.match(LAT_LON_RE)
  if (match) {
    const coord = { lon: match[1], lat: match[2] }
    if (side === 'origin') form.originCoord = coord
    else form.destCoord = coord
    return
  }

  geocoding[side] = true
  try {
    const result = await geocodeAddress(address)
    if (!result) {
      ElMessage.warning('地址未找到，请修改后重试')
      return
    }
    if (side === 'origin') form.originCoord = result
    else form.destCoord = result
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    geocoding[side] = false
  }
}

function clearCoord(side: 'origin' | 'dest') {
  if (side === 'origin') form.originCoord = null
  else form.destCoord = null
}

// ── Speed ─────────────────────────────────────────────────────────────────────

function onSpeedPresetChange(val: string) {
  if (val !== 'custom') form.speed = parseInt(val)
}

// ── Route preview ─────────────────────────────────────────────────────────────

interface RouteInfo {
  distance: number
  duration: number
}

const previewing = ref(false)
const routeInfo = ref<RouteInfo | null>(null)
const routePoints = ref<Array<{ lon: string; lat: string }>>([])

const canPreview = computed(
  () => !!form.originCoord && !!form.destCoord && isIdle.value,
)

async function previewRoute() {
  if (!form.originCoord || !form.destCoord) {
    ElMessage.warning('请先完成起终点地理编码')
    return
  }

  if (
    form.originCoord.lon === form.destCoord.lon &&
    form.originCoord.lat === form.destCoord.lat
  ) {
    ElMessage.warning('起终点不能相同')
    return
  }

  previewing.value = true
  try {
    const result = await planDrivingRoute(form.originCoord, form.destCoord)
    routePoints.value = result.points
    routeInfo.value = { distance: result.distance, duration: result.duration }
    ElMessage.success(
      `路线规划成功，全程 ${(result.distance / 1000).toFixed(1)} km`,
    )
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    previewing.value = false
  }
}

// ── Task control ──────────────────────────────────────────────────────────────

const taskState = ref<TaskState>('idle')
const taskStatus = ref<TaskStatus | null>(null)
const starting = ref(false)
const controlling = ref(false)

const isIdle = computed(() => taskState.value === 'idle')

const canStart = computed(
  () =>
    !!form.originCoord &&
    !!form.destCoord &&
    !!form.caseId.trim() &&
    !!form.workId.trim() &&
    routePoints.value.length > 0,
)

async function startTask() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!form.originCoord || !form.destCoord) {
    ElMessage.warning('请先完成起终点地理编码')
    return
  }
  if (routePoints.value.length === 0) {
    ElMessage.warning('请先点击「预览路线」')
    return
  }

  starting.value = true
  try {
    const result = await taskApi.start({
      caseId: form.caseId,
      workId: form.workId,
      origin: { address: form.originAddress, ...form.originCoord },
      destination: { address: form.destAddress, ...form.destCoord },
      speed: form.speed,
      interval: form.interval,
      routePoints: routePoints.value,
      reportTaskId: form.reportTaskId || undefined,
      handleUserId: form.handleUserId || undefined,
      handleOrgId: form.handleOrgId || undefined,
      handleUserName: form.handleUserName || undefined,
      caseFromOrgId: form.caseFromOrgId ? parseInt(form.caseFromOrgId) : undefined,
      deviceToken: form.deviceToken || undefined,
    })
    taskState.value = 'running'
    ElMessage.success(
      `任务已启动，共 ${result.totalPoints} 个采样点，预计 ${formatDuration(result.estimatedDuration)}`,
    )
    startPolling()
    fetchLogs()
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    starting.value = false
  }
}

async function pauseTask() {
  controlling.value = true
  try {
    await taskApi.pause()
    taskState.value = 'paused'
    ElMessage.info('任务已暂停')
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    controlling.value = false
  }
}

async function resumeTask() {
  controlling.value = true
  try {
    await taskApi.resume()
    taskState.value = 'running'
    ElMessage.success('任务已继续')
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    controlling.value = false
  }
}

async function stopTask() {
  await ElMessageBox.confirm('确定要停止当前任务吗？进度将清零。', '停止任务', {
    type: 'warning',
    confirmButtonText: '停止',
    cancelButtonText: '取消',
    confirmButtonClass: 'el-button--danger',
  }).catch(() => undefined)

  controlling.value = true
  try {
    await taskApi.stop()
    stopPolling()
    taskState.value = 'idle'
    taskStatus.value = null
    ElMessage.info('任务已停止')
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    controlling.value = false
  }
}

// ── Status polling ─────────────────────────────────────────────────────────────

let pollTimer: ReturnType<typeof setInterval> | null = null
const backendErrorCount = ref(0)
const backendError = computed(() => backendErrorCount.value >= 3)

function startPolling() {
  stopPolling()
  pollTimer = setInterval(pollStatus, 5000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

async function pollStatus() {
  try {
    const status = await taskApi.getStatus()
    taskStatus.value = status
    taskState.value = status.state
    backendErrorCount.value = 0

    if (status.state === 'completed') {
      stopPolling()
      fetchLogs()
      saveRouteVisible.value = true
    }
    if (status.state === 'stopped') {
      stopPolling()
    }
  } catch {
    backendErrorCount.value++
  }
}

// ── Logs ──────────────────────────────────────────────────────────────────────

const logs = ref<ReportLog[]>([])
const logTotal = ref(0)
const logsPage = ref(1)
const logsPageSize = 20

async function fetchLogs() {
  try {
    const result = await taskApi.getLogs(logsPage.value, logsPageSize)
    logs.value = result.logs
    logTotal.value = result.total
  } catch {
    // non-critical
  }
}

function logRowClass({ row }: { row: ReportLog }) {
  return row.success ? '' : 'log-row-error'
}

// ── History routes ─────────────────────────────────────────────────────────────

const savedRoutes = ref<SavedRoute[]>([])
const selectedRouteId = ref('')

async function loadSavedRoutes() {
  try {
    savedRoutes.value = await routesApi.list()
  } catch {
    // non-critical
  }
}

function applyHistoryRoute(routeId: string) {
  const route = savedRoutes.value.find((r) => r.routeId === routeId)
  if (!route) return
  form.originAddress = route.origin.address
  form.destAddress = route.destination.address
  form.originCoord = { lon: route.origin.lon, lat: route.origin.lat }
  form.destCoord = { lon: route.destination.lon, lat: route.destination.lat }
  ElMessage.success(`已加载路线：${route.name}`)
}

async function deleteHistoryRoute(routeId: string) {
  await ElMessageBox.confirm('确定删除该历史路线？', '删除', {
    type: 'warning',
  }).catch(() => { throw new Error('cancelled') })

  try {
    await routesApi.delete(routeId)
    selectedRouteId.value = ''
    await loadSavedRoutes()
    ElMessage.success('已删除')
  } catch (e) {
    if ((e as Error).message !== 'cancelled') ElMessage.error((e as Error).message)
  }
}

// ── Save route dialog ─────────────────────────────────────────────────────────

const saveRouteVisible = ref(false)
const saveRouteName = ref('')
const savingRoute = ref(false)

async function saveRoute() {
  if (!saveRouteName.value.trim()) {
    ElMessage.warning('请输入路线名称')
    return
  }
  if (!form.originCoord || !form.destCoord) return

  savingRoute.value = true
  try {
    await routesApi.save({
      name: saveRouteName.value.trim(),
      origin: { address: form.originAddress, ...form.originCoord },
      destination: { address: form.destAddress, ...form.destCoord },
    })
    ElMessage.success('路线已保存')
    saveRouteVisible.value = false
    saveRouteName.value = ''
    await loadSavedRoutes()
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    savingRoute.value = false
  }
}

// ── Map computed props ────────────────────────────────────────────────────────

const mapOrigin = computed(() =>
  form.originCoord
    ? { ...form.originCoord, address: form.originAddress }
    : null,
)

const mapDest = computed(() =>
  form.destCoord
    ? { ...form.destCoord, address: form.destAddress }
    : null,
)

// ── Status display helpers ────────────────────────────────────────────────────

const STATE_LABEL: Record<TaskState, string> = {
  idle: '待机',
  running: '运行中',
  paused: '已暂停',
  stopped: '已停止',
  completed: '已完成',
}

const STATE_TAG_TYPE: Record<TaskState, '' | 'success' | 'warning' | 'danger' | 'info'> = {
  idle: 'info',
  running: 'success',
  paused: 'warning',
  stopped: 'danger',
  completed: 'success',
}

const stateLabel = computed(() => STATE_LABEL[taskState.value])
const stateTagType = computed(() => STATE_TAG_TYPE[taskState.value])

function formatDuration(seconds: number): string {
  if (!seconds) return '-'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) return `${h}小时${m}分`
  if (m > 0) return `${m}分${s}秒`
  return `${s}秒`
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

loadSavedRoutes()

// Resume display if task was already running before page reload
;(async () => {
  try {
    const status = await taskApi.getStatus()
    if (status.state !== 'idle' && status.state !== 'stopped') {
      taskStatus.value = status
      taskState.value = status.state
      if (status.state === 'running') startPolling()
      fetchLogs()
    }
  } catch {
    // no active task
  }
})()

onUnmounted(() => stopPolling())

// ── Rescue action nodes ───────────────────────────────────────────────────────

const rescueActioning = ref<string | null>(null)

const canRescueAction = computed(
  () => !!form.caseId.trim() && !!form.reportTaskId.trim() && !!form.handleUserId.trim(),
)

const technicianActionMap = {
  sign: taskApi.technicianSign,
  arrive: taskApi.technicianArrive,
  start: taskApi.technicianStart,
  complete: taskApi.technicianComplete,
} as const

async function technicianAction(action: keyof typeof technicianActionMap) {
  if (!canRescueAction.value) {
    ElMessage.warning('请先填写订单号、外部任务号（taskId）和技师账号（handleUserId）')
    return
  }
  const body: TechnicianActionRequest = {
    taskId: form.reportTaskId,
    handleUserId: form.handleUserId,
    ...(form.handleOrgId && { handleOrgId: form.handleOrgId }),
    ...(form.handleUserName && { handleUserName: form.handleUserName }),
    ...(form.deviceToken && { deviceToken: form.deviceToken }),
    ...(form.caseFromOrgId && { caseFromOrgId: Number(form.caseFromOrgId) }),
    ...(() => {
      const pos = taskStatus.value?.currentPosition ?? form.originCoord
      return pos ? {
        rescueRealAddressLon: Number(pos.lon),
        rescueRealAddressLat: Number(pos.lat),
      } : {}
    })(),
  }
  rescueActioning.value = action
  try {
    await technicianActionMap[action](form.caseId, body, esbToken.value || undefined)
    ElMessage.success('操作成功')
    if (form.caseId.trim()) {
      try {
        const detail = await taskApi.queryOrder(form.caseId.trim(), sosToken.value || undefined)
        orderSummary.value = detail
      } catch { /* 刷新失败不影响主流程 */ }
    }
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    rescueActioning.value = null
  }
}
</script>

<style scoped>
.task-view {
  padding: 4px 0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  background: #f8fafc;
  border-radius: 6px;
  padding: 8px 12px;
}
.stat-label {
  font-size: 12px;
  color: #94a3b8;
}
.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>

<style>
.log-row-error td {
  background-color: #fef2f2 !important;
  color: #dc2626;
}
</style>
