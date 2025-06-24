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
    Checkbox,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { chartApi } from '../../api/chart/chartApi';
import { API_CONFIG } from '../../config/api';
import { ChartSummary } from '../../types/chart';

interface AddReportDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess?: () => void;
}

const AddReportDialog: React.FC<AddReportDialogProps> = ({ open, onClose, onSuccess }) => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [charts, setCharts] = useState<ChartSummary[]>([]);
    const [selectedChartIds, setSelectedChartIds] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [fetchingCharts, setFetchingCharts] = useState(false);

    useEffect(() => {
        if (open) {
            setName('');
            setDescription('');
            setSelectedChartIds([]);
            setError(null);
            fetchCharts();
        }
    }, [open]);

    const fetchCharts = async () => {
        setFetchingCharts(true);
        try {
            const data = await chartApi.getCharts();
            if (data.success && data.result && data.result.charts) {
                setCharts(data.result.charts);
            } else {
                setCharts([]);
            }
        } catch (err) {
            setCharts([]);
        } finally {
            setFetchingCharts(false);
        }
    };

    const handleToggleChart = (chartId: string) => {
        setSelectedChartIds((prev) =>
            prev.includes(chartId)
                ? prev.filter((id) => id !== chartId)
                : [...prev, chartId]
        );
    };

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
                    chart_ids: selectedChartIds
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
                        <Typography variant="subtitle1" sx={{ mb: 1 }}>Name</Typography>
                        <TextField
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            fullWidth
                            required
                            disabled={loading}
                        />
                    </Box>
                    <Box>
                        <Typography variant="subtitle1" sx={{ mb: 1 }}>Description</Typography>
                        <TextField
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            fullWidth
                            disabled={loading}
                        />
                    </Box>
                    <Typography variant="subtitle1" sx={{ mt: 2 }}>Select Charts</Typography>
                    {fetchingCharts ? (
                        <Box display="flex" justifyContent="center" alignItems="center" minHeight={80}>
                            <CircularProgress size={24} />
                        </Box>
                    ) : (
                        <List dense sx={{ maxHeight: 200, overflow: 'auto', border: '1px solid #eee', borderRadius: 1 }}>
                            {charts.length === 0 ? (
                                <ListItem>
                                    <ListItemText primary="No charts available" />
                                </ListItem>
                            ) : (
                                charts.map((chart) => (
                                    <ListItem
                                        key={chart.id}
                                        onClick={() => handleToggleChart(chart.id)}
                                    >
                                        <ListItemIcon>
                                            <Checkbox
                                                edge="start"
                                                checked={selectedChartIds.includes(chart.id)}
                                                tabIndex={-1}
                                                disableRipple
                                                inputProps={{ 'aria-labelledby': `checkbox-list-label-${chart.id}` }}
                                            />
                                        </ListItemIcon>
                                        <ListItemText
                                            id={`checkbox-list-label-${chart.id}`}
                                            primary={chart.name}
                                            secondary={chart.description}
                                        />
                                    </ListItem>
                                ))
                            )}
                        </List>
                    )}
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