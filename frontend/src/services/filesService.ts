import axios from "axios";
import { TaggerFileQueryResponse } from "../common/types";

const query = async (queryStr: string): Promise<TaggerFileQueryResponse> => {
    const searchParams = new URLSearchParams(
        {
            query: queryStr
        }
    );

    const response = await fetch(`/api/files?${searchParams}`);
    const json = await response.json();

    return json as TaggerFileQueryResponse;
};

interface UploadForm {
    filename: string
}

const upload = async (uploadForm: UploadForm) => {
    const formData = new FormData();
    formData.append("filename", uploadForm.filename);

    const response = await fetch("/api/files", {
        method: "POST",
        body: formData
    });

    return response.json();
};

const getById = async (id: number) => {
    const response = await axios.get(`/api/files/${id}`);
    return response.data;
}

const actions = {
    query,
    upload,
    getById,
};

export default actions;
