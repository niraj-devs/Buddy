const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

async function taskRequest(path, options = {}) {
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

        let message = 'Task operation failed.'

        try {
            const error = await response.json()
            message = error.message || error.detail || message
        } catch {
            // Keep fallback error message.
        }

        throw new Error(message)
    }

    if (response.status === 204) return null

    return response.json()
}

export function createPersonalTask(data) {
    return taskRequest('/api/tasks', {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function createMilestoneTask(milestoneId, data) {
    return taskRequest(`/api/milestones/${milestoneId}/tasks`, {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function getMilestoneTasks(milestoneId, signal) {
    return taskRequest(`/api/milestones/${milestoneId}/tasks`, {
        method: 'GET',
        signal,
    })
}

export function getMyTasks(signal) {
    return taskRequest('/api/tasks/me', {
        method: 'GET',
        signal,
    })
}

export function getTaskById(taskId) {
    return taskRequest(`/api/tasks/${taskId}`, {
        method: 'GET',
    })
}

export function updateTask(taskId, data) {
    return taskRequest(`/api/tasks/${taskId}`, {
        method: 'PATCH',
        body: JSON.stringify(data),
    })
}

export function deleteTaskById(taskId) {
    return taskRequest(`/api/tasks/${taskId}`, {
        method: 'DELETE',
    })
}

/*
 * Backward-compatible functions used by Dashboard.jsx
 */

export function createTask(data) {
    return createPersonalTask(data)
}

export function updateTaskStatus(taskId, status) {
    return updateTask(taskId, { status })
}
