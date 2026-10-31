
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Sun, Moon, UserRound, Mail, Palette } from 'lucide-react'
import './Settings.css'

function Settings() {
    const [theme, setTheme] = useState(
        localStorage.getItem('buddyTheme') || 'light'
    )

    const [email] = useState(
        localStorage.getItem('buddyEmail') || ''
    )

    useEffect(() => {
        localStorage.setItem('buddyTheme', theme)
        document.documentElement.dataset.buddyTheme = theme
    }, [theme])

    return (
        <main className="settings-page">

            <section className="settings-card">
                <div className="settings-card-heading">
                    <Palette size={21} />
                    <div>
                        <h2>Appearance</h2>
                        <p>Choose how Buddy looks on your device.</p>
                    </div>
                </div>

                <div className="settings-theme-options">
                    <button
                        type="button"
                        className={`settings-theme-option ${theme === 'light' ? 'selected' : ''}`}
                        onClick={() => setTheme('light')}
                    >
                        <Sun size={22} />
                        <span>Light mode</span>
                        {theme === 'light' && <span className="theme-check">Selected</span>}
                    </button>

                    <button
                        type="button"
                        className={`settings-theme-option ${theme === 'dark' ? 'selected' : ''}`}
                        onClick={() => setTheme('dark')}
                    >
                        <Moon size={22} />
                        <span>Dark mode</span>
                        {theme === 'dark' && <span className="theme-check">Selected</span>}
                    </button>
                </div>
            </section>

            <section className="settings-card">
                <div className="settings-card-heading">
                    <Mail size={21} />
                    <div>
                        <h2>Account</h2>
                        <p>Your registered account information.</p>
                    </div>
                </div>

                <div className="settings-account-info">
                    <span>Email address</span>
                    <strong>{email || 'Email not available'}</strong>
                </div>
            </section>

            <section className="settings-card settings-profile-card">
                <div className="settings-card-heading">
                    <UserRound size={21} />
                    <div>
                        <h2>Profile</h2>
                        <p>Update your personal and professional details.</p>
                    </div>
                </div>

                <Link to="/settings" className="settings-profile-link">
                    Manage profile
                </Link>
            </section>
        </main>
    )
}

export default Settings
