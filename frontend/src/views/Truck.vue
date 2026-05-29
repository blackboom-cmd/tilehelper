<template>
  <div class="truck-page">
    <h1>{{ selectMode ? '选择货车' : '货车表' }}</h1>
    <div class="sub-row">
      <span class="sub-title">{{ selectMode ? '点击选择需要的货车' : '展示货车信息' }}</span>
      <div class="toolbar">
        <template v-if="selectMode">
          <button class="btn-back" @click="goBack">返回</button>
        </template>
        <template v-else>
          <button v-if="!isDeleteMode" class="btn-delete-mode" @click="isDeleteMode = true">
            批量删除
          </button>
          <div v-else class="delete-group">
            <button class="btn-select-all" @click="selectAll">全选</button>
            <button class="btn-delete" @click="handleBatchDelete">删除选中</button>
            <button class="btn-cancel" @click="cancelDeleteMode">取消</button>
          </div>
          <button class="add-btn" @click="openAdd">+ 新增货车</button>
        </template>
      </div>
    </div>

    <div class="card-grid">
      <div class="truck-card" v-for="truck in trucks" :key="truck.id">
        <div class="card-top-right">
          <input
            v-if="isDeleteMode"
            type="checkbox"
            :value="truck.id"
            v-model="selectedIds"
            @click.stop
          />
        </div>
        <div class="card-content">
          <h3 class="truck-name">{{ truck.name }}</h3>
          <p class="truck-capacity">载重：{{ truck.capacity ?? '—' }} 吨</p>
        </div>
        <div v-if="selectMode" class="card-actions">
          <button class="action-btn select-btn" @click="selectTruck(truck)">选择</button>
        </div>
        <div v-else-if="!isDeleteMode" class="card-actions">
          <button class="action-btn edit-btn" @click="openEdit(truck)">编辑</button>
          <button class="action-btn delete-btn" @click="handleDelete(truck.id)">删除</button>
        </div>
      </div>

      <div v-if="trucks.length === 0" class="empty">暂无货车，点击"新增货车"添加</div>
    </div>

    <!-- 新增弹窗 -->
    <div v-if="showAdd" class="mask" @click="showAdd = false">
      <div class="modal" @click.stop>
        <h2 class="modal-title">新增货车</h2>
        <div class="form-grid">
          <div class="form-field full-width">
            <label>货车名称</label>
            <input v-model="addForm.name" placeholder="例如：东风天龙" />
          </div>
          <div class="form-field full-width">
            <label>载重（吨）</label>
            <input v-model.number="addForm.capacity" type="number" placeholder="0.00" step="0.01" />
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
        <h2 class="modal-title">编辑货车</h2>
        <div class="form-grid">
          <div class="form-field full-width">
            <label>货车名称</label>
            <input v-model="editForm.name" placeholder="货车名称" />
          </div>
          <div class="form-field full-width">
            <label>载重（吨）</label>
            <input v-model.number="editForm.capacity" type="number" placeholder="0.00" step="0.01" />
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
import { ref, computed, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getTruckList, addTruck, updateTruck, deleteTrucks } from "@/api/truck"

const route = useRoute()
const router = useRouter()

const selectMode = computed(() => route.query.selectMode === 'true')

const trucks = ref([])

const fetchTrucks = async () => {
  const res = await getTruckList()
  trucks.value = res.data
}

onMounted(() => {
  fetchTrucks()
})

// ========== 选择模式 ==========
function goBack() {
  const from = route.query.from || '/transport'
  router.push({ path: from })
}

function selectTruck(truck) {
  const from = route.query.from || '/transport'
  sessionStorage.setItem('selectedTruck', JSON.stringify({
    id: truck.id,
    name: truck.name,
    capacity: truck.capacity
  }))
  router.push({
    path: from,
    query: {
      truckId: truck.id,
      truckName: truck.name,
      truckCapacity: truck.capacity
    }
  })
}

// ========== 新增 ==========
const showAdd = ref(false)
const addForm = ref({ name: "", capacity: null })

const openAdd = () => {
  addForm.value = { name: "", capacity: null }
  showAdd.value = true
}

const submitAdd = async () => {
  await addTruck({
    name: addForm.value.name || "未命名货车",
    capacity: addForm.value.capacity || 0
  })
  showAdd.value = false
  await fetchTrucks()
}

// ========== 编辑 ==========
const showEdit = ref(false)
const editForm = ref({})

const openEdit = (truck) => {
  editForm.value = {
    id: truck.id,
    name: truck.name,
    capacity: truck.capacity
  }
  showEdit.value = true
}

const submitEdit = async () => {
  await updateTruck({
    id: editForm.value.id,
    name: editForm.value.name || "未命名货车",
    capacity: editForm.value.capacity || 0
  })
  showEdit.value = false
  await fetchTrucks()
}

// ========== 单个删除 ==========
const handleDelete = async (id) => {
  if (!confirm("确定要删除该货车吗？")) return
  await deleteTrucks([id])
  await fetchTrucks()
}

// ========== 批量删除 ==========
const isDeleteMode = ref(false)
const selectedIds = ref([])

const selectAll = () => {
  if (selectedIds.value.length === trucks.value.length) {
    selectedIds.value = []
  } else {
    selectedIds.value = trucks.value.map(t => t.id)
  }
}

const cancelDeleteMode = () => {
  isDeleteMode.value = false
  selectedIds.value = []
}

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    alert("请选择要删除的货车")
    return
  }
  await deleteTrucks(selectedIds.value)
  await fetchTrucks()
  cancelDeleteMode()
}
</script>

<style scoped>
.truck-page {
  max-width: 950px;
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

.sub-title {
  font-size: 14px;
  color: #888;
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

/* ===== 卡片网格 ===== */
.card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.truck-card {
  width: 220px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  background: #fff;
  transition: box-shadow 0.2s, border-color 0.2s;
  display: flex;
  flex-direction: column;
}

.truck-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-top-right {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
  min-height: 20px;
}

.card-content {
  flex: 1;
  text-align: center;
  margin-bottom: 16px;
}

.truck-name {
  margin: 0 0 8px;
  font-size: 16px;
  color: #1f2937;
}

.truck-capacity {
  margin: 0;
  font-size: 13px;
  color: #888;
}

/* ===== 卡片操作按钮 ===== */
.card-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.action-btn {
  flex: 1;
  padding: 5px 0;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  background: #fff;
  transition: 0.2s;
}

.edit-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.delete-btn:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.select-btn {
  background: #3b82f6 !important;
  color: #fff !important;
  border-color: #3b82f6 !important;
}

.select-btn:hover {
  background: #2563eb !important;
  border-color: #2563eb !important;
}

/* ===== 返回按钮 ===== */
.btn-back {
  padding: 6px 14px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: 0.2s;
}

.btn-back:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

/* ===== 空状态 ===== */
.empty {
  width: 100%;
  text-align: center;
  color: #bbb;
  font-size: 14px;
  padding: 48px 0;
}

/* ===== 批量删除按钮 ===== */
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

.delete-group {
  display: flex;
  gap: 8px;
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
  width: 420px;
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
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-field label {
  font-size: 13px;
  color: #666;
}

.form-field input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-field input:focus {
  border-color: #4ade80;
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
