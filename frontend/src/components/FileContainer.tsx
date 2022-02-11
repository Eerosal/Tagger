import "./FileContainer.css";
import { TaggerFile } from "../common/types";
import PrivateImage from "./PrivateImage";
import PrivateVideo from "./PrivateVideo";

interface FileContainerProps {
    file: TaggerFile;
}

export default function FileContainer(props: FileContainerProps) {
    const { file } = props;

    if (!file) {
        return null;
    }

    const internalFilename = `${file.id}.${file.extension}`;

    const fileUrl = `/static/${internalFilename}`;

    switch (file.extension) {
        case "png":
        case "jpg":
        case "gif":
            return (
                <PrivateImage
                    src={fileUrl}
                    className="file imageFile"
                    alt={file.name}
                />
            );
        case "mp4":
            return (
                <PrivateVideo
                    src={fileUrl}
                    className="file videoFile"
                />
            );
    }

    return null;
}
