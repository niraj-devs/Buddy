const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

async function milestoneRequest(path, options = {}) {
    const token = getAccessToken()

    if (!token) {
        throw new Error('Session expired. Please log in again.')
    }

    const response = await fetch(path, {
        ...options,
        headers: {
            Authorization: `Bearer ${token}`,
            ...(options.body ? { 'Content-Type': 'application/json' } : {}),
            ...options.headers,
        },
    })

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error('Session expired. Please log in again.')
        }

        let message = 'Milestone operation failed.'

        try {
            const error = await response.json()
            message = error.message || error.detail || message
        } catch {
            // Preserve fallback message.
        }

        if (response.status === 409 && message === 'Milestone operation failed.') {
            message = 'This sequence number is already used by another milestone.'
        }

        throw new Error(message)
    }

    if (response.status === 204) return null

    return response.json()
}

export function getGoalMilestones(goalId, signal) {
    return milestoneRequest(`/api/goals/${goalId}/milestones`, {
        method: 'GET',
        signal,
    })
}

export function getMilestoneById(milestoneId) {
    return milestoneRequest(`/api/milestones/${milestoneId}`, {
        method: 'GET',
    })
}

export function createMilestone(goalId, data) {
    return milestoneRequest(`/api/goals/${goalId}/milestones`, {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function updateMilestone(milestoneId, data) {
    return milestoneRequest(`/api/milestones/${milestoneId}`, {
        method: 'PATCH',
        body: JSON.stringify(data),
    })
}

export function deleteMilestoneById(milestoneId) {
    return milestoneRequest(`/api/milestones/${milestoneId}`, {
        method: 'DELETE',
    })
}
