import { useEffect, useRef, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import {
Eye,
EyeOff,
Upload,
Sliders,
Image as ImageIcon,
Check,
Sparkles,
RotateCcw,
X,
BookOpen,
} from 'lucide-react'
import './Login.css'


const POSTER_CARDS = [
    { title: 'PROGRAMMING', subtitle: 'THINK • CODE • SOLVE', color: '#075985' },
    { title: 'PROJECTS', subtitle: 'TURN IDEAS INTO REALITY', color: '#164e63' },
    { title: 'EXPLORE', subtitle: 'DISCOVER YOUR INTERESTS', color: '#173b76' },
     { title: 'ACADEMICS', subtitle: 'SUBJECTS • NOTES • PYQs', color: '#92400e' },
    { title: 'DSA', subtitle: 'LOGIC & PROBLEM SOLVING', color: '#166534' },
    { title: 'FOUNDATIONS', subtitle: 'BUILD STRONG BASICS', color: '#563b85' },
    { title: 'TECH STACK', subtitle: 'WEB • AI • CLOUD • MORE', color: '#9f1239' },
    { title: 'CAREER', subtitle: 'EXPLORE YOUR PATH', color: '#115e59' },
    { title: 'COLLABORATE', subtitle: 'LEARN AND BUILD TOGETHER', color: '#3730a3' },
    { title: 'INTERNSHIPS', subtitle: 'GAIN REAL-WORLD EXPERIENCE', color: '#854d0e' },
    { title: 'PLACEMENTS', subtitle: 'PREPARE • PRACTICE • PERFORM', color: '#7f1d1d' },
    { title: 'YOUR FUTURE', subtitle: 'MAKE EVERY SEMESTER COUNT', color: '#334155' },
];



function Login() {
const navigate = useNavigate()
const location = useLocation()


const [email, setEmail] = useState(
    location.state?.registeredEmail || ''
)

const [firstName, setFirstName] = useState(
    location.state?.registeredFirstName || ''
)
const [password, setPassword] = useState('')
const [isSubmitting, setIsSubmitting] = useState(false)
const [showPassword, setShowPassword] = useState(false)
const [rememberMe, setRememberMe] = useState(true)
const [useSignInCode, setUseSignInCode] = useState(false)
const [learnMoreOpen, setLearnMoreOpen] = useState(false)
const [statusMessage, setStatusMessage] = useState('')
const [customImage, setCustomImage] = useState('')
const [imageUrlInput, setImageUrlInput] = useState('')
const [waveMode, setWaveMode] = useState('liquid')
const [waveIntensity, setWaveIntensity] = useState(18)
const [waveSpeed, setWaveSpeed] = useState(1)
const [showControls, setShowControls] = useState(false)

const turbulenceRef = useRef(null)
const fileInputRef = useRef(null)
const messageTimerRef = useRef(null)

const handlePosterMouseMove = (event) => {
    const poster = event.currentTarget
    const rect = poster.getBoundingClientRect()

    const x = event.clientX - rect.left
    const y = event.clientY - rect.top

    const centerX = rect.width / 2
    const centerY = rect.height / 2

    if (centerX === 0 || centerY === 0) return

    const rotateY = ((x - centerX) / centerX) * 10
    const rotateX = ((centerY - y) / centerY) * 10

    poster.style.setProperty('--rotate-x', `${rotateX}deg`)
    poster.style.setProperty('--rotate-y', `${rotateY}deg`)
}

const handlePosterMouseLeave = (event) => {
    const poster = event.currentTarget

    poster.style.setProperty('--rotate-x', '0deg')
    poster.style.setProperty('--rotate-y', '0deg')
}

useEffect(() => {
    let frameId
    let phase = 0

    const animate = () => {
        if (waveMode === 'liquid' && turbulenceRef.current) {
            phase += 0.004 * waveSpeed

            const x = 0.011 + Math.sin(phase) * 0.003
            const y = 0.016 + Math.cos(phase * 0.85) * 0.004

            turbulenceRef.current.setAttribute(
                'baseFrequency',
                `${x.toFixed(5)} ${y.toFixed(5)}`
            )
        }

        frameId = requestAnimationFrame(animate)
    }

    frameId = requestAnimationFrame(animate)

    const handlePosterMouseMove = (event) => {
        const poster = event.currentTarget
        const rect = poster.getBoundingClientRect()

        const x = event.clientX - rect.left
        const y = event.clientY - rect.top

        const centerX = rect.width / 2
        const centerY = rect.height / 2

        const rotateY = ((x - centerX) / centerX) * 10
        const rotateX = ((centerY - y) / centerY) * 10

        poster.style.setProperty('--rotate-x', `${rotateX}deg`)
        poster.style.setProperty('--rotate-y', `${rotateY}deg`)
    }

    const handlePosterMouseLeave = (event) => {
        const poster = event.currentTarget

        poster.style.setProperty('--rotate-x', '0deg')
        poster.style.setProperty('--rotate-y', '0deg')
    }

    return () => cancelAnimationFrame(frameId)
}, [waveMode, waveSpeed])

useEffect(() => {
    return () => {
        if (messageTimerRef.current) {
            clearTimeout(messageTimerRef.current)
        }
    }
}, [])

const showMessage = (message) => {
    if (messageTimerRef.current) {
        clearTimeout(messageTimerRef.current)
    }

    setStatusMessage(message)

    messageTimerRef.current = setTimeout(() => {
        setStatusMessage('')
    }, 3500)
}

const handleFileUpload = (event) => {
    const file = event.target.files?.[0]

    if (!file) return

    if (!file.type.startsWith('image/')) {
        showMessage('Please select a valid image file.')
        return
    }

    if (file.size > 5 * 1024 * 1024) {
        showMessage('Please choose an image smaller than 5 MB.')
        return
    }

    const reader = new FileReader()

    reader.onload = () => {
        if (typeof reader.result === 'string') {
            setCustomImage(reader.result)
            showMessage('Your background image has been applied.')
        }
    }

    reader.onerror = () => {
        showMessage('Could not read that image. Try another file.')
    }

    reader.readAsDataURL(file)
    event.target.value = ''
}

const handleUrlSubmit = (event) => {
    event.preventDefault()

    try {
        const url = new URL(imageUrlInput.trim())

        if (!['http:', 'https:'].includes(url.protocol)) {
            showMessage('Please enter a valid HTTP or HTTPS image URL.')
            return
        }

        setCustomImage(url.href)
        setImageUrlInput('')
        showMessage('Background image updated.')
    } catch {
        showMessage('Please enter a valid image URL.')
    }
}

const handleSignIn = async (event) => {
    event.preventDefault()

    if (useSignInCode) {
        showMessage('Sign-in codes are not available yet.')
        return
    }

    const normalizedEmail = email.trim()

    if (!normalizedEmail) {
        showMessage('Please enter your email address.')
        return
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(normalizedEmail)) {
        showMessage('Please enter a valid email address.')
        return
    }

    if (!password) {
        showMessage('Please enter your password.')
        return
    }

    setIsSubmitting(true)

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: normalizedEmail,
                password,
            }),
        })

        const data = await response.json().catch(() => null)

        if (!response.ok) {
            showMessage(
                data?.message ||
                data?.detail ||
                (response.status === 401
                    ? 'Invalid email or password.'
                    : `Login failed (${response.status}). Please try again.`)
            )
            return
        }

        if (!data?.accessToken || !data?.refreshToken) {
            showMessage('The server returned an unexpected login response.')
            return
        }

        const storage = rememberMe ? localStorage : sessionStorage

        // Clear previous login data from both storage locations.
        const keys = [
            'buddyAccessToken',
            'buddyRefreshToken',
            'buddyEmail',
            'buddyFirstName',
            'buddyLastName',
        ]

        keys.forEach((key) => {
            localStorage.removeItem(key)
            sessionStorage.removeItem(key)
        })

        // Store the authenticated student's details from the backend.
        storage.setItem('buddyAccessToken', data.accessToken)
        storage.setItem('buddyRefreshToken', data.refreshToken)
        storage.setItem('buddyEmail', data.email)
        storage.setItem('buddyFirstName', data.firstName)
        storage.setItem('buddyLastName', data.lastName)
        // Notify the Navbar that authentication has changed.
        window.dispatchEvent(new Event('buddy-auth-changed'))

        showMessage('Login successful!')

        navigate('/dashboard', { replace: true })

    } catch (error) {
        console.error('Login request failed:', error)
        showMessage(
            'Could not connect to Buddy. Check whether the backend is running.'
        )
    } finally {
        setIsSubmitting(false)
    }
}

