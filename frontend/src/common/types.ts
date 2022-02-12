export interface TaggerFile {
    id?: number,
    name: string,
    extension?: string
}

export interface TaggerTag {
    id: number,
    name: string
}

export interface TaggerFileQueryResponse {
    totalResultsCount: number,
    results: TaggerFile[]
}

export interface TaggerFileResponse {
    file: TaggerFile,
    tags: TaggerTag[]
}

export interface FileViewState {
    uploadedFileResponse?: TaggerFileResponse
}

export interface Session {
    token: string,
    updatedAt: number
}

export interface TaggerAuthorizationResponse {
    token: string,
    lifetimeSeconds: number
}


export interface TaggerErrorResponse {
    error: string
}
