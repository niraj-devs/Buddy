import { useEffect, useRef, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import {
    UserRound,
    ChevronDown,
    Settings,
    LogOut,
    Sun,
    Moon,
    Palette,
    CircleUserRound,
} from 'lucide-react'
import './Navbar.css'

function Navbar() {
    const location = useLocation()
    const navigate = useNavigate()
    const profileRef = useRef(null)

    const isAuthPage =
        location.pathname === '/login' ||
        location.pathname === '/register'

    const isDashboard = ['/dashboard', '/revisions', '/academics', '/skills', '/explore'].includes(location.pathname)
    const isAppPage = ['/dashboard', '/revisions', '/academics', '/skills', '/explore', '/settings', '/settings/preferences'].includes(location.pathname)

    const getStoredValue = (key) =>
        localStorage.getItem(key) || sessionStorage.getItem(key)

    const [visible, setVisible] = useState(!isAuthPage)
    const [profileOpen, setProfileOpen] = useState(false)
    const [appearanceOpen, setAppearanceOpen] = useState(false)

    const [theme, setTheme] = useState(
    localStorage.getItem('buddyTheme') || 'light'
    )

    useEffect(() => {
    const isAppPage = ['/dashboard', '/revisions', '/academics', '/skills', '/explore', '/settings', '/settings/preferences'].includes(location.pathname)


    if (isAppPage) {
        localStorage.setItem('buddyTheme', theme)
        document.documentElement.dataset.buddyTheme = theme
    } else {
        delete document.documentElement.dataset.buddyTheme
    }

    return () => {
        delete document.documentElement.dataset.buddyTheme
    }


    }, [theme, location.pathname])
const [firstName, setFirstName] = useState(
        getStoredValue('buddyFirstName') || ''
    )
    const [isLoggedIn, setIsLoggedIn] = useState(
        Boolean(getStoredValue('buddyAccessToken'))
    )

    // Show the Navbar on the dashboard and reset the profile menu on navigation.
    useEffect(() => {
        setVisible(!isAuthPage)
        setProfileOpen(false)
    }, [location.pathname, isAuthPage])

    // Preserve the existing public-page Navbar behavior.
    // Keep the dashboard Navbar visible at all times.
    useEffect(() => {
        const handleScroll = () => {
            if (isAuthPage || isDashboard) return

            if (window.scrollY < 10) {
                setVisible(true)
            } else {
                setVisible(false)
            }
        }

        const handleMouseMove = (event) => {
            if (!isAuthPage) return
            setVisible(event.clientY <= 20)
        }

        window.addEventListener('scroll', handleScroll, { passive: true })
        window.addEventListener('mousemove', handleMouseMove)

        return () => {
            window.removeEventListener('scroll', handleScroll)
            window.removeEventListener('mousemove', handleMouseMove)
        }
    }, [isAuthPage, isDashboard])

    // Close the profile menu when clicking outside or pressing Escape.
    useEffect(() => {
        const handleOutsideClick = (event) => {
            if (
                profileRef.current &&
                !profileRef.current.contains(event.target)
            ) {
                setProfileOpen(false)
            }
        }

        const handleEscape = (event) => {
            if (event.key === 'Escape') {
                setProfileOpen(false)
            }
        }

        document.addEventListener('mousedown', handleOutsideClick)
        document.addEventListener('keydown', handleEscape)

        return () => {
            document.removeEventListener('mousedown', handleOutsideClick)
            document.removeEventListener('keydown', handleEscape)
        }
    }, [])

    // Sync authentication state after login or logout.
    useEffect(() => {
        const syncAuthState = () => {
            setIsLoggedIn(Boolean(getStoredValue('buddyAccessToken')))
            setFirstName(getStoredValue('buddyFirstName') || '')
        }

        window.addEventListener('storage', syncAuthState)
        window.addEventListener('buddy-auth-changed', syncAuthState)

        return () => {
            window.removeEventListener('storage', syncAuthState)
            window.removeEventListener('buddy-auth-changed', syncAuthState)
        }
    }, [])

    const handleLogout = async () => {
        const refreshToken = getStoredValue('buddyRefreshToken')
        const accessToken = getStoredValue('buddyAccessToken')

        try {
            if (refreshToken) {
                await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        ...(accessToken
                            ? { Authorization: `Bearer ${accessToken}` }
                            : {}),
                    },
                    body: JSON.stringify({ refreshToken }),
                })
            }
        } catch (error) {
            console.error('Logout request failed:', error)
        } finally {
            const keys = [
                'buddyAccessToken',
                'buddyRefreshToken',
                'buddyEmail',
                'buddyFirstName',
            ]

            keys.forEach((key) => {
                localStorage.removeItem(key)
                sessionStorage.removeItem(key)
            })

            setIsLoggedIn(false)
            setFirstName('')
            setProfileOpen(false)

            window.dispatchEvent(new Event('buddy-auth-changed'))
            navigate('/login', { replace: true })
        }
    }

    return (
        <header
            className={`${
                visible || isDashboard
                    ? 'navbar-visible'
                    : 'navbar-hidden'
            } ${isDashboard ? 'dashboard-navbar' : ''}`}
            aria-hidden={isAuthPage && !visible}
        >
            <div className="navbar-container">
                <Link to="/" className="brand" aria-label="Buddy home">
                    <img
                        src="/buddy-logo.png"
                        alt="Buddy"
                        className="brand-logo"
                    />
                </Link>

                <nav className="nav-links">
                    <Link to="/#how-it-works">How It Works</Link>
                    <Link to="/#features">Features</Link>
                </nav>

                <div className="nav-actions">
                    {isLoggedIn && !isAppPage && (
                        <Link to="/dashboard" className="back-dashboard-btn">
                            <span>←</span>
                            Back to Dashboard
                        </Link>
                    )}
                    {isLoggedIn ? (
                        <div className="profile-wrapper" ref={profileRef}>
                            <button
                                type="button"
                                className="profile-trigger"
                                onClick={() => setProfileOpen((open) => !open)}
                                aria-expanded={profileOpen}
                                aria-haspopup="menu"
                            >
                                <span className="profile-avatar">
                                    <UserRound size={18} />
                                </span>

                                <span className="profile-name">
                                    {firstName || 'My Profile'}
                                </span>

                                <ChevronDown
                                    size={16}
                                    className={profileOpen ? 'chevron-open' : ''}
                                />
                            </button>

                            {profileOpen && (
                                <div className="profile-dropdown" role="menu">
                                    <div className="profile-dropdown-header">
                                        <span className="profile-avatar profile-avatar-large">
                                            <UserRound size={22} />
                                        </span>

                                        <div>
                                            <strong>
                                                {firstName || 'Student'}
                                            </strong>
                                            <p>Your Buddy account</p>
                                        </div>
                                    </div>

                                    <div className="profile-dropdown-divider" />

                                    <Link
                                        to="/settings"
                                        className="profile-menu-item"
                                        role="menuitem"
                                        onClick={() => setProfileOpen(false)}
                                    >
                                        <CircleUserRound size={17} />
                                        Profile
                                    </Link>

                                    <Link
                                        to="/settings/preferences"
                                        className="profile-menu-item"
                                        role="menuitem"
                                        onClick={() => {
                                            setProfileOpen(false)
                                            setAppearanceOpen(false)
                                        }}
                                    >
                                        <Settings size={17} />
                                        Settings
                                    </Link>
                                    <button
                                    type="button"
                                    className="profile-menu-item"
                                    aria-expanded={appearanceOpen}
                                    onClick={() => setAppearanceOpen((open) => !open)}

                                    >


                                    <Palette size={17} />



                                    Appearance
                                    <ChevronDown
                                        size={15}
                                        className={appearanceOpen ? 'chevron-open' : ''}
                                    />


                                    </button>

                                    {appearanceOpen && ( <div className="appearance-options">
                                    <button
                                    type="button"
                                    className={`profile-menu-item ${theme === 'light' ? 'appearance-selected' : ''}`}
                                    onClick={() => {
                                    setTheme('light')
                                    setProfileOpen(false)
                                    setAppearanceOpen(false)
                                    }}
                                    > <Sun size={17} />
                                    Light </button>


                                        <button
                                            type="button"
                                            className={`profile-menu-item ${theme === 'dark' ? 'appearance-selected' : ''}`}
                                            onClick={() => {
                                                setTheme('dark')
                                                setProfileOpen(false)
                                                setAppearanceOpen(false)
                                            }}
                                        >
                                            <Moon size={17} />
                                            Dark
                                        </button>
                                    </div>


                                    )}


                                    <button
                                        type="button"
                                        className="profile-menu-item profile-logout"
                                        role="menuitem"
                                        onClick={handleLogout}
                                    >
                                        <LogOut size={17} />
                                        Log out
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        <Link to="/login" className="login-btn">
                            <UserRound
                                className="login-icon"
                                fill="black"
                            />
                            <span>Login</span>
                        </Link>
                    )}
                </div>
            </div>
        </header>
    )
}

export default Navbar
