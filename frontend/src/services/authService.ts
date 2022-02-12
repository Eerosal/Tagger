import axios from "axios";
import { TaggerAuthorizationResponse } from "../common/types";

interface LoginForm {
    username: string,
    password: string
}

const login = async (loginForm: LoginForm):
    Promise<TaggerAuthorizationResponse> => {
    const response = await axios.post("/authorize", loginForm);

    const data = await response.data;

    return data as TaggerAuthorizationResponse;
}

const renewToken = async (token: string):
    Promise<TaggerAuthorizationResponse> => {
    const response = await axios.post("/renew-token", {}, {
        headers: { Authorization: `Bearer ${token}`}
    });

    const data = await response.data;

    return data as TaggerAuthorizationResponse;
}

const actions = {
    login,
    renewToken
};


export default actions;

