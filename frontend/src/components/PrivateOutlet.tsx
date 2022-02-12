import { Navigate , Outlet } from "react-router-dom";
import { useContext, useEffect } from "react";
import { JwtTokenContext } from "./AuthenticationProvider";


export default function PrivateOutlet() {
    const { jwtToken } = useContext<any>(JwtTokenContext);


    return jwtToken && jwtToken.length > 0
        ? <Outlet /> : <Navigate to="/login" />
}

