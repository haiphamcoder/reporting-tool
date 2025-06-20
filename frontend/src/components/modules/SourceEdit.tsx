import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { SourceDetail } from '../../types/source';
import { useState, useEffect } from 'react';
import Grid from '@mui/material/Grid';
import ImageIcon from '@mui/icons-material/Image';
import TableChartIcon from '@mui/icons-material/TableChart';
import StorageIcon from '@mui/icons-material/Storage';
import GoogleIcon from '@mui/icons-material/Google';
import connectorCsvIcon from '../../assets/connector-csv.png';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import CircularProgress from '@mui/material/CircularProgress';
import { API_CONFIG } from '../../config/api';

interface SourceEditProps {
  sourceId: string;
  onBack: () => void;
  onSave: (source: SourceDetail) => void;
}

const getSourceTypeIcon = (type: number) => {
  switch (type) {
    case 1:
      return () => <img src={connectorCsvIcon} alt="CSV" style={{ width: 24, height: 24 }} />;
    case 2:
      return TableChartIcon;
    case 3:
      return StorageIcon;
    case 4:
      return GoogleIcon;
    default:
      return ImageIcon;
  }
};

const getSourceTypeName = (type: number) => {
  switch (type) {
    case 1:
      return 'CSV';
    case 2:
      return 'Excel';
    case 3:
      return 'MySQL';
    case 4:
      return 'Google Sheet';
    default:
      return 'Unknown';
  }
};

export default function SourceEdit({ sourceId, onBack, onSave }: SourceEditProps) {
  const [formData, setFormData] = useState<SourceDetail | null>(null);
  const [tab, setTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}/${sourceId}`, {
      credentials: 'include',
    })
      .then(async (res) => {
        if (!res.ok) throw new Error('Failed to fetch source detail');
        const data = await res.json();
        if (data && data.result) {
          console.log(data.result);
          setFormData(data.result);
        } else {
          setError('No detail data');
        }
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [sourceId]);

  const handleChange = (field: keyof SourceDetail, value: string) => {
    setFormData((prev) => {
      if (!prev) return prev;
      const next = { ...prev, [field]: value };
      if (!next.id || !next.name || next.connector_type === undefined) return prev;
      return next;
    });
  };

  const handleSave = () => {
    if (formData) {
      onSave(formData);
    }
  };

  const SourceTypeIcon = getSourceTypeIcon(formData?.connector_type || 0);

  if (loading || !formData) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
        <CircularProgress />
      </Box>
    );
  }
  if (error) {
    return (
      <Box sx={{ p: 2 }}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ px: 0, py: 2, height: 'calc(100vh - 100px)' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton onClick={onBack} sx={{ mr: 2 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h5" component="h1">
          Edit
        </Typography>
      </Box>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab label="General" />
        <Tab label="Mapping" />
      </Tabs>
      {tab === 0 && (
        <Box sx={{ bgcolor: 'background.paper', borderRadius: 1, p: 2 }}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Source ID
                </Typography>
                <TextField
                  value={formData.id}
                  fullWidth
                  disabled
                  InputProps={{
                    sx: { 
                      bgcolor: 'action.disabledBackground',
                      '& input': {
                        py: 1.5
                      }
                    }
                  }}
                />
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Source Type
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <SourceTypeIcon />
                  <TextField
                    value={getSourceTypeName(formData.connector_type || 0)}
                    fullWidth
                    disabled
                    InputProps={{
                      sx: { 
                        bgcolor: 'action.disabledBackground',
                        '& input': {
                          py: 1.5
                        }
                      }
                    }}
                  />
                </Box>
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Source Name
                </Typography>
                <TextField
                  value={formData.name}
                  onChange={(e) => handleChange('name', e.target.value)}
                  fullWidth
                  placeholder="Enter source name"
                  InputProps={{
                    sx: {
                      '& input': {
                        py: 1.5
                      }
                    }
                  }}
                />
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Description
                </Typography>
                <TextField
                  value={formData.description}
                  onChange={(e) => handleChange('description', e.target.value)}
                  rows={3}
                  fullWidth
                  placeholder="Enter source description"
                  InputProps={{
                    sx: {
                      '& textarea': {
                        display: 'flex',
                        alignItems: 'center',
                        minHeight: '72px',
                        padding: '16.5px 14px',
                      }
                    }
                  }}
                />
              </Box>
            </Grid>

            <Grid item xs={12}>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 2 }}>
                <Button onClick={onBack} variant="outlined">
                  Cancel
                </Button>
                <Button onClick={handleSave} variant="contained">
                  Save Changes
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Box>
      )}
      {tab === 1 && (
        <Box sx={{ bgcolor: 'background.paper', borderRadius: 1, p: 2 }}>
          <Typography variant="subtitle1" gutterBottom>Field Mappings</Typography>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Field Name</TableCell>
                  <TableCell>Field Mapping</TableCell>
                  <TableCell>Field Type</TableCell>
                  <TableCell>Hidden</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {Array.isArray(formData.mapping) && formData.mapping.length > 0 ? (
                  formData.mapping.map((field, idx) => (
                    <TableRow key={idx}>
                      <TableCell>{field.field_name}</TableCell>
                      <TableCell>{field.field_mapping}</TableCell>
                      <TableCell>{field.field_type}</TableCell>
                      <TableCell>{field.is_hidden ? 'Yes' : 'No'}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={4} align="center">No mapping data</TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      )}
    </Box>
  );
} 