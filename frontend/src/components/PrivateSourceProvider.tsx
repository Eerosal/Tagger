import { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";
import { JwtTokenContext } from "./AuthenticationProvider";

export const PrivateSourceContext = createContext<string>("");

interface PrivateSourceProviderProps {
    src: string,
    children: JSX.Element | JSX.Element[]
}

export function PrivateSourceProvider(
    props: PrivateSourceProviderProps) {
    const { src, children } = props;
    const { jwtToken } = useContext(JwtTokenContext);
    const [dataUrlSrc, setDataUrlSrc] = useState<string>("");

    useEffect(() => {
        if (!src || src.length === 0) {
            return;
        }

        (async () => {
            const response = await axios.get(src, {
                responseType: "blob",
                headers: { Authorization: `Bearer ${jwtToken}` }
            });

            const reader = new window.FileReader();
            reader.readAsDataURL(response.data);
            reader.onload = () => {
                setDataUrlSrc(reader.result.toString());
            };
        })();
    }, [jwtToken, src]);


    if(!dataUrlSrc || dataUrlSrc.length === 0){
        return null;
    }

    return (
        <PrivateSourceContext.Provider value={dataUrlSrc}>
            {children}
        </PrivateSourceContext.Provider>
    )
}
