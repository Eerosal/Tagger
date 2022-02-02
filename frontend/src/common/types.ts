export interface TaggerFile {
    id?: number,
    name: string
}

export interface TaggerFileQueryResponse {
    totalResultsCount: number,
    results?: TaggerFile[]
}

export interface FileViewState {
    uploadedFile?: TaggerFile
}
