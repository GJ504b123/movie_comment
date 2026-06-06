/**
 * ==========================================
 * 🎬 全栈影评系统 - Mock API 拦截规则配置
 * ==========================================
 */

// 内存中的用户数据库
const userDatabase = {
  '101': { id: 101, username: 'wenwen', password: '123456', email: 'wenwen@example.com', role: 'admin', status: 'approved', createTime: '2024-01-15T10:30:00Z' },
  '102': { id: 102, username: 'xiaoming', password: '123456', email: 'xiaoming@example.com', role: 'user', status: 'approved', createTime: '2024-02-20T14:20:00Z' },
  '103': { id: 103, username: 'testuser', password: '123456', email: 'test@example.com', role: 'user', status: 'pending', createTime: '2024-03-10T09:15:00Z' },
  '105': { id: 105, username: 'newuser1', password: '123456', email: 'newuser1@example.com', role: 'user', status: 'pending', createTime: '2024-03-14T10:00:00Z' },
}

let nextUserId = 106

// 影片列表数据
const movieList = [
  { id: 201, title: '肖申克的救赎', averageScore: 9.2, coverUrl: 'https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg', reviewCount: 128, releaseDate: '1994-09-23' },
  { id: 202, title: '霸王别姬', averageScore: 9.6, coverUrl: 'https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg', reviewCount: 95, releaseDate: '1993-01-01' },
  { id: 203, title: '星际穿越', averageScore: 9.4, coverUrl: 'https://images.pexels.com/photos/15209918/pexels-photo-15209918.png', reviewCount: 44, releaseDate: '2014-11-07' },
]

// 影片详情数据
const movieDetails = {
  '201': {
    id: 201, title: '肖申克的救赎', director: '弗兰克·德拉邦特', cast: '蒂姆·罗宾斯, 摩根·弗里曼',
    averageScore: 9.2, coverUrl: 'https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg',
    releaseDate: '1994-09-23', description: '一场冤狱带来的希望与救赎，一个关于自由与坚持的感人故事。',
    reviewCount: 128, reviews: {
      list: [
        { id: 301, userId: 102, username: 'xiaoming', rating: 9, comment: '经典中的经典，每个镜头都值得回味。', likeCount: 42, createTime: '2024-03-15T14:30:00Z', canEdit: false },
        { id: 302, userId: 101, username: 'wenwen', rating: 10, comment: '看过最好的电影，没有之一。', likeCount: 38, createTime: '2024-03-10T10:20:00Z', canEdit: false },
      ],
      total: 2, page: 1, size: 5, totalPages: 1
    }
  },
  '202': {
    id: 202, title: '霸王别姬', director: '陈凯歌', cast: '张国荣, 张丰毅, 巩俐',
    averageScore: 9.6, coverUrl: 'https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg',
    releaseDate: '1993-01-01', description: '不疯魔不成活，程蝶衣与段小楼的传奇人生。',
    reviewCount: 95, reviews: {
      list: [
        { id: 303, userId: 102, username: 'xiaoming', rating: 10, comment: '史诗级的作品，张国荣的表演无可挑剔。', likeCount: 56, createTime: '2024-03-12T16:45:00Z', canEdit: false },
      ],
      total: 1, page: 1, size: 5, totalPages: 1
    }
  },
  '203': {
    id: 203, title: '星际穿越', director: '克里斯托弗·诺兰', cast: '马修·麦康纳, 安妮·海瑟薇',
    averageScore: 9.4, coverUrl: 'https://images.pexels.com/photos/15209918/pexels-photo-15209918.png',
    releaseDate: '2014-11-07', description: '穿越时空的爱与亲情，探索宇宙的终极奥秘。',
    reviewCount: 44, reviews: {
      list: [
        { id: 304, userId: 101, username: 'wenwen', rating: 9, comment: '科学与情感的完美结合。', likeCount: 28, createTime: '2024-03-08T11:10:00Z', canEdit: false },
      ],
      total: 1, page: 1, size: 5, totalPages: 1
    }
  }
}

// 排行榜数据
const rankings = [
  { id: 202, title: '霸王别姬', averageScore: 9.6, coverUrl: 'https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg', reviewCount: 95, releaseDate: '1993-01-01' },
  { id: 203, title: '星际穿越', averageScore: 9.4, coverUrl: 'https://images.pexels.com/photos/15209918/pexels-photo-15209918.png', reviewCount: 44, releaseDate: '2014-11-07' },
  { id: 201, title: '肖申克的救赎', averageScore: 9.2, coverUrl: 'https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg', reviewCount: 128, releaseDate: '1994-09-23' },
]

