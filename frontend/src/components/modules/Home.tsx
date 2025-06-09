import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid2';
import Stack from '@mui/material/Stack';
import Statistic from '../Statistic';

interface HomeProps {
  sourcesData: any[];
  chartsData: any[];
  reportsData: any[];
}

export default function Home({ sourcesData, chartsData, reportsData }: HomeProps) {
  return (
    <>
      <Typography component="h2" variant="h4" sx={{ mb: 2 }}>
        Overview
      </Typography>
      <Statistic
        sourcesData={sourcesData}
        chartsData={chartsData}
        reportsData={reportsData}
      />
      <Typography component="h2" variant="h4" sx={{ mb: 2 }}>
        Details
      </Typography>
      <Grid container spacing={2} columns={12}>
        <Grid size={{ xs: 12, lg: 9 }}>
          <Typography variant="body1">
            Welcome to the home page.
          </Typography>
        </Grid>
        <Grid size={{ xs: 12, lg: 3 }}>
          <Stack gap={2} direction={{ xs: 'column', sm: 'row', lg: 'column' }}>
            <Typography variant="body1">
              Welcome to the home page.
            </Typography>
          </Stack>
        </Grid>
      </Grid>
    </>
  );
} 