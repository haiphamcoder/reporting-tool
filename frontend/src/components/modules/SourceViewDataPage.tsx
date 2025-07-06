import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { GridColDef } from '@mui/x-data-grid';
import CustomDataGrid from '../CustomizedDataGrid';
import { Button, Stack, Alert, CircularProgress, Snackbar, Chip } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { useState, useEffect, useCallback, useMemo } from 'react';
import { API_CONFIG } from '../../config/api';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogActions from '@mui/material/DialogActions';
import { useGridApiRef } from '@mui/x-data-grid';
import SearchWithField from '../SearchWithField';

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

interface SourceDetail {
  id: string;
  name: string;
  description: string;
  connector_type: number;
  table_name?: string;
  config?: any;
  mapping?: any[];
  status: number;
  last_sync_time?: string;
  created_at?: string;
  modified_at?: string;
  is_deleted?: boolean;
  is_starred?: boolean;
  user_id?: string;
  can_edit?: boolean;
  can_share?: boolean;
}

export default function SourceViewDataPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { source_id } = useParams();
  const apiRef = useGridApiRef();
  const [previewData, setPreviewData] = useState<PreviewData | null>(null);
  const [sourceDetail, setSourceDetail] = useState<SourceDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [editLoading, setEditLoading] = useState(false);
  const [snackbar, setSnackbar] = useState<{
    open: boolean;
    message: string;
    severity: 'success' | 'error' | 'warning' | 'info';
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
  
  // Search states
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedSearchField, setSelectedSearchField] = useState('');
  const [searchFields, setSearchFields] = useState<Array<{field: string, label: string}>>([]);
  const [isInitialized, setIsInitialized] = useState(false);

  // Function to fetch source detail
  const fetchSourceDetail = useCallback(async () => {
    if (!source_id) return;
    
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_DETAILS.replace(':id', source_id)}`, {
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
      if (data.success && data.result) {
        setSourceDetail(data.result);
      } else {
        console.warn('Failed to fetch source detail:', data.message);
      }
    } catch (error) {
      console.error('Error fetching source detail:', error);
      // Don't set error state here as it's not critical for the main functionality
    }
  }, [source_id]);

  // Function to update URL with pagination parameters
  const updateURLWithPagination = useCallback((page: number, size: number, search: string = '', field: string = '') => {
    const urlParams = new URLSearchParams(location.search);
    urlParams.set('page', page.toString());
    urlParams.set('pageSize', size.toString());
    
    if (search) {
      urlParams.set('search', search);
    } else {
      urlParams.delete('search');
    }
    
    if (field) {
      urlParams.set('search-by', field);
    } else {
      urlParams.delete('search-by');
    }

    // Preserve other URL parameters
    const newSearch = urlParams.toString();
    const newPath = `/dashboard/sources/${source_id}/view-data${newSearch ? `?${newSearch}` : ''}`;

    navigate(newPath, { replace: true });
  }, [source_id, navigate]);





  // Memoize URL parameters to avoid unnecessary re-renders
  const urlParams = useMemo(() => {
    const params = new URLSearchParams(location.search);
    const page = parseInt(params.get('page') || '0', 10);
    const size = parseInt(params.get('pageSize') || '10', 10);
    const search = params.get('search') || '';
    const field = params.get('search-by') || '';
    return { page, size, search, field };
  }, [location.search]);

  const handleBack = () => {
    navigate('/dashboard/sources');
  };

  const fetchSourceData = useCallback(async (page: number = 0, size: number = 10, search: string = '', field: string = '') => {
    const currentSourceId = source_id;
    if (!currentSourceId) {
      return;
    }

    try {
      setLoading(true);
      setError(null);
      let url = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_PREVIEW.replace(':source_id', currentSourceId)}?page=${page}&limit=${size}`;
      
      // Add search parameters if provided
      if (search && field) {
        url += `&search=${encodeURIComponent(search)}&search-by=${encodeURIComponent(field)}`;
      }
      
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
      } else {
        setError(data.message || 'Failed to fetch preview data');
      }
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Failed to fetch preview data');
    } finally {
      setLoading(false);
    }
  }, []); // Empty dependency array like in Sources

  const handlePageChange = useCallback((page: number, size: number) => {
    updateURLWithPagination(page, size, searchTerm, selectedSearchField);
  }, [searchTerm, selectedSearchField, updateURLWithPagination]);

  const handleRefresh = useCallback(() => {
    fetchSourceData(urlParams.page, urlParams.size, searchTerm, selectedSearchField);
  }, [searchTerm, selectedSearchField, urlParams]);

  // Handle search change
  const handleSearchChange = useCallback((newSearchTerm: string, newSelectedField: string) => {
    console.log('handleSearchChange called with:', { newSearchTerm, newSelectedField });
    console.log('Current state:', { searchTerm, selectedSearchField, isInitialized });
    
    // Only update URL if component is initialized and there's a real change
    if (isInitialized && (newSearchTerm !== searchTerm || newSelectedField !== selectedSearchField)) {
      setSearchTerm(newSearchTerm);
      setSelectedSearchField(newSelectedField);
      // Reset to first page when searching
      const newPage = 0;
      updateURLWithPagination(newPage, urlParams.size, newSearchTerm, newSelectedField);
    } else {
      // Just update state without changing URL
      setSearchTerm(newSearchTerm);
      setSelectedSearchField(newSelectedField);
    }
  }, [searchTerm, selectedSearchField, isInitialized, updateURLWithPagination, urlParams.size]);

  // Handle field change
  const handleFieldChange = useCallback((field: string) => {
    console.log('handleFieldChange called with:', { field });
    console.log('Current selectedSearchField:', selectedSearchField, 'isInitialized:', isInitialized);
    
    // Only update URL if component is initialized and field actually changed
    if (isInitialized && field !== selectedSearchField) {
      setSelectedSearchField(field);
      // Reset to first page when changing field
      const newPage = 0;
      updateURLWithPagination(newPage, urlParams.size, searchTerm, field);
    } else {
      // Just update state without changing URL
      setSelectedSearchField(field);
    }
  }, [searchTerm, selectedSearchField, isInitialized, updateURLWithPagination, urlParams.size]);

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
        handleRefresh();
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

  // Read URL parameters, update state, and fetch data
  useEffect(() => {
    if (source_id) {
      // Read directly from location.search to avoid timing issues
      const params = new URLSearchParams(location.search);
      const page = parseInt(params.get('page') || '0', 10);
      const size = parseInt(params.get('pageSize') || '10', 10);
      const search = params.get('search') || '';
      const field = params.get('search-by') || '';
      
      setCurrentPage(page);
      setPageSize(size);
      setSearchTerm(search);
      setSelectedSearchField(field);
      fetchSourceData(page, size, search, field);
      setIsInitialized(true);
    }
  }, [location.search, source_id]);

  // Fetch source detail when component mounts or source_id changes
  useEffect(() => {
    fetchSourceDetail();
  }, [source_id, fetchSourceDetail]);

  // Update search fields when preview data changes
  useEffect(() => {
    if (previewData && previewData.schema && previewData.schema.length > 0) {
      const fields = previewData.schema
        .filter((field: Schema) => field.field_mapping !== '_id_')
        .map((field: Schema) => ({
          field: field.field_mapping,
          label: field.field_name
        }));
      setSearchFields(fields);
      
      // Set default search field if not already set
      if (!selectedSearchField && fields.length > 0) {
        setSelectedSearchField(fields[0].field);
      }
    }
  }, [previewData]); // Only depend on previewData

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
    // Block edit if can_edit is false
    if (sourceDetail?.can_edit === false) {
      setSnackbar({
        open: true,
        message: 'This source is in view-only mode. Editing is not allowed.',
        severity: 'warning'
      });
      return Promise.reject(oldRow);
    }
    
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
        editable: sourceDetail?.can_edit !== false,
      }));

    // Create search fields from first record if no schema
    if (Object.keys(firstRecord).length > 0 && searchFields.length === 0) {
      const fields = Object.keys(firstRecord)
        .filter(key => key !== '_id_')
        .map(key => ({
          field: key,
          label: key
        }));
      setSearchFields(fields);
      
      // Set default search field if not already set
      if (!selectedSearchField && fields.length > 0) {
        setSelectedSearchField(fields[0].field);
      }
    }

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
            {sourceDetail?.can_edit === false && (
              <Chip label="View Only" color="warning" size="small" sx={{ ml: 1 }} />
            )}
          </Typography>
        </Stack>
        <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
          <Stack direction="row" justifyContent="start" alignItems="center" gap={1}>
            <SearchWithField
              value={searchTerm}
              onSearchChange={handleSearchChange}
              onFieldChange={handleFieldChange}
              fields={searchFields}
              selectedField={selectedSearchField}
              placeholder="Search data..."
            />
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

  const columns: GridColDef[] = displaySchema.map((field: Schema) => ({
    field: field.field_mapping,
    headerName: field.field_name,
    flex: 1,
    minWidth: 150,
    editable: sourceDetail?.can_edit !== false,
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
          {sourceDetail?.can_edit === false && (
            <Chip label="View Only" color="warning" size="small" sx={{ ml: 1 }} />
          )}
        </Typography>
      </Stack>
      <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
        <Stack direction="row" justifyContent="start" alignItems="center" gap={1}>
          <SearchWithField
            value={searchTerm}
            onSearchChange={handleSearchChange}
            onFieldChange={handleFieldChange}
            fields={searchFields}
            selectedField={selectedSearchField}
            placeholder="Search data..."
          />
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