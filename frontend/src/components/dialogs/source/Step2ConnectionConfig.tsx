import React from 'react';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

interface Step2ConnectionConfigProps {
  addForm: any;
  handleAddFormChange: (field: string, value: any) => void;
  handleFileChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleRemoveFile: () => void;
}

const Step2ConnectionConfig: React.FC<Step2ConnectionConfigProps> = ({
  addForm,
  handleAddFormChange,
  handleFileChange,
  handleRemoveFile,
}) => {
  return (
    <Stack spacing={3} sx={{ mt: 2 }}>
      {addForm.connectorType === 'csv' || addForm.connectorType === 'excel' ? (
        <Box>
          <Typography variant="subtitle2" color="text.secondary" gutterBottom>
            Upload File
          </Typography>
          {!addForm.selectedFile ? (
            <Button
              variant="outlined"
              component="label"
              fullWidth
            >
              Choose File
              <input
                type="file"
                hidden
                accept={addForm.connectorType === 'csv' ? '.csv' : '.xlsx,.xls'}
                onChange={handleFileChange}
              />
            </Button>
          ) : (
            <Box sx={{
              p: 2,
              border: '1px solid',
              borderColor: 'divider',
              borderRadius: 1,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}>
              <Stack direction="row" spacing={1} alignItems="center">
                <Typography variant="body2" noWrap sx={{ maxWidth: 300 }}>
                  {addForm.selectedFile.name}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  ({(addForm.selectedFile.size / 1024).toFixed(2)} KB)
                </Typography>
              </Stack>
              <Stack direction="row" spacing={1}>
                <Button
                  size="small"
                  onClick={handleRemoveFile}
                  color="error"
                >
                  Remove
                </Button>
                <Button
                  size="small"
                  component="label"
                  variant="outlined"
                >
                  Change
                  <input
                    type="file"
                    hidden
                    accept={addForm.connectorType === 'csv' ? '.csv' : '.xlsx,.xls'}
                    onChange={handleFileChange}
                  />
                </Button>
              </Stack>
            </Box>
          )}
        </Box>
      ) : addForm.connectorType === 'mysql' ? (
        <>
          <Box>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Host
            </Typography>
            <TextField
              value={addForm.connectionConfig.host || ''}
              onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, host: e.target.value })}
              fullWidth
              required
              placeholder="Enter host"
            />
          </Box>
          <Box>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Port
            </Typography>
            <TextField
              value={addForm.connectionConfig.port || ''}
              onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, port: e.target.value })}
              fullWidth
              required
              placeholder="Enter port"
            />
          </Box>
          <Box>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Database
            </Typography>
            <TextField
              value={addForm.connectionConfig.database || ''}
              onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, database: e.target.value })}
              fullWidth
              required
              placeholder="Enter database name"
            />
          </Box>
          <Box>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Username
            </Typography>
            <TextField
              value={addForm.connectionConfig.username || ''}
              onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, username: e.target.value })}
              fullWidth
              required
              placeholder="Enter username"
            />
          </Box>
          <Box>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Password
            </Typography>
            <TextField
              type="password"
              value={addForm.connectionConfig.password || ''}
              onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, password: e.target.value })}
              fullWidth
              required
              placeholder="Enter password"
            />
          </Box>
        </>
      ) : addForm.connectorType === 'gsheet' ? (
        <Box>
          <Typography variant="subtitle2" color="text.secondary" gutterBottom>
            Google Sheet URL
          </Typography>
          <TextField
            value={addForm.connectionConfig.url || ''}
            onChange={(e) => handleAddFormChange('connectionConfig', { ...addForm.connectionConfig, url: e.target.value })}
            fullWidth
            required
            placeholder="Enter Google Sheet URL"
          />
        </Box>
      ) : null}
    </Stack>
  );
};

export default Step2ConnectionConfig; 