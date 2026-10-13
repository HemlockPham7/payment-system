import { Header } from '@root/components'
import { useSearchParams, useNavigate } from 'react-router'

const TicketPaymentResult = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const status = searchParams.get('status')
  const orderId = searchParams.get('orderId')
  const rawAmount = searchParams.get('amount')

  const amount = rawAmount ? Number(rawAmount) / 100 : 0

  return (
    <main className='all-users wrapper'>
      <Header
        title='Ticket Payment Result'
        description='Show information of ticket payment result'
      />

      {status === 'success' && (
        <div className='flex flex-col items-center gap-4 w-full'>
          <div className='size-16 bg-success-50 rounded-full flex items-center justify-center text-success-500 text-3xl font-bold'>
            ✓
          </div>
          <h2 className='text-2xl font-bold text-dark-100'>
            Purchase successfully!
          </h2>
          <p className='text-gray-500 text-sm'>
            Thanks for booking the ticket!
          </p>

          <div className='bg-light-200 p-4 rounded-xl w-full flex flex-col gap-2 text-left mt-2'>
            <div className='flex justify-between text-sm'>
              <span className='text-gray-500'>OrderId:</span>
              <span className='font-semibold text-dark-100'>{orderId}</span>
            </div>
            <div className='flex justify-between text-sm'>
              <span className='text-gray-500'>Total Amount:</span>
              <span className='font-semibold text-success-700'>
                {amount.toLocaleString('vi-VN')} VND
              </span>
            </div>
            <div className='flex justify-between text-sm'>
              <span className='text-gray-500'>Status:</span>
              <span className='font-semibold text-success-700'>SUCCESS</span>
            </div>
          </div>
        </div>
      )}

      {status === 'invalid_signature' && (
        <div className='flex flex-col items-center gap-4 w-full'>
          <div className='size-16 bg-red-50 rounded-full flex items-center justify-center text-red-500 text-3xl font-bold'>
            ⚠
          </div>
          <h2 className='text-2xl font-bold text-red-500'>Error!</h2>
          <p className='text-gray-500 text-sm'>
            The signature is invalid, the transaction might be interfered.
          </p>
        </div>
      )}

      {status === 'failed' && (
        <div className='flex flex-col items-center gap-4 w-full'>
          <div className='size-16 bg-red-50 rounded-full flex items-center justify-center text-red-500 text-3xl font-bold'>
            ✕
          </div>
          <h2 className='text-2xl font-bold text-dark-100'>Purchase Fail!</h2>
          <p className='text-gray-500 text-sm'>
            The transactional is fail via VNPAY!
          </p>
          {orderId && (
            <div className='bg-light-200 p-4 rounded-xl w-full flex justify-between text-sm mt-2'>
              <span className='text-gray-500'>Mã đơn hàng:</span>
              <span className='font-semibold text-dark-100'>{orderId}</span>
            </div>
          )}
        </div>
      )}

      <button
        onClick={() => navigate('/')}
        className='mt-4 w-full py-3.5 bg-primary-100 text-white rounded-xl font-semibold hover:bg-primary-500 transition-all'
      >
        Back to Dashboard
      </button>
    </main>
  )
}

export default TicketPaymentResult
