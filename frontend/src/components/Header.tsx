import "./Header.css";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { useClientError } from "./ErrorHandlingProvider";

interface NavbarEntry {
    name: string,
    path: string,
}

const navbarEntries: NavbarEntry[] = [
    {
        name: "Home",
        path: "/"
    },
    {
        name: "Search",
        path: "/search"
    },
    {
        name: "Upload",
        path: "/upload"
    },
    {
        name: "Users",
        path: "/users"
    }
];

function Navbar() {
    return (
        <div className="navbar">
            {
                navbarEntries.map((navEntry) => (
                    <Link
                        className="navbar__entry"
                        to={navEntry.path}
                        key={navEntry.name}
                    >
                        {navEntry.name}
                    </Link>
                ))
            }
        </div>
    );
}

export default function Header() {
    const clientError = useClientError();
    const [showError, setShowError] = useState<boolean>(false);
    const [errorBackgroundColor, setErrorBackgroundColor] =
        useState<string>("rgba(255, 0, 0, 0)");

    useEffect(() => {
        if (!clientError
            || clientError.errorMessage.length === 0) {
            setShowError(false);

            return null;
        }

        setShowError(true);

        let i = 10;

        let interval: number = null;
        let timeout: number = null;
        interval = window.setInterval(() => {
            if (i === 0) {
                window.clearInterval(interval);

                interval = null;

                timeout = window.setTimeout(() => {
                    setShowError(false);
                }, 4000);
            }

            const alpha = (i / 10);

            const newBackgroundColor = `rgba(255, 0, 0, ${alpha})`;

            setErrorBackgroundColor(newBackgroundColor);

            i -= 1;
        }, 50);
        return () => {
            if (interval) {
                window.clearInterval(interval);
            }

            if (timeout) {
                window.clearTimeout(timeout);
            }
        };
    }, [clientError]);

    return (
        <>
            <Navbar />
            {
                showError && clientError !== null?
                    <div
                        style={{
                            backgroundColor: errorBackgroundColor
                        }}
                    >
                        <h5 className="header__error-message">
                            {clientError.errorMessage}
                        </h5>
                    </div>
                    : null
            }
        </>
    );
}
