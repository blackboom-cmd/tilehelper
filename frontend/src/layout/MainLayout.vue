<script setup>
import Sidebar from './Sidebar.vue'
import RightPanel from './RightPanel.vue'
import { useTheme } from '../composables/useTheme'

import { computed } from 'vue'
import { useRoute } from 'vue-router'

import { ref, provide } from 'vue'

const { contentBgColor } = useTheme()

const route = useRoute()

const showRightPanel = computed(() => {
  return route.path === '/tile'
})

const panelWidth = ref(0)   // ⭐默认收起，展开时仅留 toggle 按钮宽度
provide('panelWidth', panelWidth)

const selectedOrderTile = ref(null)
provide('selectedOrderTile', selectedOrderTile)

</script>

<template>
  <div class="layout">
    <Sidebar />

    <div
        class="content"
        :style="{
    backgroundColor: contentBgColor
  }"
    >
      <router-view />
    </div>

    <RightPanel v-if="showRightPanel" />
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  width: 100vw;
  height: 100vh;

  overflow-x: hidden;
}
.content {
  flex: 1;
  padding: 20px;
  padding-right: v-bind(panelWidth);

  overflow-y: auto;

  display: flex;
  flex-direction: column;

  min-width: 0;

  transition: all 0.25s ease;
}

.content > * {
  width: 100%;
}

</style>