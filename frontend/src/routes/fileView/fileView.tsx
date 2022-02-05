import "./fileView.css";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import React, { useEffect, useState } from "react";
import filesService from "../../services/filesService";
import {
    FileViewState, TaggerFileResponse } from "../../common/types";
import tagsService from "../../services/tagsService";

interface TagContainerProps {
    response: TaggerFileResponse,
    setResponse: React.Dispatch<React.SetStateAction<TaggerFileResponse>>
}

function TagContainer(props: TagContainerProps) {
    const { response, setResponse } = props;

    const removeTag = async (tagId: number) => {
        const newResponse =
            await filesService.removeTags(response.file.id, [tagId]);

        setResponse(newResponse);
    }

    const addTags = async () => {
        const tagInput = prompt("Enter new tags separated by spaces");

        const newTagNames = tagInput.split(" ");
        const newTagIds = await tagsService.getByNamesOrCreate(newTagNames);

        const newResponse =
            await filesService.addTags(
                response.file.id,
                newTagIds.map(tag => tag.id)
            );

        setResponse(newResponse);
    }

    return (
        <ul className="tagContainer">
            {
                response.tags.map((tag) => (
                    <li key={tag.id}
                        className="tagContainerEntry">
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
            <li className="tagContainerEntry">
                <a
                    href="#"
                    onClick={addTags}
                >
                    + add tags
                </a>
            </li>
        </ul>
    )
}

export default function FileView() {
    const navigate = useNavigate();
    const { fileId: fileIdParam } = useParams();
    const [response, setResponse] =
        useState<TaggerFileResponse>(null);
    const { state } = useLocation();
    const { uploadedFileResponse } = (state || {}) as FileViewState;

    useEffect(() => {
        const fileId = parseInt(fileIdParam, 10);
        if(!fileId || isNaN(fileId)){
            return;
        }

        (async () => {
            let newResponse: TaggerFileResponse;
            if(uploadedFileResponse
                && uploadedFileResponse.file.id === fileId){
                newResponse = uploadedFileResponse;
            } else {
                try {
                    newResponse = await filesService.get(fileId);
                } catch (e){
                    if(e.response
                        && e.response.status === 404){
                        navigate("/404");
                    } else {
                        alert(e);
                    }
                }
            }

            if(newResponse
                && newResponse.file){
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
                    <h3>
                        {response.file.id} {response.file.name}
                    </h3>
                    <TagContainer
                        response={response}
                        setResponse={setResponse}
                    />
                </>
            }
        </main>
    );
}
