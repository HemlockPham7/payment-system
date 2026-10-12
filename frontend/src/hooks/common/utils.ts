export interface User {
  id: string
  created_at: string
  updated_at: string
  display_name: string
  username: string
  email: string
}

export interface Base {
  id: string
  createdAt: string
}

export interface Pagination {
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type VideoStatus = 'PROCESSING' | 'READY' | 'FAILED'

export interface Video extends Base {
  title: string
  description: string
  status: VideoStatus
  author: string
  thumbnailKey: string
  thumbnailType: string
  category: string
  viewCount: number
  likeCount: number
}
