import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { AxiosError, AxiosResponse } from "axios";
import { useLocation } from "react-router-dom";
import { useSetAuthResponse } from "./AuthenticationProvider";

type HandledError = AxiosError | Error;
type ErrorConsumer = (error: HandledError | string) => void;

interface ClientError {
    errorMessage: string
}

interface ErrorHandlingContextState {
    clientError: ClientError,
    setError: ErrorConsumer
}

const ErrorHandlingContext =
    createContext<ErrorHandlingContextState>(null);

export const useClientError = (): ClientError => {
    const { clientError } = useContext(ErrorHandlingContext);

    return clientError;
};

export const useSetError = (): ErrorConsumer => {
    const { setError } = useContext(ErrorHandlingContext);

    return setError;
};

interface ErrorHandlingProviderProps {
    children: JSX.Element | JSX.Element[];
}

export function ErrorHandlingProvider(
    props: ErrorHandlingProviderProps) {
    const { children } = props;
    const [clientError, setClientError] = useState<ClientError>(null);
    const setAuthResponse = useSetAuthResponse();
    const location = useLocation();

    useEffect(() => {
        setClientError(null);
    }, [location]);

    const getErrorStr = (error: HandledError): string => {
        if (typeof error === "string") {
            return error;
        }

        if ("response.data.error" in error) {
            return (error as any).response.data.error as string;
        }

        return error.toString();
    };

    const setError: ErrorConsumer = (error) => {
        let errorStr: string = null;
        if(typeof error === "string") {
            errorStr = `Error: ${  error}`;
        } else {
            if("response" in error){
                const response = (error.response as AxiosResponse);
                const responseCode = response.status;

                // Unauthorized => reset login
                if(responseCode === 401){
                    setAuthResponse(null);

                    return;
                }

                if("error" in response.data) {
                    errorStr = `Error: ${response.data.error}`;
                }
            }

            if(errorStr == null){
                errorStr = getErrorStr(error);
            }
        }

        setClientError({
            errorMessage: errorStr
        });
    };

    const value = useMemo(() => (
        {
            clientError,
            setError
        }
    ), [clientError]);

    return (
        <ErrorHandlingContext.Provider value={value}>
            {children}
        </ErrorHandlingContext.Provider>
    );
}
