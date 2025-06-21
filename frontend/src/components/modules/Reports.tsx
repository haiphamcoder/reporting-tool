import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import RefreshIcon from '@mui/icons-material/Refresh';
import CustomizedDataGrid from '../CustomizedDataGrid';
import { GridColDef } from '@mui/x-data-grid';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { useState } from 'react';

export default function Reports() {
    const [reportsData, _setReportsData] = useState([
        { id: 1, name: 'Report 1', description: 'Description 1', owner: 'User 1', status: 'Active', updated_at: '2023-01-01', created_at: '2023-01-01' },
        { id: 2, name: 'Report 2', description: 'Description 2', owner: 'User 2', status: 'Inactive', updated_at: '2023-01-02', created_at: '2023-01-02' },
        { id: 3, name: 'Report 3', description: 'Description 3', owner: 'User 3', status: 'Active', updated_at: '2023-01-03', created_at: '2023-01-03' },
    ]);

    const handleEditClick = (row: any) => {
        console.log('Edit report:', row);
        // Handle edit logic here
    };

    const handleDeleteClick = (row: any) => {
        console.log('Delete report:', row);
        // Handle delete logic here
    };

    const handleRowDoubleClick = (row: any) => {
        console.log('View report details:', row);
        // Handle view details logic here
    };

    const handleAddClick = () => {
        console.log('Add new report');
        // Handle add logic here
    };

    const reportsColumns: GridColDef[] = [
        { field: 'id', headerName: 'ID', flex: 0.5, minWidth: 70 },
        { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
        { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
        { field: 'owner', headerName: 'Owner', flex: 1, minWidth: 150 },
        { field: 'status', headerName: 'Status', flex: 1, minWidth: 150 },
        { field: 'updated_at', headerName: 'Updated At', flex: 1, minWidth: 180 },
        { field: 'created_at', headerName: 'Created At', flex: 1, minWidth: 180 },
        {
            field: 'actions',
            headerName: '',
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
                Reports
            </Typography>
            <Stack direction="row" justifyContent="end" alignItems="center" gap={1}>
                <Button
                    variant="outlined"
                    startIcon={<RefreshIcon />}
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
                disableRowSelectionOnClick
                columnBufferPx={2}
                onRowDoubleClick={handleRowDoubleClick}
            />
        </Stack>
    );
} 