// 管理员日志数据
const adminLogs = {
  list: [
    { id: 1, userId: 101, username: 'wenwen', action: 'login', targetId: null, ip: '127.0.0.1', createTime: '2024-03-15T08:30:00Z' },
    { id: 2, userId: 103, username: 'testuser', action: 'register', targetId: null, ip: '192.168.1.100', createTime: '2024-03-15T09:15:00Z' },
    { id: 3, userId: 101, username: 'wenwen', action: 'audit_user', targetId: 103, ip: '127.0.0.1', createTime: '2024-03-15T10:00:00Z' },
    { id: 4, userId: 102, username: 'xiaoming', action: 'view_movie_detail', targetId: 201, ip: '192.168.1.101', createTime: '2024-03-15T11:30:00Z' },
    { id: 5, userId: 102, username: 'xiaoming', action: 'post_review', targetId: 201, ip: '192.168.1.101', createTime: '2024-03-15T11:45:00Z' },
    { id: 6, userId: 101, username: 'wenwen', action: 'view_ranking', targetId: null, ip: '127.0.0.1', createTime: '2024-03-15T12:00:00Z' },
    { id: 7, userId: 102, username: 'xiaoming', action: 'search_movie', targetId: null, ip: '192.168.1.101', createTime: '2024-03-15T14:20:00Z' },
    { id: 8, userId: 101, username: 'wenwen', action: 'add_movie', targetId: 204, ip: '127.0.0.1', createTime: '2024-03-15T15:00:00Z' },
    { id: 9, userId: 105, username: 'moviefan', action: 'register', targetId: null, ip: '192.168.1.105', createTime: '2024-03-15T15:30:00Z' },
    { id: 10, userId: 106, username: 'filmlover', action: 'register', targetId: null, ip: '192.168.1.106', createTime: '2024-03-15T16:00:00Z' },
    { id: 11, userId: 101, username: 'wenwen', action: 'audit_user', targetId: 105, ip: '127.0.0.1', createTime: '2024-03-15T16:30:00Z' },
    { id: 12, userId: 105, username: 'moviefan', action: 'login', targetId: null, ip: '192.168.1.105', createTime: '2024-03-15T17:00:00Z' },
    { id: 13, userId: 105, username: 'moviefan', action: 'view_movie_detail', targetId: 202, ip: '192.168.1.105', createTime: '2024-03-15T17:15:00Z' },
    { id: 14, userId: 105, username: 'moviefan', action: 'post_review', targetId: 202, ip: '192.168.1.105', createTime: '2024-03-15T17:30:00Z' },
    { id: 15, userId: 101, username: 'wenwen', action: 'edit_movie', targetId: 201, ip: '127.0.0.1', createTime: '2024-03-15T18:00:00Z' },
    { id: 16, userId: 107, username: 'critic', action: 'register', targetId: null, ip: '192.168.1.107', createTime: '2024-03-15T18:30:00Z' },
    { id: 17, userId: 101, username: 'wenwen', action: 'view_reviews', targetId: null, ip: '127.0.0.1', createTime: '2024-03-15T19:00:00Z' },
    { id: 18, userId: 101, username: 'wenwen', action: 'hide_review', targetId: 311, ip: '127.0.0.1', createTime: '2024-03-15T19:15:00Z' },
    { id: 19, userId: 102, username: 'xiaoming', action: 'view_ranking', targetId: null, ip: '192.168.1.101', createTime: '2024-03-15T20:00:00Z' },
    { id: 20, userId: 101, username: 'wenwen', action: 'delete_movie', targetId: 205, ip: '127.0.0.1', createTime: '2024-03-15T21:00:00Z' },
    { id: 21, userId: 108, username: 'classicfan', action: 'login', targetId: null, ip: '192.168.1.108', createTime: '2024-03-16T08:00:00Z' },
    { id: 22, userId: 108, username: 'classicfan', action: 'view_movie_detail', targetId: 203, ip: '192.168.1.108', createTime: '2024-03-16T08:30:00Z' },
    { id: 23, userId: 101, username: 'wenwen', action: 'login', targetId: null, ip: '127.0.0.1', createTime: '2024-03-16T09:00:00Z' },
    { id: 24, userId: 101, username: 'wenwen', action: 'view_logs', targetId: null, ip: '127.0.0.1', createTime: '2024-03-16T09:30:00Z' },
    { id: 25, userId: 109, username: 'sci-fi', action: 'register', targetId: null, ip: '192.168.1.109', createTime: '2024-03-16T10:00:00Z' },
  ],
  total: 25, page: 1, size: 20
}

