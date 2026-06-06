<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-black text-gray-800">🎥 影片管理控制台</h2>
        <p class="text-xs text-gray-500 mt-1">管理全站影片数据，上架新电影或下架违规内容</p>
      </div>
      <button
        @click="showAddModal = true"
        class="flex items-center gap-2 px-4 py-2.5 bg-purple-600 hover:bg-purple-700 text-white font-bold rounded-xl transition-all shadow-md shadow-purple-100"
      >
        <span>+</span> 上架新电影
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm flex gap-4">
      <a-input
        v-model:value="searchKeyword"
        placeholder="搜索影片标题..."
        size="large"
        allow-clear
        class="flex-1"
      />
      <a-select v-model:value="sortBy" placeholder="排序方式">
        <a-select-option value="rating">按评分</a-select-option>
        <a-select-option value="releaseDate">按上映时间</a-select-option>
      </a-select>
    </div>

    <!-- 影片列表 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-3 bg-gray-50">
        <div class="grid grid-cols-12 gap-4 text-xs font-bold text-gray-500">
          <div class="col-span-1">ID</div>
          <div class="col-span-1">封面</div>
          <div class="col-span-3">标题</div>
          <div class="col-span-2">导演</div>
          <div class="col-span-2">评分 / 评论数</div>
          <div class="col-span-2">上映日期</div>
          <div class="col-span-1 text-center">操作</div>
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
          class="border-b border-gray-50 px-6 py-4 hover:bg-purple-50/30 transition-colors"
        >
          <div class="grid grid-cols-12 gap-4 items-center">
            <div class="col-span-1 text-sm font-mono text-gray-600">#{{ movie.id }}</div>
            <div class="col-span-1">
              <img :src="movie.coverUrl" class="w-10 h-14 object-cover rounded-lg" />
            </div>
            <div class="col-span-3">
              <p class="font-bold text-gray-800 text-sm">{{ movie.title }}</p>
              <p class="text-xs text-gray-400 line-clamp-1">{{ movie.cast }}</p>
            </div>
            <div class="col-span-2 text-sm text-gray-600 truncate">{{ movie.director }}</div>
            <div class="col-span-2 text-sm">
              <span class="text-yellow-500 font-bold">★ {{ movie.averageScore }}</span>
              <span class="text-gray-400 text-xs ml-2">({{ movie.reviewCount }}评)</span>
            </div>
            <div class="col-span-2 text-xs text-gray-400">{{ movie.releaseDate }}</div>
            <div class="col-span-1 flex items-center justify-center gap-1">
              <button
                @click="handleEdit(movie)"
                class="px-2 py-1 bg-blue-100 text-blue-700 rounded-lg text-xs font-bold hover:bg-blue-200 transition-colors"
              >
                ✏️
              </button>
              <button
                @click="handleDelete(movie.id)"
                class="px-2 py-1 bg-red-100 text-red-700 rounded-lg text-xs font-bold hover:bg-red-200 transition-colors"
              >
                🗑️
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="!loading && movies.length > 0" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
        <p class="text-xs text-gray-400">共 {{ total }} 部影片</p>
        <a-pagination
          :current="page"
          :total="total"
          :page-size="size"
          show-size-changer
          show-quick-jumper
          @change="handlePageChange"
        />
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <a-modal
      v-model:open="showAddModal"
      :title="editingMovie ? '修改影片信息' : '上架新电影'"
      :footer="null"
    >
      <a-form :model="formData" layout="vertical">
        <a-form-item label="影片标题" name="title" :rules="[{ required: true, message: '请输入影片标题' }]">
          <a-input v-model:value="formData.title" placeholder="如：肖申克的救赎" />
        </a-form-item>
        <a-form-item label="导演" name="director" :rules="[{ required: true, message: '请输入导演' }]">
          <a-input v-model:value="formData.director" placeholder="如：弗兰克·德拉邦特" />
        </a-form-item>
        <a-form-item label="主演" name="cast">
          <a-input v-model:value="formData.cast" placeholder="如：蒂姆·罗宾斯, 摩根·弗里曼" />
        </a-form-item>
        <a-form-item label="上映日期" name="releaseDate" :rules="[{ required: true, message: '请选择上映日期' }]">
          <a-date-picker v-model:value="formData.releaseDate" style="width: 100%" />
        </a-form-item>
        <a-form-item label="封面URL" name="coverUrl" :rules="[{ required: true, message: '请输入封面URL' }]">
          <a-input v-model:value="formData.coverUrl" placeholder="https://..." />
        </a-form-item>
        <a-form-item label="剧情简介" name="description">
          <a-textarea v-model:value="formData.description" :rows="3" placeholder="请输入影片简介..." />
        </a-form-item>
        <div class="flex justify-end gap-3 mt-6">
          <a-button @click="showAddModal = false">取消</a-button>
          <a-button type="primary" @click="handleSubmit">
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
  releaseDate: null,
  coverUrl: '',
  description: ''
})

const fetchMovies = async () => {
  loading.value = true
  try {
    const params = { 
      page: page.value, 
      size: size.value,
      keyword: searchKeyword.value,
      sortBy: sortBy.value
    }
    const res = await request.get('/movies', { params })
    if (res.code === 200) {
      movies.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取影片列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (movie) => {
  editingMovie.value = movie
  formData.value.title = movie.title
  formData.value.director = movie.director
  formData.value.cast = movie.cast
  formData.value.releaseDate = new Date(movie.releaseDate)
  formData.value.coverUrl = movie.coverUrl
  formData.value.description = movie.description || ''
  showAddModal.value = true
}

const handleDelete = async (movieId) => {
  try {
    const res = await request.delete(`/movies/${movieId}`)
    if (res.code === 200) {
      message.success(`影片 #${movieId} 已下架`)
      movies.value = movies.value.filter(m => m.id !== movieId)
      total.value--
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formData.value.title || !formData.value.director || !formData.value.coverUrl) {
    message.error('请填写必填字段')
    return
  }
  
  const data = {
    title: formData.value.title,
    director: formData.value.director,
    cast: formData.value.cast,
    releaseDate: formData.value.releaseDate ? formData.value.releaseDate.toISOString().split('T')[0] : null,
    coverUrl: formData.value.coverUrl,
    description: formData.value.description
  }
  
  try {
    if (editingMovie.value) {
      const res = await request.put(`/movies/${editingMovie.value.id}`, data)
      if (res.code === 200) {
        const index = movies.value.findIndex(m => m.id === editingMovie.value.id)
        if (index !== -1) {
          movies.value[index] = { ...movies.value[index], ...data }
        }
        message.success('影片信息已更新')
      }
    } else {
      const res = await request.post('/movies', data)
      if (res.code === 201) {
        message.success('新影片上架成功')
        total.value++
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
  
  showAddModal.value = false
  editingMovie.value = null
  formData.value.title = ''
  formData.value.director = ''
  formData.value.cast = ''
  formData.value.releaseDate = null
  formData.value.coverUrl = ''
  formData.value.description = ''
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
