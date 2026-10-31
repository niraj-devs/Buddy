import './Explore.css'

const features = [
{
number: '01',
title: 'College Academics',
description:
'Organize your subjects, units, topics, learning resources, and previous-year questions in one place.',
points: ['Subject-wise learning', 'Topic completion', 'Revision tracking'],
icon: ( <svg viewBox="0 0 48 48" aria-hidden="true"> <path d="M7 12.5 24 6l17 6.5L24 19 7 12.5Z" /> <path d="M13 16v12c6 5 16 5 22 0V16" /> <path d="M41 13v14" /> <path d="M19 22v6" /> <path d="M29 22v6" /> </svg>
),
},
{
number: '02',
title: 'Career Roadmaps',
description:
'Follow a structured learning path for your chosen career and understand what to learn next.',
points: ['Skill prerequisites', 'Learning milestones', 'Progress tracking'],
icon: ( <svg viewBox="0 0 48 48" aria-hidden="true"> <circle cx="10" cy="12" r="4" /> <circle cx="38" cy="24" r="4" /> <circle cx="10" cy="36" r="4" /> <path d="M14 12h7a7 7 0 0 1 7 7v0a5 5 0 0 0 5 5" /> <path d="M14 36h7a7 7 0 0 0 7-7v0a5 5 0 0 1 5-5" /> </svg>
),
},
{
number: '03',
title: 'Projects & Experience',
description:
'Turn what you learn into practical work and maintain a record of projects and experience.',
points: ['Project tracking', 'Practical experience', 'Work evidence'],
icon: ( <svg viewBox="0 0 48 48" aria-hidden="true"> <rect x="7" y="13" width="34" height="27" rx="3" /> <path d="M17 13V8h14v5" /> <path d="m19 23-5 4 5 4" /> <path d="m29 23 5 4-5 4" /> <path d="m26 21-4 12" /> </svg>
),
},
{
number: '04',
title: 'Opportunities',
description:
'Explore relevant internships, hackathons, scholarships, events, and other career opportunities.',
points: ['Eligibility details', 'Deadline awareness', 'Skill-based relevance'],
icon: ( <svg viewBox="0 0 48 48" aria-hidden="true"> <rect x="6" y="15" width="36" height="25" rx="3" /> <path d="M17 15V9h14v6" /> <path d="M6 24h36" /> <path d="M20 24v4h8v-4" /> </svg>
),
},
{
number: '05',
title: 'Goals & Tasks',
description:
'Break bigger ambitions into manageable tasks, milestones, and deadlines you can work towards.',
points: ['Personal goals', 'Task management', 'Deadline tracking'],
icon: ( <svg viewBox="0 0 48 48" aria-hidden="true"> <rect x="8" y="7" width="32" height="35" rx="3" /> <path d="m14 18 3 3 6-7" /> <path d="M27 19h7" /> <path d="m14 30 3 3 6-7" /> <path d="M27 31h7" /> </svg>
),
},
{
number: '06',
title: 'Progress Dashboard',
description:
'See your academic progress, skill development, projects, goals, and next priorities together.',
points: ['Learning overview', 'Career progress', 'Clear next actions'],
icon: ( <svg viewBox="0 0 48 48" aria-hidden="true"> <rect x="7" y="7" width="34" height="34" rx="3" /> <path d="M15 32V24" /> <path d="M24 32V16" /> <path d="M33 32V20" /> <path d="M12 36h24" /> </svg>
),
},
]

const journey = [
{
number: '01',
title: 'Learn',
description: 'Study topics and strengthen your academic foundations.',
},
{
number: '02',
title: 'Develop',
description: 'Build the skills required for your chosen career.',
},
{
number: '03',
title: 'Build',
description: 'Apply your knowledge through practical projects.',
},
{
number: '04',
title: 'Move Forward',
description: 'Track your progress and pursue relevant opportunities.',
},
]

function BuddyLogo({ className = '' }) {
return (
<img
src="/Buddy-b.png"
alt="Buddy"
className={`buddy-logo ${className}`}
/>
)
}

