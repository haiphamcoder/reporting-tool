import { alpha, Box, CssBaseline, Stack } from "@mui/material";
import AppTheme from "../theme/AppTheme";
import {
    chartsCustomizations,
    dataGridCustomizations,
    datePickersCustomizations,
    treeViewCustomizations,
} from '../theme/customizations';
import SideMenu from "../components/SizeMenu";
import AppNavbar from "../components/AppNavbar";
import Header from "../components/Header";
import MainGrid from "../components/MainGrid";
import SourceEditPage from "../pages/SourceEditPage";
import SourceViewDataPage from "../components/modules/SourceViewDataPage";
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';

const xThemeComponents = {
    ...chartsCustomizations,
    ...dataGridCustomizations,
    ...datePickersCustomizations,
    ...treeViewCustomizations,
};

export default function Dashboard(props: { disableCustomTheme?: boolean }) {
    const location = useLocation();
    console.log('Dashboard - location.pathname:', location.pathname);
    
    return (
        <AppTheme {...props} themeComponents={xThemeComponents}>
            <CssBaseline enableColorScheme />
            <Box sx={{ display: 'flex' }}>
                <SideMenu />
                <AppNavbar />
                {/* Main content */}
                <Box
                    component="main"
                    sx={(theme) => ({
                        flexGrow: 1,
                        backgroundColor: alpha(theme.palette.background.default, 1),
                        overflow: 'auto',
                    })}
                >
                    <Stack
                        spacing={2}
                        sx={{
                            alignItems: 'center',
                            mx: 3,
                            pb: 5,
                            mt: { xs: 8, md: 0 },
                        }}
                    >
                        <Header />
                        <Routes>
                            <Route path="/" element={<Navigate to="/dashboard/home" replace />} />
                            <Route path="/sources/:source_id/view-data" element={<SourceViewDataPage />} />
                            <Route path="/sources/:sourceId/edit" element={<SourceEditPage />} />
                            <Route path="/:section" element={<MainGrid />} />
                        </Routes>
                    </Stack>
                </Box>
            </Box>
        </AppTheme>
    );
}