import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'

function ScrollToSection() {
const location = useLocation()


useEffect(() => {
    const timeoutId = setTimeout(() => {
        if (!location.hash) {
            window.scrollTo({
                top: 0,
                behavior: 'smooth',
            })
            return
        }

        const sectionId = location.hash.substring(1)
        const section = document.getElementById(sectionId)

        if (section) {
            section.scrollIntoView({
                behavior: 'smooth',
                block: 'start',
            })
        }
    }, 100)

    return () => clearTimeout(timeoutId)
}, [location.pathname, location.hash, location.key])

return null

}

export default ScrollToSection