// 待审核用户列表
const pendingUsers = [
  { id: 105, username: 'newuser1', email: 'newuser1@example.com', createTime: '2024-03-14T10:00:00Z' },
]

// 所有评论列表
let allReviews = [
  { id: 301, movieId: 201, movieTitle: '肖申克的救赎', userId: 102, username: 'xiaoming', rating: 9, comment: '经典中的经典，每个镜头都值得回味。', likeCount: 42, hidden: false, createTime: '2024-03-15T14:30:00Z' },
  { id: 302, movieId: 201, movieTitle: '肖申克的救赎', userId: 101, username: 'wenwen', rating: 10, comment: '看过最好的电影，没有之一。', likeCount: 38, hidden: false, createTime: '2024-03-10T10:20:00Z' },
  { id: 303, movieId: 202, movieTitle: '霸王别姬', userId: 102, username: 'xiaoming', rating: 10, comment: '史诗级的作品，张国荣的表演无可挑剔。', likeCount: 56, hidden: false, createTime: '2024-03-12T16:45:00Z' },
  { id: 304, movieId: 203, movieTitle: '星际穿越', userId: 101, username: 'wenwen', rating: 9, comment: '科学与情感的完美结合。', likeCount: 28, hidden: false, createTime: '2024-03-08T11:10:00Z' },
  { id: 305, movieId: 201, movieTitle: '肖申克的救赎', userId: 105, username: 'moviefan', rating: 9, comment: '每次看都有新的感悟，Tim Robbins的演技太棒了！', likeCount: 32, hidden: false, createTime: '2024-03-14T09:20:00Z' },
  { id: 306, movieId: 202, movieTitle: '霸王别姬', userId: 106, username: 'filmlover', rating: 10, comment: '不疯魔不成活，这才是真正的艺术！', likeCount: 48, hidden: false, createTime: '2024-03-13T15:30:00Z' },
  { id: 307, movieId: 203, movieTitle: '星际穿越', userId: 102, username: 'xiaoming', rating: 8, comment: '视觉效果震撼，但剧情有些冗长。', likeCount: 15, hidden: false, createTime: '2024-03-11T12:15:00Z' },
  { id: 308, movieId: 201, movieTitle: '肖申克的救赎', userId: 107, username: 'critic', rating: 10, comment: '希望是美好的事物，也许是人间至善。', likeCount: 55, hidden: false, createTime: '2024-03-09T18:40:00Z' },
  { id: 309, movieId: 202, movieTitle: '霸王别姬', userId: 108, username: 'classicfan', rating: 9, comment: '中国电影的巅峰之作，无法超越。', likeCount: 41, hidden: false, createTime: '2024-03-07T10:00:00Z' },
  { id: 310, movieId: 203, movieTitle: '星际穿越', userId: 109, username: 'sci-fi', rating: 10, comment: '诺兰神作！科学与人性的完美结合。', likeCount: 63, hidden: false, createTime: '2024-03-06T20:30:00Z' },
  { id: 311, movieId: 201, movieTitle: '肖申克的救赎', userId: 110, username: 'moviebuff', rating: 9, comment: '安迪的坚持让我相信希望永远存在。', likeCount: 28, hidden: true, createTime: '2024-03-05T14:20:00Z' },
  { id: 312, movieId: 202, movieTitle: '霸王别姬', userId: 111, username: 'dramaqueen', rating: 10, comment: '张国荣之后，再无程蝶衣。', likeCount: 72, hidden: false, createTime: '2024-03-04T11:10:00Z' },
  { id: 313, movieId: 203, movieTitle: '星际穿越', userId: 112, username: 'spacegeek', rating: 9, comment: '黑洞场景太震撼了，值得在IMAX观看！', likeCount: 35, hidden: false, createTime: '2024-03-03T16:45:00Z' },
  { id: 314, movieId: 201, movieTitle: '肖申克的救赎', userId: 113, username: 'hope', rating: 10, comment: '这是一部关于希望和自由的史诗。', likeCount: 49, hidden: false, createTime: '2024-03-02T09:00:00Z' },
  { id: 315, movieId: 202, movieTitle: '霸王别姬', userId: 114, username: 'artlover', rating: 9, comment: '从一而终，四个字道尽人生。', likeCount: 36, hidden: true, createTime: '2024-03-01T13:25:00Z' },
  { id: 316, movieId: 203, movieTitle: '星际穿越', userId: 115, username: 'emotion', rating: 8, comment: '父女情让人泪目，但科学设定有些硬伤。', likeCount: 22, hidden: false, createTime: '2024-02-28T17:30:00Z' },
]

