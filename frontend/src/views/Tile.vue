<template>
  <div class="setting-page">
    <h1>瓦片信息表</h1>
    <p class="sub-title">展示瓦片信息表格</p>

    <div class="toolbar">

      <button
          v-if="!isDeleteMode"
          class="btn-delete-mode"
          @click="isDeleteMode = true"
      >
        批量删除
      </button>

      <div v-else class="delete-group">
        <button class="btn-select-all" @click="selectAll">全选</button>
        <button class="btn-delete" @click="handleBatchDelete">删除选中</button>
        <button class="btn-cancel" @click="cancelDeleteMode">取消</button>
      </div>

    </div>

    <div class="grid">
      <div class="card"
           v-for="item in tiles"
           :key="item.id"
           @click="!isDeleteMode && handleCardClick(item)">

        <!-- 图片 -->
        <input
            v-if="isDeleteMode"
            type="checkbox"
            :value="item.id"
            v-model="selectedIds"
            @click.stop
        />

        <img class="img" :src="'http://localhost:8080' + item.imageUrl" />

        <!-- 名称 -->
        <div class="name">
          {{ item.tileName }}
        </div>

        <!-- 价格 -->
        <div class="price">
          ￥{{ item.price }}
        </div>

        <!-- 详情按钮（非删除模式下显示） -->
        <button
            v-if="!isDeleteMode"
            class="card-detail-btn"
            @click.stop="openDetail(item)"
        >
          详情
        </button>

      </div>

    </div>

    <!-- 添加按钮 -->
    <div class="add-btn" @click="openAdd">
      +
    </div>

  </div>

  <!-- 瓦片详情模态框 -->
  <div v-if="showDetail" class="mask" @click="closeDetail">

    <div class="modal" @click.stop>

      <!-- 左侧图片 -->
      <div class="left">
        <img :src="'http://localhost:8080' + selectedTile.imageUrl" />
      </div>

      <!-- 右侧全部字段 -->
      <div class="right">

        <h2>{{ selectedTile.tileName }}</h2>

        <div class="field">
          <span class="label">型号：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.model }}</span>
          <input v-else v-model="editForm.model" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">规格：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.specification }}</span>
          <input v-else v-model="editForm.specification" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">颜色：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.color }}</span>
          <input v-else v-model="editForm.color" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">价格：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.price }}</span>
          <input v-else v-model="editForm.price" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">单位数量：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.unitQuantity }}</span>
          <input v-else v-model="editForm.unitQuantity" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">库存数量：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.stockQuantity }}</span>
          <input v-else v-model="editForm.stockQuantity" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">配件：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.accessories }}</span>
          <input v-else v-model="editForm.accessories" class="edit-input" />
        </div>

        <div class="field">
          <span class="label">存放位置：</span>
          <span v-if="!isEdit" class="value">{{ selectedTile.location }}</span>
          <input v-else v-model="editForm.location" class="edit-input" />
        </div>

        <button v-if="!isEdit" class="edit-btn" @click="isEdit = true">
          编辑
        </button>

        <button v-if="isEdit" class="edit-btn" @click="handleSave">
          保存
        </button>

      </div>

    </div>

  </div>

  <!-- 新增弹窗 -->
  <div v-if="showAdd" class="mask" @click="showAdd = false">

    <div class="modal" @click.stop>

      <div class="left">
        <img
            v-if="form.imageUrl"
            :src="'http://localhost:8080' + form.imageUrl"
        />
        <!-- 自定义按钮 -->
        <div
            v-else
            class="upload-btn"
            @click="$refs.fileInput.click()"
        >
          选择图片
        </div>
      </div>

      <div class="right">
        <h3 class="title">新增瓦片</h3>

        <!-- 图片上传 -->
        <div class="upload-box">

          <!-- 隐藏原始 input -->
          <input
              type="file"
              accept="image/*"
              @change="handleUpload"
              ref="fileInput"
              class="file-input"
          />

        </div>
        <input class="input" v-model="form.tileName" placeholder="瓦片名称" />
        <input class="input" v-model="form.model" placeholder="型号" />
        <input class="input" v-model="form.specification" placeholder="规格" />
        <input class="input" v-model="form.color" placeholder="颜色" />

        <div class="field-hint">💰 售价（元）</div>
        <input class="input" v-model="form.price" placeholder="例如：25.00" />
        <div class="field-hint">📦 每包数量（例如：10片/包）</div>
        <input class="input" v-model="form.unitQuantity" placeholder="例如：10" />
        <div class="field-hint">📊 当前库存数量</div>
        <input class="input" v-model="form.stockQuantity" placeholder="例如：100" />

        <input class="input" v-model="form.accessories" placeholder="配件说明" />
        <input class="input" v-model="form.location" placeholder="存放位置" />

        <button class="submit-btn" @click="submitAdd">确认新增</button>
      </div>

    </div>

  </div>

