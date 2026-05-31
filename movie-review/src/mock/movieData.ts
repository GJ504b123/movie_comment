/**
 * ==========================================
 * 🎬 全栈影评系统 - 严格对齐后端 API 合同的 Mock 数据库
 * ==========================================
 */

// ----------------------------------------------------
// 【合同 3.1】查询影片列表假数据（首页使用）
// ----------------------------------------------------
export const mockMovieList = [
  {
    id: 201,
    title: "肖申克的救赎",
    averageScore: 9.2,
    coverUrl: "https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg",
    reviewCount: 128,
    releaseDate: "1994-09-23"
  },
  {
    id: 202,
    title: "霸王别姬",
    averageScore: 9.6,
    coverUrl: "https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg",
    reviewCount: 95,
    releaseDate: "1993-01-01"
  },
  {
    id: 203,
    title: "星际穿越",
    averageScore: 9.4,
    coverUrl: "https://images.pexels.com/photos/15209918/pexels-photo-15209918.png",
    reviewCount: 44,
    releaseDate: "2014-11-07"
  }
]

// ----------------------------------------------------
// 【合同 3.2】获取影片详情含评论列表假数据（详情页使用）
// ----------------------------------------------------
export const mockMovieDatabase: Record<string, any> = {
  '201': {
    movie: {
      id: 201,
      title: "肖申克的救赎",
      description: "这场谋杀使银行家安迪蒙冤入狱，在长达20年的牢狱生涯中，他用信念和智慧为自己完成了救赎，重获自由。",
      releaseDate: "1994-09-23",
      coverUrl: "https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg",
      director: "弗兰克·德拉邦特",
      cast: "蒂姆·罗宾斯, 摩根·弗里曼",
      averageScore: 9.2,
      reviewCount: 128
    },
    reviews: {
      list: [
        {
          id: 301,
          userId: 101,
          username: "filmfan",
          rating: 10,
          comment: "永远的神作，信念是关不住的鸟儿，它每一片羽毛都闪耀着自由的光辉。",
          likeCount: 32,
          createTime: "2025-03-01T09:00:00Z",
          canEdit: true // 标志当前登录用户本人可修改
        },
        {
          id: 302,
          userId: 105,
          username: "汶汶",
          rating: 9,
          comment: "被安迪在雷雨中张开双臂迎接自由的镜头震撼到了，吹爆这个骨架屏和路由跳转！",
          likeCount: 15,
          createTime: "2026-05-28T11:30:00Z",
          canEdit: false
        }
      ],
      total: 128,
      page: 1,
      size: 5,
      totalPages: 26
    }
  },
  '202': {
    movie: {
      id: 202,
      title: "霸王别姬",
      description: "影片通过两位京剧伶人程蝶衣与段小楼风雨坎坷的坎坷命运，展现了中国半个多世纪的风云变幻以及对传统文化、人性的深度反思。",
      releaseDate: "1993-01-01",
      coverUrl: "https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg",
      director: "陈凯歌",
      cast: "张国荣, 张丰毅, 巩俐",
      averageScore: 9.6,
      reviewCount: 95
    },
    reviews: {
      list: [
        {
          id: 304,
          userId: 108,
          username: "戏迷小张",
          rating: 10,
          comment: "不疯魔不成活。张国荣把程蝶衣那种执着、绝望演得入木三分，绝代风华！",
          likeCount: 99,
          createTime: "2025-02-14T20:15:00Z",
          canEdit: false
        }
      ],
      total: 95,
      page: 1,
      size: 5,
      totalPages: 19
    }
  },
  '203': {
    movie: {
      id: 203,
      title: "星际穿越",
      description: "近未来地球遭遇严重的枯萎病，前飞行员库珀不得不告别年幼的女儿，毅然穿越虫洞深入未知的宇宙，为濒临灭绝的人类寻找新的家园。",
      releaseDate: "2014-11-07",
      coverUrl: "https://images.pexels.com/photos/15209918/pexels-photo-15209918.png",
      director: "克里斯托弗·诺兰",
      cast: "马修·麦康纳, 安妮·海瑟薇",
      averageScore: 9.4,
      reviewCount: 44
    },
    reviews: {
      list: [
        {
          id: 303,
          userId: 103,
          username: "科幻迷",
          rating: 9,
          comment: "当汉斯·季默的管风琴轰鸣响起，库珀在黑洞深处的五维空间哭泣时，浑身直接起鸡皮疙瘩！",
          likeCount: 88,
          createTime: "2025-03-20T16:20:00Z",
          canEdit: false
        }
      ],
      total: 44,
      page: 1,
      size: 5,
      totalPages: 9
    }
  }
}

