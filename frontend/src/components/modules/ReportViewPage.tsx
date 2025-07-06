import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Box,
    Typography,
    Button,
    Stack,
    CircularProgress,
    Alert,
    IconButton,
    Paper,
    Menu,
    MenuItem,
    ListItemIcon,
    ListItemText
} from '@mui/material';
import {
    ArrowBack as ArrowBackIcon,
    Refresh as RefreshIcon,
    Add as AddIcon
} from '@mui/icons-material';
import { Bar, Pie, Line, Doughnut } from 'react-chartjs-2';
import { API_CONFIG } from '../../config/api';
import AddChartToReportDialog from '../dialogs/AddChartToReportDialog';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import Fab from '@mui/material/Fab';
import ExportPdfDialog from './ExportPdfDialog';
import { ReportBlock, ReportDetail } from '../../types/report';
import TextBlockEditor from './TextBlockEditor';
import { v4 as uuidv4 } from 'uuid';
import TextFieldsIcon from '@mui/icons-material/TextFields';
import InsertChartIcon from '@mui/icons-material/InsertChart';
import SaveIcon from '@mui/icons-material/Save';

const ChartPreviewInReport: React.FC<{ chart: any }> = ({ chart }) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [previewData, setPreviewData] = useState<any>(null);

    useEffect(() => {
        const fetchPreview = async () => {
            setLoading(true);
            setError(null);
            try {
                console.log('ChartPreviewInReport: Starting fetch for chart:', chart.id);
                console.log('Chart config:', chart.config);

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
                        fields = (chart.config.query_option.fields || []).map((f: any) => ({
                            field_name: f.alias && f.alias !== '' ? f.alias : f.field_name,
                            data_type: f.data_type,
                            alias: f.alias || ''
                        }));
                        console.log('Converted SQL query:', sql_query);
                        console.log('Fields:', fields);
                    }
                } else if (chart.config.mode === 'advanced' && chart.sql_query) {
                    sql_query = chart.sql_query;
                    fields = [];
                    console.log('Using advanced SQL query:', sql_query);
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
                console.log('Preview data received:', preview.result);
                setPreviewData(preview.result);
            } catch (err: any) {
                console.error('ChartPreviewInReport error:', err);
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

        console.log('Raw previewData:', previewData);

        let data: any[] = [];
        let fields: string[] = [];

        // Extract data based on structure - same logic as ChartViewPage
        if (previewData.columns && previewData.rows) {
            fields = previewData.columns;
            data = previewData.rows;
            console.log('Using columns/rows structure:', { fields, dataLength: data.length });
        } else if (previewData.schema && previewData.records) {
            fields = previewData.schema
                .filter((col: any) => !col.is_hidden)
                .map((col: any) => col.field_name);
            data = previewData.records;
            console.log('Using schema/records structure:', { fields, dataLength: data.length });
        }

        if (data.length === 0 || fields.length === 0) {
            console.log('No data or fields found:', { dataLength: data.length, fieldsLength: fields.length });
            return { data: [], fields: [] };
        }

        console.log('Final extracted data:', { data: data.slice(0, 3), fields });
        return { data, fields };
    };

    // Chart renderers
    const renderBarChart = () => {
        const { data, fields } = getDataAndFields();
        console.log('renderBarChart - data:', data, 'fields:', fields);
        if (!data.length || !fields.length || !chart.config.bar_chart_config) {
            console.log('Bar chart render failed:', { dataLength: data.length, fieldsLength: fields.length, barConfig: chart.config.bar_chart_config });
            return <Typography fontSize={14}>No data</Typography>;
        }
        const config = chart.config.bar_chart_config;
        const xField = config.x_axis || fields[0];
        const yField = config.y_axis || fields[1] || fields[0];
        const labels = data.map((row: any) => String(row[xField] || 'N/A'));
        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map((row: any) => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
                backgroundColor: 'rgba(54, 162, 235, 0.8)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1,
                stack: config.stacked ? 'stack1' : undefined,
            }]
        };
        console.log('Bar chart config:', chartConfig);
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
        console.log('renderPieChart - data:', data, 'fields:', fields);
        if (!data.length || !fields.length || !chart.config.pie_chart_config) {
            console.log('Pie chart render failed:', { dataLength: data.length, fieldsLength: fields.length, pieConfig: chart.config.pie_chart_config });
            return <Typography fontSize={14}>No data</Typography>;
        }
        const config = chart.config.pie_chart_config;
        const labelField = config.label_field || fields[0];
        const valueField = config.value_field || fields[1] || fields[0];
        const chartConfig = {
            labels: data.map((row: any) => String(row[labelField] || 'N/A')),
            datasets: [{
                data: data.map((row: any) => {
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
        console.log('Pie chart config:', chartConfig);
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
        console.log('renderLineChart - data:', data, 'fields:', fields);
        if (!data.length || !fields.length || !chart.config.line_chart_config) {
            console.log('Line chart render failed:', { dataLength: data.length, fieldsLength: fields.length, lineConfig: chart.config.line_chart_config });
            return <Typography fontSize={14}>No data</Typography>;
        }
        const config = chart.config.line_chart_config;
        const xField = config.x_axis || fields[0];
        const yField = config.y_axis || fields[1] || fields[0];
        const labels = data.map((row: any) => String(row[xField] || 'N/A'));
        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map((row: any) => {
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
        console.log('Line chart config:', chartConfig);
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
        console.log('renderAreaChart - data:', data, 'fields:', fields);
        if (!data.length || !fields.length || !chart.config.area_chart_config) {
            console.log('Area chart render failed:', { dataLength: data.length, fieldsLength: fields.length, areaConfig: chart.config.area_chart_config });
            return <Typography fontSize={14}>No data</Typography>;
        }
        const config = chart.config.area_chart_config;
        const xField = config.x_axis || fields[0];
        const yField = config.y_axis || fields[1] || fields[0];
        const labels = data.map((row: any) => String(row[xField] || 'N/A'));
        const chartConfig = {
            labels,
            datasets: [{
                label: config.y_axis_label || yField,
                data: data.map((row: any) => {
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
        console.log('Area chart config:', chartConfig);
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
        console.log('renderChart called with chart type:', chart.config.type);
        console.log('previewData available:', !!previewData);
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
    const [addBlockAnchorEl, setAddBlockAnchorEl] = useState<null | HTMLElement>(null);
    const [addBlockIdx, setAddBlockIdx] = useState<number | undefined>(undefined);
    const [addChartDialogOpen, setAddChartDialogOpen] = useState(false);
    const reportContentRef = React.useRef<HTMLDivElement>(null);
    const [exportDialogOpen, setExportDialogOpen] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [blocks, setBlocks] = useState<ReportBlock[]>([]);
    const [editingBlockId, setEditingBlockId] = useState<string | null>(null);
    const [editingText, setEditingText] = useState('');
    const [editingFormat, setEditingFormat] = useState<any>(null);
    const [saving, setSaving] = useState(false);

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

    useEffect(() => {
        if (report) {
            const b = getBlocks(report);
            setBlocks(b);
        }
    }, [report]);

    const handleBack = () => {
        navigate('/dashboard/reports');
    };

    const handleAddBlockClick = (event: React.MouseEvent<HTMLButtonElement>, idx?: number) => {
        setAddBlockAnchorEl(event.currentTarget);
        setAddBlockIdx(idx);
    };
    const handleAddBlockClose = () => {
        setAddBlockAnchorEl(null);
        setAddBlockIdx(undefined);
    };
    const handleAddTextBlock = () => {
        const newBlock: ReportBlock = {
            id: uuidv4(),
            type: 'text',
            content: { text: '' },
        };
        let newBlocks = [...blocks];
        if (addBlockIdx === -1) {
            newBlocks.unshift(newBlock);
        } else if (typeof addBlockIdx === 'number') {
            newBlocks.splice(addBlockIdx + 1, 0, newBlock);
        } else {
            newBlocks.push(newBlock);
        }
        setBlocks(newBlocks);
        setEditingBlockId(newBlock.id);
        setEditingText('');
        setEditingFormat(null);
        handleAddBlockClose();
    };
    const handleAddChartBlock = () => {
        setAddChartDialogOpen(true);
        handleAddBlockClose();
    };
    const handleChartSelected = (chart: any) => {
        const newBlock: ReportBlock = {
            id: uuidv4(),
            type: 'chart',
            content: { chartId: chart.id, chart },
        };
        let newBlocks = [...blocks];
        if (addBlockIdx === -1) {
            newBlocks.unshift(newBlock);
        } else if (typeof addBlockIdx === 'number') {
            newBlocks.splice(addBlockIdx + 1, 0, newBlock);
        } else {
            newBlocks.push(newBlock);
        }
        setBlocks(newBlocks);
        setAddChartDialogOpen(false);
    };

    // Sửa block text
    const handleEditTextBlock = (block: ReportBlock) => {
        if (block.type === 'text') {
            const textContent = (block.content as import('../../types/report').TextBlockContent).text || '';
            const formatContent = (block.content as import('../../types/report').TextBlockContent).format || null;
            console.log('Editing text block:', block.id, 'with content:', textContent, 'format:', formatContent);
            setEditingBlockId(block.id);
            setEditingText(textContent);
            setEditingFormat(formatContent);
        }
    };
    const handleSaveTextBlock = () => {
        setBlocks(blocks.map(b =>
            b.id === editingBlockId && b.type === 'text'
                ? { ...b, content: { ...(b.content as import('../../types/report').TextBlockContent), text: editingText, format: editingFormat } }
                : b
        ));
        setEditingBlockId(null);
        setEditingText('');
        setEditingFormat(null);
    };
    const handleCancelEditTextBlock = () => {
        setEditingBlockId(null);
        setEditingText('');
        setEditingFormat(null);
    };
    // Xóa block
    const handleDeleteBlock = (id: string) => {
        setBlocks(blocks.filter(b => b.id !== id));
    };
    // Move block
    const handleMoveBlock = (id: string, direction: 'up' | 'down') => {
        const idx = blocks.findIndex(b => b.id === id);
        if (idx < 0) return;
        let newBlocks = [...blocks];
        if (direction === 'up' && idx > 0) {
            [newBlocks[idx - 1], newBlocks[idx]] = [newBlocks[idx], newBlocks[idx - 1]];
        } else if (direction === 'down' && idx < newBlocks.length - 1) {
            [newBlocks[idx], newBlocks[idx + 1]] = [newBlocks[idx + 1], newBlocks[idx]];
        }
        setBlocks(newBlocks);
    };
    // Lưu blocks (chuẩn bị cho API PUT)
    const handleSaveBlocks = async () => {
        if (!report || !id) return;
        
        setSaving(true);
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${id}`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    name: report.name,
                    description: report.description,
                    config: {
                        blocks: blocks
                    }
                }),
            });
            
            const data = await response.json();
            if (!response.ok || !data.success) {
                throw new Error(data.message || 'Failed to save report');
            }
            
            // Cập nhật report state với data mới
            setReport(data.result);
            setEditMode(false);
            
            // Có thể thêm notification thành công ở đây
            console.log('Report saved successfully');
            
        } catch (error: any) {
            console.error('Error saving report:', error);
            // Có thể thêm notification lỗi ở đây
            alert('Failed to save report: ' + error.message);
        } finally {
            setSaving(false);
        }
    };

    // Export PDF handler (nhận options từ dialog)
    const handleExportPDF = async (options?: {
        printTitle: boolean;
        printDescription: boolean;
        selectedChartIds: string[];
        scale: number;
        layout?: 'vertical' | '2col' | '3col';
    }) => {
        if (!reportContentRef.current || !report) return;
        if (!options) {
            setExportDialogOpen(true);
            return;
        }
        setExportDialogOpen(false);
        // Tạo DOM tạm để export
        const temp = document.createElement('div');
        temp.style.padding = '32px';
        temp.style.background = 'white';
        temp.style.width = reportContentRef.current.offsetWidth + 'px';
        // Title
        if (options.printTitle) {
            const title = document.createElement('h2');
            title.innerText = report.name;
            title.style.textAlign = 'center';
            title.style.margin = '0 0 8px 0';
            title.style.fontSize = '2rem';
            title.style.fontWeight = '700';
            temp.appendChild(title);
        }
        // Description
        if (options.printDescription && report.description) {
            const desc = document.createElement('div');
            desc.innerText = report.description;
            desc.style.textAlign = 'left';
            desc.style.margin = '0 0 24px 0';
            desc.style.fontSize = '16px';
            temp.appendChild(desc);
        }
        // Charts: chụp từng chart thành ảnh rồi gắn vào DOM tạm
        const chartsToExport = blocks.filter((b: any) => b.type === 'chart' && options.selectedChartIds.includes(b.content.chartId)).map((b: any) => b.content.chart);
        const html2canvas = (await import('html2canvas')).default;
        // Tạo container cho chart theo layout
        const chartContainer = document.createElement('div');
        let columns = 1;
        if (options.layout === '2col') columns = 2;
        if (options.layout === '3col') columns = 3;
        chartContainer.style.display = columns === 1 ? 'block' : 'grid';
        if (columns > 1) {
            chartContainer.style.gridTemplateColumns = `repeat(${columns}, 1fr)`;
            chartContainer.style.gap = '24px';
        }
        chartContainer.style.marginTop = '16px';
        for (const chart of chartsToExport) {
            const chartNode = reportContentRef.current?.querySelector(`[data-chart-id="${chart.id}"]`);
            if (chartNode) {
                const canvasNode = chartNode.querySelector('canvas');
                if (canvasNode) {
                    const chartImgCanvas = await html2canvas(chartNode as HTMLElement, { scale: options.scale });
                    const img = document.createElement('img');
                    img.src = chartImgCanvas.toDataURL('image/png');
                    img.style.display = 'block';
                    img.style.margin = columns === 1 ? '0 auto 32px auto' : '0 auto';
                    img.style.maxWidth = '100%';
                    img.style.width = '100%';
                    img.style.height = 'auto';
                    chartContainer.appendChild(img);
                }
            }
        }
        temp.appendChild(chartContainer);
        temp.style.position = 'fixed';
        temp.style.left = '-99999px';
        temp.style.top = '0';
        document.body.appendChild(temp);
        // Chụp DOM tạm này
        const jsPDF = (await import('jspdf')).default;
        const canvas = await html2canvas(temp, { scale: options.scale });
        const imgData = canvas.toDataURL('image/png');
        const pdf = new jsPDF({ orientation: 'portrait', unit: 'pt', format: 'a4' });
        const pageWidth = pdf.internal.pageSize.getWidth();
        const pageHeight = pdf.internal.pageSize.getHeight();
        const maxImgWidth = Math.min(pageWidth - 40, canvas.width);
        const imgWidth = maxImgWidth;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;
        let position = 20;
        pdf.addImage(imgData, 'PNG', 20, position, imgWidth, imgHeight);
        let remainingHeight = imgHeight + position - pageHeight;
        while (remainingHeight > 0) {
            position = position - pageHeight;
            pdf.addPage();
            pdf.addImage(imgData, 'PNG', 20, position, imgWidth, imgHeight);
            remainingHeight -= pageHeight;
        }
        pdf.save(`${report?.name || 'report'}.pdf`);
        document.body.removeChild(temp);
    };

    // Helper: convert old charts to blocks if needed
    const getBlocks = (report: ReportDetail | null): ReportBlock[] => {
        if (!report) return [];
        
        // Check for new structure: config.blocks
        if (report.config && report.config.blocks && Array.isArray(report.config.blocks)) {
            return report.config.blocks;
        }
        
        // Legacy: check if config is directly an array of blocks (for backward compatibility)
        if (report.config && Array.isArray(report.config as any) && (report.config as any).length > 0) {
            return report.config as any;
        }
        
        // Legacy: convert from charts array
        if ((report as any).charts && Array.isArray((report as any).charts)) {
            return (report as any).charts.map((chart: any) => ({
                id: chart.id,
                type: 'chart',
                content: { chartId: chart.id, chart },
            }));
        }
        
        return [];
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
            <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Stack direction="row" alignItems="flex-start" gap={1}>
                    <Button variant={editMode ? 'outlined' : 'contained'} onClick={() => setEditMode(e => !e)}>
                        {editMode ? 'View Mode' : 'Edit Mode'}
                    </Button>
                    {editMode && (
                        <Button
                            variant="contained"
                            color="primary"
                            startIcon={saving ? <CircularProgress size={16} /> : <SaveIcon />}
                            onClick={handleSaveBlocks}
                            disabled={saving}>
                            {saving ? 'Saving...' : 'Save'}
                        </Button>
                    )}
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
                    >
                        Add Chart
                    </Button>
                </Stack>
            </Stack>
            <Box ref={reportContentRef} sx={{ mt: 2, p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
                {loading ? (
                    <Box display="flex" justifyContent="center" alignItems="center" minHeight={200}>
                        <CircularProgress />
                    </Box>
                ) : error ? (
                    <Alert severity="error">{error}</Alert>
                ) : report ? (
                    <>
                        <Box>
                            {blocks.length === 0 ? (
                                editMode ? (
                                    <Box sx={{ textAlign: 'center', py: 4 }}>
                                        <Typography variant="body1" color="text.secondary" mb={2}>
                                            No content in this report. Add your first block to get started.
                                        </Typography>
                                        <Stack direction="row" gap={1} justifyContent="center">
                                            <Button
                                                variant="outlined"
                                                startIcon={<TextFieldsIcon />}
                                                onClick={() => {
                                                    const newBlock: ReportBlock = {
                                                        id: uuidv4(),
                                                        type: 'text',
                                                        content: { text: '' },
                                                    };
                                                    setBlocks([newBlock]);
                                                    setEditingBlockId(newBlock.id);
                                                    setEditingText('');
                                                    setEditingFormat(null);
                                                }}
                                            >
                                                Add Text Block
                                            </Button>
                                            <Button
                                                variant="outlined"
                                                startIcon={<InsertChartIcon />}
                                                onClick={() => setAddChartDialogOpen(true)}
                                            >
                                                Add Chart Block
                                            </Button>
                                        </Stack>
                                    </Box>
                                ) : (
                                    <Alert severity="info">No content in this report.</Alert>
                                )
                            ) : (
                                <Stack gap={2}>
                                    {blocks.map((block, idx) => (
                                        <Box key={block.id}>
                                            {editMode && (
                                                <Stack direction="row" gap={1} mb={1}>
                                                    <Button size="small" onClick={e => handleAddBlockClick(e, idx - 1)}>Add block above</Button>
                                                    <Button size="small" onClick={e => handleAddBlockClick(e, idx)}>Add block below</Button>
                                                    <Button size="small" onClick={() => handleMoveBlock(block.id, 'up')} disabled={idx === 0}>Move up</Button>
                                                    <Button size="small" onClick={() => handleMoveBlock(block.id, 'down')} disabled={idx === blocks.length - 1}>Move down</Button>
                                                    <Button size="small" color="error" onClick={() => handleDeleteBlock(block.id)}>Delete</Button>
                                                    {block.type === 'text' && (
                                                        <Button size="small" onClick={() => handleEditTextBlock(block)}>Edit</Button>
                                                    )}
                                                </Stack>
                                            )}
                                            {block.type === 'chart' ? (
                                                <Paper sx={{ p: 2 }} elevation={2} data-chart-id={(block.content as import('../../types/report').ChartBlockContent).chartId}>
                                                    <ChartPreviewInReport chart={(block.content as import('../../types/report').ChartBlockContent).chart} />
                                                </Paper>
                                            ) : block.type === 'text' ? (
                                                editingBlockId === block.id && editMode ? (
                                                    <TextBlockEditor
                                                        value={editingText}
                                                        format={editingFormat}
                                                        onChange={(text, format) => {
                                                            setEditingText(text);
                                                            setEditingFormat(format);
                                                        }}
                                                        onSave={handleSaveTextBlock}
                                                        onCancel={handleCancelEditTextBlock}
                                                        autoFocus
                                                    />
                                                ) : (
                                                    <Paper sx={{ p: 2, background: '#f9f9f9' }} elevation={1}>
                                                        <Typography 
                                                            variant="body1" 
                                                            style={{ 
                                                                whiteSpace: 'pre-line',
                                                                textAlign: (block.content as any).format?.text_align || 'left',
                                                                fontSize: (block.content as any).format?.font_size ? `${(block.content as any).format.font_size}px` : undefined,
                                                                fontWeight: (block.content as any).format?.font_weight || 'normal',
                                                                fontStyle: (block.content as any).format?.font_style || 'normal',
                                                                color: (block.content as any).format?.color,
                                                                backgroundColor: (block.content as any).format?.background_color,
                                                                textDecoration: [
                                                                    (block.content as any).format?.underline ? 'underline' : '',
                                                                    (block.content as any).format?.strikethrough ? 'line-through' : ''
                                                                ].filter(Boolean).join(' ') || 'none'
                                                            }}
                                                        >
                                                            {(block.content as import('../../types/report').TextBlockContent).text}
                                                        </Typography>
                                                    </Paper>
                                                )
                                            ) : null}
                                        </Box>
                                    ))}
                                </Stack>
                            )}
                        </Box>
                    </>
                ) : null}
            </Box>
            <Menu anchorEl={addBlockAnchorEl} open={!!addBlockAnchorEl} onClose={handleAddBlockClose}>
                <MenuItem onClick={handleAddTextBlock}>
                    <ListItemIcon><TextFieldsIcon /></ListItemIcon>
                    <ListItemText>Text Block</ListItemText>
                </MenuItem>
                <MenuItem onClick={handleAddChartBlock}>
                    <ListItemIcon><InsertChartIcon /></ListItemIcon>
                    <ListItemText>Chart Block</ListItemText>
                </MenuItem>
            </Menu>
            <AddChartToReportDialog
                open={addChartDialogOpen}
                onClose={() => setAddChartDialogOpen(false)}
                reportId={report?.id || ''}
                existingChartIds={blocks.filter(b => b.type === 'chart').map((b: any) => b.content.chartId) || []}
                onSuccess={chart => handleChartSelected(chart)}
                selectMode
            />
            {/* Floating Action Button for Export PDF */}
            <Fab
                color="primary"
                aria-label="export-pdf"
                onClick={() => handleExportPDF()}
                sx={{
                    position: 'fixed',
                    bottom: { xs: 16, md: 32 },
                    right: { xs: 16, md: 32 },
                    zIndex: 1200
                }}
            >
                <PictureAsPdfIcon />
            </Fab>
            {/* Export PDF Dialog */}
            <ExportPdfDialog
                open={exportDialogOpen}
                onClose={() => setExportDialogOpen(false)}
                onExport={handleExportPDF}
                charts={getBlocks(report).filter(b => b.type === 'chart').map((b: any) => ({ id: b.content.chartId, name: b.content.chart?.name || '' })) || []}
                defaultScale={3}
            />
        </Stack>
    );
};

export default ReportViewPage; 