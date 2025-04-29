import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from '../context/AuthContext';
import LoadingPage from "../pages/LoadingPage";

const OAuth2RedirectHandler = () => {
    const navigate = useNavigate();
    const { checkAuth } = useAuth();

    useEffect(() => {
        const handleOAuth2Login = async () => {
            await checkAuth();
            navigate("/dashboard");
        }

        handleOAuth2Login();
    }, [navigate, checkAuth]);

    return <LoadingPage text="Processing OAuth2 login..." />;
};

export default OAuth2RedirectHandler;
