import React, { useState } from 'react';
import {
    Box,
    Typography,
    Button,
    Grid,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Switch,
    FormControlLabel,
    Slider,
    Card,
    CardContent,
    Divider,
    IconButton,
    Alert,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import {
    ShowChart as ShowChartIcon} from '@mui/icons-material';
import { Bar, Pie, Line, Doughnut } from 'react-chartjs-2';
import { 
    ChartType, 
    BarChartConfig, 
    PieChartConfig, 
    LineChartConfig, 
    AreaChartConfig, 
    TableConfig} from '../../../types/chart';

interface Step3ChartConfigProps {
    chartType: ChartType;
    previewData: any;
    barChartConfig: BarChartConfig;
    pieChartConfig: PieChartConfig;
    lineChartConfig: LineChartConfig;
    areaChartConfig: AreaChartConfig;
    tableConfig: TableConfig;
    onBarChartConfigChange: (config: BarChartConfig) => void;
    onPieChartConfigChange: (config: PieChartConfig) => void;
    onLineChartConfigChange: (config: LineChartConfig) => void;
    onAreaChartConfigChange: (config: AreaChartConfig) => void;
    onTableConfigChange: (config: TableConfig) => void;
}

const Step3ChartConfig: React.FC<Step3ChartConfigProps> = ({
    chartType,
    previewData,
    barChartConfig,
    pieChartConfig,
    lineChartConfig,
    areaChartConfig,
    tableConfig,
    onBarChartConfigChange,
    onPieChartConfigChange,
    onLineChartConfigChange,
    onAreaChartConfigChange,
    onTableConfigChange
}) => {
    const [previewOpen, setPreviewOpen] = useState(false);
    
    // Hỗ trợ cả kiểu cũ (columns/rows) và kiểu mới (schema/records)
    let availableFields: string[] = [];
    
    if (previewData) {
        if (previewData.columns && previewData.rows) {
            // Kiểu cũ
            availableFields = previewData.columns;
        } else if (previewData.schema && previewData.records) {
            // Kiểu mới
            availableFields = previewData.schema
                .filter((col: any) => !col.is_hidden)
                .map((col: any) => col.field_name);
        } else if (previewData.result && previewData.result.columns) {
            // Có thể API trả về { result: { columns: [], rows: [] } }
            availableFields = previewData.result.columns;
        } else if (previewData.data && previewData.data.columns) {
            // Có thể API trả về { data: { columns: [], rows: [] } }
            availableFields = previewData.data.columns;
        } else if (previewData.records && previewData.records.length > 0) {
            // Fallback: lấy fields từ record đầu tiên
            availableFields = Object.keys(previewData.records[0]);
        } else if (previewData.rows && previewData.rows.length > 0) {
            // Fallback: lấy fields từ row đầu tiên
            availableFields = Object.keys(previewData.rows[0]);
        }
    }

    // Generate chart data for preview
    const generateChartData = () => {
        if (!previewData || availableFields.length === 0) return null;

        let data: any[] = [];
        let labels: string[] = [];

        // Extract data based on structure
        if (previewData.rows && previewData.rows.length > 0) {
            data = previewData.rows.slice(0, 10); // Limit to 10 rows for preview
        } else if (previewData.records && previewData.records.length > 0) {
            data = previewData.records.slice(0, 10); // Limit to 10 rows for preview
        }

        if (data.length === 0) return null;

        // Generate labels from first field
        const firstField = availableFields[0];
        labels = data.map(row => String(row[firstField] || 'N/A'));

        return { data, labels };
    };

    const renderBarChartPreview = () => {
        const chartData = generateChartData();
        if (!chartData) return <Typography>No data available for preview</Typography>;

        const { data, labels } = chartData;
        const yField = barChartConfig.y_axis || availableFields[1] || availableFields[0];
        
        const chartConfig = {
            labels,
            datasets: [{
                label: barChartConfig.y_axis_label || yField,
                data: data.map(row => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
                backgroundColor: 'rgba(54, 162, 235, 0.8)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1,
                stack: barChartConfig.stacked ? 'stack1' : undefined,
            }]
        };

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            indexAxis: (barChartConfig.orientation === 'horizontal' ? 'y' : 'x') as 'x' | 'y',
            plugins: {
                legend: {
                    position: 'top' as const,
                },
                title: {
                    display: true,
                    text: 'Bar Chart Preview'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: barChartConfig.orientation === 'horizontal' 
                            ? (barChartConfig.x_axis_label || barChartConfig.x_axis || availableFields[0])
                            : (barChartConfig.y_axis_label || yField)
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: barChartConfig.orientation === 'horizontal'
                            ? (barChartConfig.y_axis_label || yField)
                            : (barChartConfig.x_axis_label || barChartConfig.x_axis || availableFields[0])
                    }
                }
            }
        };

        return (
            <Box sx={{ height: 400 }}>
                <Bar data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderPieChartPreview = () => {
        const chartData = generateChartData();
        if (!chartData) return <Typography>No data available for preview</Typography>;

        const { data } = chartData;
        const labelField = pieChartConfig.label_field || availableFields[0];
        const valueField = pieChartConfig.value_field || availableFields[1] || availableFields[0];
        
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
                    display: pieChartConfig.show_legend !== false,
                },
                title: {
                    display: true,
                    text: 'Pie Chart Preview'
                }
            }
        };

        const ChartComponent = pieChartConfig.donut ? Doughnut : Pie;
        return (
            <Box sx={{ height: 400 }}>
                <ChartComponent data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderLineChartPreview = () => {
        const chartData = generateChartData();
        if (!chartData) return <Typography>No data available for preview</Typography>;

        const { data, labels } = chartData;
        const yField = lineChartConfig.y_axis || availableFields[1] || availableFields[0];
        
        const chartConfig = {
            labels,
            datasets: [{
                label: lineChartConfig.y_axis_label || yField,
                data: data.map(row => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: lineChartConfig.fill_area ? 'rgba(75, 192, 192, 0.2)' : 'transparent',
                borderWidth: 2,
                fill: lineChartConfig.fill_area || false,
                tension: lineChartConfig.smooth ? 0.4 : 0,
                pointRadius: lineChartConfig.show_points !== false ? 4 : 0,
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
                    text: 'Line Chart Preview'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: lineChartConfig.y_axis_label || yField
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: lineChartConfig.x_axis_label || lineChartConfig.x_axis || availableFields[0]
                    }
                }
            }
        };

        return (
            <Box sx={{ height: 400 }}>
                <Line data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderAreaChartPreview = () => {
        const chartData = generateChartData();
        if (!chartData) return <Typography>No data available for preview</Typography>;

        const { data, labels } = chartData;
        const yField = areaChartConfig.y_axis || availableFields[1] || availableFields[0];
        
        const chartConfig = {
            labels,
            datasets: [{
                label: areaChartConfig.y_axis_label || yField,
                data: data.map(row => {
                    const value = row[yField];
                    return typeof value === 'number' ? value : parseFloat(value) || 0;
                }),
                borderColor: 'rgba(255, 159, 64, 1)',
                backgroundColor: `rgba(255, 159, 64, ${areaChartConfig.opacity || 0.7})`,
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
                    text: 'Area Chart Preview'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: areaChartConfig.y_axis_label || yField
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: areaChartConfig.x_axis_label || areaChartConfig.x_axis || availableFields[0]
                    }
                }
            }
        };

        return (
            <Box sx={{ height: 400 }}>
                <Line data={chartConfig} options={options} />
            </Box>
        );
    };

    const renderTablePreview = () => {
        if (!previewData) return <Typography>No data available for preview</Typography>;

        let data: any[] = [];
        if (previewData.rows && previewData.rows.length > 0) {
            data = previewData.rows.slice(0, 5); // Limit to 5 rows for preview
        } else if (previewData.records && previewData.records.length > 0) {
            data = previewData.records.slice(0, 5); // Limit to 5 rows for preview
        }

        if (data.length === 0) return <Typography>No data available for preview</Typography>;

        return (
            <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#f5f5f5' }}>
                            {availableFields.map((field, index) => (
                                <th key={index} style={{ 
                                    padding: '8px', 
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
                                        padding: '8px', 
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

    const renderChartPreview = () => {
        switch (chartType) {
            case 'bar':
                return renderBarChartPreview();
            case 'pie':
                return renderPieChartPreview();
            case 'line':
                return renderLineChartPreview();
            case 'area':
                return renderAreaChartPreview();
            case 'table':
                return renderTablePreview();
            default:
                return <Typography>Unknown chart type</Typography>;
        }
    };

    const renderBarChartConfig = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                Bar Chart Configuration
            </Typography>
            
            <Grid container spacing={3}>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>X-Axis Field</InputLabel>
                        <Select
                            value={barChartConfig.x_axis}
                            onChange={(e) => onBarChartConfigChange({
                                ...barChartConfig,
                                x_axis: e.target.value
                            })}
                            label="X-Axis Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>Y-Axis Field</InputLabel>
                        <Select
                            value={barChartConfig.y_axis}
                            onChange={(e) => onBarChartConfigChange({
                                ...barChartConfig,
                                y_axis: e.target.value
                            })}
                            label="Y-Axis Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        label="X-Axis Label"
                        value={barChartConfig.x_axis_label || ''}
                        onChange={(e) => onBarChartConfigChange({
                            ...barChartConfig,
                            x_axis_label: e.target.value
                        })}
                    />
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        label="Y-Axis Label"
                        value={barChartConfig.y_axis_label || ''}
                        onChange={(e) => onBarChartConfigChange({
                            ...barChartConfig,
                            y_axis_label: e.target.value
                        })}
                    />
                </Grid>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>Orientation</InputLabel>
                        <Select
                            value={barChartConfig.orientation || 'vertical'}
                            onChange={(e) => onBarChartConfigChange({
                                ...barChartConfig,
                                orientation: e.target.value as 'vertical' | 'horizontal'
                            })}
                            label="Orientation"
                        >
                            <MenuItem value="vertical">Vertical</MenuItem>
                            <MenuItem value="horizontal">Horizontal</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={barChartConfig.stacked || false}
                                onChange={(e) => onBarChartConfigChange({
                                    ...barChartConfig,
                                    stacked: e.target.checked
                                })}
                            />
                        }
                        label="Stacked Bars"
                    />
                </Grid>
            </Grid>
        </Box>
    );

    const renderPieChartConfig = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                Pie Chart Configuration
            </Typography>
            
            <Grid container spacing={3}>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>Label Field</InputLabel>
                        <Select
                            value={pieChartConfig.label_field}
                            onChange={(e) => onPieChartConfigChange({
                                ...pieChartConfig,
                                label_field: e.target.value
                            })}
                            label="Label Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>Value Field</InputLabel>
                        <Select
                            value={pieChartConfig.value_field}
                            onChange={(e) => onPieChartConfigChange({
                                ...pieChartConfig,
                                value_field: e.target.value
                            })}
                            label="Value Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={4}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={pieChartConfig.show_percentage || false}
                                onChange={(e) => onPieChartConfigChange({
                                    ...pieChartConfig,
                                    show_percentage: e.target.checked
                                })}
                            />
                        }
                        label="Show Percentage"
                    />
                </Grid>
                <Grid item xs={4}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={pieChartConfig.show_legend || false}
                                onChange={(e) => onPieChartConfigChange({
                                    ...pieChartConfig,
                                    show_legend: e.target.checked
                                })}
                            />
                        }
                        label="Show Legend"
                    />
                </Grid>
                <Grid item xs={4}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={pieChartConfig.donut || false}
                                onChange={(e) => onPieChartConfigChange({
                                    ...pieChartConfig,
                                    donut: e.target.checked
                                })}
                            />
                        }
                        label="Donut Chart"
                    />
                </Grid>
            </Grid>
        </Box>
    );

    const renderLineChartConfig = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                Line Chart Configuration
            </Typography>
            
            <Grid container spacing={3}>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>X-Axis Field</InputLabel>
                        <Select
                            value={lineChartConfig.x_axis}
                            onChange={(e) => onLineChartConfigChange({
                                ...lineChartConfig,
                                x_axis: e.target.value
                            })}
                            label="X-Axis Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>Y-Axis Field</InputLabel>
                        <Select
                            value={lineChartConfig.y_axis}
                            onChange={(e) => onLineChartConfigChange({
                                ...lineChartConfig,
                                y_axis: e.target.value
                            })}
                            label="Y-Axis Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        label="X-Axis Label"
                        value={lineChartConfig.x_axis_label || ''}
                        onChange={(e) => onLineChartConfigChange({
                            ...lineChartConfig,
                            x_axis_label: e.target.value
                        })}
                    />
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        label="Y-Axis Label"
                        value={lineChartConfig.y_axis_label || ''}
                        onChange={(e) => onLineChartConfigChange({
                            ...lineChartConfig,
                            y_axis_label: e.target.value
                        })}
                    />
                </Grid>
                <Grid item xs={4}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={lineChartConfig.smooth || false}
                                onChange={(e) => onLineChartConfigChange({
                                    ...lineChartConfig,
                                    smooth: e.target.checked
                                })}
                            />
                        }
                        label="Smooth Line"
                    />
                </Grid>
                <Grid item xs={4}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={lineChartConfig.show_points || false}
                                onChange={(e) => onLineChartConfigChange({
                                    ...lineChartConfig,
                                    show_points: e.target.checked
                                })}
                            />
                        }
                        label="Show Points"
                    />
                </Grid>
                <Grid item xs={4}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={lineChartConfig.fill_area || false}
                                onChange={(e) => onLineChartConfigChange({
                                    ...lineChartConfig,
                                    fill_area: e.target.checked
                                })}
                            />
                        }
                        label="Fill Area"
                    />
                </Grid>
            </Grid>
        </Box>
    );

    const renderAreaChartConfig = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                Area Chart Configuration
            </Typography>
            
            <Grid container spacing={3}>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>X-Axis Field</InputLabel>
                        <Select
                            value={areaChartConfig.x_axis}
                            onChange={(e) => onAreaChartConfigChange({
                                ...areaChartConfig,
                                x_axis: e.target.value
                            })}
                            label="X-Axis Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <FormControl fullWidth>
                        <InputLabel>Y-Axis Field</InputLabel>
                        <Select
                            value={areaChartConfig.y_axis}
                            onChange={(e) => onAreaChartConfigChange({
                                ...areaChartConfig,
                                y_axis: e.target.value
                            })}
                            label="Y-Axis Field"
                        >
                            {availableFields.map((field: string) => (
                                <MenuItem key={field} value={field}>{field}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        label="X-Axis Label"
                        value={areaChartConfig.x_axis_label || ''}
                        onChange={(e) => onAreaChartConfigChange({
                            ...areaChartConfig,
                            x_axis_label: e.target.value
                        })}
                    />
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        label="Y-Axis Label"
                        value={areaChartConfig.y_axis_label || ''}
                        onChange={(e) => onAreaChartConfigChange({
                            ...areaChartConfig,
                            y_axis_label: e.target.value
                        })}
                    />
                </Grid>
                <Grid item xs={6}>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={areaChartConfig.stacked || false}
                                onChange={(e) => onAreaChartConfigChange({
                                    ...areaChartConfig,
                                    stacked: e.target.checked
                                })}
                            />
                        }
                        label="Stacked Areas"
                    />
                </Grid>
                <Grid item xs={6}>
                    <Box>
                        <Typography gutterBottom>Opacity</Typography>
                        <Slider
                            value={areaChartConfig.opacity || 0.7}
                            onChange={(_, value) => onAreaChartConfigChange({
                                ...areaChartConfig,
                                opacity: value as number
                            })}
                            min={0.1}
                            max={1}
                            step={0.1}
                            marks
                            valueLabelDisplay="auto"
                        />
                    </Box>
                </Grid>
            </Grid>
        </Box>
    );

    const renderTableConfig = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                Table Configuration
            </Typography>
            
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Typography variant="subtitle1" gutterBottom>
                        Column Configuration
                    </Typography>
                    {availableFields.map((field: string) => {
                        const column = tableConfig.columns?.find(col => col.field === field) || {
                            field,
                            header: field,
                            sortable: true,
                            width: 'auto',
                            align: 'left' as const
                        };
                        
                        return (
                            <Card key={field} sx={{ mb: 2 }}>
                                <CardContent>
                                    <Grid container spacing={2} alignItems="center">
                                        <Grid item xs={3}>
                                            <Typography variant="subtitle2" color="text.secondary">
                                                {field}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={3}>
                                            <TextField
                                                fullWidth
                                                size="small"
                                                label="Header"
                                                value={column.header}
                                                onChange={(e) => {
                                                    const updatedColumns = [...(tableConfig.columns || [])];
                                                    const existingIndex = updatedColumns.findIndex(col => col.field === field);
                                                    if (existingIndex >= 0) {
                                                        updatedColumns[existingIndex] = { ...column, header: e.target.value };
                                                    } else {
                                                        updatedColumns.push({ ...column, header: e.target.value });
                                                    }
                                                    onTableConfigChange({
                                                        ...tableConfig,
                                                        columns: updatedColumns
                                                    });
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={2}>
                                            <FormControl fullWidth size="small">
                                                <InputLabel>Align</InputLabel>
                                                <Select
                                                    value={column.align}
                                                    onChange={(e) => {
                                                        const updatedColumns = [...(tableConfig.columns || [])];
                                                        const existingIndex = updatedColumns.findIndex(col => col.field === field);
                                                        if (existingIndex >= 0) {
                                                            updatedColumns[existingIndex] = { ...column, align: e.target.value as any };
                                                        } else {
                                                            updatedColumns.push({ ...column, align: e.target.value as any });
                                                        }
                                                        onTableConfigChange({
                                                            ...tableConfig,
                                                            columns: updatedColumns
                                                        });
                                                    }}
                                                    label="Align"
                                                >
                                                    <MenuItem value="left">Left</MenuItem>
                                                    <MenuItem value="center">Center</MenuItem>
                                                    <MenuItem value="right">Right</MenuItem>
                                                </Select>
                                            </FormControl>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <FormControlLabel
                                                control={
                                                    <Switch
                                                        size="small"
                                                        checked={column.sortable || false}
                                                        onChange={(e) => {
                                                            const updatedColumns = [...(tableConfig.columns || [])];
                                                            const existingIndex = updatedColumns.findIndex(col => col.field === field);
                                                            if (existingIndex >= 0) {
                                                                updatedColumns[existingIndex] = { ...column, sortable: e.target.checked };
                                                            } else {
                                                                updatedColumns.push({ ...column, sortable: e.target.checked });
                                                            }
                                                            onTableConfigChange({
                                                                ...tableConfig,
                                                                columns: updatedColumns
                                                            });
                                                        }}
                                                    />
                                                }
                                                label="Sortable"
                                            />
                                        </Grid>
                                        <Grid item xs={2}>
                                            <TextField
                                                fullWidth
                                                size="small"
                                                label="Width"
                                                value={column.width || 'auto'}
                                                onChange={(e) => {
                                                    const updatedColumns = [...(tableConfig.columns || [])];
                                                    const existingIndex = updatedColumns.findIndex(col => col.field === field);
                                                    if (existingIndex >= 0) {
                                                        updatedColumns[existingIndex] = { ...column, width: e.target.value };
                                                    } else {
                                                        updatedColumns.push({ ...column, width: e.target.value });
                                                    }
                                                    onTableConfigChange({
                                                        ...tableConfig,
                                                        columns: updatedColumns
                                                    });
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        );
                    })}
                </Grid>
                
                <Grid item xs={12}>
                    <Divider sx={{ my: 2 }} />
                    <Typography variant="subtitle1" gutterBottom>
                        Table Options
                    </Typography>
                    <Grid container spacing={2}>
                        <Grid item xs={3}>
                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={tableConfig.show_header || false}
                                        onChange={(e) => onTableConfigChange({
                                            ...tableConfig,
                                            show_header: e.target.checked
                                        })}
                                    />
                                }
                                label="Show Header"
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={tableConfig.striped || false}
                                        onChange={(e) => onTableConfigChange({
                                            ...tableConfig,
                                            striped: e.target.checked
                                        })}
                                    />
                                }
                                label="Striped Rows"
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={tableConfig.bordered || false}
                                        onChange={(e) => onTableConfigChange({
                                            ...tableConfig,
                                            bordered: e.target.checked
                                        })}
                                    />
                                }
                                label="Bordered"
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={tableConfig.pagination || false}
                                        onChange={(e) => onTableConfigChange({
                                            ...tableConfig,
                                            pagination: e.target.checked
                                        })}
                                    />
                                }
                                label="Pagination"
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        </Box>
    );

    const renderChartConfig = () => {
        switch (chartType) {
            case 'bar':
                return renderBarChartConfig();
            case 'pie':
                return renderPieChartConfig();
            case 'line':
                return renderLineChartConfig();
            case 'area':
                return renderAreaChartConfig();
            case 'table':
                return renderTableConfig();
            default:
                return <Typography>Unknown chart type</Typography>;
        }
    };

    return (
        <Box sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
                <ShowChartIcon />
                <Typography variant="h6">
                    Chart Configuration
                </Typography>
                <Box sx={{ flex: 1 }} />
                <Button
                    variant="outlined"
                    startIcon={<ShowChartIcon />}
                    onClick={() => setPreviewOpen(true)}
                    disabled={!previewData || availableFields.length === 0}
                >
                    Preview Chart
                </Button>
            </Box>

            {!previewData && (
                <Alert severity="warning" sx={{ mb: 2 }}>
                    Please preview data in Step 2 before configuring the chart.
                </Alert>
            )}

            {previewData && availableFields.length === 0 && (
                <Alert severity="warning" sx={{ mb: 2 }}>
                    No fields found in the preview data. Please check your query in Step 2.
                </Alert>
            )}

            {previewData && availableFields.length > 0 && (
                <Card>
                    <CardContent>
                        {renderChartConfig()}
                    </CardContent>
                </Card>
            )}

            {/* Preview Dialog */}
            <Dialog
                open={previewOpen}
                onClose={() => setPreviewOpen(false)}
                maxWidth="lg"
                fullWidth
                PaperProps={{
                    sx: {
                        minHeight: '70vh',
                        maxHeight: '90vh'
                    }
                }}
            >
                <DialogTitle sx={{ pb: 1 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="h6">
                            Chart Preview - {chartType.charAt(0).toUpperCase() + chartType.slice(1)} Chart
                        </Typography>
                        <IconButton onClick={() => setPreviewOpen(false)}>
                            <CloseIcon />
                        </IconButton>
                    </Box>
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ p: 2 }}>
                        {renderChartPreview()}
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setPreviewOpen(false)}>
                        Close
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default Step3ChartConfig; 