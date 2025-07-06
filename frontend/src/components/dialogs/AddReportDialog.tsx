import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Box,
    Typography,
    CircularProgress,
    Alert,
    IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { API_CONFIG } from '../../config/api';

interface AddReportDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess?: () => void;
}

const AddReportDialog: React.FC<AddReportDialogProps> = ({ open, onClose, onSuccess }) => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (open) {
            setName('');
            setDescription('');
            setError(null);
        }
    }, [open]);

    const handleCreate = async () => {
        if (!name.trim()) {
            setError('Name is required');
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    name: name.trim(),
                    description: description.trim(),
                }),
            });
            const data = await response.json();
            if (!response.ok || !data.success) {
                throw new Error(data.message || 'Failed to create report');
            }
            if (onSuccess) onSuccess();
            onClose();
        } catch (err: any) {
            setError(err.message || 'Failed to create report');
        } finally {
            setLoading(false);
        }
    };

    const handleDialogClose = () => {
        if (!loading) {
            onClose();
        }
    };

    return (
        <Dialog open={open} onClose={handleDialogClose} maxWidth="sm" fullWidth>
            <DialogTitle>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Typography variant="h6">Add New Report</Typography>
                    <IconButton onClick={handleDialogClose}>
                        <CloseIcon />
                    </IconButton>
                </Box>
            </DialogTitle>
            <DialogContent>
                <Box display="flex" flexDirection="column" gap={2}>
                    <Box>
                        <Typography variant="subtitle1" sx={{ mb: 1 }}>Name *</Typography>
                        <TextField
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            fullWidth
                            required
                            disabled={loading}
                            placeholder="Enter report name"
                        />
                    </Box>
                    <Box>
                        <Typography variant="subtitle1" sx={{ mb: 1 }}>Description</Typography>
                        <TextField
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            fullWidth
                            disabled={loading}
                            placeholder="Enter report description (optional)"
                        />
                    </Box>
                    {error && <Alert severity="error">{error}</Alert>}
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleDialogClose} disabled={loading}>Cancel</Button>
                <Button
                    onClick={handleCreate}
                    variant="contained"
                    color="primary"
                    disabled={loading || !name.trim()}
                >
                    {loading ? <CircularProgress size={20} /> : 'Create'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddReportDialog; 