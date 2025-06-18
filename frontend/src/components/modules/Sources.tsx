import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import RefreshIcon from '@mui/icons-material/Refresh';
import AddIcon from '@mui/icons-material/Add';
import CustomizedDataGrid from '../CustomizedDataGrid';
import { GridColDef, GridPaginationModel, GridRowParams, GridRenderCellParams } from '@mui/x-data-grid';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import connectorCsvIcon from '../../assets/connector-csv.png';
import { SourceSummary } from '../../types/source';
import Box from '@mui/material/Box';

interface SourcesMetadata {
    total_elements: number;
    number_of_elements: number;
    total_pages: number;
    current_page: number;
    page_size: number;
}

interface SourcesProps {
    sourcesData: SourceSummary[];
    metadata: SourcesMetadata;
    onPageChange: (model: GridPaginationModel) => void;
    handleEditClick: (row: SourceSummary) => void;
    handleDeleteClick: (row: SourceSummary) => void;
    handleRowDoubleClick: (params: GridRowParams<SourceSummary>) => void;
    handleAddClick: () => void;
    handleRefresh: () => void;
}

export default function Sources({
    sourcesData,
    metadata,
    onPageChange,
    handleEditClick,
    handleDeleteClick,
    handleRowDoubleClick,
    handleAddClick,
    handleRefresh
}: SourcesProps) {
    console.log('Sources component received data:', { sourcesData, metadata });

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

    return (
        <Stack gap={2}>
            <Typography variant="h4" component="h2" gutterBottom>
                Sources
            </Typography>
            <Stack direction="row" justifyContent="end" alignItems="center" gap={1}>
                <Button
                    variant="contained"
                    startIcon={<RefreshIcon />}
                    onClick={handleRefresh}
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
                disableColumnMenu
                disableRowSelectionOnClick
                columnBufferPx={2}
                onRowDoubleClick={handleRowDoubleClick}
                paginationMode="server"
                rowCount={metadata.total_elements}
                pageSizeOptions={[10, 25, 50]}
                paginationModel={{
                    page: metadata.current_page,
                    pageSize: metadata.page_size
                }}
                onPaginationModelChange={onPageChange}
            />
        </Stack>
    );
} 