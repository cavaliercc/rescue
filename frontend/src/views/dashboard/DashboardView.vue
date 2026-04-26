<template>
  <div>
    <h2 class="text-2xl font-bold text-gray-700 mb-6">仪表盘</h2>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <el-card v-for="stat in stats" :key="stat.label" shadow="hover">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-full flex items-center justify-center" :class="stat.bgColor">
            <el-icon :size="24" :class="stat.textColor"><component :is="stat.icon" /></el-icon>
          </div>
          <div>
            <p class="text-gray-500 text-sm">{{ stat.label }}</p>
            <p class="text-2xl font-bold text-gray-800">{{ stat.value }}</p>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
const stats = [
  { label: '用户总数', value: '0', icon: 'User', bgColor: 'bg-blue-100', textColor: 'text-blue-600' },
  { label: '今日活跃', value: '0', icon: 'TrendCharts', bgColor: 'bg-green-100', textColor: 'text-green-600' },
  { label: '系统状态', value: '正常', icon: 'Monitor', bgColor: 'bg-purple-100', textColor: 'text-purple-600' },
]
</script>
