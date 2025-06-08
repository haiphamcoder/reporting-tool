import Grid from '@mui/material/Grid2';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useContent } from '../context/ContentContext';
import { GridColDef } from '@mui/x-data-grid';
import CustomizedDataGrid from './CustomizedDataGrid';
import StatCard from './StatCard';
import Button from '@mui/material/Button';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../config/api';
import AddSourceDialog from './AddSourceDialog';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';

const sourcesColumns: GridColDef[] = [
  { field: 'id', headerName: 'ID', flex: 0.5, minWidth: 70 },
  { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
  { field: 'type', headerName: 'Type', flex: 1, minWidth: 150 },
  { field: 'schedule', headerName: 'Schedule', flex: 1, minWidth: 150 },
  { field: 'lastRun', headerName: 'Last Run', flex: 1, minWidth: 180 },
  {
    field: 'actions',
    headerName: '',
    flex: 0.5,
    minWidth: 120,
    sortable: false,
    renderCell: () => (
      <Stack
        direction="row"
        spacing={1}
        justifyContent="flex-end"
        alignItems="center"
        sx={{ height: '100%', width: '100%' }}
      >
        <IconButton color="primary" size="small">
          <EditIcon />
        </IconButton>
        <IconButton color="error" size="small">
          <DeleteIcon />
        </IconButton>
      </Stack>
    ),
  },
];

const sourcesRows = [
  { id: 1, name: 'Source 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Source 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Source 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const chartsColumns: GridColDef[] = [
  { field: 'id', headerName: 'ID', flex: 0.5, minWidth: 70 },
  { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
  { field: 'type', headerName: 'Type', flex: 1, minWidth: 150 },
  { field: 'schedule', headerName: 'Schedule', flex: 1, minWidth: 150 },
  { field: 'lastRun', headerName: 'Last Run', flex: 1, minWidth: 180 },
  {
    field: 'actions',
    headerName: '',
    flex: 0.5,
    minWidth: 120,
    sortable: false,
    renderCell: () => (
      <Stack
        direction="row"
        spacing={1}
        justifyContent="flex-end"
        alignItems="center"
        sx={{ height: '100%', width: '100%' }}
      >
        <IconButton color="primary" size="small">
          <EditIcon />
        </IconButton>
        <IconButton color="error" size="small">
          <DeleteIcon />
        </IconButton>
      </Stack>
    ),
  },
];

const chartsRows = [
  { id: 1, name: 'Chart 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Chart 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Chart 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const reportsColumns: GridColDef[] = [
  { field: 'id', headerName: 'ID', flex: 0.5, minWidth: 70 },
  { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
  { field: 'type', headerName: 'Type', flex: 1, minWidth: 150 },
  { field: 'schedule', headerName: 'Schedule', flex: 1, minWidth: 150 },
  { field: 'lastRun', headerName: 'Last Run', flex: 1, minWidth: 180 },
  {
    field: 'actions',
    headerName: '',
    flex: 0.5,
    minWidth: 120,
    sortable: false,
    renderCell: () => (
      <Stack
        direction="row"
        spacing={1}
        justifyContent="flex-end"
        alignItems="center"
        sx={{ height: '100%', width: '100%' }}
      >
        <IconButton color="primary" size="small">
          <EditIcon />
        </IconButton>
        <IconButton color="error" size="small">
          <DeleteIcon />
        </IconButton>
      </Stack>
    ),
  },
];

const reportsRows = [
  { id: 1, name: 'Report 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Report 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Report 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

export default function MainGrid() {
  const { currentContent } = useContent();

  // Dialog state and connector logic
  const [addSourceOpen, setAddSourceOpen] = useState(false);
  const [connectors, setConnectors] = useState<any[]>([]);
  const [loadingConnectors, setLoadingConnectors] = useState(false);

  useEffect(() => {
    if (addSourceOpen) {
      setLoadingConnectors(true);
      fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CONNECTOR}`, {
        method: 'GET',
        credentials: 'include',
      })
        .then((res) => res.json())
        .then((data) => {
          console.log(data);
          setConnectors(data.data || []);
          setLoadingConnectors(false);
        })
        .catch(() => setLoadingConnectors(false));
    }
  }, [addSourceOpen]);

  const handleOpenAddSource = () => {
    setAddSourceOpen(true);
  };
  const handleCloseAddSource = () => {
    setAddSourceOpen(false);
  };
  const handleAddSourceNext = (data: { sourceName: string; connector: any; file?: File | null }) => {
    console.log(data.connector);
    setAddSourceOpen(false);
  };

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
              <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                  title="Data Sources"
                  value={sourcesRows.length.toString()}
                  interval=""
                  trend="up"
                  data={[5, 7, 8, 9, 10, 12, 15]}
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                  title="Charts"
                  value={chartsRows.length.toString()}
                  interval=""
                  trend="neutral"
                  data={[3, 4, 5, 6, 7, 8, 9]}
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                <StatCard
                  title="Reports"
                  value={reportsRows.length.toString()}
                  interval=""
                  trend="down"
                  data={[8, 7, 6, 5, 4, 3, 2]}
                />
              </Grid>
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
          <Stack gap={2}>
            <Typography variant="h6" component="h2" gutterBottom>
              Sources
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center">
              <IconButton color="primary" size="small">
                <RefreshIcon />
              </IconButton>
              <Button variant="contained" color="primary" onClick={handleOpenAddSource}>
                Add Source
              </Button>
            </Stack>
            <CustomizedDataGrid
              rows={sourcesRows}
              columns={sourcesColumns}
              sx={{ '& .MuiDataGrid-cell:focus': { outline: 'none' } }}
              autoHeight
              disableColumnMenu
              disableRowSelectionOnClick
              columnBufferPx={2}
            />
            <AddSourceDialog
              open={addSourceOpen}
              onClose={handleCloseAddSource}
              onNext={handleAddSourceNext}
              connectors={connectors}
              loadingConnectors={loadingConnectors}
              disableCustomTheme={false}
            />
          </Stack>
        );
      case 'charts':
        return (
          <Stack gap={2}>
            <Typography variant="h6" component="h2" gutterBottom>
              Charts
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center">
              <IconButton color="primary" size="small">
                <RefreshIcon />
              </IconButton>
              <Button variant="contained" color="primary">
                Add Chart
              </Button>
            </Stack>
            <CustomizedDataGrid
              rows={chartsRows}
              columns={chartsColumns}
              sx={{ '& .MuiDataGrid-cell:focus': { outline: 'none' } }}
              autoHeight
              disableColumnMenu
              disableRowSelectionOnClick
              columnBufferPx={2}
            />
          </Stack>
        );
      case 'reports':
        return (
          <Stack gap={2}>
            <Typography variant="h6" component="h2" gutterBottom>
              Reports
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center">
              <IconButton color="primary" size="small">
                <RefreshIcon />
              </IconButton>
              <Button variant="contained" color="primary">
                Add Report
              </Button>
            </Stack>
            <CustomizedDataGrid
              rows={reportsRows}
              columns={reportsColumns}
              sx={{ '& .MuiDataGrid-cell:focus': { outline: 'none' } }}
              autoHeight
              disableColumnMenu
              disableRowSelectionOnClick
              columnBufferPx={2}
            />
          </Stack>
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
