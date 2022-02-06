import axios from "axios";
import { TaggerFileQueryResponse, TaggerFileResponse } from "../common/types";

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
    filename: string,
    file: File,
}

const upload = async (uploadForm: UploadForm): Promise<TaggerFileResponse> => {
    const formData = new FormData();
    formData.append("filename", uploadForm.filename);
    formData.append("file", uploadForm.file);

    const response = await fetch("/api/files", {
        method: "POST",
        body: formData
    });

    return response.json();
};

const get = async (id: number): Promise<TaggerFileResponse> => {
    const response = await axios.get(`/api/files/${id}`);
    return response.data;
};

const removeTags = async (id: number, tagIds: number[]):
    Promise<TaggerFileResponse> => {
    const response = await axios.delete(`/api/files/${id}/remove-tags`, {
        data: {
            tagIds
        }
    });

    return response.data;
};

const addTags = async (id: number, tagIds: number[]):
    Promise<TaggerFileResponse> => {
    const response = await axios.post(`/api/files/${id}/add-tags`, {
        tagIds
    });

    return response.data;
};


const actions = {
    query,
    upload,
    get,
    removeTags,
    addTags
};

export default actions;
