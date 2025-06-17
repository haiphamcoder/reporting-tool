import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import LoadingPage from "../pages/LoadingPage";

const OAuth2RedirectHandler = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const handleOAuth2Login = async () => {
            navigate("/dashboard");
        }

        handleOAuth2Login();
    }, [navigate]);

    return <LoadingPage text="Processing OAuth2 login..." />;
};

export default OAuth2RedirectHandler;
