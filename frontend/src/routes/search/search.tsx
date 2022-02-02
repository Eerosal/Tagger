import "./search.css";
import { useSearchParams } from "react-router-dom";
import { ChangeEvent, useEffect, useState } from "react";
import filesService from "../../services/filesService";
import { TaggerFileQueryResponse } from "../../common/types";

export default function Search() {
    const [queryResponse, setQueryResponse] = useState<TaggerFileQueryResponse>(
        {
            totalResultsCount: 0
        }
    );
    const [searchParams, setSearchParams] = useState<string>("");
    const [urlSearchParams, setUrlSearchParams] = useSearchParams();

    useEffect(() => {
        const query: string = urlSearchParams.get("query");

        if (query) {
            setSearchParams(query);

            const page: number =
                parseInt(urlSearchParams.get("page"), 10) || 1;

            const fullQuery = `${query} page_size:24 page:${page}`;

            (async () => {
                try {
                    const newQueryResponse =
                        await filesService.query(fullQuery);

                    setQueryResponse(newQueryResponse);
                } catch (e) {
                    alert(e);
                }
            })();
        }
    }, [urlSearchParams]);

    const runSearch = async () => {
        setUrlSearchParams({
            ...urlSearchParams,
            query: searchParams
        });
    };

    return (
        <main>
            <h2>Search</h2>
            <input
                type="text"
                placeholder="search terms"
                onChange={(event: ChangeEvent<HTMLInputElement>) => {
                    setSearchParams(event.target.value);
                }}
                value={searchParams}
            />
            <input
                type="submit" value="Search"
                onClick={runSearch}
            />
            <div className="searchResults">
                {
                    queryResponse.results &&
                    queryResponse.results.map(result => (
                        <div key={result.id} className="searchResult">
                            <h2>{result.id}</h2>
                        </div>
                    ))
                }
            </div>
        </main>
    );
}
