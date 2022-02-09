import "./search.css";
import { useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import FileSearch from "../../components/FileSearch";

interface SearchState {
    query: string,
    page: number,
}

export default function Search() {
    const [urlSearchParams] = useSearchParams();
    const [state, setState] = useState<SearchState>();

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
    }, [urlSearchParams])


    return (
        <main>
            <h2>Search</h2>
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
