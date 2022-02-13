import axios from "axios";
import { TaggerUser } from "../common/types";

const getAll = async (token: string): Promise<TaggerUser[]> => {
    const response = await axios.get("/api/users", {
        headers: { Authorization: `Bearer ${token}` }
    });

    return response.data as TaggerUser[];
};

interface CreateUserForm {
    username: string,
    password: string
}

const create = async (token: string, form: CreateUserForm):
    Promise<TaggerUser> => {
    const response = await axios.post("/api/users", form, {
        headers: { Authorization: `Bearer ${token}` }
    });

    return response.data as TaggerUser;
};

const deleteById = async (token: string, id: number): Promise<void> => {
    await axios.delete(`/api/users/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
    });
};

interface EditUserForm {
    username?: string,
    password?: string
}

const editById = async (token: string, id: number, form: EditUserForm):
    Promise<TaggerUser> => {
    const response = await axios.patch(`/api/users/${id}`, form, {
        headers: { Authorization: `Bearer ${token}` }
    });

    return response.data;
}

const actions = {
    getAll,
    create,
    deleteById,
    editById
};

export default actions;
