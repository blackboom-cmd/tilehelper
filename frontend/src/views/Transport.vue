<template>
  <div class="transport-page">
    <h1>运输表</h1>
    <div class="sub-row">
      <span class="sub-title">展示运输信息</span>
      <div class="toolbar">
        <button v-if="!isDeleteMode" class="btn-delete-mode" @click="isDeleteMode = true">
          批量删除
        </button>
        <div v-else class="delete-group">
          <button class="btn-select-all" @click="selectAll">全选</button>
          <button class="btn-delete" @click="handleBatchDelete">删除选中</button>
          <button class="btn-cancel" @click="cancelDeleteMode">取消</button>
        </div>
        <button class="add-btn" @click="openAdd">+ 新增运输</button>
      </div>
    </div>

    <!-- 选中运输卡片的概要信息 -->
    <div v-if="selectedTransport" class="selected-summary">
      <div class="summary-row">
        <span class="summary-label">送货时间</span>
        <span class="summary-value">{{ formatDateTime(selectedTransport.deliveryTime) }}</span>
      </div>
      <div class="summary-row">
        <span class="summary-label">送货地址</span>
        <span class="summary-value">{{ selectedTransport.deliveryAddress || '—' }}</span>
      </div>
    </div>

    <div class="split-container">
      <!-- 左侧：货车信息 + 选中货物详情 -->
      <div class="left-panel">
        <div class="truck-display">
          <!-- 选中运输卡片的货物数据 -->
          <div v-if="selectedTransport" class="cargo-detail">
            <div class="cargo-title">📦 选中货物</div>
            <div class="cargo-stats">
              <div class="cargo-stat">
                <span class="cargo-num">{{ selectedTransport.totalPieces ?? 0 }}</span>
                <span class="cargo-unit">总张数</span>
              </div>
              <div class="cargo-stat">
                <span class="cargo-num">{{ selectedTransport.packageCount ?? 0 }}</span>
                <span class="cargo-unit">整包</span>
              </div>
              <div class="cargo-stat">
                <span class="cargo-num">{{ selectedTransport.extraPieces ?? 0 }}</span>
                <span class="cargo-unit">零散张</span>
              </div>
            </div>
            <div class="cargo-divider"></div>
          </div>

          <svg class="truck-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="22" width="40" height="28" rx="4" fill="#e5e7eb" stroke="#9ca3af" stroke-width="2"/>
            <rect x="44" y="14" width="16" height="36" rx="4" fill="#d1d5db" stroke="#9ca3af" stroke-width="2"/>
            <rect x="8" y="26" width="16" height="16" rx="2" fill="#f9fafb" stroke="#d1d5db" stroke-width="1"/>
            <circle cx="16" cy="54" r="7" fill="#fff" stroke="#6b7280" stroke-width="2.5"/>
            <circle cx="44" cy="54" r="7" fill="#fff" stroke="#6b7280" stroke-width="2.5"/>
            <line x1="20" y1="40" x2="56" y2="40" stroke="#9ca3af" stroke-width="2"/>
          </svg>

          <div v-if="selectedTruck.id" class="truck-info">
            <div class="truck-name">{{ selectedTruck.name }}</div>
            <div class="truck-capacity">载重：{{ selectedTruck.capacity ?? '—' }} 吨</div>
          </div>
          <div v-else class="truck-info">
            <div class="truck-name placeholder">未选择货车</div>
            <div class="truck-capacity">请点击下方按钮选择</div>
          </div>
        </div>

        <button class="switch-btn" @click="goToTruckSelect">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="1 4 1 10 7 10"/>
            <polyline points="23 20 23 14 17 14"/>
            <path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15"/>
          </svg>
          更换货车
        </button>
      </div>

      <!-- 分割线 -->
      <div class="divider"></div>

      <!-- 右侧：运输记录 -->
      <div class="right-panel">
        <!-- 进行中的运输 -->
        <div class="section">
          <div class="section-header" @click="showActive = !showActive">
            <div class="section-header-left">
              <h3>进行中</h3>
              <span class="count">{{ activeTransports.length }} 单</span>
            </div>
            <span class="arrow">{{ showActive ? '▲' : '▼' }}</span>
          </div>

          <div v-if="showActive" class="card-list">
            <div class="transport-card" v-for="t in activeTransports" :key="t.id"
                 :class="{ selected: selectedTransportId === t.id }"
                 @click="selectTransport(t)"
                 :style="{
                   borderLeft: `6px solid ${getDeliveryColor(t.deliveryAddress, t.deliveryTime)}`,
                   background: `linear-gradient(to right, ${getDeliveryColor(t.deliveryAddress, t.deliveryTime)}08, transparent 60px)`
                 }">
              <div class="card-top">
                <div class="card-top-left">
                  <input
                    v-if="isDeleteMode"
                    type="checkbox"
                    :value="t.id"
                    v-model="selectedIds"
                    @click.stop
                  />
                </div>
                <span class="status-badge" :style="{ background: statusColor(t.status) }">
                  {{ t.status }}
                </span>
              </div>

              <div class="card-info-grid">
                <div class="info-item"><span class="lbl">客户</span><span class="val">{{ t.customerName }}</span></div>
                <div class="info-item"><span class="lbl">货车</span><span class="val">{{ t.truckName || '—' }}</span></div>
                <div class="info-item"><span class="lbl">瓦片</span><span class="val">{{ t.tileName || '—' }}</span></div>
                <div class="info-item"><span class="lbl">总张数</span><span class="val">{{ t.totalPieces ?? '—' }} 张</span></div>
                <div class="info-item"><span class="lbl">整包/零散</span><span class="val">{{ t.packageCount ?? 0 }}包 / {{ t.extraPieces ?? 0 }}张</span></div>
                <div class="info-item"><span class="lbl">地址</span><span class="val">{{ t.deliveryAddress || '—' }}</span></div>
                <div class="info-item" v-if="t.deliveryTime"><span class="lbl">配送</span><span class="val">{{ formatDate(t.deliveryTime) }}</span></div>
                <div class="info-item" v-if="t.note"><span class="lbl">备注</span><span class="val">{{ t.note }}</span></div>
              </div>

              <div v-if="!isDeleteMode" class="card-actions" @click.stop>
                <button class="action-btn edit-action" @click="openEdit(t)">编辑</button>
                <button
                  v-if="t.status === '待发货'"
                  class="action-btn ship-action"
                  @click="quickShip(t)"
                >发货</button>
                <button
                  v-if="t.status === '运输中'"
                  class="action-btn complete-action"
                  @click="quickComplete(t)"
                >完成</button>
                <button
                  v-if="t.status !== '已完成' && t.status !== '已取消'"
                  class="action-btn cancel-action"
                  @click="quickCancel(t)"
                >取消</button>
              </div>
            </div>

            <div v-if="activeTransports.length === 0" class="empty">暂无进行中的运输记录</div>
          </div>
        </div>

        <!-- 已完成 / 已取消的运输 -->
        <div class="section">
          <div class="section-header finished-header" @click="showCompleted = !showCompleted">
            <div class="section-header-left">
              <h3>已完成 / 已取消</h3>
              <span class="count">{{ completedTransports.length }} 单</span>
            </div>
            <span class="arrow">{{ showCompleted ? '▲' : '▼' }}</span>
          </div>

          <div v-if="showCompleted" class="card-list">
            <div class="transport-card" v-for="t in completedTransports" :key="t.id"
                 :class="{ selected: selectedTransportId === t.id }"
                 @click="selectTransport(t)"
                 :style="{
                   borderLeft: `6px solid ${getDeliveryColor(t.deliveryAddress, t.deliveryTime)}`,
                   background: `linear-gradient(to right, ${getDeliveryColor(t.deliveryAddress, t.deliveryTime)}08, transparent 60px)`
                 }">
              <div class="card-top">
                <div class="card-top-left">
                  <input
                    v-if="isDeleteMode"
                    type="checkbox"
                    :value="t.id"
                    v-model="selectedIds"
                    @click.stop
                  />
                </div>
                <span class="status-badge" :style="{ background: statusColor(t.status) }">
                  {{ t.status }}
                </span>
              </div>

              <div class="card-info-grid">
                <div class="info-item"><span class="lbl">客户</span><span class="val">{{ t.customerName }}</span></div>
                <div class="info-item"><span class="lbl">货车</span><span class="val">{{ t.truckName || '—' }}</span></div>
                <div class="info-item"><span class="lbl">瓦片</span><span class="val">{{ t.tileName || '—' }}</span></div>
                <div class="info-item"><span class="lbl">总张数</span><span class="val">{{ t.totalPieces ?? '—' }} 张</span></div>
                <div class="info-item"><span class="lbl">整包/零散</span><span class="val">{{ t.packageCount ?? 0 }}包 / {{ t.extraPieces ?? 0 }}张</span></div>
                <div class="info-item"><span class="lbl">地址</span><span class="val">{{ t.deliveryAddress || '—' }}</span></div>
                <div class="info-item" v-if="t.deliveryTime"><span class="lbl">配送</span><span class="val">{{ formatDate(t.deliveryTime) }}</span></div>
                <div class="info-item" v-if="t.note"><span class="lbl">备注</span><span class="val">{{ t.note }}</span></div>
              </div>

              <div v-if="!isDeleteMode" class="card-actions" @click.stop>
                <button class="action-btn edit-action" @click="openEdit(t)">编辑</button>
              </div>
            </div>

            <div v-if="completedTransports.length === 0" class="empty">暂无已完成或已取消的运输记录</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增弹窗 -->
    <div v-if="showAdd" class="mask" @click="showAdd = false">
      <div class="modal" @click.stop>
        <h2 class="modal-title">新增运输</h2>
        <div class="form-grid">
          <div class="form-field">
            <label>客户姓名</label>
            <input v-model="addForm.customerName" placeholder="客户姓名" />
          </div>
          <div class="form-field">
            <label>货车名称</label>
            <input v-model="addForm.truckName" placeholder="货车名称" />
          </div>
          <div class="form-field">
            <label>瓦片名称</label>
            <select v-model="addForm.tileName" @change="onAddTileChange">
              <option value="">请选择瓦片</option>
              <option v-for="tile in tiles" :key="tile.id" :value="tile.tileName">
                {{ tile.tileName }}（{{ tile.unitQuantity }}张/包）
              </option>
            </select>
          </div>
          <div class="form-field">
            <label>总张数</label>
            <input v-model.number="addForm.totalPieces" type="number" placeholder="0" @input="calcAddPackage" />
          </div>
          <div class="form-field">
            <label>整包数量</label>
            <input v-model.number="addForm.packageCount" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field">
            <label>零散张数</label>
            <input v-model.number="addForm.extraPieces" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field">
            <label>状态</label>
            <select v-model="addForm.status">
              <option value="待发货">待发货</option>
              <option value="运输中">运输中</option>
              <option value="已发货">已发货</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
            </select>
          </div>
          <div class="form-field">
            <label>配送地址</label>
            <input v-model="addForm.deliveryAddress" placeholder="详细地址" />
          </div>
          <div class="form-field">
            <label>配送时间</label>
            <input v-model="addForm.deliveryTime" type="datetime-local" />
          </div>
          <div class="form-field full-width">
            <label>备注</label>
            <input v-model="addForm.note" placeholder="备注信息（可选）" />
          </div>
        </div>
        <div class="modal-btns">
          <button class="btn-cancel-modal" @click="showAdd = false">取消</button>
          <button class="btn-submit" @click="submitAdd">确认新增</button>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEdit" class="mask" @click="showEdit = false">
      <div class="modal" @click.stop>
        <h2 class="modal-title">编辑运输</h2>
        <div class="form-grid">
          <div class="form-field">
            <label>客户姓名</label>
            <input v-model="editForm.customerName" placeholder="客户姓名" />
          </div>
          <div class="form-field">
            <label>货车名称</label>
            <input v-model="editForm.truckName" placeholder="货车名称" />
          </div>
          <div class="form-field">
            <label>瓦片名称</label>
            <select v-model="editForm.tileName" @change="onEditTileChange">
              <option value="">请选择瓦片</option>
              <option v-for="tile in tiles" :key="tile.id" :value="tile.tileName">
                {{ tile.tileName }}（{{ tile.unitQuantity }}张/包）
              </option>
            </select>
          </div>
          <div class="form-field">
            <label>总张数</label>
            <input v-model.number="editForm.totalPieces" type="number" placeholder="0" @input="calcEditPackage" />
          </div>
          <div class="form-field">
            <label>整包数量</label>
            <input v-model.number="editForm.packageCount" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field">
            <label>零散张数</label>
            <input v-model.number="editForm.extraPieces" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field">
            <label>状态</label>
            <select v-model="editForm.status">
              <option value="待发货">待发货</option>
              <option value="运输中">运输中</option>
              <option value="已发货">已发货</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
            </select>
          </div>
          <div class="form-field">
            <label>配送地址</label>
            <input v-model="editForm.deliveryAddress" placeholder="详细地址" />
          </div>
          <div class="form-field">
            <label>配送时间</label>
            <input v-model="editForm.deliveryTime" type="datetime-local" />
          </div>
          <div class="form-field full-width">
            <label>备注</label>
            <input v-model="editForm.note" placeholder="备注信息（可选）" />
          </div>
        </div>
        <div class="modal-btns">
          <button class="btn-cancel-modal" @click="showEdit = false">取消</button>
          <button class="btn-submit" @click="submitEdit">保存修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import {
  getTransportList,
  addTransport,
  updateTransport,
  updateTransportStatus,
  deleteTransports
} from "@/api/transport"
import { getTileList } from "@/api/tile"

