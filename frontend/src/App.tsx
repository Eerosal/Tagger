import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
import { useState } from "react";
import Home from "./routes/Home";
import Search from "./routes/Search";
import Upload from "./routes/Upload";
import FileView from "./routes/FileView";
import Navbar from "./components/Navbar";
import PrivateOutlet from "./components/PrivateOutlet";

function App() {
    const [authenticated, setAuthenticated] = useState<boolean>(true);

    return (
        <div className="app">
            <Navbar />
            <Routes>
                <Route path="/login" element={<h1>lol</h1>} />

                <Route path="/404" element={
                    <main>
                        <h2>404 Not found</h2>
                    </main>
                } />

                <Route path="/" element={
                    <PrivateOutlet authenticated={authenticated} />
                }>
                    <Route path="/" element={<Home />} />
                    <Route path="/search" element={<Search />} />
                    <Route path="/upload" element={<Upload />} />
                    <Route path="/files/:fileId" element={<FileView />} />
                </Route>

                <Route path="*"
                    element={
                        <Navigate to="/404" />
                    }
                />
            </Routes>
        </div>
    );
}

export default App;
