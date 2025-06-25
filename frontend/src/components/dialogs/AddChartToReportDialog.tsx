import React, { useEffect, useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    ListItemIcon,
    Checkbox,
    CircularProgress,
    Alert,
    IconButton,
    Box,
    Typography
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { chartApi } from '../../api/chart/chartApi';
import { ChartSummary } from '../../types/chart';
import { API_CONFIG } from '../../config/api';

interface AddChartToReportDialogProps {
    open: boolean;
    onClose: () => void;
    reportId: string;
    existingChartIds: string[];
    onSuccess?: () => void;
}

const AddChartToReportDialog: React.FC<AddChartToReportDialogProps> = ({ open, onClose, reportId, existingChartIds, onSuccess }) => {
    const [charts, setCharts] = useState<ChartSummary[]>([]);
    const [selectedChartIds, setSelectedChartIds] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [fetchingCharts, setFetchingCharts] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    useEffect(() => {
        if (open) {
            setSelectedChartIds([]);
            setError(null);
            setSuccess(null);
            fetchCharts();
        }
        // eslint-disable-next-line
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
        if (existingChartIds.includes(chartId)) return;
        setSelectedChartIds((prev) =>
            prev.includes(chartId)
                ? prev.filter((id) => id !== chartId)
                : [...prev, chartId]
        );
    };

    const handleAddCharts = async () => {
        if (selectedChartIds.length === 0) {
            setError('Please select at least one chart to add.');
            return;
        }
        setLoading(true);
        setError(null);
        setSuccess(null);
        try {
            for (const chartId of selectedChartIds) {
                const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/reports/${reportId}/charts/${chartId}`, {
                    method: 'POST',
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json',
                    },
                });
                const data = await response.json();
                if (!response.ok || !data.success) {
                    throw new Error(data.message || 'Failed to add chart to report');
                }
            }
            setSuccess('Charts added successfully!');
            if (onSuccess) onSuccess();
            onClose();
        } catch (err: any) {
            setError(err.message || 'Failed to add chart(s) to report');
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
                    <Typography variant="h6">Add Chart to Report</Typography>
                    <IconButton onClick={handleDialogClose}>
                        <CloseIcon />
                    </IconButton>
                </Box>
            </DialogTitle>
            <DialogContent>
                <Box display="flex" flexDirection="column" gap={2}>
                    <Typography variant="subtitle1">Select charts to add</Typography>
                    {fetchingCharts ? (
                        <Box display="flex" justifyContent="center" alignItems="center" minHeight={80}>
                            <CircularProgress size={24} />
                        </Box>
                    ) : (
                        <List dense sx={{ maxHeight: 240, overflow: 'auto', border: '1px solid #eee', borderRadius: 1 }}>
                            {charts.length === 0 ? (
                                <ListItem>
                                    <ListItemText primary="No charts available" />
                                </ListItem>
                            ) : (
                                charts.map((chart) => {
                                    const disabled = existingChartIds.includes(chart.id);
                                    return (
                                        <ListItem key={chart.id} disablePadding>
                                            <ListItemButton
                                                onClick={() => handleToggleChart(chart.id)}
                                                disabled={disabled}
                                                sx={disabled ? { opacity: 0.5 } : {}}
                                            >
                                                <ListItemIcon>
                                                    <Checkbox
                                                        edge="start"
                                                        checked={selectedChartIds.includes(chart.id) || disabled}
                                                        tabIndex={-1}
                                                        disableRipple
                                                        inputProps={{ 'aria-labelledby': `checkbox-list-label-${chart.id}` }}
                                                        disabled={disabled}
                                                    />
                                                </ListItemIcon>
                                                <ListItemText
                                                    id={`checkbox-list-label-${chart.id}`}
                                                    primary={chart.name}
                                                    secondary={chart.description}
                                                />
                                            </ListItemButton>
                                        </ListItem>
                                    );
                                })
                            )}
                        </List>
                    )}
                    {error && <Alert severity="error">{error}</Alert>}
                    {success && <Alert severity="success">{success}</Alert>}
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleDialogClose} disabled={loading}>Cancel</Button>
                <Button
                    onClick={handleAddCharts}
                    variant="contained"
                    color="primary"
                    disabled={loading || selectedChartIds.length === 0}
                >
                    {loading ? <CircularProgress size={20} /> : 'Add'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddChartToReportDialog; 