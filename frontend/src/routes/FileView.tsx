import "./FileView.css";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import React, { useContext, useEffect, useState } from "react";
import filesService from "../services/filesService";
import {
    FileViewState, TaggerFileResponse
} from "../common/types";
import tagsService from "../services/tagsService";
import FileContainer from "../components/FileContainer";
import { JwtTokenContext } from "../components/AuthenticationProvider";

interface TagContainerProps {
    response: TaggerFileResponse,
    setResponse: React.Dispatch<React.SetStateAction<TaggerFileResponse>>
}

function TagContainer(props: TagContainerProps) {
    const { response, setResponse } = props;
    const { jwtToken } = useContext(JwtTokenContext);

    const removeTag = async (tagId: number) => {
        const newResponse =
            await filesService.removeTags(
                jwtToken, response.file.id, [tagId]
            );

        setResponse(newResponse);
    };

    const addTags = async () => {
        const tagInput = prompt("Enter new tags separated by spaces");

        const newTagNames = tagInput.split(" ");
        const newTagIds = await tagsService.getByNamesOrCreate(
            jwtToken,
            newTagNames
        );

        const newResponse =
            await filesService.addTags(
                jwtToken,
                response.file.id,
                newTagIds.map(tag => tag.id)
            );

        setResponse(newResponse);
    };

    return (
        <ul className="tag-container">
            {
                response.tags.map((tag) => (
                    <li key={tag.id}
                        className="tag-container__tag-entry">
                        <a
                            href="#"
                            onClick={() => removeTag(tag.id)}
                        >
                            -
                        </a>
                        &nbsp;
                        <Link
                            to={`/search?query=${tag.name}`}
                        >
                            {tag.name}
                        </Link>
                    </li>
                ))
            }
            <li className="tag-container__tag-entry">
                <a
                    href="#"
                    onClick={addTags}
                >
                    + add tags
                </a>
            </li>
        </ul>
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
    const { jwtToken } = useContext(JwtTokenContext);

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
                        alert(e);
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
