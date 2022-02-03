import "./search.css";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { ChangeEvent, useEffect, useState } from "react";
import filesService from "../../services/filesService";
import { TaggerFileQueryResponse } from "../../common/types";

interface PaginatorInfo {
    page?: number,
    pageSize?: number,
    query?: string,
}

interface PaginatorProps {
    pageInfo: PaginatorInfo,
    response: TaggerFileQueryResponse
}

const DEFAULT_PAGE_SIZE = 24;

function Paginator(props: PaginatorProps) {
    const { pageInfo, response } = props;
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
                                    const lastPage = Math.floor(
                                        response.totalResultsCount
                                        / pageInfo.pageSize + 1);

                                    const showRightDots =
                                        pageInfo.page < lastPage - 3;

                                    let number;
                                    if (i === 0) {
                                        number = 1;
                                    } else if (i === 6
                                        && showRightDots) {
                                        number = lastPage;
                                    } else {
                                        number = Math.max(
                                            pageInfo.page - 3, 1
                                        ) + i;
                                    }

                                    if (number === pageInfo.page) {
                                        return (
                                            <span>
                                                <b>{number}</b>
                                            </span>
                                        );
                                    }


                                    if (number > lastPage) return null;

                                    if ((i === 1
                                            && pageInfo.page > 3)
                                        || (i === 5 && showRightDots)) {
                                        return (
                                            <span>...</span>
                                        );
                                    }

                                    const urlParams =
                                        new URLSearchParams({
                                            query: pageInfo.query,
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

    const [response, setResponse] = useState<TaggerFileQueryResponse>();
    const [pageInfo, setPageInfo] = useState<PaginatorInfo>({});
    const [searchInputValue, setSearchInputValue] =
        useState<string>("");

    useEffect(() => {
        let page;

        const pageUrlParam = urlSearchParams.get("page");
        if (pageUrlParam) {
            page = parseInt(pageUrlParam, 10) || 1;
        } else {
            page = 1;
        }

        const query = urlSearchParams.get("query");

        setPageInfo({
            pageSize: pageInfo.pageSize || DEFAULT_PAGE_SIZE,
            page,
            query
        } as PaginatorInfo);
    }, [urlSearchParams]);

    useEffect(() => {
        const pageSize = pageInfo.pageSize || DEFAULT_PAGE_SIZE;
        const page = pageInfo.page || 1;

        const queryStr =
            `${pageInfo.query} page_size:${pageSize} page:${page}`;

        filesService.query(queryStr).then((newResponse) => {
            if (newResponse.results.length === 0
                && newResponse.totalResultsCount > 0) {
                setUrlSearchParams({
                    query: pageInfo.query,
                    page: "1"
                });
            } else {
                setResponse(newResponse);
            }
        }).catch((e) => {
            alert(e);
        });
    }, [pageInfo]);

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
                                    <h2>{result.id}</h2>
                                </div>
                            ))
                        }
                    </div>
                    {
                        response.totalResultsCount &&
                        <Paginator
                            pageInfo={pageInfo}
                            response={response}
                        />
                    }
                </>
            }
        </main>
    );
}
