import { MagicalShiningBook } from './MagicalShiningBook'
const links = [
    { to: '/dashboard', label: 'Dashboard', icon: '\u2302', end: true },
    { to: '/academics', label: 'Academics', icon: '\u25A4' },
    { to: '/skills', label: 'Career Roadmaps', icon: '\u25C7' },
    { to: '/revisions', label: 'Revisions', icon: '\u21BB', end: true },
    { to: '/explore', label: 'Explore', icon: '\u2315' },
    { to: '/settings/preferences', label: 'Settings', icon: '\u2699' },
]
import { NavLink, Link } from 'react-router-dom'
import { useEffect, useState } from 'react'

import { getMyTasks, createTask, updateTaskStatus, deleteTaskById } from '../services/taskService'


import { getAcademicProgressSummary } from '../services/academicService'

import { getMySkills } from '../services/skillService'
import DashboardGoalProgress from './DashboardGoalProgress'
import './Dashboard.css'

const stats = [
    {
        label: 'Academic Progress',
        value: '68%',
        detail: 'Keep moving forward',
        progress: 68,
        icon: 'A',
        target: 'academics',
    },
    {
        label: 'Skills Developed',
        value: '12',
        detail: 'Skills in your roadmap',
        progress: 60,
        icon: 'S',
        target: 'roadmaps',
    },
        {
            label: 'Tasks Completed',
            value: '0/3',
            detail: 'Your current tasks',
            progress: 0,
            icon: 'T',
            target: 'today-tasks',
                },
        ]

const shortcuts = [
    {
        number: '01',
        title: 'College Academics',
        description: 'Subjects, topics and revision',
        target: 'academics',
    },
    {
        number: '02',
        title: 'Career Roadmaps',
        description: 'Build skills step by step',
        target: 'roadmaps',
    },
    {
        number: '03',
        title: 'Projects & Experience',
        description: 'Turn knowledge into practice',
        target: 'projects',
    },
    {
        number: '04',
        title: 'Opportunities',
        description: 'Discover your next opportunity',
        target: 'opportunities',
    },
]

function formatTaskDate(dateString) {
    if (!dateString || dateString === 'No due date') {
        return 'No due date'
    }

    const today = new Date()
    const tomorrow = new Date(today)

    tomorrow.setDate(today.getDate() + 1)

    const toLocalDate = (date) =>
        `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`

    if (dateString === toLocalDate(today)) return 'Today'
    if (dateString === toLocalDate(tomorrow)) return 'Tomorrow'

    const [year, month, day] = dateString.split('-').map(Number)

    return new Date(year, month - 1, day).toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
    })
}

