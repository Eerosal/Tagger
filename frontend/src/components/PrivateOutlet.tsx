import { Navigate , Outlet } from "react-router-dom";

interface PrivateOutletProps {
    authenticated: boolean
}

export default function PrivateOutlet(props: PrivateOutletProps) {
    const { authenticated } = props;

    return authenticated? <Outlet /> : <Navigate to="/login" />
}

