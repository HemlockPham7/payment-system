import { Outlet } from 'react-router'
import { SidebarComponent } from '@syncfusion/ej2-react-navigations'
import {
  EditProfileModal,
  NavItems,
  TableSettingsModal,
} from '@root/components'
import { useState } from 'react'
import type { User } from '@root/hooks/common/utils.ts'

const mockUser: User = {
  id: 'user-001',
  display_name: 'Nguyen Van An',
  username: 'nguyenvanan',
  email: 'an.nguyen@example.com',
  created_at: '2026-01-01T08:00:00.000Z',
  updated_at: '2026-10-01T08:00:00.000Z',
}

const Navbar = () => {
  const [isProfileOpen, setIsProfileOpen] = useState(false)

  const user = mockUser

  return (
    <div className='admin-layout'>
      <aside className='w-full max-w-67.5 hidden lg:block'>
        <SidebarComponent width={270} enableGestures={false}>
          <NavItems onProfileClick={() => setIsProfileOpen(true)} />
        </SidebarComponent>
      </aside>
      <aside className='children'>
        <Outlet />
      </aside>

      {isProfileOpen && user && (
        <EditProfileModal user={user} onClose={() => setIsProfileOpen(false)} />
      )}

      <TableSettingsModal />
    </div>
  )
}

export default Navbar