</template>

<script setup>
import { ref, onMounted, inject } from "vue"
import { getTileList } from "@/api/tile"

const selectedOrderTile = inject('selectedOrderTile')

const tiles = ref([])

onMounted(async () => {
  const res = await getTileList()
  tiles.value = res.data
})

const selectedTile = ref(null)
const showDetail = ref(false)

const handleCardClick = (item) => {
  selectedOrderTile.value = item
}

const openDetail = (item) => {
  selectedTile.value = item
  showDetail.value = true

  editForm.value = JSON.parse(JSON.stringify(item))
}

const closeDetail = () => {
  showDetail.value = false
}

// 添加功能相关
const showAdd = ref(false)

const openAdd = () => {
  showAdd.value = true
}

const form = ref({
  tileName: "",
  model: "",
  specification: "",
  color: "",
  price: 0,
  unitQuantity: 0,
  stockQuantity: 0,
  accessories: "",
  imageUrl: "",
  location: ""
})

import axios from "axios"

const submitAdd = async () => {
  await axios.post("http://localhost:8080/tile/add", form.value)

  showAdd.value = false

  const res = await axios.get("http://localhost:8080/tile/list", {
    params: { _t: Date.now() }
  })
  tiles.value = res.data
}

// 编辑功能可以在详情页实现
const isEdit = ref(false)
const editForm = ref({})

const handleSave = async () => {
  const res = await axios.put("http://localhost:8080/tile/update", editForm.value)

  if (res.data === true) {
    const listRes = await axios.get("http://localhost:8080/tile/list", {
      params: { _t: Date.now() }
    })
    tiles.value = listRes.data

    selectedTile.value = { ...editForm.value }

    isEdit.value = false
  } else {
    alert("更新失败")
  }
}

const handleUpload = async (e) => {
  const file = e.target.files[0]

  if (!file) {
    return
  }

  const formData = new FormData()
  formData.append("file", file)

  const res = await axios.post("http://localhost:8080/upload", formData)

  // 把后端返回的路径存进表单
  form.value.imageUrl = res.data.url
}

// 删除功能
const isDeleteMode = ref(false)
const selectedIds = ref([])

// 进入删除模式
const enterDeleteMode = () => {
  isDeleteMode.value = true
  selectedIds.value = []
}

// 全选 / 取消全选
const selectAll = () => {
  if (selectedIds.value.length === tiles.value.length) {
    selectedIds.value = []
  } else {
    selectedIds.value = tiles.value.map(item => item.id)
  }
}

// 取消删除模式
const cancelDeleteMode = () => {
  isDeleteMode.value = false
  selectedIds.value = []
}

// 批量删除（核心）
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    alert("请选择要删除的瓦片")
    return
  }

  await axios.delete("http://localhost:8080/tile/delete", {
    data: selectedIds.value
  })

  // 重新加载列表
  const res = await axios.get("http://localhost:8080/tile/list", {
    params: { _t: Date.now() }
  })
  tiles.value = res.data

  // 退出删除模式
  cancelDeleteMode()
}

</script>

<style scoped>

.sub-title {
  font-size: 14px;   /* ⭐变小 */
  color: #888;       /* ⭐更像订单页的灰字 */
  margin-top: 4px;
}

.setting-page {
  max-width: 950px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;

  display: flex;
  flex-direction: column;
  min-height: 90vh;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: flex-start;

  margin-top: 20px;
}

.card {
  width: 160px;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 10px;
  background: white;
}

.img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}

.name {
  margin-top: 8px;
  font-weight: bold;
}

.price {
  color: red;
  margin-top: 4px;
}

