import { useEffect, useState } from 'react'
import {
    getGoalMilestones,
    createMilestone,
    updateMilestone,
    deleteMilestoneById,
} from '../services/milestoneService'
import TaskSection from './TaskSection'

const initialForm = {
    title: '',
    description: '',
    sequenceNumber: '',
    targetDate: '',
}

function MilestoneSection({ goalId }) {
    const [expanded, setExpanded] = useState(false)
    const [milestones, setMilestones] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [showForm, setShowForm] = useState(false)
    const [form, setForm] = useState(initialForm)
    const [saving, setSaving] = useState(false)
    const [editingMilestoneId, setEditingMilestoneId] = useState(null)
    const [formError, setFormError] = useState('')
    const [updatingMilestoneId, setUpdatingMilestoneId] = useState(null)
    const [statusError, setStatusError] = useState('')
    const [deletingMilestoneId, setDeletingMilestoneId] = useState(null)

    useEffect(() => {
        if (!expanded) return

        const controller = new AbortController()

        async function loadMilestones() {
            setLoading(true)
            setError('')

            try {
                const data = await getGoalMilestones(
                    goalId,
                    controller.signal
                )

                if (!controller.signal.aborted) {
                    setMilestones(
                        [...data].sort(
                            (a, b) => a.sequenceNumber - b.sequenceNumber
                        )
                    )
                }
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

        loadMilestones()

        return () => controller.abort()
    }, [goalId, expanded])

    function handleInputChange(event) {
        const { name, value } = event.target

        setForm(previous => ({
            ...previous,
            [name]: value,
        }))
    }

    function resetForm() {
        setForm(initialForm)
        setFormError('')
        setEditingMilestoneId(null)
        setShowForm(false)
    }

    function handleEditMilestone(milestone) {
        setEditingMilestoneId(milestone.id)
        setForm({
            title: milestone.title,
            description: milestone.description || '',
            sequenceNumber: String(milestone.sequenceNumber),
            targetDate: milestone.targetDate || '',
        })
        setFormError('')
        setShowForm(true)
    }

    async function handleCreateMilestone(event) {
        event.preventDefault()

        const title = form.title.trim()
        const sequenceNumber = Number(form.sequenceNumber)

        if (!title || title.length > 200) {
            setFormError('Title must contain 1 to 200 characters.')
            return
        }

        if (!Number.isSafeInteger(sequenceNumber) || sequenceNumber < 1) {
            setFormError('Sequence number must be a positive integer.')
            return
        }

        if (form.targetDate && form.targetDate < new Date().toLocaleDateString('en-CA')) {
            setFormError('Target date cannot be in the past.')
            return
        }

        setSaving(true)
        setFormError('')

        try {
            const payload = {
                title,
                description: form.description,
                sequenceNumber,
                ...(form.targetDate ? { targetDate: form.targetDate } : {}),
            }

            if (editingMilestoneId !== null) {
                const updated = await updateMilestone(
                    editingMilestoneId,
                    payload
                )

                setMilestones(previous =>
                    previous
                        .map(milestone =>
                            milestone.id === editingMilestoneId
                                ? updated
                                : milestone
                        )
                        .sort((a, b) => a.sequenceNumber - b.sequenceNumber)
                )
            } else {
                const created = await createMilestone(goalId, {
                    ...payload,
                    targetDate: form.targetDate || null,
                })

                setMilestones(previous =>
                    [...previous, created].sort(
                        (a, b) => a.sequenceNumber - b.sequenceNumber
                    )
                )
            }

            resetForm()
        } catch (err) {
            setFormError(err.message)
        } finally {
            setSaving(false)
        }
    }

    async function handleStatusChange(milestoneId, status) {
        setUpdatingMilestoneId(milestoneId)
        setStatusError('')

        try {
            const updated = await updateMilestone(milestoneId, {
                status,
            })

            setMilestones(previous =>
                previous.map(milestone =>
                    milestone.id === milestoneId ? updated : milestone
                )
            )
        } catch (err) {
            setStatusError(err.message)
        } finally {
            setUpdatingMilestoneId(null)
        }
    }
    async function handleDeleteMilestone(milestone) {
        const confirmed = window.confirm(
            `Delete milestone "${milestone.title}"? This cannot be undone.`
        )

        if (!confirmed) return

        setDeletingMilestoneId(milestone.id)
        setStatusError('')

        try {
            await deleteMilestoneById(milestone.id)

            setMilestones(previous =>
                previous.filter(item => item.id !== milestone.id)
            )

            if (editingMilestoneId === milestone.id) {
                resetForm()
            }
        } catch (err) {
            setStatusError(err.message)
        } finally {
            setDeletingMilestoneId(null)
        }
    }
    return (
        <section className="milestone-section">
            <button
                type="button"
                className="milestone-toggle"
                aria-expanded={expanded}
                onClick={() => setExpanded(previous => !previous)}
            >
                {expanded ? '− Hide Milestones' : '+ View Milestones'}
            </button>

            {expanded && (
                <div className="milestone-content">
                    {statusError && (
                        <p className="goal-error" role="alert">
                            {statusError}
                        </p>
                    )}
                    <button
                        type="button"
                        className="milestone-add-button"
                        onClick={() => {
                            if (showForm) {
                                resetForm()
                            } else {
                                resetForm()
                                setShowForm(true)
                            }
                        }}
                    >
                        {showForm ? 'Cancel' : '+ Add Milestone'}
                    </button>

                    {showForm && (
                        <form
                            className="milestone-form"
                            onSubmit={handleCreateMilestone}
                        >
                            <label>
                                Milestone Title
                                <input
                                    name="title"
                                    value={form.title}
                                    onChange={handleInputChange}
                                    maxLength={200}
                                    required
                                />
                            </label>

                            <label>
                                Description
                                <textarea
                                    name="description"
                                    value={form.description}
                                    onChange={handleInputChange}
                                    rows={3}
                                />
                            </label>

                            <label>
                                Sequence Number
                                <input
                                    type="number"
                                    name="sequenceNumber"
                                    min="1"
                                    step="1"
                                    value={form.sequenceNumber}
                                    onChange={handleInputChange}
                                    required
                                />
                            </label>

                            <label>
                                Target Date (Optional)
                                <input
                                    type="date"
                                    name="targetDate"
                                    min={new Date().toLocaleDateString('en-CA')}
                                    value={form.targetDate}
                                    onChange={handleInputChange}
                                />
                            </label>

                            {formError && (
                                <p className="goal-error" role="alert">
                                    {formError}
                                </p>
                            )}

                            <button
                                type="submit"
                                className="milestone-save-button"
                                disabled={saving}
                            >
                                {saving ? 'Saving...' : editingMilestoneId !== null ? 'Save Changes' : 'Create Milestone'}
                            </button>
                        </form>
                    )}

                    {loading ? (
                        <p>Loading milestones...</p>
                    ) : error ? (
                        <p className="goal-error" role="alert">{error}</p>
                    ) : milestones.length === 0 ? (
                        <p>No milestones added yet.</p>
                    ) : (
                        <div className="milestone-list">
                            {milestones.map(milestone => (
                                <article
                                    className="milestone-item"
                                    key={milestone.id}
                                >
                                    <div className="milestone-item-heading">
                                        <strong>
                                            {milestone.sequenceNumber}. {milestone.title}
                                        </strong>

                                        <select
                                            className="milestone-status-select"
                                            aria-label={`Status for ${milestone.title}`}
                                            value={milestone.status}
                                            disabled={updatingMilestoneId === milestone.id}
                                            onChange={event =>
                                                handleStatusChange(
                                                    milestone.id,
                                                    event.target.value
                                                )
                                            }
                                        >
                                            <option value="NOT_STARTED">Not Started</option>
                                            <option value="IN_PROGRESS">In Progress</option>
                                            <option value="COMPLETED">Completed</option>
                                        </select>
                                    </div>

                                    {milestone.description && (
                                        <p>{milestone.description}</p>
                                    )}

                                    <small>
                                        Target: {milestone.targetDate || 'Not specified'}
                                    </small>

                                    <TaskSection milestoneId={milestone.id} />

                                    <div className="milestone-actions">
                                        <button
                                            type="button"
                                            className="milestone-edit-button"
                                            disabled={saving || updatingMilestoneId === milestone.id}
                                            onClick={() => handleEditMilestone(milestone)}
                                        >
                                            Edit
                                        </button>

                                        <button
                                            type="button"
                                            className="milestone-delete-button"
                                            disabled={
                                                deletingMilestoneId === milestone.id ||
                                                saving ||
                                                updatingMilestoneId === milestone.id
                                            }
                                            onClick={() => handleDeleteMilestone(milestone)}
                                        >
                                            {deletingMilestoneId === milestone.id
                                                ? 'Deleting...'
                                                : 'Delete'}
                                        </button>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}
                </div>
            )}
        </section>
    )
}

export default MilestoneSection
