import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from 'react-router'
import routes from '@root/routes/routes.tsx'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { TableSettingsProvider } from '@root/context/TableSettingsContext.tsx'

const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <TableSettingsProvider>
        <RouterProvider router={routes} />
      </TableSettingsProvider>
    </QueryClientProvider>
  </StrictMode>,
)