const route = useRoute()
const router = useRouter()

const transports = ref([])
const tiles = ref([])
const showActive = ref(true)
const showCompleted = ref(true)

const selectedTruck = ref({
  id: null,
  name: '',
  capacity: null
})

const selectedTransportId = ref(null)

const selectedTransport = computed(() => {
  if (selectedTransportId.value == null) return null
  return transports.value.find(t => t.id === selectedTransportId.value) || null
})

function selectTransport(t) {
  if (isDeleteMode.value) return
  selectedTransportId.value = selectedTransportId.value === t.id ? null : t.id
}

const fetchTransports = async () => {
  const res = await getTransportList()
  transports.value = res.data
}

const fetchTiles = async () => {
  const tileRes = await getTileList()
  tiles.value = tileRes.data
}

function loadSelectedTruck() {
  // 优先从 URL 参数恢复
  if (route.query.truckId) {
    const truck = {
      id: Number(route.query.truckId),
      name: route.query.truckName || '',
      capacity: route.query.truckCapacity ? Number(route.query.truckCapacity) : null
    }
    selectedTruck.value = truck
    sessionStorage.setItem('selectedTruck', JSON.stringify(truck))
    router.replace({ path: '/transport' })
    return
  }
  // 其次从 sessionStorage 恢复（切换界面或刷新后）
  const saved = sessionStorage.getItem('selectedTruck')
  if (saved) {
    try {
      selectedTruck.value = JSON.parse(saved)
    } catch {
      sessionStorage.removeItem('selectedTruck')
    }
  }
}

