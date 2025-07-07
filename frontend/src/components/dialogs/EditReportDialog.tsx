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
    Stack,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
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
    can_edit?: boolean;
    can_share?: boolean;
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
    const [, setAvailableCharts] = useState<ChartSummary[]>([]);
    const [, setSelectedChartIds] = useState<string[]>([]);
    const [currentChartIds, setCurrentChartIds] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [, setFetchingCharts] = useState(false);

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
                                    disabled={loading || report.can_edit === false}
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
                                    disabled={loading || report.can_edit === false}
                                    placeholder="Enter report description"
                                />
                            </Box>
                        </Stack>
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
                {report.can_edit !== false && (
                    <Button
                        onClick={handleUpdateReport}
                        variant="contained"
                        color="primary"
                        disabled={loading || !name.trim()}
                    >
                        {loading ? <CircularProgress size={20} /> : 'Update Report'}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default EditReportDialog;