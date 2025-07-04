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
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import ShareIcon from '@mui/icons-material/Share';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { useState, useEffect, useCallback } from 'react';
import DeleteConfirmationDialog from '../dialogs/DeleteConfirmationDialog';
import CardAlert from '../CardAlert';
import { Box, CircularProgress } from '@mui/material';
import { ReportSummary } from '../../types/report';
import { API_CONFIG } from '../../config/api';
import { useNavigate, useLocation } from 'react-router-dom';
import AddReportDialog from '../dialogs/AddReportDialog';
import EditReportDialog from '../dialogs/EditReportDialog';
import Search from '../Search';

interface ReportsMetadata {
    total_elements: number;
    number_of_elements: number;
    total_pages: number;
    current_page: number;
    page_size: number;
}

export default function Reports() {
    const navigate = useNavigate();
    const location = useLocation();

    const [reportsData, setReportsData] = useState<ReportSummary[]>([]);
    const [metadata, setMetadata] = useState<ReportsMetadata>({
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
    const [reportToDelete, setReportToDelete] = useState<ReportSummary | null>(null);
    // Add Report dialog state
    const [addDialogOpen, setAddDialogOpen] = useState(false);
    // Edit Report dialog state
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [reportToEdit, setReportToEdit] = useState<any>(null);

    // Pagination states
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);

    // Search state
    const [searchQuery, setSearchQuery] = useState('');

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
        const newPath = `/dashboard/reports${newSearch ? `?${newSearch}` : ''}`;
        
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
            setSuccess('Report updated successfully');
            setShowSuccessPopup(true);
            // Clear only the success parameter, keep pagination and search
            urlParams.delete('success');
            const newSearch = urlParams.toString();
            const newPath = `/dashboard/reports${newSearch ? `?${newSearch}` : ''}`;
            navigate(newPath, { replace: true });
        }

        // Update pagination and search state from URL
        setCurrentPage(page);
        setPageSize(size);
        setSearchQuery(search);
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
            const newPath = `/dashboard/reports?${newSearch}`;
            navigate(newPath, { replace: true });
        }
    }, []); // Only run once on mount

    const fetchReports = useCallback(async (page: number = 0, pageSize: number = 10, search: string = '') => {
        try {
            setLoading(true);
            setError(null);
            
            const params = new URLSearchParams();
            params.append('page', page.toString());
            params.append('limit', pageSize.toString());
            if (search.trim()) {
                params.append('search', search.trim());
            }
            
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}?${params.toString()}`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('Failed to fetch reports');
            }
            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                throw new TypeError("Response was not JSON");
            }
            const data = await response.json();
            if (data.success) {
                const processedReports = data.result.reports.map((report: any) => ({
                    ...report,
                    id: report.id.toString()
                }));
                setReportsData(processedReports);
                setMetadata(data.result.metadata);
                setCurrentPage(page);
                setPageSize(pageSize);
            } else {
                setError(data.message || 'Failed to fetch reports');
                setShowErrorPopup(true);
            }
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Failed to fetch reports');
            setShowErrorPopup(true);
            setReportsData([]);
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
        fetchReports(page, size, search);
    }, [fetchReports]);

    const handlePageChange = useCallback((page: number, size: number) => {
        updateURLWithPagination(page, size, searchQuery);
        fetchReports(page, size, searchQuery);
    }, [searchQuery, updateURLWithPagination, fetchReports]);

    const handleSearchChange = useCallback((searchTerm: string) => {
        // Reset to first page when searching
        const newPage = 0;
        updateURLWithPagination(newPage, pageSize, searchTerm);
        fetchReports(newPage, pageSize, searchTerm);
    }, [pageSize, fetchReports, updateURLWithPagination]);

    const handleRowDoubleClick = async (params: GridRowParams<ReportSummary>) => {
        if (params.row) {
            const clickedReportId = params.row.id.toString();
            navigate(`/dashboard/reports/${clickedReportId}/view`);
        }
    }

    const handleEditClick = async (row: any) => {
        try {
            // Fetch the full report details including charts
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${row.id}`, {
                credentials: 'include'
            });
            const data = await response.json();
            if (!response.ok || !data.success) {
                throw new Error(data.message || 'Failed to fetch report details');
            }
            
            setReportToEdit(data.result);
            setEditDialogOpen(true);
        } catch (err: any) {
            setError(err.message || 'Failed to fetch report details');
            setShowErrorPopup(true);
        }
    };

    const handleDeleteClick = (row: any) => {
        setReportToDelete(row);
        setDeleteDialogOpen(true);
    };

    const handleAddClick = () => {
        setAddDialogOpen(true);
    };

    const handleDeleteConfirm = async () => {
        if (!reportToDelete) return;

        try {
            setError(null);
            setSuccess(null);

            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${reportToDelete.id}`, {
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
                setSuccess('Report deleted successfully');
                setShowSuccessPopup(true);
                setDeleteDialogOpen(false);
                setReportToDelete(null);
                fetchReports(metadata.current_page, metadata.page_size, searchQuery);
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

    const reportsColumns: GridColDef[] = [
        { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
        { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
        {
            field: 'number_of_charts',
            headerName: 'Number of Charts',
            flex: 1,
            minWidth: 150,
            renderCell: (params: GridRenderCellParams<ReportSummary>) => (
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
            renderCell: (params: GridRenderCellParams<ReportSummary>) => {
                const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
                const open = Boolean(anchorEl);
                const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
                    event.stopPropagation();
                    setAnchorEl(event.currentTarget);
                };
                const handleMenuClose = (_event?: React.SyntheticEvent | {}, _reason?: 'backdropClick' | 'escapeKeyDown') => {
                    setAnchorEl(null);
                };
                return (
                    <>
                        <IconButton
                            size="small"
                            onClick={handleMenuOpen}
                        >
                            <MoreVertIcon />
                        </IconButton>
                        <Menu
                            anchorEl={anchorEl}
                            open={open}
                            onClose={handleMenuClose}
                            onClick={handleMenuClose}
                            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                            transformOrigin={{ vertical: 'top', horizontal: 'right' }}
                        >
                            <MenuItem onClick={(e) => { e.stopPropagation(); handleEditClick(params.row); handleMenuClose(); }}>
                                <ListItemIcon><EditIcon fontSize="small" /></ListItemIcon>
                                <ListItemText>Edit</ListItemText>
                            </MenuItem>
                            <MenuItem onClick={(e) => { e.stopPropagation(); /* TODO: Clone logic */ handleMenuClose(); }}>
                                <ListItemIcon><ContentCopyIcon fontSize="small" /></ListItemIcon>
                                <ListItemText>Clone</ListItemText>
                            </MenuItem>
                            <MenuItem onClick={(e) => { e.stopPropagation(); /* TODO: Share logic */ handleMenuClose(); }}>
                                <ListItemIcon><ShareIcon fontSize="small" /></ListItemIcon>
                                <ListItemText>Share</ListItemText>
                            </MenuItem>
                            <MenuItem onClick={(e) => { e.stopPropagation(); handleDeleteClick(params.row); handleMenuClose(); }}>
                                <ListItemIcon><DeleteIcon fontSize="small" color="error" /></ListItemIcon>
                                <ListItemText>Delete</ListItemText>
                            </MenuItem>
                        </Menu>
                    </>
                );
            },
        },
    ];

    const handleRefresh = useCallback(() => {
        fetchReports(metadata.current_page, metadata.page_size, searchQuery);
    }, [metadata.current_page, metadata.page_size, searchQuery, fetchReports]);

    return (
        <Stack gap={2}>
            <Typography variant="h4" component="h2" gutterBottom>
                Reports
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
                <Search 
                    value={searchQuery}
                    onSearchChange={handleSearchChange}
                    placeholder="Search reports..."
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
                        Add Report
                    </Button>
                </Stack>
            </Stack>
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <CustomizedDataGrid
                    rows={reportsData}
                    columns={reportsColumns}
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
                title="Delete Report"
                message={`Are you sure you want to delete "${reportToDelete?.name}"? This action cannot be undone.`}
                severity="error"
            />

            <AddReportDialog
                open={addDialogOpen}
                onClose={() => setAddDialogOpen(false)}
                onSuccess={() => {
                    setAddDialogOpen(false);
                    fetchReports(currentPage, pageSize);
                    setSuccess('Report created successfully');
                    setShowSuccessPopup(true);
                }}
            />

            <EditReportDialog
                open={editDialogOpen}
                onClose={() => setEditDialogOpen(false)}
                onSuccess={() => {
                    setEditDialogOpen(false);
                    fetchReports(currentPage, pageSize);
                    setSuccess('Report updated successfully');
                    setShowSuccessPopup(true);
                }}
                report={reportToEdit}
            />
        </Stack>
    );
} 