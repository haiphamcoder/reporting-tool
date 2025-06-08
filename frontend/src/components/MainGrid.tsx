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
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import MenuItem from '@mui/material/MenuItem';

const sourcesRows = [
  { id: 1, name: 'Source 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Source 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Source 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const chartsRows = [
  { id: 1, name: 'Chart 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Chart 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Chart 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const reportsRows = [
  { id: 1, name: 'Report 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Report 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Report 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

export default function MainGrid() {
  const { currentContent } = useContent();
  const [selectedItem, setSelectedItem] = useState<any>(null);
  const [showDetails, setShowDetails] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editForm, setEditForm] = useState<any>(null);
  const [addSourceOpen, setAddSourceOpen] = useState(false);
  const [connectors, setConnectors] = useState<any[]>([]);
  const [loadingConnectors, setLoadingConnectors] = useState(false);

  const handleRowDoubleClick = (params: any) => {
    setSelectedItem(params.row);
    setShowDetails(true);
  };

  const handleBackToList = () => {
    setShowDetails(false);
    setSelectedItem(null);
  };

  const handleEditClick = (row: any) => {
    setEditForm(row);
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setEditForm(null);
  };

  const handleEditSave = () => {
    // TODO: Implement save logic
    console.log('Saving edited data:', editForm);
    handleEditClose();
  };

  const handleEditFormChange = (field: string, value: any) => {
    setEditForm((prev: any) => ({
      ...prev,
      [field]: value
    }));
  };

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
      renderCell: (params) => (
        <Stack
          direction="row"
          spacing={1}
          justifyContent="flex-end"
          alignItems="center"
          sx={{ height: '100%', width: '100%' }}
        >
          <IconButton 
            color="primary" 
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleEditClick(params.row);
            }}
          >
            <EditIcon />
          </IconButton>
          <IconButton color="error" size="small">
            <DeleteIcon />
          </IconButton>
        </Stack>
      ),
    },
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
      renderCell: (params) => (
        <Stack
          direction="row"
          spacing={1}
          justifyContent="flex-end"
          alignItems="center"
          sx={{ height: '100%', width: '100%' }}
        >
          <IconButton 
            color="primary" 
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleEditClick(params.row);
            }}
          >
            <EditIcon />
          </IconButton>
          <IconButton color="error" size="small">
            <DeleteIcon />
          </IconButton>
        </Stack>
      ),
    },
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
      renderCell: (params) => (
        <Stack
          direction="row"
          spacing={1}
          justifyContent="flex-end"
          alignItems="center"
          sx={{ height: '100%', width: '100%' }}
        >
          <IconButton 
            color="primary" 
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleEditClick(params.row);
            }}
          >
            <EditIcon />
          </IconButton>
          <IconButton color="error" size="small">
            <DeleteIcon />
          </IconButton>
        </Stack>
      ),
    },
  ];

  const renderEditDialog = () => {
    if (!editForm) return null;

    return (
      <Dialog open={editDialogOpen} onClose={handleEditClose} maxWidth="sm" fullWidth>
        <DialogTitle>Edit {currentContent?.charAt(0).toUpperCase() + currentContent?.slice(1)}</DialogTitle>
        <DialogContent>
          <Stack spacing={3} sx={{ mt: 2 }}>
            <TextField
              label="Name"
              value={editForm.name}
              onChange={(e) => handleEditFormChange('name', e.target.value)}
              fullWidth
            />
            <TextField
              select
              label="Type"
              value={editForm.type}
              onChange={(e) => handleEditFormChange('type', e.target.value)}
              fullWidth
            >
              <MenuItem value="Type 1">Type 1</MenuItem>
              <MenuItem value="Type 2">Type 2</MenuItem>
              <MenuItem value="Type 3">Type 3</MenuItem>
            </TextField>
            <TextField
              select
              label="Schedule"
              value={editForm.schedule}
              onChange={(e) => handleEditFormChange('schedule', e.target.value)}
              fullWidth
            >
              <MenuItem value="Daily">Daily</MenuItem>
              <MenuItem value="Weekly">Weekly</MenuItem>
              <MenuItem value="Monthly">Monthly</MenuItem>
            </TextField>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose}>Cancel</Button>
          <Button onClick={handleEditSave} variant="contained">Save</Button>
        </DialogActions>
      </Dialog>
    );
  };

  const renderDetails = () => {
    if (!selectedItem) return null;

    return (
      <Stack gap={2}>
        <Stack direction="row" alignItems="center" spacing={2}>
          <IconButton onClick={handleBackToList} size="small">
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" component="h2">
            {selectedItem.name}
          </Typography>
        </Stack>
        <Box sx={{ p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">ID</Typography>
              <Typography variant="body1">{selectedItem.id}</Typography>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">Type</Typography>
              <Typography variant="body1">{selectedItem.type}</Typography>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">Schedule</Typography>
              <Typography variant="body1">{selectedItem.schedule}</Typography>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">Last Run</Typography>
              <Typography variant="body1">{selectedItem.lastRun}</Typography>
            </Grid>
          </Grid>
        </Box>
      </Stack>
    );
  };

  const renderContent = () => {
    if (showDetails) {
      return renderDetails();
    }

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
              onRowDoubleClick={handleRowDoubleClick}
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
              onRowDoubleClick={handleRowDoubleClick}
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
              onRowDoubleClick={handleRowDoubleClick}
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
      {renderEditDialog()}
    </Box>
  );
}