/* ===== 卡片上的详情按钮 ===== */
.card-detail-btn {
  width: 100%;
  margin-top: 8px;
  padding: 4px 0;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #f5f5f5;
  color: #666;
  font-size: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.card-detail-btn:hover {
  background: #e0e0e0;
  color: #333;
  border-color: #bbb;
}

/* ===============================
   瓦片详情弹窗样式（Detail Modal）
   功能：
   - 点击瓦片卡片后展示详细信息弹窗
   - 左侧显示图片
   - 右侧显示数据库全部字段信息
   - 点击遮罩关闭弹窗
   =============================== */
.mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  width: 70%;
  height: 70%;
  background: white;

  border-radius: 12px;

  display: flex;           /* ⭐ 横向布局 */
  overflow: hidden;        /* 防止溢出 */
}

.left {
  width: 50%;              /* ⭐ 对半 */
  height: 100%;

  display: flex;              /* ⭐ 开启弹性布局 */
  align-items: center;        /* ⭐ 垂直居中 */
  justify-content: center;    /* ⭐ 水平居中 */

  background: #f8f8f8;        /* 可选：让区域更清晰 */
}

.left img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.right {
  width: 50%;              /* ⭐ 对半 */
  height: 100%;

  padding: 20px;

  display: flex;
  flex-direction: column;  /* ⭐ 让按钮能贴底 */
  overflow-y: auto;        /* ⭐ 内容多可滚动 */
}

.edit-btn {
  margin-top: auto;        /* ⭐ 自动推到底部 */
  padding: 10px;
}

.empty {
  color: #aaa;
  font-size: 14px;
}

/* ===============================
   添加按钮样式（Add Button）
   功能：
   - 固定在页面右下角
   - 圆形设计，突出显示
   - 鼠标悬停时变色并稍微放大
   =============================== */
.add-btn {
  position: fixed;
  bottom: 40px;
  right: 50px;

  width: 56px;
  height: 56px;
  border-radius: 50%;

  background-color: #4ade80; /* 浅绿色 */
  color: white;

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 32px;
  font-weight: bold;

  cursor: pointer;

  box-shadow: 0 4px 10px rgba(0,0,0,0.2);
  transition: 0.2s;
}

.add-btn:hover {
  background-color: #22c55e;
  transform: scale(1.05);
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.input {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 12px;

  border-radius: 10px;
  border: 1px solid #ddd;

  font-size: 14px;
  outline: none;
  transition: 0.2s;
}

.input:focus {
  border-color: #4ade80;
}

.upload-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
}

.preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #eee;
  margin-top: 8px;
}

.submit-btn {
  width: 100%;
  padding: 12px;

  border: none;
  border-radius: 10px;

  background-color: #111;
  color: white;

  font-size: 16px;
  font-weight: bold;

  cursor: pointer;
  transition: 0.2s;
}

.submit-btn:hover {
  background-color: #22c55e;
}

.file-input {
  display: none;
}

.upload-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
}

.upload-btn {
  width: 100%;
  text-align: center;

  padding: 10px;
  border-radius: 10px;
  border: 1px dashed #ccc;

  cursor: pointer;
  color: #666;
  transition: 0.2s;
}

.upload-btn:hover {
  border-color: #4ade80;
  color: #4ade80;
}

.preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 10px;
  margin-top: 10px;
}

.field-hint {
  font-size: 12px;
  color: #888;
  margin: 4px 0;
}

.field {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
}

.label {
  width: 90px;
  color: #666;
}

.value {
  flex: 1;
  line-height: 32px;
}

.edit-input {
  flex: 1;

  height: 32px;
  padding: 0 12px;

  margin: 0;

  border: 1px solid #ddd;
  border-radius: 8px;

  font-size: 14px;
  outline: none;

  box-sizing: border-box;
}

/* ===============================
   删除按钮样式调整
   =============================== */
.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-top: -40px;
  margin-right: 20px;
}

/* ===== 按钮基础间距 ===== */
.delete-group {
  display: flex;
  gap: 8px;   /* ⭐按钮之间小间距 */
}

/* ===== 批量删除（初始按钮） ===== */
.btn-delete-mode {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

/* ===== 全选（淡蓝色） ===== */
.btn-select-all {
  background: #6aa9ff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

/* ===== 删除选中（红色） ===== */
.btn-delete {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

/* ===== 取消（淡绿色） ===== */
.btn-cancel {
  background: #2ecc71;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

/* ===== 统一 hover 效果（轻微变亮） ===== */
.btn-delete-mode:hover,
.btn-select-all:hover,
.btn-delete:hover,
.btn-cancel:hover {
  opacity: 0.85;
}

h1 {
  color: #333;
}
</style>