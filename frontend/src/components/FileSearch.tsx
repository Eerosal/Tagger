import "./FileSearch.css";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { TaggerFile, TaggerFileQueryResponse } from "../common/types";
import filesService from "../services/filesService";
import Paginator from "./Paginator";
import VideoThumbnail from "../assets/img/video_thumbnail.svg";
import Spinner from "./Spinner";

const { REACT_APP_MINIO_URL: MINIO_URL } = process.env;

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
                `${MINIO_URL}/tg-thumbnails/${file.id}_thumbnail.jpg`;

            child = <img
                src={thumbnailUrl}
                alt={
                    file.name
                }
            />;
            break;
        }
        case "mp4":
            child = <>
                <div>
                    <img
                        src={VideoThumbnail}
                        alt={
                            file.name
                        }
                    />
                </div>
                <div className="videoFilename">
                    <p>{file.name}</p>
                </div>
            </>;
            break;
    }

    if (child != null) {
        return <Link to={`/files/${file.id}`}>
            <div className="file-search__result">
                {child}
            </div>
        </Link>;

    }

    return null;
}

interface FileSearchProps {
    query: string,
    page: number,
    pageSize?: number,
    paginator?: boolean,
}

function FileSearch(props: FileSearchProps) {
    const { query, page, pageSize, paginator } = props;

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
                const newResponse = await filesService.query(fullQuery);

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
                        <FileSearchThumbnail
                            key={result.id}
                            file={result}
                        />
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
    paginator: true,
};

export default FileSearch;