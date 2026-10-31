import { Link } from 'react-router-dom'

function StudentDashboard() {
return (
<main style={{
minHeight: '70vh',
display: 'grid',
placeItems: 'center',
padding: '40px 24px',
background: '#f5f8ff',
}}>
<section style={{
width: '100%',
maxWidth: '620px',
padding: '40px',
border: '1px solid #e5ebf5',
borderRadius: '18px',
background: '#fff',
textAlign: 'center',
}}>
<p style={{
color: '#3478f6',
fontSize: '12px',
fontWeight: 800,
letterSpacing: '2px',
}}>
YOUR STUDENT SPACE </p>

            <h1>Welcome to Buddy!</h1>

            <p style={{ color: '#52627a', lineHeight: 1.8 }}>
                Your dashboard destination is ready. Real account
                authentication and student data will be connected
                to the Spring Boot backend in the next stage.
            </p>

            <Link
                to="/explore"
                style={{
                    display: 'inline-block',
                    marginTop: '12px',
                    padding: '12px 20px',
                    borderRadius: '8px',
                    color: '#fff',
                    background: '#3478f6',
                    textDecoration: 'none',
                    fontWeight: 700,
                }}
            >
                Explore Buddy
            </Link>
        </section>
    </main>
)


}

export default StudentDashboard
