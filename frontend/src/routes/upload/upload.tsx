import "./upload.css";
import { ChangeEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as React from "react";
import filesService from "../../services/filesService";

export default function Upload() {
    const [filename, setFilename] = useState("");
    const navigate = useNavigate();

    const onUploadFormSubmit = async (
        event: React.SyntheticEvent) => {
        event.preventDefault();

        if (!filename) {
            alert("No input filename specified");

            return;
        }

        let uploadedFileResponse;
        try {
            uploadedFileResponse = await filesService.upload({
                filename
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
                <div className="uploadForm">
                    <label>
                        Filename<br />
                        <input
                            type="text"
                            id="inputFilename"
                            className="uploadFormTextInput"
                            value={filename}
                            onChange={
                                (event: ChangeEvent<HTMLInputElement>) => {
                                    setFilename(event.target.value);
                                }
                            }
                        />
                    </label>
                    <br />
                    <input type="submit" value="Upload" />
                </div>
            </form>
        </main>
    );
};
