import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import RefreshIcon from '@mui/icons-material/Refresh';
import AddIcon from '@mui/icons-material/Add';
import CustomizedDataGrid from '../CustomizedDataGrid';
import { GridColDef } from '@mui/x-data-grid';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

interface SourcesProps {
    sourcesData: any[];
    handleEditClick: (row: any) => void;
    handleDeleteClick: (row: any) => void;
    handleRowDoubleClick: (row: any) => void;
    handleAddClick: () => void;
}

export default function Sources({ sourcesData, handleEditClick, handleDeleteClick, handleRowDoubleClick, handleAddClick }: SourcesProps) {

    const sourcesColumns: GridColDef[] = [
        { field: 'id', headerName: 'ID', flex: 0.5, minWidth: 70 },
        { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
        { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
        { field: 'type', headerName: 'Type', flex: 1, minWidth: 150 },
        { field: 'owner', headerName: 'Owner', flex: 1, minWidth: 150 },
        { field: 'updated_at', headerName: 'Updated At', flex: 1, minWidth: 180 },
        { field: 'created_at', headerName: 'Created At', flex: 1, minWidth: 180 },
        { field: 'status', headerName: 'Status', flex: 1, minWidth: 150 },
        {
            field: 'actions',
            headerName: 'Actions',
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

    return (
        <Stack gap={2}>
            <Typography variant="h4" component="h2" gutterBottom>
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
                disableColumnMenu
                disableRowSelectionOnClick
                columnBufferPx={2}
                onRowDoubleClick={handleRowDoubleClick}
            />
        </Stack>
    );
} 