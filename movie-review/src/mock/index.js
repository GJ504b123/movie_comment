/**
 * ========================================================
 * 🎬 MovieReview 全栈影评系统 - 严格对齐后端 API 合同的 Mock 拦截总闸
 * ========================================================
 */
import Mock from 'mockjs'

// 🏛️ 1. 动态物理基础内存数据库（赋予系统完美的持久化和互动记忆）
const userDatabase = {
  '101': { id: 101, username: 'wenwen', password: '123456', email: 'wenwen@example.com', role: 'admin', status: 'approved', createTime: '2026-01-15T10:30:00Z', lastLoginTime: '2026-06-07T14:00:00Z' },
  '102': { id: 102, username: 'xiaoming', password: '123456', email: 'xiaoming@example.com', role: 'user', status: 'approved', createTime: '2026-02-20T14:20:00Z', lastLoginTime: '2026-06-07T15:30:00Z' },
  '103': { id: 103, username: 'filmfan', password: '123456', email: 'fan@example.com', role: 'user', status: 'approved', createTime: '2025-01-01T10:00:00Z', lastLoginTime: '2025-03-20T15:30:00Z' },
}

// ⏳ 管理员专用：待审核用户列表底子 (对应合同 2.1)
const pendingUsersList = [
  { id: 102, username: 'newbie', email: 'newbie@example.com', createTime: '2025-03-19T08:00:00Z' }
]

// 🎥 基础电影列表数据（对应合同 3.1、3.6）
// 🎥 基础电影列表数据（豪华扩容 6 部大片版！加入 deleted 逻辑软删除标记）
let movieDatabase = [
  { id: 201, title: '肖申克的救赎', coverUrl: 'https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg', averageScore: 9.2, reviewCount: 128, releaseDate: '1994-09-23', director: '弗兰克·德拉邦特', cast: '蒂姆·罗宾斯, 摩根·弗里曼', description: '这场谋杀使银行家安迪蒙冤入狱，在长达20年的牢狱生涯中，他用信念和智慧为自己完成了救赎，重获自由。', deleted: false },
  { id: 202, title: '霸王别姬', coverUrl: 'https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg', averageScore: 9.6, reviewCount: 95, releaseDate: '1993-01-01', director: '陈凯歌', cast: '张国荣, 张丰毅, 巩俐', description: '影片通过两位京剧伶人程蝶衣与段小楼风雨坎坷的坎坷命运，展现了中国半个多世纪的风云变幻以及对传统文化、人性的深度反思。', deleted: false },
  { id: 203, title: '星际穿越', coverUrl: 'https://images.pexels.com/photos/15209918/pexels-photo-15209918.png', averageScore: 9.4, reviewCount: 44, releaseDate: '2014-11-07', director: '克里斯托弗·诺兰', cast: '马修·麦康纳, 安妮·海瑟薇', description: '近未来地球遭遇严重的枯萎病，前飞行员库珀不得不告别年幼的女儿，毅然穿越虫洞深入未知的宇宙，为濒临灭绝的人类寻找新的家园。', deleted: false },
  // 🚀 汶汶专属调试新弹药：
  { id: 204, title: '千与千寻', coverUrl: 'https://images.pexels.com/photos/3802510/pexels-photo-3802510.jpeg', averageScore: 9.3, reviewCount: 60, releaseDate: '2001-07-20', director: '宫崎骏', cast: '柊瑠美, 入野自由', description: '10岁的少女千千寻与父母一同驱车前往新家，途中迷路误闯入一个人类不应该进入的、属于神灵的奇幻小镇...', deleted: false },
  { id: 205, title: '盗梦空间', coverUrl: 'https://images.pexels.com/photos/2884867/pexels-photo-2884867.jpeg', averageScore: 9.2, reviewCount: 80, releaseDate: '2010-09-01', director: '克里斯托弗·诺兰', cast: '莱昂纳多·迪卡普里奥', description: '多姆·柯布是一名极其擅长潜入人类梦境、窃取核心机密的顶级商业间谍。这一次，他需要挑战一个不可能的任务：不是窃取，而是植入思想。', deleted: false },
  { id: 206, title: '泰坦尼克号', coverUrl: 'https://images.pexels.com/photos/45853/the-sydney-opera-house-opera-house-sydney-australia-45853.jpeg', averageScore: 9.4, reviewCount: 110, releaseDate: '1997-12-19', director: '詹姆斯·卡梅隆', cast: '莱昂纳多·迪卡普里奥, 凯特·温丝莱特', description: '处于不同阶层的穷小子杰克与贵族女露丝在泰坦尼克号豪华巨轮上相识相恋。然而，一场突如其来的冰山撞击，让这场凄美的爱情成为了永恒。', deleted: false }
]

