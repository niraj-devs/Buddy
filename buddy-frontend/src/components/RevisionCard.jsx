function RevisionCard({
    revision,
    onAction,
    processingRevisionId,
    showActions = false,
}) {
    const isScheduled = revision.status === 'SCHEDULED'
    const isProcessing = processingRevisionId === revision.id
    const isBusy = processingRevisionId !== null

    return (
        <article className="revision-card">
            <h3>{revision.topicTitle}</h3>

            <p>Revision #{revision.revisionNumber}</p>
            <p>Scheduled: {revision.scheduledDate}</p>
            <p>Status: {revision.status}</p>

            {revision.completedAt && (
                <p>Completed: {revision.completedAt}</p>
            )}

            {showActions && isScheduled && (
                <div className="revision-card-actions">
                    <button
                        type="button"
                        disabled={isBusy}
                        onClick={() => onAction(revision.id, 'complete')}
                    >
                        {isProcessing
                            ? 'Processing...'
                            : 'Complete Revision'}
                    </button>

                    <button
                        type="button"
                        disabled={isBusy}
                        onClick={() => onAction(revision.id, 'miss')}
                    >
                        Mark Missed
                    </button>
                </div>
            )}
        </article>
    )
}

export default RevisionCard