function Explore() {
return ( <main className="explore-page"> <div className="explore-background-wave explore-wave-one" aria-hidden="true" /> <div className="explore-background-wave explore-wave-two" aria-hidden="true" /> <div className="explore-background-wave explore-wave-three" aria-hidden="true" />

        <div className="explore-content">
            <section className="explore-hero">
                <div className="explore-hero-copy">
                    <div className="explore-brand">
                        <BuddyLogo className="explore-brand-logo" />
                        <span>YOUR STUDENT JOURNEY</span>
                    </div>

                    <h1>
                        Your entire student journey.
                        <span> One connected roadmap.</span>
                    </h1>

                    <p className="explore-hero-description">
                        Manage college academics, develop career-ready
                        skills, build projects, and discover opportunities
                        with a clearer plan for your future.
                    </p>

                    <div className="explore-hero-actions">
                        <a href="/register" className="explore-button explore-button-primary">
                            Start Your Journey
                            <span aria-hidden="true">→</span>
                        </a>

                        <a
                            href="#explore-features"
                            className="explore-button explore-button-secondary"
                        >
                            Explore Features
                        </a>
                    </div>
                </div>

                <div className="explore-hero-visual">
                    <div className="explore-visual-heading">
                        <BuddyLogo className="explore-preview-logo" />
                        <div>
                            <p className="explore-visual-title">Your Student Space</p>
                            <p className="explore-visual-subtitle">
                                Academics, skills and goals
                            </p>
                        </div>
                    </div>

                    <div className="explore-visual-divider" />

                    <div className="explore-preview-item">
                        <span className="explore-preview-number">01</span>
                        <div className="explore-preview-text">
                            <h3>Academic Learning</h3>
                            <p>Subjects, topics and revisions</p>
                        </div>
                        <span className="explore-preview-arrow" aria-hidden="true">↗</span>
                    </div>

                    <div className="explore-preview-item">
                        <span className="explore-preview-number">02</span>
                        <div className="explore-preview-text">
                            <h3>Career Development</h3>
                            <p>Skills and learning roadmaps</p>
                        </div>
                        <span className="explore-preview-arrow" aria-hidden="true">↗</span>
                    </div>

                    <div className="explore-preview-item">
                        <span className="explore-preview-number">03</span>
                        <div className="explore-preview-text">
                            <h3>Projects & Opportunities</h3>
                            <p>Practical work and next steps</p>
                        </div>
                        <span className="explore-preview-arrow" aria-hidden="true">↗</span>
                    </div>

                    <div className="explore-visual-footer">
                        <span className="explore-footer-mark" aria-hidden="true" />
                        <span>A clearer way to move forward</span>
                    </div>
                </div>
            </section>

            <section className="explore-features-section" id="explore-features">
                <div className="explore-section-heading">
                    <p className="explore-eyebrow">WHAT YOU CAN DO</p>
                    <h2>Everything you need to keep moving.</h2>
                    <p>
                        Keep your learning, career preparation, and
                        opportunities connected instead of managing them
                        in separate places.
                    </p>
                </div>

                <div className="explore-feature-grid">
                    {features.map((feature) => (
                        <article className="explore-feature-card" key={feature.number}>
                            <div className="explore-feature-card-top">
                                <div className="explore-feature-icon">
                                    {feature.icon}
                                </div>
                                <span className="explore-feature-number">
                                    {feature.number}
                                </span>
                            </div>

                            <h3>{feature.title}</h3>
                            <p className="explore-feature-description">
                                {feature.description}
                            </p>

                            <ul className="explore-feature-points">
                                {feature.points.map((point) => (
                                    <li key={point}>
                                        <span className="explore-point-mark" aria-hidden="true" />
                                        {point}
                                    </li>
                                ))}
                            </ul>
                        </article>
                    ))}
                </div>
            </section>

            <section className="explore-journey-section">
                <div className="explore-journey-heading">
                    <p className="explore-eyebrow">HOW IT CONNECTS</p>
                    <h2>From learning to real opportunities.</h2>
                    <p>
                        Make progress in one area and use it to move
                        forward in the next.
                    </p>
                </div>

                <div className="explore-journey-grid">
                    {journey.map((step, index) => (
                        <article className="explore-journey-step" key={step.number}>
                            <div className="explore-journey-step-top">
                                <span className="explore-journey-number">
                                    {step.number}
                                </span>

                                {index < journey.length - 1 && (
                                    <span
                                        className="explore-journey-connector"
                                        aria-hidden="true"
                                    />
                                )}
                            </div>

                            <h3>{step.title}</h3>
                            <p>{step.description}</p>
                        </article>
                    ))}
                </div>
            </section>

            <section className="explore-final-cta">
                <div className="explore-final-cta-brand">
                    <BuddyLogo className="explore-final-logo" />
                </div>

                <div className="explore-final-cta-copy">
                    <p className="explore-eyebrow">YOUR NEXT CHAPTER</p>
                    <h2>Give your ambitions a direction.</h2>
                    <p>
                        Start organizing your academic work, career goals,
                        and next steps in one place.
                    </p>
                </div>

                <a href="/register" className="explore-button explore-button-dark">
                    Get Started
                    <span aria-hidden="true">→</span>
                </a>
            </section>

            <footer className="explore-footer">
                <div className="explore-footer-brand">
                    <BuddyLogo className="explore-footer-logo" />
                    <span>Buddy</span>
                </div>
                <p>Make your next step count.</p>
            </footer>
        </div>
    </main>
)


}

export default Explore
