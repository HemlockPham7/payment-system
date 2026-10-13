import paymentAxios from '@root/api/payment/paymentAxios.ts'
import { useQuery } from '@tanstack/react-query'

export interface GetAllMoviesResponse {
  id: string
  title: string
  description: string
  seatsStatus: string
}

const getAllMovies = async (): Promise<GetAllMoviesResponse[]> => {
  const response = await paymentAxios.get<GetAllMoviesResponse[]>('/movies')

  return response.data
}

export const useListMovies = () => {
  return useQuery<GetAllMoviesResponse[], Error>({
    queryKey: ['movies'],
    queryFn: () => getAllMovies(),
  })
}
