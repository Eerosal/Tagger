import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import fileService from "../../services/filesService";
import { FileViewState, TaggerFile } from "../../common/types";

interface FileInfo {
    error?: string,
    file?: TaggerFile
}

export default function FileView() {
    const navigate = useNavigate();
    const { fileId: fileIdParam } = useParams();
    const [currentFile, setCurrentFile] = useState<TaggerFile>(null);
    const { state } = useLocation();
    const { uploadedFile } = (state || {}) as FileViewState;

    useEffect(() => {
        const fileId = parseInt(fileIdParam, 10);
        if(!fileId || isNaN(fileId)){
            return;
        }

        (async () => {
            let newFile;
            if(uploadedFile
                && uploadedFile.id === fileId){
                newFile = uploadedFile;
            } else {
                try {
                    newFile = await fileService.getById(fileId)
                } catch (e){
                    if(e.response
                        && e.response.status === 404){
                        navigate("/404");
                    } else {
                        alert(e);
                    }
                }
            }

            setCurrentFile(newFile);
        })();
    }, [fileIdParam, uploadedFile]);

    return (
        <main>
            <h2>File view</h2>
            {
                currentFile &&
                <h3>{currentFile.id} {currentFile.name}</h3>
            }
        </main>
    );
}
