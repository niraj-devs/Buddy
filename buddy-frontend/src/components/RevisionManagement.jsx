import { useEffect, useState } from 'react'
import RevisionCard from './RevisionCard'
import StudentWorkspaceLayout from './StudentWorkspaceLayout'
import './RevisionManagement.css'
import {
    getColleges,
    getBranches,
    getSemesters,
    getSubjects,
    getUnits,
    getTopics,
} from '../services/academicService'
import {
    completeRevision,
    markRevisionMissed,
    scheduleRevision,
    getMyRevisions,
    getTodaysRevisions,
    getUpcomingRevisions,
} from '../services/revisionService'

function RevisionManagement() {
    const [revisions, setRevisions] = useState([])
    const [today, setToday] = useState([])
    const [upcoming, setUpcoming] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [topicId, setTopicId] = useState('')
    const [colleges, setColleges] = useState([])
    const [branches, setBranches] = useState([])
    const [semesters, setSemesters] = useState([])
    const [subjects, setSubjects] = useState([])
    const [units, setUnits] = useState([])
    const [topics, setTopics] = useState([])

    const [selectedCollege, setSelectedCollege] = useState('')
    const [selectedBranch, setSelectedBranch] = useState('')
    const [selectedSemester, setSelectedSemester] = useState('')
    const [selectedSubject, setSelectedSubject] = useState('')
    const [selectedUnit, setSelectedUnit] = useState('')
    const [scheduledDate, setScheduledDate] = useState('')
    const [scheduling, setScheduling] = useState(false)
    const [processingRevisionId, setProcessingRevisionId] = useState(null)
    const [success, setSuccess] = useState('')

    useEffect(() => {
        let active = true

        getColleges()
            .then(data => {
                if (active) setColleges(data)
            })
            .catch(err => {
                if (active) setError(err.message)
            })

        return () => {
            active = false
        }
    }, [])

    useEffect(() => {
        let active = true

        Promise.allSettled([
            getMyRevisions(),
            getTodaysRevisions(),
            getUpcomingRevisions(),
        ])
            .then(results => {
                if (!active) return

                const [all, todays, future] = results
                const errors = []

                if (all.status === 'fulfilled') {
                    setRevisions(all.value)
                } else {
                    errors.push('Failed to load all revisions.')
                }

                if (todays.status === 'fulfilled') {
                    setToday(todays.value)
                } else {
                    errors.push("Failed to load today's revisions.")
                }

                if (future.status === 'fulfilled') {
                    setUpcoming(future.value)
                } else {
                    errors.push('Failed to load upcoming revisions.')
                }

                if (errors.length > 0) {
                    setError(errors.join(' '))
                }
            })
            .finally(() => {
                if (active) setLoading(false)
            })

        return () => {
            active = false
        }
    }, [])

    const handleCollegeChange = async (collegeId) => {
        setSelectedCollege(collegeId)

        setSelectedBranch('')
        setSelectedSemester('')
        setSelectedSubject('')
        setSelectedUnit('')
        setTopicId('')

        setBranches([])
        setSemesters([])
        setSubjects([])
        setUnits([])
        setTopics([])
        setError('')

        if (!collegeId) return

        try {
            const data = await getBranches(collegeId)
            setBranches(data)
        } catch (err) {
            setError(err.message)
        }
    }
    const handleBranchChange = async (branchId) => {
        setSelectedBranch(branchId)

        setSelectedSemester('')
        setSelectedSubject('')
        setSelectedUnit('')
        setTopicId('')

        setSemesters([])
        setSubjects([])
        setUnits([])
        setTopics([])
        setError('')

        if (!branchId) return

        try {
            const data = await getSemesters(branchId)
            setSemesters(data)
        } catch (err) {
            setError(err.message)
        }
    }
    const handleSemesterChange = async (semesterId) => {
        setSelectedSemester(semesterId)

        setSelectedSubject('')
        setSelectedUnit('')
        setTopicId('')

        setSubjects([])
        setUnits([])
        setTopics([])
        setError('')

        if (!semesterId) return

        try {
            const data = await getSubjects(semesterId)
            setSubjects(data)
        } catch (err) {
            setError(err.message)
        }
    }
    const handleSubjectChange = async (subjectId) => {
        setSelectedSubject(subjectId)

        setSelectedUnit('')
        setTopicId('')

        setUnits([])
        setTopics([])
        setError('')

        if (!subjectId) return

        try {
            const data = await getUnits(subjectId)
            setUnits(data)
        } catch (err) {
            setError(err.message)
        }
    }
    const handleUnitChange = async (unitId) => {
        setSelectedUnit(unitId)
        setTopicId('')
        setTopics([])
        setError('')

        if (!unitId) return

        try {
            const data = await getTopics(unitId)
            setTopics(data)
        } catch (err) {
            setError(err.message)
        }
    }
    const handleRevisionAction = async (revisionId, action) => {
        setProcessingRevisionId(revisionId)
        setError('')
        setSuccess('')

        try {
            if (action === 'complete') {
                await completeRevision(revisionId)
            } else if (action === 'miss') {
                await markRevisionMissed(revisionId)
            } else {
                throw new Error('Invalid revision action.')
            }

            setSuccess(
                action === 'complete'
                    ? 'Revision completed successfully.'
                    : 'Revision marked as missed.'
            )
        } catch (err) {
            setError(err.message)
            setProcessingRevisionId(null)
            return
        }

        try {
            const results = await Promise.allSettled([
                getMyRevisions(),
                getTodaysRevisions(),
                getUpcomingRevisions(),
            ])

            const [all, todays, future] = results
            const refreshErrors = []

            if (all.status === 'fulfilled') {
                setRevisions(all.value)
            } else {
                refreshErrors.push('all revisions')
            }

            if (todays.status === 'fulfilled') {
                setToday(todays.value)
            } else {
                refreshErrors.push("today's revisions")
            }

            if (future.status === 'fulfilled') {
                setUpcoming(future.value)
            } else {
                refreshErrors.push('upcoming revisions')
            }

            if (refreshErrors.length > 0) {
                setError(
                    'Revision updated, but failed to refresh: ' +
                    refreshErrors.join(', ') + '.'
                )
            }
        } finally {
            setProcessingRevisionId(null)
        }
    }
    const handleScheduleRevision = async (event) => {
        event.preventDefault()

        const id = Number(topicId)

        if (!Number.isSafeInteger(id) || id <= 0 || !scheduledDate) {
            setError('Enter a valid topic ID and revision date.')
            return
        }

        setScheduling(true)
        setError('')
        setSuccess('')

        try {
            await scheduleRevision(id, scheduledDate)
            setTopicId('')
            setScheduledDate('')
            setSuccess('Revision scheduled successfully.')

            const results = await Promise.allSettled([
                getMyRevisions(),
                getTodaysRevisions(),
                getUpcomingRevisions(),
            ])

            const [all, todays, future] = results
            const refreshErrors = []

            if (all.status === 'fulfilled') {
                setRevisions(all.value)
            } else {
                refreshErrors.push('all revisions')
            }

            if (todays.status === 'fulfilled') {
                setToday(todays.value)
            } else {
                refreshErrors.push("today's revisions")
            }

            if (future.status === 'fulfilled') {
                setUpcoming(future.value)
            } else {
                refreshErrors.push('upcoming revisions')
            }

            if (refreshErrors.length > 0) {
                setError(
                    'Revision scheduled, but failed to refresh: ' +
                    refreshErrors.join(', ') + '.'
                )
            }
        } catch (err) {
            setError(err.message)
        } finally {
            setScheduling(false)
        }
    }
    const scheduledCount = revisions.filter(
        revision => revision.status === 'SCHEDULED'
    ).length

    const completedCount = revisions.filter(
        revision => revision.status === 'COMPLETED'
    ).length

    const missedCount = revisions.filter(
        revision => revision.status === 'MISSED'
    ).length
    const decidedCount = completedCount + missedCount

    const completionRate = decidedCount === 0
        ? 0
        : Math.round((completedCount / decidedCount) * 100)
    return (
        <StudentWorkspaceLayout title="Revision Management" breadcrumb="WORKSPACE / REVISIONS">
            <main className="revision-management">
            <h1>Revision Management</h1>
            <p>Schedule, monitor, and track your academic revisions.</p>

            <section className="revision-summary">
                <article className="revision-summary-card">
                    <h2>Scheduled</h2>
                    <p>{scheduledCount}</p>
                </article>

                <article className="revision-summary-card">
                    <h2>Completed</h2>
                    <p>{completedCount}</p>
                </article>

                <article className="revision-summary-card">
                    <h2>Missed</h2>
                    <p>{missedCount}</p>
                </article>
                <article className="revision-summary-card">
                    <h2>Completion Rate</h2>
                    <p>{completionRate}%</p>
                </article>
            </section>

            <section>
                <h2>Schedule a Revision</h2>

                <form onSubmit={handleScheduleRevision}>
                    <label htmlFor="revision-college">College</label>
                    <select
                        id="revision-college"
                        value={selectedCollege}
                        onChange={event => handleCollegeChange(event.target.value)}
                    >
                        <option value="">Choose College</option>
                        {colleges.map(college => (
                            <option key={college.id} value={college.id}>
                                {college.name}
                            </option>
                        ))}
                    </select>
                    <label htmlFor="revision-branch">Branch</label>
                    <select
                        id="revision-branch"
                        value={selectedBranch}
                        onChange={event => handleBranchChange(event.target.value)}
                        disabled={!selectedCollege}
                    >
                        <option value="">Choose Branch</option>
                        {branches.map(branch => (
                            <option key={branch.id} value={branch.id}>
                                {branch.name}
                            </option>
                        ))}
                    </select>
                    <label htmlFor="revision-semester">Semester</label>
                    <select
                        id="revision-semester"
                        value={selectedSemester}
                        onChange={event => handleSemesterChange(event.target.value)}
                        disabled={!selectedBranch}
                    >
                        <option value="">Choose Semester</option>
                        {semesters.map(semester => (
                            <option key={semester.id} value={semester.id}>
                                Semester {semester.semesterNumber}
                            </option>
                        ))}
                    </select>
                    <label htmlFor="revision-subject">Subject</label>
                    <select
                        id="revision-subject"
                        value={selectedSubject}
                        onChange={event => handleSubjectChange(event.target.value)}
                        disabled={!selectedSemester}
                    >
                        <option value="">Choose Subject</option>
                        {subjects.map(subject => (
                            <option key={subject.id} value={subject.id}>
                                {subject.name} ({subject.code})
                            </option>
                        ))}
                    </select>
                    <label htmlFor="revision-unit">Unit</label>
                    <select
                        id="revision-unit"
                        value={selectedUnit}
                        onChange={event => handleUnitChange(event.target.value)}
                        disabled={!selectedSubject}
                    >
                        <option value="">Choose Unit</option>
                        {units.map(unit => (
                            <option key={unit.id} value={unit.id}>
                                Unit {unit.unitNumber}: {unit.title}
                            </option>
                        ))}
                    </select>
                    <label htmlFor="revision-topic">Topic</label>
                    <select
                        id="revision-topic"
                        value={topicId}
                        onChange={event => setTopicId(event.target.value)}
                        disabled={!selectedUnit}
                    >
                        <option value="">Choose Topic</option>
                        {topics.map(topic => (
                            <option key={topic.id} value={topic.id}>
                                {topic.title}
                            </option>
                        ))}
                    </select>

                    <label htmlFor="revision-date">Revision Date</label>
                    <input
                        id="revision-date"
                        type="date"
                        required
                        min={new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 10)}
                        value={scheduledDate}
                        onChange={event => setScheduledDate(event.target.value)}
                    />

                    <button type="submit" disabled={scheduling}>
                        {scheduling ? 'Scheduling...' : 'Schedule Revision'}
                    </button>
                </form>

                {success && <p role="status">{success}</p>}
            </section>
            {loading && <p>Loading revisions...</p>}
            {error && <p role="alert">{error}</p>}

            {!loading && (
                <>
                    <h2>Today's Revisions ({today.length})</h2>
                    {today.length === 0 && <p>No revisions scheduled for today.</p>}
                    {today.map(revision => (
                        <RevisionCard
                            key={revision.id}
                            revision={revision}
                            onAction={handleRevisionAction}
                            processingRevisionId={processingRevisionId}
                            showActions
                        />
                    ))}
                    <h2>Upcoming Revisions ({upcoming.length})</h2>
                    {upcoming.length === 0 && <p>No upcoming revisions.</p>}
                    {upcoming.map(revision => (
                        <RevisionCard
                            key={revision.id}
                            revision={revision}
                            onAction={handleRevisionAction}
                            processingRevisionId={processingRevisionId}
                            showActions
                        />
                    ))}
                    <h2>Revision History</h2>
                    {revisions.filter(revision =>
                        revision.status === 'COMPLETED' ||
                        revision.status === 'MISSED'
                    ).length === 0 && (
                        <p>No revision history available.</p>
                    )}

                    {revisions
                        .filter(revision =>
                            revision.status === 'COMPLETED' ||
                            revision.status === 'MISSED'
                        )
                        .map(revision => (
                            <RevisionCard
                                key={revision.id}
                                revision={revision}
                            />
                        ))}
                </>
            )}
        </main>
        </StudentWorkspaceLayout>
    )
}

export default RevisionManagement