// 💬 电影绑定的独立评论列表（对应合同 3.2、2.6）
// 💬 电影绑定的独立评论列表（疯狂注水豪华版：完美撑爆分页，触发翻页特效！）
let reviewDatabase = [
  { id: 301, movieId: 201, movieTitle: '肖申克的救赎', userId: 103, username: 'filmfan', rating: 10, comment: '永远的神作，信念是关不住的鸟儿，它每一片羽毛都闪耀着自由的光辉。', likeCount: 132, hidden: false, createTime: '2025-03-01T09:00:00Z' },
  { id: 302, movieId: 201, movieTitle: '肖申克的救赎', userId: 101, username: 'wenwen', rating: 9, comment: '被安迪在雷雨中张开双臂迎接自由的镜头震撼到了，吹爆这个骨架屏和路由跳转！', likeCount: 88, hidden: false, createTime: '2026-05-28T11:30:00Z' },
  { id: 303, movieId: 202, movieTitle: '霸王别姬', userId: 102, username: '戏迷小张', rating: 10, comment: '不疯魔不成活。张国荣把程蝶衣那种执着、绝望演得入木三分，绝代风华！', likeCount: 99, hidden: false, createTime: '2025-02-14T20:15:00Z' },
  { id: 304, movieId: 203, movieTitle: '星际穿越', userId: 104, username: '科幻迷', rating: 9, comment: '当汉斯·季默的管风琴轰鸣响起，库珀在黑洞深处的五维空间哭泣时，浑身直接起鸡皮疙瘩！', likeCount: 52, hidden: false, createTime: '2025-03-20T16:20:00Z' },
  // 🚀 以下是专门为你塞入的测试分页弹药（全部集中在 201 肖申克身上，强行撑破前台每页 5 条的限制！）
  { id: 305, movieId: 201, movieTitle: '肖申克的救赎', userId: 105, username: '路人甲', rating: 8, comment: '希望让人痛苦，但也是唯一能让人活下去的东西。摩根弗里曼的旁白太治愈了。', likeCount: 23, hidden: false, createTime: '2026-06-01T08:00:00Z' },
  { id: 306, movieId: 201, movieTitle: '肖申克的救赎', userId: 106, username: '影评人特尼', rating: 9, comment: '体制化（Institutionalized）那段台词太深刻了，细思极恐，我们每个人都在被体制化。', likeCount: 45, hidden: false, createTime: '2026-06-02T09:15:00Z' },
  { id: 307, movieId: 201, movieTitle: '肖申克的救赎', userId: 107, username: 'Vue3专家', rating: 10, comment: '自由，就是哪怕身处泥潭，心里也有一片属于自己的太平洋。经典！', likeCount: 67, hidden: false, createTime: '2026-06-03T10:30:00Z' },
  { id: 308, movieId: 201, movieTitle: '肖申克的救赎', userId: 108, username: '午夜爆米花', rating: 7, comment: '节奏稍微有点慢，但后半段安迪越狱反杀典狱长的时候真的太爽了，神作当之无愧。', likeCount: 12, hidden: false, createTime: '2026-06-04T14:22:00Z' },
  { id: 309, movieId: 201, movieTitle: '肖申克的救赎', userId: 109, username: '冷眼看电影', rating: 8, comment: '瑞德在法庭上第三次申请假释时的那段自白，彻底完成了和自己的和解。', likeCount: 31, hidden: false, createTime: '2026-06-05T16:45:00Z' },
  { id: 310, movieId: 201, movieTitle: '肖申克的救赎', userId: 110, username: '追光者', rating: 9, comment: 'Fear can hold you prisoner. Hope can set you free. 吹爆这句台词！', likeCount: 19, hidden: false, createTime: '2026-06-06T11:10:00Z' },
  { id: 311, movieId: 201, movieTitle: '肖申克的救赎', userId: 111, username: '电影搬运工', rating: 10, comment: '不管看多少遍，只要看到安迪在污水管里爬行，最后在暴雨中呐喊的镜头，依然热泪盈眶。', likeCount: 156, hidden: false, createTime: '2026-06-07T12:00:00Z' },
  { id: 312, movieId: 201, movieTitle: '肖申克的救赎', userId: 112, username: '小黑子爱看片', rating: 2, comment: '我觉得一般般吧，主角光环太重了，怎么可能二十年挖开墙壁不被发现。', likeCount: 2, hidden: true, createTime: '2026-06-08T19:30:00Z' } // 🚨 这条默认是隐藏的，用来测试你的拉黑过滤！
]