onMounted(async () => {
  await Promise.all([fetchTransports(), fetchTiles()])
  loadSelectedTruck()
})

watch(() => route.query.truckId, (newId) => {
  if (newId) {
    const truck = {
      id: Number(newId),
      name: route.query.truckName || '',
      capacity: route.query.truckCapacity ? Number(route.query.truckCapacity) : null
    }
    selectedTruck.value = truck
    sessionStorage.setItem('selectedTruck', JSON.stringify(truck))
    router.replace({ path: '/transport' })
  }
})

function getTileUnitQuantity(tileName) {
  if (!tileName) return null
  const tile = tiles.value.find(t => t.tileName === tileName)
  return tile ? tile.unitQuantity : null
}

function calcPackageFromForm(form) {
  const tileName = form.tileName
  const totalPieces = form.totalPieces
  if (!tileName || !totalPieces || totalPieces <= 0) {
    form.packageCount = null
    form.extraPieces = null
    return
  }
  const unitQty = getTileUnitQuantity(tileName)
  if (unitQty && unitQty > 0) {
    form.packageCount = Math.floor(totalPieces / unitQty)
    form.extraPieces = totalPieces % unitQty
  }
}

function onAddTileChange() { calcPackageFromForm(addForm.value) }
function calcAddPackage() { calcPackageFromForm(addForm.value) }
function onEditTileChange() { calcPackageFromForm(editForm.value) }
function calcEditPackage() { calcPackageFromForm(editForm.value) }

