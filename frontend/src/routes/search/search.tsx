import "./search.css";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { ChangeEvent, useEffect, useState } from "react";
import filesService from "../../services/filesService";
import { TaggerFileQueryResponse } from "../../common/types";

interface PaginatorInfo {
    page?: number,
    pageSize?: number,
}

interface PaginatorProps {
    pageInfo: PaginatorInfo,
    response: TaggerFileQueryResponse
}

const DEFAULT_PAGE_SIZE = 24;

function Paginator(props: PaginatorProps) {
    const { pageInfo, response } = props;
    const [links, setLinks] = useState([]);
    const [urlSearchParams] = useSearchParams();

    useEffect(() => {
        const { page, pageSize } = pageInfo;

        if(!page || !pageSize){
            setLinks([]);

            return;
        }

        const pagesStart = Math.max(
            1, page - 3
        );

        const { totalResultsCount } = response;
        const lastPage =
            (Math.floor((totalResultsCount || 0) / pageSize)) + 1;
        const pagesEnd = Math.min(
            page + 3, lastPage
        );

        const newLinks: any = [];
        for (let i = pagesStart; i <= pagesEnd; i += 1) {
            const urlParams =
                new URLSearchParams({
                    query:
                        urlSearchParams.get(
                            "query"
                        ),
                    page: `${i}`
                });

            const path = `/search?${
                urlParams.toString()}`;

            newLinks.push(
                {
                    text: `${i}`,
                    current: i === pageInfo.page,
                    path
                }
            );
        }

        setLinks(newLinks);
    }, [response]);

    return (
        <menu>
            {
                links.map((link) => (
                    <p
                        className="paginatorLink"
                        key={link.text}
                    >
                        {
                            (link.current) ?
                                <span className="paginatorCurrentLink">
                                    <b>{link.text}</b>
                                </span>
                                :
                                <Link
                                    to={link.path}
                                >
                                    {link.text}
                                </Link>
                        }
                    </p>
                ))
            }
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
        const pageUrlParam = urlSearchParams.get("page");
        if(pageUrlParam){
            setPageInfo({
                pageSize: pageInfo.pageSize || DEFAULT_PAGE_SIZE,
                page: parseInt(pageUrlParam, 10) || 1
            } as PaginatorInfo)
        }


    }, [urlSearchParams]);

    useEffect(() => {
        if(!pageInfo.page || !pageInfo.pageSize){
            return;
        }

        const queryUrlParam = urlSearchParams.get("query");
        if(queryUrlParam){

            const pageSize = pageInfo.pageSize || DEFAULT_PAGE_SIZE;
            const page = pageInfo.page || 1;

            const queryStr =
                `${queryUrlParam} page_size:${pageSize} page:${page}`;

            filesService.query(queryStr).then((newResponse) => {
                if(newResponse.results.length === 0
                    && newResponse.totalResultsCount > 0){
                    setUrlSearchParams({
                        query: queryUrlParam,
                        page: "1"
                    })
                } else {
                    setResponse(newResponse);
                }
            }).catch((e) => {
                alert(e);
            })
        }
    }, [pageInfo, urlSearchParams]);

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

                    const url = `/search?${  params.toString()}`;

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
