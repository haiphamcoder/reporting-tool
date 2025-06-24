import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Box,
    Typography,
    CircularProgress,
    Alert,
    Grid,
    Paper,
    Stack,
    Button,
    IconButton
} from '@mui/material';
import { API_CONFIG } from '../../config/api';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement,
    LineElement,
    PointElement,
    Filler
} from 'chart.js';
import { Bar, Pie, Line, Doughnut } from 'react-chartjs-2';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import RefreshIcon from '@mui/icons-material/Refresh';
import AddIcon from '@mui/icons-material/Add';
import AddChartToReportDialog from '../dialogs/AddChartToReportDialog';

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement,
    LineElement,
    PointElement,
    Filler
);

interface ReportDetail {
    id: string;
    name: string;
    user_id: string;
    description: string;
    charts: any[];
    is_deleted: boolean;
    created_at: string;
    modified_at: string;
}

const ChartPreviewInReport: React.FC<{ chart: any }> = ({ chart }) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [previewData, setPreviewData] = useState<any>(null);

    useEffect(() => {
        const fetchPreview = async () => {
            setLoading(true);
            setError(null);
            try {
                let sql_query = '';
                let fields: any[] = [];
                if (chart.config.mode === 'basic') {
                    if (chart.config.query_option) {
                        // Convert query_option to SQL query
                        const res = await fetch(`${API_CONFIG.BASE_URL}/reporting/charts/convert-query`, {
                            method: 'POST',
                            credentials: 'include',
                            headers: {
                                'Content-Type': 'application/json',
                                'Accept': 'application/json',
                            },
                            body: JSON.stringify(chart.config.query_option),
                        });
                        const data = await res.json();
                        if (!data.success) throw new Error(data.message || 'Failed to convert query');
                        sql_query = data.result;
                        fields = chart.config.query_option.fields || [];
                    }
                } else if (chart.config.mode === 'advanced' && chart.sql_query) {
                    sql_query = chart.sql_query;
                    fields = [];
                }
                if (!sql_query) throw new Error('No SQL query available');
                // Fetch preview data
                const previewRes = await fetch(`${API_CONFIG.BASE_URL}/data-processing/charts/preview-data`, {
                    method: 'POST',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    body: JSON.stringify({ sql_query, fields }),
                });
                const preview = await previewRes.json();
                if (!preview.success) throw new Error(preview.message || 'Failed to fetch chart data');
                setPreviewData(preview.result);
            } catch (err: any) {
                setError(err.message || 'Failed to preview chart');
            } finally {
                setLoading(false);
            }
        };
        fetchPreview();
        // eslint-disable-next-line
    }, [chart.id]);

    // Helper to extract data/fields
    const getDataAndFields = () => {
        if (!previewData) return { data: [], fields: [] };
        if (previewData.columns && previewData.rows) {
            return { data: previewData.rows, fields: previewData.columns };
        } else if (previewData.schema && previewData.records) {
            return {
                data: previewData.records,
                fields: previewData.schema.filter((col: any) => !col.is_hidden).map((col: any) => col.field_name)
            };
        }
        return { data: [], fields: [] };
    };

    // Chart renderers
    const renderBarChart = () => {
        const { data, fields } = getDataAndFields();
        if (!data.length || !fields.length || !chart.config.bar_chart_config) return <Typography fontSize={14}>No data</Typography>;
        const config = chart.config.bar_chart_config;
        const xField = config.x_axis || fields[0];
        const yField = config.y_axis || fields[1] || fields[0];
        const labels = data.map((row: any) => String(row[xField] || 'N/A'));
        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map((row: any) => Number(row[yField]) || 0),
                backgroundColor: 'rgba(54, 162, 235, 0.8)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1,
                stack: config.stacked ? 'stack1' : undefined,
            }]
        };
        const options = {
            responsive: true,
            maintainAspectRatio: false,
            indexAxis: (config.orientation === 'horizontal' ? 'y' : 'x') as 'x' | 'y',
            plugins: { legend: { position: 'top' as const }, title: { display: false } },
            scales: { y: { beginAtZero: true }, x: {} }
        };
        return <Box sx={{ height: 220 }}><Bar data={chartConfig} options={options} /></Box>;
    };
    const renderPieChart = () => {
        const { data, fields } = getDataAndFields();
        if (!data.length || !fields.length || !chart.config.pie_chart_config) return <Typography fontSize={14}>No data</Typography>;
        const config = chart.config.pie_chart_config;
        const labelField = config.label_field || fields[0];
        const valueField = config.value_field || fields[1] || fields[0];
        const chartConfig = {
            labels: data.map((row: any) => String(row[labelField] || 'N/A')),
            datasets: [{
                data: data.map((row: any) => Number(row[valueField]) || 0),
                backgroundColor: [
                    'rgba(255, 99, 132, 0.8)',
                    'rgba(54, 162, 235, 0.8)',
                    'rgba(255, 206, 86, 0.8)',
                    'rgba(75, 192, 192, 0.8)',
                    'rgba(153, 102, 255, 0.8)',
                ],
                borderWidth: 1,
            }]
        };
        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'top' as const }, title: { display: false } }
        };
        const ChartComponent = config.donut ? Doughnut : Pie;
        return <Box sx={{ height: 220 }}><ChartComponent data={chartConfig} options={options} /></Box>;
    };
    const renderLineChart = () => {
        const { data, fields } = getDataAndFields();
        if (!data.length || !fields.length || !chart.config.line_chart_config) return <Typography fontSize={14}>No data</Typography>;
        const config = chart.config.line_chart_config;
        const xField = config.x_axis || fields[0];
        const yField = config.y_axis || fields[1] || fields[0];
        const labels = data.map((row: any) => String(row[xField] || 'N/A'));
        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map((row: any) => Number(row[yField]) || 0),
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: config.fill_area ? 'rgba(75, 192, 192, 0.2)' : 'transparent',
                borderWidth: 2,
                fill: config.fill_area || false,
                tension: config.smooth ? 0.4 : 0,
                pointRadius: config.show_points !== false ? 4 : 0,
            }]
        };
        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'top' as const }, title: { display: false } },
            scales: { y: { beginAtZero: true }, x: {} }
        };
        return <Box sx={{ height: 220 }}><Line data={chartConfig} options={options} /></Box>;
    };
    const renderAreaChart = () => {
        const { data, fields } = getDataAndFields();
        if (!data.length || !fields.length || !chart.config.area_chart_config) return <Typography fontSize={14}>No data</Typography>;
        const config = chart.config.area_chart_config;
        const xField = config.x_axis || fields[0];
        const yField = config.y_axis || fields[1] || fields[0];
        const labels = data.map((row: any) => String(row[xField] || 'N/A'));
        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map((row: any) => Number(row[yField]) || 0),
                borderColor: 'rgba(255, 159, 64, 1)',
                backgroundColor: `rgba(255, 159, 64, ${config.opacity || 0.7})`,
                borderWidth: 2,
                fill: true,
                tension: 0.4,
            }]
        };
        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'top' as const }, title: { display: false } },
            scales: { y: { beginAtZero: true }, x: {} }
        };
        return <Box sx={{ height: 220 }}><Line data={chartConfig} options={options} /></Box>;
    };
    const renderTable = () => {
        const { data, fields } = getDataAndFields();
        if (!data.length || !fields.length) return <Typography fontSize={14}>No data</Typography>;
        return (
            <Box sx={{ maxHeight: 220, overflow: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#f5f5f5' }}>
                            {fields.map((field: string, index: number) => (
                                <th key={index} style={{ padding: '8px', border: '1px solid #ddd', textAlign: 'left', fontWeight: 'bold' }}>{field}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((row: any, rowIndex: number) => (
                            <tr key={rowIndex} style={{ backgroundColor: rowIndex % 2 === 0 ? '#ffffff' : '#f9f9f9' }}>
                                {fields.map((field: string, colIndex: number) => (
                                    <td key={colIndex} style={{ padding: '8px', border: '1px solid #ddd' }}>{String(row[field] || '')}</td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </Box>
        );
    };
    const renderChart = () => {
        if (!previewData) return null;
        switch (chart.config.type) {
            case 'bar': return renderBarChart();
            case 'pie': return renderPieChart();
            case 'line': return renderLineChart();
            case 'area': return renderAreaChart();
            case 'table': return renderTable();
            default: return <Typography fontSize={14}>Unknown chart type</Typography>;
        }
    };
    return (
        <Box>
            {loading ? (
                <Box display="flex" justifyContent="center" alignItems="center" minHeight={120}><CircularProgress size={24} /></Box>
            ) : error ? (
                <Alert severity="error">{error}</Alert>
            ) : (
                <>
                    {renderChart()}
                    <Typography variant="subtitle1" fontWeight={600} mt={1} mb={0.5} textAlign="center">{chart.name}</Typography>
                    <Typography variant="body2" color="text.secondary" textAlign="center">{chart.description}</Typography>
                </>
            )}
        </Box>
    );
};

const ReportViewPage: React.FC = () => {
    const navigate = useNavigate();
    const { id } = useParams<{ id: string }>();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [report, setReport] = useState<ReportDetail | null>(null);
    const [addChartDialogOpen, setAddChartDialogOpen] = useState(false);

    const fetchReport = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${id}`, {
                credentials: 'include'
            });
            const data = await response.json();
            if (!response.ok || !data.success) {
                throw new Error(data.message || 'Failed to fetch report');
            }
            setReport(data.result);
        } catch (err: any) {
            setError(err.message || 'Failed to fetch report');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (id) fetchReport();
    }, [id]);

    const handleBack = () => {
        navigate('/dashboard/reports');
    };

    const handleAddChartSuccess = () => {
        fetchReport();
    };

    return (
        <Stack gap={2} sx={{ width: '100%' }}>
            {/* Header */}
            <Stack direction="row" alignItems="center" gap={2}>
                <IconButton
                    onClick={handleBack}
                    sx={{
                        border: '1px solid',
                        borderColor: 'divider',
                        '&:hover': {
                            backgroundColor: 'action.hover',
                        }
                    }}
                >
                    <ArrowBackIcon />
                </IconButton>
                <Box>
                    <Stack direction="row" alignItems="center" gap={1}>
                        <Typography variant="h4" component="h1">
                            {report?.name}
                        </Typography>
                    </Stack>
                    <Typography variant="body1" color="text.secondary">
                        {report?.description}
                    </Typography>
                </Box>
            </Stack>
            <Stack direction="row" justifyContent="end" alignItems="center" gap={1}>
                <Button
                    variant="outlined"
                    startIcon={<RefreshIcon />}
                    onClick={fetchReport}
                    disabled={loading}
                >
                    Refresh
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={() => setAddChartDialogOpen(true)}
                    disabled={loading}
                >
                    Add Chart
                </Button>
            </Stack>
            <Box sx={{ p: { xs: 1, sm: 2, md: 4 } }}>
                {loading ? (
                    <Box display="flex" justifyContent="center" alignItems="center" minHeight={200}>
                        <CircularProgress />
                    </Box>
                ) : error ? (
                    <Alert severity="error">{error}</Alert>
                ) : report ? (
                    <>
                        <Box>
                            {(!report.charts || report.charts.length === 0) ? (
                                <Alert severity="info">No charts in this report.</Alert>
                            ) : (
                                <Grid container spacing={2}>
                                    {report.charts.map((chart, idx) => {
                                        let gridProps = { xs: 12, sm: 12, md: 12, lg: 12 };
                                        if (report.charts.length === 2) gridProps = { xs: 12, sm: 6, md: 6, lg: 6 };
                                        else if (report.charts.length >= 3) gridProps = { xs: 12, sm: 6, md: 4, lg: 4 };
                                        return (
                                            <Grid item key={chart.id} {...gridProps}>
                                                <Paper sx={{ p: 2, height: '100%' }} elevation={2}>
                                                    <ChartPreviewInReport chart={chart} />
                                                </Paper>
                                            </Grid>
                                        );
                                    })}
                                </Grid>
                            )}
                        </Box>
                    </>
                ) : null}
            </Box>
            <AddChartToReportDialog
                open={addChartDialogOpen}
                onClose={() => setAddChartDialogOpen(false)}
                reportId={report?.id || ''}
                existingChartIds={report?.charts?.map((c: any) => c.id) || []}
                onSuccess={handleAddChartSuccess}
            />
        </Stack>
    );
};

export default ReportViewPage; 