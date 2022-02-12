import { Dispatch, SetStateAction, useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import { useSetAuthResponse } from "../components/AuthenticationProvider";

export default function Login() {
    const setAuthResponse = useSetAuthResponse();

    const navigate = useNavigate();

    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");

    const attemptLogin = async () => {
        try {
            const authResponse = await authService.login({
                username,
                password
            });

            if (authResponse) {
                setAuthResponse(authResponse);

                navigate("/");
            }
        } catch (e) {
            if (e.response
                && e.response.data.error) {
                alert(e.response.data.error);
            } else {
                alert(e);
            }

            setAuthResponse(null);
        }

    };

    return (
        <main>
            <h2>Login</h2>
            <label>Username<br />
                <input
                    type="text"
                    value={username}
                    onChange={(event) => {
                        setUsername(event.target.value);
                    }}
                    maxLength={256}
                />
            </label>
            <br />
            <label>
                Password<br />
                <input
                    type="password"
                    value={password}
                    onChange={(event) => {
                        setPassword(event.target.value);
                    }}
                    maxLength={256}
                />
            </label>
            <br />
            <br />
            <button type="button" onClick={attemptLogin}>
                Login
            </button>
        </main>
    );
}
