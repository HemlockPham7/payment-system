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

export interface GetAllMoviesResponse {
  id: string
  title: string
  description: string
  seatsStatus: string
}
