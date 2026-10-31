import { Link } from 'react-router-dom'
import './SkillsManagement.css'
import { useEffect, useState } from 'react'
import { getMySkills, updateSkillProgress, getSkillUnlockStatus } from '../services/skillService'

function SkillsManagement() {
    const [skills, setSkills] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [progressInputs, setProgressInputs] = useState({})
    const [savingSkillId, setSavingSkillId] = useState(null)
    const [updateError, setUpdateError] = useState('')
    const [unlockStatuses, setUnlockStatuses] = useState({})

    const handleUpdateProgress = async (skill) => {
        const progress = Number(
            progressInputs[skill.skillId] ?? skill.progressPercentage
        )

        if (!Number.isInteger(progress) || progress < 0 || progress > 100) {
            setUpdateError('Enter a whole number between 0 and 100.')
            return
        }

        setSavingSkillId(skill.skillId)
        setUpdateError('')

        try {
            await updateSkillProgress(skill.skillId, progress)
            const updatedSkills = await getMySkills()

            const results = await Promise.allSettled(
                updatedSkills.map((item) =>
                    getSkillUnlockStatus(item.skillId)
                )
            )

            const statuses = {}

            updatedSkills.forEach((item, index) => {
                const result = results[index]

                statuses[item.skillId] =
                    result.status === 'fulfilled'
                        ? result.value
                        : { unlocked: false, checkFailed: true }
            })

            setUnlockStatuses(statuses)
            setSkills(updatedSkills)
        } catch (err) {
            setUpdateError(err.message)
        } finally {
            setSavingSkillId(null)
        }
    }

    useEffect(() => {
        const controller = new AbortController()

        getMySkills(controller.signal)
            .then(async (loadedSkills) => {
                const results = await Promise.allSettled(
                    loadedSkills.map((skill) =>
                        getSkillUnlockStatus(skill.skillId, controller.signal)
                    )
                )

                if (controller.signal.aborted) return

                const statuses = {}
                loadedSkills.forEach((skill, index) => {
                    const result = results[index]
                    statuses[skill.skillId] =
                        result.status === 'fulfilled'
                            ? result.value
                            : { unlocked: false, checkFailed: true }
                })

                setUnlockStatuses(statuses)
                setSkills(loadedSkills)
            })
            .catch((err) => {
                if (!controller.signal.aborted) {
                    setError(err.message)
                }
            })
            .finally(() => {
                if (!controller.signal.aborted) {
                    setLoading(false)
                }
            })

        return () => controller.abort()
    }, [])

    if (loading) return <p>Loading skills...</p>
    if (error) return <p role="alert">{error}</p>

    return (
        <main className="skills-management">
<h1>Skills Management</h1>
            <p>Track your learning progress.</p>

            {updateError && <p role="alert">{updateError}</p>}

            {skills.length === 0 && <p>No skills available.</p>}

            <div className="skills-grid">
            {skills.map((skill) => (
                <section key={skill.skillId} className="skill-card">
                    <h3>{skill.skillName}</h3>
                    <p>Status: {skill.status}</p>
                    <p>
                        Access:{' '}
                        {unlockStatuses[skill.skillId]?.checkFailed
                            ? 'Unable to verify prerequisites'
                            : unlockStatuses[skill.skillId]?.unlocked
                              ? 'Unlocked'
                              : 'Locked'}
                    </p>
                    {unlockStatuses[skill.skillId]?.unlocked === false &&
                        !unlockStatuses[skill.skillId]?.checkFailed && (
                            <p>
                                Complete these prerequisites first:{' '}
{unlockStatuses[skill.skillId].unmetPrerequisites.join(', ')}
                            </p>
                        )}
                    <p>Progress: {skill.progressPercentage}%</p>
                    <progress
                        value={Number(skill.progressPercentage)}
                        max="100"
                    />

                    <div>
                        <label htmlFor={`progress-${skill.skillId}`}>
                            Update progress (%)
                        </label>
                        <input
                            id={`progress-${skill.skillId}`}
                            type="number"
                            min="0"
                            max="100"
                            step="1"
                            value={
                                progressInputs[skill.skillId] ??
                                skill.progressPercentage
                            }
                            disabled={skill.status === 'COMPLETED' || unlockStatuses[skill.skillId]?.unlocked !== true}
                            onChange={(event) =>
                                setProgressInputs((current) => ({
                                    ...current,
                                    [skill.skillId]: event.target.value,
                                }))
                            }
                        />
                        <button
                            type="button"
                            disabled={
                                savingSkillId !== null ||
                                skill.status === 'COMPLETED' ||
                                unlockStatuses[skill.skillId]?.unlocked !== true
                            }
                            onClick={() => handleUpdateProgress(skill)}
                        >
                            {savingSkillId === skill.skillId
                                ? 'Saving...'
                                : 'Update Progress'}
                        </button>
                    </div>
                </section>
            ))}
            </div>
        </main>
    )
}

export default SkillsManagement
