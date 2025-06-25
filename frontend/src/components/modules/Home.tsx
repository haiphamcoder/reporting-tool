import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid2';
import Stack from '@mui/material/Stack';
import Statistic from '../Statistic';
import { Chart } from 'react-chartjs-2';

const chartData = {
  labels: ['CSV', 'Excel', 'Google Sheets', 'API'],
  datasets: [
    {
      type: 'bar' as const,
      label: 'Số lượng Source',
      data: [12, 7, 5, 9],
      backgroundColor: 'rgba(54, 162, 235, 0.5)',
    },
    {
      type: 'line' as const,
      label: 'Số lượng Report',
      data: [8, 10, 3, 6],
      borderColor: 'rgba(255, 99, 132, 1)',
      borderWidth: 2,
      fill: false,
    },
  ],
};

export default function Home() {
  return (
    <>
      <Typography component="h2" variant="h4" sx={{ mb: 2 }}>
        Overview
      </Typography>
      <Statistic />
      <Typography component="h2" variant="h4" sx={{ mb: 2 }}>
        Details
      </Typography>
      <Grid container spacing={2} columns={12}>
        <Grid size={{ xs: 12, lg: 9 }}>
          <Typography variant="h6" sx={{ mb: 1 }}>
            Thống kê nguồn và báo cáo (Fake Data)
          </Typography>
          <Chart type='bar' data={chartData} />
        </Grid>
        <Grid size={{ xs: 12, lg: 3 }}>
          <Stack gap={2} direction={{ xs: 'column', sm: 'row', lg: 'column' }}>
            <Typography variant="body2" color="text.secondary">
              (Placeholder cho các thông tin phụ)
            </Typography>
          </Stack>
        </Grid>
      </Grid>
    </>
  );
} 