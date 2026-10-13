import movieAxios from '@root/api/movie/movieAxios.ts'
import { useQuery } from '@tanstack/react-query'
import type { GetAllMoviesResponse } from '@root/hooks/common/utils.ts'

const getAllMovies = async (): Promise<GetAllMoviesResponse[]> => {
  const response = await movieAxios.get<GetAllMoviesResponse[]>('/movies')

  return response.data
}

export const useListMovies = () => {
  return useQuery<GetAllMoviesResponse[], Error>({
    queryKey: ['movies'],
    queryFn: () => getAllMovies(),
  })
}
