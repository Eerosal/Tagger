import "./paginator.css";
import { Link, useSearchParams } from "react-router-dom";

interface PaginatorProps {
    page: number,
    totalPageCount: number,
    path: string,
}


export default function Paginator(props: PaginatorProps) {
    const { page, totalPageCount, path } = props;
    const [urlSearchParams] = useSearchParams();

    if (!page || !totalPageCount) {
        return null;
    }

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
                                    const showRightDots =
                                        page < totalPageCount - 3;

                                    let linkPage: number;
                                    if (i === 0) {
                                        linkPage = 1;
                                    } else if (i === 6
                                        && showRightDots) {
                                        linkPage = totalPageCount;
                                    } else {
                                        linkPage = Math.max(
                                            page - 3, 1
                                        ) + i;
                                    }

                                    if (linkPage === page) {
                                        return (
                                            <span>
                                                <b>{linkPage}</b>
                                            </span>
                                        );
                                    }


                                    if (linkPage > totalPageCount)
                                        return null;

                                    if ((i === 1
                                            && page > 3)
                                        || (i === 5 && showRightDots)) {
                                        return (
                                            <span>...</span>
                                        );
                                    }

                                    const linkSearchParams =
                                        new URLSearchParams(
                                            urlSearchParams.toString()
                                        );

                                    linkSearchParams.set("page",
                                        `${linkPage}`
                                    );

                                    const linkPath = `${path
                                    }?${
                                        linkSearchParams.toString()}`;

                                    return (
                                        <span>
                                            <Link to={linkPath}>
                                                {linkPage}
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
