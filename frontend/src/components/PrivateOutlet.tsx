import { Navigate , Outlet } from "react-router-dom";
import { useContext, useEffect } from "react";
import { JwtTokenContext } from "./Authentication";


export default function PrivateOutlet() {
    const { jwtToken, setJwtToken } = useContext<any>(JwtTokenContext);


    return jwtToken && jwtToken.length > 0
        ? <Outlet /> : <Navigate to="/login" />
}

