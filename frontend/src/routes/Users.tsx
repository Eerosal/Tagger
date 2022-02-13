import { useEffect, useState } from "react";
import { useJwtToken } from "../components/AuthenticationProvider";
import { TaggerUser } from "../common/types";
import userService from "../services/userService";
import { useSetError } from "../components/ErrorHandlingProvider";

const USERNAME_REGEX = /^[a-z0-9_]+$/i;

const getUsername = async (): Promise<string> => {
    const name = prompt(
        "Enter name for the user. "
        + "Allowed characters are a-z, 0-9 and _"
    );

    if (!name || name.length === 0) {
        throw new Error("Username cannot be empty.");
    }

    if (name.length > 128) {
        throw new Error(
            "Username is too long. Maximum length is 128 characters"
        );
    }

    if (!name.match(USERNAME_REGEX)) {
        throw new Error(
            "Username is invalid. "
            + "Allowed characters are a-z, 0-9 and _"
        );
    }

    return name;
};

const getPassword = async (): Promise<string> => {
    const password = prompt("Enter password for the new user.");

    if (!password || password.length === 0) {
        throw new Error("Password cannot be empty");
    }

    if (password.length > 128) {
        throw new Error(
            "Password is too long. Maximum length is 128 characters"
        );
    }

    return password;
};


export default function Users() {
    const jwtToken = useJwtToken();
    const setError = useSetError();
    const [users, setUsers] = useState<TaggerUser[]>(null);

    const refreshUserList = async () => {
        try {
            const newUserArray = await userService.getAll(jwtToken);

            setUsers(newUserArray);
        } catch (e) {
            setError(e);
        }
    };

    const createUser = async () => {
        try {
            await userService.create(jwtToken, {
                username: await getUsername(),
                password: await getPassword()
            });
        } catch (e) {
            setError(e);

            return;
        }

        await refreshUserList();
    };

    const deleteUser = async (id: number) => {
        try {
            await userService.deleteById(jwtToken, id);
        } catch (e) {
            setError(e);
        }

        await refreshUserList();
    };

    const editUser = async (id: number, fieldName: string) => {
        try {
            let form = null;
            if (fieldName === "username") {
                form = {
                    username: await getUsername()
                };
            } else if (fieldName === "password") {
                form = {
                    password: await getPassword()
                };
            } else {
                alert("editUser() error: invalid field name");

                return;
            }

            await userService.editById(jwtToken, id, form);

            alert("Changes applied successfully");

            await refreshUserList();
        } catch (e) {
            setError(e);
        }
    };

    useEffect(() => {
        refreshUserList().then();
    }, []);

    if (users == null) {
        return null;
    }

    let child;
    if (users.length === 0) {
        child = <h3>No users found</h3>;
    } else {
        child = <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>NAME</th>
                    <th>ROLE</th>
                </tr>
            </thead>
            <tbody>
                {
                    users.map((user, i) => (
                        <tr key={user.id}>
                            <td>
                                {user.id}
                            </td>
                            <td>
                                {user.name}
                            </td>
                            <td>
                                {user.role}
                            </td>
                            <td>
                                <button
                                    type="button"
                                    onClick={async () => {
                                        await editUser(user.id, "username");
                                    }}
                                >
                                set username
                                </button>
                            &nbsp;
                                <button
                                    type="button"
                                    onClick={async () => {
                                        await editUser(user.id, "password");
                                    }}
                                >
                                set password
                                </button>
                            &nbsp;
                                <button
                                    type="button"
                                    onClick={async () => {
                                        await deleteUser(user.id);
                                    }}
                                >
                                delete
                                </button>
                            </td>
                        </tr>
                    ))
                }
            </tbody>
        </table>;
    }

    return (
        <>
            <br />
            <button
                type="button"
                onClick={() => createUser()}
            >
                create user
            </button>
            {child}
        </>
    );
}
