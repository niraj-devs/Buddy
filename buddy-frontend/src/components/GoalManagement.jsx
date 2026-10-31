import { useEffect, useState } from 'react'
import { getMyGoals, createGoal, updateGoal, deleteGoalById } from '../services/goalService'
import MilestoneSection from './MilestoneSection'
import './GoalManagement.css'

function GoalManagement() {
    const [goals, setGoals] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [updatingGoalId, setUpdatingGoalId] = useState(null)

    const handleStatusChange = async (goalId, status) => {
        if (updatingGoalId !== null) return

        setUpdatingGoalId(goalId)
        setError('')

        try {
            const updatedGoal = await updateGoal(goalId, { status })

            setGoals(previous =>
                previous.map(goal =>
                    goal.id === goalId ? updatedGoal : goal
                )
            )
        } catch (err) {
            setError(err.message)
        } finally {
            setUpdatingGoalId(null)
        }
    }
    const [editingGoalId, setEditingGoalId] = useState(null)
    const [deletingGoalId, setDeletingGoalId] = useState(null)

    const handleDeleteGoal = async goal => {
        if (deletingGoalId !== null) return

        const confirmed = window.confirm(
            `Delete "${goal.title}"? This action cannot be undone.`
        )

        if (!confirmed) return

        setDeletingGoalId(goal.id)
        setError('')

        try {
            await deleteGoalById(goal.id)

            setGoals(previous =>
                previous.filter(item => item.id !== goal.id)
            )

            if (editingGoalId === goal.id) {
                resetGoalForm()
            }
        } catch (err) {
            setError(err.message)
        } finally {
            setDeletingGoalId(null)
        }
    }
    const [showForm, setShowForm] = useState(false)
    const [saving, setSaving] = useState(false)
    const [formError, setFormError] = useState('')
    const [form, setForm] = useState({
        title: '',
        description: '',
        goalType: 'ACADEMIC',
        targetDate: '',
    })

    const handleChange = event => {
        const { name, value } = event.target
        setForm(previous => ({ ...previous, [name]: value }))
    }

    const resetGoalForm = () => {
        setForm({
            title: '',
            description: '',
            goalType: 'ACADEMIC',
            targetDate: '',
        })
        setEditingGoalId(null)
        setShowForm(false)
        setFormError('')
    }

    const handleEditGoal = goal => {
        setEditingGoalId(goal.id)
        setForm({
            title: goal.title || '',
            description: goal.description || '',
            goalType: goal.goalType || 'ACADEMIC',
            targetDate: goal.targetDate || '',
        })
        setFormError('')
        setShowForm(true)
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    const handleCreateGoal = async event => {
        event.preventDefault()
        if (saving) return

        const title = form.title.trim()

        if (!title || title.length > 200) {
            setFormError('Title must contain 1–200 characters.')
            return
        }

        setSaving(true)
        setFormError('')

        try {
            const payload = {
                title,
                description: form.description.trim(),
                goalType: form.goalType,
            }

            if (form.targetDate) {
                payload.targetDate = form.targetDate
            }

            if (editingGoalId !== null) {
                const updatedGoal = await updateGoal(editingGoalId, payload)

                setGoals(previous =>
                    previous.map(goal =>
                        goal.id === editingGoalId ? updatedGoal : goal
                    )
                )
            } else {
                const createdGoal = await createGoal({
                    ...payload,
                    targetDate: form.targetDate || null,
                })

                setGoals(previous => [createdGoal, ...previous])
            }

            resetGoalForm()
        } catch (err) {
            setFormError(err.message)
        } finally {
            setSaving(false)
        }
    }

    useEffect(() => {
        const controller = new AbortController()

        async function loadGoals() {
            try {
                const data = await getMyGoals(controller.signal)
                setGoals(data)
            } catch (err) {
                if (!controller.signal.aborted) {
                    setError(err.message)
                }
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false)
                }
            }
        }

        loadGoals()

        return () => controller.abort()
    }, [])

    const totalGoals = goals.length
    const notStarted = goals.filter(
        goal => goal.status === 'NOT_STARTED'
    ).length
    const inProgress = goals.filter(
        goal => goal.status === 'IN_PROGRESS'
    ).length
    const completed = goals.filter(
        goal => goal.status === 'COMPLETED'
    ).length

    return (
        <main className="goal-management">
            <div className="goal-page-heading">
                <div>
                    <p className="goal-eyebrow">MY PLANNING SPACE</p>
                    <h1>Goal Management</h1>
                    <p>Set meaningful goals and track your progress.</p>
                </div>
                <button
                    type="button"
                    className="goal-create-button"
                    onClick={() => {
                        resetGoalForm()
                        setShowForm(true)
                    }}
                >
                    + Create Goal
                </button>
            </div>

            {showForm && (
                <form className="goal-create-form" onSubmit={handleCreateGoal}>
                    <h2>{editingGoalId !== null ? "Edit Goal" : "Create New Goal"}</h2>

                    <label htmlFor="goal-title">Goal Title</label>
                    <input
                        id="goal-title"
                        name="title"
                        value={form.title}
                        onChange={handleChange}
                        maxLength={200}
                        required
                        placeholder="e.g. Master Spring Boot"
                    />

                    <label htmlFor="goal-description">Description</label>
                    <textarea
                        id="goal-description"
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                        rows={3}
                        placeholder="Describe your goal"
                    />

                    <label htmlFor="goal-type">Goal Type</label>
                    <select
                        id="goal-type"
                        name="goalType"
                        value={form.goalType}
                        onChange={handleChange}
                    >
                        {['ACADEMIC', 'CAREER', 'SKILL', 'PROJECT', 'PERSONAL', 'OTHER'].map(type => (
                            <option key={type} value={type}>{type}</option>
                        ))}
                    </select>

                    <label htmlFor="goal-date">Target Date</label>
                    <input
                        id="goal-date"
                        type="date"
                        name="targetDate"
                        value={form.targetDate}
                        onChange={handleChange}
                        min={new Date().toLocaleDateString('en-CA')}
                    />

                    {formError && (
                        <p className="goal-error" role="alert">{formError}</p>
                    )}

                    <div className="goal-form-actions">
                        <button type="submit" disabled={saving}>
                            {saving ? 'Saving...' : editingGoalId !== null ? 'Save Changes' : 'Save Goal'}
                        </button>
                        <button
                            type="button"
                            disabled={saving}
                            onClick={resetGoalForm}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            )}

            {error && (
                <div className="goal-error" role="alert">
                    {error}
                </div>
            )}

            {loading ? (
                <p>Loading your goals...</p>
            ) : (
                <>
                    <div className="goal-stats-grid">
                        <div className="goal-stat-card">
                            <span>Total Goals</span>
                            <strong>{totalGoals}</strong>
                        </div>

                        <div className="goal-stat-card">
                            <span>Not Started</span>
                            <strong>{notStarted}</strong>
                        </div>

                        <div className="goal-stat-card">
                            <span>In Progress</span>
                            <strong>{inProgress}</strong>
                        </div>

                        <div className="goal-stat-card">
                            <span>Completed</span>
                            <strong>{completed}</strong>
                        </div>
                    </div>

                    <section className="goal-list-section">
                        <h2>My Goals</h2>

                        {goals.length === 0 ? (
                            <div className="goal-empty-state">
                                <h3>No goals yet</h3>
                                <p>Your goals will appear here once created.</p>
                            </div>
                        ) : (
                            <div className="goal-cards-grid">
                                {goals.map(goal => (
                                    <article className="goal-card" key={goal.id}>
                                        <div className="goal-card-top">
                                            <span className="goal-type">
                                                {goal.goalType}
                                            </span>
                                            <select
                                                className="goal-status-select"
                                                aria-label={`Status for ${goal.title}`}
                                                value={goal.status}
                                                disabled={updatingGoalId !== null}
                                                onChange={event =>
                                                    handleStatusChange(
                                                        goal.id,
                                                        event.target.value
                                                    )
                                                }
                                            >
                                                <option value="NOT_STARTED">Not Started</option>
                                                <option value="IN_PROGRESS">In Progress</option>
                                                <option value="COMPLETED">Completed</option>
                                                <option value="CANCELLED">Cancelled</option>
                                            </select>
                                        </div>

                                        <h3>{goal.title}</h3>
                                        <p>{goal.description || 'No description provided.'}</p>

                                        <div className="goal-card-actions">
                                            <button
                                                type="button"
                                                className="goal-edit-button"
                                                disabled={deletingGoalId === goal.id}
                                                onClick={() => handleEditGoal(goal)}
                                            >
                                                Edit Goal
                                            </button>

                                            <button
                                                type="button"
                                                className="goal-delete-button"
                                                disabled={deletingGoalId === goal.id}
                                                onClick={() => handleDeleteGoal(goal)}
                                            >
                                                {deletingGoalId === goal.id
                                                    ? 'Deleting...'
                                                    : 'Delete'}
                                            </button>
                                        </div>

                                        <MilestoneSection goalId={goal.id} />

                                        <div className="goal-card-footer">
                                            Target: {goal.targetDate || 'Not specified'}
                                        </div>
                                    </article>
                                ))}
                            </div>
                        )}
                    </section>
                </>
            )}
        </main>
    )
}

export default GoalManagement
