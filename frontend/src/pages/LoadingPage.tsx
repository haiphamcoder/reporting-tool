import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import AppTheme from "../theme/AppTheme";

interface LoadingPageProps {
    text?: string;
}

export default function LoadingPage({text = "Loading..."}: LoadingPageProps) {
    return (
        <AppTheme >
            <Box
                sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    justifyContent: "center",
                    height: "100vh",
                }}
            >
                <CircularProgress size={50} />
                <Typography variant="h6" sx={{ mt: 2 }}>
                    {text}
                </Typography>
            </Box>
        </AppTheme>
    )
};