export default [
  // ==========================================
  // 【认证模块】
  // ==========================================
  
  // 用户登录
  {
    url: '/api/auth/login',
    method: 'post',
    response: ({ body }) => {
      const user = Object.values(userDatabase).find(u => u.username === body.username)
      
      if (!user) {
        return { code: 401, message: '用户名或密码错误', data: null }
      }
      
      if (user.password !== body.password) {
        return { code: 401, message: '用户名或密码错误', data: null }
      }
      
      if (user.status === 'pending') {
        return { code: 403, message: '您的账号正在等待审核，请耐心等待管理员审核通过', data: null }
      }
      
      if (user.status === 'rejected') {
        return { code: 403, message: '您的账号审核未通过，管理员已拒绝您的申请，请重新注册（可能是用户名重复或其他原因）', data: null }
      }
      
      return {
        code: 200,
        message: '登录成功',
        data: {
          token: 'mock_token_' + user.id,
          user: {
            id: user.id,
            username: user.username,
            role: user.role,
            status: user.status
          }
        }
      }
    }
  },
  
  // 用户注册
  {
    url: '/api/auth/register',
    method: 'post',
    response: ({ body }) => {
      if (!body.username || !body.email || !body.password) {
        return { code: 400, message: '参数不完整', data: null }
      }
      
      const exists = Object.values(userDatabase).find(u => u.username === body.username)
      if (exists) {
        return { code: 409, message: '用户名已被注册', data: null }
      }
      
      const newUser = {
        id: nextUserId,
        username: body.username,
        password: body.password,
        email: body.email,
        role: 'user',
        status: 'pending',
        createTime: new Date().toISOString()
      }
      
      userDatabase[nextUserId.toString()] = newUser
      pendingUsers.push({ id: nextUserId, username: newUser.username, email: newUser.email, createTime: newUser.createTime })
      nextUserId++
      
      return {
        code: 201,
        message: '注册成功，等待管理员审核',
        data: { userId: newUser.id, status: 'pending' }
      }
    }
  },
  
  // ==========================================
  // 【影片模块】
  // ==========================================
  
  // 查询影片列表
  {
    url: '/api/movies',
    method: 'get',
    response: ({ query }) => {
      const page = parseInt(query.page) || 1
      const size = parseInt(query.size) || 10
      const keyword = query.keyword || ''
      const sortBy = query.sortBy || 'rating'
      
      let list = [...movieList]
      if (keyword) {
        list = list.filter(m => m.title.includes(keyword))
      }
      
      if (sortBy === 'rating') {
        list.sort((a, b) => b.averageScore - a.averageScore)
      } else if (sortBy === 'releaseDate') {
        list.sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate))
      }
      
      const total = list.length
      const start = (page - 1) * size
      const end = start + size
      
      return {
        code: 200,
        message: '成功',
        data: {
          list: list.slice(start, end),
          total,
          page,
          size,
          totalPages: Math.ceil(total / size)
        }
      }
    }
  },
  
  // 获取排行榜（必须在 /api/movies/:id 之前，否则 ranking 会被当作 id 匹配）
  {
    url: '/api/movies/ranking',
    method: 'get',
    response: () => ({
      code: 200,
      message: '成功',
      data: rankings
    })
  },

  // 获取影片详情
  {
    url: '/api/movies/:id',
    method: 'get',
    response: (req) => {
      const id = req.params?.id || req.url?.split('?')[0].split('/').filter(Boolean).pop()
      const movie = movieDetails[id]
      if (!movie) {
        return { code: 404, message: '影片不存在', data: null }
      }
      return { code: 200, message: '成功', data: movie }
    }
  },
  
  // 添加影片
  {
    url: '/api/movies',
    method: 'post',
    response: (req) => {
      const body = req.body || {}
      const newId = Date.now()
      
      // 添加到影片列表
      const newMovie = {
        id: newId,
        title: body.title,
        averageScore: 0,
        coverUrl: body.coverUrl,
        reviewCount: 0,
        releaseDate: body.releaseDate
      }
      movieList.push(newMovie)
      
      // 添加到影片详情
      movieDetails[newId.toString()] = {
        id: newId,
        title: body.title,
        director: body.director || '',
        cast: body.cast || '',
        averageScore: 0,
        coverUrl: body.coverUrl,
        releaseDate: body.releaseDate,
        description: body.description || '',
        reviewCount: 0,
        reviews: {
          list: [],
          total: 0,
          page: 1,
          size: 5,
          totalPages: 0
        }
      }
      
      // 添加日志记录
      adminLogs.list.unshift({
        id: adminLogs.list.length + 1,
        userId: 101,
        username: 'admin',
        action: 'add_movie',
        targetId: newId,
        ip: '127.0.0.1',
        createTime: new Date().toISOString()
      })
      adminLogs.total = adminLogs.list.length
      
      return {
        code: 201,
        message: '影片添加成功',
        data: { id: newId, ...body }
      }
    }
  },
  
  // 更新影片
  {
    url: '/api/movies/:id',
    method: 'put',
    response: (req) => {
      const id = parseInt(req.params?.id || req.url?.split('?')[0].split('/').filter(Boolean).pop())
      const body = req.body || {}
      
      // 更新影片列表
      const movieIndex = movieList.findIndex(m => m.id === id)
      if (movieIndex !== -1) {
        movieList[movieIndex] = {
          ...movieList[movieIndex],
          title: body.title,
          coverUrl: body.coverUrl,
          releaseDate: body.releaseDate
        }
      }
      
      // 更新影片详情
      if (movieDetails[id.toString()]) {
        movieDetails[id.toString()] = {
          ...movieDetails[id.toString()],
          title: body.title,
          director: body.director || '',
          cast: body.cast || '',
          coverUrl: body.coverUrl,
          releaseDate: body.releaseDate,
          description: body.description || ''
        }
      }
      
      // 添加日志记录
      adminLogs.list.unshift({
        id: adminLogs.list.length + 1,
        userId: 101,
        username: 'admin',
        action: 'edit_movie',
        targetId: id,
        ip: '127.0.0.1',
        createTime: new Date().toISOString()
      })
      adminLogs.total = adminLogs.list.length
      
      return {
        code: 200,
        message: '影片更新成功',
        data: { id, ...body }
      }
    }
  },
  
  // 删除影片
  {
    url: '/api/movies/:id',
    method: 'delete',
    response: (req) => {
      const movieId = parseInt(req.params?.id || req.url?.split('?')[0].split('/').filter(Boolean).pop())
      
      // 删除影片列表中的影片
      const movieIndex = movieList.findIndex(m => m.id === movieId)
      if (movieIndex !== -1) {
        movieList.splice(movieIndex, 1)
      }
      
      // 删除影片详情
      delete movieDetails[movieId.toString()]
      
      // 删除相关评论
      const initialReviewCount = allReviews.length
      allReviews = allReviews.filter(r => r.movieId !== movieId)
      const deletedReviews = initialReviewCount - allReviews.length
      
      // 删除相关日志（查看详情、发表评论等操作）
      const initialLogCount = adminLogs.list.length
      adminLogs.list = adminLogs.list.filter(l => !(l.targetId === movieId && 
        (l.action === 'view_movie_detail' || l.action === 'post_review' || 
         l.action === 'add_movie' || l.action === 'edit_movie' || l.action === 'delete_movie')))
      const deletedLogs = initialLogCount - adminLogs.list.length
      adminLogs.total = adminLogs.list.length
      
      return { 
        code: 200, 
        message: `删除成功，同时删除了 ${deletedReviews} 条评论和 ${deletedLogs} 条相关日志`, 
        data: null 
      }
    }
  },
  
  // ==========================================
  // 【评论模块】
  // ==========================================
  
  // 发表评论
  {
    url: '/api/reviews',
    method: 'post',
    response: ({ body }) => ({
      code: 201,
      message: '评论发表成功',
      data: { id: Date.now(), ...body, likeCount: 0, createTime: new Date().toISOString() }
    })
  },
  
  // 获取评论列表
  {
    url: '/api/reviews',
    method: 'get',
    response: ({ query }) => {
      const movieId = query.movieId
      if (movieId && movieDetails[movieId]) {
        return { code: 200, message: '成功', data: movieDetails[movieId].reviews }
      }
      return { code: 200, message: '成功', data: { list: [], total: 0, page: 1, size: 5, totalPages: 0 } }
    }
  },
  
  // 删除评论
  {
    url: '/api/reviews/:id',
    method: 'delete',
    response: () => ({ code: 200, message: '删除成功', data: null })
  },
  
  // ==========================================
  // 【管理员模块】
  // ==========================================
  
  // 获取待审核用户列表
  {
    url: '/api/admin/users/pending',
    method: 'get',
    response: ({ query }) => {
      const page = parseInt(query.page) || 1
      const size = parseInt(query.size) || 10
      const total = pendingUsers.length
      const start = (page - 1) * size
      const end = start + size
      
      return {
        code: 200,
        message: '成功',
        data: { list: pendingUsers.slice(start, end), total, page, size, totalPages: Math.ceil(total / size) }
      }
    }
  },
  
  // 审核用户（通过）
  {
    url: '/api/admin/users/:id/approve',
    method: 'put',
    response: (req) => {
      const userId = parseInt(req.params?.id || req.url?.split('?')[0].split('/').filter(Boolean).slice(-2)[0])
      const user = userDatabase[userId.toString()]
      if (!user) {
        return { code: 404, message: '用户不存在', data: null }
      }
      user.status = 'approved'
      const idx = pendingUsers.findIndex(u => u.id === userId)
      if (idx !== -1) pendingUsers.splice(idx, 1)
      return { code: 200, message: '审核通过', data: { userId, status: 'approved' } }
    }
  },
  
  // 审核用户（拒绝）
  {
    url: '/api/admin/users/:id/reject',
    method: 'put',
    response: (req) => {
      const userId = parseInt(req.params?.id || req.url?.split('?')[0].split('/').filter(Boolean).slice(-2)[0])
      const user = userDatabase[userId.toString()]
      if (!user) {
        return { code: 404, message: '用户不存在', data: null }
      }
      user.status = 'rejected'
      const idx = pendingUsers.findIndex(u => u.id === userId)
      if (idx !== -1) pendingUsers.splice(idx, 1)
      return { code: 200, message: '已拒绝该用户', data: { userId, status: 'rejected' } }
    }
  },
  
  // 获取所有评论（管理视图）
  {
    url: '/api/admin/reviews',
    method: 'get',
    response: ({ query }) => {
      const page = parseInt(query.page) || 1
      const size = parseInt(query.size) || 10
      const keyword = query.keyword || ''
      const hidden = query.hidden
      
      let list = [...allReviews]
      if (keyword) {
        list = list.filter(r => r.movieTitle.includes(keyword) || r.username.includes(keyword))
      }
      if (hidden !== undefined) {
        list = list.filter(r => r.hidden === (hidden === 'true'))
      }
      
      const total = list.length
      const start = (page - 1) * size
      const end = start + size
      
      const totalCount = allReviews.length
      const visibleCount = allReviews.filter(r => !r.hidden).length
      const hiddenCount = allReviews.filter(r => r.hidden).length
      const totalLikes = allReviews.reduce((sum, r) => sum + r.likeCount, 0)
      
      return { 
        code: 200, 
        message: '成功', 
        data: { 
          list: list.slice(start, end), 
          total, 
          page, 
          size,
          totalCount,
          visibleCount,
          hiddenCount,
          totalLikes
        } 
      }
    }
  },
  
  // 隐藏/显示评论
  {
    url: '/api/admin/reviews/:id/hide',
    method: 'put',
    response: (req) => {
      const id = parseInt(req.params?.id || req.url?.split('?')[0].split('/').filter(Boolean).slice(-2)[0])
      const review = allReviews.find(r => r.id === id)
      const body = req.body || {}
      if (review) review.hidden = body.hidden
      return { code: 200, message: body.hidden ? '评论已隐藏' : '评论已显示', data: { id, hidden: body.hidden } }
    }
  },
  
  // 获取访问日志
  {
    url: '/api/admin/logs',
    method: 'get',
    response: ({ query }) => {
      const page = parseInt(query.page) || 1
      const size = parseInt(query.size) || 20
      const keyword = query.keyword || ''
      const action = query.action
      
      let list = [...adminLogs.list]
      if (keyword) {
        list = list.filter(l => l.username.includes(keyword) || l.ip.includes(keyword))
      }
      if (action) {
        list = list.filter(l => l.action === action)
      }
      
      const total = list.length
      const start = (page - 1) * size
      const end = start + size
      
      return { code: 200, message: '成功', data: { list: list.slice(start, end), total, page, size } }
    }
  }
]
