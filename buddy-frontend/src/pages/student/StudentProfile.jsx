import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
    ArrowLeft,
    ExternalLink,
    Code2,
    Globe,
    BriefcaseBusiness,
    LoaderCircle,
    Save,
    UserRound,
} from 'lucide-react'
import './StudentProfile.css'

const API_URL = '/api/students/me'

const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

const getInitials = (firstName = '', lastName = '') =>
    `${firstName.trim().charAt(0)}${lastName.trim().charAt(0)}`.toUpperCase() || 'S'

export default function StudentProfile() {
    const [profile, setProfile] = useState(null)
    const [form, setForm] = useState({
        bio: '',
        profilePictureUrl: '',
        linkedinUrl: '',
        githubUrl: '',
        portfolioUrl: '',
    })
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    useEffect(() => {
        const controller = new AbortController()

        async function loadProfile() {
            try {
                const token = getAccessToken()

                if (!token) {
                    throw new Error('Your session has expired. Please log in again.')
                }

                const response = await fetch(API_URL, {
                    headers: { Authorization: `Bearer ${token}` },
                    signal: controller.signal,
                })

                if (!response.ok) {
                    if (response.status === 404) {
                        throw new Error('Your student profile was not found.')
                    }
                    if (response.status === 401) {
                        throw new Error('Your session has expired. Please log in again.')
                    }
                    throw new Error('Unable to load your profile. Please try again.')
                }

                const data = await response.json()
                setProfile(data)
                setForm({
                    bio: data.bio ?? '',
                    profilePictureUrl: data.profilePictureUrl ?? '',
                    linkedinUrl: data.linkedinUrl ?? '',
                    githubUrl: data.githubUrl ?? '',
                    portfolioUrl: data.portfolioUrl ?? '',
                })
            } catch (err) {
                if (err.name !== 'AbortError') {
                    setError(err.message || 'Something went wrong while loading your profile.')
                }
            } finally {
                if (!controller.signal.aborted) setLoading(false)
            }
        }

        loadProfile()
        return () => controller.abort()
    }, [])

    const handleChange = (event) => {
        const { name, value } = event.target
        setForm((current) => ({ ...current, [name]: value }))
        setError('')
        setSuccess('')
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        setError('')
        setSuccess('')

        const token = getAccessToken()
        if (!token) {
            setError('Your session has expired. Please log in again.')
            return
        }

        setSaving(true)

        try {
            const response = await fetch(API_URL, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    bio: form.bio,
                    profilePictureUrl: form.profilePictureUrl,
                    linkedinUrl: form.linkedinUrl,
                    githubUrl: form.githubUrl,
                    portfolioUrl: form.portfolioUrl,
                }),
            })

            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Your session has expired. Please log in again.')
                }

                const result = await response.json().catch(() => null)
                const validationMessage = result?.message ||
                    (result?.errors ? Object.values(result.errors).join(' ') : '')

                throw new Error(
                    validationMessage || 'Could not save your profile. Check your inputs and try again.'
                )
            }

            const updatedProfile = await response.json()
            setProfile(updatedProfile)
            setForm({
                bio: updatedProfile.bio ?? '',
                profilePictureUrl: updatedProfile.profilePictureUrl ?? '',
                linkedinUrl: updatedProfile.linkedinUrl ?? '',
                githubUrl: updatedProfile.githubUrl ?? '',
                portfolioUrl: updatedProfile.portfolioUrl ?? '',
            })
            setSuccess('Your profile has been saved successfully.')
        } catch (err) {
            setError(err.message || 'Something went wrong while saving your profile.')
        } finally {
            setSaving(false)
        }
    }

    if (loading) {
        return (
            <main className="student-profile-page">
                <div className="profile-state">
                    <LoaderCircle className="profile-spinner" size={28} />
                    <p>Loading your profile...</p>
                </div>
            </main>
        )
    }

    if (error && !profile) {
        return (
            <main className="student-profile-page">
                <div className="profile-state">
                    <p className="profile-error" role="alert">{error}</p>
                    <Link className="profile-back-link" to="/dashboard">
                        <ArrowLeft size={17} /> Back to dashboard
                    </Link>
                </div>
            </main>
        )
    }

    if (!profile) return null

    const fullName = `${profile.firstName ?? ''} ${profile.lastName ?? ''}`.trim()

    return (
        <main className="student-profile-page">
            <div className="student-profile-container">
                <Link className="profile-back-link" to="/dashboard">
                    <ArrowLeft size={17} />
                    Back to dashboard
                </Link>

                <header className="profile-page-heading">
                    <div>
                        <span className="profile-eyebrow">YOUR ACCOUNT</span>
                        <h1>Profile & Settings</h1>
                        <p>Manage your personal details and professional links.</p>
                    </div>
                </header>

                <section className="profile-summary-card">
                    <div className="profile-avatar">
                        {form.profilePictureUrl.trim() ? (
                            <img
                                src={form.profilePictureUrl.trim()}
                                alt={`${fullName}'s profile`}
                                onError={(event) => {
                                    event.currentTarget.style.display = 'none'
                                }}
                            />
                        ) : (
                            <span>{getInitials(profile.firstName, profile.lastName) || <UserRound size={30} />}</span>
                        )}
                    </div>

                    <div className="profile-summary-details">
                        <h2>{fullName || 'Student'}</h2>
                        <p>{profile.email}</p>
                        <span className="profile-role-badge">
                            {profile.careerPathName || 'Student'}
                        </span>
                    </div>
                </section>

                <form className="profile-form-card" onSubmit={handleSubmit}>
                    <div className="profile-section-heading">
                        <div>
                            <h2>Personal information</h2>
                            <p>Keep your profile up to date.</p>
                        </div>
                    </div>

                    <div className="profile-readonly-grid">
                        <div className="profile-field">
                            <label htmlFor="profile-full-name">Full name</label>
                            <input id="profile-full-name" value={fullName} readOnly />
                        </div>
                        <div className="profile-field">
                            <label htmlFor="profile-email">Email address</label>
                            <input id="profile-email" value={profile.email ?? ''} readOnly />
                        </div>
                    </div>

                    <div className="profile-field">
                        <label htmlFor="profile-bio">About you</label>
                        <textarea
                            id="profile-bio"
                            name="bio"
                            value={form.bio}
                            onChange={handleChange}
                            maxLength={500}
                            rows={4}
                            placeholder="Tell others about your interests, skills, and goals..."
                        />
                        <span className="profile-field-hint">{form.bio.length}/500 characters</span>
                    </div>

                    <div className="profile-field">
                        <label htmlFor="profile-picture">Profile picture URL</label>
                        <input
                            id="profile-picture"
                            name="profilePictureUrl"
                            type="url"
                            value={form.profilePictureUrl}
                            onChange={handleChange}
                            maxLength={2048}
                            placeholder="https://example.com/your-photo.jpg"
                        />
                        <span className="profile-field-hint">
                            Use a publicly accessible image URL. File uploads are not configured yet.
                        </span>
                    </div>

                    <div className="profile-section-heading profile-links-heading">
                        <div>
                            <h2>Professional links</h2>
                            <p>Help others find your work and connect with you.</p>
                        </div>
                    </div>

                    <div className="profile-field">
                        <label htmlFor="profile-linkedin"><BriefcaseBusiness size={16} /> LinkedIn</label>
                        <input
                            id="profile-linkedin"
                            name="linkedinUrl"
                            type="url"
                            value={form.linkedinUrl}
                            onChange={handleChange}
                            maxLength={2048}
                            placeholder="https://www.linkedin.com/in/your-profile"
                        />
                    </div>

                    <div className="profile-field">
                        <label htmlFor="profile-github"><Code2 size={16} /> GitHub</label>
                        <input
                            id="profile-github"
                            name="githubUrl"
                            type="url"
                            value={form.githubUrl}
                            onChange={handleChange}
                            maxLength={2048}
                            placeholder="https://github.com/your-username"
                        />
                    </div>

                    <div className="profile-field">
                        <label htmlFor="profile-portfolio"><Globe size={16} /> Portfolio website</label>
                        <input
                            id="profile-portfolio"
                            name="portfolioUrl"
                            type="url"
                            value={form.portfolioUrl}
                            onChange={handleChange}
                            maxLength={2048}
                            placeholder="https://your-portfolio.com"
                        />
                    </div>

                    {error && <p className="profile-error" role="alert">{error}</p>}
                    {success && <p className="profile-success" role="status">{success}</p>}

                    <div className="profile-form-footer">
                        <span>Your changes are saved to your Buddy account.</span>
                        <button className="profile-save-button" type="submit" disabled={saving}>
                            {saving
                                ? <LoaderCircle className="profile-spinner" size={17} />
                                : <Save size={17} />}
                            {saving ? 'Saving...' : 'Save changes'}
                        </button>
                    </div>
                </form>

                <section className="profile-academic-card">
                    <div className="profile-section-heading">
                        <div>
                            <h2>Academic details</h2>
                            <p>Your current student information.</p>
                        </div>
                    </div>

                    <div className="profile-academic-grid">
                        <div>
                            <span>College</span>
                            <strong>{profile.collegeName || 'Not set'}</strong>
                        </div>
                        <div>
                            <span>Branch</span>
                            <strong>{profile.branchName || 'Not set'}</strong>
                        </div>
                        <div>
                            <span>Semester</span>
                            <strong>{profile.semesterNumber ?? 'Not set'}</strong>
                        </div>
                        <div>
                            <span>Roll number</span>
                            <strong>{profile.rollNumber || 'Not set'}</strong>
                        </div>
                        <div>
                            <span>CGPA</span>
                            <strong>{profile.cgpa ?? 'Not set'}</strong>
                        </div>
                        <div>
                            <span>Graduation year</span>
                            <strong>{profile.graduationYear ?? 'Not set'}</strong>
                        </div>
                    </div>
                </section>

                <div className="profile-external-links">
                    {form.linkedinUrl && (
                        <a href={form.linkedinUrl} target="_blank" rel="noreferrer">
                            LinkedIn <ExternalLink size={14} />
                        </a>
                    )}
                    {form.githubUrl && (
                        <a href={form.githubUrl} target="_blank" rel="noreferrer">
                            GitHub <ExternalLink size={14} />
                        </a>
                    )}
                    {form.portfolioUrl && (
                        <a href={form.portfolioUrl} target="_blank" rel="noreferrer">
                            Portfolio <ExternalLink size={14} />
                        </a>
                    )}
                </div>
            </div>
        </main>
    )
}
