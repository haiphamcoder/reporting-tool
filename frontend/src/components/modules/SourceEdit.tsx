import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { Source } from '../../types/source';
import { useState } from 'react';
import Grid from '@mui/material/Grid';
import ImageIcon from '@mui/icons-material/Image';
import TableChartIcon from '@mui/icons-material/TableChart';
import StorageIcon from '@mui/icons-material/Storage';
import GoogleIcon from '@mui/icons-material/Google';
import connectorCsvIcon from '../../assets/connector-csv.png';

interface SourceEditProps {
  source: Source;
  onBack: () => void;
  onSave: (source: Source) => void;
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

export default function SourceEdit({ source, onBack, onSave }: SourceEditProps) {
  const [formData, setFormData] = useState<Source>(source);

  const handleChange = (field: keyof Source, value: string) => {
    setFormData((prev: Source) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSave = () => {
    onSave(formData);
  };

  const SourceTypeIcon = getSourceTypeIcon(source.type);

  return (
    <Box sx={{ px: 0, py: 2, height: 'calc(100vh - 100px)' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton onClick={onBack} sx={{ mr: 2 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h5" component="h1">
          {source.name} - Edit
        </Typography>
      </Box>

      <Box sx={{ bgcolor: 'background.paper', borderRadius: 1, p: 2 }}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Box>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Source ID
              </Typography>
              <TextField
                value={source.id}
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
                  value={getSourceTypeName(source.type)}
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
    </Box>
  );
} 