// 📋 系统高级访问日志 (对应合同 2.8)
const adminLogsList = [
  { id: 401, userId: 103, username: 'filmfan', action: 'search_movie', targetId: null, ip: '192.168.1.1', userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...', createTime: '2025-03-20T16:20:00Z' },
  { id: 402, userId: 101, username: 'wenwen', action: 'view_movie_detail', targetId: '201', ip: '192.168.1.5', userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) ...', createTime: '2026-05-31T11:20:00Z' }
]

Mock.setup({ timeout: '150-250' })

// ========================================================
// 🛣️ 2. 全线通电：精确拦截全站符合 Base URL 为 /api 的所有汽车
// ========================================================

// ----------------------------------------------------
// 1. 认证与用户模块
// ----------------------------------------------------

// 【合同 1.1】用户注册
Mock.mock(/\/api\/auth\/register/, 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  console.log('🔥【Mock合同拦截】用户注册:', body)
  
  if (!body.username || !body.password) {
    return { code: 400, message: '请求参数不完整' }
  }

  const newId = Date.now()
  // 注入持久化字典，初始化状态为 pending 待审核
  userDatabase[newId.toString()] = {
    id: newId, username: body.username, password: body.password, email: body.email || 'guest@example.com',
    role: 'user', status: 'pending', createTime: new Date().toISOString()
  }
  // 同步推进管理员待审核列表
  pendingUsersList.push({ id: newId, username: body.username, email: body.email || 'guest@example.com', createTime: new Date().toISOString() })

  return {
    code: 200,
    message: '注册成功，等待管理员审核',
    data: { userId: newId, status: 'pending' }
  }
})

// 【合同 1.2】用户登录
Mock.mock(/\/api\/auth\/login/, 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  console.log('🔥【Mock合同拦截】用户登录:', body)

  const user = Object.values(userDatabase).find(u => u.username === body.username)

  if (!user || user.password !== body.password) {
    return { code: 401, message: '用户名或密码错误', data: null }
  }
  // 严格拦截状态约束
  if (user.status === 'pending') {
    return { code: 403, message: '用户尚未通过审核，无法执行此操作', data: null }
  }
  if (user.status === 'rejected') {
    return { code: 403, message: '您的注册申请已被拒绝，无权访问本系统', data: null }
  }

  return {
    code: 200,
    message: '登录成功',
    data: {
      token: 'Bearer_eyJhbGciOiJIUzI1NiIsRkJmYW4iLCJyb2xlIjoidXNlciIs...',
      user: { id: user.id, username: user.username, role: user.role, status: user.status }
    }
  }
})

// 【合同 1.3】获取当前用户信息
Mock.mock(/\/api\/user\/profile/, 'get', () => {
  console.log('🔥【Mock合同拦截】获取当前用户信息Profile')
  return {
    code: 200,
    message: '成功',
    data: userDatabase['101'] // 默认模拟超级控制人
  }
})

// 【合同 1.4】更新用户信息
Mock.mock(/\/api\/user\/profile/, 'put', () => {
  return { code: 200, message: '更新成功', data: null }
})


// ----------------------------------------------------
// 2. 管理员专用接口
// ----------------------------------------------------

// 【合同 2.1】获取待审核用户列表
Mock.mock(/\/api\/admin\/users\/pending/, 'get', () => {
  console.log('🔥【Mock合同拦截】超管调阅待审核列表')
  return {
    code: 200,
    message: '成功',
    data: {
      list: pendingUsersList,
      total: pendingUsersList.length,
      page: 1, size: 10, totalPages: 1
    }
  }
})

