
import { useEffect, useRef, useState } from 'react'
import { Compass, Code2, Rocket, Repeat2 } from 'lucide-react'
import './HowItWorks.css'

function HowItWorks() {
    const sectionRef = useRef(null)
    const [isVisible, setIsVisible] = useState(false)

    useEffect(() => {
        const section = sectionRef.current

        if (!section) return

        const observer = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setIsVisible(true)
                    observer.unobserve(section)
                }
            },
            { threshold: 0.15 }
        )

        observer.observe(section)

        return () => observer.disconnect()
    }, [])

    const steps = [
        {
            number: '1',
            label: 'STEP ONE',
            title: 'Set Your Direction',
            description:
                'Organize your academic responsibilities, define your career goals, and understand what you want to achieve.',
            Icon: Compass,
        },
        {
            number: '2',
            label: 'STEP TWO',
            title: 'Build Your Skills',
            description:
                'Follow structured learning roadmaps, track your progress, and stay consistent with revision and practice.',
            Icon: Code2,
        },
        {
            number: '3',
            label: 'STEP THREE',
            title: 'Discover Opportunities',
            description:
                'Explore internships, hackathons, and other opportunities while identifying the requirements you need to work toward.',
            Icon: Rocket,
        },
    ]

    return (
        <section
            id="how-it-works"
            ref={sectionRef}
            className={`how-section ${isVisible ? 'how-visible' : ''}`}
        >
            <div className="how-container">

              <div className="how-heading">
                  <p className="how-label">YOUR JOURNEY WITH BUDDY</p>

                  <h2>Make the most of your college years.</h2>

                  <p>
                      Keep up with your academics, build skills that matter,
                      and find opportunities to put them to use.
                  </p>
              </div>



                <div className="how-steps">
                    {steps.map(({ number, label, title, description, Icon }, index) => (
                        <article
                            className="how-step"
                            key={number}
                            style={{ '--step-index': index }}
                        >
                            <div className="step-visual">
                                <div className="step-badge">
                                    <Icon
                                        className="step-icon"
                                        strokeWidth={1.7}
                                        aria-hidden="true"
                                    />
                                </div>

                                <span className="step-number">{number}</span>
                            </div>

                            <span className="step-label">{label}</span>

                            <h3>{title}</h3>

                            <p className="step-description">{description}</p>
                        </article>
                    ))}
                </div>


            </div>
        </section>
    )
}

export default HowItWorks
