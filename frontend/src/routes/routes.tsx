import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'
import TicketDashboard from '@root/routes/ticket/TicketDashboard.tsx'
import TicketPaymentResult from '@root/routes/ticket/TicketPaymentResult.tsx'
import MovieDashboard from '@root/routes/ticket/MovieDashboard.tsx'
import CheckoutPayment from '@root/routes/ticket/CheckoutPayment.tsx'

const routes = createBrowserRouter([
  {
    path: '/',
    Component: Navbar,
    children: [
      {
        index: true,
        Component: MovieDashboard,
      },
      {
        path: '/movies/:movieId',
        Component: TicketDashboard,
      },
      {
        path: '/payment-result',
        Component: TicketPaymentResult,
      },
      {
        path: '/checkout/:paymentId',
        Component: CheckoutPayment,
      },
    ],
  },
])

export default routes
