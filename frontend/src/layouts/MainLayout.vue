<template>
  <el-container class="h-full">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar transition-all duration-300">
      <div class="flex items-center justify-center h-16 bg-blue-600 text-white font-bold text-lg overflow-hidden">
        <span v-if="!isCollapsed">Rescue</span>
        <span v-else>R</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#1e293b"
        text-color="#94a3b8"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/task">
          <el-icon><Location /></el-icon>
          <template #title>任务管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="flex items-center justify-between px-4 bg-white border-b border-gray-200">
        <el-icon class="text-xl cursor-pointer" @click="isCollapsed = !isCollapsed">
          <Fold v-if="!isCollapsed" />
          <Expand v-else />
        </el-icon>
        <div class="flex items-center gap-4">
          <span class="text-gray-600 text-sm">救援管理系统</span>
        </div>
      </el-header>
      <el-main class="bg-gray-50">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  background-color: #1e293b;
  height: 100%;
  overflow: hidden;
}
</style>
