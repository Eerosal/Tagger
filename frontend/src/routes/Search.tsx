import "./Search.css";
import { useNavigate, useSearchParams } from "react-router-dom";
import { ChangeEvent, useEffect, useState } from "react";
import FileSearch from "../components/FileSearch";

interface SearchState {
    query: string,
    page: number,
}

export default function Search() {
    const [urlSearchParams] = useSearchParams();
    const [state, setState] = useState<SearchState>();
    const [queryInputValue, setQueryInputValue] = useState<string>();
    const navigate = useNavigate();

    useEffect(() => {
        const queryParam = urlSearchParams.get("query");

        let query;
        if(queryParam && queryParam.length > 0){
            query = queryParam;
        } else {
            query = "order:id_desc";
        }

        const page: number =
            parseInt(urlSearchParams.get("page"), 10) || 1;

        setState({
            query,
            page,
        })
    }, [urlSearchParams]);

    return (
        <main>
            <h2>Search</h2>
            <input
                type="text"
                placeholder="search terms"
                onChange={(event: ChangeEvent<HTMLInputElement>) => {
                    setQueryInputValue(event.target.value);
                }}
                value={queryInputValue}
            />
            <input
                type="submit" value="Search"
                onClick={() => {
                    const params = new URLSearchParams({
                        query: queryInputValue
                    });

                    const url = `/search?${params.toString()}`;

                    navigate(url);
                }}
            />
            {
                state &&
                <FileSearch
                    query={state.query}
                    page={state.page}
                />
            }
        </main>
    );
}
