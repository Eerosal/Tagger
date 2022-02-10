import axios from "axios";
import jwtDecode, { JwtPayload } from "jwt-decode";

interface LoginForm {
    username: string,
    password: string
}

interface TaggerAuthorizationResponse {
    token: string
}

const login = async (loginForm: LoginForm): Promise<string> => {
    const response = await axios.post("/authorize", loginForm);

    const data = await response.data;

    const { token } = data as TaggerAuthorizationResponse;

    return token;
}

const renewToken = async (token: string): Promise<string> => {
    const response = await axios.post("/renew-token", {}, {
        headers: { Authorization: `Bearer ${token}`}
    });

    const data = await response.data;

    const { token: newToken } = data as TaggerAuthorizationResponse;

    return newToken;
}

const actions = {
    login,
    renewToken
};


export default actions;

