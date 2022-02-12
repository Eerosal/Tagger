import "./FileSearch.css";
import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { TaggerFile, TaggerFileQueryResponse } from "../common/types";
import filesService from "../services/filesService";
import Paginator from "./Paginator";
import VideoThumbnail from "../assets/img/video_thumbnail.svg";
import Spinner from "./Spinner";
import PrivateImage from "./PrivateImage";
import { PrivateSourceProvider } from "./PrivateSourceProvider";
import { useJwtToken } from "./AuthenticationProvider";

interface FileSearchThumbnailProps {
    file: TaggerFile;
}

function FileSearchThumbnail(props: FileSearchThumbnailProps) {
    const { file } = props;

    let child = null;
    switch (file.extension) {
        case "jpg":
        case "png":
        case "gif": {
            const thumbnailUrl =
                `/static/${file.id}_thumbnail.jpg`;

            child = <PrivateSourceProvider
                src={thumbnailUrl}
            >
                <PrivateImage
                    alt={
                        file.name
                    }
                />
            </PrivateSourceProvider>;
            break;
        }
        case "mp4":
            child = <div
                className="file-search-thumbnail__video-thumbnail"
            >
                <img
                    src={VideoThumbnail}
                    alt={
                        file.name
                    }
                />
                <p className="file-search-thumbnail__filename">
                    {file.name}
                </p>
            </div>;
            break;
    }

    if (child === null) {
        return null;
    }

    return <Link to={`/files/${file.id}`}>
        {child}
    </Link>;
}

interface FileSearchProps {
    query: string,
    page: number,
    pageSize?: number,
    paginator?: boolean,
}

function FileSearch(props: FileSearchProps) {
    const { query, page, pageSize, paginator } = props;
    const jwtToken = useJwtToken();


    const [response, setResponse] =
        useState<TaggerFileQueryResponse>(null);

    useEffect(() => {
        if (!query || !page) {
            setResponse(null);

            return;
        }

        const fullQuery = `${query} page:${page} page_size:${pageSize}`;

        (async () => {
            try {
                const newResponse =
                    await filesService.query(jwtToken, fullQuery);

                setResponse(newResponse);
            } catch (e) {
                alert(e);
            }
        })();
    }, [query, page, pageSize]);

    return response ?
        <div className="file-search">

            <div className="file-search__results-container">
                {
                    response.results.map((result) => (
                        <div
                            key={result.id}
                            className="file-search__result"
                        >
                            <FileSearchThumbnail
                                file={result}
                            />
                        </div>
                    ))
                }
            </div>
            {
                paginator &&
                <Paginator
                    currentPage={page}
                    pageCount={
                        Math.ceil(response.totalResultsCount / pageSize)
                    }
                />
            }
        </div>
        :
        <Spinner />;
}

FileSearch.defaultProps = {
    pageSize: 24,
    paginator: true
};

export default FileSearch;
