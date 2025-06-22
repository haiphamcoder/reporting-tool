import React from 'react';
import {
    Box,
    Typography,
    Grid,
    Card,
    CardContent,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Switch,
    FormControlLabel,
    Slider,
    Divider,
    Alert
} from '@mui/material';
import {
    ShowChart as ChartIcon
} from '@mui/icons-material';
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
    const availableFields = previewData?.columns || [];

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
                <ChartIcon />
                <Typography variant="h6">
                    Chart Configuration
                </Typography>
            </Box>

            {!previewData && (
                <Alert severity="warning" sx={{ mb: 2 }}>
                    Please preview data in Step 2 before configuring the chart.
                </Alert>
            )}

            {previewData && (
                <Card>
                    <CardContent>
                        {renderChartConfig()}
                    </CardContent>
                </Card>
            )}
        </Box>
    );
};

export default Step3ChartConfig; 