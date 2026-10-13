import movieAxios from '@root/api/movie/movieAxios.ts'
import { useQuery } from '@tanstack/react-query'
import type { GetAllMoviesResponse } from '@root/hooks/common/utils.ts'

export const getMovieById = async (
  movieId: string,
): Promise<GetAllMoviesResponse> => {
  const response = await movieAxios.get<GetAllMoviesResponse>(
    `/movies/${movieId}`,
  )
  return response.data
}

export const useMovieDetail = (movieId: string) => {
  return useQuery<GetAllMoviesResponse, Error>({
    queryKey: ['movie', movieId],
    queryFn: () => getMovieById(movieId),
    enabled: !!movieId,
  })
}
