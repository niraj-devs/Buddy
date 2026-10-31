import { useEffect, useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import './WorkspaceMenu.css'

const items = [
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/academics', label: 'Academics' },
    { to: '/skills', label: 'Career Roadmaps' },
    { to: '/revisions', label: 'Revisions' },
    { to: '/explore', label: 'Explore' },
    { to: '/settings/preferences', label: 'Settings' },
]

function WorkspaceMenu() {
    const { pathname } = useLocation()
    const [open, setOpen] = useState(false)

    useEffect(() => {
        setOpen(false)
    }, [pathname])

    useEffect(() => {
        if (!open) return
        const onKeyDown = (event) => {
            if (event.key === 'Escape') setOpen(false)
        }
        window.addEventListener('keydown', onKeyDown)
        return () => window.removeEventListener('keydown', onKeyDown)
    }, [open])

    return (
        <>
            <button
                type="button"
                className="workspace-menu-toggle"
                aria-expanded={open}
                aria-controls="buddy-workspace-panel"
                aria-label={open ? 'Close workspace menu' : 'Open workspace menu'}
                onClick={() => setOpen(value => !value)}
            >
                <span className="workspace-menu-bars" aria-hidden="true">
                    <span /><span /><span />
                </span>
                <span className="workspace-menu-label">MENU</span>
            </button>

            {open && (
                <div
                    className="workspace-menu-backdrop"
                    onClick={() => setOpen(false)}
                    aria-hidden="true"
                />
            )}

            <aside
                id="buddy-workspace-panel"
                className={`workspace-menu-panel ${open ? 'is-open' : ''}`}
                aria-label="Workspace menu"
                aria-hidden={!open}
                inert={!open}
            >
                <div className="workspace-menu-heading">
                    <strong>Buddy</strong>
                    <button
                        type="button"
                        onClick={() => setOpen(false)}
                        aria-label="Close menu"
                    >
                        ×
                    </button>
                </div>

                <p className="workspace-menu-caption">WORKSPACE</p>
                <nav aria-label="Workspace navigation">
                    {items.map(item => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            end
                            className={({ isActive }) =>
                                `workspace-menu-link ${isActive ? 'is-active' : ''}`
                            }
                            onClick={() => setOpen(false)}
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>
            </aside>
        </>
    )
}

export default WorkspaceMenu