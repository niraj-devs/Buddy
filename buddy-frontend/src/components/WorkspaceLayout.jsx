import { Outlet } from 'react-router-dom'
import WorkspaceMenu from './WorkspaceMenu'
import './WorkspaceLayout.css'

function WorkspaceLayout() {
    return (
        <div className="buddy-workspace-shell">
            <div className="buddy-workspace-rail">
                <WorkspaceMenu />
            </div>
            <div className="buddy-workspace-content">
                <Outlet />
            </div>
        </div>
    )
}

export default WorkspaceLayout