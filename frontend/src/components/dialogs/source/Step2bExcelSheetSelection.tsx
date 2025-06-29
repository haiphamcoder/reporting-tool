import React, { useEffect, useState } from 'react';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import { sourceApi } from '../../../api/source';
import Button from '@mui/material/Button';

interface Step2bExcelSheetSelectionProps {
  addForm: any;
  handleAddFormChange: (field: string, value: any) => void;
}

const Step2bExcelSheetSelection: React.FC<Step2bExcelSheetSelectionProps> = ({
  addForm,
  handleAddFormChange,
}) => {
  const [sheets, setSheets] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Fetch sheets when component mounts
  useEffect(() => {
    const fetchSheets = async () => {
      if (!addForm.sourceId || !addForm.connectionConfig?.filePath) {
        setError('Missing source ID or file path. Please go back and try again.');
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const response = await sourceApi.getExcelSheets(addForm.connectionConfig.filePath);
        setSheets(response.result || []);
        
        // Auto-select first sheet if available
        if (response.result && response.result.length > 0 && !addForm.connectionConfig.sheetName) {
          handleAddFormChange('connectionConfig', {
            ...addForm.connectionConfig,
            sheetName: response.result[0]
          });
        }
      } catch (err: any) {
        setError(err.message || 'Failed to fetch Excel sheets');
        console.error('Error fetching Excel sheets:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchSheets();
  }, [addForm.sourceId, addForm.connectionConfig?.filePath]);

  const handleSheetChange = (sheetName: string) => {
    handleAddFormChange('connectionConfig', {
      ...addForm.connectionConfig,
      sheetName
    });
  };

  const handleRangeChange = (dataRange: string) => {
    // Basic validation for Excel range format
    const rangePattern = /^[A-Z]+\d+:[A-Z]+\d+$/;
    const isValidRange = rangePattern.test(dataRange.trim());
    
    handleAddFormChange('connectionConfig', {
      ...addForm.connectionConfig,
      dataRangeSelected: dataRange,
      isRangeValid: isValidRange
    });
  };

  if (loading) {
    return (
      <Stack spacing={3} sx={{ mt: 2 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
        <Typography variant="body2" color="text.secondary" textAlign="center">
          Loading Excel sheets...
        </Typography>
      </Stack>
    );
  }

  if (error) {
    return (
      <Stack spacing={3} sx={{ mt: 2 }}>
        <Alert severity="error">
          {error}
        </Alert>
      </Stack>
    );
  }

  return (
    <Stack spacing={3} sx={{ mt: 2 }}>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Select Sheet
        </Typography>
        <TextField
          select
          value={addForm.connectionConfig?.sheetName || ''}
          onChange={(e) => handleSheetChange(e.target.value)}
          fullWidth
          required
          disabled={sheets.length === 0}
        >
          {sheets.map((sheet) => (
            <MenuItem key={sheet} value={sheet}>
              {sheet}
            </MenuItem>
          ))}
        </TextField>
        {sheets.length === 0 && (
          <Typography variant="caption" color="text.secondary" sx={{ mt: 1 }}>
            No sheets found in the Excel file
          </Typography>
        )}
      </Box>

      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Data Range (e.g., A1:Z50000)
        </Typography>
        <TextField
          value={addForm.connectionConfig?.dataRangeSelected || ''}
          onChange={(e) => handleRangeChange(e.target.value)}
          fullWidth
          required
          placeholder="Enter data range (e.g., A1:Z50000)"
          helperText={
            addForm.connectionConfig?.dataRangeSelected && !addForm.connectionConfig?.isRangeValid
              ? "Invalid range format. Use Excel notation like A1:Z50000"
              : "Specify the range of cells to import. Use Excel notation like A1:Z50000"
          }
          error={addForm.connectionConfig?.dataRangeSelected && !addForm.connectionConfig?.isRangeValid}
        />
        <Stack direction="row" spacing={1} sx={{ mt: 1 }}>
          <Typography variant="caption" color="text.secondary">
            Common ranges:
          </Typography>
          {['A1:Z1000', 'A1:Z10000', 'A1:Z50000'].map((range) => (
            <Button
              key={range}
              size="small"
              variant="outlined"
              onClick={() => handleRangeChange(range)}
              sx={{ minWidth: 'auto', px: 1, py: 0.5 }}
            >
              {range}
            </Button>
          ))}
        </Stack>
      </Box>

      <Box>
        <Typography variant="body2" color="text.secondary">
          <strong>Selected File:</strong> {addForm.selectedFile?.name}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          <strong>Available Sheets:</strong> {sheets.length} sheet(s)
        </Typography>
      </Box>
    </Stack>
  );
};

export default Step2bExcelSheetSelection; 