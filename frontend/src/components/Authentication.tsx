import React, { useEffect, useMemo, useState } from "react";
import Spinner from "./Spinner";
import { Session } from "../common/types";
import authService from "../services/authService";

const JWT_TOKEN_TTL = 60 * 2;

interface JwtTokenContextState {
    jwtToken: string,
    setJwtToken: (jwtToken: string) => void
}

export const JwtTokenContext =
    React.createContext<JwtTokenContextState>({
        jwtToken: "",
        setJwtToken: (jwtToken: string) => {
        }
    });

interface AuthenticationProps {
    children: JSX.Element | JSX.Element[];
}

export function Authentication(props: AuthenticationProps) {
    const { children } = props;

    const [ready, setReady] = useState<boolean>(false);
    const [authenticated, setAuthenticated] = useState<boolean>(false);

    const [jwtToken, setJwtToken] = useState<string>("");
    const value = useMemo(() => (
        { jwtToken, setJwtToken }), [jwtToken]);

    const getSession = () => JSON.parse(
        localStorage.taggerSession || "{}"
    ) as Session;

    const renewToken = async (session: Session) => {
        if (!session) {
            setJwtToken("");

            return;
        }

        try {
            const token = await authService.renewToken(session.token);

            if (token) {
                setJwtToken(token);
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

            const now = new Date().getTime() / 1000;
            const lastUpdate = session.updatedAt;

            const diff = (now - lastUpdate);
            if (diff < JWT_TOKEN_TTL / 3) {
                return;
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
            <JwtTokenContext.Provider value={value}>
                {children}
            </JwtTokenContext.Provider>
        );
    }

    return (
        <main>
            <Spinner />
        </main>
    );
}

