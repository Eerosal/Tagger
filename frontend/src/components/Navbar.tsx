import { Link } from "react-router-dom";

interface NavbarEntry {
    name: string,
    path: string,
}

const navbarEntries: NavbarEntry[] = [
    {
        name: "Home",
        path: "/"
    },
    {
        name: "Search",
        path: "/search"
    },
    {
        name: "Upload",
        path: "/upload"
    }
];

export default function Navbar() {
    return (
        <div className="navBar">
            {
                navbarEntries.map((navEntry) => (
                    <Link
                        className="navBarEntry"
                        to={navEntry.path}
                        key={navEntry.name}
                    >
                        {navEntry.name}
                    </Link>
                ))
            }
        </div>
    );
}