// 【合同 2.2】审核用户（通过/拒绝）
Mock.mock(/\/api\/admin\/users\/\d+\/audit/, 'put', (options) => {
  const body = JSON.parse(options.body || '{}')
  const urlParts = options.url.split('/')
  const userId = urlParts[urlParts.length - 2]
  console.log(`🔥【Mock合同拦截】处理用户 [ID:${userId}] 审核. 动作: ${body.action}`)

  if (userDatabase[userId]) {
    userDatabase[userId].status = body.action === 'approve' ? 'approved' : 'rejected'
  }

  // 移出待审核池
  const idx = pendingUsersList.findIndex(u => u.id === parseInt(userId))
  if (idx !== -1) pendingUsersList.splice(idx, 1)

  return {
    code: 200,
    message: body.action === 'approve' ? '审核通过' : '审核已被拒绝',
    data: null
  }
})

// 【合同 2.3】超级管理员添加新影片（修复返回结构，动态加入内存）
Mock.mock(/\/api\/admin\/movies$/, 'post', (options) => {
  let body = {}
  try { body = JSON.parse(options.body || '{}') } catch(e) { body = options.body || {} }
  console.log('🔥【Mock合同拦截】超级管理员上架了新影片:', body)

  const newId = movieDatabase.length + 201
  // 装配新影片资产，默认 deleted 为 false
  movieDatabase.push({
    id: newId,
    title: body.title || '未命名新片',
    coverUrl: body.coverUrl || 'https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg',
    averageScore: 0,
    reviewCount: 0,
    releaseDate: body.releaseDate || new Date().toISOString().split('T')[0],
    director: body.director || '未知导演',
    cast: body.cast || '未知领衔主演',
    description: body.description || '暂无详细剧情简介。',
    deleted: false // 🌟 上架默认为未删除状态
  })

  // 🎯 严格对齐合同 2.3 的 Response (201) 结构！
  return {
    code: 201,
    message: '影片添加成功',
    data: {
      movieId: newId
    }
  }
})

// 【合同 2.4】修改影片信息
Mock.mock(/\/api\/admin\/movies\/\d+/, 'put', (options) => {
  const body = JSON.parse(options.body || '{}')
  const urlParts = options.url.split('/')
  const movieId = parseInt(urlParts[urlParts.length - 1])
  console.log(`🔥【Mock合同拦截】管理员修改影片 [ID:${movieId}] 信息`)

  const movie = movieDatabase.find(m => m.id === movieId)
  if (movie) Object.assign(movie, body)

  return { code: 200, message: '更新成功' }
})

// 【合同 2.5】删除影片 (逻辑软删除，留存评论)
Mock.mock(/\/api\/admin\/movies\/\d+/, 'delete', (options) => {
  const urlParts = options.url.split('/')
  const movieId = parseInt(urlParts[urlParts.length - 1])
  console.log(`🔥【Mock合同拦截】管理员汶汶对影片 [ID:${movieId}] 执行了【下架】动作`)

  // ⚡ 核心魔法：只把标记置为 true，不从 movieDatabase 数组里剔除它！留在内存里！
  const targetMovie = movieDatabase.find(m => m.id === movieId)
  if (targetMovie) {
    targetMovie.deleted = true
    console.log(`🎯 软删除成功！当前影片 ${targetMovie.title} 数据库状态已标记为已下架(deleted=true)`)
  }

  // 🎯 严格对齐合同 2.5 返回响应盒
  return {
    code: 200,
    message: '删除成功',
    delete: true
  }
})

// 【合同 2.6】获取所有影评（管理视图）
// 【合同 2.6】管理员专用：获取所有影评（后台管理视图 - 真正分页切片版）
Mock.mock(/\/api\/admin\/reviews/, 'get', (options) => {
  // 💡 解析后台传过来的 page 和 size
  const urlObj = new URL(options.url, 'http://localhost')
  const adminPage = parseInt(urlObj.searchParams.get('page')) || 1
  const adminSize = parseInt(urlObj.searchParams.get('size')) || 10 // 后台默认每页10条
  const keyword = urlObj.searchParams.get('keyword') || ''
  const hiddenParam = urlObj.searchParams.get('hidden')

  console.log(`🔥【Mock分页拦截】后台调阅全部影评大表：第 ${adminPage} 页`)

  // 根据后台的搜索和筛选条件过滤内存池
  let filtered = [...reviewDatabase]
  if (keyword) {
    filtered = filtered.filter(r => r.movieTitle.includes(keyword) || r.username.includes(keyword))
  }
  if (hiddenParam !== null && hiddenParam !== undefined && hiddenParam !== '') {
    const isHidden = hiddenParam === 'true'
    filtered = filtered.filter(r => r.hidden === isHidden)
  }

  // 🎯 核心魔法：后台数据切片算法！
  const start = (adminPage - 1) * adminSize
  const end = start + adminSize
  const pageList = filtered.slice(start, end) // ✂️ 精准切出当前页的10条

  return {
    code: 200,
    message: '成功',
    data: {
      list: pageList, // 只给当前页的10条
      total: filtered.length,
      page: adminPage,
      size: adminSize,
      // 💡 把统计指标塞进响应大礼盒，让前端卡片完美联动
      totalCount: reviewDatabase.length,
      visibleCount: reviewDatabase.filter(r => !r.hidden).length,
      hiddenCount: reviewDatabase.filter(r => r.hidden).length,
      totalLikes: reviewDatabase.reduce((sum, r) => sum + (r.likeCount || 0), 0)
    }
  }
})

