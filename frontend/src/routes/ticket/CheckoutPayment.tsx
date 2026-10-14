import { useEffect } from 'react'
import { useParams } from 'react-router'
import { useGetPaymentUrl } from '@root/hooks/order/useGetPaymentUrl.ts'
import { Header } from '@root/components'

const CheckoutPayment = () => {
  const { paymentId } = useParams<{ paymentId: string }>()

  const {
    data: paymentUrl,
    isLoading,
    isError,
    refetch,
  } = useGetPaymentUrl(paymentId!)

  useEffect(() => {
    if (!paymentUrl || paymentUrl === 'PAYMENT_URL_NOT_READY') {
      return
    }

    window.location.href = paymentUrl
  }, [paymentUrl])

  useEffect(() => {
    if (!paymentId || paymentUrl !== 'PAYMENT_URL_NOT_READY') {
      return
    }

    const timer = setTimeout(() => {
      void refetch()
    }, 2000)

    return () => clearTimeout(timer)
  }, [paymentId, paymentUrl, refetch])

  return (
    <main className='all-users wrapper'>
      <Header
        title='Ticket check out payment'
        description='Waiting for the backend process do the magic work!'
      />

      {isLoading && (
        <div className='flex justify-center items-center py-20'>
          <p className='text-gray-500 text-lg font-medium'>
            Creating your payment session
          </p>
        </div>
      )}

      {isError && (
        <div className='flex justify-center items-center py-20'>
          <p className='error'>Unable to retrieve payment URL.</p>
        </div>
      )}
    </main>
  )
}

export default CheckoutPayment
