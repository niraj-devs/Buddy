const BASE_URL = '/api/revisions'

const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

async function revisionRequest(path, options = {}) {
    const token = getAccessToken()

    const response = await fetch(`${BASE_URL}${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            ...options.headers,
        },
    })

    if (!response.ok) {
        throw new Error(`Revision request failed: ${response.status}`)
    }

    return response.json()
}

export const scheduleRevision = (topicId, scheduledDate) =>
    revisionRequest('', {
        method: 'POST',
        body: JSON.stringify({ topicId, scheduledDate }),
    })

export const getMyRevisions = () =>
    revisionRequest('/me')

export const getTodaysRevisions = () =>
    revisionRequest('/me/today')

export const getUpcomingRevisions = () =>
    revisionRequest('/me/upcoming')

export const completeRevision = (revisionId) =>
    revisionRequest(`/${revisionId}/complete`, {
        method: 'PATCH',
    })

export const markRevisionMissed = (revisionId) =>
    revisionRequest(`/${revisionId}/miss`, {
        method: 'PATCH',
    })
