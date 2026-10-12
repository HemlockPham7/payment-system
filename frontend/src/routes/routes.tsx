import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'
import TicketDashboard from '@root/routes/ticket/TicketDashboard.tsx'

const routes = createBrowserRouter([
  {
    path: '/',
    Component: Navbar,
    children: [
      {
        index: true,
        Component: TicketDashboard,
      },
    ],
  },
])

export default routes
