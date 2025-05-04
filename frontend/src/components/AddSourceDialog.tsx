import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import Grid from '@mui/material/Grid2';
import { useState } from 'react';
import { FormControl, FormLabel, Stack } from '@mui/material';
import CSVLogo from '../assets/connector-csv.png';
import ExcelLogo from '../assets/connector-excel.png';
interface Connector {
  id: number;
  name: string;
  description?: string;
}

interface AddSourceDialogProps {
  open: boolean;
  onClose: () => void;
  onNext: (data: { sourceName: string; connector: Connector; file?: File | null }) => void;
  connectors: Connector[];
  loadingConnectors: boolean;
  disableCustomTheme?: boolean;
}

const AddSourceDialog = ({ open, onClose, onNext, connectors, loadingConnectors }: AddSourceDialogProps) => {
  const [sourceName, setSourceName] = useState('');
  const [selectedConnector, setSelectedConnector] = useState<number | ''>('');
  const [selectedConnectorObj, setSelectedConnectorObj] = useState<Connector | null>(null);
  const [file, setFile] = useState<File | null>(null);

  const handleConnectorCardClick = (connector: Connector) => {
    setSelectedConnector(connector.id);
    setSelectedConnectorObj(connector);
    setFile(null);
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setFile(event.target.files[0]);
    }
  };

  const handleNext = () => {
    if (sourceName && selectedConnectorObj && (!needsFile(selectedConnectorObj) || file)) {
      onNext({ sourceName, connector: selectedConnectorObj, file });
      setSourceName('');
      setSelectedConnector('');
      setSelectedConnectorObj(null);
      setFile(null);
    }
  };

  const needsFile = (connector: Connector) =>
    connector.name === 'CSV' || connector.name === 'Excel';

  // Reset dialog state when closed
  const handleDialogClose = () => {
    setSourceName('');
    setSelectedConnector('');
    setSelectedConnectorObj(null);
    setFile(null);
    onClose();
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    handleNext();
  };

  const getConnectorLogo = (connector: Connector) => {
    if (connector.name === 'CSV') {
      return CSVLogo;
    } else if (connector.name === 'Excel') {
      return ExcelLogo;
    }
    return undefined;
  };

  return (
    <Dialog open={open} onClose={handleDialogClose} maxWidth="md" fullWidth fullScreen={false}>
      <DialogTitle>Add New Source</DialogTitle>
      <DialogContent>
        <Box
          component="form"
          onSubmit={handleSubmit}
          noValidate
          sx={{
            display: 'flex',
            flexDirection: 'column',
            width: '100%',
            gap: 2,
          }}
        >
          <FormControl fullWidth>
            <FormLabel htmlFor="sourceName">Source Name</FormLabel>
            <TextField
              id="sourceName"
              type="sourceName"
              name="sourceName"
              placeholder=""
              autoFocus
              required
              fullWidth
              variant="outlined"
              color="primary"
              value={sourceName}
              onChange={e => setSourceName(e.target.value)}
            />
          </FormControl>
          <FormControl fullWidth >
            <FormLabel htmlFor="connector">Connector Type</FormLabel>
            {loadingConnectors ? (
              <Box display="flex" justifyContent="center" alignItems="center" minHeight={100}>
                <CircularProgress />
              </Box>
            ) : (
              <RadioGroup value={selectedConnector} onChange={(_, value) => {
                const found = connectors.find((c) => c.id === Number(value));
                setSelectedConnector(value ? Number(value) : '');
                setSelectedConnectorObj(found || null);
                setFile(null);
              }}>
                <Grid container spacing={2} columns={12}>
                  {connectors.map((connector) => (
                    <Grid size={4} key={connector.id}>
                      <Card
                        variant={selectedConnector === connector.id ? 'outlined' : undefined}
                        sx={{
                          border: selectedConnector === connector.id ? '2px solid #1976d2' : undefined,
                          height: '100%',
                          display: 'flex',
                          alignItems: 'stretch',
                        }}
                      >
                        <CardActionArea onClick={() => handleConnectorCardClick(connector)} sx={{ height: '100%' }}>
                          <Box display="flex" alignItems="center" px={{ xs: 1, sm: 2 }} py={1}>
                            <FormControlLabel
                              value={connector.id}
                              control={<Radio checked={selectedConnector === connector.id} />}
                              label={
                                <Stack direction="row" spacing={2} alignItems="center">
                                  <img src={getConnectorLogo(connector)} alt={connector.name} style={{ width: 54, height: 54 }} />
                                  <Box>
                                    <Typography variant="subtitle1">{connector.name}</Typography>
                                    <Typography variant="body2" color="text.secondary">{connector.description}</Typography>
                                  </Box>
                                </Stack>
                              }
                              sx={{ m: 0 }}
                            />
                          </Box>
                        </CardActionArea>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              </RadioGroup>
            )}
          </FormControl>
          {selectedConnectorObj &&
            needsFile(selectedConnectorObj) && (
              <Box>
                <Button
                  variant="contained"
                  component="label"
                  fullWidth
                  startIcon={<CloudUploadIcon />}
                >
                  {file ? file.name : 'Upload File'}
                  <input
                    type="file"
                    hidden
                    accept={selectedConnectorObj.name === 'CSV' ? '.csv' : '.csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel'}
                    onChange={handleFileChange}
                  />
                </Button>
              </Box>
            )}
        </Box>

      </DialogContent>
      <DialogActions>
        <Button onClick={handleDialogClose}>Cancel</Button>
        <Button
          variant="contained"
          // disabled={!sourceName || !selectedConnectorObj || (selectedConnectorObj && (selectedConnectorObj.name === 'CSV' || selectedConnectorObj.name === 'Excel') && !file)}
          onClick={handleNext}
        >
          Next
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddSourceDialog; 