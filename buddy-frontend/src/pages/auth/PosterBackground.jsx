import './Login.css'

const POSTER_CARDS = [
{ title: 'PROGRAMMING', subtitle: 'THINK • CODE • SOLVE', color: '#075985' },
{ title: 'PROJECTS', subtitle: 'TURN IDEAS INTO REALITY', color: '#164e63' },
{ title: 'EXPLORE', subtitle: 'DISCOVER YOUR INTERESTS', color: '#173b76' },
{ title: 'ACADEMICS', subtitle: 'SUBJECTS • NOTES • PYQs', color: '#92400e' },
{ title: 'DSA', subtitle: 'LOGIC & PROBLEM SOLVING', color: '#166534' },
{ title: 'FOUNDATIONS', subtitle: 'BUILD STRONG BASICS', color: '#563b85' },
{ title: 'TECH STACK', subtitle: 'WEB • AI • CLOUD • MORE', color: '#9f1239' },
{ title: 'CAREER', subtitle: 'EXPLORE YOUR PATH', color: '#115e59' },
{ title: 'COLLABORATE', subtitle: 'LEARN AND BUILD TOGETHER', color: '#3730a3' },
{ title: 'INTERNSHIPS', subtitle: 'GAIN REAL-WORLD EXPERIENCE', color: '#854d0e' },
{ title: 'PLACEMENTS', subtitle: 'PREPARE • PRACTICE • PERFORM', color: '#7f1d1d' },
{ title: 'YOUR FUTURE', subtitle: 'MAKE EVERY SEMESTER COUNT', color: '#334155' },
]

function PosterBackground() {
return ( <div className="buddy-login-background" aria-hidden="true"> <div className="buddy-water-float"> <div className="buddy-poster-stage"> <div className="buddy-poster-grid">
{POSTER_CARDS.map((poster, index) => (
<article
key={`${poster.title}-${index}`}
className="buddy-poster"
style={{
'--poster-color': poster.color,
'--poster-index': index,
}}
> <div className="buddy-poster-top"> <span className="buddy-poster-mark">B.</span> <span>LEARN · BUILD · GROW</span> </div>

                            <div className="buddy-poster-title">
                                <p>{poster.subtitle}</p>
                                <h2>{poster.title}</h2>
                            </div>
                        </article>
                    ))}
                </div>
            </div>

            <div className="buddy-water-sheen" />
        </div>

        <div className="buddy-login-vignette" />
    </div>
)


}

export default PosterBackground
