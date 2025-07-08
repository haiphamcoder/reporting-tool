import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Box,
    Typography,
    Button,
    Stack,
    CircularProgress,
    Alert,
    IconButton,
    Chip,
    Card,
    CardContent} from '@mui/material';
import {
    ArrowBack as ArrowBackIcon,
    Refresh as RefreshIcon,
    ShowChart as ShowChartIcon} from '@mui/icons-material';
import { Bar, Pie, Line, Doughnut } from 'react-chartjs-2';
import { API_CONFIG } from '../../config/api';
import { ChartType } from '../../types/chart';

interface ChartDetail {
    id: string;
    name: string;
    description: string;
    type: ChartType;
    config: {
        type: ChartType;
        mode: 'basic' | 'advanced';
        query_option?: any;
        sql_query?: string;
        bar_chart_config?: any;
        pie_chart_config?: any;
        line_chart_config?: any;
        area_chart_config?: any;
        table_config?: any;
    };
    sql_query?: string;
    created_at: string;
    updated_at: string;
}

interface ChartData {
    columns?: string[];
    rows?: any[];
    schema?: any[];
    records?: any[];
}

export default function ChartViewPage() {
    const navigate = useNavigate();
    const { chartId } = useParams<{ chartId: string }>();

    const [chartDetail, setChartDetail] = useState<ChartDetail | null>(null);
    const [chartData, setChartData] = useState<ChartData | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchChartDetail = async () => {
        if (!chartId) return;

        try {
            setLoading(true);
            setError(null);

            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}/${chartId}`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                setChartDetail(data.result);
                // After getting chart detail, fetch the chart data
                await fetchChartData(data.result);
            } else {
                throw new Error(data.message || 'Failed to fetch chart details');
            }
        } catch (error) {
            console.error('Error fetching chart details:', error);
            setError(error instanceof Error ? error.message : 'Failed to fetch chart details');
        } finally {
            setLoading(false);
        }
    };

    const fetchChartData = async (chart: ChartDetail) => {
        try {
            console.log('Fetching chart data for:', chart);
            let sqlQuery = '';
            let fields: any[] = [];

            // Use sql_query directly from chart detail
            if (chart.sql_query) {
                console.log('Using sql_query from chart detail:', chart.sql_query);
                sqlQuery = chart.sql_query;
                // Extract fields from query_option if available for field mapping
                if (chart.config.query_option && chart.config.query_option.fields) {
                    fields = (chart.config.query_option.fields || []).map((f: any) => ({
                        field_name: f.alias && f.alias !== '' ? f.alias : f.field_name,
                        data_type: f.data_type,
                        alias: f.alias || ''
                    }));
                }
            } else if (chart.config.sql_query) {
                console.log('Using sql_query from chart config:', chart.config.sql_query);
                sqlQuery = chart.config.sql_query;
                // Extract fields from query_option if available for field mapping
                if (chart.config.query_option && chart.config.query_option.fields) {
                    fields = (chart.config.query_option.fields || []).map((f: any) => ({
                        field_name: f.alias && f.alias !== '' ? f.alias : f.field_name,
                        data_type: f.data_type,
                        alias: f.alias || ''
                    }));
                }
            } else {
                throw new Error('No SQL query available in chart detail');
            }

            if (!sqlQuery) {
                throw new Error('No SQL query available');
            }

            console.log('SQL Query:', sqlQuery);
            console.log('Fields:', fields);

            // Fetch preview data
            const previewResponse = await fetch(`${API_CONFIG.BASE_URL}/data-processing/charts/preview-data`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({ sql_query: sqlQuery, fields }),
            });

            if (!previewResponse.ok) {
                throw new Error('Failed to fetch chart data');
            }

            const previewData = await previewResponse.json();
            console.log('Preview data response:', previewData);
            if (!previewData.success) {
                throw new Error(previewData.message || 'Failed to fetch chart data');
            }

            setChartData(previewData.result);
        } catch (error) {
            console.error('Error fetching chart data:', error);
            setError(error instanceof Error ? error.message : 'Failed to fetch chart data');
        }
    };

    useEffect(() => {
        fetchChartDetail();
    }, [chartId]);

    const handleBack = () => {
        navigate('/dashboard/charts');
    };

    const handleRefresh = () => {
        fetchChartDetail();
    };

    // Generate chart data for rendering
    const generateChartData = () => {
        if (!chartData) {
            console.log('No chartData available');
            return null;
        }

        console.log('Raw chartData:', chartData);

        let data: any[] = [];
        let availableFields: string[] = [];

        // Extract data based on structure
        if (chartData.columns && chartData.rows) {
            availableFields = chartData.columns;
            data = chartData.rows;
            console.log('Using columns/rows structure:', { availableFields, dataLength: data.length });
        } else if (chartData.schema && chartData.records) {
            availableFields = chartData.schema
                .filter((col: any) => !col.is_hidden)
                .map((col: any) => col.field_name);
            data = chartData.records;
            console.log('Using schema/records structure:', { availableFields, dataLength: data.length });
        } else if (chartData.columns && chartData.rows) {
            availableFields = chartData.columns;
            data = chartData.rows;
            console.log('Using columns/rows structure:', { availableFields, dataLength: data.length });
        }

        if (data.length === 0 || availableFields.length === 0) {
            console.log('No data or fields found:', { dataLength: data.length, fieldsLength: availableFields.length });
            return null;
        }

        console.log('Final generated data:', { data: data.slice(0, 3), availableFields });
        return { data, availableFields };
    };

    const renderBarChart = () => {
        const chartData = generateChartData();
        if (!chartData || !chartDetail?.config.bar_chart_config) {
            console.log('Bar chart render failed:', { chartData, barConfig: chartDetail?.config.bar_chart_config });
            return null;
        }

        const { data, availableFields } = chartData;
        const config = chartDetail.config.bar_chart_config;
        const yField = config.y_axis || availableFields[1] || availableFields[0];

        console.log('Bar chart data:', { data, availableFields, config, yField });

        const labels = data.map(row => String(row[config.x_axis || availableFields[0]] || 'N/A'));

        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map(row => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
                backgroundColor: 'rgba(54, 162, 235, 0.8)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1,
                stack: config.stacked ? 'stack1' : undefined,
            }]
        };

        console.log('Chart config:', chartConfig);

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            indexAxis: (config.orientation === 'horizontal' ? 'y' : 'x') as 'x' | 'y',
            plugins: {
                legend: {
                    position: 'top' as const,
                },
                title: {
                    display: true,
                    text: chartDetail.name
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: config.orientation === 'horizontal'
                            ? (config.x_axis_label || config.x_axis || availableFields[0])
                            : (config.y_axis_label || yField)
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: config.orientation === 'horizontal'
                            ? (config.y_axis_label || yField)
                            : (config.x_axis_label || config.x_axis || availableFields[0])
                    }
                }
            }
        };

        return (
            <Box sx={{ height: 500 }}>
                <Bar data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderPieChart = () => {
        const chartData = generateChartData();
        if (!chartData || !chartDetail?.config.pie_chart_config) return null;

        const { data, availableFields } = chartData;
        const config = chartDetail.config.pie_chart_config;
        const labelField = config.label_field || availableFields[0];
        const valueField = config.value_field || availableFields[1] || availableFields[0];

        const chartConfig = {
            labels: data.map(row => String(row[labelField] || 'N/A')),
            datasets: [{
                data: data.map(row => {
                    const value = row[valueField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
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
            plugins: {
                legend: {
                    position: 'top' as const,
                    display: config.show_legend !== false,
                },
                title: {
                    display: true,
                    text: chartDetail.name
                }
            }
        };

        const ChartComponent = config.donut ? Doughnut : Pie;
        return (
            <Box sx={{ height: 500 }}>
                <ChartComponent data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderLineChart = () => {
        const chartData = generateChartData();
        if (!chartData || !chartDetail?.config.line_chart_config) return null;

        const { data, availableFields } = chartData;
        const config = chartDetail.config.line_chart_config;
        const yField = config.y_axis || availableFields[1] || availableFields[0];

        const labels = data.map(row => String(row[config.x_axis || availableFields[0]] || 'N/A'));

        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map(row => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
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
            plugins: {
                legend: {
                    position: 'top' as const,
                },
                title: {
                    display: true,
                    text: chartDetail.name
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: config.y_axis_label || yField
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: config.x_axis_label || config.x_axis || availableFields[0]
                    }
                }
            }
        };

        return (
            <Box sx={{ height: 500 }}>
                <Line data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderAreaChart = () => {
        const chartData = generateChartData();
        if (!chartData || !chartDetail?.config.area_chart_config) return null;

        const { data, availableFields } = chartData;
        const config = chartDetail.config.area_chart_config;
        const yField = config.y_axis || availableFields[1] || availableFields[0];

        const labels = data.map(row => String(row[config.x_axis || availableFields[0]] || 'N/A'));

        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map(row => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
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
            plugins: {
                legend: {
                    position: 'top' as const,
                },
                title: {
                    display: true,
                    text: chartDetail.name
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: config.y_axis_label || yField
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: config.x_axis_label || config.x_axis || availableFields[0]
                    }
                }
            }
        };

        return (
            <Box sx={{ height: 500 }}>
                <Line data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderTable = () => {
        const chartData = generateChartData();
        if (!chartData || !chartDetail?.config.table_config) return null;

        const { data, availableFields } = chartData;

        return (
            <Box sx={{ maxHeight: 500, overflow: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#f5f5f5' }}>
                            {availableFields.map((field, index) => (
                                <th key={index} style={{
                                    padding: '12px',
                                    border: '1px solid #ddd',
                                    textAlign: 'left',
                                    fontWeight: 'bold'
                                }}>
                                    {field}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((row, rowIndex) => (
                            <tr key={rowIndex} style={{
                                backgroundColor: rowIndex % 2 === 0 ? '#ffffff' : '#f9f9f9'
                            }}>
                                {availableFields.map((field, colIndex) => (
                                    <td key={colIndex} style={{
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>
                                        {String(row[field] || '')}
                                    </td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </Box>
        );
    };

    const renderChart = () => {
        if (!chartDetail) return null;

        const chartType = chartDetail.config.type || chartDetail.type;

        switch (chartType) {
            case 'bar':
                return renderBarChart();
            case 'pie':
                return renderPieChart();
            case 'line':
                return renderLineChart();
            case 'area':
                return renderAreaChart();
            case 'table':
                return renderTable();
            default:
                return <Typography>Unknown chart type: {chartType}</Typography>;
        }
    };

    if (loading && !chartDetail) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error" onClose={() => setError(null)}>
                    {error}
                </Alert>
            </Box>
        );
    }

    if (!chartDetail) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="info">Chart not found</Alert>
            </Box>
        );
    }

    return (
        <Stack gap={2} sx={{ height: '100%', width: '100%' }}>
            {/* Header */}
            <Stack direction="row" justifyContent="space-between" alignItems="center">
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
                                {chartDetail.name}
                            </Typography>
                            <Chip
                                icon={<ShowChartIcon />}
                                label={(chartDetail.config.type || chartDetail.type).toUpperCase()}
                                color="primary"
                                variant="outlined"
                            />
                        </Stack>
                        <Typography variant="body1" color="text.secondary">
                            {chartDetail.description}
                        </Typography>
                    </Box>
                </Stack>
                <Stack direction="row" gap={1}>
                    <Button
                        variant="outlined"
                        startIcon={<RefreshIcon />}
                        onClick={handleRefresh}
                        disabled={loading}
                    >
                        Refresh
                    </Button>
                </Stack>
            </Stack>

            {/* Chart Display */}
            <Card>
                <CardContent>
                    {loading ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 200 }}>
                            <CircularProgress />
                        </Box>
                    ) : chartData ? (
                        <>
                            {renderChart()}
                        </>
                    ) : (
                        <Alert severity="warning">
                            No data available for this chart. Please check the chart configuration.
                        </Alert>
                    )}
                </CardContent>
            </Card>

            {/* Chart Info */}
            <Card>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Chart Information
                    </Typography>
                    <Stack gap={1}>
                        <Box sx={{ display: 'flex', gap: 2 }}>
                            <Typography variant="body2" color="text.secondary">
                                <strong>Type:</strong> {chartDetail.config.type || chartDetail.type}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                <strong>Mode:</strong> {chartDetail.config.mode}
                            </Typography>
                        </Box>
                        <Box sx={{ display: 'flex', gap: 2 }}>
                            <Typography variant="body2" color="text.secondary">
                                <strong>Created:</strong> {new Date(chartDetail.created_at).toLocaleString()}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                <strong>Updated:</strong> {new Date(chartDetail.created_at).toLocaleString()}
                            </Typography>
                        </Box>
                    </Stack>
                </CardContent>
            </Card>
        </Stack>
    );
} 