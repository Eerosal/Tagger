import FileSearchContainer from
    "../../components/FileSearchContainer/fileSearchContainer";

export default function Home() {
    return (
        <main>
            <h2>Index</h2>
            <FileSearchContainer query="order:id_desc" page={1} pageSize={24} />
        </main>
    )
}
