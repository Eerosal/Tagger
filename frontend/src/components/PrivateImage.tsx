import { useContext } from "react";
import { PrivateSourceContext } from "./PrivateSourceProvider";

interface PrivateImageProps {
    alt: string,
    className?: string
}

function PrivateImage(props: PrivateImageProps) {
    const { alt, className } = props;
    const dataUrlSrc = useContext<string>(PrivateSourceContext);

    if (!dataUrlSrc || dataUrlSrc.length === 0) {
        return null;
    }

    return (
        <img
            src={dataUrlSrc}
            alt={alt}
            className={className}
        />
    );
}

PrivateImage.defaultProps = {
    className: ""
};

export default PrivateImage;
