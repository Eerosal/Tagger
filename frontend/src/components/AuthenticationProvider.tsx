import React, { useContext, useEffect, useMemo, useState } from "react";
import Spinner from "./Spinner";
import { Session, TaggerAuthorizationResponse } from "../common/types";
import authService from "../services/authService";

let JWT_TOKEN_TTL: number = null;

type AuthResponseConsumer = (auth: TaggerAuthorizationResponse) => void;

interface AuthenticationContextState {
    jwtToken: string,
    setAuthResponse: AuthResponseConsumer
}

const AuthenticationContext =
    React.createContext<AuthenticationContextState>(null);

export const useJwtToken = (): string => {
    const { jwtToken } = useContext(AuthenticationContext);

    return jwtToken;
}

export const useSetAuthResponse = (): AuthResponseConsumer => {
    const { setAuthResponse } = useContext(AuthenticationContext);

    return setAuthResponse;
}

interface AuthenticationProps {
    children: JSX.Element | JSX.Element[];
}

export function AuthenticationProvider(props: AuthenticationProps) {
    const { children } = props;

    const [ready, setReady] = useState<boolean>(false);
    const [authenticated, setAuthenticated] = useState<boolean>(false);

    const [jwtToken, setJwtToken] = useState<string>("");

    const setAuthResponse = (authResponse: TaggerAuthorizationResponse) => {
        if(authResponse == null){
            setJwtToken("");

            return;
        }

        setJwtToken(authResponse.token);

        JWT_TOKEN_TTL = authResponse.lifetimeSeconds;
    };

    const value = useMemo(() => (
        {
            jwtToken,
            setAuthResponse
        }
    ), [jwtToken]);

    const getSession = () => JSON.parse(
        localStorage.taggerSession || "{}"
    ) as Session;

    const renewToken = async (session: Session) => {
        if (!session) {
            setJwtToken("");

            return;
        }

        try {
            const authResponse =
                await authService.renewToken(session.token);

            if (authResponse) {
                setAuthResponse(authResponse);
                return;
            }
        } catch (e) {
            console.log(e);
        }

        setJwtToken("");
    };

    useEffect(() => {
        if (!jwtToken
            || jwtToken.length === 0) {
            setAuthenticated(false);
        } else {
            const newSession = {
                token: jwtToken,
                updatedAt: Math.floor(new Date().getTime() / 1000)
            } as Session;

            localStorage.taggerSession = JSON.stringify(
                newSession
            );

            setAuthenticated(true);
        }
    }, [jwtToken]);

    useEffect(() => {
        if (!authenticated) {
            return null;
        }

        const interval = window.setInterval(async () => {
            const session = getSession();

            if (!session) {
                setJwtToken("");
                setAuthenticated(false);

                return;
            }

            if(JWT_TOKEN_TTL) {
                const now = new Date().getTime() / 1000;
                const lastUpdate = session.updatedAt;

                const diff = (now - lastUpdate);
                if (diff < JWT_TOKEN_TTL / 3) {
                    return;
                }
            }

            await renewToken(session);
        }, 15000);

        return () => {
            window.clearInterval(interval);
        };
    }, [authenticated]);

    useEffect(() => {
        (async () => {
            await renewToken(getSession());

            setReady(true);
        })();
    }, []);

    if (ready) {
        return (
            <AuthenticationContext.Provider value={value}>
                {children}
            </AuthenticationContext.Provider>
        );
    }

    return (
        <main>
            <Spinner />
        </main>
    );
}

