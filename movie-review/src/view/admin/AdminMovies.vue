<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-black text-gray-800">🎥 影片管理控制台</h2>
        <p class="text-xs text-gray-500 mt-1">管理全站影片数据，上架新电影或下架违规内容（支持逻辑软删除留痕）</p>
      </div>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2.5 bg-purple-600 hover:bg-purple-700 text-white font-bold rounded-xl transition-all shadow-md shadow-purple-100 cursor-pointer"
      >
        <span>+</span> 上架新电影
      </button>
    </div>

    <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm flex gap-4">
      <a-input
        v-model:value="searchKeyword"
        placeholder="搜索影片标题..."
        size="large"
        allow-clear
        class="flex-1"
      />
      <a-select v-model:value="sortBy" placeholder="排序方式" style="width: 150px">
        <a-select-option value="rating">按评分</a-select-option>
        <a-select-option value="releaseDate">按上映时间</a-select-option>
      </a-select>
    </div>

    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-3 bg-gray-50">
        <div class="grid grid-cols-12 gap-4 text-xs font-bold text-gray-500">
          <div class="col-span-1">ID</div>
          <div class="col-span-1">封面</div>
          <div class="col-span-3">标题</div>
          <div class="col-span-2">导演</div>
          <div class="col-span-2">评分 / 评论数</div>
          <div class="col-span-1">数据状态</div>
          <div class="col-span-2 text-center">操作</div>
        </div>
      </div>

      <div v-if="loading" class="p-12">
        <a-skeleton active :paragraph="{ rows: 4 }" />
      </div>

      <div v-else-if="movies.length === 0" class="p-12 text-center">
        <span class="text-4xl">📭</span>
        <p class="text-gray-500 mt-4 font-bold">暂无影片</p>
        <p class="text-xs text-gray-400">点击右上角按钮添加新影片</p>
      </div>

      <div v-else>
        <div
          v-for="movie in movies"
          :key="movie.id"
          :class="movie.deleted ? 'bg-gray-100/70' : ''"
          class="border-b border-gray-50 px-6 py-4 hover:bg-purple-50/30 transition-colors"
        >
          <div class="grid grid-cols-12 gap-4 items-center">
            <div class="col-span-1 text-sm font-mono text-gray-600">#{{ movie.id }}</div>
            <div class="col-span-1">
              <img :src="movie.coverUrl" class="w-10 h-14 object-cover rounded-lg shadow-sm" :class="movie.deleted ? 'grayscale opacity-60' : ''" />
            </div>
            <div class="col-span-3">
              <p class="font-bold text-sm" :class="movie.deleted ? 'text-gray-400 line-through' : 'text-gray-800'">{{ movie.title }}</p>
              <p class="text-xs text-gray-400 line-clamp-1">{{ movie.cast }}</p>
            </div>
            <div class="col-span-2 text-sm text-gray-600 truncate">{{ movie.director }}</div>
            <div class="col-span-2 text-sm">
              <span class="text-yellow-500 font-bold">★ {{ movie.averageScore?.toFixed(1) || '0.0' }}</span>
              <span class="text-gray-400 text-xs ml-2">({{ movie.reviewCount || 0 }}评)</span>
            </div>
            
            <div class="col-span-1">
              <span
                :class="movie.deleted ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'"
                class="px-2.5 py-0.5 rounded-full text-xs font-black"
              >
                {{ movie.deleted ? '已下架' : '正常' }}
              </span>
            </div>

            <div class="col-span-2 flex items-center justify-center gap-2">
              <button
                @click="handleEdit(movie)"
                class="px-2.5 py-1 bg-blue-100 text-blue-700 rounded-lg text-xs font-bold hover:bg-blue-200 transition-colors cursor-pointer"
              >
                ✏️ 编辑
              </button>
              <button
                v-if="!movie.deleted"
                @click="handleDelete(movie)"
                class="px-2.5 py-1 bg-red-100 text-red-700 rounded-lg text-xs font-bold hover:bg-red-200 transition-colors cursor-pointer"
              >
                🗑️ 下架
              </button>
              <span v-else class="text-xs text-gray-400 font-medium">留痕留存中</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!loading && movies.length > 0" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between bg-white rounded-2xl border border-gray-200 shadow-sm">
      <p class="text-xs text-gray-400">共 {{ total }} 部影片</p>
      <a-pagination
        :current="page"
        :total="total"
        :page-size="size"
        @change="handlePageChange"
      />
    </div>

    <a-modal
      v-model:open="showAddModal"
      :title="editingMovie ? '修改影片信息' : '上架新电影'"
      :footer="null"
      @cancel="closeModal"
    >
      <a-form :model="formData" layout="vertical" @finish="handleSubmit">
        <a-form-item label="影片标题" :rules="[{ required: true, message: '请输入影片标题' }]">
          <a-input v-model:value="formData.title" placeholder="如：肖申克的救赎" />
        </a-form-item>
        <a-form-item label="导演" :rules="[{ required: true, message: '请输入导演' }]">
          <a-input v-model:value="formData.director" placeholder="如：弗兰克·德拉邦特" />
        </a-form-item>
        <a-form-item label="主演">
          <a-input v-model:value="formData.cast" placeholder="如：蒂姆·罗宾斯, 摩根·弗里曼" />
        </a-form-item>
        <a-form-item label="上映日期" :rules="[{ required: true, message: '请选择上映日期' }]">
          <a-input v-model:value="formData.releaseDate" placeholder="格式如：1994-09-23" />
        </a-form-item>
        <a-form-item label="封面URL" :rules="[{ required: true, message: '请输入封面URL' }]">
          <a-input v-model:value="formData.coverUrl" placeholder="https://..." />
        </a-form-item>
        <a-form-item label="剧情简介">
          <a-textarea v-model:value="formData.description" :rows="3" placeholder="请输入影片简介..." />
        </a-form-item>
        <div class="flex justify-end gap-3 mt-6">
          <a-button @click="closeModal">取消</a-button>
          <a-button type="primary" html-type="submit" class="!bg-purple-600 hover:!bg-purple-700">
            {{ editingMovie ? '保存修改' : '确认上架' }}
          </a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import request from '../../utils/request'

