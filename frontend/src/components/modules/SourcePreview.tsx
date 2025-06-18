import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';

interface Schema {
  field_name: string;
  field_mapping: string;
  field_type: string;
  is_hidden: boolean;
}

interface SourcePreviewProps {
  source: {
    id: string;
    name: string;
  };
  previewData: {
    schema: Schema[];
    records: any[];
  };
  onBack: () => void;
  onPageChange: (page: number, pageSize: number) => void;
  totalRows: number;
  pageSize: number;
  currentPage: number;
  loading: boolean;
}

export default function SourcePreview({ 
  source, 
  previewData, 
  onBack,
  onPageChange,
  totalRows,
  pageSize,
  currentPage,
  loading
}: SourcePreviewProps) {
  const columns: GridColDef[] = previewData.schema.map((field) => ({
    field: field.field_mapping,
    headerName: field.field_name,
    flex: 1,
    minWidth: 150,
  }));

  return (
    <Box sx={{ p: 3, height: 'calc(100vh - 100px)' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton onClick={onBack} sx={{ mr: 2 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h5" component="h1" sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          Data Preview - 
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
            {source.id}
          </Box>
        </Typography>
      </Box>

      <Box sx={{ height: 'calc(100% - 80px)' }}>
        <DataGrid
          rows={previewData.records}
          columns={columns}
          getRowId={(row) => row.id || Math.random()}
          rowCount={totalRows}
          pageSizeOptions={[10, 25, 50, 100]}
          paginationMode="server"
          paginationModel={{
            page: currentPage,
            pageSize: pageSize,
          }}
          onPaginationModelChange={(model) => {
            onPageChange(model.page, model.pageSize);
          }}
          loading={loading}
          disableRowSelectionOnClick
          density="compact"
          initialState={{
            pagination: {
              paginationModel: {
                pageSize: pageSize,
                page: currentPage,
              },
            },
          }}
          keepNonExistentRowsSelected
          slots={{
            pagination: () => (
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', p: 1, gap: 2 }}>
                <FormControl size="small" sx={{ minWidth: 120 }}>
                  <Select
                    value={pageSize}
                    label="Rows per page" 
                    onChange={(e) => onPageChange(0, Number(e.target.value))}
                  >
                    <MenuItem value={10}>10</MenuItem>
                    <MenuItem value={25}>25</MenuItem>
                    <MenuItem value={50}>50</MenuItem>
                    <MenuItem value={100}>100</MenuItem>
                  </Select>
                </FormControl>
                <Typography variant="body2">
                  {`Page ${currentPage + 1}`}
                </Typography>
                <IconButton 
                  onClick={() => onPageChange(currentPage - 1, pageSize)}
                  disabled={currentPage === 0}
                >
                  ←
                </IconButton>
                <IconButton 
                  onClick={() => onPageChange(currentPage + 1, pageSize)}
                  disabled={previewData.records.length < pageSize}
                >
                  →
                </IconButton>
              </Box>
            ),
          }}
        />
      </Box>
    </Box>
  );
} 