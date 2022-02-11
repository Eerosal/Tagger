import { useContext, useEffect, useState } from "react";
import axios from "axios";
import { JwtTokenContext } from "./Authentication";

interface PrivateImageProps {
    src: string,
    alt: string,
    className?: string
}

function PrivateImage(props: PrivateImageProps) {
    const { src, alt, className } = props;
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
            <img
                src={dataUrl}
                alt={alt}
                className={className}
            />
        );
    }

    return null;
}

PrivateImage.defaultProps = {
    className: ""
};

export default PrivateImage;
