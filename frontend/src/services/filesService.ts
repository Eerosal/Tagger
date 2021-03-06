import axios from "axios";
import { TaggerFileQueryResponse, TaggerFileResponse } from "../common/types";

const query = async (token: string, queryStr: string):
    Promise<TaggerFileQueryResponse> => {
    const searchParams = new URLSearchParams(
        {
            query: queryStr
        }
    );

    const response = await axios.get(`/api/files?${searchParams}`, {
        headers: { Authorization: `Bearer ${token}`}
    })

    return response.data;
};

interface UploadForm {
    filename: string,
    file: File,
}

const upload = async (token: string, uploadForm: UploadForm):
    Promise<TaggerFileResponse> => {
    const formData = new FormData();
    formData.append("filename", uploadForm.filename);
    formData.append("file", uploadForm.file);

    const response = await axios.post(
        "/api/files",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: `Bearer ${token}`
            },
        }
    );

    return response.data;
};

const get = async (token: string, id: number):
    Promise<TaggerFileResponse> => {
    const response = await axios.get(`/api/files/${id}`, {
        headers: { Authorization: `Bearer ${token}`}
    });
    return response.data;
};

const removeTags = async (token: string, id: number, tagIds: number[]):
    Promise<TaggerFileResponse> => {
    const response = await axios.delete(`/api/files/${id}/remove-tags`, {
        data: {
            tagIds
        },
        headers: { Authorization: `Bearer ${token}`}
    });

    return response.data;
};

const addTags = async (token: string, id: number, tagIds: number[]):
    Promise<TaggerFileResponse> => {
    const response = await axios.post(`/api/files/${id}/add-tags`, {
        tagIds
    }, {
        headers: { Authorization: `Bearer ${token}`}
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
