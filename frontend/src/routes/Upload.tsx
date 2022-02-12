import "./Upload.css";
import { ChangeEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as React from "react";
import filesService from "../services/filesService";
import { useJwtToken } from "../components/AuthenticationProvider";

export default function Upload() {
    const [filename, setFilename] = useState("");
    const fileInput = React.createRef<HTMLInputElement>();
    const navigate = useNavigate();
    const jwtToken = useJwtToken();

    const onUploadFormSubmit = async (
        event: React.SyntheticEvent) => {
        event.preventDefault();

        if(!fileInput.current || fileInput.current.files.length === 0) {
            alert("No file specified");

            return;
        }

        let uploadFilename;
        if(filename && filename.length > 0){
            uploadFilename = filename;
        } else {
            uploadFilename = fileInput.current.files[0].name;
        }

        let uploadedFileResponse;
        try {
            uploadedFileResponse = await filesService.upload(jwtToken,{
                filename: uploadFilename,
                file: fileInput.current.files[0]
            });
        } catch (e) {
            alert(e);

            return;
        }

        if (uploadedFileResponse) {
            navigate(
                `/files/${uploadedFileResponse.file.id}`,
                {
                    state: {
                        uploadedFileResponse
                    }
                }
            );
        }
    };

    return (
        <main>
            <h2>Upload</h2>
            <form onSubmit={onUploadFormSubmit}>
                <div className="upload__upload-box">
                    <label>
                        Filename<br />
                        <input
                            type="text"
                            className="upload__upload-name-input"
                            value={filename}
                            onChange={
                                (event: ChangeEvent<HTMLInputElement>) => {
                                    setFilename(event.target.value);
                                }
                            }
                        />
                    </label>
                    <br />
                    <label>
                        File<br />
                        <input
                            type="file"
                            ref={fileInput}
                        />
                    </label>
                    <br />
                    <input type="submit" value="Upload" />
                </div>
            </form>
        </main>
    );
};