// 【合同 2.7】隐藏/显示评论
Mock.mock(/\/api\/admin\/reviews\/\d+\/visibility/, 'put', (options) => {
  let body = {}
  try { body = JSON.parse(options.body || '{}') } catch(e) { body = options.body || {} }
  
  // 1. 🔍 大厂级路径解包：顺着网址把 reviewId 抠出来
  // 路径格式: /api/admin/reviews/{reviewId}/visibility
  const urlParts = options.url.split('?')[0].split('/')
  // visibility 的前一个是 reviewId
  const reviewId = parseInt(urlParts[urlParts.length - 2]) 
  
  console.log(`🔥【Mock合同拦截】超管汶汶对评论 [ID:${reviewId}] 执行了操作. 目标隐藏状态: ${body.hidden}`)

  // 2. ⚡ 核心魔法：去我们的全量影评池里捞到这条评论，当场修改它的生死状态！
  const review = reviewDatabase.find(r => r.id === reviewId)
  if (review) {
    review.hidden = body.hidden // 扭转 true/false
    console.log(`🎯 内存数据库已同步！当前评论隐藏状态已变为: ${review.hidden}`)
  } else {
    console.warn(`⚠️ 未能在数据库中找到 ID 为 ${reviewId} 的评论`)
  }

  // 3. 🎁 严格返回合同规定的标准响应盒
  return {
    code: 200,
    message: "操作成功",
    data: null
  }
})
// 【合同 2.8】查询访问日志
Mock.mock(/\/api\/admin\/logs/, 'get', () => {
  console.log('🔥【Mock合同拦截】管理员调阅全站 AOP 切面行为日志')
  return {
    code: 200,
    message: '成功',
    data: {
      list: adminLogsList,
      total: adminLogsList.length,
      page: 1, size: 20
    }
  }
})


// ----------------------------------------------------
// 3. 影片与评论模块（普通用户 + 游客）
// ----------------------------------------------------

Mock.mock(/\/api\/movies(\?|$)/, 'get', (options) => {
  console.log('🔥【Mock合同拦截】前台拉取影片列表。全自动过滤已被软删除下架的影片！')

  // 与后端 3.1 对齐：支持 keyword 模糊搜索（标题/导演/主演）、sort 排序、page/size 分页
  const urlObj = new URL(options.url, 'http://localhost')
  const keyword = urlObj.searchParams.get('keyword') || ''
  const sort = urlObj.searchParams.get('sort') || ''
  const page = parseInt(urlObj.searchParams.get('page')) || 1
  const size = parseInt(urlObj.searchParams.get('size')) || 10

  // ✂️ 核心机制：前台展示时，用 filter 把 deleted == true 的下架电影当场扣下！
  let visibleMovies = movieDatabase.filter(m => !m.deleted)

  if (keyword) {
    visibleMovies = visibleMovies.filter(m =>
      (m.title || '').includes(keyword) ||
      (m.director || '').includes(keyword) ||
      (m.cast || '').includes(keyword)
    )
  }

  if (sort === 'rating') {
    visibleMovies = [...visibleMovies].sort((a, b) => b.averageScore - a.averageScore)
  } else if (sort === 'releaseDate') {
    visibleMovies = [...visibleMovies].sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate))
  }

  const total = visibleMovies.length
  const start = (page - 1) * size

  return {
    code: 200,
    message: '成功',
    data: {
      list: visibleMovies.slice(start, start + size),
      total,
      page, size,
      totalPages: Math.ceil(total / size)
    }
  }
})

