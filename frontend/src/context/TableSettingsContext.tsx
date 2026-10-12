import { createContext, useContext, useState, type ReactNode } from 'react'

interface TableSettingsContextValue {
  pageSize: number
  setPageSize: (pageSize: number) => void
  isSettingsOpen: boolean
  openSettings: () => void
  closeSettings: () => void
}

const TableSettingsContext = createContext<
  TableSettingsContextValue | undefined
>(undefined)

interface TableSettingsProviderProps {
  children: ReactNode
}

export const TableSettingsProvider = ({
  children,
}: TableSettingsProviderProps) => {
  const [pageSize, setPageSize] = useState(5)
  const [isSettingsOpen, setIsSettingsOpen] = useState(false)

  const openSettings = () => {
    setIsSettingsOpen(true)
  }

  const closeSettings = () => {
    setIsSettingsOpen(false)
  }

  return (
    <TableSettingsContext.Provider
      value={{
        pageSize,
        setPageSize,
        isSettingsOpen,
        openSettings,
        closeSettings,
      }}
    >
      {children}
    </TableSettingsContext.Provider>
  )
}

export const useTableSettings = () => {
  const context = useContext(TableSettingsContext)

  if (!context) {
    throw new Error(
      'useTableSettings must be used within TableSettingsProvider',
    )
  }

  return context
}
