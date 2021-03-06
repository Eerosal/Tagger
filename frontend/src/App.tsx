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
import { AuthenticationProvider } from "./components/AuthenticationProvider";
import { ErrorHandlingProvider } from "./components/ErrorHandlingProvider";
import Header from "./components/Header";
import Users from "./routes/Users";

function App() {
    return (
        <div className="app">
            <AuthenticationProvider>
                <ErrorHandlingProvider>
                    <Header />
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
                            <Route path="/users" element={<Users />} />
                        </Route>

                        <Route
                            path="*"
                            element={
                                <Navigate to="/404" />
                            }
                        />
                    </Routes>
                </ErrorHandlingProvider>
            </AuthenticationProvider>
        </div>
    );
}

export default App;
