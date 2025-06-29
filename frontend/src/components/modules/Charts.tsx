import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import RefreshIcon from '@mui/icons-material/Refresh';
import CustomizedDataGrid from '../CustomizedDataGrid';
import { GridColDef, GridRenderCellParams, GridRowParams } from '@mui/x-data-grid';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ChartSummary } from '../../types/chart';
import { API_CONFIG } from '../../config/api';
import { Box, CircularProgress } from '@mui/material';
import CardAlert from '../CardAlert';
import DeleteConfirmationDialog from '../dialogs/DeleteConfirmationDialog';
import AddChartDialog from '../dialogs/AddChartDiaglog';
import EditChartDialog from '../dialogs/EditChartDialog';
import Search from '../Search';

interface ChartsMetadata {
    total_elements: number;
    number_of_elements: number;
    total_pages: number;
    current_page: number;
    page_size: number;
}

export default function Charts() {
    const navigate = useNavigate();
    const location = useLocation();

    const [chartsData, setChartsData] = useState<ChartSummary[]>([]);
    const [metadata, setMetadata] = useState<ChartsMetadata>({
        total_elements: 0,
        number_of_elements: 0,
        total_pages: 0,
        current_page: 0,
        page_size: 10
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    // Popup notification states
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [showSuccessPopup, setShowSuccessPopup] = useState(false);

    // Dialog states
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [chartToDelete, setChartToDelete] = useState<ChartSummary | null>(null);

    // Pagination states
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);

    // Search state
    const [searchTerm, setSearchTerm] = useState('');

    const [addDialogOpen, setAddDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [chartToEdit, setChartToEdit] = useState<ChartSummary | null>(null);

    // Function to update URL with pagination parameters
    const updateURLWithPagination = useCallback((page: number, size: number, search: string = '') => {
        const urlParams = new URLSearchParams(location.search);
        urlParams.set('page', page.toString());
        urlParams.set('pageSize', size.toString());
        
        if (search) {
            urlParams.set('search', search);
        } else {
            urlParams.delete('search');
        }
        
        // Preserve other URL parameters (like success)
        const newSearch = urlParams.toString();
        const newPath = `/dashboard/charts${newSearch ? `?${newSearch}` : ''}`;
        
        navigate(newPath, { replace: true });
    }, [location.search, navigate]);

    // Function to get pagination parameters from URL
    const getPaginationFromURL = () => {
        const urlParams = new URLSearchParams(location.search);
        const page = parseInt(urlParams.get('page') || '0', 10);
        const size = parseInt(urlParams.get('pageSize') || '10', 10);
        const search = urlParams.get('search') || '';
        return { page, size, search };
    };

    // Check for success parameter in URL and pagination parameters
    useEffect(() => {
        const urlParams = new URLSearchParams(location.search);
        const successParam = urlParams.get('success');
        const { page, size, search } = getPaginationFromURL();

        if (successParam === 'updated') {
            setSuccess('Chart updated successfully');
            setShowSuccessPopup(true);
            // Clear only the success parameter, keep pagination and search
            urlParams.delete('success');
            const newSearch = urlParams.toString();
            const newPath = `/dashboard/charts${newSearch ? `?${newSearch}` : ''}`;
            navigate(newPath, { replace: true });
        }

        // Update pagination and search state from URL
        setCurrentPage(page);
        setPageSize(size);
        setSearchTerm(search);
    }, [location.search, navigate]);

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
            const newPath = `/dashboard/charts?${newSearch}`;
            navigate(newPath, { replace: true });
        }
    }, []); // Only run once on mount

    const fetchCharts = useCallback(async (page: number = 0, pageSize: number = 10, search: string = '') => {
        try {
            setLoading(true);
            setError(null);
            
            const params = new URLSearchParams();
            params.append('page', page.toString());
            params.append('limit', pageSize.toString());
            if (search.trim()) {
                params.append('search', search.trim());
            }
            
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}?${params.toString()}`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('Failed to fetch charts');
            }
            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                throw new TypeError("Response was not JSON");
            }
            const data = await response.json();
            if (data.success) {
                const processedCharts = data.result.charts.map((chart: any) => ({
                    ...chart,
                    id: chart.id.toString()
                }));
                setChartsData(processedCharts);
                setMetadata(data.result.metadata);
                setCurrentPage(page);
                setPageSize(pageSize);
            } else {
                setError(data.message || 'Failed to fetch charts');
                setShowErrorPopup(true);
            }
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Failed to fetch charts');
            setShowErrorPopup(true);
            setChartsData([]);
            setMetadata({
                total_elements: 0,
                number_of_elements: 0,
                total_pages: 0,
                current_page: 0,
                page_size: pageSize
            });
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        const { page, size, search } = getPaginationFromURL();
        fetchCharts(page, size, search);
    }, [fetchCharts]);

    const handlePageChange = useCallback((page: number, size: number) => {
        updateURLWithPagination(page, size, searchTerm);
        fetchCharts(page, size, searchTerm);
    }, [searchTerm, updateURLWithPagination, fetchCharts]);

    const handleSearchChange = useCallback((searchTerm: string) => {
        // Reset to first page when searching
        const newPage = 0;
        updateURLWithPagination(newPage, pageSize, searchTerm);
        fetchCharts(newPage, pageSize, searchTerm);
    }, [pageSize, fetchCharts, updateURLWithPagination]);

    const handleRowDoubleClick = async (params: GridRowParams<ChartSummary>) => {
        if (params.row) {
            const clickedChartId = params.row.id.toString();

            navigate(`/dashboard/charts/${clickedChartId}/view`);
        }
    }

    const handleEditClick = (row: any) => {
        setChartToEdit(row);
        setEditDialogOpen(true);
    };

    const handleDeleteClick = (row: any) => {
        setChartToDelete(row);
        setDeleteDialogOpen(true);
    };

    const handleAddClick = () => {
        setAddDialogOpen(true);
    };

    const handleDeleteConfirm = async () => {
        if (!chartToDelete) return;

        try {
            setError(null);
            setSuccess(null);

            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}/${chartToDelete.id}`, {
                method: 'DELETE',
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
                setSuccess('Chart deleted successfully');
                setShowSuccessPopup(true);
                setDeleteDialogOpen(false);
                setChartToDelete(null);
                fetchCharts(metadata.current_page, metadata.page_size, searchTerm);
            } else {
                setError(data.message || 'Failed to delete source');
                setShowErrorPopup(true);
            }
        } catch (error) {
            console.error('Error deleting source:', error);
            setError(error instanceof Error ? error.message : 'Failed to delete source');
            setShowErrorPopup(true);
        }
    };

    const chartsColumns: GridColDef[] = [
        { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
        { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
        {
            field: 'type',
            headerName: 'Type',
            flex: 1,
            minWidth: 150,
            renderCell: (params: GridRenderCellParams<ChartSummary>) => (
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        height: '100%',
                    }}
                >
                    <Box
                        component="span"
                        sx={{
                            color: 'success.main',
                            border: '1.5px solid',
                            borderColor: 'success.light',
                            backgroundColor: 'white',
                            borderRadius: '16px',
                            px: 1,
                            py: 0,
                            fontWeight: 600,
                            fontSize: '1rem',
                            minWidth: 60,
                            textAlign: 'center',
                            ml: 1,
                            lineHeight: 1.5,
                        }}
                    >
                        {params.value}
                    </Box>
                </Box>
            ),
        },
        { field: 'updated_at', headerName: 'Updated At', flex: 1, minWidth: 180 },
        {
            field: 'created_at', headerName: 'Created At', flex: 1, minWidth: 180
        },
        {
            field: 'actions',
            headerName: 'Actions',
            flex: 0.5,
            minWidth: 120,
            sortable: false,
            renderCell: (params: GridRenderCellParams<ChartSummary>) => {
                if (!params.row) return null;
                return (
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
                );
            },
        },
    ];

    const handleRefresh = useCallback(() => {
        fetchCharts(metadata.current_page, metadata.page_size, searchTerm);
    }, [metadata.current_page, metadata.page_size, searchTerm, fetchCharts]);

    function handleAddClose(): void {
        setAddDialogOpen(false);
    }

    const handleAddSuccess = () => {
        setAddDialogOpen(false);
        setSuccess('Chart created successfully');
        setShowSuccessPopup(true);
        // Refresh the charts list
        fetchCharts(metadata.current_page, metadata.page_size, searchTerm);
    };

    const handleEditSuccess = () => {
        setEditDialogOpen(false);
        setChartToEdit(null);
        setSuccess('Chart updated successfully');
        setShowSuccessPopup(true);
        // Refresh the charts list
        fetchCharts(metadata.current_page, metadata.page_size, searchTerm);
    };

    return (
        <Stack gap={2}>
            <Typography variant="h4" component="h2" gutterBottom>
                Charts
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
                <Search 
                    value={searchTerm}
                    onSearchChange={handleSearchChange}
                    placeholder="Search charts..."
                />
                <Stack direction="row" gap={1}>
                    <Button
                        variant="outlined"
                        startIcon={<RefreshIcon />}
                        onClick={handleRefresh}
                        disabled={loading}
                        sx={{ minWidth: '120px' }}
                    >
                        Refresh
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleAddClick}
                        startIcon={<AddIcon />}
                        sx={{ minWidth: '140px', maxWidth: '140px' }}
                    >
                        Add Chart
                    </Button>
                </Stack>
            </Stack>
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <CustomizedDataGrid
                    rows={chartsData}
                    columns={chartsColumns}
                    // sx={{ '& .MuiDataGrid-cell:focus': { outline: 'none' } }}
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
                    paginationMode="server"
                    onPaginationModelChange={(model) => handlePageChange(model.page, model.pageSize)}
                    rowCount={metadata.total_elements}
                    pageSizeOptions={[10, 25, 50]}
                    paginationModel={{
                        page: currentPage,
                        pageSize: pageSize
                    }}
                    disableRowSelectionOnClick
                    onRowDoubleClick={handleRowDoubleClick}
                    hideFooterSelectedRowCount
                    loading={loading}
                />
            )}

            {/* Popup Notifications */}
            {showErrorPopup && (
                <CardAlert
                    open={showErrorPopup}
                    severity="error"
                    message={error || 'An error occurred'}
                    onClose={() => setShowErrorPopup(false)}
                    autoHideDuration={5000}
                    position="bottom-right"
                />
            )}

            {showSuccessPopup && (
                <CardAlert
                    open={showSuccessPopup}
                    severity="success"
                    message={success || 'Operation completed successfully'}
                    onClose={() => setShowSuccessPopup(false)}
                    autoHideDuration={2000}
                    position="bottom-right"
                />
            )}

            <DeleteConfirmationDialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={handleDeleteConfirm}
                title="Delete Chart"
                message={`Are you sure you want to delete "${chartToDelete?.name}"? This action cannot be undone.`}
                severity="error"
            />

            <AddChartDialog
                open={addDialogOpen}
                onClose={() => setAddDialogOpen(false)}
                onSuccess={handleAddSuccess}
            />

            <EditChartDialog
                open={editDialogOpen}
                onClose={() => {
                    setEditDialogOpen(false);
                    setChartToEdit(null);
                }}
                onSuccess={handleEditSuccess}
                chartId={chartToEdit?.id}
            />
        </Stack>
    );
} 