import Grid from '@mui/material/Grid2';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useContent } from '../context/ContentContext';
import { GridColDef } from '@mui/x-data-grid';
import CustomizedDataGrid from './CustomizedDataGrid';

const sourcesColumns: GridColDef[] = [
  { field: 'name', headerName: 'Name', width: 200 },
  { field: 'type', headerName: 'Type', width: 150 },
  { field: 'schedule', headerName: 'Schedule', width: 150 },
  { field: 'lastRun', headerName: 'Last Run', width: 180 },
];

const sourcesRows = [
  { id: 1, name: 'Source 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Source 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Source 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const chartsColumns: GridColDef[] = [
  { field: 'name', headerName: 'Name', width: 200 },
  { field: 'type', headerName: 'Type', width: 150 },
  { field: 'schedule', headerName: 'Schedule', width: 150 },
  { field: 'lastRun', headerName: 'Last Run', width: 180 },
];

const chartsRows = [
  { id: 1, name: 'Chart 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Chart 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Chart 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const reportsColumns: GridColDef[] = [
  { field: 'name', headerName: 'Name', width: 200 },
  { field: 'type', headerName: 'Type', width: 150 },
  { field: 'schedule', headerName: 'Schedule', width: 150 },
  { field: 'lastRun', headerName: 'Last Run', width: 180 },
];

const reportsRows = [
  { id: 1, name: 'Report 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Report 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Report 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];



export default function MainGrid() {
  const { currentContent } = useContent();

  const renderContent = () => {
    switch (currentContent) {
      case 'home':
        return (
          <>
            <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
              Overview
            </Typography>
            <Grid
              container
              spacing={2}
              columns={12}
              sx={{ mb: (theme) => theme.spacing(2) }}
            >
              {/* Add your home content here */}
            </Grid>
            <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
              Details
            </Typography>
            <Grid container spacing={2} columns={12}>
              <Grid size={{ xs: 12, lg: 9 }}>
                {/* Add your home details here */}
              </Grid>
              <Grid size={{ xs: 12, lg: 3 }}>
                <Stack gap={2} direction={{ xs: 'column', sm: 'row', lg: 'column' }}>
                  {/* Add your home sidebar content here */}
                </Stack>
              </Grid>
            </Grid>
          </>
        );
      case 'sources':
        return (
          <CustomizedDataGrid
            title="Data Sources"
            rows={sourcesRows}
            columns={sourcesColumns}
          />
        );
      case 'charts':
        return (
          <CustomizedDataGrid
            title="Charts"
            rows={chartsRows}
            columns={chartsColumns}
          />
        );
      case 'reports':
        return (
          <CustomizedDataGrid
            title="Reports"
            rows={reportsRows}
            columns={reportsColumns}
          />
        );
      case 'settings':
        return (
          <Box>
            <Typography variant="h4" component="h1" gutterBottom>
              Settings
            </Typography>
            <Typography variant="body1">
              This is the Settings content.
            </Typography>
          </Box>
        );
      case 'about':
        return (
          <Box>
            <Typography variant="h4" component="h1" gutterBottom>
              About
            </Typography>
            <Typography variant="body1">
              This is the About content.
            </Typography>
          </Box>
        );
      case 'feedback':
        return (
          <Box>
            <Typography variant="h4" component="h1" gutterBottom>
              Feedback
            </Typography>
            <Typography variant="body1">
              This is the Feedback content.
            </Typography>
          </Box>
        );
      default:
        return null;
    }
  };

  return (
    <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>
      {renderContent()}
    </Box>
  );
}
