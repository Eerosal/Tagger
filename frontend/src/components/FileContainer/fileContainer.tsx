import "./fileContainer.css";
import { TaggerFileResponse } from "../../common/types";

interface FileContainerProps {
    response: TaggerFileResponse
}

const {REACT_APP_MINIO_URL: MINIO_URL} = process.env;

export default function FileContainer(props: FileContainerProps){
    const { response } = props;

    if(!response || !response.file){
        return null;
    }

    const internalFilename = `${response.file.id}.${response.file.extension}`;

    const fileUrl = `${MINIO_URL}/tg-files/${internalFilename}`;

    switch(response.file.extension){
    case "png":
    case "jpg":
    case "gif":
        return (
            <img src={fileUrl} className="file imageFile" />
        )
        break;
    case "mp4":
        return (
            <video className="file videoFile" controls>
                <source src={fileUrl} type="video/mp4"/>
                <track kind="captions" />
            </video>
        )
        break;
    }

    return null;
}