return (
    <main className="buddy-login">
        <style>{`
            @keyframes buddyWaterFloat {
                0%, 100% { transform: scale(1.13) translate3d(0, 0, 0) rotate(0deg); }
                25% { transform: scale(1.16) translate3d(-9px, -7px, 0) rotate(-0.25deg); }
                50% { transform: scale(1.14) translate3d(7px, -10px, 0) rotate(0.25deg); }
                75% { transform: scale(1.17) translate3d(-5px, 7px, 0) rotate(-0.15deg); }
            }

            @keyframes buddyCaustics {
                0%, 100% { opacity: 0.12; transform: translateY(0); }
                50% { opacity: 0.25; transform: translateY(-10px); }
            }

            .buddy-login .buddy-water-float {
                animation: buddyWaterFloat ${Math.max(6, 16 / Math.max(0.3, waveSpeed))}s ease-in-out infinite;
                will-change: transform;
            }

            .buddy-login .buddy-water-sheen {
                animation: buddyCaustics 9s ease-in-out infinite;
            }

            @media (prefers-reduced-motion: reduce) {
                .buddy-login .buddy-water-float,
                .buddy-login .buddy-water-sheen {
                    animation: none !important;
                }
            }
        `}</style>

        <svg className="buddy-filter-defs" aria-hidden="true">
            <defs>
                <filter id="buddy-water-filter">
                    <feTurbulence
                        ref={turbulenceRef}
                        type="fractalNoise"
                        baseFrequency="0.012 0.018"
                        numOctaves="2"
                        seed="3"
                        result="waterNoise"
                    />
                    <feDisplacementMap
                        in="SourceGraphic"
                        in2="waterNoise"
                        scale={waveMode === 'liquid' ? waveIntensity : 0}
                        xChannelSelector="R"
                        yChannelSelector="G"
                    />
                </filter>
            </defs>
        </svg>

        <div className="buddy-login-background" aria-hidden="true">
            <div
                className="buddy-water-float"
                style={{
                    filter: waveMode === 'liquid'
                        ? 'url(#buddy-water-filter)'
                        : 'none',
                }}
            >
                {customImage ? (
                    <img
                        className="buddy-custom-background"
                        src={customImage}
                        alt=""
                    />
                ) : (
                    <div className="buddy-poster-stage">
                        <div className="buddy-poster-grid">
                            {POSTER_CARDS.map((poster, index) => (
                                <article
                                    key={`${poster.title}-${index}`}
                                    className="buddy-poster"
                                    onMouseMove={handlePosterMouseMove}
                                    onMouseLeave={handlePosterMouseLeave}
                                    style={{
                                        '--poster-color': poster.color,
                                        '--poster-index': index,
                                    }}
                                >
                                    <div className="buddy-poster-top">
                                        <span className="buddy-poster-mark">B.</span>
                                        <span>LEARN · BUILD · GROW</span>
                                    </div>

                                    <div className="buddy-poster-title">
                                        <p>{poster.subtitle}</p>
                                        <h2>{poster.title}</h2>
                                    </div>
                                </article>
                            ))}
                        </div>
                    </div>
                )}

                <div className="buddy-water-sheen" />
            </div>

            <div className="buddy-login-vignette" />
        </div>

        <section className="buddy-login-content">
              <Link to="/" className="auth-brand buddy-login-brand">
                    <span className="auth-brand-icon">
                        <BookOpen size={22} />
                    </span>
                    <span>Buddy</span>
                </Link>
            <div className="buddy-signin-card">
                <div className="buddy-signin-heading">
                    <p className="buddy-signin-eyebrow">YOUR STUDENT SPACE</p>
                    <h1>Welcome back</h1>
                    <p>Sign in to continue your journey with Buddy.</p>
                </div>

                {statusMessage && (
                    <div className="buddy-status-message" role="status">
                        <span>{statusMessage}</span>
                        <button
                            type="button"
                            onClick={() => setStatusMessage('')}
                            aria-label="Dismiss message"
                        >
                            <X size={16} />
                        </button>
                    </div>
                )}

                <form className="buddy-signin-form" onSubmit={handleSignIn}>
                    <label className="buddy-floating-field">
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder=" "
                            autoComplete="email"
                            required
                        />
                        <span>Email address</span>
                    </label>

                    {!useSignInCode && (
                        <label className="buddy-floating-field buddy-password-field">
                            <input
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                placeholder=" "
                                autoComplete="current-password"
                                required
                            />
                            <span>Password</span>

                            <button
                                type="button"
                                className="buddy-password-toggle"
                                onClick={() => setShowPassword((previous) => !previous)}
                                aria-label={showPassword ? 'Hide password' : 'Show password'}
                            >
                                {showPassword
                                    ? <EyeOff size={17} />
                                    : <Eye size={17} />}
                            </button>
                        </label>
                    )}

                   <button
                       type="submit"
                       className="buddy-signin-submit"
                       disabled={isSubmitting}
                   >
                       {isSubmitting
                           ? 'Signing in...'
                           : useSignInCode
                               ? 'Request sign-in code'
                               : 'Sign in'}
                   </button>
                    <div className="buddy-signin-divider">
                        <span />
                        <span>OR</span>
                        <span />
                    </div>

                    <button
                        type="button"
                        className="buddy-signin-secondary"
                        onClick={() => setUseSignInCode((previous) => !previous)}
                    >
                        {useSignInCode ? 'Use your password' : 'Use a sign-in code'}
                    </button>

                    <div className="buddy-signin-options">
                        <label className="buddy-remember">
                            <input
                                type="checkbox"
                                checked={rememberMe}
                                onChange={(event) => setRememberMe(event.target.checked)}
                            />
                            <span className="buddy-check-visual">
                                {rememberMe && <Check size={13} />}
                            </span>
                            <span>Remember me</span>
                        </label>

                        <button
                            type="button"
                            className="buddy-forgot-link"
                            onClick={() => showMessage(
                                'Password reset will be available after backend integration.'
                            )}
                        >
                            Forgot password?
                        </button>
                    </div>
                </form>

                <p className="buddy-signup-prompt">
                    New to Buddy? <Link to="/register">Create an account</Link>
                </p>

                <p className="buddy-signin-disclaimer">
                    Your learning journey, organized in one place.
                </p>
            </div>
        </section>

    </main>
)


}

export default Login
