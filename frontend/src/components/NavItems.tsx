import { Link, NavLink, useNavigate } from 'react-router'
import { sidebarItems } from '@root/constants'
import { cn } from '@root/lib/utils.ts'
import type { User } from '@root/hooks/common/utils.ts'

interface NavItemsProps {
  handleClick?: () => void
  onProfileClick: () => void
}

const mockUser: User = {
  id: 'user-001',
  display_name: 'Nguyen Van An',
  username: 'nguyenvanan',
  email: 'an.nguyen@example.com',
  created_at: '2026-01-01T08:00:00.000Z',
  updated_at: '2026-10-01T08:00:00.000Z',
}

const NavItems = ({ handleClick, onProfileClick }: NavItemsProps) => {
  const navigate = useNavigate()
  const user = mockUser

  const handleLogout = async () => {
    localStorage.removeItem('accessToken')
    navigate('/sign-in')
  }

  return (
    <section className='nav-items'>
      <Link to='/' className='link-logo'>
        <img
          src='/assets/icons/shorten-url.svg'
          alt='logo'
          className='size-7.5'
        />
        <h1>Ticket App</h1>
      </Link>

      <div className='container'>
        <nav>
          {sidebarItems.map(({ id, href, icon, label }) => (
            <NavLink to={href} key={id}>
              {({ isActive }: { isActive: boolean }) => (
                <div
                  className={cn('group nav-item', {
                    'bg-primary-100 text-white!': isActive,
                  })}
                  onClick={handleClick}
                >
                  <img
                    src={icon}
                    alt={label}
                    className={`group-hover:brightness-0 size-0 group-hover:invert ${isActive ? 'brightness-0 invert' : 'text-dark-200'}`}
                  />
                  {label}
                </div>
              )}
            </NavLink>
          ))}
        </nav>

        <footer className='nav-footer'>
          <button
            type='button'
            className='flex min-w-0 flex-1 items-center gap-2.5 text-left cursor-pointer'
            onClick={onProfileClick}
          >
            <img src='/assets/images/david.webp' alt={user?.display_name} />

            <article>
              <h2>{user?.display_name}</h2>
              <p>{user?.email}</p>
            </article>
          </button>

          <button onClick={handleLogout} className='cursor-pointer'>
            <img
              src='/assets/icons/logout.svg'
              alt='logout'
              className='size-6'
            />
          </button>
        </footer>
      </div>
    </section>
  )
}

export default NavItems
