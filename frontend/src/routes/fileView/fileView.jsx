import { useLocation, useParams } from "react-router-dom";
import React, { useEffect, useState } from "react";
import fileService from "../../services/filesService.js";

export default function FileView() {
    const { fileId } = useParams();
    const [fileInfo, setFileInfo] = useState({});
    const { state } = useLocation();
    const { uploadedFile } = state || {};

    useEffect(() => {
        if (uploadedFile) {
            setFileInfo({
                error: false,
                file: uploadedFile
            });
        } else {
            (async () => {
                try {
                    const file = await fileService.getById(fileId);

                    setFileInfo({
                        error: false,
                        file
                    });
                } catch (e) {
                    let errorMessage;
                    if (e.response) {
                        if (e.response.status === 404) {
                            errorMessage = "File not found";
                        }
                    }

                    setFileInfo({
                        error: errorMessage || e.toString(),
                        file: null
                    });
                }
            })();
        }

    }, [uploadedFile, fileId]);

    return (
        <main>
            <h2>File view</h2>
            {
                fileInfo.error &&
                <h3>Error: {fileInfo.error}</h3>
            }
            {
                fileInfo.file &&
                <h2>{fileInfo.file.name}</h2>
            }
        </main>
    );
}
