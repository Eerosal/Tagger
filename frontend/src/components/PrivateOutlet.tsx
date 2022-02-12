import { Navigate , Outlet } from "react-router-dom";
import { useJwtToken } from "./AuthenticationProvider";


export default function PrivateOutlet() {
    const jwtToken = useJwtToken();

    return jwtToken && jwtToken.length > 0
        ? <Outlet /> : <Navigate to="/login" />
}

