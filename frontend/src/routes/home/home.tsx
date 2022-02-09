import FileSearch from "../../components/FileSearch";


export default function Home() {
    return (
        <main>
            <h2>Index</h2>
            <FileSearch
                query="order:id_desc"
                page={1}
                paginator={false}
            />
        </main>
    )
}
