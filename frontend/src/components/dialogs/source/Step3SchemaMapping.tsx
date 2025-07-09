import React from 'react';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import CustomizedDataGrid from '../../CustomizedDataGrid';
import { GridColDef, GridRenderEditCellParams } from '@mui/x-data-grid';

interface Step3SchemaMappingProps {
  addForm: any;
  setAddForm: (form: any) => void;
}

const Step3SchemaMapping: React.FC<Step3SchemaMappingProps> = ({
  addForm,
  setAddForm,
}) => {
  // Tạo columns cho DataGrid
  const columns: GridColDef[] = [
    {
      field: 'field_name',
      headerName: 'Field Name',
      flex: 1,
      minWidth: 120,
      editable: false,
      align: 'center',
      headerAlign: 'center',
    },
    {
      field: 'field_mapping',
      headerName: 'Field Mapping',
      flex: 1,
      minWidth: 120,
      editable: false,
      align: 'center',
      headerAlign: 'center',
    },
    {
      field: 'field_type',
      headerName: 'Field Type',
      flex: 1,
      minWidth: 120,
      editable: true,
      type: 'singleSelect',
      valueOptions: [
        { value: 'TEXT', label: 'TEXT' },
        { value: 'BIGINT', label: 'BIGINT' },
        { value: 'DOUBLE', label: 'DOUBLE' },
        { value: 'BOOLEAN', label: 'BOOLEAN' },
        { value: 'DATE', label: 'DATE' },
        { value: 'DATETIME', label: 'DATETIME' },
        { value: 'TIMESTAMP', label: 'TIMESTAMP' },
        { value: 'ARRAY', label: 'ARRAY' },
        { value: 'OBJECT', label: 'OBJECT' },
        { value: 'ENUM', label: 'ENUM' },
        // Thêm các type khác nếu cần
      ],
      align: 'center',
      headerAlign: 'center',
    },
    {
      field: 'is_hidden',
      headerName: 'Is Hidden',
      flex: 1,
      minWidth: 100,
      editable: true,
      type: 'boolean',
      renderCell: (params) => (
        <input type="checkbox" checked={!!params.value} disabled style={{ pointerEvents: 'none' }} />
      ),
      renderEditCell: (params: GridRenderEditCellParams) => (
        <input
          type="checkbox"
          checked={!!params.value}
          onChange={e => {
            params.api.setEditCellValue({ id: params.id, field: params.field, value: e.target.checked }, e);
          }}
          autoFocus
        />
      ),
      align: 'center',
      headerAlign: 'center',
    },
  ];

  // Xử lý cập nhật row
  const handleProcessRowUpdate = (newRow: any) => {
    const newSchema = addForm.schema.map((row: any) =>
      row.field_name === newRow.field_name ? { ...row, ...newRow } : row
    );
    setAddForm((prev: any) => ({ ...prev, schema: newSchema }));
    return newRow;
  };

  return (
    <Stack spacing={3} sx={{ mt: 2 }}>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Schema Mapping
        </Typography>
        {addForm.schema ? (
          <Box sx={{ mt: 2 }}>
            <CustomizedDataGrid
              rows={addForm.schema}
              columns={columns}
              getRowId={(row) => row.field_name}
              autoHeight
              hideFooter
              disableColumnMenu
              disableRowSelectionOnClick
              processRowUpdate={handleProcessRowUpdate}
              sx={{
                '& .MuiDataGrid-cell:focus': { outline: 'none' },
                '& .MuiDataGrid-row': { borderBottom: '1px solid #e0e0e0' },
                '& .MuiDataGrid-row:hover': { backgroundColor: 'rgba(25, 118, 210, 0.08)' },
                '& .MuiDataGrid-cell, & .MuiDataGrid-columnHeader': {
                  textAlign: 'center',
                  justifyContent: 'center',
                  alignItems: 'center',
                },
                '& .MuiDataGrid-cell': { padding: '6px 16px' },
                '& .MuiDataGrid-columnHeader': { padding: '6px 16px' },
              }}
            />
          </Box>
        ) : (
          <Typography variant="body2" color="text.secondary">Loading schema...</Typography>
        )}
      </Box>
    </Stack>
  );
};

export default Step3SchemaMapping; 