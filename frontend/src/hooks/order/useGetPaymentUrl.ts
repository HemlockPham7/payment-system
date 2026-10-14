import orderAxios from '@root/api/order/orderAxios.ts'
import { useQuery } from '@tanstack/react-query'

export const getPaymentUrl = async (paymentId: string): Promise<string> => {
  const response = await orderAxios.get<string>(`/orders/payment/${paymentId}`)
  return response.data
}

export const useGetPaymentUrl = (paymentId: string) => {
  return useQuery<string, Error>({
    queryKey: ['payment', paymentId],
    queryFn: () => getPaymentUrl(paymentId),
    enabled: !!paymentId,
  })
}
