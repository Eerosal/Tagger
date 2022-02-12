import "./FileContainer.css";
import { TaggerFile } from "../common/types";
import PrivateImage from "./PrivateImage";
import PrivateVideo from "./PrivateVideo";
import { PrivateSourceProvider } from "./PrivateSourceProvider";

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

    let child = null;
    switch (file.extension) {
        case "png":
        case "jpg":
        case "gif":
            child = <PrivateImage
                className="file imageFile"
                alt={file.name}
            />
            break;
        case "mp4":
            child = <PrivateVideo
                className="file videoFile"
            />;
    }

    if(child == null){
        return null;
    }

    return (
        <PrivateSourceProvider src={fileUrl}>
            {child}
        </PrivateSourceProvider>
    )
}
