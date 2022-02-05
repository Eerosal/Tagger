export interface TaggerFile {
    id?: number,
    name: string
}

export interface TaggerTag {
    id: number,
    name: string
}

export interface TaggerFileQueryResponse {
    totalResultsCount?: number,
    results?: TaggerFile[]
}

export interface TaggerFileResponse {
    file: TaggerFile,
    tags: TaggerTag[]
}

export interface FileViewState {
    uploadedFileResponse?: TaggerFileResponse
}
