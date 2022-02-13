import "./FileView.css";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import React, { useEffect, useState } from "react";
import filesService from "../services/filesService";
import {
    FileViewState, TaggerFileResponse
} from "../common/types";
import tagsService from "../services/tagsService";
import FileContainer from "../components/FileContainer";
import { useJwtToken } from "../components/AuthenticationProvider";
import { useSetError } from "../components/ErrorHandlingProvider";

interface TagContainerProps {
    response: TaggerFileResponse,
    setResponse: React.Dispatch<React.SetStateAction<TaggerFileResponse>>
}

const TAG_NAME_PATTERN = /^[a-z_]+$/i;

function TagContainer(props: TagContainerProps) {
    const { response, setResponse } = props;
    const jwtToken = useJwtToken();
    const setError = useSetError();

    const removeTag = async (tagId: number): Promise<void> => {
        try {
            const newResponse =
                await filesService.removeTags(
                    jwtToken, response.file.id, [tagId]
                );

            setResponse(newResponse);
        } catch (e) {
            setError(e);
        }
    };

    const addTags = async () => {
        const tagInput = prompt("Enter new tags separated by spaces");

        const newTagNames = tagInput.split(" ");
        const newTagNamesFiltered = newTagNames.filter(
            (tagName) => tagName.match(TAG_NAME_PATTERN)
        ).map((tagName) => tagName.toLocaleLowerCase());

        if (newTagNames.length !== newTagNamesFiltered.length) {
            if (newTagNamesFiltered.length === 0) {
                setError(
                    "Invalid list of tag names. "
                    + "Tag names can only contain letters a-z and underscores."
                );

                return;
            }

            setError(
                "Some tag names were omitted. "
                + "Tag names can only contain letters a-z and underscores."
            );
        }

        const newTagIds = await tagsService.getByNamesOrCreate(
            jwtToken,
            newTagNames
        );

        try {
            const newResponse =
                await filesService.addTags(
                    jwtToken,
                    response.file.id,
                    newTagIds.map(tag => tag.id)
                );

            setResponse(newResponse);
        } catch (e) {
            setError(e);
        }
    };

    return (
        <main>
            <ul className="tag-container">
                {
                    response.tags.map((tag, i) => (
                        <li key={tag.id}
                            className="tag-container__tag-entry">
                            <a
                                role="button"
                                tabIndex={i * 2}
                                title={`Remove tag ${tag.name}`}
                                onKeyPress={
                                    (event) => {
                                        if (event.key === "Enter") {
                                            event.preventDefault();

                                            removeTag(tag.id).then();
                                        }
                                    }
                                }
                                onClick={() => removeTag(tag.id)}
                            >
                                -
                            </a>
                            &nbsp;
                            <Link
                                to={`/search?query=${tag.name}`}
                                tabIndex={i * 2 + 1}
                                title={`Search for ${tag.name}`}
                            >
                                {tag.name}
                            </Link>
                        </li>
                    ))
                }
                <li className="tag-container__tag-entry">
                    <a
                        role="button"
                        tabIndex={response.tags.length * 2}
                        onKeyPress={
                            (event) => {
                                if (event.key === "Enter") {
                                    event.preventDefault();

                                    addTags().then();
                                }
                            }
                        }
                        onClick={addTags}
                    >
                        + add tags
                    </a>
                </li>
            </ul>
        </main>
    );
}

export default function FileView() {
    const navigate = useNavigate();
    const { fileId: fileIdParam } = useParams();
    const [maximized, setMaximized] = useState<boolean>(false);
    const [response, setResponse] =
        useState<TaggerFileResponse>(null);
    const { state } = useLocation();
    const { uploadedFileResponse } = (state || {}) as FileViewState;
    const jwtToken = useJwtToken();
    const setError = useSetError();

    useEffect(() => {
        const fileId = parseInt(fileIdParam, 10);
        if (!fileId || isNaN(fileId)) {
            return;
        }

        (async () => {
            let newResponse: TaggerFileResponse;
            if (uploadedFileResponse
                && uploadedFileResponse.file.id === fileId) {
                newResponse = uploadedFileResponse;
            } else {
                try {
                    newResponse = await filesService.get(jwtToken, fileId);
                } catch (e) {
                    if (e.response
                        && e.response.status === 404) {
                        navigate("/404");
                    } else {
                        setError(e);
                    }
                }
            }

            if (newResponse
                && newResponse.file) {
                setResponse(newResponse);
            } else {
                setResponse(null);
            }
        })();
    }, [fileIdParam, navigate, uploadedFileResponse]);

    return (
        <main>
            <h2>File view</h2>
            {
                response &&
                <>
                    <h3 className="file-view__file-name">
                        {response.file.id} {response.file.name}
                    </h3>
                    <button
                        type="button"
                        onClick={
                            () => setMaximized(value => !value)
                        }
                        className="file-view__size-button"
                    >
                        {
                            maximized ? "minimize" : "maximize"
                        }
                    </button>
                    <br /><br />
                    <div
                        className={
                            `file-view__file ${
                                maximized ? "file-view__file--maximized"
                                    : "file-view__file--minimized"}`
                        }
                    >
                        <FileContainer
                            file={response.file}
                        />
                    </div>
                    <TagContainer
                        response={response}
                        setResponse={setResponse}
                    />
                </>
            }
        </main>
    );
}