const activeTransports = computed(() =>
  transports.value.filter(t => t.status !== '已完成' && t.status !== '已取消')
)

const completedTransports = computed(() =>
  transports.value.filter(t => t.status === '已完成' || t.status === '已取消')
)

function statusColor(status) {
  const map = {
    '待发货': '#f59e0b',
    '运输中': '#3b82f6',
    '已发货': '#8b5cf6',
    '已完成': '#22c55e',
    '已取消': '#9ca3af'
  }
  return map[status] || '#6b7280'
}

const DELIVERY_COLORS = [
  '#6366f1', '#8b5cf6', '#ec4899', '#f97316', '#eab308',
  '#22c55e', '#14b8a6', '#0ea5e9', '#3b82f6', '#ef4444',
  '#a855f7', '#06b6d4', '#84cc16', '#f59e0b', '#64748b',
]

function getDeliveryColor(address, time) {
  const key = `${address || ''}|${time || ''}`
  let hash = 0
  for (let i = 0; i < key.length; i++) {
    hash = ((hash << 5) - hash) + key.charCodeAt(i)
    hash |= 0
  }
  return DELIVERY_COLORS[Math.abs(hash) % DELIVERY_COLORS.length]
}

function formatDate(timestamp) {
  if (!timestamp) return '—'
  const d = new Date(timestamp)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function formatDateTime(timestamp) {
  if (!timestamp) return '—'
  const d = new Date(timestamp)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day}  ${h}:${min}`
}

function toDatetimeLocal(timestamp) {
  if (!timestamp) return ''
  const d = new Date(timestamp)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day}T${h}:${min}`
}

