import "./Paginator.css";
import { Link, useLocation, useSearchParams } from "react-router-dom";

interface PaginatorLinkProps {
    page: number,
    currentPage: number
}

function PaginatorLink(props: PaginatorLinkProps) {
    const { page, currentPage } = props;
    const location = useLocation();
    const [urlSearchParams] = useSearchParams();

    const getLinkToPage = (): string => {
        const params = new URLSearchParams(
            urlSearchParams.toString()
        );

        params.set("page", `${page}`);

        return `${location.pathname}?${params.toString()}`;
    };

    return page === currentPage ?
        <b>{page}</b>
        :
        <Link to={getLinkToPage()}>
            {page}
        </Link>;
}

interface PaginatorProps {
    currentPage: number,
    pageCount: number,
}

export default function Paginator(props: PaginatorProps) {
    const { currentPage, pageCount } = props;

    return (
        <menu className="paginator">
            <ul>
                {
                    pageCount > 0 &&
                    <li className="paginator__page-entry">
                        <PaginatorLink page={1} currentPage={currentPage} />
                    </li>
                }
                {
                    pageCount > 1 &&
                    <li className="paginator__page-entry">
                        {
                            currentPage > 4?
                                <b>...</b>
                                :
                                <PaginatorLink
                                    page={2}
                                    currentPage={currentPage}
                                />
                        }
                    </li>
                }
                {
                    [...new Array(3)]
                        .map((_: any, i: number) => {
                            let rangeStart = Math.max(
                                3, currentPage - 1
                            );

                            if (pageCount > 6) {
                                rangeStart = Math.min(
                                    rangeStart, pageCount - 4
                                );
                            }

                            const page = rangeStart + i;

                            if (page > pageCount) {
                                return null;
                            }

                            return (
                                <li key={page}
                                    className="paginator__page-entry">
                                    <PaginatorLink
                                        page={page}
                                        currentPage={currentPage}
                                    />
                                </li>
                            );
                        })
                }
                {
                    pageCount > 5 &&
                    <li className="paginator__page-entry">
                        {
                            currentPage < pageCount - 3?
                                <b>...</b>
                                :
                                <PaginatorLink
                                    page={pageCount - 1}
                                    currentPage={currentPage}
                                />
                        }
                    </li>
                }
                {
                    pageCount > 6 &&
                    <li className="paginator__page-entry">
                        <PaginatorLink
                            page={pageCount}
                            currentPage={currentPage}
                        />
                    </li>
                }
            </ul>
        </menu>
    );
}
