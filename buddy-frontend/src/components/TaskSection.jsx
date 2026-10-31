import { useEffect, useState } from 'react'
import {
    getMilestoneTasks,
    createMilestoneTask,
    updateTask,
    deleteTaskById,
} from '../services/taskService'

const initialForm = {
    title: '',
    description: '',
    priority: 'MEDIUM',
    dueDate: '',
}

function TaskSection({ milestoneId }) {
    const [expanded, setExpanded] = useState(false)
    const [tasks, setTasks] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [showForm, setShowForm] = useState(false)
    const [editingTaskId, setEditingTaskId] = useState(null)
    const [form, setForm] = useState(initialForm)
    const [saving, setSaving] = useState(false)
    const [formError, setFormError] = useState('')

    const today = new Date()
    const localToday = [
        today.getFullYear(),
        String(today.getMonth() + 1).padStart(2, '0'),
        String(today.getDate()).padStart(2, '0'),
    ].join('-')

    useEffect(() => {
        if (!expanded) return

        const controller = new AbortController()

        async function loadTasks() {
            setLoading(true)
            setError('')

            try {
                const data = await getMilestoneTasks(
                    milestoneId,
                    controller.signal
                )

                if (!controller.signal.aborted) {
                    setTasks(data)
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

        loadTasks()

        return () => controller.abort()
    }, [milestoneId, expanded])

    async function handleStatusChange(taskId, status) {
        try {
            const updated = await updateTask(taskId, { status })

            setTasks(previous =>
                previous.map(task =>
                    task.id === taskId ? updated : task
                )
            )
        } catch (err) {
            setError(err.message)
        }
    }
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
        setShowForm(false)
        setEditingTaskId(null)
    }

    async function handleDeleteTask(taskId) {
        const confirmed = window.confirm(
            'Are you sure you want to delete this task?'
        )

        if (!confirmed) return

        try {
            await deleteTaskById(taskId)

            setTasks(previous =>
                previous.filter(task => task.id !== taskId)
            )

            if (editingTaskId === taskId) {
                resetForm()
            }
        } catch (err) {
            setError(err.message)
        }
    }
    function handleEditTask(task) {
        setEditingTaskId(task.id)
        setForm({
            title: task.title,
            description: task.description || '',
            priority: task.priority || 'MEDIUM',
            dueDate: task.dueDate || '',
        })
        setFormError('')
        setShowForm(true)
    }

    async function handleCreateTask(event) {
        event.preventDefault()

        const title = form.title.trim()

        if (!title || title.length > 200) {
            setFormError('Title must contain 1 to 200 characters.')
            return
        }

        if (form.dueDate && form.dueDate < localToday) {
            setFormError('Due date cannot be in the past.')
            return
        }

        setSaving(true)
        setFormError('')

        try {
            const payload = {
                title,
                description: form.description,
                priority: form.priority,
                ...(form.dueDate ? { dueDate: form.dueDate } : {}),
            }

            if (editingTaskId !== null) {
                const updated = await updateTask(editingTaskId, payload)

                setTasks(previous =>
                    previous.map(task =>
                        task.id === editingTaskId ? updated : task
                    )
                )
            } else {
                const created = await createMilestoneTask(milestoneId, {
                    ...payload,
                    dueDate: form.dueDate || null,
                })

                setTasks(previous => [...previous, created])
            }

            resetForm()
        } catch (err) {
            setFormError(err.message)
        } finally {
            setSaving(false)
        }
    }

    return (
        <section className="task-section">
            <button
                type="button"
                className="task-toggle"
                aria-expanded={expanded}
                onClick={() => setExpanded(previous => !previous)}
            >
                {expanded ? '− Hide Tasks' : '+ View Tasks'}
            </button>

            {expanded && (
                <div className="task-content">
                    <button
                        type="button"
                        className="task-add-button"
                        onClick={() => {
                            if (showForm) {
                                resetForm()
                            } else {
                                setShowForm(true)
                            }
                        }}
                    >
                        {showForm ? 'Cancel' : '+ Add Task'}
                    </button>

                    {showForm && (
                        <form
                            className="task-form"
                            onSubmit={handleCreateTask}
                        >
                            <label>
                                Task Title
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
                                Priority
                                <select
                                    name="priority"
                                    value={form.priority}
                                    onChange={handleInputChange}
                                >
                                    <option value="LOW">Low</option>
                                    <option value="MEDIUM">Medium</option>
                                    <option value="HIGH">High</option>
                                </select>
                            </label>

                            <label>
                                Due Date (Optional)
                                <input
                                    type="date"
                                    name="dueDate"
                                    min={localToday}
                                    value={form.dueDate}
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
                                className="task-save-button"
                                disabled={saving}
                            >
                                {saving ? 'Saving...' : editingTaskId !== null ? 'Save Changes' : 'Create Task'}
                            </button>
                        </form>
                    )}

                    {loading ? (
                        <p>Loading tasks...</p>
                    ) : error ? (
                        <p role="alert">{error}</p>
                    ) : tasks.length === 0 ? (
                        <p>No tasks added yet.</p>
                    ) : (
                        <div className="task-list">
                            {tasks.map(task => (
                                <article
                                    className="task-item"
                                    key={task.id}
                                >
                                    <div className="task-item-heading">
                                        <strong>{task.title}</strong>
                                        <select
                                            className="task-status-select"
                                            value={task.status}
                                            aria-label={`Status for ${task.title}`}
                                            onChange={event =>
                                                handleStatusChange(
                                                    task.id,
                                                    event.target.value
                                                )
                                            }
                                        >
                                            <option value="TODO">To Do</option>
                                            <option value="IN_PROGRESS">In Progress</option>
                                            <option value="COMPLETED">Completed</option>
                                            <option value="CANCELLED">Cancelled</option>
                                        </select>
                                    </div>

                                    <button
                                        type="button"
                                        className="task-edit-button"
                                        onClick={() => handleEditTask(task)}
                                        disabled={saving}
                                    >
                                        Edit Task
                                    </button>

                                    <button
                                        type="button"
                                        className="task-delete-button"
                                        onClick={() => handleDeleteTask(task.id)}
                                    >
                                        Delete Task
                                    </button>

                                    {task.description && (
                                        <p>{task.description}</p>
                                    )}

                                    <small>
                                        Priority: {task.priority}
                                    </small>

                                    <small>
                                        Due: {task.dueDate || 'Not specified'}
                                    </small>
                                </article>
                            ))}
                        </div>
                    )}
                </div>
            )}
        </section>
    )
}

export default TaskSection
