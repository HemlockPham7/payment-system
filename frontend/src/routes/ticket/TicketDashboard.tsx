import { Header } from '@root/components'
import { useParams } from 'react-router'
import { useMovieDetail } from '@root/hooks/movie/useMovieDetail.ts'
import { useRef, useState } from 'react'
import {
  createIdempotencyKey,
  useCheckOut,
} from '@root/hooks/payment/useCheckOut.ts'

const ROWS = [1, 2, 3, 4, 5]
const COLS = ['A', 'B', 'C', 'D']

const getSeatPrice = (row: number) => {
  return row >= 4 ? 150000 : 90000
}

const TicketDashboard = () => {
  const { movieId } = useParams<{ movieId: string }>()

  const { data: movie, isLoading, isError } = useMovieDetail(movieId!)
  const [selectedSeats, setSelectedSeats] = useState<string[]>([])
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)

  const checkoutMutation = useCheckOut()
  const idempotencyKeyRef = useRef<string | null>(null)

  let seatsStatusMap: Record<string, string> = {}
  try {
    if (movie?.seatsStatus) {
      seatsStatusMap = JSON.parse(movie.seatsStatus)
    }
  } catch (e) {
    console.error('Parse seatsStatus failed:', e)
  }

  const handleSeatClick = (seatCode: string, status: string) => {
    if (status !== 'AVAILABLE') return

    idempotencyKeyRef.current = null

    if (selectedSeats.includes(seatCode)) {
      setSelectedSeats(selectedSeats.filter((s) => s !== seatCode))
    } else {
      setSelectedSeats([...selectedSeats, seatCode])
    }
  }

  const totalAmount = selectedSeats.reduce((sum, seatCode) => {
    const row = parseInt(seatCode.charAt(0))
    return sum + getSeatPrice(row)
  }, 0)

  const handleCheckout = async () => {
    if (selectedSeats.length === 0 || isSubmitting) return

    setIsSubmitting(true)

    if (idempotencyKeyRef.current === null) {
      idempotencyKeyRef.current = createIdempotencyKey()
    }

    try {
      window.location.href = await checkoutMutation.mutateAsync({
        idempotencyKey: idempotencyKeyRef.current,
        payload: {
          customerId: '123',
          movieId: movieId!,
          amount: totalAmount,
          seats: selectedSeats,
          paymentMethod: 'VNPAY',
        },
      })
    } catch (error) {
      console.error('Checkout failed:', error)
    } finally {
      setIsSubmitting(false)
    }
  }

  if (isLoading) {
    return (
      <main className='all-users wrapper py-10'>
        <p className='text-center text-gray-500'>Loading seats...</p>
      </main>
    )
  }

  if (isError || !movie) {
    return (
      <main className='all-users wrapper py-10'>
        <p className='error'>Not found movie detail</p>
      </main>
    )
  }

  return (
    <main className='all-users wrapper'>
      <Header
        title='Ticket Dashboard'
        description='Show all the ticket for selling'
      />

      <div className='flex items-center gap-6 bg-white p-4 rounded-xl shadow-sm w-fit'>
        <div className='flex items-center gap-2'>
          <span className='size-5 bg-white border border-gray-300 rounded-md inline-block' />
          <span className='text-sm text-gray-700'>AVAILABLE</span>
        </div>
        <div className='flex items-center gap-2'>
          <span className='size-5 bg-primary-100 rounded-md inline-block' />
          <span className='text-sm text-gray-700'>SELECTING</span>
        </div>
        <div className='flex items-center gap-2'>
          <span className='size-5 bg-red-500 rounded-md inline-block' />
          <span className='text-sm text-gray-700'>LOCKED</span>
        </div>
        <div className='flex items-center gap-2'>
          <span className='size-5 bg-gray-300 rounded-md inline-block' />
          <span className='text-sm text-gray-700'>BOOKED</span>
        </div>
      </div>

      <div className='bg-white p-8 rounded-2xl shadow-300 flex flex-col items-center gap-8'>
        <div className='w-full max-w-md h-3 bg-gray-300 rounded-full text-center text-xs font-bold text-gray-600 tracking-widest uppercase mb-4'>
          Screen
        </div>

        <div className='flex flex-col gap-4'>
          {ROWS.map((row) => (
            <div key={row} className='flex items-center gap-4'>
              <span className='w-6 text-sm font-semibold text-gray-400'>
                R{row}
              </span>
              <div className='flex gap-3'>
                {COLS.map((col) => {
                  const seatCode = `${row}${col}`
                  const status = seatsStatusMap[seatCode] || 'AVAILABLE'
                  const isSelected = selectedSeats.includes(seatCode)

                  let bgClass =
                    'bg-white border border-gray-300 text-dark-100 hover:border-primary-100 cursor-pointer'
                  if (status === 'BOOKED') {
                    bgClass = 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  } else if (status === 'LOCKED') {
                    bgClass = 'bg-red-500 text-white cursor-not-allowed'
                  } else if (isSelected) {
                    bgClass = 'bg-primary-100 text-white font-bold'
                  }

                  return (
                    <button
                      key={seatCode}
                      disabled={status !== 'AVAILABLE'}
                      onClick={() => handleSeatClick(seatCode, status)}
                      className={`size-12 rounded-xl flex flex-col items-center justify-center transition-all shadow-sm ${bgClass}`}
                    >
                      <span className='text-xs font-semibold'>{seatCode}</span>
                      <span className='text-[10px] opacity-75'>
                        {row >= 4 ? 'VIP' : 'NORMAL'}
                      </span>
                    </button>
                  )
                })}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className='bg-white p-6 rounded-2xl shadow-200 flex flex-col md:flex-row items-center justify-between gap-4 sticky bottom-5'>
        <div>
          <p className='text-sm text-gray-100'>
            Seats are selected:{' '}
            <span className='font-bold text-dark-100'>
              {selectedSeats.length > 0
                ? selectedSeats.join(', ')
                : 'Not selecting any seat'}
            </span>
          </p>
          <p className='text-lg font-bold text-primary-500 mt-1'>
            Total amount: {totalAmount.toLocaleString('vi-VN')} VND
          </p>
        </div>

        <button
          disabled={selectedSeats.length === 0 || isSubmitting}
          onClick={handleCheckout}
          className={`px-8 py-3.5 rounded-xl font-semibold text-white transition-all ${
            selectedSeats.length === 0 || isSubmitting
              ? 'bg-gray-300 cursor-not-allowed'
              : 'bg-primary-100 hover:bg-primary-500 shadow-lg'
          }`}
        >
          {isSubmitting
            ? 'Creating Payment...'
            : 'Confirm & Purchase via VNPay'}
        </button>
      </div>
    </main>
  )
}

export default TicketDashboard
