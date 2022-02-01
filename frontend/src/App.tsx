import "./App.css";
import { Routes, Route } from "react-router-dom";
import Home from "./routes/home/home";
import Search from "./routes/search/search";
import Upload from "./routes/upload/upload";
import FileView from "./routes/fileView/fileView";
import Navbar from "./components/Navbar/navbar";

function App() {
    return (
        <div className="app">
            <Navbar />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/search" element={<Search />} />
                <Route path="/upload" element={<Upload />} />
                <Route path="/files/:fileId" element={<FileView />} />
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
