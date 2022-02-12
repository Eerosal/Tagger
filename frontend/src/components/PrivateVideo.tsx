import { useContext, useEffect, useState } from "react";
import { PrivateSourceContext } from "./PrivateSourceProvider";

interface PrivateVideoProps {
    className?: string;
}

function PrivateVideo(props: PrivateVideoProps) {
    const { className } = props;
    const dataUrlSrc = useContext<string>(PrivateSourceContext);

    if (!dataUrlSrc || dataUrlSrc.length === 0) {
        return null;
    }

    return (
        <video className={className} controls>
            <source src={dataUrlSrc} type="video/mp4" />
            <track kind="captions" />
        </video>
    );
}

PrivateVideo.defaultProps = {
    className: ""
};

export default PrivateVideo;
