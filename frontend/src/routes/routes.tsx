import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'
import TicketDashboard from '@root/routes/ticket/TicketDashboard.tsx'
import TicketPaymentResult from '@root/routes/ticket/TicketPaymentResult.tsx'

const routes = createBrowserRouter([
  {
    path: '/',
    Component: Navbar,
    children: [
      {
        index: true,
        Component: TicketDashboard,
      },
      {
        path: '/payment-result',
        Component: TicketPaymentResult,
      },
    ],
  },
])

export default routes
