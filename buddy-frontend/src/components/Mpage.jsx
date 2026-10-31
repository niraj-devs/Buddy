
import { Link } from 'react-router-dom'
import NeonGraduationBackground from './NeonGraduationBackground'
import './Mpage.css'

function Mpage() {
    return (
        <section className="mpage">
            <NeonGraduationBackground
                particleCount={95}
                connectionDistance={155}
                animationSpeed={1}
                glowIntensity={1.15}
                interactiveMouse={true}
                showCap={true}
                className="mpage-background"
            />

            <div className="mpage-container">
                <div className="mpage-content">
                    <p className="mpage-label">
                        YOUR JOURNEY, YOUR PACE
                    </p>

                    <h1 className="mpage-title">
                        Build the Future You Deserve
                    </h1>

                    <p className="mpage-description">
                        Master your academics, build career-ready skills, and discover the right
                        opportunities, all in one platform. Buddy turns your ambition into
                        measurable progress, one milestone at a time.
                    </p>

                    <div className="mpage-actions">
                        <Link to="/register" className="mpage-primary-btn">
                            Get Started
                        </Link>

                        <Link to="/explore" className="mpage-secondary-link">
                            Explore Buddy →
                        </Link>
                    </div>
                </div>
            </div>
        </section>
    )
}

export default Mpage
