import React from 'react';
import {
    Box,
    TextField,
    FormControlLabel,
    RadioGroup,
    Radio,
    Typography,
    Grid,
    Card,
    CardContent
} from '@mui/material';
import {
    BarChart as BarChartIcon,
    PieChart as PieChartIcon,
    ShowChart as LineChartIcon,
    StackedLineChart as AreaChartIcon,
    TableChart as TableChartIcon,
    Code as AdvancedIcon,
    Dashboard as BasicIcon
} from '@mui/icons-material';
import { ChartType, ChartMode } from '../../../types/chart';

interface Step1BasicInfoProps {
    name: string;
    description: string;
    chartType: ChartType;
    chartMode: ChartMode;
    onNameChange: (name: string) => void;
    onDescriptionChange: (description: string) => void;
    onChartTypeChange: (type: ChartType) => void;
    onChartModeChange: (mode: ChartMode) => void;
}

const chartTypeOptions = [
    { value: 'table', label: 'Table', icon: TableChartIcon, description: 'Display data in tabular format' },
    { value: 'bar', label: 'Bar Chart', icon: BarChartIcon, description: 'Compare values across categories' },
    { value: 'line', label: 'Line Chart', icon: LineChartIcon, description: 'Show trends over time' },
    { value: 'area', label: 'Area Chart', icon: AreaChartIcon, description: 'Display cumulative data' },
    { value: 'pie', label: 'Pie Chart', icon: PieChartIcon, description: 'Show proportions of a whole' }
];

const Step1BasicInfo: React.FC<Step1BasicInfoProps> = ({
    name,
    description,
    chartType,
    chartMode,
    onNameChange,
    onDescriptionChange,
    onChartTypeChange,
    onChartModeChange
}) => {
    return (
        <Box sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>

            {/* Name and Description */}
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                <Typography variant="h6" gutterBottom>
                    Basic Information
                </Typography>
                <Grid sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                    <Typography variant="body1">
                        Chart Name
                    </Typography>
                    <TextField
                        fullWidth
                        value={name}
                        onChange={(e) => onNameChange(e.target.value)}
                        placeholder="Enter chart name"
                        required
                    />
                </Grid>
                <Grid sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                    <Typography variant="body1">
                        Description
                    </Typography>
                    <TextField
                        fullWidth
                        value={description}
                        onChange={(e) => onDescriptionChange(e.target.value)}
                        placeholder="Enter chart description"
                    />
                </Grid>
            </Box>

            {/* Chart Mode Selection */}
            {/* <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                <Typography variant="h6" gutterBottom>
                    Chart Mode
                </Typography>
                <RadioGroup
                    value={chartMode}
                    onChange={(e) => onChartModeChange(e.target.value as ChartMode)}
                    sx={{ mb: 4 }}
                >
                    <Grid container spacing={2}>
                        <Grid item xs={6}>
                            <Card
                                variant="outlined"
                                sx={{
                                    cursor: 'pointer',
                                    borderColor: chartMode === 'basic' ? 'primary.main' : 'divider',
                                    backgroundColor: chartMode === 'basic' ? 'primary.50' : 'background.paper'
                                }}
                                onClick={() => onChartModeChange('basic')}
                            >
                                <CardContent sx={{ textAlign: 'center', py: 2 }}>
                                    <Box sx={{ display: 'flex', flexDirection: 'row', gap: 1, alignItems: 'center', justifyContent: 'center' }}>
                                        <BasicIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
                                        <FormControlLabel
                                            value="basic"
                                            control={<Radio />}
                                            label="Basic Mode"
                                            sx={{ mb: 1 }}
                                        />
                                    </Box>
                                    <Typography variant="body2" color="text.secondary">
                                        Use visual interface to build queries
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                        <Grid item xs={6}>
                            <Card
                                variant="outlined"
                                sx={{
                                    cursor: 'pointer',
                                    borderColor: chartMode === 'advanced' ? 'primary.main' : 'divider',
                                    backgroundColor: chartMode === 'advanced' ? 'primary.50' : 'background.paper'
                                }}
                                onClick={() => onChartModeChange('advanced')}
                            >
                                <CardContent sx={{ textAlign: 'center', py: 2 }}>
                                    <Box sx={{ display: 'flex', flexDirection: 'row', gap: 1, alignItems: 'center', justifyContent: 'center' }}>
                                        <AdvancedIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
                                        <FormControlLabel
                                            value="advanced"
                                            control={<Radio />}
                                            label="Advanced Mode"
                                            sx={{ mb: 1 }}
                                        />
                                    </Box>
                                    <Typography variant="body2" color="text.secondary">
                                        Write custom SQL queries
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    </Grid>
                </RadioGroup>
            </Box> */}

            {/* Chart Type Selection */}
            <Typography variant="h6" gutterBottom>
                Chart Type
            </Typography>
            <Grid container spacing={2}>
                {chartTypeOptions.map((option) => {
                    const IconComponent = option.icon;
                    return (
                        <Grid item xs={12} sm={6} md={4} key={option.value}>
                            <Card
                                variant="outlined"
                                sx={{
                                    cursor: 'pointer',
                                    borderColor: chartType === option.value ? 'primary.main' : 'divider',
                                    backgroundColor: chartType === option.value ? 'primary.50' : 'background.paper',
                                    '&:hover': {
                                        borderColor: 'primary.main',
                                        backgroundColor: 'primary.25'
                                    }
                                }}
                                onClick={() => onChartTypeChange(option.value as ChartType)}
                            >
                                <CardContent sx={{ textAlign: 'center', py: 2 }}>
                                    <IconComponent
                                        sx={{
                                            fontSize: 40,
                                            color: chartType === option.value ? 'primary.main' : 'text.secondary',
                                            mb: 1
                                        }}
                                    />
                                    <Typography variant="subtitle1" fontWeight="medium">
                                        {option.label}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                        {option.description}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    );
                })}
            </Grid>
        </Box>
    );
};

export default Step1BasicInfo; 