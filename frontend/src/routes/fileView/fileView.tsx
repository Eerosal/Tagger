import { useLocation, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import fileService from "../../services/filesService";
import { FileViewState, TaggerFile } from "../../common/types";

interface FileInfo {
    error?: string,
    file?: TaggerFile
}

export default function FileView() {
    const { fileId } = useParams();
    const [fileInfo, setFileInfo] = useState<FileInfo>({});
    const { state } = useLocation();
    const { uploadedFile } = state as FileViewState;

    useEffect(() => {
        if (uploadedFile) {
            setFileInfo({
                error: null,
                file: uploadedFile
            });
        } else {
            (async () => {
                try {
                    const file = await fileService.getById(fileId);

                    setFileInfo({
                        error: null,
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
