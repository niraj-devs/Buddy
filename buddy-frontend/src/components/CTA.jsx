
import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import './CTA.css'

const headline = 'Your goals deserve a plan.'

function CTA() {
    const sectionRef = useRef(null)
    const [animate, setAnimate] = useState(false)

    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setAnimate(true)
                    observer.disconnect()
                }
            },
            { threshold: 0.25 }
        )

        if (sectionRef.current) {
            observer.observe(sectionRef.current)
        }

        return () => observer.disconnect()
    }, [])

    return (
        <section
            ref={sectionRef}
            className={`cta-section ${animate ? 'cta-visible' : ''}`}
        >
            <div className="cta-container">
                <p className="cta-label">
                    YOUR NEXT CHAPTER STARTS HERE
                </p>

                <h2 className="cta-heading" aria-label={headline}>
                    {headline.split('').map((letter, index) => (
                        <span
                            key={index}
                            className="cta-letter"
                            aria-hidden="true"
                            style={{
                                '--i': index,
                                '--x': `${((index * 47) % 500) - 250}px`,
                                '--y': `${((index * 71) % 300) - 150}px`,
                                '--r': `${((index * 31) % 100) - 50}deg`,
                            }}
                        >
                            {letter === ' ' ? '\u00A0' : letter}
                        </span>
                    ))}
                </h2>

                <p className="cta-description">
                    Bring your academics, skills, and career ambitions
                    together. Make every step of your student journey count.
                </p>

                <Link to="/register" className="cta-button">
                    Get Started <span aria-hidden="true">→</span>
                </Link>
            </div>
        </section>
    )
}

export default CTA
