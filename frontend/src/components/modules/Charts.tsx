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
import Avatar from '@mui/material/Avatar';
import CardAlert from '../CardAlert';
import DeleteConfirmationDialog from '../dialogs/DeleteConfirmationDialog';
import AddChartDialog from '../dialogs/AddChartDiaglog';
import EditChartDialog from '../dialogs/EditChartDialog';
import ShareChartDialog from '../dialogs/ShareChartDialog';
import Search from '../Search';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import ShareIcon from '@mui/icons-material/Share';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { useAuth } from '../../context/AuthContext';
import { chartApi } from '../../api/chart/chartApi';

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
    const { user } = useAuth();

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
    
    // Share dialog state
    const [shareDialogOpen, setShareDialogOpen] = useState(false);
    const [chartToShare, setChartToShare] = useState<ChartSummary | null>(null);

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
                const processedCharts = data.result.charts.map((chart: any) => {
                    console.log('Chart data:', chart); // Debug log
                    return {
                        ...chart,
                        id: chart.id.toString(),
                        can_edit: chart.can_edit ?? false,
                        can_share: chart.can_share ?? false
                    };
                });
                console.log('Processed charts:', processedCharts); // Debug log
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
        if (!row.can_edit) {
            setError('You do not have permission to edit this chart');
            setShowErrorPopup(true);
            return;
        }
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

    const handleShareClick = (row: ChartSummary) => {
        if (!row.can_share) {
            setError('You do not have permission to share this chart');
            setShowErrorPopup(true);
            return;
        }
        setChartToShare(row);
        setShareDialogOpen(true);
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

    const handleCloneClick = async (row: ChartSummary) => {
        try {
            setError(null);
            setSuccess(null);

            const data = await chartApi.cloneChart(row.id);

            if (data.success) {
                setSuccess('Chart cloned successfully');
                setShowSuccessPopup(true);
                // Refresh the charts list
                fetchCharts(metadata.current_page, metadata.page_size, searchTerm);
            } else {
                setError(data.message || 'Failed to clone chart');
                setShowErrorPopup(true);
            }
        } catch (error) {
            console.error('Error cloning chart:', error);
            setError(error instanceof Error ? error.message : 'Failed to clone chart');
            setShowErrorPopup(true);
        }
    };

    const chartsColumns: GridColDef[] = [
        { 
            field: 'name', 
            headerName: 'Name', 
            flex: 1.2, 
            minWidth: 120,
            headerAlign: 'left',
            align: 'left',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => (
                <Box sx={{ 
                    display: 'flex', 
                    alignItems: 'center',
                    justifyContent: 'left',
                    height: '100%',
                    width: '100%',
                }}>
                    <Typography 
                        variant="body2" 
                        sx={{ 
                            whiteSpace: 'normal',
                            wordBreak: 'break-word',
                            lineHeight: 1.4,
                            display: '-webkit-box',
                            WebkitLineClamp: 3,
                            WebkitBoxOrient: 'vertical',
                            overflow: 'hidden'
                        }}
                    >
                        {params.value}
                    </Typography>
                </Box>
            )
        },
        { 
            field: 'description', 
            headerName: 'Description', 
            flex: 1.5, 
            minWidth: 150,
            headerAlign: 'left',
            align: 'left',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => (
                <Box sx={{ 
                    display: 'flex', 
                    alignItems: 'center',
                    justifyContent: 'left',
                    height: '100%',
                    width: '100%'
                }}>
                    <Typography 
                        variant="body2" 
                        sx={{ 
                            whiteSpace: 'normal',
                            wordBreak: 'break-word',
                            lineHeight: 1.4,
                            display: '-webkit-box',
                            WebkitLineClamp: 3,
                            WebkitBoxOrient: 'vertical',
                            overflow: 'hidden'
                        }}
                    >
                        {params.value}
                    </Typography>
                </Box>
            )
        },
        {
            field: 'owner',
            headerName: 'Owner',
            flex: 0.8,
            minWidth: 120,
            headerAlign: 'center',
            align: 'center',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => {
                const owner = params.row?.owner;
                if (!owner) return null;
                
                const isCurrentUser = user && owner.id === user.user_id;
                const displayName = isCurrentUser ? 'Me' : owner.name;
                
                return (
                    <Stack direction="row" spacing={1} alignItems="center" justifyContent="center" sx={{ height: '100%', width: '100%' }}>
                        <Avatar
                            src={owner.avatar}
                            alt={owner.name}
                            sx={{ width: 28, height: 28 }}
                        >
                            {owner.name.charAt(0).toUpperCase()}
                        </Avatar>
                        <Typography variant="body2" sx={{ fontSize: '0.875rem' }}>
                            {displayName}
                        </Typography>
                    </Stack>
                );
            }
        },
        {
            field: 'type',
            headerName: 'Type',
            flex: 0.6,
            minWidth: 100,
            headerAlign: 'center',
            align: 'center',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => (
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        height: '100%',
                        justifyContent: 'center',
                    }}
                >
                    <Box
                        component="span"
                        sx={{
                            color: 'success.main',
                            border: '1.5px solid',
                            borderColor: 'success.light',
                            backgroundColor: 'white',
                            borderRadius: '12px',
                            px: 1,
                            py: 0.5,
                            fontWeight: 600,
                            fontSize: '0.75rem',
                            minWidth: 50,
                            textAlign: 'center',
                            lineHeight: 1.2,
                        }}
                    >
                        {params.value}
                    </Box>
                </Box>
            ),
        },
        { 
            field: 'updated_at', 
            headerName: 'Updated At', 
            flex: 0.8, 
            minWidth: 120,
            headerAlign: 'center',
            align: 'center',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => {
                const formatDateTime = (dateTimeString: string) => {
                    if (!dateTimeString) return { date: '', time: '' };
                    
                    // Giả sử format gốc là "yyyy-MM-dd HH:mm:ss"
                    const parts = dateTimeString.split(' ');
                    if (parts.length >= 2) {
                        return { date: parts[0], time: parts[1] };
                    }
                    
                    // Fallback nếu format khác
                    const date = new Date(dateTimeString);
                    const dateStr = date.toISOString().split('T')[0]; // yyyy-MM-dd
                    const timeStr = date.toTimeString().split(' ')[0]; // HH:mm:ss
                    
                    return { date: dateStr, time: timeStr };
                };

                const { date, time } = formatDateTime(params.value);

                return (
                    <Box sx={{ 
                        display: 'flex', 
                        flexDirection: 'column',
                        alignItems: 'center', 
                        justifyContent: 'center',
                        height: '100%',
                        width: '100%',
                        textAlign: 'center'
                    }}>
                        <Typography variant="body2">
                            {date}
                        </Typography>
                        <Typography variant="body2">
                            {time}
                        </Typography>
                    </Box>
                );
            }
        },
        {
            field: 'created_at', 
            headerName: 'Created At', 
            flex: 0.8, 
            minWidth: 120,
            headerAlign: 'center',
            align: 'center',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => {
                const formatDateTime = (dateTimeString: string) => {
                    if (!dateTimeString) return { date: '', time: '' };
                    
                    // Giả sử format gốc là "yyyy-MM-dd HH:mm:ss"
                    const parts = dateTimeString.split(' ');
                    if (parts.length >= 2) {
                        return { date: parts[0], time: parts[1] };
                    }
                    
                    // Fallback nếu format khác
                    const date = new Date(dateTimeString);
                    const dateStr = date.toISOString().split('T')[0]; // yyyy-MM-dd
                    const timeStr = date.toTimeString().split(' ')[0]; // HH:mm:ss
                    
                    return { date: dateStr, time: timeStr };
                };

                const { date, time } = formatDateTime(params.value);

                return (
                    <Box sx={{ 
                        display: 'flex', 
                        flexDirection: 'column',
                        alignItems: 'center', 
                        justifyContent: 'center',
                        height: '100%',
                        width: '100%',
                        textAlign: 'center'
                    }}>
                        <Typography variant="body2">
                            {date}
                        </Typography>
                        <Typography variant="body2">
                            {time}
                        </Typography>
                    </Box>
                );
            }
        },
        {
            field: 'actions',
            headerName: 'Actions',
            flex: 0.6,
            minWidth: 80,
            sortable: false,
            headerAlign: 'center',
            align: 'center',
            renderCell: (params: GridRenderCellParams<ChartSummary>) => {
                const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
                const open = Boolean(anchorEl);
                
                // Debug log để kiểm tra giá trị can_edit và can_share
                console.log('Row data in actions:', {
                    id: params.row.id,
                    name: params.row.name,
                    can_edit: params.row.can_edit,
                    can_share: params.row.can_share
                });
                
                const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
                    event.stopPropagation();
                    setAnchorEl(event.currentTarget);
                };
                const handleMenuClose = (_event?: React.SyntheticEvent | {}, _reason?: 'backdropClick' | 'escapeKeyDown') => {
                    setAnchorEl(null);
                };
                return (
                    <Box sx={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        justifyContent: 'center',
                        width: '100%',
                        height: '100%',
                    }}>
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
                            {params.row.can_edit && (
                                <MenuItem onClick={(e) => { e.stopPropagation(); handleEditClick(params.row); handleMenuClose(); }}>
                                    <ListItemIcon><EditIcon fontSize="small" /></ListItemIcon>
                                    <ListItemText>Edit</ListItemText>
                                </MenuItem>
                            )}
                            <MenuItem onClick={(e) => { e.stopPropagation(); handleCloneClick(params.row); handleMenuClose(); }}>
                                <ListItemIcon><ContentCopyIcon fontSize="small" /></ListItemIcon>
                                <ListItemText>Clone</ListItemText>
                            </MenuItem>
                            {params.row.can_share && (
                                <MenuItem onClick={(e) => { e.stopPropagation(); handleShareClick(params.row); handleMenuClose(); }}>
                                    <ListItemIcon><ShareIcon fontSize="small" /></ListItemIcon>
                                    <ListItemText>Share</ListItemText>
                                </MenuItem>
                            )}
                            <MenuItem onClick={(e) => { e.stopPropagation(); handleDeleteClick(params.row); handleMenuClose(); }}>
                                    <ListItemIcon><DeleteIcon fontSize="small" color="error" /></ListItemIcon>
                                    <ListItemText>Delete</ListItemText>
                                </MenuItem>
                        </Menu>
                    </Box>
                );
            },
        },
    ];

    const handleRefresh = useCallback(() => {
        fetchCharts(metadata.current_page, metadata.page_size, searchTerm);
    }, [metadata.current_page, metadata.page_size, searchTerm, fetchCharts]);


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
                    sx={{
                        height: '100% !important',
                        minHeight: '500px',
                        maxHeight: '500px',
                        '& .MuiDataGrid-cell:focus': { outline: 'none' },
                        '& .MuiDataGrid-row': {
                            borderBottom: '1px solid #e0e0e0',
                            minHeight: 'auto !important',
                            maxHeight: 'none !important',
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
                        '& .MuiDataGrid-cell': {
                            padding: '6px 16px',
                            alignItems: 'center',
                        },
                        '& .MuiDataGrid-columnHeader': {
                            padding: '6px 16px',
                        },
                    }}
                    disableColumnMenu
                    disableRowSelectionOnClick
                    onRowDoubleClick={handleRowDoubleClick}
                    paginationMode="server"
                    rowCount={metadata.total_elements}
                    pageSizeOptions={[10, 25, 50]}
                    paginationModel={{
                        page: currentPage,
                        pageSize: pageSize
                    }}
                    onPaginationModelChange={(model) => handlePageChange(model.page, model.pageSize)}
                    hideFooterSelectedRowCount
                    loading={loading}
                    getRowHeight={() => 'auto'}
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

            <ShareChartDialog
                open={shareDialogOpen}
                onClose={() => {
                    setShareDialogOpen(false);
                    setChartToShare(null);
                }}
                chartId={chartToShare?.id || ''}
                chartName={chartToShare?.name}
                onSuccess={(message) => {
                    setSuccess(message);
                    setShowSuccessPopup(true);
                }}
            />
        </Stack>
    );
} 