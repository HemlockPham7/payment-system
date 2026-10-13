import paymentAxios from '@root/api/payment/paymentAxios.ts'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export interface CheckOutPayload {
  customerId: number
  movieId: number
  amount: number
  seats: string[]
  paymentMethod: 'VNPAY' | 'CASH'
}

export type CheckOutResponse = string

function createIdempotencyKey() {
  if (
    typeof globalThis.crypto !== 'undefined' &&
    typeof globalThis.crypto.randomUUID() === 'function'
  ) {
    return globalThis.crypto.randomUUID()
  }

  return `checkout-${Date.now()}-${Math.random().toString(36).slice(2, 12)}`
}

const checkOutPayment = async (
  payload: CheckOutPayload,
  idempotencyKey: string,
): Promise<CheckOutResponse> => {
  const response = await paymentAxios.post<string>(
    '/payments/checkout',
    payload,
    {
      headers: {
        'Idempotency-Key': idempotencyKey,
      },
    },
  )

  return response.data
}

export const useCheckOut = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      payload,
      idempotencyKey,
    }: {
      payload: CheckOutPayload
      idempotencyKey: string
    }) => checkOutPayment(payload, idempotencyKey),
    onSuccess: async (_data, variables) => {
      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: ['movie', variables.payload.movieId],
        }),
      ])
    },
  })
}

export { createIdempotencyKey }