const loading = ref(true)
const searchKeyword = ref('')
const sortBy = ref('rating')
const movies = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const showAddModal = ref(false)
const editingMovie = ref(null)

const formData = ref({
  title: '',
  director: '',
  cast: '',
  releaseDate: '',
  coverUrl: '',
  description: ''
})

// 🚀 后台专用列表：GET /admin/movies 返回全量影片（含已下架 deleted=true 的留痕数据）
const fetchMovies = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/movies', {
      params: { page: page.value, size: size.value, keyword: searchKeyword.value, sort: sortBy.value }
    })
    if (res.code === 200) {
      // 💡 大厂级兼容：由于前端后台要调试“已下架”留痕状态，我们这里直接接收全量列表
      const responseData = res.data || res
      movies.value = responseData.list || []
      total.value = responseData.total || 0
    }
  } catch (error) {
    console.error('获取影片列表失败:', error)
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  editingMovie.value = null
  formData.value = { title: '', director: '', cast: '', releaseDate: '2026-06-09', coverUrl: '', description: '' }
  showAddModal.value = true
}

const handleEdit = (movie) => {
  editingMovie.value = movie
  formData.value = {
    title: movie.title,
    director: movie.director,
    cast: movie.cast,
    releaseDate: movie.releaseDate,
    coverUrl: movie.coverUrl,
    description: movie.description || ''
  }
  showAddModal.value = true
}

// 🚀 【严格对齐合同 2.5】：逻辑软下架路口
const handleDelete = async (movie) => {
  try {
    console.log(`🚀 后台正在向管理员专属路口 DELETE /api/admin/movies/${movie.id} 发起弹药...`)
    const res = await request.delete(`/admin/movies/${movie.id}`)
    if (res.code === 200) {
      message.success(`影片《${movie.title}》已成功下架留痕`)
      // 🔄 重新拉取大盘，这时候它会完美变成灰色的“已下架”标签！
      fetchMovies()
    }
  } catch (error) {
    console.error('下架失败:', error)
  }
}

// 🚀 【严格对齐合同 2.3 & 2.4】：添加与修改
const handleSubmit = async () => {
  if (!formData.value.title || !formData.value.director || !formData.value.coverUrl) {
    message.error('请填写必填字段')
    return
  }
  // 后端按 yyyy-MM-dd 严格解析上映日期，提交前先校验格式
  if (formData.value.releaseDate && !/^\d{4}-\d{2}-\d{2}$/.test(formData.value.releaseDate)) {
    message.error('上映日期格式应为 yyyy-MM-dd，例如 1994-09-23')
    return
  }

  const payload = {
    title: formData.value.title,
    director: formData.value.director,
    cast: formData.value.cast,
    releaseDate: formData.value.releaseDate,
    coverUrl: formData.value.coverUrl,
    description: formData.value.description
  }
  
  try {
    if (editingMovie.value) {
      // 🎯 【合同 2.4】管理员修改影片
      console.log(`🚀 向合同路口 PUT /api/admin/movies/${editingMovie.value.id} 提交修改`)
      const res = await request.put(`/admin/movies/${editingMovie.value.id}`, payload)
      if (res.code === 200) {
        message.success('影片信息已成功更新')
        fetchMovies()
      }
    } else {
      // 🎯 【合同 2.3】管理员添加上架影片
      console.log('🚀 向合同路口 POST /api/admin/movies 发起全网新片上架发布...')
      const res = await request.post('/admin/movies', payload)
      if (res.code === 201 || res.code === 200) {
        message.success('新影片已成功发布上架！')
        fetchMovies()
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    closeModal()
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingMovie.value = null
}

const handlePageChange = (pageNum) => {
  page.value = pageNum
  fetchMovies()
}

watch([searchKeyword, sortBy], () => {
  page.value = 1
  fetchMovies()
})

onMounted(() => {
  fetchMovies()
})
</script>