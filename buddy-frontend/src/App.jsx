import GoalManagement from './components/GoalManagement'
import AcademicManagement from './components/AcademicManagement'
import RevisionManagement from './components/RevisionManagement'

import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'

import Navbar from './components/Navbar'
import WorkspaceLayout from './components/WorkspaceLayout'
import Mpage from './components/Mpage'
import Explore from './components/Explore'
import Features from './components/Features'
import HowItWorks from './components/HowItWorks'
import CTA from './components/CTA'
import Footer from './components/Footer'
import ScrollToSection from './components/ScrollToSection'
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import ProtectedRoute from './components/auth/ProtectedRoute'
import Dashboard from './components/Dashboard'
import SkillsManagement from './components/SkillsManagement'
import StudentProfile from './pages/student/StudentProfile'
import Settings from './pages/settings/Settings'

function AppContent() {
    const location = useLocation()

    const isAuthPage =
        location.pathname === '/login' ||
        location.pathname === '/register'

    return (
        <>
            <ScrollToSection />


            <div className={isAuthPage ? '' : 'app-with-navbar'}>
                {!isAuthPage && <Navbar />}

                <Routes>
                    <Route
                        path="/"
                        element={
                            <>
                                <Mpage />
                                <Features />
                                <HowItWorks />
                                <CTA />
                            </>
                        }
                    />

                  <Route element={<ProtectedRoute />}>
                      <Route element={<WorkspaceLayout />}>
                      <Route path="/explore" element={<Explore />} />
                      <Route path="/dashboard" element={<Dashboard />} />
                      <Route path="/skills" element={<SkillsManagement />} />
                      <Route path="/academics" element={<AcademicManagement />} />
                      <Route path="/goals" element={<GoalManagement />} />
                      <Route path="/revisions" element={<RevisionManagement />} />
                      <Route path="/settings" element={<StudentProfile />} />
                      <Route path="/settings/preferences" element={<Settings />} />
                      </Route>
                  </Route>

                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                </Routes>

                {!isAuthPage && <Footer />}
            </div>
        </>
    )
}
function App() {
    return (
        <BrowserRouter>
            <AppContent />
        </BrowserRouter>
    )
}

export default App
