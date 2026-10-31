const API_URL = '/api/goals'

const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

async function goalRequest(path = '', options = {}) {
    const token = getAccessToken()

    if (!token) {
        throw new Error('Session expired. Please log in again.')
    }

    const response = await fetch(`${API_URL}${path}`, {
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

        if (response.status === 404) {
            throw new Error(
                'Goal not found or student profile is unavailable.'
            )
        }

        let message = 'Goal operation failed.'

        try {
            const error = await response.json()
            message = error.message || error.detail || message
        } catch {
            // Use the fallback error message.
        }

        throw new Error(message)
    }

    if (response.status === 204) return null

    return response.json()
}

export function getMyGoals(signal) {
    return goalRequest('/me', { method: 'GET', signal })
}

export function getGoalById(goalId) {
    return goalRequest(`/${goalId}`, { method: 'GET' })
}

export function createGoal(goalData) {
    return goalRequest('', {
        method: 'POST',
        body: JSON.stringify(goalData),
    })
}

export function updateGoal(goalId, goalData) {
    return goalRequest(`/${goalId}`, {
        method: 'PATCH',
        body: JSON.stringify(goalData),
    })
}

export function deleteGoalById(goalId) {
    return goalRequest(`/${goalId}`, {
        method: 'DELETE',
    })
}
