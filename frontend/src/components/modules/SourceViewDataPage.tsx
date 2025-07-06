import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { GridColDef } from '@mui/x-data-grid';
import CustomDataGrid from '../CustomizedDataGrid';
import { Button, Stack, Alert, CircularProgress, Snackbar } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../../config/api';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogActions from '@mui/material/DialogActions';
import { useGridApiRef } from '@mui/x-data-grid';

interface Schema {
  field_name: string;
  field_mapping: string;
  field_type: string;
  is_hidden: boolean;
}

interface PreviewData {
  schema: Schema[];
  records: any[];
}

export default function SourceViewDataPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { source_id } = useParams();
  const apiRef = useGridApiRef();
  const [previewData, setPreviewData] = useState<PreviewData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [editLoading, setEditLoading] = useState(false);
  const [snackbar, setSnackbar] = useState<{
    open: boolean;
    message: string;
    severity: 'success' | 'error';
  }>({ open: false, message: '', severity: 'success' });
  // State for edit confirmation dialog
  const [editDialog, setEditDialog] = useState<{
    open: boolean;
    newRow: any | null;
    oldRow: any | null;
    resolve: ((value: any) => void) | null;
    reject: ((reason?: any) => void) | null;
    id?: string | number;
    field?: string;
  }>({ open: false, newRow: null, oldRow: null, resolve: null, reject: null, id: undefined, field: undefined });
  const [forceUpdate, setForceUpdate] = useState(0);

  // Function to update URL with pagination parameters
  const updateURLWithPagination = (page: number, size: number) => {
    const urlParams = new URLSearchParams(location.search);
    urlParams.set('page', page.toString());
    urlParams.set('pageSize', size.toString());
    
    // Preserve other URL parameters
    const newSearch = urlParams.toString();
    const newPath = `/dashboard/sources/${source_id}/view-data${newSearch ? `?${newSearch}` : ''}`;
    
    navigate(newPath, { replace: true });
  };

  // Function to get pagination parameters from URL
  const getPaginationFromURL = () => {
    const urlParams = new URLSearchParams(location.search);
    const page = parseInt(urlParams.get('page') || '0', 10);
    const size = parseInt(urlParams.get('pageSize') || '10', 10);
    return { page, size };
  };

  // Initialize URL with default pagination values on first load
  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const hasPageParam = urlParams.has('page');
    const hasPageSizeParam = urlParams.has('pageSize');
    
    // If URL doesn't have pagination parameters, add default values
    if (!hasPageParam || !hasPageSizeParam) {
      const defaultPage = hasPageParam ? parseInt(urlParams.get('page') || '0', 10) : 0;
      const defaultSize = hasPageSizeParam ? parseInt(urlParams.get('pageSize') || '10', 10) : 10;
      
      urlParams.set('page', defaultPage.toString());
      urlParams.set('pageSize', defaultSize.toString());
      
      const newSearch = urlParams.toString();
      const newPath = `/dashboard/sources/${source_id}/view-data?${newSearch}`;
      navigate(newPath, { replace: true });
    }
  }, [source_id]); // Run when source_id changes

  const handleBack = () => {
    navigate('/dashboard/sources');
  };

  const fetchSourceData = async (page: number = 0, size: number = 10) => {
    if (!source_id) {
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const url = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_PREVIEW.replace(':source_id', source_id)}?page=${page}&limit=${size}`;
      const response = await fetch(url, {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      if (data.success) {
        // Handle different response structures
        let previewDataToSet;

        if (data.result && data.result.schema && data.result.records) {
          // Expected structure: { schema: [...], records: [...] }
          previewDataToSet = data.result;
        } else if (data.result && Array.isArray(data.result)) {
          // If result is an array, treat it as records
          previewDataToSet = {
            schema: data.result.length > 0 ? Object.keys(data.result[0]).map(key => ({
              field_name: key,
              field_mapping: key,
              field_type: 'string',
              is_hidden: false
            })) : [],
            records: data.result
          };
        } else if (data.result && typeof data.result === 'object') {
          // If result is an object, try to extract schema and records
          if (data.result.sources) {
            // This might be the sources list response
            setError('This endpoint returns sources list, not preview data');
            return;
          }

          // Try to treat the object as a single record
          previewDataToSet = {
            schema: Object.keys(data.result).map(key => ({
              field_name: key,
              field_mapping: key,
              field_type: 'string',
              is_hidden: false
            })),
            records: [data.result]
          };
        } else {
          setError('Unexpected data structure from API');
          return;
        }

        setPreviewData(previewDataToSet);
        setCurrentPage(page);
        setPageSize(size);
      } else {
        setError(data.message || 'Failed to fetch preview data');
      }
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Failed to fetch preview data');
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number, size: number) => {
    updateURLWithPagination(page, size);
    fetchSourceData(page, size);
  };

  const handleRefresh = () => {
    fetchSourceData(currentPage, pageSize);
  };

  // Function to save edited data
  const handleSaveData = async (updatedData: any) => {
    if (!source_id) {
      setSnackbar({
        open: true,
        message: 'Source ID not found',
        severity: 'error'
      });
      return;
    }

    try {
      setEditLoading(true);
      const url = `${API_CONFIG.BASE_URL}/data-processing/sources/${source_id}/data`;
      const response = await fetch(url, {
        method: 'PUT',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ data: updatedData }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      if (data.success) {
        setSnackbar({
          open: true,
          message: 'Data updated successfully',
          severity: 'success'
        });
        // Refresh data to show updated values
        fetchSourceData(currentPage, pageSize);
      } else {
        setSnackbar({
          open: true,
          message: data.message || 'Failed to update data',
          severity: 'error'
        });
      }
    } catch (error) {
      setSnackbar({
        open: true,
        message: error instanceof Error ? error.message : 'Failed to update data',
        severity: 'error'
      });
    } finally {
      setEditLoading(false);
    }
  };

  // Fetch data on component mount
  useEffect(() => {
    if (source_id) {
      const { page, size } = getPaginationFromURL();
      fetchSourceData(page, size);
    } else {
      console.log('No source_id in useEffect');
    }
  }, [source_id, location.search]); // Also listen to location.search changes

  // Function to create a unique, stable hash from row data (no timestamp/random)
  const createRowHash = (row: any): string => {
    // Simple hash function
    const hashString = (str: string): number => {
      let hash = 0;
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32-bit integer
      }
      return Math.abs(hash);
    };
    // Convert row data to string and hash it
    const rowString = JSON.stringify(row);
    const dataHash = hashString(rowString);
    return `row_${dataHash}`;
  };

  // Handle row update (cell edit) with confirmation dialog
  const processRowUpdate = (newRow: any, oldRow: any) => {
    // Chỉ cho phép 1 cell edit tại 1 thời điểm
    if (editDialog.open) {
      return Promise.reject(oldRow);
    }
    // Tìm field đang edit
    let changedField: string | undefined = undefined;
    if (oldRow && newRow) {
      for (const key of Object.keys(newRow)) {
        if (newRow[key] !== oldRow[key]) {
          changedField = key;
          break;
        }
      }
    }
    const id = (newRow._id_ ?? newRow.id) ?? undefined;
    return new Promise((resolve, reject) => {
      setEditDialog({ open: true, newRow, oldRow, resolve, reject, id, field: changedField });
    });
  };

  // Handler for confirming edit
  const handleConfirmEdit = async () => {
    if (!editDialog.newRow) return;
    try {
      await handleSaveData(editDialog.newRow);
      editDialog.resolve && editDialog.resolve(editDialog.newRow);
    } catch (e) {
      editDialog.reject && editDialog.reject(e);
    } finally {
      setEditDialog({ open: false, newRow: null, oldRow: null, resolve: null, reject: null, id: undefined, field: undefined });
      // Đảm bảo cell trở về view mode
      if (editDialog.id !== undefined && editDialog.field !== undefined) {
        apiRef.current.stopCellEditMode({ id: editDialog.id, field: editDialog.field });
      }
    }
  };

  // Handler for canceling edit
  const handleCancelEdit = () => {
    // Đảm bảo cell trở về view mode trước khi đóng dialog
    if (editDialog.id !== undefined && editDialog.field !== undefined) {
      apiRef.current.stopCellEditMode({ id: editDialog.id, field: editDialog.field });
    }
    if (editDialog.reject && editDialog.oldRow) {
      editDialog.reject(editDialog.oldRow);
    } else if (editDialog.reject) {
      editDialog.reject();
    }
    setEditDialog({ open: false, newRow: null, oldRow: null, resolve: null, reject: null, id: undefined, field: undefined });
    setForceUpdate(f => f + 1);
  };

  if (!source_id) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">Source ID not found</Alert>
      </Box>
    );
  }

  if (loading && !previewData) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" onClose={() => setError(null)}>
          {error}
        </Alert>
      </Box>
    );
  }

  if (!previewData) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="info">No preview data available</Alert>
      </Box>
    );
  }

  // Filter out _id_ field from schema for display, but keep it in records
  const displaySchema = previewData.schema.filter(field => field.field_mapping !== '_id_');

  // Validate schema and records
  if (!displaySchema || !Array.isArray(displaySchema) || displaySchema.length === 0) {
    const firstRecord = previewData.records && previewData.records.length > 0 ? previewData.records[0] : {};
    const columns: GridColDef[] = Object.keys(firstRecord)
      .filter(key => key !== '_id_') // Filter out _id_ field
      .map((key) => ({
        field: key,
        headerName: key,
        flex: 1,
        minWidth: 150,
        editable: true,
      }));

    return (
      <Stack gap={2} sx={{ height: '100%', width: '100%' }}>
        <Stack direction="row" justifyContent="start" alignItems="center" gap={1}>
          <IconButton
            onClick={handleBack}
            sx={{
              mr: 2,
              border: '1px solid',
              borderColor: 'divider',
              '&:hover': {
                backgroundColor: 'action.hover',
              }
            }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h5" component="h1" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            View Data:
            <Box
              component="span"
              sx={{
                color: 'success.main',
                border: '1.5px solid',
                borderColor: 'success.light',
                backgroundColor: 'white',
                borderRadius: '16px',
                px: 1.5,
                py: 0.25,
                fontWeight: 600,
                fontSize: '1rem',
                ml: 1,
                display: 'inline-block',
                minWidth: 60,
                textAlign: 'center',
              }}
            >
              {source_id}
            </Box>
          </Typography>
        </Stack>
        <Stack direction="row" justifyContent="end" alignItems="center" gap={1}>
          <Button
            variant="contained"
            startIcon={<RefreshIcon />}
            onClick={handleRefresh}
            disabled={loading}
            sx={{ minWidth: '120px' }}
          >
            Refresh
          </Button>
        </Stack>
        <CustomDataGrid
          apiRef={apiRef}
          rows={previewData.records}
          columns={columns}
          getRowId={(row: any) => row._id_ ?? row.id ?? createRowHash(row)}
          key={forceUpdate}
          sx={{
            height: '100% !important',
            minHeight: '500px',
            maxHeight: '500px',
            '& .MuiDataGrid-cell:focus': { outline: 'none' },
            '& .MuiDataGrid-row': {
              borderBottom: '1px solid #e0e0e0',
            },
            '& .MuiDataGrid-row:hover': {
              backgroundColor: 'rgba(25, 118, 210, 0.08)',
            },
            '& .MuiDataGrid-virtualScroller': {
              overflow: 'auto !important',
            },
            '& .MuiDataGrid-main': {
              height: '100% !important',
            },
          }}
          disableColumnMenu
          disableRowSelectionOnClick
          disableColumnResize
          paginationMode="server"
          rowCount={-1}
          pageSizeOptions={[10, 25, 50, 100]}
          paginationModel={{
            page: currentPage,
            pageSize: pageSize
          }}
          onPaginationModelChange={(model: any) => {
            handlePageChange(model.page, model.pageSize);
          }}
          loading={loading || editLoading}
          hideFooterSelectedRowCount
          processRowUpdate={processRowUpdate}
        />
        <Snackbar
          open={snackbar.open}
          autoHideDuration={6000}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          <Alert 
            onClose={() => setSnackbar({ ...snackbar, open: false })} 
            severity={snackbar.severity}
            sx={{ width: '100%' }}
          >
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Stack>
    );
  }

  const columns: GridColDef[] = displaySchema.map((field) => ({
    field: field.field_mapping,
    headerName: field.field_name,
    flex: 1,
    minWidth: 150,
    editable: true,
  }));

  return (
    <Stack gap={2} sx={{ height: '100%', width: '100%' }}>
      <Stack direction="row" justifyContent="start" alignItems="center" gap={1}>
        <IconButton
          onClick={handleBack}
          sx={{
            mr: 2,
            border: '1px solid',
            borderColor: 'divider',
            '&:hover': {
              backgroundColor: 'action.hover',
            }
          }}
        >
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h5" component="h1" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          View Data:
          <Box
            component="span"
            sx={{
              color: 'success.main',
              border: '1.5px solid',
              borderColor: 'success.light',
              backgroundColor: 'white',
              borderRadius: '16px',
              px: 1.5,
              py: 0.25,
              fontWeight: 600,
              fontSize: '1rem',
              ml: 1,
              display: 'inline-block',
              minWidth: 60,
              textAlign: 'center',
            }}
          >
            {source_id}
          </Box>
        </Typography>
      </Stack>
      <Stack direction="row" justifyContent="end" alignItems="center" gap={1}>
        <Button
          variant="contained"
          startIcon={<RefreshIcon />}
          onClick={handleRefresh}
          disabled={loading}
          sx={{ minWidth: '140px', maxWidth: '140px' }}
        >
          Refresh
        </Button>
      </Stack>
      <CustomDataGrid
        apiRef={apiRef}
        rows={previewData.records}
        columns={columns}
        getRowId={(row: any) => row._id_ ?? row.id ?? createRowHash(row)}
        key={forceUpdate}
        sx={{
          height: '100% !important',
          minHeight: '500px',
          maxHeight: '500px',
          '& .MuiDataGrid-cell:focus': { outline: 'none' },
          '& .MuiDataGrid-row': {
            borderBottom: '1px solid #e0e0e0',
          },
          '& .MuiDataGrid-row:hover': {
            backgroundColor: 'rgba(25, 118, 210, 0.08)',
          },
          '& .MuiDataGrid-virtualScroller': {
            overflow: 'auto !important',
          },
          '& .MuiDataGrid-main': {
            height: '100% !important',
          },
        }}
        disableColumnMenu
        disableRowSelectionOnClick
        disableColumnResize
        paginationMode="server"
        rowCount={-1}
        pageSizeOptions={[10, 25, 50, 100]}
        paginationModel={{
          page: currentPage,
          pageSize: pageSize
        }}
        onPaginationModelChange={(model: any) => {
          handlePageChange(model.page, model.pageSize);
        }}
        loading={loading || editLoading}
        hideFooterSelectedRowCount
        processRowUpdate={processRowUpdate}
      />
      <Dialog open={editDialog.open} onClose={handleCancelEdit}>
        <DialogTitle>Confirm Edit</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to save these changes?
          </DialogContentText>
          {editDialog.oldRow && editDialog.newRow && (
            <Box sx={{ mt: 2 }}>
              {Object.keys(editDialog.newRow).map((key) => {
                if (editDialog.oldRow[key] !== editDialog.newRow[key]) {
                  return (
                    <Typography key={key} variant="body2">
                      <b>{key}:</b> "{editDialog.oldRow[key]}" → "{editDialog.newRow[key]}"
                    </Typography>
                  );
                }
                return null;
              })}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancelEdit} color="inherit">Cancel</Button>
          <Button onClick={handleConfirmEdit} color="primary" variant="contained">Save</Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert 
          onClose={() => setSnackbar({ ...snackbar, open: false })} 
          severity={snackbar.severity}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Stack>
  );
} 