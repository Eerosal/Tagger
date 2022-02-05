import "./search.css";
import { useNavigate, useSearchParams } from "react-router-dom";
import { ChangeEvent, useEffect, useState } from "react";
import FileSearchContainer from
    "../../components/FileSearchContainer/fileSearchContainer";


export default function Search() {
    const navigate = useNavigate();

    const [urlSearchParams] = useSearchParams();

    const getPageFromParams = () => {
        const pageUrlParam = urlSearchParams.get("page");
        if (pageUrlParam) {
            return parseInt(pageUrlParam, 10) || 1;
        }
        return 1;
    }

    const [page, setPage] = useState<number>(getPageFromParams());
    const [query, setQuery] = useState<string>("");

    const [searchInputValue, setSearchInputValue] =
        useState<string>("");

    useEffect(() => {
        const newPage: number = getPageFromParams();
        setPage(
            newPage
        );

        const newQuery = urlSearchParams.get("query") || "";
        setQuery(
            newQuery
        );
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
            <FileSearchContainer
                page={page}
                pageSize={24}
                query={query.length > 0? query : "order:id_desc"}
            />
        </main>
    );
}
