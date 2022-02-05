import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { TaggerFileQueryResponse } from "../../common/types";
import filesService from "../../services/filesService";
import Paginator from "../Paginator/paginator";
import Spinner from "../Spinner/spinner";

interface SearchProps {
    query: string,
    page: number,
    pageSize: number,
}

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

    if(loading){
        return <Spinner />;
    }

    return (
        <>
            {
                loading?
                    <Spinner />
                    :
                    <div className="searchResults">
                        {
                            response.results &&
                            response.results.map(result => (
                                <div
                                    key={result.id}
                                    className="searchResult"
                                >
                                    <Link to={`/files/${result.id}`}>
                                        <h4>{result.id} {result.name}</h4>
                                    </Link>
                                </div>
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
