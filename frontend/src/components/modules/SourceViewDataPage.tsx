import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { GridColDef } from '@mui/x-data-grid';
import CustomizedDataGrid from '../CustomizedDataGrid';
import { Button, Stack, Alert, CircularProgress } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useNavigate, useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../../config/api';

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
  const { source_id } = useParams();
  const [previewData, setPreviewData] = useState<PreviewData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

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
          console.log('Using schema and records structure');
          previewDataToSet = data.result;
        } else if (data.result && Array.isArray(data.result)) {
          // If result is an array, treat it as records
          console.log('Using array as records structure');
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
            console.log('Detected sources list response, not preview data');
            setError('This endpoint returns sources list, not preview data');
            return;
          }

          // Try to treat the object as a single record
          console.log('Using object as single record structure');
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
          console.log('Unexpected data structure:', data.result);
          setError('Unexpected data structure from API');
          return;
        }

        console.log('Processed preview data:', previewDataToSet);
        setPreviewData(previewDataToSet);
        setCurrentPage(page);
        setPageSize(size);
      } else {
        console.log('API returned success: false');
        setError(data.message || 'Failed to fetch preview data');
      }
    } catch (error) {
      console.error('Error in fetchSourcePreview:', error);
      setError(error instanceof Error ? error.message : 'Failed to fetch preview data');
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number, size: number) => {
    fetchSourceData(page, size);
  };

  const handleRefresh = () => {
    fetchSourceData(currentPage, pageSize);
  };

  // Fetch data on component mount
  useEffect(() => {
    console.log('SourcePreview useEffect - source_id:', source_id);
    if (source_id) {
      console.log('Fetching preview data for source_id:', source_id);
      fetchSourceData(0, 10);
    } else {
      console.log('No source_id in useEffect');
    }
  }, [source_id]);

  console.log('SourcePreview render - source_id:', source_id, 'loading:', loading, 'error:', error, 'previewData:', previewData);

  if (!source_id) {
    console.log('Rendering error: Source ID not found');
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">Source ID not found</Alert>
      </Box>
    );
  }

  if (loading && !previewData) {
    console.log('Rendering loading state');
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    console.log('Rendering error state:', error);
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" onClose={() => setError(null)}>
          {error}
        </Alert>
      </Box>
    );
  }

  if (!previewData) {
    console.log('Rendering no data state');
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="info">No preview data available</Alert>
      </Box>
    );
  }

  console.log('Rendering preview data with records:', previewData.records?.length);

  // Validate schema and records
  if (!previewData.schema || !Array.isArray(previewData.schema) || previewData.schema.length === 0) {
    console.log('Invalid schema, creating default columns from records');
    const firstRecord = previewData.records && previewData.records.length > 0 ? previewData.records[0] : {};
    const columns: GridColDef[] = Object.keys(firstRecord).map((key) => ({
      field: key,
      headerName: key,
      flex: 1,
      minWidth: 150,
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
        <CustomizedDataGrid
          rows={previewData.records}
          columns={columns}
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
          columnBufferPx={2}
          density="compact"
          disableColumnResize
          paginationMode="server"
          rowCount={-1}
          pageSizeOptions={[10, 25, 50, 100]}
          paginationModel={{
            page: currentPage,
            pageSize: pageSize
          }}
          onPaginationModelChange={(model) => {
            console.log('Pagination changed:', model);
            handlePageChange(model.page, model.pageSize);
          }}
          loading={loading}
          hideFooterSelectedRowCount
        />
      </Stack>
    );
  }

  const columns: GridColDef[] = previewData.schema.map((field) => ({
    field: field.field_mapping,
    headerName: field.field_name,
    flex: 1,
    minWidth: 150,
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
      <CustomizedDataGrid
        rows={previewData.records}
        columns={columns}
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
        onPaginationModelChange={(model) => {
          console.log('Pagination changed:', model);
          handlePageChange(model.page, model.pageSize);
        }}
        loading={loading}
        hideFooterSelectedRowCount
      />
    </Stack>
  );
} 