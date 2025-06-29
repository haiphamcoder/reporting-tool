import { useState, useEffect, useCallback } from 'react';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import RefreshIcon from '@mui/icons-material/Refresh';
import AddIcon from '@mui/icons-material/Add';
import CustomizedDataGrid from '../CustomizedDataGrid';
import { GridColDef, GridRowParams, GridRenderCellParams } from '@mui/x-data-grid';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import connectorCsvIcon from '../../assets/connector-csv.png';
import connectorExcelIcon from '../../assets/connector-excel.png';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import { sourceApi } from '../../api/source';
import { AddSourceDialog } from '../dialogs/source';
import DeleteConfirmationDialog from '../dialogs/DeleteConfirmationDialog';
import CardAlert from '../CardAlert';
import { useNavigate, useLocation } from 'react-router-dom';
import { SourceSummary } from '../../types/source';
import Search from '../Search';

interface SourcesMetadata {
    total_elements: number;
    number_of_elements: number;
    total_pages: number;
    current_page: number;
    page_size: number;
}

export default function Sources() {
    const navigate = useNavigate();
    const location = useLocation();

    const [sourcesData, setSourcesData] = useState<SourceSummary[]>([]);
    const [metadata, setMetadata] = useState<SourcesMetadata>({
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
    const [sourceToDelete, setSourceToDelete] = useState<SourceSummary | null>(null);

    // Pagination states
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);

    // Search state
    const [searchTerm, setSearchTerm] = useState('');

    // Add source dialog states
    const [addStep, setAddStep] = useState(1);
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
        const newPath = `/dashboard/sources${newSearch ? `?${newSearch}` : ''}`;

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
            setSuccess('Source updated successfully');
            setShowSuccessPopup(true);
            // Clear only the success parameter, keep pagination and search
            urlParams.delete('success');
            const newSearch = urlParams.toString();
            const newPath = `/dashboard/sources${newSearch ? `?${newSearch}` : ''}`;
            navigate(newPath, { replace: true });
        }

        // Update pagination and search state from URL
        setCurrentPage(page);
        setPageSize(size);
        setSearchTerm(search);
    }, [location.search, navigate]);

    // Initialize URL with default pagination values on first load (only if missing)
    useEffect(() => {
        const urlParams = new URLSearchParams(location.search);
        const hasPageParam = urlParams.has('page');
        const hasPageSizeParam = urlParams.has('pageSize');

        // Only add default pagination parameters if they're missing
        if (!hasPageParam || !hasPageSizeParam) {
            const currentPage = hasPageParam ? parseInt(urlParams.get('page') || '0', 10) : 0;
            const currentSize = hasPageSizeParam ? parseInt(urlParams.get('pageSize') || '10', 10) : 10;

            urlParams.set('page', currentPage.toString());
            urlParams.set('pageSize', currentSize.toString());

            const newSearch = urlParams.toString();
            const newPath = `/dashboard/sources?${newSearch}`;
            navigate(newPath, { replace: true });
        }
    }, []); // Only run once on mount

    const fetchSources = useCallback(async (page: number = 0, pageSize: number = 10, search: string = '') => {
        try {
            setLoading(true);
            setError(null);
            
            const data = await sourceApi.getSources(page, pageSize, search);
            
            if (data.success) {
                const processedSources = data.result.sources.map((source: any) => ({
                    ...source,
                    id: source.id.toString()
                }));
                setSourcesData(processedSources);
                setMetadata(data.result.metadata);
                setCurrentPage(page);
                setPageSize(pageSize);
            } else {
                setError('Failed to fetch sources');
                setShowErrorPopup(true);
            }
        } catch (error) {
            console.error('Error fetching sources:', error);
            setError(error instanceof Error ? error.message : 'Failed to fetch sources');
            setShowErrorPopup(true);
            setSourcesData([]);
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
        fetchSources(page, size, search);
    }, [fetchSources]);

    const handlePageChange = useCallback((page: number, size: number) => {
        updateURLWithPagination(page, size, searchTerm);
        fetchSources(page, size, searchTerm);
    }, [searchTerm, updateURLWithPagination, fetchSources]);

    const handleSearchChange = useCallback((searchTerm: string) => {
        // Reset to first page when searching
        const newPage = 0;
        updateURLWithPagination(newPage, pageSize, searchTerm);
        fetchSources(newPage, pageSize, searchTerm);
    }, [pageSize, fetchSources, updateURLWithPagination]);

    const handleRowDoubleClick = async (params: GridRowParams<SourceSummary>) => {
        if (params.row) {
            const clickedSourceId = params.row.id.toString();

            // Navigate to the preview URL
            navigate(`/dashboard/sources/${clickedSourceId}/view-data`);
        }
    };

    const handleEditClick = (row: SourceSummary) => {
        // Navigate to the edit page instead of opening dialog
        navigate(`/dashboard/sources/${row.id}/edit`);
    };

    const handleDeleteClick = (row: SourceSummary) => {
        setSourceToDelete(row);
        setDeleteDialogOpen(true);
    };

    const handleDeleteConfirm = async () => {
        if (!sourceToDelete) return;

        try {
            setError(null);
            setSuccess(null);

            await sourceApi.deleteSource(sourceToDelete.id);

            setSuccess('Source deleted successfully');
            setShowSuccessPopup(true);
            setDeleteDialogOpen(false);
            setSourceToDelete(null);
            fetchSources(metadata.current_page, metadata.page_size, searchTerm);
        } catch (error) {
            console.error('Error deleting source:', error);
            setError(error instanceof Error ? error.message : 'Failed to delete source');
            setShowErrorPopup(true);
        }
    };

    const handleAddClick = () => {
        setAddDialogOpen(true);
    };

    const handleRefresh = useCallback(() => {
        fetchSources(metadata.current_page, metadata.page_size, searchTerm);
    }, [metadata.current_page, metadata.page_size, searchTerm, fetchSources]);

    const handleAddNext = async () => {
        if (addStep === 1) {
            setAddStep(2);
        } else if (addStep === 2) {
            setAddStep(3);
        } else if (addStep === 3) {
            setAddStep(4);
        }
    };

    const handleAddBack = () => {
        if (addStep > 1) {
            setAddStep(addStep - 1);
        }
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

    const sourcesColumns: GridColDef[] = [
        { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
        { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
        {
            field: 'type',
            headerName: 'Type',
            flex: 1,
            minWidth: 150,
            renderCell: (params: GridRenderCellParams<SourceSummary>) => {
                if (params.row?.type === 1) {
                    return (
                        <Stack direction="row" spacing={1} alignItems="center" sx={{ height: '100%', width: '100%' }}>
                            <img src={connectorCsvIcon} alt="CSV" style={{ width: 24, height: 24 }} />
                            <Typography>CSV</Typography>
                        </Stack>
                    );
                } else if (params.row?.type === 2) {
                    return (
                        <Stack direction="row" spacing={1} alignItems="center" sx={{ height: '100%', width: '100%' }}>
                            <img src={connectorExcelIcon} alt="Excel" style={{ width: 24, height: 24 }} />
                            <Typography>Excel</Typography>
                        </Stack>
                    );
                }
                return params.row?.type || '';
            }
        },
        {
            field: 'status',
            headerName: 'Status',
            flex: 1,
            minWidth: 150,
            renderCell: (params: GridRenderCellParams<SourceSummary>) => (
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
            renderCell: (params: GridRenderCellParams<SourceSummary>) => {
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

    console.log('Rendering Sources DataGrid mode');

    return (
        <Stack gap={2}>
            <Typography variant="h4" component="h2" gutterBottom>
                Sources
            </Typography>
            <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
                <Search 
                    value={searchTerm}
                    onSearchChange={handleSearchChange}
                    placeholder="Search sources..."
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
                        Add Source
                    </Button>
                </Stack>
            </Stack>
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <CustomizedDataGrid
                    rows={sourcesData}
                    columns={sourcesColumns}
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

            {/* Dialogs */}
            <AddSourceDialog
                open={addDialogOpen}
                onClose={handleAddClose}
                addStep={addStep}
                setAddStep={setAddStep}
                addForm={addForm}
                setAddForm={setAddForm}
                currentContent="sources"
                sourcesData={sourcesData}
                chartsData={[]}
                handleAddNext={handleAddNext}
                handleAddBack={handleAddBack}
                onSourceCreated={fetchSources}
            />

            <DeleteConfirmationDialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={handleDeleteConfirm}
                title="Delete Source"
                message={`Are you sure you want to delete "${sourceToDelete?.name}"? This action cannot be undone.`}
                severity="error"
            />
        </Stack>
    );
} 