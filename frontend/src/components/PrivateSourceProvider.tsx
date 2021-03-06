import { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";
import { useJwtToken } from "./AuthenticationProvider";
import { useSetError } from "./ErrorHandlingProvider";

export const PrivateSourceContext = createContext<string>("");

interface PrivateSourceProviderProps {
    src: string,
    children: JSX.Element | JSX.Element[]
}

export function PrivateSourceProvider(
    props: PrivateSourceProviderProps) {
    const { src, children } = props;
    const jwtToken = useJwtToken();
    const setError = useSetError();
    const [dataUrlSrc, setDataUrlSrc] = useState<string>("");

    useEffect(() => {
        if (!src || src.length === 0) {
            return;
        }

        if(dataUrlSrc && dataUrlSrc.length > 0){
            return;
        }

        (async () => {
            try {
                const response = await axios.get(src, {
                    responseType: "blob",
                    headers: { Authorization: `Bearer ${jwtToken}` }
                });

                const reader = new window.FileReader();
                reader.readAsDataURL(response.data);
                reader.onload = () => {
                    setDataUrlSrc(reader.result.toString());
                };
            } catch (e){
                setError(e);
            }

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
