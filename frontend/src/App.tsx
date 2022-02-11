import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
import React from "react";
import Home from "./routes/Home";
import Search from "./routes/Search";
import Upload from "./routes/Upload";
import FileView from "./routes/FileView";
import Navbar from "./components/Navbar";
import PrivateOutlet from "./components/PrivateOutlet";
import Login from "./routes/Login";
import { Authentication } from "./components/Authentication";

function App() {
    return (
        <div className="app">
            <Authentication>
                <Navbar />
                <Routes>
                    <Route path="/login" element={
                        <Login />
                    } />

                    <Route path="/404" element={
                        <main>
                            <h2>404 Not found</h2>
                        </main>
                    } />

                    <Route path="/" element={
                        <PrivateOutlet />
                    }>
                        <Route path="/" element={<Home />} />
                        <Route path="/search" element={<Search />} />
                        <Route path="/upload" element={<Upload />} />
                        <Route
                            path="/files/:fileId" element={<FileView />}
                        />
                    </Route>

                    <Route path="*"
                        element={
                            <Navigate to="/404" />
                        }
                    />
                </Routes>
            </Authentication>
        </div>
    );
}

export default App;
