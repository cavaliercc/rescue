<template>
  <div ref="mapEl" class="w-full h-full min-h-[400px] rounded-md overflow-hidden">
    <div v-if="loadError" class="flex items-center justify-center h-full bg-gray-100 text-gray-500 text-sm">
      {{ loadError }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { loadAmap } from '@/utils/amap'

interface LatLon {
  lon: string
  lat: string
  address?: string
}

const props = withDefaults(
  defineProps<{
    origin?: LatLon | null
    destination?: LatLon | null
    routePoints?: Array<{ lon: string; lat: string }>
    currentPosition?: { lon: string; lat: string } | null
  }>(),
  {
    origin: null,
    destination: null,
    routePoints: () => [],
    currentPosition: null,
  },
)

const mapEl = ref<HTMLDivElement | null>(null)
const loadError = ref('')

let map: any = null
let originMarker: any = null
let destMarker: any = null
let currentMarker: any = null
let routePolyline: any = null

onMounted(async () => {
  try {
    await loadAmap()
    initMap()
  } catch (e) {
    loadError.value = (e as Error).message
  }
})

onUnmounted(() => {
  map?.destroy()
  map = null
})

function initMap() {
  const AMap = window.AMap
  map = new AMap.Map(mapEl.value, {
    zoom: 11,
    center: [116.397428, 39.90923],
    mapStyle: 'amap://styles/normal',
  })
  refreshAll()
}

function refreshAll() {
  updateMarkers()
  updateRoute()
  updateCurrentPosition()
}

function updateMarkers() {
  if (!map || !window.AMap) return
  const AMap = window.AMap

  originMarker?.setMap(null)
  destMarker?.setMap(null)
  originMarker = null
  destMarker = null

  if (props.origin?.lon && props.origin?.lat) {
    originMarker = new AMap.Marker({
      position: [parseFloat(props.origin.lon), parseFloat(props.origin.lat)],
      title: props.origin.address || '起点',
      label: {
        content: `<div class="map-label origin-label">起</div>`,
        offset: new AMap.Pixel(0, -36),
      },
    })
    originMarker.setMap(map)
  }

  if (props.destination?.lon && props.destination?.lat) {
    destMarker = new AMap.Marker({
      position: [parseFloat(props.destination.lon), parseFloat(props.destination.lat)],
      title: props.destination.address || '终点',
      label: {
        content: `<div class="map-label dest-label">终</div>`,
        offset: new AMap.Pixel(0, -36),
      },
    })
    destMarker.setMap(map)
  }

  fitView()
}

function updateRoute() {
  if (!map || !window.AMap) return
  const AMap = window.AMap

  routePolyline?.setMap(null)
  routePolyline = null

  if (props.routePoints.length > 1) {
    const path = props.routePoints.map((p) => [parseFloat(p.lon), parseFloat(p.lat)])
    routePolyline = new AMap.Polyline({
      path,
      strokeColor: '#3B82F6',
      strokeWeight: 5,
      strokeOpacity: 0.85,
      lineJoin: 'round',
      lineCap: 'round',
    })
    routePolyline.setMap(map)
    map.setFitView([routePolyline], false, [60, 60, 60, 60])
  }
}

function updateCurrentPosition() {
  if (!map || !window.AMap) return
  const AMap = window.AMap

  if (!props.currentPosition?.lon) {
    currentMarker?.setMap(null)
    currentMarker = null
    return
  }

  const pos = [parseFloat(props.currentPosition.lon), parseFloat(props.currentPosition.lat)]

  if (!currentMarker) {
    currentMarker = new AMap.Marker({
      position: pos,
      offset: new AMap.Pixel(-12, -12),
      content: '<div class="current-marker">🚗</div>',
      zIndex: 200,
    })
    currentMarker.setMap(map)
  } else {
    currentMarker.setPosition(pos)
  }
}

function fitView() {
  if (!map) return
  const markers = [originMarker, destMarker].filter(Boolean)
  if (markers.length === 2) {
    map.setFitView(markers, false, [80, 80, 80, 80])
  } else if (markers.length === 1) {
    const m = markers[0]
    map.setCenter(m.getPosition())
    map.setZoom(14)
  }
}

watch(() => [props.origin, props.destination], updateMarkers, { deep: true })
watch(() => props.routePoints, updateRoute, { deep: true })
watch(() => props.currentPosition, updateCurrentPosition, { deep: true })
</script>

<style>
.map-label {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  white-space: nowrap;
}
.origin-label { background: #22c55e; }
.dest-label { background: #ef4444; }
.current-marker { font-size: 24px; line-height: 1; }
</style>
