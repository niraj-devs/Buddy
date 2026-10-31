const API_URL = '/api/progress/me/summary'

const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

export async function getAcademicProgressSummary(signal) {
    const token = getAccessToken()

    const response = await fetch(API_URL, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
        signal,
    })

    if (!response.ok) {
        throw new Error('Failed to load academic progress')
    }

    return response.json()
}

async function academicRequest(path, options = {}) {
    const token = getAccessToken()

    const response = await fetch(path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            ...options.headers,
        },
    })

    if (!response.ok) {
        throw new Error(`Academic request failed: ${response.status}`)
    }

    return response.json()
}

export const getColleges = () =>
    academicRequest('/api/academic/colleges')

export const getBranches = (collegeId) =>
    academicRequest(`/api/academic/colleges/${collegeId}/branches`)

export const getSemesters = (branchId) =>
    academicRequest(`/api/academic/branches/${branchId}/semesters`)

export const getSubjects = (semesterId) =>
    academicRequest(`/api/academic/semesters/${semesterId}/subjects`)

export const getUnits = (subjectId) =>
    academicRequest(`/api/academic/subjects/${subjectId}/units`)

export const getTopics = (unitId) =>
    academicRequest(`/api/academic/units/${unitId}/topics`)

export const getMyTopicProgress = () =>
    academicRequest('/api/progress/me')

export const getUnlockedTopics = () =>
    academicRequest('/api/progress/topics/unlocked')

export const startTopic = (topicId) =>
    academicRequest(`/api/progress/topics/${topicId}/start`, {
        method: 'POST',
    })

export const updateTopicProgress = (topicId, percentage) =>
    academicRequest(`/api/progress/topics/${topicId}`, {
        method: 'PATCH',
        body: JSON.stringify({ progressPercentage: percentage }),
    })

export const completeTopic = (topicId) =>
    academicRequest(`/api/progress/topics/${topicId}/complete`, {
        method: 'POST',
    })