function AnimatedProgressRing({ progress }) {
    const [displayProgress, setDisplayProgress] = useState(0)

    useEffect(() => {
        let frame
        let startTime = null
        const duration = 2500
        const target = Math.max(0, Math.min(100, Number(progress) || 0))

        setDisplayProgress(0)

        const animate = (timestamp) => {
            if (startTime === null) startTime = timestamp

            const elapsed = timestamp - startTime
            const fraction = Math.min(elapsed / duration, 1)
            const eased = 1 - Math.pow(1 - fraction, 3)

            setDisplayProgress(Math.round(target * eased))

            if (fraction < 1) {
                frame = requestAnimationFrame(animate)
            }
        }

        frame = requestAnimationFrame(animate)

        return () => cancelAnimationFrame(frame)
    }, [progress])

    const circumference = 2 * Math.PI * 40

    return (
        <svg
            viewBox="0 0 100 100"
            role="img"
            aria-label={`${displayProgress}% progress`}
        >
            <circle
                className="dashboard-progress-track-circle"
                cx="50"
                cy="50"
                r="40"
            />
            <circle
                className="dashboard-progress-value-circle"
                cx="50"
                cy="50"
                r="40"
                style={{
                    strokeDasharray: circumference,
                    strokeDashoffset:
                        circumference * (1 - displayProgress / 100),
                }}
            />
            <text
                x="50"
                y="50"
                textAnchor="middle"
                dominantBaseline="central"
            >
                {displayProgress}%
            </text>
        </svg>
    )
}
function Dashboard() {
    const getStoredValue = (key) =>
        localStorage.getItem(key) || sessionStorage.getItem(key)

    const getGreeting = () => {
        const hour = new Date().getHours()

        if (hour < 12) return 'Good morning'
        if (hour < 17) return 'Good afternoon'
        return 'Good evening'
    }

    const [lastName, setLastName] = useState(
        getStoredValue('buddyLastName') || ''
    )
    const [profilePictureUrl, setProfilePictureUrl] = useState('')
    const [profileImageFailed, setProfileImageFailed] = useState(false)

    const initials = (
        (getStoredValue('buddyFirstName') || 'Student').trim().charAt(0) +
        (getStoredValue('buddyLastName') || '').trim().charAt(0)
    ).toUpperCase() || 'S'

    useEffect(() => {
        const controller = new AbortController()
        const token = getStoredValue('buddyAccessToken')

        if (!token) return () => controller.abort()

        fetch('/api/students/me', {
            headers: { Authorization: `Bearer ${token}` },
            signal: controller.signal,
        })
            .then((response) => {
                if (!response.ok) throw new Error('Profile unavailable')
                return response.json()
            })
            .then((data) => {
                setProfilePictureUrl(data.profilePictureUrl || '')
                setProfileImageFailed(false)
            })
            .catch((error) => {
                if (error.name !== 'AbortError') {
                    setProfilePictureUrl('')
                }
            })

        return () => controller.abort()
    }, [])
    const [firstName, setFirstName] = useState(
        getStoredValue('buddyFirstName') || 'Student'
    )

    const [greeting, setGreeting] = useState(getGreeting)
    const [showAddTask, setShowAddTask] = useState(false)
    const [newTaskTitle, setNewTaskTitle] = useState('')
    const [newTaskDueDate, setNewTaskDueDate] = useState('')
    const [backendTasks, setBackendTasks] = useState(null)
    const [academicProgress, setAcademicProgress] = useState(null)
    const [studentSkills, setStudentSkills] = useState(null)
    const [tasksLoading, setTasksLoading] = useState(true)
    const [tasksError, setTasksError] = useState('')
    const [taskActionError, setTaskActionError] = useState('')


// Fetch tasks from Spring Boot
useEffect(() => {
    const controller = new AbortController()

    async function loadBackendTasks() {
        try {
            const data = await getMyTasks(controller.signal)

            const formattedTasks = data.map((task) => ({
                id: task.id,
                title: task.title,
                category: task.milestoneId === null
                    ? 'Personal Task'
                    : 'Milestone Task',
                date: task.dueDate ?? 'No due date',
                completed: task.status === 'COMPLETED',
                status: task.status,
                priority: task.priority,
                milestoneId: task.milestoneId,
            }))


            setBackendTasks(formattedTasks)
        }  catch (error) {
              if (error.name !== 'AbortError') {
                  console.error('Failed to load backend tasks:', error)
                  setTasksError(error.message || 'Unable to load tasks.')
              }
          } finally {
              if (!controller.signal.aborted) {
                  setTasksLoading(false)
              }
          }
    }

    loadBackendTasks()

    return () => controller.abort()
}, [])

    const toggleTask = async (taskId) => {
        setTaskActionError('')
        const task = backendTasks?.find((item) => item.id === taskId)

        if (!task) return

        const newStatus = task.completed ? 'TODO' : 'COMPLETED'

        try {
            const updatedTask = await updateTaskStatus(taskId, newStatus)

            setBackendTasks((currentTasks) =>
                currentTasks.map((item) =>
                    item.id === taskId
                        ? {
                              ...item,
                              completed: updatedTask.status === 'COMPLETED',
                              status: updatedTask.status,
                          }
                        : item
                )
            )
        } catch (error) {
            console.error('Failed to update task:', error)
            setTaskActionError(error.message || 'Unable to update task.')
        }
    }

const addTask = async (event) => {
    setTaskActionError('')
    event.preventDefault()

    const title = newTaskTitle.trim()
    if (!title) return

    try {
        const savedTask = await createTask({
            title,
            priority: 'MEDIUM',
            dueDate: newTaskDueDate || null,
        })

        const formattedTask = {
            id: savedTask.id,
            title: savedTask.title,
            category: 'Personal Task',
            date: savedTask.dueDate ?? 'No due date',
            completed: savedTask.status === 'COMPLETED',
            status: savedTask.status,
            priority: savedTask.priority,
            milestoneId: savedTask.milestoneId,
        }

        setBackendTasks((currentTasks) => [
            ...(currentTasks ?? []),
            formattedTask,
        ])

        setNewTaskTitle('')
        setNewTaskTitle('')
        setShowAddTask(false)
    } catch (error) {
        console.error('Failed to add task:', error)
        setTaskActionError(error.message || 'Unable to create task.')
    }
}

const deleteTask = async (taskId) => {
    setTaskActionError('')
    const task = backendTasks?.find((item) => item.id === taskId)

    if (!task) return

    try {
        await deleteTaskById(taskId)

        setBackendTasks((currentTasks) =>
            currentTasks.filter((item) => item.id !== taskId)
        )
    } catch (error) {
        console.error('Failed to delete task:', error)
        setTaskActionError(error.message || 'Unable to delete task.')
    }
}

    const displayedTasks = backendTasks ?? []

    const upcomingDeadlines = displayedTasks
        .filter(task =>
            task.date &&
            task.date !== 'No due date' &&
            task.status !== 'COMPLETED' &&
            task.status !== 'CANCELLED'
        )
        .sort((a, b) => a.date.localeCompare(b.date))
        .slice(0, 5)
    const completedTaskCount = displayedTasks.filter(
        (task) => task.completed
    ).length

const completedSkillCount = (studentSkills ?? []).filter(
    (skill) => skill.status === 'COMPLETED'
).length
const dashboardStats = stats.map((stat) =>
    stat.label === 'Tasks Completed'
        ? {
              ...stat,
              value: `${completedTaskCount}/${displayedTasks.length}`,
              detail: 'Your current tasks',
              progress: displayedTasks.length
                  ? Math.round(
                        (completedTaskCount / displayedTasks.length) * 100
                    )
                  : 0,
          }
        : stat.label === 'Skills Developed'
        ? {
              ...stat,
              value: studentSkills === null
                  ? 'Loading...'
                  : `${completedSkillCount}`,
              detail: studentSkills === null
                  ? 'Loading student skills'
                  : `${completedSkillCount}/${studentSkills.length} skills completed`,
              progress: studentSkills?.length
                  ? Math.round(
                        (completedSkillCount / studentSkills.length) * 100
                    )
                  : 0,
          }
        : stat.label === 'Academic Progress'
        ? {
              ...stat,
              value: academicProgress === null
                  ? 'Loading...'
                  : `${Math.round(academicProgress.progressPercentage)}%`,
              detail: academicProgress === null
                  ? 'Loading academic progress'
                  : academicProgress.totalTopics === 0
                      ? 'No curriculum available'
                      : `${academicProgress.completedTopics}/${academicProgress.totalTopics} topics completed`,
              progress: academicProgress?.progressPercentage ?? 0,
          }
        : stat
)

    // Fetch student skills from Spring Boot
    useEffect(() => {
        const controller = new AbortController()

        async function loadStudentSkills() {
            try {
                const data = await getMySkills(controller.signal)

                if (!controller.signal.aborted) {
                    setStudentSkills(data)
                }
            } catch (error) {
                if (!controller.signal.aborted) {
                    console.error('Failed to fetch student skills:', error)
                }
            }
        }

        loadStudentSkills()

        return () => controller.abort()
    }, [])
    // Fetch academic progress from Spring Boot
    useEffect(() => {
        const controller = new AbortController()

        async function loadAcademicProgress() {
            try {
                const data = await getAcademicProgressSummary(controller.signal)
                if (!controller.signal.aborted) {
                    setAcademicProgress(data)
                }
            } catch (error) {
                if (!controller.signal.aborted) {
                    console.error('Academic progress fetch failed:', error)
                }
            }
        }

        loadAcademicProgress()

        return () => controller.abort()
    }, [])
    // Keep the greeting and displayed user information updated.
    useEffect(() => {
        const updateGreeting = () => {
            setGreeting(getGreeting())
        }

        const syncUser = () => {
            setFirstName(
                getStoredValue('buddyFirstName') || 'Student'
            )
        }

        window.addEventListener('buddy-auth-changed', syncUser)
        window.addEventListener('storage', syncUser)

        const timer = window.setInterval(updateGreeting, 60_000)

        return () => {
            window.removeEventListener('buddy-auth-changed', syncUser)
            window.removeEventListener('storage', syncUser)
            window.clearInterval(timer)
        }
    }, [])

    // Make the blue profile wave follow the pointer.
    useEffect(() => {
        const illustration = document.querySelector(
            '.dashboard-welcome-mark'
        )
        const wave = illustration?.querySelector(
            '.dashboard-profile-wave'
        )

        if (!illustration || !wave) return

        const handlePointerMove = (event) => {
            const rect = illustration.getBoundingClientRect()

            const x = (event.clientX - rect.left) / rect.width - 0.5
            const y = (event.clientY - rect.top) / rect.height - 0.5

            wave.style.transform =
                `translate(${x * 22}px, ${y * 18}px) rotate(-12deg)`
        }

        const resetWave = () => {
            wave.style.transform = 'translate(0, 0) rotate(-12deg)'
        }

        illustration.addEventListener('pointermove', handlePointerMove)
        illustration.addEventListener('pointerleave', resetWave)

        return () => {
            illustration.removeEventListener('pointermove', handlePointerMove)
            illustration.removeEventListener('pointerleave', resetWave)
        }
    }, [])

    const scrollToSection = (target) => {
        document.getElementById(target)?.scrollIntoView({
            behavior: 'smooth',
            block: 'start',
        })
    }

    return (
        <main
    className="dashboard-page"
    onPointerMove={(event) => {
        event.currentTarget.style.setProperty('--wave-x', `${event.clientX}px`)
        event.currentTarget.style.setProperty('--wave-y', `${event.clientY}px`)
    }}
>

            <section className="dashboard-main">
                <header className="dashboard-topbar">
                    <div>
                        <p className="dashboard-breadcrumb">
                            WORKSPACE / OVERVIEW
                        </p>
                        <h1>Your Dashboard</h1>
                    </div>

                    <a href="/login" className="dashboard-account-link">
                        Account
                        <span aria-hidden="true">{'\u2197'}</span>
                    </a>
                </header>

                <section className="dashboard-welcome">
                    <div className="dashboard-magical-book-bg" aria-hidden="true">
                        <MagicalShiningBook
                            isPlaying={true}
                            flipSpeed={1}
                            glowIntensity={1}
                            starDensity={1}
                            enableSigning={true}
                        />
                    </div>
                    <div className="dashboard-welcome-copy">
                        <p className="dashboard-eyebrow">
                            YOUR JOURNEY CONTINUES
                        </p>

                        <h2>
                            <span className="greeting-main">
                                {greeting}, {firstName}!
                            </span>

                            <span className="greeting-tagline">
                                Let's make today count.
                            </span>
                        </h2>

                        <p>
                            Keep your academics, career goals, and personal
                            progress moving in the right direction.
                        </p>

                        <a
                            href="#today-tasks"
                            className="dashboard-primary-button"
                        >
                            View today's tasks{' '}
                            <span aria-hidden="true">{'\u2192'}</span>
                        </a>
                    </div>

                    <div
                        className="dashboard-welcome-mark"
                        role="img"
                        aria-label="Student profile illustration with an interactive blue wave"
                    >
                        <div className="dashboard-mark-ring dashboard-mark-ring-one" />
                        <div className="dashboard-mark-ring dashboard-mark-ring-two" />

                        <div className="dashboard-dynamic-avatar">
    {profilePictureUrl && !profileImageFailed ? (
        <img
            src={profilePictureUrl}
            alt={`${firstName} ${lastName}`.trim()}
            onError={() => setProfileImageFailed(true)}
        />
    ) : (
        <span>{initials}</span>
    )}
</div>
                    </div>
                </section>

                {/* Interactive progress cards */}
                <section
                    className="dashboard-stats"
                    aria-label="Progress overview"
                >
                    {dashboardStats.map((stat) => (
                        <button
                            type="button"
                            className="dashboard-stat-card"
                            key={stat.label}
                            onClick={() => {
    if (stat.label === 'Skills Developed') {
        window.location.assign('/skills')
    } else {
        scrollToSection(stat.target)
    }
}}
                            aria-label={`${stat.label}: ${stat.value}. View details`}
                        >
                            <div className="dashboard-stat-top">


                                <span className="dashboard-stat-trend">
                                    VIEW DETAILS {'\u2192'}
                                </span>
                            </div>

                            <div className="dashboard-stat-progress"
                                style={{
                                    '--progress-blue':
                                        stat.progress > 75 ? '#1E40AF' :
                                        stat.progress > 50 ? '#2563EB' :
                                        stat.progress > 25 ? '#38BDF8' :
                                        '#7DD3FC'
                                }}>
                                <AnimatedProgressRing progress={stat.progress} />
                            </div>

                            <p className="dashboard-stat-label">
                                {stat.label}
                            </p>

                            <strong className="dashboard-stat-value">
                                {stat.value}
                            </strong>

                            <p className="dashboard-stat-detail">
                                {stat.detail}
                            </p>
                        </button>
                    ))}
                </section>

                <DashboardGoalProgress />

                <section
    className="dashboard-deadlines-section"
    aria-label="Upcoming deadlines"
>
    <div className="dashboard-deadlines-heading">
        <div>
            <p className="dashboard-eyebrow">STAY ON TRACK</p>
            <h2>Upcoming Deadlines</h2>
        </div>
        <span className="dashboard-deadlines-count">
            {upcomingDeadlines.length} TASKS
        </span>
    </div>

    {tasksLoading ? (
        <p>Loading deadlines...</p>
    ) : tasksError ? (
        <p role="alert">{tasksError}</p>
    ) : upcomingDeadlines.length === 0 ? (
        <p>No pending deadlines. You're all caught up!</p>
    ) : (
        <div className="dashboard-deadlines-list">
            {upcomingDeadlines.map(task => {
                const today = new Date()
                const localToday = [
                    today.getFullYear(),
                    String(today.getMonth() + 1).padStart(2, '0'),
                    String(today.getDate()).padStart(2, '0'),
                ].join('-')

                const overdue = task.date < localToday

                return (
                    <article
                        className="dashboard-deadline-item"
                        key={task.id}
                    >
                        <div className="dashboard-deadline-info">
                            <strong>{task.title}</strong>
                            <span>{task.category}</span>
                        </div>

                        <div className="dashboard-deadline-meta">
                            <span className="dashboard-deadline-priority">
                                {task.priority}
                            </span>
                            <span
                                className={
                                    overdue
                                        ? 'dashboard-deadline-date overdue'
                                        : 'dashboard-deadline-date'
                                }
                            >
                                {overdue ? 'Overdue: ' : 'Due: '}
                                {formatTaskDate(task.date)}
                            </span>
                        </div>
                    </article>
                )
            })}
        </div>
    )}
</section>
<section className="dashboard-content-grid">
                    <div className="dashboard-panel dashboard-quick-panel">
                        <div className="dashboard-panel-heading">
                            <div>
                                <p className="dashboard-eyebrow">
                                    YOUR WORKSPACE
                                </p>
                                <h2>Pick up where you left off.</h2>
                            </div>

                            <span className="dashboard-panel-count">
                                04 AREAS
                            </span>
                        </div>

                        <div className="dashboard-shortcuts">
                            {shortcuts.map((shortcut) => (
                                <a
                                href={`#${shortcut.target}`}
                                className="dashboard-shortcut"
                                key={shortcut.number}
                                onPointerMove={(event) => {
                                const rect = event.currentTarget.getBoundingClientRect()
                                const x = ((event.clientX - rect.left) / rect.width) * 100
                                const y = ((event.clientY - rect.top) / rect.height) * 100


                                    event.currentTarget.style.setProperty('--pointer-x', `${x}%`)
                                    event.currentTarget.style.setProperty('--pointer-y', `${y}%`)
                                }}


                                >

                                    <span className="dashboard-shortcut-number">
                                        {shortcut.number}
                                    </span>

                                    <span className="dashboard-shortcut-copy">
                                        <strong>{shortcut.title}</strong>
                                        <span>{shortcut.description}</span>
                                    </span>

                                    <span
                                        className="dashboard-shortcut-arrow"
                                        aria-hidden="true"
                                    >
                                        {'\u2192'}
                                    </span>
                                </a>
                            ))}
                        </div>
                    </div>

                    <div
                        className="dashboard-panel dashboard-tasks-panel"
                        id="today-tasks"
                    >
                        <div className="dashboard-panel-heading">
                            <div>
                                <p className="dashboard-eyebrow">
                                    STAY ON TRACK
                                </p>
                                <h2>Today's priorities</h2>
                            </div>


                        </div>

                        <div className="dashboard-add-task">
                            <button
                                type="button"
                                className="dashboard-add-task-btn"
                                onClick={() => setShowAddTask(!showAddTask)}
                            >
                                {showAddTask ? 'Cancel' : '+ Add Task'}
                            </button>

                            {showAddTask && (
                                <form onSubmit={addTask} className="dashboard-add-task-form">
                                    <input
                                        type="text"
                                        value={newTaskTitle}
                                        onChange={(event) => setNewTaskTitle(event.target.value)}
                                        placeholder="What do you need to work on?"
                                        aria-label="New task title"
                                        maxLength={120}
                                        required
                                    />
                                  <input
                                      type="date"
                                      value={newTaskDueDate}
                                      min={new Date().toLocaleDateString('sv-SE')}
                                      onChange={(event) => setNewTaskDueDate(event.target.value)}
                                      aria-label="Task due date"
                                  />

                                    <button type="submit">
                                        Add
                                    </button>
                                </form>
                            )}
                        </div>


                        {taskActionError && (
    <p role="alert" className="dashboard-task-action-error">
        {taskActionError}
        <button type="button" onClick={() => setTaskActionError('')}>
            Dismiss
        </button>
    </p>
)}

<div className="dashboard-task-list">
                            {tasksLoading ? (
                                <p>Loading your tasks...</p>
                            ) : tasksError ? (
                                <p role="alert">{tasksError}</p>
                            ) : displayedTasks.length === 0 ? (
                                <p>No tasks yet. Add your first task to get started.</p>
                            ) : (
                                displayedTasks.map((task) => (
                                <article
                                    className="dashboard-task"
                                    key={task.id ?? task.title}
                                    role="button"
                                    tabIndex={0}
                                    aria-pressed={task.completed}
                                    aria-label={`${task.title}: ${
                                        task.completed ? 'completed' : 'not completed'
                                    }. Press Enter or Space to toggle.`}
                                    onClick={() => toggleTask(task.id ?? task.title)}
                                    onKeyDown={(event) => {
                                        if (event.key === 'Enter' || event.key === ' ') {
                                            event.preventDefault()
                                            toggleTask(task.id ?? task.title)
                                        }
                                    }}
                                    >
                                    <span
                                        className={`dashboard-task-check ${
                                            task.completed ? 'completed' : ''
                                        }`}
                                        aria-hidden="true"
                                    >
                                        {task.completed ? '\u2713' : ''}
                                    </span>

                                    <div className="dashboard-task-copy">
                                        <strong
                                            className={task.completed ? 'task-done' : ''}
                                        >
                                            {task.title}
                                        </strong>

                                        <span>{task.category}</span>
                                    </div>

                                    <span className="dashboard-task-date">
                                        {formatTaskDate(task.date)}
                                    </span>
                                    <button
    type="button"
    className="dashboard-task-delete"
    aria-label={`Delete ${task.title}`}
    title="Delete task"
    onClick={(event) => {
        event.stopPropagation()
        deleteTask(task.id ?? task.title)
    }}
    onKeyDown={(event) => {
        event.stopPropagation()
    }}
>
    {'\u00D7'}
</button>
                                </article>
                            )))}
                        </div>
                    </div>
                </section>

                <footer className="dashboard-footer">
                    <span>{'\u2302'}</span>

                    <span>
                        LEARN. DEVELOP. BUILD. MOVE FORWARD.
                    </span>
                </footer>
            </section>
        </main>
    )
}

export default Dashboard
