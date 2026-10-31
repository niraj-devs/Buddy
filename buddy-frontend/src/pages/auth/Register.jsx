import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowRight, BookOpen, Eye, EyeOff } from 'lucide-react'
import './Auth.css'
import PosterBackground from './PosterBackground'

function Register() {
const navigate = useNavigate()
const [showPassword, setShowPassword] = useState(false)
const [form, setForm] = useState({
fullName: '',
email: '',
password: '',
confirmPassword: '',
})
const [isSubmitting, setIsSubmitting] = useState(false)
const [error, setError] = useState('')

const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value })
    setError('')
}

const handleSubmit = async (event) => {
    event.preventDefault()

    if (form.fullName.trim().length < 2) {
        setError('Please enter your full name.')
        return
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
        setError('Please enter a valid email address.')
        return
    }

    if (form.password.length < 8) {
        setError('Your password must contain at least 8 characters.')
        return
    }

    if (form.password !== form.confirmPassword) {
        setError('Your passwords do not match.')
        return
    }

    const nameParts = form.fullName.trim().split(/\s+/)

    const firstName = nameParts[0]
    const lastName = nameParts.slice(1).join(' ') || firstName

    setIsSubmitting(true)
    setError('')

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                firstName,
                lastName,
                email: form.email.trim(),
                password: form.password,
            }),
        })

        const data = await response.json().catch(() => null)

        if (!response.ok) {
            setError(
                data?.message ||
                data?.detail ||
                (response.status === 409
                    ? 'An account with this email already exists.'
                    : `Registration failed (${response.status}). Please try again.`)
            )
            return
        }

       navigate('/login', {
           state: {
               registeredEmail: form.email.trim(),
               registeredFirstName: firstName,
           },
       })
    } catch (error) {
        console.error('Registration request failed:', error)
        setError('Could not connect to Buddy. Check whether the backend is running.')
    } finally {
        setIsSubmitting(false)
    }
}

return (
    <main className="auth-page">
        <PosterBackground />
        <section className="auth-aside">
            <Link to="/" className="auth-brand">
                <span className="auth-brand-icon">
                    <BookOpen size={22} />
                </span>
                <span>Buddy</span>
            </Link>

            <div className="auth-aside-content">
                <p className="auth-eyebrow">
                    YOUR NEXT CHAPTER STARTS HERE
                </p>

                <h1>
                    Small steps.
                    <br />
                    Remarkable progress.
                </h1>

                <p>
                    Bring your academics, skills, and career goals together.
                    Build your journey one milestone at a time.
                </p>

                <div className="auth-note">
                    <span className="auth-note-mark">✓</span>
                    <span>
                        A clearer path from learning to opportunity.
                    </span>
                </div>
            </div>

            <p className="auth-aside-footer">
                Your pace. Your priorities. Your progress.
            </p>
        </section>

        <section className="auth-main">
            <div className="auth-form-wrap">
                <div className="auth-mobile-brand">
                    <span className="auth-brand-icon">
                        <BookOpen size={22} />
                    </span>
                    <span>Buddy</span>
                </div>

                <p className="auth-step">GET STARTED</p>

                <h2>Create your account</h2>

                <p className="auth-subtitle">
                    Connect with your Buddy and grow together.
                </p>

                <form
                    className="auth-form"
                    onSubmit={handleSubmit}
                    noValidate
                >
                    <label htmlFor="fullName">Full name</label>

                    <input
                        id="fullName"
                        name="fullName"
                        type="text"
                        placeholder="Enter your full name"
                        autoComplete="name"
                        value={form.fullName}
                        onChange={handleChange}
                        required
                    />

                    <label htmlFor="registerEmail">Email address</label>

                    <input
                        id="registerEmail"
                        name="email"
                        type="email"
                        placeholder="you@example.com"
                        autoComplete="email"
                        value={form.email}
                        onChange={handleChange}
                        required
                    />

                    <label htmlFor="registerPassword">Password</label>

                    <div className="auth-password-field">
                        <input
                            id="registerPassword"
                            name="password"
                            type={showPassword ? 'text' : 'password'}
                            placeholder="At least 8 characters"
                            autoComplete="new-password"
                            value={form.password}
                            onChange={handleChange}
                            required
                        />

                        <button
                            type="button"
                            className="auth-password-toggle"
                            onClick={() => setShowPassword(!showPassword)}
                            aria-label={
                                showPassword ? 'Hide password' : 'Show password'
                            }
                        >
                            {showPassword
                                ? <EyeOff size={18} />
                                : <Eye size={18} />
                            }
                        </button>
                    </div>

                    <label htmlFor="confirmPassword">
                        Confirm password
                    </label>

                    <input
                        id="confirmPassword"
                        name="confirmPassword"
                        type="password"
                        placeholder="Enter your password again"
                        autoComplete="new-password"
                        value={form.confirmPassword}
                        onChange={handleChange}
                        required
                    />

                    {error && (
                        <p className="auth-error" role="alert">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        className="auth-submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Creating account...' : 'Create account'}
                        {!isSubmitting && <ArrowRight size={18} />}
                    </button>
                </form>

                <p className="auth-switch">
                    Already have an account? <Link to="/login">Log in</Link>
                </p>

                <p className="auth-legal">
                    By continuing, you agree to use Buddy responsibly.
                </p>
            </div>
        </section>
    </main>
)


}

export default Register
