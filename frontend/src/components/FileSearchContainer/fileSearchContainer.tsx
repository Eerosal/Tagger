import VideoThumbnail from "./thumbnails/videoThumbnail.svg";
import "./fileSearchContainer.css";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { TaggerFile, TaggerFileQueryResponse } from "../../common/types";
import filesService from "../../services/filesService";
import Paginator from "../Paginator/paginator";
import Spinner from "../Spinner/spinner";

interface ThumbnailProps {
    result: TaggerFile;
}

function Thumbnail(props: ThumbnailProps) {
    const { result } = props;

    switch (result.extension) {
    case "png":
    case "jpg":
    case "gif":
        return (
            <img
                src={
                    `${MINIO_URL}/tg-thumbnails/`
                        + `${result.id}_thumbnail.jpg`
                }
                alt={
                    result.name
                }
            />
        );
        break;
    case "mp4":
        return (
            <>
                <div>
                    <img
                        src={VideoThumbnail}
                        alt={
                            result.name
                        }
                    />
                </div>
                <div className="videoFilename">
                    <p>{result.name}</p>
                </div>
            </>
        );
        break;
    }

    return null;
}

interface SearchProps {
    query: string,
    page: number,
    pageSize: number,
}

const { REACT_APP_MINIO_URL: MINIO_URL } = process.env;

export default function FileSearchContainer(props: SearchProps) {
    const { query, page, pageSize } = props;
    const [response, setResponse] = useState<TaggerFileQueryResponse>({});
    const [totalPageCount, setTotalPageCount] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        if (!query || query.length === 0) {
            return;
        }

        const fullQuery =
            `${query} page_size:${pageSize} page:${page}`;

        setLoading(true);
        (async () => {
            const newResponse = await filesService.query(fullQuery);

            setTotalPageCount(
                Math.ceil(
                    newResponse.totalResultsCount / pageSize
                )
            );

            setResponse(newResponse);
            setLoading(false);
        })();
    }, [page, pageSize, query]);

    if (loading) {
        return <Spinner />;
    }

    return (
        <>
            {
                loading ?
                    <Spinner />
                    :
                    <div className="searchResults">
                        {
                            response.results &&
                            response.results.map(result => (
                                <Link
                                    to={`/files/${result.id}`}
                                    key={result.id}
                                >
                                    <div
                                        className="searchResult"
                                    >

                                        <Thumbnail
                                            result={result}
                                        />
                                    </div>
                                </Link>
                            ))
                        }
                    </div>
            }
            {
                response.totalResultsCount > 0 &&
                <Paginator
                    page={page}
                    totalPageCount={totalPageCount}
                    path="/search"
                />
            }
        </>
    );
}
