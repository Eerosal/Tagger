import { useContext, useEffect, useState } from "react";
import axios from "axios";
import { JwtTokenContext } from "./Authentication";

interface PrivateVideoProps {
    src: string,
    className?: string
}

function PrivateVideo(props: PrivateVideoProps) {
    const { src, className } = props; // TODO: abstract
    const { jwtToken } = useContext(JwtTokenContext);
    const [dataUrl, setDataUrl] = useState<string>(null);


    useEffect(() => {
        if (!src || src.length === 0) {
            return;
        }

        if (dataUrl && dataUrl.length !== 0) {
            return;
        }

        (async () => {
            const response = await axios.get(src, {
                responseType: "blob",
                headers: { Authorization: `Bearer ${jwtToken}` }
            });

            const reader = new window.FileReader();
            reader.readAsDataURL(response.data);
            reader.onload = () => {
                setDataUrl(reader.result.toString());
            };
        })();
    }, [jwtToken, dataUrl, src]);

    if (dataUrl && dataUrl.length > 0) {
        return (
            <video className={className} controls>
                <source src={dataUrl} type="video/mp4" />
                <track kind="captions" />
            </video>
        );
    }

    return null;
}

PrivateVideo.defaultProps = {
    className: ""
};

export default PrivateVideo;
