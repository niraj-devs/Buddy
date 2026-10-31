import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyGoals } from '../services/goalService'
import { getGoalMilestones } from '../services/milestoneService'

function DashboardGoalProgress() {
    const [goals, setGoals] = useState([])
    const [milestonesByGoal, setMilestonesByGoal] = useState({})
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        const controller = new AbortController()

        async function loadProgress() {
            try {
                const goalData = await getMyGoals(controller.signal)

                const activeGoals = goalData.filter(
                    goal => goal.status !== 'CANCELLED'
                )

                const results = await Promise.all(
                    activeGoals.map(async goal => ({
                        goalId: goal.id,
                        milestones: await getGoalMilestones(
                            goal.id,
                            controller.signal
                        ),
                    }))
                )

                if (controller.signal.aborted) return

                setGoals(activeGoals)
                setMilestonesByGoal(
                    Object.fromEntries(
                        results.map(result => [
                            result.goalId,
                            result.milestones,
                        ])
                    )
                )
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

        loadProgress()

        return () => controller.abort()
    }, [])

    const completedGoals = goals.filter(
        goal => goal.status === 'COMPLETED'
    ).length

    const activeGoals = goals.filter(
        goal => goal.status !== 'COMPLETED'
    ).length

    return (
        <section
            className="dashboard-goals-section"
            aria-label="Goal progress"
        >
            <div className="dashboard-goals-heading">
                <div>
                    <p className="dashboard-eyebrow">
                        YOUR PROGRESS
                    </p>
                    <h2>Goals & Milestones</h2>
                </div>

                <Link to="/goals" className="dashboard-goals-link">
                    Manage Goals →
                </Link>
            </div>

            {loading ? (
                <p>Loading goal progress...</p>
            ) : error ? (
                <p role="alert">{error}</p>
            ) : (
                <>
                    <div className="dashboard-goals-summary">
                        <span>Active Goals: {activeGoals}</span>
                        <span>Completed Goals: {completedGoals}</span>
                    </div>

                    {goals.length === 0 ? (
                        <p>No goals created yet.</p>
                    ) : (
                        <div className="dashboard-goals-list">
                            {goals.map(goal => {
                                const milestones =
                                    milestonesByGoal[goal.id] || []

                                const completed = milestones.filter(
                                    milestone =>
                                        milestone.status === 'COMPLETED'
                                ).length

                                const percentage = milestones.length
                                    ? Math.round(
                                        (completed / milestones.length) * 100
                                    )
                                    : 0

                                return (
                                    <article
                                        className="dashboard-goal-card"
                                        key={goal.id}
                                    >
                                        <div className="dashboard-goal-top">
                                            <strong>{goal.title}</strong>
                                            <span>{percentage}%</span>
                                        </div>

                                        <div
                                            className="dashboard-goal-track"
                                            role="progressbar"
                                            aria-label={`${goal.title} milestone progress`}
                                            aria-valuenow={percentage}
                                            aria-valuemin={0}
                                            aria-valuemax={100}
                                        >
                                            <div
                                                className="dashboard-goal-fill"
                                                style={{
                                                    width: `${percentage}%`,
                                                }}
                                            />
                                        </div>

                                        <p>
                                            {completed}/{milestones.length}
                                            {' '}milestones completed
                                        </p>
                                    </article>
                                )
                            })}
                        </div>
                    )}
                </>
            )}
        </section>
    )
}

export default DashboardGoalProgress