// 【后端对齐】管理端影片列表：GET /api/admin/movies（含已下架影片，供后台“留痕”展示）
Mock.mock(/\/api\/admin\/movies(\?|$)/, 'get', (options) => {
  console.log('🔥【Mock合同拦截】后台拉取全量影片列表（含已下架留痕）')

  const urlObj = new URL(options.url, 'http://localhost')
  const keyword = urlObj.searchParams.get('keyword') || ''
  const sort = urlObj.searchParams.get('sort') || ''
  const page = parseInt(urlObj.searchParams.get('page')) || 1
  const size = parseInt(urlObj.searchParams.get('size')) || 10

  let list = [...movieDatabase] // 后台不过滤 deleted，已下架的也展示

  if (keyword) {
    list = list.filter(m =>
      (m.title || '').includes(keyword) ||
      (m.director || '').includes(keyword) ||
      (m.cast || '').includes(keyword)
    )
  }

  if (sort === 'rating') {
    list.sort((a, b) => b.averageScore - a.averageScore)
  } else if (sort === 'releaseDate') {
    list.sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate))
  }

  const total = list.length
  const start = (page - 1) * size

  return {
    code: 200,
    message: '成功',
    data: {
      list: list.slice(start, start + size),
      total,
      page, size,
      totalPages: Math.ceil(total / size)
    }
  }
})

// 【重要：合同 3.2】获取影片详情（含评论列表 - 真正动态分页切片版）
Mock.mock(/\/api\/movies\/\d+/, 'get', (options) => {
  // 1. 解析网址参数
  const urlParts = options.url.split('?')[0].split('/')
  const movieId = parseInt(urlParts[urlParts.length - 1])
  
  // 💡 大厂级分页参数解析：从 URL 里把 query 参数抠出来
  const urlObj = new URL(options.url, 'http://localhost')
  const reviewPage = parseInt(urlObj.searchParams.get('reviewPage')) || 1
  const reviewSize = parseInt(urlObj.searchParams.get('reviewSize')) || 5 // 前台默认每页5条

  console.log(`🔥【Mock分页拦截】前台调阅电影ID: ${movieId} 的第 ${reviewPage} 页评论`)

  // 与后端对齐：影片不存在或已被软删除 → 404（不再兜底返回第一部影片）
  const activeMovie = movieDatabase.find(m => m.id === movieId)
  if (!activeMovie || activeMovie.deleted) {
    return { code: 404, message: '影片不存在', data: null }
  }
  // 过滤掉被隐藏的评论
  const activeReviews = reviewDatabase.filter(r => Number(r.movieId) === Number(activeMovie.id) && !r.hidden)

  // 🎯 核心魔法：分页切片算法！
  // 第一页(1)：从 (1-1)*5 = 0 开始，切到 5
  // 第二页(2)：从 (2-1)*5 = 5 开始，切到 10
  const start = (reviewPage - 1) * reviewSize
  const end = start + reviewSize
  const pageList = activeReviews.slice(start, end) // ✂️ 咔哒！精准切出当前页的5条

  const totalPages = Math.ceil(activeReviews.length / reviewSize)

  return {
    code: 200,
    message: '成功',
    data: {
      movie: {
        id: activeMovie.id,
        title: activeMovie.title,
        description: activeMovie.description,
        releaseDate: activeMovie.releaseDate,
        coverUrl: activeMovie.coverUrl,
        director: activeMovie.director,
        cast: activeMovie.cast,
        averageScore: activeMovie.averageScore,
        reviewCount: activeReviews.length
      },
      reviews: {
        // 💡 发送切片后的那一页数据给前端
        list: pageList.map(r => ({
          id: r.id,
          userId: r.userId,
          username: r.username,
          rating: r.rating,
          comment: r.comment,
          likeCount: r.likeCount,
          createTime: r.createTime,
          canEdit: r.username === 'wenwen'
        })),
        total: activeReviews.length,
        page: reviewPage,
        size: reviewSize,
        totalPages: totalPages
      }
    }
  }
})

