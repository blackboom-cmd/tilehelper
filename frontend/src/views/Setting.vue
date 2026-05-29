<template>
  <div class="setting-page">
    <h1>主题设置</h1>
    <p>选择您喜欢的主题颜色</p>

    <div class="color-cards-container">
      <div
        v-for="(card, index) in colorCards"
        :key="card.color"
        class="color-card"
        :class="{ 'selected': selectedIndex === index }"
        :style="{ backgroundColor: card.color }"
        @click="selectCard(index)"
      >
        <div class="card-label">{{ card.label }}</div>
        <div v-if="selectedIndex === index" class="checkmark">✓</div>
      </div>
    </div>

    <div class="current-theme-info">
      <p>当前主题颜色: <span class="theme-color-display" :style="{ color: themeColor }">{{ themeColor }}</span></p>
      <p>导航栏颜色: <span class="color-preview" :style="{ backgroundColor: themeColor }"></span></p>
      <p>内容区背景颜色: <span class="color-preview" :style="{ backgroundColor: contentBgColor }"></span></p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useTheme } from '../composables/useTheme'

const { themeColor, contentBgColor, setThemeColor } = useTheme()

// 颜色卡片数据
const colorCards = ref([
  { color: '#cccccc', label: '灰色' },
  { color: '#ffffff', label: '白色' },
  { color: '#4CAF50', label: '绿色' },
  { color: '#2196F3', label: '蓝色' },
  { color: '#FF9800', label: '橙色' },
  { color: '#9C27B0', label: '紫色' },
  { color: '#F44336', label: '红色' },
  { color: '#00BCD4', label: '青色' }
])

// 默认选中灰色卡片（对应默认主题颜色 #cccccc）
const selectedIndex = ref(
    colorCards.value.findIndex(c => c.color === themeColor.value)
)

// 选择卡片
function selectCard(index) {
  selectedIndex.value = index
  const selectedColor = colorCards.value[index].color
  setThemeColor(selectedColor)
}
</script>

<style scoped>
.setting-page {
  width: 100%;
  padding: 20px;
  box-sizing: border-box;
}

h1 {
  color: #333;
  margin-bottom: 10px;
}

p {
  color: #666;
  margin-bottom: 30px;
}

.color-cards-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 40px;
}

.color-card {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border: 2px solid transparent;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.color-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.color-card.selected {
  border-color: #4CAF50;
  transform: scale(1.05);
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.3);
  filter: brightness(1.1);
}

.card-label {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 4px 8px;
  border-radius: 4px;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: bold;
}

.checkmark {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background-color: #4CAF50;
  color: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
}

.current-theme-info {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #eee;
}

.theme-color-display {
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 4px;
  background-color: #f5f5f5;
}

.color-preview {
  display: inline-block;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  vertical-align: middle;
  margin-left: 8px;
  border: 1px solid #ddd;
}
</style>