// ========== 跳转货车选择 ==========
function goToTruckSelect() {
  router.push({ path: '/truck', query: { selectMode: 'true', from: '/transport' } })
}

// ========== 新增 ==========
const showAdd = ref(false)
const addForm = ref({
  customerName: '',
  truckName: '',
  tileName: '',
  totalPieces: null,
  packageCount: null,
  extraPieces: null,
  status: '待发货',
  deliveryAddress: '',
  deliveryTime: '',
  note: ''
})

const openAdd = () => {
  addForm.value = {
    customerName: '',
    truckName: selectedTruck.value.name || '',
    tileName: '',
    totalPieces: null,
    packageCount: null,
    extraPieces: null,
    status: '待发货',
    deliveryAddress: '',
    deliveryTime: '',
    note: ''
  }
  showAdd.value = true
}

const submitAdd = async () => {
  const data = {
    customerName: addForm.value.customerName || '未命名客户',
    truckName: addForm.value.truckName || '',
    tileName: addForm.value.tileName || '',
    totalPieces: addForm.value.totalPieces || 0,
    packageCount: addForm.value.packageCount || 0,
    extraPieces: addForm.value.extraPieces || 0,
    status: addForm.value.status || '待发货',
    deliveryAddress: addForm.value.deliveryAddress || '',
    deliveryTime: addForm.value.deliveryTime
      ? new Date(addForm.value.deliveryTime).toISOString()
      : null,
    note: addForm.value.note || '',
    createdAt: new Date().toISOString()
  }
  await addTransport(data)
  showAdd.value = false
  await fetchTransports()
}

