import './AcademicManagement.css'
import { useEffect, useState } from 'react'
import { getColleges, getBranches, getSemesters, getSubjects, getUnits, getTopics, getMyTopicProgress, getUnlockedTopics, startTopic, updateTopicProgress, completeTopic } from '../services/academicService'

function AcademicManagement() {
    const [colleges, setColleges] = useState([])
    const [branches, setBranches] = useState([])
    const [semesters, setSemesters] = useState([])
    const [subjects, setSubjects] = useState([])
    const [units, setUnits] = useState([])
    const [topics, setTopics] = useState([])
    const [topicProgress, setTopicProgress] = useState({})
    const [topicUnlocks, setTopicUnlocks] = useState({})
    const [startingTopicId, setStartingTopicId] = useState(null)
    const [progressInputs, setProgressInputs] = useState({})
    const [updatingTopicId, setUpdatingTopicId] = useState(null)
    const [completingTopicId, setCompletingTopicId] = useState(null)
    const [selectedUnit, setSelectedUnit] = useState('')
    const [selectedSubject, setSelectedSubject] = useState('')
    const [selectedSemester, setSelectedSemester] = useState('')
    const [selectedBranch, setSelectedBranch] = useState('')
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionFeedback, setActionFeedback] = useState('')
    const [selectedCollege, setSelectedCollege] = useState('')
    const [showAcademicSelectors, setShowAcademicSelectors] = useState(false)
    const [myCurriculum, setMyCurriculum] = useState(null)

    useEffect(() => {
        let cancelled = false

        async function initializeAcademicContext() {
            try {
                const token =
                    localStorage.getItem('buddyAccessToken') ||
                    sessionStorage.getItem('buddyAccessToken')

                if (!token) {
                    throw new Error('Please log in to access Academic Management.')
                }

                const [collegeData, profileResponse] = await Promise.all([
                    getColleges(),
                    fetch('/api/students/me', {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }),
                ])

                if (cancelled) return

                setColleges(collegeData)

                if (profileResponse.status === 404) {
                    return
                }

                if (!profileResponse.ok) {
                    throw new Error('Unable to load your academic profile.')
                }

                const profile = await profileResponse.json()
                if (cancelled) return

                const { collegeId, branchId, semesterId } = profile

                if (collegeId && branchId && semesterId) {
                    setMyCurriculum({
                        collegeId: String(collegeId),
                        branchId: String(branchId),
                        semesterId: String(semesterId)
                    })
                }

                if (!collegeId || !branchId || !semesterId) {
                    return
                }

                const branchData = await getBranches(collegeId)
                if (cancelled) return

                const semesterData = await getSemesters(branchId)
                if (cancelled) return

                const subjectData = await getSubjects(semesterId)
                if (cancelled) return

                setBranches(branchData)
                setSemesters(semesterData)
                setSubjects(subjectData)

                setSelectedCollege(String(collegeId))
                setSelectedBranch(String(branchId))
                setSelectedSemester(String(semesterId))
            } catch (err) {
                if (!cancelled) {
                    setError(err.message)
                }
            } finally {
                if (!cancelled) {
                    setLoading(false)
                }
            }
        }

        initializeAcademicContext()

        return () => {
            cancelled = true
        }
    }, [])

    const handleRestoreMyCurriculum = async () => {
        if (!myCurriculum || loading) return

        setLoading(true)
        setError('')
        setActionFeedback('')

        try {
            const [branchData, semesterData, subjectData] = await Promise.all([
                getBranches(myCurriculum.collegeId),
                getSemesters(myCurriculum.branchId),
                getSubjects(myCurriculum.semesterId)
            ])

            setBranches(branchData)
            setSemesters(semesterData)
            setSubjects(subjectData)

            setSelectedCollege(myCurriculum.collegeId)
            setSelectedBranch(myCurriculum.branchId)
            setSelectedSemester(myCurriculum.semesterId)

            setSelectedSubject('')
            setSelectedUnit('')
            setUnits([])
            setTopics([])
            setTopicProgress({})
            setTopicUnlocks({})
            setProgressInputs({})

            setShowAcademicSelectors(false)
            setActionFeedback('Your academic curriculum has been restored.')
        } catch (err) {
            setError(err.message || 'Unable to restore your curriculum.')
        } finally {
            setLoading(false)
        }
    }
    const handleCollegeChange = async (collegeId) => {
        setSelectedCollege(collegeId)
        setBranches([])
        setSemesters([])
        setSubjects([])
        setUnits([])
        setTopics([])
        setSelectedUnit('')
        setSelectedSubject('')
        setSelectedSemester('')
        setSelectedBranch('')
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
        setSemesters([])
        setSubjects([])
        setUnits([])
        setTopics([])
        setSelectedUnit('')
        setSelectedSubject('')
        setSelectedSemester('')
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
        setSubjects([])
        setUnits([])
        setTopics([])
        setSelectedUnit('')
        setSelectedSubject('')
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
        setUnits([])
        setTopics([])
        setSelectedUnit('')
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
        setTopics([])
        setError('')

        if (!unitId) return

        try {
            const [data, progress, unlocks] = await Promise.all([
                getTopics(unitId),
                getMyTopicProgress(),
                getUnlockedTopics()
            ])
            setTopics(data)
            setTopicProgress(
                Object.fromEntries(
                    progress.map(item => [item.topicId, item])
                )
            )
            setTopicUnlocks(
                Object.fromEntries(
                    unlocks.map(item => [item.topicId, item])
                )
            )
        } catch (err) {
            setError(err.message)
        }
    }
    const handleStartTopic = async (topicId) => {
        setActionFeedback('')
        setStartingTopicId(topicId)
        setError('')

        try {
            const result = await startTopic(topicId)
            setActionFeedback('Topic started successfully.')
            setTopicProgress(prev => ({
                ...prev,
                [topicId]: result
            }))
        } catch (err) {
            setError(err.message)
        } finally {
            setStartingTopicId(null)
        }
    }
    const handleUpdateProgress = async (topicId) => {
        const percentage = Number(
            progressInputs[topicId] ?? topicProgress[topicId]?.progressPercentage ?? 0
        )

        if (!Number.isFinite(percentage) || percentage < 0 || percentage > 100) {
            setError('Progress must be between 0 and 100.')
            return
        }

        setActionFeedback('')
        setUpdatingTopicId(topicId)
        setError('')

        try {
            const result = await updateTopicProgress(topicId, percentage)
            setActionFeedback('Progress updated successfully.')
            setTopicProgress(prev => ({
                ...prev,
                [topicId]: result
            }))
            setProgressInputs(prev => ({
                ...prev,
                [topicId]: String(result.progressPercentage)
            }))
        } catch (err) {
            setError(err.message)
        } finally {
            setUpdatingTopicId(null)
        }
    }
    const handleCompleteTopic = async (topicId) => {
        setActionFeedback('')
        setCompletingTopicId(topicId)
        setError('')

        try {
            const result = await completeTopic(topicId)
            setTopicProgress(prev => ({
                ...prev,
                [topicId]: result
            }))


            const unlocks = await getUnlockedTopics()
            setTopicUnlocks(
                Object.fromEntries(
                    unlocks.map(item => [item.topicId, item])
                )
            )
            setActionFeedback('Topic completed successfully.')
        } catch (err) {
            setError(err.message)
        } finally {
            setCompletingTopicId(null)
        }
    }
    const selectedCollegeData = colleges.find(
        college => String(college.id) === selectedCollege
    )
    const selectedBranchData = branches.find(
        branch => String(branch.id) === selectedBranch
    )
    const selectedSemesterData = semesters.find(
        semester => String(semester.id) === selectedSemester
    )

    const completedTopics = topics.filter(
        topic => topicProgress[topic.id]?.status === 'COMPLETED'
    ).length

    const inProgressTopics = topics.filter(
        topic => topicProgress[topic.id]?.status === 'IN_PROGRESS'
    ).length

    const lockedTopics = topics.filter(
        topic =>
            !topicProgress[topic.id] &&
            topicUnlocks[topic.id]?.unlocked === false
    ).length

    const unitCompletion = topics.length > 0
        ? Math.round((completedTopics / topics.length) * 100)
        : 0
    return (
        <main className="academic-management">
            {error && (
                <div className="academic-error-feedback" role="alert">
                    <span>{error}</span>
                    <button
                        type="button"
                        onClick={() => setError('')}
                        aria-label="Dismiss error notification"
                    >
                        Dismiss
                    </button>
                </div>
            )}
            {actionFeedback && (
                <div className="academic-action-feedback" role="status">
                    {actionFeedback}
                    <button
                        type="button"
                        onClick={() => setActionFeedback('')}
                        aria-label="Dismiss notification"
                    >
                        Dismiss
                    </button>
                </div>
            )}
            <div className="academic-overview">
                <div className="academic-overview-header">
                    <div>
                        <p className="academic-eyebrow">YOUR ACADEMIC WORKSPACE</p>
                        <h2>My Academic Space</h2>
                        <p>Manage your curriculum and track your learning journey.</p>
                    </div>
                    <span className="academic-overview-badge">
                        {selectedSemester ? 'Academic context loaded' : 'Select academic context'}
                    </span>
                </div>

                {loading && (
                    <div className="academic-skeleton-grid" role="status" aria-label="Loading academic information">
                        {Array.from({ length: 4 }, (_, index) => (
                            <div className="academic-skeleton-card" key={index} aria-hidden="true">
                                <div className="academic-skeleton-line short" />
                                <div className="academic-skeleton-line long" />
                            </div>
                        ))}
                    </div>
                )}
                <div className={`academic-overview-grid ${loading ? 'academic-grid-loading' : ''}`}>
                    <div className="academic-overview-stat">
                        <span>College</span>
                        <strong>{selectedCollegeData?.name ?? 'Not selected'}</strong>
                    </div>
                    <div className="academic-overview-stat">
                        <span>Branch</span>
                        <strong>{selectedBranchData?.name ?? 'Not selected'}</strong>
                    </div>
                    <div className="academic-overview-stat">
                        <span>Semester</span>
                        <strong>
                            {selectedSemesterData
                                ? `Semester ${selectedSemesterData.semesterNumber}`
                                : 'Not selected'}
                        </strong>
                    </div>
                    <div className="academic-overview-stat">
                        <span>Available Subjects</span>
                        <strong>{selectedSemester ? subjects.length : 'ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â'}</strong>
                    </div>
                </div>
            </div>



            <div className="academic-context-controls">
                {myCurriculum && (
                    selectedCollege !== myCurriculum.collegeId ||
                    selectedBranch !== myCurriculum.branchId ||
                    selectedSemester !== myCurriculum.semesterId
                ) && (
                    <button
                        type="button"
                        className="academic-context-toggle"
                        onClick={handleRestoreMyCurriculum}
                        disabled={loading}
                    >
                        {loading ? 'Restoring...' : 'Back to My Curriculum'}
                    </button>
                )}
                <button
                    type="button"
                    className="academic-context-toggle"
                    aria-expanded={showAcademicSelectors}
                    onClick={() => setShowAcademicSelectors(value => !value)}
                >
                    {showAcademicSelectors
                        ? 'Hide Academic Selection'
                        : 'Explore Another Curriculum'}
                </button>
            </div>

            {(showAcademicSelectors || (!loading && !selectedSemester)) && (
                <div className="academic-context-panel">
            <h2>Select College</h2>

            {loading && <p>Loading colleges...</p>}


            {!loading && (
                <select
                    value={selectedCollege}
                    onChange={(e) => handleCollegeChange(e.target.value)}
                >
                    <option value="">Choose College</option>
                    {colleges.map(college => (
                        <option key={college.id} value={college.id}>
                            {college.name}
                        </option>
                    ))}
                </select>
            )}

            {selectedCollege && (
                <>
                    <h2>Branches</h2>
                    {branches.length === 0 ? (
                        <p>No branches available.</p>
                    ) : (
                        <select
                            value={selectedBranch}
                            onChange={(e) => handleBranchChange(e.target.value)}
                        >
                            <option value="">Choose Branch</option>
                            {branches.map(branch => (
                                <option key={branch.id} value={branch.id}>
                                    {branch.name}
                                </option>
                            ))}
                        </select>
                    )}
                </>
            )}

            {selectedBranch && (
                <>
                    <h2>Semesters</h2>
                    {semesters.length === 0 ? (
                        <p>No semesters available.</p>
                    ) : (
                        <select
                            value={selectedSemester}
                            onChange={(e) => handleSemesterChange(e.target.value)}
                        >
                            <option value="">Choose Semester</option>
                            {semesters.map(semester => (
                                <option key={semester.id} value={semester.id}>
                                    Semester {semester.semesterNumber}
                                </option>
                            ))}
                        </select>
                    )}
                </>
            )}

                </div>
            )}
            {selectedSemester && (
                <div className="academic-curriculum-panel">
                    <div className="academic-curriculum-heading">
                        <div>
                            <p className="academic-eyebrow">CURRICULUM EXPLORER</p>
                            <h2>Explore Your Subjects</h2>
                            <p>Select a subject and unit to explore topics and track progress.</p>
                        </div>
                        <span className="academic-curriculum-count">
                            {subjects.length} Subjects
                        </span>
                    </div>

                    <div className="academic-curriculum-fields">
                        <div className="academic-curriculum-field">
                            <label htmlFor="academic-subject">Subject</label>
                            <select
                                id="academic-subject"
                                value={selectedSubject}
                                onChange={(e) => handleSubjectChange(e.target.value)}
                            >
                                <option value="">Choose Subject</option>
                                {subjects.map(subject => (
                                    <option key={subject.id} value={subject.id}>
                                        {subject.name} ({subject.code})
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="academic-curriculum-field">
                            <label htmlFor="academic-unit">Unit</label>
                            <select
                                id="academic-unit"
                                value={selectedUnit}
                                disabled={!selectedSubject || units.length === 0}
                                onChange={(e) => handleUnitChange(e.target.value)}
                            >
                                <option value="">
                                    {!selectedSubject
                                        ? 'Select a subject first'
                                        : units.length === 0
                                            ? 'No units available'
                                            : 'Choose Unit'}
                                </option>
                                {units.map(unit => (
                                    <option key={unit.id} value={unit.id}>
                                        Unit {unit.unitNumber}: {unit.title}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>
            )}
            {!loading && selectedSemester && subjects.length === 0 && (
                <div className="academic-empty-state" role="status">
                    <div className="academic-empty-icon" aria-hidden="true">
                        <span>--</span>
                    </div>
                    <h3>No subjects available yet</h3>
                    <p>
                        Subjects haven't been added for this semester.
                        Once available, you can explore units, topics,
                        and track your progress.
                    </p>
                </div>
            )}
            {selectedUnit && (
                <div className="academic-topics-panel">
                    <div className="academic-topics-heading">
                        <div>
                            <p className="academic-eyebrow">LEARNING PROGRESS</p>
                            <h2>Unit Topics</h2>
                            <p>Track your learning and complete topics at your own pace.</p>
                        </div>
                        <span className="academic-curriculum-count">
                            {topics.length} Topics
                        </span>
                    </div>
                    <div className="academic-unit-progress-summary">
                        <div className="academic-progress-summary-heading">
                            <h3>Unit Progress Overview</h3>
                            <span>{unitCompletion}% completed</span>
                        </div>

                        <div className="academic-progress-summary-grid">
                            <div>
                                <span>Completed</span>
                                <strong>{completedTopics} / {topics.length}</strong>
                            </div>
                            <div>
                                <span>In Progress</span>
                                <strong>{inProgressTopics}</strong>
                            </div>
                            <div>
                                <span>Locked</span>
                                <strong>{lockedTopics}</strong>
                            </div>
                            <div>
                                <span>Completion</span>
                                <strong>{unitCompletion}%</strong>
                            </div>
                        </div>

                        <progress
                            value={unitCompletion}
                            max="100"
                            aria-label="Unit completion percentage"
                        />
                    </div>

                    <div className="academic-topics-grid">
                    {topics.length === 0 ? (
                        <p>No topics available.</p>
                    ) : (
                        topics.map(topic => (
                            <section
    key={topic.id}
    className={`academic-topic-card ${
        !topicProgress[topic.id] &&
        topicUnlocks[topic.id]?.unlocked === false
            ? 'is-locked'
            : ''
    }`}
>
    <span className={`academic-topic-status ${
        topicProgress[topic.id]?.status === 'COMPLETED'
            ? 'is-completed'
            : topicProgress[topic.id]?.status === 'IN_PROGRESS'
                ? 'is-progress'
                : !topicProgress[topic.id] &&
                  topicUnlocks[topic.id]?.unlocked === false
                    ? 'is-locked'
                    : 'is-not-started'
    }`}>
        {topicProgress[topic.id]?.status === 'COMPLETED'
            ? 'Completed'
            : topicProgress[topic.id]?.status === 'IN_PROGRESS'
                ? 'In Progress'
                : !topicProgress[topic.id] &&
                  topicUnlocks[topic.id]?.unlocked === false
                    ? 'Locked'
                    : 'Not Started'}
    </span>
                                <h3>{topic.sequenceNumber}. {topic.title}</h3>
                                {topic.description && <p>{topic.description}</p>}
                                <p>
                                    Status: {topicProgress[topic.id]?.status ?? 'NOT_STARTED'}
                                </p>
                                <progress
                                    value={Number(topicProgress[topic.id]?.progressPercentage ?? 0)}
                                    max="100"
                                />
                                <span>
                                    {Number(topicProgress[topic.id]?.progressPercentage ?? 0)}%
                                </span>
                                {topicProgress[topic.id]?.status === 'IN_PROGRESS' && (
                                    <div>
                                        <input
                                            type="number"
                                            min="0"
                                            max="100"
                                            step="1"
                                            aria-label={`Progress percentage for ${topic.title}`}
                                            value={progressInputs[topic.id] ?? topicProgress[topic.id].progressPercentage}
                                            onChange={(e) => setProgressInputs(prev => ({
                                                ...prev,
                                                [topic.id]: e.target.value
                                            }))}
                                        />
                                        <button
                                            type="button"
                                            disabled={updatingTopicId !== null}
                                            onClick={() => handleUpdateProgress(topic.id)}
                                        >
                                            {updatingTopicId === topic.id
                                                ? 'Updating...'
                                                : 'Update Progress'}
                                        </button>
                                    </div>
                                )}
                                {topicProgress[topic.id]?.status === 'IN_PROGRESS' && (
                                    <button
                                        type="button"
                                        disabled={completingTopicId !== null || updatingTopicId !== null}
                                        onClick={() => handleCompleteTopic(topic.id)}
                                    >
                                        {completingTopicId === topic.id
                                            ? 'Completing...'
                                            : 'Complete Topic'}
                                    </button>
                                )}
                                {!topicProgress[topic.id] && (
                                    <div>
                                        {topicUnlocks[topic.id]?.unlocked === false && (
                                            <p>
                                                Locked - Complete: {
                                                    topicUnlocks[topic.id].incompletePrerequisites.join(', ')
                                                }
                                            </p>
                                        )}
                                        <button
                                            type="button"
                                            disabled={
                                                startingTopicId !== null ||
                                                topicUnlocks[topic.id]?.unlocked !== true
                                            }
                                            onClick={() => handleStartTopic(topic.id)}
                                        >
                                            {startingTopicId === topic.id
                                                ? 'Starting...'
                                                : topicUnlocks[topic.id]?.unlocked === false
                                                    ? 'Locked'
                                                    : 'Start Topic'}
                                        </button>
                                    </div>
                                )}
                            </section>
                        ))
                    )}
                    </div>
                </div>
            )}
        </main>
    )
}

export default AcademicManagement
