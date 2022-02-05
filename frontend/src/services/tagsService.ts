import axios from "axios";
import { TaggerTag } from "../common/types";

const getOrCreate = async (tagNames: string[]): Promise<TaggerTag[]> => {
    const response = await axios.post("/api/tags/get-or-create", {
        tagNames
    });
    return response.data;
}

const actions = {
    getByNamesOrCreate: getOrCreate
};

export default actions;