// ========== 编辑 ==========
const showEdit = ref(false)
const editForm = ref({})

const openEdit = (t) => {
  editForm.value = {
    id: t.id,
    orderId: t.orderId,
    customerName: t.customerName,
    truckName: t.truckName || '',
    tileName: t.tileName || '',
    totalPieces: t.totalPieces ?? null,
    packageCount: t.packageCount ?? null,
    extraPieces: t.extraPieces ?? null,
    status: t.status,
    deliveryAddress: t.deliveryAddress || '',
    deliveryTime: toDatetimeLocal(t.deliveryTime),
    note: t.note || '',
    tileId: t.tileId,
    createdAt: t.createdAt
  }
  calcPackageFromForm(editForm.value)
  showEdit.value = true
}

const submitEdit = async () => {
  const data = {
    ...editForm.value,
    customerName: editForm.value.customerName || '未命名客户',
    truckName: editForm.value.truckName || '',
    tileName: editForm.value.tileName || '',
    totalPieces: editForm.value.totalPieces || 0,
    packageCount: editForm.value.packageCount || 0,
    extraPieces: editForm.value.extraPieces || 0,
    deliveryTime: editForm.value.deliveryTime
      ? new Date(editForm.value.deliveryTime).toISOString()
      : null,
    note: editForm.value.note || ''
  }
  await updateTransport(data)
  showEdit.value = false
  await fetchTransports()
}

// ========== 快捷状态变更 ==========
const quickShip = async (t) => {
  await updateTransportStatus({ id: t.id, status: '已发货' })
  await fetchTransports()
}

const quickComplete = async (t) => {
  await updateTransportStatus({ id: t.id, status: '已完成' })
  await fetchTransports()
}

const quickCancel = async (t) => {
  await updateTransportStatus({ id: t.id, status: '已取消' })
  await fetchTransports()
}

// ========== 批量删除 ==========
const isDeleteMode = ref(false)
const selectedIds = ref([])

const selectAll = () => {
  if (selectedIds.value.length === transports.value.length) {
    selectedIds.value = []
  } else {
    selectedIds.value = transports.value.map(t => t.id)
  }
}

const cancelDeleteMode = () => {
  isDeleteMode.value = false
  selectedIds.value = []
}

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    alert('请选择要删除的运输记录')
    return
  }
  await deleteTransports(selectedIds.value)
  await fetchTransports()
  cancelDeleteMode()
}
</script>

<style scoped>
.transport-page {
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 20px 40px;
}

.sub-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

h1 {
  color: #333;
  margin-bottom: 4px;
}

.sub-title {
  font-size: 14px;
  color: #888;
}

/* ===== 选中卡片概要信息 ===== */
.selected-summary {
  margin: 12px 0 8px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #eff6ff 0%, #f0f7ff 100%);
  border: 1px solid #bfdbfe;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.summary-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.summary-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
  white-space: nowrap;
}

.summary-value {
  font-size: 20px;
  font-weight: 700;
  color: #1e40af;
  letter-spacing: 0.5px;
}

/* ===== 左右分割布局 ===== */
.split-container {
  display: flex;
  gap: 0;
  min-height: 500px;
}

.left-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.truck-display {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.truck-icon {
  width: 80px;
  height: 80px;
}

.truck-info {
  text-align: center;
}

.truck-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.truck-name.placeholder {
  color: #bbb;
  font-weight: 400;
}

.truck-capacity {
  font-size: 13px;
  color: #888;
}

/* ===== 选中货物详情 ===== */
.cargo-detail {
  width: 100%;
  margin-bottom: 8px;
}

.cargo-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 14px 0 0;
}

.cargo-title {
  font-size: 13px;
  font-weight: 600;
  color: #555;
  text-align: center;
  margin-bottom: 12px;
}

