import "./search.css";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { ChangeEvent, useEffect, useState } from "react";
import filesService from "../../services/filesService";
import { TaggerFileQueryResponse } from "../../common/types";

interface PaginatorProps {
    page: number,
    totalPageCount: number,
}

const PAGE_SIZE = 24;

function Paginator(props: PaginatorProps) {
    const [urlSearchParams] = useSearchParams();
    const { page, totalPageCount } = props;

    return (
        <menu>
            <ul>
                {
                    Array.from(
                        { length: 7 },
                        (_: number, i: number) => i
                    ).map(i => (
                        <li
                            className="paginatorLink"
                            key={i}
                        >
                            {
                                (() => {
                                    const lastPage =
                                        totalPageCount;

                                    const showRightDots =
                                        page < lastPage - 3;

                                    let number;
                                    if (i === 0) {
                                        number = 1;
                                    } else if (i === 6
                                        && showRightDots) {
                                        number = lastPage;
                                    } else {
                                        number = Math.max(
                                            page - 3, 1
                                        ) + i;
                                    }

                                    if (number === page) {
                                        return (
                                            <span>
                                                <b>{number}</b>
                                            </span>
                                        );
                                    }


                                    if (number > lastPage) return null;

                                    if ((i === 1
                                            && page > 3)
                                        || (i === 5 && showRightDots)) {
                                        return (
                                            <span>...</span>
                                        );
                                    }

                                    const urlParams =
                                        new URLSearchParams({
                                            query: urlSearchParams.get("query"),
                                            page: `${number}`
                                        });

                                    const path = `/search?${
                                        urlParams.toString()}`;

                                    return (
                                        <span>
                                            <Link to={path}>
                                                {number}
                                            </Link>
                                        </span>
                                    );
                                })()
                            }
                        </li>
                    ))
                }
            </ul>
        </menu>
    );
}


export default function Search() {
    const navigate = useNavigate();

    const [urlSearchParams, setUrlSearchParams] = useSearchParams();

    const getPageFromParams = () => {
        const pageUrlParam = urlSearchParams.get("page");
        if (pageUrlParam) {
            return parseInt(pageUrlParam, 10) || 1;
        }
        return 1;
    }

    const [response, setResponse] = useState<TaggerFileQueryResponse>();
    const [page, setPage] = useState<number>(getPageFromParams());
    const [totalPageCount, setTotalPageCount] = useState<number>(0);

    const [searchInputValue, setSearchInputValue] =
        useState<string>("");

    useEffect(() => {
        const newPage: number = getPageFromParams();
        setPage(
            newPage
        );

        const newQuery = urlSearchParams.get("query") || "";
        setSearchInputValue(
            newQuery
        );

        const fullQuery =
            `${newQuery} page_size:${PAGE_SIZE} page:${newPage}`;

        (async () => {
            const newResponse = await filesService.query(fullQuery);

            setTotalPageCount(
                Math.floor(
                    newResponse.totalResultsCount / PAGE_SIZE
                ) + 1
            );

            setResponse(newResponse);
        })();
    }, [urlSearchParams]);

    return (
        <main>
            <h2>Search</h2>
            <input
                type="text"
                placeholder="search terms"
                onChange={(event: ChangeEvent<HTMLInputElement>) => {
                    setSearchInputValue(event.target.value);
                }}
                value={searchInputValue}
            />
            <input
                type="submit" value="Search"
                onClick={() => {
                    const params = new URLSearchParams({
                        query: searchInputValue
                    });

                    const url = `/search?${params.toString()}`;

                    navigate(url);
                }}
            />
            {
                response &&
                <>
                    <div className="searchResults">
                        {
                            response.results &&
                            response.results.map(result => (
                                <div key={result.id} className="searchResult">
                                    <h2>{result.id} {result.name}</h2>
                                </div>
                            ))
                        }
                    </div>
                    {
                        response.totalResultsCount &&
                        <Paginator
                            page={page} totalPageCount={totalPageCount}
                        />
                    }
                </>
            }
        </main>
    );
}
