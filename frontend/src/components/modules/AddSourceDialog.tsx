import React, { useEffect } from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';

interface AddSourceDialogProps {
  open: boolean;
  onClose: () => void;
  addStep: number;
  setAddStep: (step: number) => void;
  addForm: any;
  setAddForm: (form: any) => void;
  currentContent: string;
  sourcesData: any[];
  chartsData: any[];
  handleAddNext: () => void;
  handleAddBack: () => void;
}

const AddSourceDialog: React.FC<AddSourceDialogProps> = ({
  open,
  onClose,
  addStep,
  setAddStep,
  addForm,
  setAddForm,
  currentContent,
  sourcesData,
  chartsData,
  handleAddNext,
  handleAddBack,
}) => {
  const handleAddFormChange = (field: string, value: any) => {
    setAddForm((prev: any) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setAddForm((prev: any) => ({
        ...prev,
        selectedFile: file,
        connectionConfig: {
          ...prev.connectionConfig,
          fileName: file.name,
        }
      }));
    }
  };

  const handleRemoveFile = () => {
    setAddForm((prev: any) => ({
      ...prev,
      selectedFile: null,
      connectionConfig: {
        ...prev.connectionConfig,
        fileName: null,
      }
    }));
  };

  const getStepContent = () => {
    if (currentContent === 'sources') {
      switch (addStep) {
        case 1:
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
        case 2:
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
        case 3:
          return (
            <Stack spacing={3} sx={{ mt: 2 }}>
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Schema Mapping
                </Typography>
                {addForm.schema ? (
                  <Box sx={{ mt: 2 }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr>
                          <th style={{ border: '1px solid #ccc', padding: 4 }}>Field Name</th>
                          <th style={{ border: '1px solid #ccc', padding: 4 }}>Field Mapping</th>
                          <th style={{ border: '1px solid #ccc', padding: 4 }}>Field Type</th>
                          <th style={{ border: '1px solid #ccc', padding: 4 }}>Is Hidden</th>
                        </tr>
                      </thead>
                      <tbody>
                        {addForm.schema.map((row: any, idx: number) => (
                          <tr key={row.field_name}>
                            <td style={{ border: '1px solid #ccc', padding: 4 }}>{row.field_name}</td>
                            <td style={{ border: '1px solid #ccc', padding: 4 }}>{row.field_mapping}</td>
                            <td style={{ border: '1px solid #ccc', padding: 4 }}>
                              <select
                                value={row.field_type}
                                onChange={e => {
                                  const newSchema = [...addForm.schema];
                                  newSchema[idx] = { ...newSchema[idx], field_type: e.target.value };
                                  setAddForm((prev: any) => ({ ...prev, schema: newSchema }));
                                }}
                              >
                                <option value="TEXT">TEXT</option>
                                <option value="BIGINT">BIGINT</option>
                                <option value="DOUBLE">DOUBLE</option>
                                {/* Thêm các type khác nếu cần */}
                              </select>
                            </td>
                            <td style={{ border: '1px solid #ccc', padding: 4, textAlign: 'center' }}>
                              <input
                                type="checkbox"
                                checked={row.is_hidden}
                                onChange={e => {
                                  const newSchema = [...addForm.schema];
                                  newSchema[idx] = { ...newSchema[idx], is_hidden: e.target.checked };
                                  setAddForm((prev: any) => ({ ...prev, schema: newSchema }));
                                }}
                              />
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </Box>
                ) : (
                  <Typography variant="body2" color="text.secondary">Loading schema...</Typography>
                )}
              </Box>
            </Stack>
          );
        case 4:
          return (
            <Stack spacing={3} sx={{ mt: 2 }}>
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Confirm Schema and Ingest Data
                </Typography>
                {/* Nút Create sẽ dùng handleAddNextInternal ở DialogActions */}
              </Box>
            </Stack>
          );
        default:
          return null;
      }
    } else if (currentContent === 'charts') {
      // ... (giữ nguyên logic cho charts và reports như trong MainGrid)
      // Để ngắn gọn, bạn có thể copy phần renderAddDialog từ MainGrid sang đây
      // ...
      // (Phần này sẽ giống như MainGrid, chỉ thay đổi các callback và state sang props)
      // ...
      // (Nếu cần, tôi sẽ bổ sung chi tiết cho charts và reports)
      return null;
    } else if (currentContent === 'reports') {
      // ... (giữ nguyên logic cho reports như trong MainGrid)
      return null;
    }
    return null;
  };

  // Điều kiện disabled cho nút Next/Create
  const isDisabled = () => {
    if (currentContent === 'sources') {
      if (addStep === 1) return !addForm.name || !addForm.connectorType;
      if (addStep === 2) {
        if ((addForm.connectorType === 'csv' || addForm.connectorType === 'excel') && !addForm.selectedFile) return true;
        if (addForm.connectorType === 'mysql' && (!addForm.connectionConfig.host || !addForm.connectionConfig.port || !addForm.connectionConfig.database || !addForm.connectionConfig.username || !addForm.connectionConfig.password)) return true;
        if (addForm.connectorType === 'gsheet' && !addForm.connectionConfig.url) return true;
      }
    }
    // ... (tương tự cho charts và reports nếu cần)
    return false;
  };

  // Mapping connectorType string to number
  const connectorTypeToNumber = (type: string) => {
    switch (type) {
      case 'csv': return 1;
      case 'excel': return 2;
      case 'mysql': return 3;
      case 'gsheet': return 4;
      default: return 0;
    }
  };

  // Custom handleAddNext để gọi API ở step 1
  const handleAddNextInternal = async () => {
    if (currentContent === 'sources' && addStep === 1) {
      try {
        const response = await fetch('http://localhost:8765/reporting/sources/init', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            name: addForm.name,
            connector_type: connectorTypeToNumber(addForm.connectorType),
            description: addForm.description || '',
          })
        });
        if (!response.ok) {
          const error = await response.json();
          throw new Error(error.message || 'Failed to init source');
        }
        const data = await response.json();
        // Nếu thành công thì lưu id vào addForm
        setAddForm((prev: any) => ({ ...prev, sourceId: data.result.id }));
        setAddStep(addStep + 1);
      } catch (err: any) {
        alert(err.message || 'Failed to init source');
      }
    } else if (currentContent === 'sources' && addStep === 2 && connectorTypeToNumber(addForm.connectorType) === 1) {
      // Step 2, CSV, upload file
      if (!addForm.selectedFile || !addForm.sourceId) {
        alert('Please select a file and ensure source is initialized.');
        return;
      }
      try {
        const formData = new FormData();
        formData.append('file', addForm.selectedFile);
        const response = await fetch(`http://localhost:8765/reporting/sources/upload-file?source-id=${addForm.sourceId}`, {
          method: 'POST',
          credentials: 'include',
          body: formData,
        });
        if (!response.ok) {
          const error = await response.json();
          throw new Error(error.message || 'Failed to upload file');
        }
        setAddStep(addStep + 1);
      } catch (err: any) {
        alert(err.message || 'Failed to upload file');
      }
    } else if (currentContent === 'sources' && addStep === 3) {
      // Step 3: Confirm schema và ingest data
      if (!addForm.sourceId || !addForm.schema) {
        alert('Missing source or schema');
        return;
      }
      try {
        // 1. Confirm schema
        const confirmRes = await fetch('http://localhost:8765/reporting/sources/confirm-schema', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            id: addForm.sourceId,
            mapping: addForm.schema,
          })
        });
        if (!confirmRes.ok) {
          const error = await confirmRes.json();
          throw new Error(error.message || 'Failed to confirm schema');
        }
        // 2. Ingest data
        const ingestRes = await fetch(`http://localhost:8765/data-processing/sources/import/${addForm.sourceId}`, {
          method: 'POST',
          credentials: 'include',
        });
        if (!ingestRes.ok) {
          const error = await ingestRes.json();
          throw new Error(error.message || 'Failed to ingest data');
        }
        alert('Source created and ingest job submitted!');
        onClose();
      } catch (err: any) {
        alert(err.message || 'Failed to create source');
      }
    } else {
      handleAddNext();
    }
  };

  // Fetch schema khi vào step 3
  useEffect(() => {
    if (currentContent === 'sources' && addStep === 3 && addForm.sourceId && !addForm.schema) {
      (async () => {
        try {
          const response = await fetch(`http://localhost:8765/data-processing/sources/schema/${addForm.sourceId}`, {
            method: 'GET',
            credentials: 'include',
          });
          if (!response.ok) {
            throw new Error('Failed to fetch schema');
          }
          const data = await response.json();
          setAddForm((prev: any) => ({ ...prev, schema: data.result }));
        } catch (err: any) {
          alert(err.message || 'Failed to fetch schema');
        }
      })();
    }
  }, [addStep, currentContent, addForm.sourceId, addForm.schema, setAddForm]);

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>
        Add {currentContent?.charAt(0).toUpperCase() + currentContent?.slice(1)}
      </DialogTitle>
      <DialogContent>
        {getStepContent()}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        {addStep > 1 && (
          <Button onClick={handleAddBack}>Back</Button>
        )}
        <Button
          onClick={handleAddNextInternal}
          variant="contained"
          disabled={isDisabled()}
        >
          {currentContent === 'sources' && addStep === 3 ? 'Create' : 'Next'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddSourceDialog; 