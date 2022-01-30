import "./App.css";
import { Routes, Route, Link } from "react-router-dom";
import Home from "./routes/home";
import Search from "./routes/search";

function NavBar() {
    return (
        <div className="navBar">
            {
                [
                    {
                        name: "Home",
                        path: "/"
                    },
                    {
                        name: "Search",
                        path: "/search"
                    }
                ].map((navEntry) => (
                    <Link className="navBarEntry"
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

function App() {
    return (
        <div className="app">
            <NavBar />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/search" element={<Search />} />
                <Route path="*"
                    element={
                        <main>
                            <h2>404 Not found</h2>
                        </main>
                    }
                />
            </Routes>
        </div>
    );
}

export default App;
