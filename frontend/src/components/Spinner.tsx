import "./Spinner.css";
import SpinnerImage from "../assets/img/spinner.svg";

export default function Spinner(){
    return (
        <img
            src={SpinnerImage}
            className="spinner"
            alt="Loading animation"
        />
    )
}
