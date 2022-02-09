import "./FileContainer.css";
import { TaggerFile } from "../common/types";

interface FileContainerProps {
    file: TaggerFile;
}

const { REACT_APP_MINIO_URL: MINIO_URL } = process.env;

export default function FileContainer(props: FileContainerProps) {
    const { file } = props;

    if (!file) {
        return null;
    }

    const internalFilename = `${file.id}.${file.extension}`;

    const fileUrl = `${MINIO_URL}/tg-files/${internalFilename}`;

    switch (file.extension) {
        case "png":
        case "jpg":
        case "gif":
            return (
                <img
                    src={fileUrl}
                    className="file imageFile"
                    alt={file.name}
                />
            );
        case "mp4":
            return (
                <video className="file videoFile" controls>
                    <source src={fileUrl} type="video/mp4" />
                    <track kind="captions" />
                </video>
            );
    }

    return null;
}
