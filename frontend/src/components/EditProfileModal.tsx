import { useEffect, useState } from 'react'
import { TextBoxComponent } from '@syncfusion/ej2-react-inputs'
import { ButtonComponent } from '@syncfusion/ej2-react-buttons'
import type { User } from '@root/hooks/common/utils.ts'

interface EditProfileModalProps {
  user: User
  onClose: () => void
}

const EditProfileModal = ({ user, onClose }: EditProfileModalProps) => {
  const [displayName, setDisplayName] = useState(user.display_name)
  const [email, setEmail] = useState(user.email)

  useEffect(() => {
    setDisplayName(user.display_name)
    setEmail(user.email)
  }, [user])

  const handleSubmit = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault()
    console.log('hehe')
  }

  return (
    <div
      className='fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-6'
      onClick={onClose}
    >
      <div
        className='w-full max-w-125 rounded-2xl bg-white p-6 shadow-200'
        onClick={(e) => e.stopPropagation()}
      >
        <header className='mb-6'>
          <h2 className='p-24-semibold text-dark-100'>Edit Profile</h2>

          <p className='mt-2 text-sm text-gray-500'>
            Update your personal information
          </p>
        </header>

        <form onSubmit={handleSubmit} className='flex flex-col gap-5'>
          <div className='flex flex-col gap-2'>
            <label htmlFor='displayName' className='form-label'>
              Display Name
            </label>

            <TextBoxComponent
              id='displayName'
              value={displayName}
              placeholder='Enter your display name'
              cssClass='form-input'
              change={(args) => setDisplayName(args.value)}
            />
          </div>

          <div className='flex flex-col gap-2'>
            <label htmlFor='email' className='form-label'>
              Email
            </label>

            <TextBoxComponent
              id='email'
              type='email'
              value={email}
              placeholder='Enter your email'
              cssClass='form-input'
              change={(args) => setEmail(args.value)}
            />
          </div>

          <div className='flex flex-col gap-2'>
            <label htmlFor='username' className='form-label'>
              Username
            </label>

            <TextBoxComponent
              id='username'
              value={user.username}
              cssClass='form-input'
              enabled={false}
            />
          </div>

          <div className='flex gap-3 pt-2'>
            <ButtonComponent
              type='button'
              className='button-class-secondary h-12 flex-1'
              onClick={onClose}
            >
              <span className='p-18-semibold text-dark-100'>Cancel</span>
            </ButtonComponent>
          </div>
        </form>
      </div>
    </div>
  )
}

export default EditProfileModal
