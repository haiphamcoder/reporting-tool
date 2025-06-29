import React from 'react';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';

interface Step4ConfirmationProps {
  addForm: any;
}

const Step4Confirmation: React.FC<Step4ConfirmationProps> = ({
  addForm,
}) => {
  return (
    <Stack spacing={3} sx={{ mt: 2 }}>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Confirm Schema and Ingest Data
        </Typography>
        <Paper sx={{ p: 2, mt: 2 }}>
          <Stack spacing={2}>
            <Box>
              <Typography variant="body2" fontWeight="bold">Source Information:</Typography>
              <Typography variant="body2">Name: {addForm.name}</Typography>
              <Typography variant="body2">Description: {addForm.description || 'No description'}</Typography>
              <Typography variant="body2">Connector Type: {addForm.connectorType}</Typography>
            </Box>
            {addForm.schema && (
              <Box>
                <Typography variant="body2" fontWeight="bold">Schema Fields ({addForm.schema.length}):</Typography>
                <Typography variant="body2" color="text.secondary">
                  {addForm.schema.map((field: any) => field.field_name).join(', ')}
                </Typography>
              </Box>
            )}
            <Typography variant="body2" color="primary">
              Click "Create" to confirm the schema and start the data ingestion process.
            </Typography>
          </Stack>
        </Paper>
      </Box>
    </Stack>
  );
};

export default Step4Confirmation; 