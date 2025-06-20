import Grid from '@mui/material/Grid2';
import StatCard from './StatCard';
import { useStatistics } from '../context/StatisticsContext';
import { CircularProgress, Box } from '@mui/material';

export default function Statistic() {
    const { statistics, loading } = useStatistics();

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!statistics) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                <div>No statistics available</div>
            </Box>
        );
    }

    return (
        <Grid
            container
            spacing={2}
            columns={12}
            sx={{ mb: (theme) => theme.spacing(2) }}
        >
            <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                    title="Data Sources"
                    value={statistics.sources.count.toString()}
                    interval="Last 30 days"
                    data={statistics.sources.data}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                    title="Charts"
                    value={statistics.charts.count.toString()}
                    interval="Last 30 days"
                    data={statistics.charts.data}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                    title="Reports"
                    value={statistics.reports.count.toString()}
                    interval="Last 30 days"
                    data={statistics.reports.data}
                />
            </Grid>
        </Grid>
    );
}