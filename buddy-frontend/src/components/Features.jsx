
import './Features.css'

const images = [
    '/public/Academic Companion.png',
    '/public/Career Roadmaps.png',
    '/public/Opportunity Discovery.png',
    '/public/Progress That Matters.png',
    '/public/Smart Revision Planning.png',
    '/public/Personalized Student Journey.png',
]

function Features() {
    // Duplicate the images to create a seamless scrolling loop
    const carouselImages = [...images, ...images]

    return (
        <section id="features">
            <div className="features-container">

                <div className="features-heading">
                    <p>WHY BUDDY?</p>

                    <h2>
                        Learn with purpose. Prepare with direction.
                    </h2>

                    <p>
                        Manage academics, build career-ready skills,
                        and discover opportunities that move you closer
                        to your goals—all in one place.
                    </p>
                </div>

                <div className="features-carousel">
                    <div className="features-track">
                        {carouselImages.map((image, index) => (
                            <div
                                className="features-image"
                                key={`${image}-${index}`}
                            >
                                <img
                                    src={image}
                                    alt={`Buddy showcase ${(index % images.length) + 1}`}
                                    loading="lazy"
                                />
                            </div>
                        ))}
                    </div>
                </div>

            </div>
        </section>
    )
}

export default Features
