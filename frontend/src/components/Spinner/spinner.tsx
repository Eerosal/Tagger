import "./spinner.css";
import SpinnerImage from "./spinner.svg";

export default function Spinner(){
    return (
        <img
            src={SpinnerImage}
            className="spinner"
            alt="Loading animation"
        />
    )
}
