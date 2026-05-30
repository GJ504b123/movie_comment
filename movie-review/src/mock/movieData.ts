// 严格对齐后端 3.1 接口返回的“影片列表”数据
export const mockMovieList = [
  {
    id: 201,
    title: "肖申克的救赎",
    averageScore: 9.2,
    coverUrl: "https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg" ,
    reviewCount: 128,
    releaseDate: "1994-09-23"
  },
  {
    id: 202,
    title: "霸王别姬",
    averageScore: 9.6,
    coverUrl: "https://images.pexels.com/photos/35195183/pexels-photo-35195183.jpeg",
    reviewCount: 95,
    releaseDate: "2004-1-23"
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


// 严格对齐后端 3.2 接口的 data 结构的 Mock 数据库
export const mockMovieDatabase: Record<string, any> = {
  '201': {
    // 对应合同里的 "movie" 节点
    movie: {
      id: 201,
      title: "肖申克的救赎",
      description: "这场谋杀使银行家安迪蒙冤入狱，在长达20年的牢狱生涯中，他用信念和智慧为自己救赎...",
      releaseDate: "1994-09-23",
      coverUrl: "https://images.pexels.com/photos/1117132/pexels-photo-1117132.jpeg",
      director: "弗兰克·德拉邦特",
      cast: "蒂姆·罗宾斯, 摩根·弗里曼",
      averageScore: 9.2,
      reviewCount: 128
    },
    // 对应合同里的 "reviews" 节点，带分页响应结构
    reviews: {
      list: [
        {
          id: 301,
          userId: 101,
          username: "filmfan",
          rating: 10,
          comment: "永远的神作，信念是关不住的鸟儿。",
          likeCount: 32,
          createTime: "2025-03-01T09:00:00Z",
          canEdit: true
        },
        {
          id: 302,
          userId: 105,
          username: "汶汶",
          rating: 9,
          comment: "吹爆这个骨架屏和路由跳转！",
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
      description: "1",
      releaseDate: "1994-1-22",
      coverUrl: "https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg",
      director: "克里斯托弗·诺兰",
      cast: "马修·麦康纳, 安妮·海瑟薇",
      averageScore: 9.4,
      reviewCount: 95
    },
    reviews: {
      list: [
        {
          id: 303,
          userId: 103,
          username: "科幻迷",
          rating: 9,
          comment: "当汉斯季默的管风琴响起，直接起鸡皮疙瘩！",
          likeCount: 88,
          createTime: "2025-03-20T16:20:00Z",
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
      description: "近未来地球遭遇枯萎病，前飞行员库珀忍痛告别女儿，穿越虫洞为人类寻找新家园...",
      releaseDate: "2014-11-07",
      coverUrl: "https://images.pexels.com/photos/27219316/pexels-photo-27219316.jpeg",
      director: "克里斯托弗·诺兰",
      cast: "马修·麦康纳, 安妮·海瑟薇",
      averageScore: 9.4,
      reviewCount: 95
    },
    reviews: {
      list: [
        {
          id: 303,
          userId: 103,
          username: "科幻迷",
          rating: 9,
          comment: "当汉斯季默的管风琴响起，直接起鸡皮疙瘩！",
          likeCount: 88,
          createTime: "2025-03-20T16:20:00Z",
          canEdit: false
        }
      ],
      total: 95,
      page: 1,
      size: 5,
      totalPages: 19
    }
  }
}