.cargo-stats {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.cargo-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px 14px;
  min-width: 70px;
}

.cargo-num {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.cargo-unit {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.switch-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #555;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 16px;
}

.switch-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #f0f7ff;
}

.divider {
  width: 1px;
  background: #e5e7eb;
  margin: 0 20px;
  align-self: stretch;
}

.right-panel {
  flex: 1;
  min-width: 0;
}

/* ===== 分区 ===== */
.section {
  margin-bottom: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #f9fafb;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.section-header:hover {
  background: #f3f4f6;
}

.section-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.count {
  font-size: 13px;
  color: #888;
  background: #e5e7eb;
  padding: 2px 10px;
  border-radius: 20px;
}

.arrow {
  font-size: 12px;
  color: #999;
}

/* ===== 卡片列表 ===== */
.card-list {
  padding: 12px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 520px;
  overflow-y: auto;
  padding-right: 10px;
}

.card-list::-webkit-scrollbar {
  width: 5px;
}

.card-list::-webkit-scrollbar-track {
  background: transparent;
}

.card-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.card-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* ===== 运输卡片 ===== */
.transport-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px 16px;
  background: #fff;
  transition: box-shadow 0.2s, border-color 0.2s, transform 0.15s;
  cursor: pointer;
  user-select: none;
}

.transport-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.transport-card.selected {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
  transform: translateY(-1px);
}

.card-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.card-top-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-badge {
  font-size: 12px;
  color: #fff;
  padding: 3px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.card-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 3px 16px;
  margin-bottom: 6px;
}

.info-item {
  display: flex;
  gap: 4px;
  font-size: 13px;
  align-items: baseline;
  min-width: 0;
}

.info-item .lbl {
  color: #999;
  flex-shrink: 0;
}

.info-item .val {
  color: #555;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.remark {
  font-size: 12px;
  color: #aaa;
}

/* ===== 卡片操作按钮 ===== */
.card-actions {
  display: flex;
  gap: 6px;
  padding-top: 6px;
  border-top: 1px dashed #e5e7eb;
}

.action-btn {
  padding: 3px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #fff;
  transition: 0.2s;
}

.edit-action:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.ship-action:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.complete-action:hover {
  border-color: #22c55e;
  color: #22c55e;
}

.cancel-action:hover {
  border-color: #9ca3af;
  color: #9ca3af;
}

/* ===== 空状态 ===== */
.empty {
  text-align: center;
  color: #bbb;
  font-size: 14px;
  padding: 32px 0;
}

/* ===== 删除模式 ===== */
.delete-group {
  display: flex;
  gap: 8px;
}

.add-btn {
  padding: 6px 14px;
  background: #4ade80;
  border: none;
  color: white;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}

.add-btn:hover {
  background: #22c55e;
}

.btn-delete-mode {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
}

.btn-select-all {
  background: #6aa9ff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-cancel {
  background: #2ecc71;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete-mode:hover,
.btn-select-all:hover,
.btn-delete:hover,
.btn-cancel:hover {
  opacity: 0.85;
}

/* ===== 弹窗 ===== */
.mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  width: 520px;
  max-height: 80vh;
  background: white;
  border-radius: 12px;
  padding: 28px 32px;
  overflow-y: auto;
}

.modal-title {
  margin: 0 0 20px;
  font-size: 18px;
  color: #333;
  text-align: center;
}

.form-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
}

.form-field {
  width: calc(50% - 7px);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-field.full-width {
  width: 100%;
}

.form-field label {
  font-size: 13px;
  color: #666;
}

.form-field input,
.form-field select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-field input:focus,
.form-field select:focus {
  border-color: #4ade80;
}

.form-field input[readonly] {
  background: #f5f5f5;
  color: #888;
  cursor: default;
}

.modal-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-cancel-modal {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel-modal:hover {
  background: #f3f4f6;
}

.btn-submit {
  padding: 8px 20px;
  border: none;
  border-radius: 8px;
  background: #111;
  color: white;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-submit:hover {
  background: #22c55e;
}
</style>
