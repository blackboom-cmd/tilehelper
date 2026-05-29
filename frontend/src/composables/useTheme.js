import { ref, computed } from 'vue'

// 默认主题颜色
const DEFAULT_THEME_COLOR = '#cccccc'

// 创建响应式主题状态
const themeColor = ref(
    localStorage.getItem('themeColor') || DEFAULT_THEME_COLOR
)

// 计算内容区背景色（themeColor的浅色版本）
const contentBgColor = computed(() => {
  // 简单实现：将颜色调亮
  // 将hex颜色转换为rgb，增加亮度，再转回hex
  const hex = themeColor.value.replace('#', '')
  const r = parseInt(hex.substr(0, 2), 16)
  const g = parseInt(hex.substr(2, 2), 16)
  const b = parseInt(hex.substr(4, 2), 16)

  // 增加亮度（简单线性增加）
  const lightenFactor = 0.85 // 亮度增加因子，值越大背景越亮
  const lightR = Math.min(255, Math.floor(r + (255 - r) * lightenFactor))
  const lightG = Math.min(255, Math.floor(g + (255 - g) * lightenFactor))
  const lightB = Math.min(255, Math.floor(b + (255 - b) * lightenFactor))

  // 转回hex
  return `#${lightR.toString(16).padStart(2, '0')}${lightG.toString(16).padStart(2, '0')}${lightB.toString(16).padStart(2, '0')}`
})

// 更新主题颜色
function setThemeColor(color) {
  themeColor.value = color
  localStorage.setItem('themeColor', color)
}

// 重置为默认主题
function resetTheme() {
  themeColor.value = DEFAULT_THEME_COLOR
  localStorage.setItem('themeColor', DEFAULT_THEME_COLOR)
}

// 导出可用的函数和状态
export function useTheme() {
  return {
    themeColor,
    contentBgColor,
    setThemeColor,
    resetTheme,
    DEFAULT_THEME_COLOR
  }
}