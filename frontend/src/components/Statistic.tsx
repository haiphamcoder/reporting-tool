import Grid from '@mui/material/Grid2';
import StatCard from './StatCard';

interface StatisticProps {
    sourcesData: any[];
    chartsData: any[];
    reportsData: any[];
}

export default function Statistic({ sourcesData, chartsData, reportsData }: StatisticProps) {
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
                    value={sourcesData.length.toString()}
                    interval="Last 30 days"
                    // trend="up"
                    data={[5, 7, 8, 9, 10, 12, 15]}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                    title="Charts"
                    value={chartsData.length.toString()}
                    interval="Last 30 days"
                    // trend="neutral"
                    data={[3, 4, 5, 6, 7, 8, 9]}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                    title="Reports"
                    value={reportsData.length.toString()}
                    interval="Last 30 days"
                    // trend="down"
                    data={[8, 7, 6, 5, 4, 3, 2]}
                />
            </Grid>
        </Grid>
    );
}