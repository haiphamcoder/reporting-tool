import React from 'react';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';

interface Step1BasicInfoProps {
  addForm: any;
  handleAddFormChange: (field: string, value: any) => void;
}

const Step1BasicInfo: React.FC<Step1BasicInfoProps> = ({
  addForm,
  handleAddFormChange,
}) => {
  return (
    <Stack spacing={3} sx={{ mt: 2 }}>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Source Name
        </Typography>
        <TextField
          value={addForm.name}
          onChange={(e) => handleAddFormChange('name', e.target.value)}
          fullWidth
          required
          placeholder="Enter source name"
        />
      </Box>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Source Description
        </Typography>
        <TextField
          value={addForm.description || ''}
          onChange={(e) => handleAddFormChange('description', e.target.value)}
          fullWidth
          placeholder="Enter source description"
        />
      </Box>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Connector Type
        </Typography>
        <TextField
          select
          value={addForm.connectorType}
          onChange={(e) => handleAddFormChange('connectorType', e.target.value)}
          fullWidth
          required
        >
          <MenuItem value="csv">CSV File</MenuItem>
          <MenuItem value="excel">Excel File</MenuItem>
          <MenuItem value="mysql">MySQL Database</MenuItem>
          <MenuItem value="gsheet">Google Sheet</MenuItem>
        </TextField>
      </Box>
    </Stack>
  );
};

export default Step1BasicInfo; 