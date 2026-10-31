const API_URL = '/api/development/skills/me'

const getAccessToken = () =>
    localStorage.getItem('buddyAccessToken') ||
    sessionStorage.getItem('buddyAccessToken')

export async function getMySkills(signal) {
    const response = await fetch(API_URL, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${getAccessToken()}`,
        },
        signal,
    })

    if (!response.ok) {
        throw new Error('Failed to load student skills')
    }

    return response.json()
}

export async function updateSkillProgress(skillId, progressPercentage) {
    const response = await fetch(
        `/api/development/skills/${skillId}/progress`,
        {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ progressPercentage }),
        }
    )

    if (!response.ok) {
        const error = await response.text()
        throw new Error(error || 'Failed to update skill progress')
    }

    return response.json()
}

export async function getSkillUnlockStatus(skillId, signal) {
    const response = await fetch(
        `/api/development/skills/${skillId}/unlock-status`,
        {
            method: 'GET',
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
            },
            signal,
        }
    )

    if (!response.ok) {
        throw new Error('Failed to check skill prerequisites')
    }

    return response.json()
}
