declare global {
  interface Window {
    AMap: any
    _amapLoadPromise?: Promise<void>
  }
}

export function loadAmap(): Promise<void> {
  if (window.AMap) return Promise.resolve()
  if (window._amapLoadPromise) return window._amapLoadPromise

  const key = import.meta.env.VITE_AMAP_KEY
  const securityCode = import.meta.env.VITE_AMAP_SECURITY_CODE
  if (!key) return Promise.reject(new Error('请在 .env 中配置 VITE_AMAP_KEY'))

  if (securityCode) {
    (window as any)._AMapSecurityConfig = { securityJsCode: securityCode }
  }

  window._amapLoadPromise = new Promise<void>((resolve, reject) => {
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${key}`
    script.onload = () => resolve()
    script.onerror = () => {
      window._amapLoadPromise = undefined
      reject(new Error('高德地图 JS API 加载失败'))
    }
    document.head.appendChild(script)
  })

  return window._amapLoadPromise
}

export async function geocodeAddress(address: string): Promise<{ lon: string; lat: string } | null> {
  const key = import.meta.env.VITE_AMAP_SERVICE_KEY || import.meta.env.VITE_AMAP_KEY
  if (!key) throw new Error('请在 .env 中配置 VITE_AMAP_SERVICE_KEY')

  const url = `https://restapi.amap.com/v3/geocode/geo?key=${key}&address=${encodeURIComponent(address)}&output=JSON`
  const res = await fetch(url)
  if (!res.ok) throw new Error('地理编码请求失败')

  const data = await res.json()
  if (data.status !== '1' || parseInt(data.count) === 0) return null

  const [lon, lat] = data.geocodes[0].location.split(',')
  return { lon, lat }
}

export interface AmapRouteResult {
  points: Array<{ lon: string; lat: string }>
  distance: number
  duration: number
}

export async function planDrivingRoute(
  origin: { lon: string; lat: string },
  destination: { lon: string; lat: string },
): Promise<AmapRouteResult> {
  const key = import.meta.env.VITE_AMAP_SERVICE_KEY || import.meta.env.VITE_AMAP_KEY
  if (!key) throw new Error('请在 .env 中配置 VITE_AMAP_SERVICE_KEY')

  const url =
    `https://restapi.amap.com/v3/direction/driving` +
    `?key=${key}` +
    `&origin=${origin.lon},${origin.lat}` +
    `&destination=${destination.lon},${destination.lat}` +
    `&policy=0&output=JSON`

  const res = await fetch(url)
  if (!res.ok) throw new Error('路径规划请求失败')

  const data = await res.json()
  if (data.status !== '1') throw new Error(data.info || '路径规划失败')

  const path = data.route?.paths?.[0]
  if (!path) throw new Error('无法获取路线数据')

  const points: Array<{ lon: string; lat: string }> = []
  for (const step of path.steps as any[]) {
    for (const pair of (step.polyline as string).split(';')) {
      const [lon, lat] = pair.split(',')
      if (lon && lat) points.push({ lon, lat })
    }
  }

  return {
    points,
    distance: parseInt(path.distance),
    duration: parseInt(path.duration),
  }
}
