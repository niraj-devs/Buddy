import { NavLink, Link } from 'react-router-dom'
import './Dashboard.css'

function StudentWorkspaceLayout({ title, breadcrumb, children }) {
    const firstName =
        localStorage.getItem('buddyFirstName') ||
        sessionStorage.getItem('buddyFirstName') ||
        'Student'

    const links = [
        { to: '/dashboard', label: 'Dashboard', icon: '\u2302', end: true },
        { to: '/academics', label: 'Academics', icon: '\u25A4' },
        { to: '/skills', label: 'Career Roadmaps', icon: '\u25C7' },
        { to: '/revisions', label: 'Revisions', icon: '\u21BB', end: true },
        { to: '/explore', label: 'Explore', icon: '\u2315' },
        { to: '/settings/preferences', label: 'Settings', icon: '\u2699' },
    ]

    return (
        <main className="dashboard-page">

            <section className="dashboard-main">
                <header className="dashboard-topbar">
                    <div>
                        <p className="dashboard-breadcrumb">{breadcrumb}</p>
                        <h1>{title}</h1>
                    </div>
                    <Link to="/settings" className="dashboard-account-link">
                        Profile <span aria-hidden="true">{'\u2197'}</span>
                    </Link>
                </header>

                {children}

                <footer className="dashboard-footer">
                    <span>{'\u2302'}</span>
                    <span>LEARN. DEVELOP. BUILD. MOVE FORWARD.</span>
                </footer>
            </section>
        </main>
    )
}

export default StudentWorkspaceLayout