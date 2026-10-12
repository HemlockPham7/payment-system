import { useState } from 'react'
import { ButtonComponent } from '@syncfusion/ej2-react-buttons'
import { TextBoxComponent } from '@syncfusion/ej2-react-inputs'
import { useTableSettings } from '@root/context/TableSettingsContext.tsx'

const TableSettingsModal = () => {
  const { pageSize, isSettingsOpen, closeSettings, setPageSize } =
    useTableSettings()

  const [selectedPageSize, setSelectedPageSize] = useState(pageSize)

  if (!isSettingsOpen) {
    return null
  }

  const handleSubmit = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault()

    setPageSize(selectedPageSize)
    closeSettings()
  }

  const handleClose = () => {
    setSelectedPageSize(pageSize)
    closeSettings()
  }

  return (
    <div
      className='fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-6'
      onClick={handleClose}
    >
      <div
        className='w-full max-w-125 rounded-2xl bg-white p-6 shadow-200'
        onClick={(e) => e.stopPropagation()}
      >
        <header className='mb-6'>
          <h2 className='p-24-semibold text-dark-100'>Table Settings</h2>

          <p className='mt-2 text-sm text-gray-500'>
            Choose how many items to display per page.
          </p>
        </header>

        <form onSubmit={handleSubmit} className='flex flex-col gap-5'>
          <div className='flex flex-col gap-2'>
            <label htmlFor='pageSize' className='form-label'>
              Items per page
            </label>

            <TextBoxComponent
              id='pageSize'
              type='number'
              value={String(selectedPageSize)}
              cssClass='form-input'
              change={(args) => {
                setSelectedPageSize(Number(args.value))
              }}
            />
          </div>

          <div className='flex gap-3 pt-2'>
            <ButtonComponent
              type='button'
              className='button-class-secondary h-12 flex-1'
              onClick={handleClose}
            >
              <span className='p-18-semibold text-dark-100'>Cancel</span>
            </ButtonComponent>

            <ButtonComponent type='submit' className='button-class h-12 flex-1'>
              <span className='p-18-semibold text-white'>Save Changes</span>
            </ButtonComponent>
          </div>
        </form>
      </div>
    </div>
  )
}

export default TableSettingsModal
