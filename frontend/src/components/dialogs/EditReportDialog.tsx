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
    ListItemSecondaryAction,
    Divider,
    Stack,
    Tooltip,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import DeleteIcon from '@mui/icons-material/Delete';
import { chartApi } from '../../api/chart/chartApi';
import { API_CONFIG } from '../../config/api';
import { ChartSummary } from '../../types/chart';

interface ReportDetail {
    id: string;
    name: string;
    description: string;
    charts: ChartSummary[];
    created_at: string;
    modified_at: string;
}

interface EditReportDialogProps {
    open: boolean;
    onClose: () => void;
    report: ReportDetail | null;
    onSuccess?: () => void;
}

const EditReportDialog: React.FC<EditReportDialogProps> = ({ open, onClose, report, onSuccess }) => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [availableCharts, setAvailableCharts] = useState<ChartSummary[]>([]);
    const [selectedChartIds, setSelectedChartIds] = useState<string[]>([]);
    const [currentChartIds, setCurrentChartIds] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [fetchingCharts, setFetchingCharts] = useState(false);

    useEffect(() => {
        if (open && report) {
            setName(report.name);
            setDescription(report.description);
            setCurrentChartIds(report.charts ? report.charts.map(chart => chart.id) : []);
            setSelectedChartIds([]);
            setError(null);
            setSuccess(null);
            fetchAvailableCharts();
        }
    }, [open, report]);

    const fetchAvailableCharts = async () => {
        setFetchingCharts(true);
        try {
            const data = await chartApi.getCharts();
            if (data.success && data.result && data.result.charts) {
                setAvailableCharts(data.result.charts);
            } else {
                setAvailableCharts([]);
            }
        } catch (err) {
            setAvailableCharts([]);
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

    const handleRemoveChart = async (chartId: string) => {
        if (!report) return;
        
        setLoading(true);
        setError(null);
        try {
            // Update local state first
            const newChartIds = currentChartIds.filter(id => id !== chartId);
            setCurrentChartIds(newChartIds);
            
            // Update the report with new chart_ids
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${report.id}`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    name: name.trim(),
                    description: description.trim(),
                    chart_ids: newChartIds
                }),
            });
            
            if (!response.ok) {
                const data = await response.json();
                throw new Error(data.message || 'Failed to remove chart from report');
            }
            
            setSuccess('Chart removed successfully');
            
            // Refresh available charts
            fetchAvailableCharts();
        } catch (err: any) {
            // Revert local state on error
            setCurrentChartIds(prev => [...prev, chartId]);
            setError(err.message || 'Failed to remove chart from report');
        } finally {
            setLoading(false);
        }
    };

    const handleAddCharts = async () => {
        if (!report || selectedChartIds.length === 0) return;
        
        setLoading(true);
        setError(null);
        try {
            // Update local state first
            const newChartIds = [...currentChartIds, ...selectedChartIds];
            setCurrentChartIds(newChartIds);
            setSelectedChartIds([]);
            
            // Update the report with new chart_ids
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${report.id}`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    name: name.trim(),
                    description: description.trim(),
                    chart_ids: newChartIds
                }),
            });
            
            if (!response.ok) {
                const data = await response.json();
                throw new Error(data.message || 'Failed to add charts to report');
            }
            
            setSuccess('Charts added successfully');
            
            // Refresh available charts
            fetchAvailableCharts();
        } catch (err: any) {
            // Revert local state on error
            setCurrentChartIds(prev => prev.filter(id => !selectedChartIds.includes(id)));
            setSelectedChartIds(selectedChartIds);
            setError(err.message || 'Failed to add charts to report');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateReport = async () => {
        if (!report || !name.trim()) {
            setError('Name is required');
            return;
        }
        
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${report.id}`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    name: name.trim(),
                    description: description.trim(),
                    chart_ids: currentChartIds
                }),
            });
            
            const data = await response.json();
            if (!response.ok || !data.success) {
                throw new Error(data.message || 'Failed to update report');
            }
            
            setSuccess('Report updated successfully');
            if (onSuccess) onSuccess();
        } catch (err: any) {
            setError(err.message || 'Failed to update report');
        } finally {
            setLoading(false);
        }
    };

    const handleDialogClose = () => {
        if (!loading) {
            onClose();
        }
    };

    const getCurrentCharts = () => {
        return availableCharts.filter(chart => currentChartIds.includes(chart.id));
    };

    const getAvailableChartsForSelection = () => {
        return availableCharts.filter(chart => !currentChartIds.includes(chart.id));
    };

    if (!report) return null;

    return (
        <Dialog open={open} onClose={handleDialogClose} maxWidth="md" fullWidth>
            <DialogTitle>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Typography variant="h6">Edit Report</Typography>
                    <IconButton onClick={handleDialogClose}>
                        <CloseIcon />
                    </IconButton>
                </Box>
            </DialogTitle>
            <DialogContent>
                <Box display="flex" flexDirection="column" gap={3}>
                    {/* Report Information Section */}
                    <Box>
                        <Typography variant="h6" sx={{ mb: 2 }}>Report Information</Typography>
                        <Stack spacing={2}>
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
                                    rows={3}
                                    disabled={loading}
                                    placeholder="Enter report description"
                                />
                            </Box>
                        </Stack>
                    </Box>

                    <Divider />

                    {/* Current Charts Section */}
                    <Box>
                        <Typography variant="h6" sx={{ mb: 2 }}>
                            Current Charts ({getCurrentCharts().length})
                        </Typography>
                        {getCurrentCharts().length === 0 ? (
                            <Alert severity="info">No charts in this report.</Alert>
                        ) : (
                            <List dense sx={{ maxHeight: 200, overflow: 'auto', border: '1px solid #eee', borderRadius: 1 }}>
                                {getCurrentCharts().map((chart) => (
                                    <ListItem key={chart.id}>
                                        <ListItemText
                                            primary={chart.name}
                                            secondary={chart.description}
                                        />
                                        <ListItemSecondaryAction>
                                            <Tooltip title="Remove chart from report">
                                                <IconButton
                                                    edge="end"
                                                    color="error"
                                                    onClick={() => handleRemoveChart(chart.id)}
                                                    disabled={loading}
                                                >
                                                    <DeleteIcon />
                                                </IconButton>
                                            </Tooltip>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                ))}
                            </List>
                        )}
                    </Box>

                    <Divider />

                    {/* Add Charts Section */}
                    <Box>
                        <Typography variant="h6" sx={{ mb: 2 }}>Add Charts</Typography>
                        {fetchingCharts ? (
                            <Box display="flex" justifyContent="center" alignItems="center" minHeight={80}>
                                <CircularProgress size={24} />
                            </Box>
                        ) : (
                            <>
                                <List dense sx={{ maxHeight: 200, overflow: 'auto', border: '1px solid #eee', borderRadius: 1 }}>
                                    {getAvailableChartsForSelection().length === 0 ? (
                                        <ListItem>
                                            <ListItemText primary="No available charts to add" />
                                        </ListItem>
                                    ) : (
                                        getAvailableChartsForSelection().map((chart) => (
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
                                {selectedChartIds.length > 0 && (
                                    <Box sx={{ mt: 2 }}>
                                        <Button
                                            variant="outlined"
                                            color="primary"
                                            onClick={handleAddCharts}
                                            disabled={loading}
                                            size="small"
                                        >
                                            Add {selectedChartIds.length} Selected Chart{selectedChartIds.length > 1 ? 's' : ''}
                                        </Button>
                                    </Box>
                                )}
                            </>
                        )}
                    </Box>

                    {/* Status Messages */}
                    {error && <Alert severity="error">{error}</Alert>}
                    {success && <Alert severity="success">{success}</Alert>}
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleDialogClose} disabled={loading}>
                    Cancel
                </Button>
                <Button
                    onClick={handleUpdateReport}
                    variant="contained"
                    color="primary"
                    disabled={loading || !name.trim()}
                >
                    {loading ? <CircularProgress size={20} /> : 'Update Report'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default EditReportDialog;