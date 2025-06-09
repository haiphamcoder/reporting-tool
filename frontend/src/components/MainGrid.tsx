import Grid from '@mui/material/Grid2';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useContent } from '../context/ContentContext';
import { GridColDef } from '@mui/x-data-grid';
import CustomizedDataGrid from './CustomizedDataGrid';
import Button from '@mui/material/Button';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../config/api';
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
import AddIcon from '@mui/icons-material/Add';
import TableContainer from '@mui/material/TableContainer';
import Table from '@mui/material/Table';
import TableHead from '@mui/material/TableHead';
import TableBody from '@mui/material/TableBody';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import Statistic from './Statistic';

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
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState<any>(null);
  const [addSourceOpen, setAddSourceOpen] = useState(false);
  const [connectors, setConnectors] = useState<any[]>([]);
  const [loadingConnectors, setLoadingConnectors] = useState(false);
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  const [addForm, setAddForm] = useState<any>({
    name: '',
    type: '',
    schedule: '',
    chartType: '',
    dataSource: '',
    description: '',
    connectorType: '',
    connectionConfig: {},
    schemaMapping: {},
    previewData: null,
    selectedFile: null,
    mode: 'normal',
    displayType: 'chart',
    selectedChartType: '',
    sources: [],
    query: {
      filters: [],
      groupBy: [],
      sortBy: [],
      joins: [],
    },
    sqlQuery: '',
    recipients: [],
    format: 'pdf',
    charts: [],
    emailSubject: '',
    emailBody: '',
    advancedSettings: {
      includeDataTable: true,
      includeChart: true,
      pageSize: 'A4',
      orientation: 'portrait',
      header: '',
      footer: '',
    }
  });
  const [addStep, setAddStep] = useState(1);

  // Add state for table data
  const [sourcesData, setSourcesData] = useState(sourcesRows);
  const [chartsData, setChartsData] = useState(chartsRows);
  const [reportsData, setReportsData] = useState(reportsRows);

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

  const getColumns = (): GridColDef[] => {
    const commonColumns: GridColDef[] = [
      {
        field: 'actions',
        headerName: 'Actions',
        width: 120,
        sortable: false,
        renderCell: (params) => (
          <Box>
            <IconButton
              size="small"
              onClick={(e) => {
                e.stopPropagation();
                handleEditClick(params.row);
              }}
            >
              <EditIcon />
            </IconButton>
            <IconButton
              size="small"
              onClick={(e) => {
                e.stopPropagation();
                handleDeleteClick(params.row);
              }}
            >
              <DeleteIcon />
            </IconButton>
          </Box>
        ),
      },
    ];

    switch (currentContent) {
      case 'sources':
        return [
          { field: 'name', headerName: 'Name', width: 200 },
          { field: 'type', headerName: 'Type', width: 150 },
          { field: 'schedule', headerName: 'Schedule', width: 150 },
          { field: 'lastRun', headerName: 'Last Run', width: 200 },
          { field: 'status', headerName: 'Status', width: 120 },
          ...commonColumns,
        ];
      case 'charts':
        return [
          { field: 'name', headerName: 'Name', width: 200 },
          { field: 'type', headerName: 'Type', width: 150 },
          { field: 'dataSource', headerName: 'Data Source', width: 200 },
          { field: 'lastUpdated', headerName: 'Last Updated', width: 200 },
          ...commonColumns,
        ];
      case 'reports':
        return [
          { field: 'name', headerName: 'Name', width: 200 },
          { field: 'type', headerName: 'Type', width: 150 },
          { field: 'schedule', headerName: 'Schedule', width: 150 },
          { field: 'lastRun', headerName: 'Last Run', width: 200 },
          ...commonColumns,
        ];
      default:
        return [];
    }
  };

  const getRows = () => {
    switch (currentContent) {
      case 'sources':
        return sourcesData;
      case 'charts':
        return chartsData;
      case 'reports':
        return reportsData;
      default:
        return [];
    }
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

  const handleDeleteClick = (row: any) => {
    setItemToDelete(row);
    setDeleteDialogOpen(true);
  };

  const handleDeleteClose = () => {
    setDeleteDialogOpen(false);
    setItemToDelete(null);
  };

  const handleDeleteConfirm = () => {
    // Remove item from the appropriate table based on currentContent
    switch (currentContent) {
      case 'sources':
        setSourcesData(prev => prev.filter(item => item.id !== itemToDelete.id));
        break;
      case 'charts':
        setChartsData(prev => prev.filter(item => item.id !== itemToDelete.id));
        break;
      case 'reports':
        setReportsData(prev => prev.filter(item => item.id !== itemToDelete.id));
        break;
    }
    handleDeleteClose();
  };

  const handleAddClick = () => {
    setAddDialogOpen(true);
    setAddStep(1);
    setAddForm({
      name: '',
      type: '',
      schedule: '',
      chartType: '',
      dataSource: '',
      description: '',
      connectorType: '',
      connectionConfig: {},
      schemaMapping: {},
      previewData: null,
      selectedFile: null,
      mode: 'normal',
      displayType: 'chart',
      selectedChartType: '',
      sources: [],
      query: {
        filters: [],
        groupBy: [],
        sortBy: [],
        joins: [],
      },
      sqlQuery: '',
      recipients: [],
      format: 'pdf',
      charts: [],
      emailSubject: '',
      emailBody: '',
      advancedSettings: {
        includeDataTable: true,
        includeChart: true,
        pageSize: 'A4',
        orientation: 'portrait',
        header: '',
        footer: '',
      }
    });
  };

  const handleAddNext = () => {
    if (currentContent === 'sources') {
      if (addStep === 1) {
        setAddStep(2);
      } else if (addStep === 2) {
        setAddStep(3);
      } else if (addStep === 3) {
        // Handle source creation
        console.log('Creating source:', addForm);
        handleAddClose();
      }
    } else if (currentContent === 'charts') {
      if (addStep === 1) {
        setAddStep(2);
      } else if (addStep === 2) {
        setAddStep(3);
      } else if (addStep === 3) {
        // Handle chart creation
        console.log('Creating chart:', addForm);
        handleAddClose();
      }
    } else if (currentContent === 'reports') {
      if (addStep === 1) {
        setAddStep(2);
      } else if (addStep === 2) {
        setAddStep(3);
      } else if (addStep === 3) {
        // Handle report creation
        console.log('Creating report:', addForm);
        handleAddClose();
      }
    }
  };

  const handleAddBack = () => {
    setAddStep(addStep - 1);
  };

  const handleAddClose = () => {
    setAddDialogOpen(false);
    setAddStep(1);
    setAddForm({
      name: '',
      type: '',
      schedule: '',
      chartType: '',
      dataSource: '',
      description: '',
      connectorType: '',
      connectionConfig: {},
      schemaMapping: {},
      previewData: null,
      selectedFile: null,
      mode: 'normal',
      displayType: 'chart',
      selectedChartType: '',
      sources: [],
      query: {
        filters: [],
        groupBy: [],
        sortBy: [],
        joins: [],
      },
      sqlQuery: '',
      recipients: [],
      format: 'pdf',
      charts: [],
      emailSubject: '',
      emailBody: '',
      advancedSettings: {
        includeDataTable: true,
        includeChart: true,
        pageSize: 'A4',
        orientation: 'portrait',
        header: '',
        footer: '',
      }
    });
  };

  const handleAddFormChange = (field: string, value: any) => {
    setAddForm((prev: any) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setAddForm((prev: any) => ({
        ...prev,
        selectedFile: file,
        connectionConfig: {
          ...prev.connectionConfig,
          fileName: file.name,
        }
      }));
    }
  };

  const handleRemoveFile = () => {
    setAddForm((prev: any) => ({
      ...prev,
      selectedFile: null,
      connectionConfig: {
        ...prev.connectionConfig,
        fileName: null,
      }
    }));
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
          <IconButton
            color="error"
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleDeleteClick(params.row);
            }}
          >
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
          <IconButton
            color="error"
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleDeleteClick(params.row);
            }}
          >
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
          <IconButton
            color="error"
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleDeleteClick(params.row);
            }}
          >
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

  const renderDeleteDialog = () => {
    if (!itemToDelete) return null;

    return (
      <Dialog open={deleteDialogOpen} onClose={handleDeleteClose}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete {itemToDelete.name}?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteClose}>Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    );
  };

  const renderAddDialog = () => {
    const getStepContent = () => {
      if (currentContent === 'sources') {
        switch (addStep) {
          case 1:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Source Name
                  </Typography>
                  <TextField
                    value={addForm.name}
                    onChange={(e) => handleAddFormChange('name', e.target.value)}
                    fullWidth
                    required
                    placeholder="Enter source name"
                  />
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Connector Type
                  </Typography>
                  <TextField
                    select
                    value={addForm.connectorType}
                    onChange={(e) => handleAddFormChange('connectorType', e.target.value)}
                    fullWidth
                    required
                  >
                    <MenuItem value="csv">CSV File</MenuItem>
                    <MenuItem value="excel">Excel File</MenuItem>
                    <MenuItem value="mysql">MySQL Database</MenuItem>
                    <MenuItem value="gsheet">Google Sheet</MenuItem>
                  </TextField>
                </Box>
              </Stack>
            );
          case 2:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                {addForm.connectorType === 'csv' || addForm.connectorType === 'excel' ? (
                  <Box>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Upload File
                    </Typography>
                    {!addForm.selectedFile ? (
                      <Button
                        variant="outlined"
                        component="label"
                        fullWidth
                      >
                        Choose File
                        <input
                          type="file"
                          hidden
                          accept={addForm.connectorType === 'csv' ? '.csv' : '.xlsx,.xls'}
                          onChange={handleFileChange}
                        />
                      </Button>
                    ) : (
                      <Box sx={{
                        p: 2,
                        border: '1px solid',
                        borderColor: 'divider',
                        borderRadius: 1,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between'
                      }}>
                        <Stack direction="row" spacing={1} alignItems="center">
                          <Typography variant="body2" noWrap sx={{ maxWidth: 300 }}>
                            {addForm.selectedFile.name}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            ({(addForm.selectedFile.size / 1024).toFixed(2)} KB)
                          </Typography>
                        </Stack>
                        <Stack direction="row" spacing={1}>
                          <Button
                            size="small"
                            onClick={handleRemoveFile}
                            color="error"
                          >
                            Remove
                          </Button>
                          <Button
                            size="small"
                            component="label"
                            variant="outlined"
                          >
                            Change
                            <input
                              type="file"
                              hidden
                              accept={addForm.connectorType === 'csv' ? '.csv' : '.xlsx,.xls'}
                              onChange={handleFileChange}
                            />
                          </Button>
                        </Stack>
                      </Box>
                    )}
                  </Box>
                ) : addForm.connectorType === 'mysql' ? (
                  <>
                    <Box>
                      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                        Host
                      </Typography>
                      <TextField
                        value={addForm.connectionConfig.host || ''}
                        onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, host: e.target.value })}
                        fullWidth
                        required
                        placeholder="Enter host"
                      />
                    </Box>
                    <Box>
                      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                        Port
                      </Typography>
                      <TextField
                        value={addForm.connectionConfig.port || ''}
                        onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, port: e.target.value })}
                        fullWidth
                        required
                        placeholder="Enter port"
                      />
                    </Box>
                    <Box>
                      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                        Database
                      </Typography>
                      <TextField
                        value={addForm.connectionConfig.database || ''}
                        onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, database: e.target.value })}
                        fullWidth
                        required
                        placeholder="Enter database name"
                      />
                    </Box>
                    <Box>
                      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                        Username
                      </Typography>
                      <TextField
                        value={addForm.connectionConfig.username || ''}
                        onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, username: e.target.value })}
                        fullWidth
                        required
                        placeholder="Enter username"
                      />
                    </Box>
                    <Box>
                      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                        Password
                      </Typography>
                      <TextField
                        type="password"
                        value={addForm.connectionConfig.password || ''}
                        onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, password: e.target.value })}
                        fullWidth
                        required
                        placeholder="Enter password"
                      />
                    </Box>
                  </>
                ) : addForm.connectorType === 'gsheet' ? (
                  <Box>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Google Sheet URL
                    </Typography>
                    <TextField
                      value={addForm.connectionConfig.url || ''}
                      onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, url: e.target.value })}
                      fullWidth
                      required
                      placeholder="Enter Google Sheet URL"
                    />
                  </Box>
                ) : null}
              </Stack>
            );
          case 3:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Schema Mapping
                  </Typography>
                  {addForm.previewData && (
                    <Box sx={{ mt: 2 }}>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Preview Data
                      </Typography>
                      <Box sx={{ maxHeight: 200, overflow: 'auto' }}>
                        {/* TODO: Add data preview table */}
                      </Box>
                    </Box>
                  )}
                </Box>
              </Stack>
            );
          default:
            return null;
        }
      } else if (currentContent === 'charts') {
        switch (addStep) {
          case 1:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Chart Name
                  </Typography>
                  <TextField
                    value={addForm.name}
                    onChange={(e) => handleAddFormChange('name', e.target.value)}
                    fullWidth
                    required
                    placeholder="Enter chart name"
                  />
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Display Type
                  </Typography>
                  <TextField
                    select
                    value={addForm.displayType}
                    onChange={(e) => handleAddFormChange('displayType', e.target.value)}
                    fullWidth
                    required
                  >
                    <MenuItem value="chart">Chart</MenuItem>
                    <MenuItem value="table">Table</MenuItem>
                  </TextField>
                </Box>
                {addForm.displayType === 'chart' && (
                  <Box>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Chart Type
                    </Typography>
                    <TextField
                      select
                      value={addForm.selectedChartType}
                      onChange={(e) => handleAddFormChange('selectedChartType', e.target.value)}
                      fullWidth
                      required
                    >
                      <MenuItem value="line">Line Chart</MenuItem>
                      <MenuItem value="bar">Bar Chart</MenuItem>
                      <MenuItem value="pie">Pie Chart</MenuItem>
                      <MenuItem value="area">Area Chart</MenuItem>
                    </TextField>
                  </Box>
                )}
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Creation Mode
                  </Typography>
                  <TextField
                    select
                    value={addForm.mode}
                    onChange={(e) => handleAddFormChange('mode', e.target.value)}
                    fullWidth
                    required
                  >
                    <MenuItem value="normal">Normal (Visual Builder)</MenuItem>
                    <MenuItem value="advanced">Advanced (SQL Query)</MenuItem>
                  </TextField>
                </Box>
              </Stack>
            );
          case 2:
            return addForm.mode === 'normal' ? (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Data Sources
                  </Typography>
                  <TextField
                    select
                    SelectProps={{
                      multiple: true,
                      value: addForm.sources,
                      onChange: (e) => handleAddFormChange('sources', e.target.value),
                    }}
                    fullWidth
                    required
                  >
                    {sourcesData.map((source) => (
                      <MenuItem key={source.id} value={source.id}>
                        {source.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Filters
                  </Typography>
                  <Stack spacing={2}>
                    {addForm.query.filters.map((filter: any, index: number) => (
                      <Box key={index} sx={{ display: 'flex', gap: 1 }}>
                        <TextField
                          select
                          value={filter.field}
                          onChange={(e) => {
                            const newFilters = [...addForm.query.filters];
                            newFilters[index].field = e.target.value;
                            handleAddFormChange('query', { ...addForm.query, filters: newFilters });
                          }}
                          sx={{ flex: 1 }}
                        >
                          {/* TODO: Add field options based on selected sources */}
                        </TextField>
                        <TextField
                          select
                          value={filter.operator}
                          onChange={(e) => {
                            const newFilters = [...addForm.query.filters];
                            newFilters[index].operator = e.target.value;
                            handleAddFormChange('query', { ...addForm.query, filters: newFilters });
                          }}
                          sx={{ width: 150 }}
                        >
                          <MenuItem value="=">=</MenuItem>
                          <MenuItem value="!=">!=</MenuItem>
                          <MenuItem value="gt">&gt;</MenuItem>
                          <MenuItem value="lt">&lt;</MenuItem>
                          <MenuItem value="gte">&gt;=</MenuItem>
                          <MenuItem value="lte">&lt;=</MenuItem>
                          <MenuItem value="like">LIKE</MenuItem>
                        </TextField>
                        <TextField
                          value={filter.value}
                          onChange={(e) => {
                            const newFilters = [...addForm.query.filters];
                            newFilters[index].value = e.target.value;
                            handleAddFormChange('query', { ...addForm.query, filters: newFilters });
                          }}
                          sx={{ flex: 1 }}
                        />
                        <IconButton
                          size="small"
                          onClick={() => {
                            const newFilters = addForm.query.filters.filter((_: any, i: number) => i !== index);
                            handleAddFormChange('query', { ...addForm.query, filters: newFilters });
                          }}
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    ))}
                    <Button
                      startIcon={<AddIcon />}
                      onClick={() => {
                        const newFilters = [...addForm.query.filters, { field: '', operator: '=', value: '' }];
                        handleAddFormChange('query', { ...addForm.query, filters: newFilters });
                      }}
                    >
                      Add Filter
                    </Button>
                  </Stack>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Group By
                  </Typography>
                  <TextField
                    select
                    SelectProps={{
                      multiple: true,
                      value: addForm.query.groupBy,
                      onChange: (event) => {
                        const value = event.target.value as string[];
                        handleAddFormChange('query', { ...addForm.query, groupBy: value });
                      },
                    }}
                    fullWidth
                  >
                    {/* TODO: Add field options based on selected sources */}
                  </TextField>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Sort By
                  </Typography>
                  <Stack spacing={2}>
                    {addForm.query.sortBy.map((sort: any, index: number) => (
                      <Box key={index} sx={{ display: 'flex', gap: 1 }}>
                        <TextField
                          select
                          value={sort.field}
                          onChange={(e) => {
                            const newSortBy = [...addForm.query.sortBy];
                            newSortBy[index].field = e.target.value;
                            handleAddFormChange('query', { ...addForm.query, sortBy: newSortBy });
                          }}
                          sx={{ flex: 1 }}
                        >
                          {/* TODO: Add field options based on selected sources */}
                        </TextField>
                        <TextField
                          select
                          value={sort.direction}
                          onChange={(e) => {
                            const newSortBy = [...addForm.query.sortBy];
                            newSortBy[index].direction = e.target.value;
                            handleAddFormChange('query', { ...addForm.query, sortBy: newSortBy });
                          }}
                          sx={{ width: 150 }}
                        >
                          <MenuItem value="asc">Ascending</MenuItem>
                          <MenuItem value="desc">Descending</MenuItem>
                        </TextField>
                        <IconButton
                          size="small"
                          onClick={() => {
                            const newSortBy = addForm.query.sortBy.filter((_: any, i: number) => i !== index);
                            handleAddFormChange('query', { ...addForm.query, sortBy: newSortBy });
                          }}
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    ))}
                    <Button
                      startIcon={<AddIcon />}
                      onClick={() => {
                        const newSortBy = [...addForm.query.sortBy, { field: '', direction: 'asc' }];
                        handleAddFormChange('query', { ...addForm.query, sortBy: newSortBy });
                      }}
                    >
                      Add Sort
                    </Button>
                  </Stack>
                </Box>
              </Stack>
            ) : (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    SQL Query
                  </Typography>
                  <Box sx={{
                    border: '1px solid',
                    borderColor: 'divider',
                    borderRadius: 1,
                    overflow: 'hidden'
                  }}>
                    <Box sx={{
                      p: 1,
                      bgcolor: 'background.paper',
                      borderBottom: '1px solid',
                      borderColor: 'divider'
                    }}>
                      <Typography variant="caption" color="text.secondary">
                        Write your SQL query to fetch data for the chart
                      </Typography>
                    </Box>
                    <TextField
                      multiline
                      rows={8}
                      value={addForm.sqlQuery}
                      onChange={(e) => handleAddFormChange('sqlQuery', e.target.value)}
                      fullWidth
                      required
                      placeholder="SELECT * FROM your_table WHERE condition"
                      variant="standard"
                      InputProps={{
                        disableUnderline: true,
                        sx: {
                          p: 2,
                          fontFamily: 'monospace',
                          fontSize: '0.875rem',
                          lineHeight: 1.5,
                          '& textarea': {
                            fontFamily: 'monospace',
                          }
                        }
                      }}
                    />
                  </Box>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Preview Data
                  </Typography>
                  <Box sx={{
                    p: 2,
                    bgcolor: 'background.paper',
                    borderRadius: 1,
                    minHeight: 200,
                    maxHeight: 400,
                    overflow: 'auto',
                    border: '1px solid',
                    borderColor: 'divider'
                  }}>
                    {addForm.previewData ? (
                      <TableContainer>
                        <Table size="small">
                          <TableHead>
                            <TableRow>
                              {Object.keys(addForm.previewData[0] || {}).map((key) => (
                                <TableCell key={key}>{key}</TableCell>
                              ))}
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            {addForm.previewData.map((row: any, index: number) => (
                              <TableRow key={index}>
                                {Object.values(row).map((value: any, i: number) => (
                                  <TableCell key={i}>{value}</TableCell>
                                ))}
                              </TableRow>
                            ))}
                          </TableBody>
                        </Table>
                      </TableContainer>
                    ) : (
                      <Box sx={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        height: '100%',
                        color: 'text.secondary'
                      }}>
                        <Typography>No preview data available</Typography>
                      </Box>
                    )}
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                  <Button
                    variant="outlined"
                    onClick={() => {
                      // TODO: Implement preview functionality
                      console.log('Preview SQL query:', addForm.sqlQuery);
                    }}
                  >
                    Preview Query
                  </Button>
                </Box>
              </Stack>
            );
          case 3:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Preview
                  </Typography>
                  <Box sx={{
                    p: 2,
                    bgcolor: 'background.paper',
                    borderRadius: 1,
                    minHeight: 300,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    {addForm.displayType === 'table' ? (
                      <Typography>Table Preview</Typography>
                    ) : (
                      <Typography>Chart Preview</Typography>
                    )}
                  </Box>
                </Box>
              </Stack>
            );
          default:
            return null;
        }
      } else if (currentContent === 'reports') {
        switch (addStep) {
          case 1:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Report Name
                  </Typography>
                  <TextField
                    value={addForm.name}
                    onChange={(e) => handleAddFormChange('name', e.target.value)}
                    fullWidth
                    required
                    placeholder="Enter report name"
                  />
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Report Type
                  </Typography>
                  <TextField
                    select
                    value={addForm.type}
                    onChange={(e) => handleAddFormChange('type', e.target.value)}
                    fullWidth
                    required
                  >
                    <MenuItem value="daily">Daily Report</MenuItem>
                    <MenuItem value="weekly">Weekly Report</MenuItem>
                    <MenuItem value="monthly">Monthly Report</MenuItem>
                    <MenuItem value="custom">Custom Report</MenuItem>
                  </TextField>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Schedule
                  </Typography>
                  <TextField
                    select
                    value={addForm.schedule}
                    onChange={(e) => handleAddFormChange('schedule', e.target.value)}
                    fullWidth
                    required
                  >
                    <MenuItem value="daily">Daily</MenuItem>
                    <MenuItem value="weekly">Weekly</MenuItem>
                    <MenuItem value="monthly">Monthly</MenuItem>
                    <MenuItem value="custom">Custom</MenuItem>
                  </TextField>
                </Box>
                {addForm.schedule === 'custom' && (
                  <Box>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Custom Schedule
                    </Typography>
                    <TextField
                      value={addForm.customSchedule}
                      onChange={(e) => handleAddFormChange('customSchedule', e.target.value)}
                      fullWidth
                      placeholder="Enter cron expression (e.g. 0 9 * * *)"
                      helperText="Use cron expression format"
                    />
                  </Box>
                )}
              </Stack>
            );
          case 2:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Description
                  </Typography>
                  <TextField
                    multiline
                    rows={3}
                    value={addForm.description}
                    onChange={(e) => handleAddFormChange('description', e.target.value)}
                    fullWidth
                    placeholder="Enter report description"
                  />
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Recipients
                  </Typography>
                  <TextField
                    select
                    SelectProps={{
                      multiple: true,
                      value: addForm.recipients,
                      onChange: (e) => handleAddFormChange('recipients', e.target.value),
                    }}
                    fullWidth
                    required
                  >
                    <MenuItem value="user1@example.com">user1@example.com</MenuItem>
                    <MenuItem value="user2@example.com">user2@example.com</MenuItem>
                    <MenuItem value="user3@example.com">user3@example.com</MenuItem>
                  </TextField>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Format
                  </Typography>
                  <TextField
                    select
                    value={addForm.format}
                    onChange={(e) => handleAddFormChange('format', e.target.value)}
                    fullWidth
                    required
                  >
                    <MenuItem value="pdf">PDF</MenuItem>
                    <MenuItem value="excel">Excel</MenuItem>
                    <MenuItem value="csv">CSV</MenuItem>
                  </TextField>
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Charts to Include
                  </Typography>
                  <TextField
                    select
                    SelectProps={{
                      multiple: true,
                      value: addForm.charts,
                      onChange: (e) => handleAddFormChange('charts', e.target.value),
                    }}
                    fullWidth
                    required
                  >
                    {chartsData.map((chart) => (
                      <MenuItem key={chart.id} value={chart.id}>
                        {chart.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Box>
              </Stack>
            );
          case 3:
            return (
              <Stack spacing={3} sx={{ mt: 2 }}>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Email Settings
                  </Typography>
                  <TextField
                    value={addForm.emailSubject}
                    onChange={(e) => handleAddFormChange('emailSubject', e.target.value)}
                    fullWidth
                    required
                    placeholder="Enter email subject"
                    sx={{ mb: 2 }}
                  />
                  <TextField
                    multiline
                    rows={4}
                    value={addForm.emailBody}
                    onChange={(e) => handleAddFormChange('emailBody', e.target.value)}
                    fullWidth
                    required
                    placeholder="Enter email body"
                  />
                </Box>
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Advanced Settings
                  </Typography>
                  <Stack spacing={2}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={addForm.advancedSettings.includeDataTable}
                          onChange={(e) => handleAddFormChange('advancedSettings', {
                            ...addForm.advancedSettings,
                            includeDataTable: e.target.checked
                          })}
                        />
                      }
                      label="Include Data Table"
                    />
                    <FormControlLabel
                      control={
                        <Switch
                          checked={addForm.advancedSettings.includeChart}
                          onChange={(e) => handleAddFormChange('advancedSettings', {
                            ...addForm.advancedSettings,
                            includeChart: e.target.checked
                          })}
                        />
                      }
                      label="Include Chart"
                    />
                    <Box sx={{ display: 'flex', gap: 2 }}>
                      <TextField
                        select
                        label="Page Size"
                        value={addForm.advancedSettings.pageSize}
                        onChange={(e) => handleAddFormChange('advancedSettings', {
                          ...addForm.advancedSettings,
                          pageSize: e.target.value
                        })}
                        sx={{ flex: 1 }}
                      >
                        <MenuItem value="A4">A4</MenuItem>
                        <MenuItem value="A3">A3</MenuItem>
                        <MenuItem value="Letter">Letter</MenuItem>
                      </TextField>
                      <TextField
                        select
                        label="Orientation"
                        value={addForm.advancedSettings.orientation}
                        onChange={(e) => handleAddFormChange('advancedSettings', {
                          ...addForm.advancedSettings,
                          orientation: e.target.value
                        })}
                        sx={{ flex: 1 }}
                      >
                        <MenuItem value="portrait">Portrait</MenuItem>
                        <MenuItem value="landscape">Landscape</MenuItem>
                      </TextField>
                    </Box>
                    <TextField
                      value={addForm.advancedSettings.header}
                      onChange={(e) => handleAddFormChange('advancedSettings', {
                        ...addForm.advancedSettings,
                        header: e.target.value
                      })}
                      fullWidth
                      placeholder="Enter header text"
                    />
                    <TextField
                      value={addForm.advancedSettings.footer}
                      onChange={(e) => handleAddFormChange('advancedSettings', {
                        ...addForm.advancedSettings,
                        footer: e.target.value
                      })}
                      fullWidth
                      placeholder="Enter footer text"
                    />
                  </Stack>
                </Box>
              </Stack>
            );
          default:
            return null;
        }
      }
      return null;
    };

    return (
      <Dialog
        open={addDialogOpen}
        onClose={handleAddClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Add {currentContent?.charAt(0).toUpperCase() + currentContent?.slice(1)}
        </DialogTitle>
        <DialogContent>
          {getStepContent()}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleAddClose}>Cancel</Button>
          {addStep > 1 && (
            <Button onClick={handleAddBack}>Back</Button>
          )}
          <Button
            onClick={handleAddNext}
            variant="contained"
            disabled={
              currentContent === 'sources'
                ? (addStep === 1 && (!addForm.name || !addForm.connectorType)) ||
                (addStep === 2 && (
                  (addForm.connectorType === 'csv' || addForm.connectorType === 'excel') && !addForm.selectedFile ||
                  (addForm.connectorType === 'mysql' && (!addForm.connectionConfig.host || !addForm.connectionConfig.port || !addForm.connectionConfig.database || !addForm.connectionConfig.username || !addForm.connectionConfig.password)) ||
                  (addForm.connectorType === 'gsheet' && !addForm.connectionConfig.url)
                ))
                : (currentContent === 'charts'
                  ? (addStep === 1 && (!addForm.name || !addForm.displayType || (addForm.displayType === 'chart' && !addForm.selectedChartType) || !addForm.mode)) ||
                  (addStep === 2 && (
                    (addForm.mode === 'normal' && (!addForm.sources.length || !addForm.query.filters.length)) ||
                    (addForm.mode === 'advanced' && !addForm.sqlQuery)
                  ))
                  : (currentContent === 'reports'
                    ? (addStep === 1 && (!addForm.name || !addForm.type || !addForm.schedule)) ||
                    (addStep === 2 && (!addForm.recipients.length || !addForm.format || !addForm.charts.length)) ||
                    (addStep === 3 && (!addForm.emailSubject || !addForm.emailBody))
                    : (!addForm.name || !addForm.type || (addStep === 2 && !addForm.schedule)))
                )
            }
          >
            {addStep === (currentContent === 'sources' ? 3 : currentContent === 'reports' ? 3 : 3) ? 'Create' : 'Next'}
          </Button>
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
      case 'sources':
        return (
          <Stack gap={1}>
            <Typography variant="h4" component="h1" gutterBottom>
              Sources
            </Typography>
            <Stack direction="row" justifyContent="end" alignItems="center" gap={1}>
              <Button
                variant="contained"
                startIcon={<RefreshIcon />}
              >
                Refresh
              </Button>
              <Button
                variant="contained"
                color="primary"
                onClick={handleAddClick}
                startIcon={<AddIcon />}
              >
                Add Source
              </Button>
            </Stack>
            <CustomizedDataGrid
              rows={sourcesData}
              columns={sourcesColumns}
              sx={{ '& .MuiDataGrid-cell:focus': { outline: 'none' } }}
              autoHeight
              disableColumnMenu
              disableRowSelectionOnClick
              columnBufferPx={2}
              onRowDoubleClick={handleRowDoubleClick}
            />
            {renderAddDialog()}
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
              <Button variant="contained" color="primary" onClick={handleAddClick}>
                Add Chart
              </Button>
            </Stack>
            <CustomizedDataGrid
              rows={chartsData}
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
              <Button variant="contained" color="primary" onClick={handleAddClick}>
                Add Report
              </Button>
            </Stack>
            <CustomizedDataGrid
              rows={reportsData}
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
      default:
        return null;
    }
  };

  return (
    <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>
      {renderContent()}
      {renderEditDialog()}
      {renderDeleteDialog()}
      {renderAddDialog()}
    </Box>
  );
}