// ----------------------------------------------------
// 【合同 3.6】影片排行榜假数据（排行榜页面使用）
// ----------------------------------------------------
export const mockRankingsData = [
  {
    rank: 1,
    movieId: 202,
    title: "霸王别姬",
    coverUrl: "https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg",
    averageScore: 9.6,
    reviewCount: 95
  },
  {
    rank: 2,
    movieId: 203,
    title: "星际穿越",
    coverUrl: "https://images.pexels.com/photos/15209918/pexels-photo-15209918.png",
    averageScore: 9.4,
    reviewCount: 44
  },
  {
    rank: 3,
    movieId: 201,
    title: "肖申克的救赎",
    coverUrl: "https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg",
    averageScore: 9.2,
    reviewCount: 128
  }
]

// ----------------------------------------------------
// 【合同 1.2】用户登录成功返回的假数据
// ----------------------------------------------------
export const mockLoginResponse = {
  token: "eyJhbGciOiJIUzI1NiIsRkJmYW4iLCJyb2xlIjoidXNlciIs...",
  user: {
    id: 101,
    username: "filmfan",
    role: "user", // 普通用户
    status: "approved" // 审核已通过
  }
}

// ----------------------------------------------------
// 【合同 1.3】当前登录用户的详细 Profile 数据
// ----------------------------------------------------
export const mockUserProfile = {
  id: 101,
  username: "filmfan",
  email: "fan@example.com",
  role: "user",
  status: "approved",
  createTime: "2025-01-01T10:00:00Z",
  lastLoginTime: "2025-03-20T15:30:00Z"
}

// ----------------------------------------------------
// 【合同 2.1】管理员专用：待审核用户列表
// ----------------------------------------------------
export const mockAdminPendingUsers = {
  list: [
    {
      id: 102,
      username: "newbie_coder",
      email: "newbie@example.com",
      createTime: "2025-03-19T08:00:00Z"
    },
    {
      id: 106,
      username: "vue3_lover",
      email: "vue3@example.com",
      createTime: "2025-03-21T14:22:00Z"
    }
  ],
  total: 2,
  page: 1,
  size: 10,
  totalPages: 1
}

// ----------------------------------------------------
// 【合同 2.6】管理员专用：全站所有影评大列表（管理视图）
// ----------------------------------------------------
export const mockAdminAllReviews = {
  list: [
    {
      id: 301,
      movieTitle: "肖申克的救赎",
      username: "filmfan",
      rating: 10,
      comment: "永远的神作，信念是关不住的鸟儿。",
      likeCount: 32,
      hidden: false,
      createTime: "2025-03-01T09:00:00Z"
    },
    {
      id: 303,
      movieTitle: "星际穿越",
      username: "科幻迷",
      rating: 9,
      comment: "当汉斯季默的管风琴响起，直接起鸡皮疙瘩！",
      likeCount: 88,
      hidden: false,
      createTime: "2025-03-20T16:20:00Z"
    }
  ],
  total: 2,
  page: 1,
  size: 10
}

// ----------------------------------------------------
// 【合同 2.8】管理员专用：系统访问日志大列表
// ----------------------------------------------------
export const mockAdminLogs = {
  list: [
    {
      id: 401,
      userId: 101,
      username: "filmfan",
      action: "search_movie",
      targetId: null,
      ip: "192.168.1.1",
      userAgent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...",
      createTime: "2025-03-20T16:20:00Z"
    },
    {
      id: 402,
      userId: 105,
      username: "汶汶",
      action: "view_movie_detail",
      targetId: "201",
      ip: "192.168.1.5",
      userAgent: "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) ...",
      createTime: "2026-05-31T11:20:00Z"
    }
  ],
  total: 2,
  page: 1,
  size: 20
}