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
import Avatar from '@mui/material/Avatar';
import { ReportSummary } from '../../types/report';
import { useNavigate, useLocation } from 'react-router-dom';
import AddReportDialog from '../dialogs/AddReportDialog';
import EditReportDialog from '../dialogs/EditReportDialog';
import Search from '../Search';
import { useAuth } from '../../context/AuthContext';
import { cloneReport, getReports, deleteReport, getReportDetail } from '../../api/report';

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
    const { user } = useAuth();

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
    const [searchTerm, setSearchTerm] = useState('');

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
            const newPath = `/dashboard/reports?${newSearch}`;
            navigate(newPath, { replace: true });
        }
    }, []); // Only run once on mount

    const fetchReports = useCallback(async (page: number = 0, pageSize: number = 10, search: string = '') => {
        try {
            setLoading(true);
            setError(null);
            
            const data = await getReports(page, pageSize, search);
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
        updateURLWithPagination(page, size, searchTerm);
        fetchReports(page, size, searchTerm);
    }, [searchTerm, updateURLWithPagination, fetchReports]);

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
            const data = await getReportDetail(row.id);
            setReportToEdit(data);
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

    const handleCloneClick = async (row: any) => {
        try {
            setError(null);
            setSuccess(null);

            const data = await cloneReport(row.id);

            if (data.success) {
                setSuccess('Report cloned successfully');
                setShowSuccessPopup(true);
                fetchReports(metadata.current_page, metadata.page_size, searchTerm);
            } else {
                setError(data.message || 'Failed to clone report');
                setShowErrorPopup(true);
            }
        } catch (error) {
            console.error('Error cloning report:', error);
            setError(error instanceof Error ? error.message : 'Failed to clone report');
            setShowErrorPopup(true);
        }
    };

    const handleDeleteConfirm = async () => {
        if (!reportToDelete) return;

        try {
            setError(null);
            setSuccess(null);

            const data = await deleteReport(reportToDelete.id);

            if (data.success) {
                setSuccess('Report deleted successfully');
                setShowSuccessPopup(true);
                setDeleteDialogOpen(false);
                setReportToDelete(null);
                fetchReports(metadata.current_page, metadata.page_size, searchTerm);
            } else {
                setError(data.message || 'Failed to delete report');
                setShowErrorPopup(true);
            }
        } catch (error) {
            console.error('Error deleting report:', error);
            setError(error instanceof Error ? error.message : 'Failed to delete report');
            setShowErrorPopup(true);
        }
    };

    const reportsColumns: GridColDef[] = [
        { 
            field: 'name', 
            headerName: 'Name', 
            flex: 1.2, 
            minWidth: 120,
            headerAlign: 'left',
            align: 'left',
            renderCell: (params: GridRenderCellParams<ReportSummary>) => (
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
            renderCell: (params: GridRenderCellParams<ReportSummary>) => (
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
            renderCell: (params: GridRenderCellParams<ReportSummary>) => {
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
            field: 'updated_at', 
            headerName: 'Updated At', 
            flex: 0.8, 
            minWidth: 120,
            headerAlign: 'center',
            align: 'center',
            renderCell: (params: GridRenderCellParams<ReportSummary>) => {
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
            renderCell: (params: GridRenderCellParams<ReportSummary>) => {
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
                                <MenuItem onClick={(e) => { e.stopPropagation(); /* TODO: Share logic */ handleMenuClose(); }}>
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
        fetchReports(metadata.current_page, metadata.page_size, searchTerm);
    }, [metadata.current_page, metadata.page_size, searchTerm, fetchReports]);

    return (
        <Stack gap={2}>
            <Typography variant="h4" component="h2" gutterBottom>
                Reports
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
                <Search 
                    value={searchTerm}
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