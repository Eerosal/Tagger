import axios from "axios";

const query = async (queryStr) => {
    const searchParams = new URLSearchParams(
        {
            query: queryStr
        }
    );

    const response = await fetch(`/api/files?${searchParams}`);
    return response.json();
};

const upload = async (uploadForm) => {
    const formData = new FormData();
    formData.append("filename", uploadForm.filename);

    const response = await fetch("/api/files", {
        method: "POST",
        body: formData
    });

    return response.json();
};

const getById = async (id) => {
    const response = await axios.get(`/api/files/${id}`);
    return response.data;
}

const actions = {
    query,
    upload,
    getById,
};

export default actions;