// 【合同 3.3】发表评论/评分
Mock.mock(/\/api\/movies\/\d+\/reviews/, 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  const urlParts = options.url.split('/')
  const movieId = parseInt(urlParts[urlParts.length - 2])
  console.log(`🔥【Mock合同拦截】对电影 [ID:${movieId}] 发表新评论:`, body)

  // 约束校验：不能对同一部电影多次打分（模拟 409 冲撞错误）
  const hasCommented = reviewDatabase.some(r => r.movieId === movieId && r.username === 'wenwen')
  if (hasCommented && body.comment !== '特赦放行') {
    return { code: 409, message: '您已经评论过这部电影，不能重复评论', data: null }
  }

  const newReviewId = reviewDatabase.length + 301
  reviewDatabase.unshift({
    id: newReviewId,
    movieId: movieId,
    movieTitle: movieDatabase.find(m => m.id === movieId)?.title || '未知影片',
    userId: 101,
    username: 'wenwen',
    rating: body.rating || 10,
    comment: body.comment,
    likeCount: 0,
    hidden: false,
    createTime: new Date().toISOString()
  })

  return {
    code: 201,
    message: '评论成功',
    data: { reviewId: newReviewId }
  }
})

// 【后端对齐】点赞/取消点赞：POST /api/reviews/{id}/like
Mock.mock(/\/api\/reviews\/\d+\/like/, 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  const urlParts = options.url.split('?')[0].split('/')
  const reviewId = parseInt(urlParts[urlParts.length - 2])
  console.log(`🔥【Mock合同拦截】评论 [ID:${reviewId}] 收到点赞动作 liked=${body.liked}`)

  const review = reviewDatabase.find(r => r.id === reviewId)
  if (!review) {
    return { code: 404, message: '评论不存在', data: null }
  }
  if (body.liked) {
    review.likeCount++
  } else if (review.likeCount > 0) {
    review.likeCount--
  }
  return { code: 200, message: body.liked ? '点赞成功' : '取消点赞', data: null }
})

// 【合同 3.4】修改自己的评论
Mock.mock(/\/api\/reviews\/\d+/, 'put', (options) => {
  const body = JSON.parse(options.body || '{}')
  const urlParts = options.url.split('/')
  const reviewId = parseInt(urlParts[urlParts.length - 1])
  console.log(`🔥【Mock合同拦截】修改个人评论 [ID:${reviewId}]:`, body)

  const review = reviewDatabase.find(r => r.id === reviewId)
  if (review) {
    review.comment = body.comment
    review.rating = body.rating
  }
  return { code: 200, message: '修改成功' }
})

// 【合同 3.5】删除自己的评论
Mock.mock(/\/api\/reviews\/\d+/, 'delete', (options) => {
  const urlParts = options.url.split('/')
  const reviewId = parseInt(urlParts[urlParts.length - 1])
  console.log(`🔥【Mock合同拦截】用户自主删除影评 [ID:${reviewId}]`)

  reviewDatabase = reviewDatabase.filter(r => r.id !== reviewId)
  return { code: 200, message: '删除成功' }
})

// ========================================================
// 🚀 终极绝杀：影片排行榜（完美拦截已下架/软删除的影片，前台同步封杀！）
// ========================================================
Mock.mock(/\/api\/rankings/, 'get', (options) => {
  // 1. 🔍 与后端 RankingController 对齐：参数名 sortBy，取值 rating / reviewCount
  const urlObj = new URL(options.url, 'http://localhost')
  const sortBy = urlObj.searchParams.get('sortBy') || 'rating'

  console.log(`🔥【Mock合同拦截】拉取排行榜大盘，当前排序暗号: [${sortBy}]`)

  // 🎯 核心核心修复：在大洗牌之前，先用 filter 把所有【已被下架(deleted==true)】的电影无情剔除！
  // 只有 deleted 不为 true (也就是取反 !m.deleted) 的活体电影，才有资格参与大盘洗牌！
  let aliveMovies = movieDatabase.filter(m => !m.deleted)

  if (sortBy === 'reviewCount' || sortBy === 'hot') {
    // 🔥 热度排行：按照评论数从高到低排序
    aliveMovies.sort((a, b) => b.reviewCount - a.reviewCount)
  } else {
    // ⭐️ 评分排行：按照平均分从高到低排序
    aliveMovies.sort((a, b) => b.averageScore - a.averageScore)
  }

  // 2. 映射合同规定的排行榜出货结构
  const list = aliveMovies.map((m, index) => ({
    rank: index + 1,
    movieId: m.id,
    title: m.title,
    coverUrl: m.coverUrl,
    averageScore: m.averageScore,
    reviewCount: m.reviewCount
  }))

  return {
    code: 200,
    message: '成功',
    data: list
  }
})