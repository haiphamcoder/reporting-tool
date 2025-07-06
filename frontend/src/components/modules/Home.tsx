import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid2';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Box from '@mui/material/Box';
import Avatar from '@mui/material/Avatar';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import Divider from '@mui/material/Divider';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Statistic from '../Statistic';
import { useState, useEffect } from 'react';
import { SourceSummary } from '../../types/source';
import { ChartSummary } from '../../types/chart';
import { ReportSummary } from '../../types/report';
import { dashboardApi } from '../../api/dashboard/dashboardApi';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend
);

interface DashboardData {
  timeSeriesData: {
    labels: string[];
    sources: number[];
    charts: number[];
    reports: number[];
  };
  topSources: SourceSummary[];
  topCharts: ChartSummary[];
  topReports: ReportSummary[];
}

export default function Home() {
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch only the data needed for Growth Trends and Recent Items
      const [
        timeSeriesData,
        topSources,
        topCharts,
        topReports
      ] = await Promise.all([
        dashboardApi.getTimeSeriesData(),
        dashboardApi.getTopSources(5),
        dashboardApi.getTopCharts(5),
        dashboardApi.getTopReports(5)
      ]);

      setDashboardData({
        timeSeriesData,
        topSources,
        topCharts,
        topReports,
      });

    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '400px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error && !dashboardData) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <>
      <Typography component="h2" variant="h4" sx={{ mb: 2 }}>
        Overview
      </Typography>
      <Statistic />
      
      <Typography component="h2" variant="h4" sx={{ mb: 2, mt: 4 }}>
        Details
      </Typography>
      
      {dashboardData && (
        <Grid container spacing={3} columns={12}>
          {/* Recent Items - Bottom Row */}
          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>
                  Recent Sources
                </Typography>
                <List dense>
                  {dashboardData.topSources.slice(0, 5).map((source, index) => (
                    <Box key={source.id}>
                      <ListItem sx={{ px: 0 }}>
                        <ListItemAvatar>
                          <Avatar sx={{ bgcolor: 'primary.main', width: 32, height: 32 }}>
                            📊
                          </Avatar>
                        </ListItemAvatar>
                        <ListItemText
                          primary={source.name}
                          secondary={`Created ${new Date(source.created_at).toLocaleDateString()}`}
                          primaryTypographyProps={{ variant: 'body2', fontWeight: 500 }}
                          secondaryTypographyProps={{ variant: 'caption' }}
                        />
                      </ListItem>
                      {index < dashboardData.topSources.length - 1 && <Divider />}
                    </Box>
                  ))}
                  {dashboardData.topSources.length === 0 && (
                    <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                      No sources found
                    </Typography>
                  )}
                </List>
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>
                  Recent Charts
                </Typography>
                <List dense>
                  {dashboardData.topCharts.slice(0, 5).map((chart, index) => (
                    <Box key={chart.id}>
                      <ListItem sx={{ px: 0 }}>
                        <ListItemAvatar>
                          <Avatar sx={{ bgcolor: 'secondary.main', width: 32, height: 32 }}>
                            📈
                          </Avatar>
                        </ListItemAvatar>
                        <ListItemText
                          primary={chart.name}
                          secondary={`Type: ${chart.type} • ${new Date(chart.created_at).toLocaleDateString()}`}
                          primaryTypographyProps={{ variant: 'body2', fontWeight: 500 }}
                          secondaryTypographyProps={{ variant: 'caption' }}
                        />
                      </ListItem>
                      {index < dashboardData.topCharts.length - 1 && <Divider />}
                    </Box>
                  ))}
                  {dashboardData.topCharts.length === 0 && (
                    <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                      No charts found
                    </Typography>
                  )}
                </List>
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>
                  Recent Reports
                </Typography>
                <List dense>
                  {dashboardData.topReports.slice(0, 5).map((report, index) => (
                    <Box key={report.id}>
                      <ListItem sx={{ px: 0 }}>
                        <ListItemAvatar>
                          <Avatar sx={{ bgcolor: 'success.main', width: 32, height: 32 }}>
                            📋
                          </Avatar>
                        </ListItemAvatar>
                        <ListItemText
                          primary={report.name}
                          // secondary={`${report.number_of_charts} charts • ${new Date(report.created_at).toLocaleDateString()}`}
                          primaryTypographyProps={{ variant: 'body2', fontWeight: 500 }}
                          secondaryTypographyProps={{ variant: 'caption' }}
                        />
                      </ListItem>
                      {index < dashboardData.topReports.length - 1 && <Divider />}
                    </Box>
                  ))}
                  {dashboardData.topReports.length === 0 && (
                    <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                      No reports found
                    </Typography>
                  )}
                </List>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </>
  );
} 