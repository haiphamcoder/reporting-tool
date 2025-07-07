import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Stepper,
    Step,
    StepLabel,
    Box,
    Typography,
    Alert,
    CircularProgress,
    IconButton
} from '@mui/material';
import {
    Close as CloseIcon,
    ArrowBack as ArrowBackIcon,
    ArrowForward as ArrowForwardIcon,
    Check as CheckIcon
} from '@mui/icons-material';
import Step1BasicInfo from './chart/Step1BasicInfo';
import Step2QueryBuilder from './chart/Step2QueryBuilder';
import Step3ChartConfig from './chart/Step3ChartConfig';
import {
    ChartType,
    ChartMode,
    QueryOption,
    BarChartConfig,
    PieChartConfig,
    LineChartConfig,
    AreaChartConfig,
    TableConfig,
    CreateChartRequest
} from '../../types/chart';
import { chartApi } from '../../api/chart/chartApi';
import { generateSqlFromQueryOption } from '../../utils/sqlGenerator';

interface AddChartDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess?: (chartId: string) => void;
}

const steps = [
    'Basic Information',
    'Query Builder',
    'Chart Configuration'
];

const AddChartDialog: React.FC<AddChartDialogProps> = ({
    open,
    onClose,
    onSuccess
}) => {
    const [activeStep, setActiveStep] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Step 1 data
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [chartType, setChartType] = useState<ChartType>('bar');
    const [chartMode, setChartMode] = useState<ChartMode>('basic');

    // Step 2 data
    const [queryOption, setQueryOption] = useState<QueryOption>({
        fields: [
            { field_name: '', data_type: 'VARCHAR', alias: '' }
        ]
    });
    const [sqlQuery, setSqlQuery] = useState('');
    const [previewData, setPreviewData] = useState<any>(null);
    const [previewSuccess, setPreviewSuccess] = useState(false);
    const [sources, setSources] = useState<any[]>([]);

    // Step 3 data
    const [barChartConfig, setBarChartConfig] = useState<BarChartConfig>({
        x_axis: '',
        y_axis: ''
    });
    const [pieChartConfig, setPieChartConfig] = useState<PieChartConfig>({
        label_field: '',
        value_field: ''
    });
    const [lineChartConfig, setLineChartConfig] = useState<LineChartConfig>({
        x_axis: '',
        y_axis: ''
    });
    const [areaChartConfig, setAreaChartConfig] = useState<AreaChartConfig>({
        x_axis: '',
        y_axis: ''
    });
    const [tableConfig, setTableConfig] = useState<TableConfig>({
        columns: []
    });

    const handleNext = () => {
        if (activeStep === 0) {
            if (!name.trim()) {
                setError('Chart name is required');
                return;
            }
            setError(null);
        } else if (activeStep === 1) {
            if (chartMode === 'basic') {
                if (!queryOption.fields.length || !queryOption.fields[0].field_name) {
                    setError('At least one field is required');
                    return;
                }
            } else {
                if (!sqlQuery.trim()) {
                    setError('SQL query is required');
                    return;
                }
            }
            if (!previewSuccess) {
                setError('Please preview data successfully before proceeding');
                return;
            }
            setError(null);
        }

        setActiveStep((prevStep) => prevStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevStep) => prevStep - 1);
        setError(null);
    };

    const handleClose = () => {
        if (!loading) {
            setActiveStep(0);
            setName('');
            setDescription('');
            setChartType('bar');
            setChartMode('basic');
            setQueryOption({ fields: [{ field_name: '', data_type: 'VARCHAR', alias: '' }] });
            setSqlQuery('');
            setPreviewData(null);
            setPreviewSuccess(false);
            setBarChartConfig({ x_axis: '', y_axis: '' });
            setPieChartConfig({ label_field: '', value_field: '' });
            setLineChartConfig({ x_axis: '', y_axis: '' });
            setAreaChartConfig({ x_axis: '', y_axis: '' });
            setTableConfig({ columns: [] });
            setError(null);
            onClose();
        }
    };

    const handleCreateChart = async () => {
        setLoading(true);
        setError(null);

        try {
            const chartConfig: any = {
                type: chartType,
                mode: chartMode
            };

            if (chartMode === 'basic') {
                chartConfig.query_option = queryOption;
            }

            // Add specific chart configuration
            switch (chartType) {
                case 'bar':
                    chartConfig.bar_chart_config = barChartConfig;
                    break;
                case 'pie':
                    chartConfig.pie_chart_config = pieChartConfig;
                    break;
                case 'line':
                    chartConfig.line_chart_config = lineChartConfig;
                    break;
                case 'area':
                    chartConfig.area_chart_config = areaChartConfig;
                    break;
                case 'table':
                    chartConfig.table_config = tableConfig;
                    break;
            }

            // Đảm bảo có sql_query cho cả basic và advanced mode
            let finalSqlQuery = sqlQuery;
            if (chartMode === 'basic' && !sqlQuery.trim()) {
                // Nếu basic mode mà chưa có sqlQuery, generate từ queryOption
                try {
                    finalSqlQuery = generateSqlFromQueryOption(queryOption, sources);
                } catch (error) {
                    console.warn('Failed to generate SQL from queryOption:', error);
                }
            }

            const chartData: CreateChartRequest = {
                name: name.trim(),
                description: description.trim(),
                config: chartConfig,
                sql_query: finalSqlQuery
            };

            const response = await chartApi.createChart(chartData);

            if (onSuccess) {
                onSuccess(response.id || 'new-chart-id');
            }

            handleClose();
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to create chart. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handlePreviewData = (data: any) => {
        setPreviewData(data);

        // Hỗ trợ cả kiểu cũ (columns/rows) và kiểu mới (schema/records)
        let fields: string[] = [];
        
        if (data) {
            if (data.columns && data.rows) {
                // Kiểu cũ
                fields = data.columns;
            } else if (data.schema && data.records) {
                // Kiểu mới
                fields = data.schema
                    .filter((col: any) => !col.is_hidden)
                    .map((col: any) => col.field_name);
            } else if (data.result && data.result.columns) {
                // Có thể API trả về { result: { columns: [], rows: [] } }
                fields = data.result.columns;
            } else if (data.data && data.data.columns) {
                // Có thể API trả về { data: { columns: [], rows: [] } }
                fields = data.data.columns;
            } else if (data.records && data.records.length > 0) {
                // Fallback: lấy fields từ record đầu tiên
                fields = Object.keys(data.records[0]);
            } else if (data.rows && data.rows.length > 0) {
                // Fallback: lấy fields từ row đầu tiên
                fields = Object.keys(data.rows[0]);
            }
        }

        // Auto-populate chart configuration based on available fields
        if (fields.length > 0) {
            switch (chartType) {
                case 'bar':
                    setBarChartConfig({
                        x_axis: fields[0] || '',
                        y_axis: fields[1] || fields[0] || '',
                        x_axis_label: fields[0] || '',
                        y_axis_label: fields[1] || fields[0] || ''
                    });
                    break;
                case 'pie':
                    setPieChartConfig({
                        label_field: fields[0] || '',
                        value_field: fields[1] || fields[0] || ''
                    });
                    break;
                case 'line':
                    setLineChartConfig({
                        x_axis: fields[0] || '',
                        y_axis: fields[1] || fields[0] || '',
                        x_axis_label: fields[0] || '',
                        y_axis_label: fields[1] || fields[0] || ''
                    });
                    break;
                case 'area':
                    setAreaChartConfig({
                        x_axis: fields[0] || '',
                        y_axis: fields[1] || fields[0] || '',
                        x_axis_label: fields[0] || '',
                        y_axis_label: fields[1] || fields[0] || ''
                    });
                    break;
                case 'table':
                    const columns = fields.map((field: string) => ({
                        field,
                        header: field,
                        sortable: true,
                        width: 'auto',
                        align: 'left' as const
                    }));
                    setTableConfig({ columns });
                    break;
            }
        }
    };

    const renderStepContent = () => {
        switch (activeStep) {
            case 0:
                return (
                    <Step1BasicInfo
                        name={name}
                        description={description}
                        chartType={chartType}
                        chartMode={chartMode}
                        onNameChange={setName}
                        onDescriptionChange={setDescription}
                        onChartTypeChange={setChartType}
                        onChartModeChange={setChartMode}
                    />
                );
            case 1:
                return (
                    <Step2QueryBuilder
                        chartMode={chartMode}
                        queryOption={queryOption}
                        sqlQuery={sqlQuery}
                        onQueryOptionChange={setQueryOption}
                        onSqlQueryChange={setSqlQuery}
                        onPreviewData={handlePreviewData}
                        onPreviewSuccess={setPreviewSuccess}
                        onSourcesChange={setSources}
                    />
                );
            case 2:
                return (
                    <Step3ChartConfig
                        chartType={chartType}
                        previewData={previewData}
                        barChartConfig={barChartConfig}
                        pieChartConfig={pieChartConfig}
                        lineChartConfig={lineChartConfig}
                        areaChartConfig={areaChartConfig}
                        tableConfig={tableConfig}
                        onBarChartConfigChange={setBarChartConfig}
                        onPieChartConfigChange={setPieChartConfig}
                        onLineChartConfigChange={setLineChartConfig}
                        onAreaChartConfigChange={setAreaChartConfig}
                        onTableConfigChange={setTableConfig}
                    />
                );
            default:
                return null;
        }
    };

    const isStepValid = (step: number) => {
        switch (step) {
            case 0:
                return name.trim().length > 0;
            case 1:
                if (chartMode === 'basic') {
                    return queryOption.fields.length > 0 &&
                        queryOption.fields[0].field_name.trim().length > 0 &&
                        previewSuccess;
                } else {
                    return sqlQuery.trim().length > 0 && previewSuccess;
                }
            case 2:
                return previewData !== null;
            default:
                return false;
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth="lg"
            fullWidth
            PaperProps={{
                sx: {
                    minHeight: '80vh',
                    maxHeight: '90vh'
                }
            }}
        >
            <DialogTitle sx={{ pb: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="h6">
                        Create New Chart
                    </Typography>
                    <IconButton onClick={handleClose}>
                        <CloseIcon />
                    </IconButton>
                </Box>
            </DialogTitle>

            <DialogContent sx={{ p: 0 }}>
                <Box sx={{ px: 3, py: 2 }}>
                    <Stepper activeStep={activeStep} alternativeLabel>
                        {steps.map((label, index) => (
                            <Step key={label}>
                                <StepLabel
                                    error={index === activeStep && error !== null}
                                    optional={index === 2 && (
                                        <Typography variant="caption" color="text.secondary">
                                            Final Step
                                        </Typography>
                                    )}
                                >
                                    {label}
                                </StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                </Box>

                {error && (
                    <Box sx={{ px: 3, pb: 2 }}>
                        <Alert severity="error" onClose={() => setError(null)}>
                            {error}
                        </Alert>
                    </Box>
                )}

                <Box sx={{ flex: 1, overflow: 'auto' }}>
                    {renderStepContent()}
                </Box>
            </DialogContent>

            <DialogActions sx={{ px: 3, py: 2, gap: 1 }}>
                <Button
                    onClick={handleBack}
                    disabled={activeStep === 0 || loading}
                    startIcon={<ArrowBackIcon />}
                >
                    Back
                </Button>

                <Box sx={{ flex: 1 }} />

                {activeStep === steps.length - 1 ? (
                    <Button
                        onClick={handleCreateChart}
                        variant="contained"
                        disabled={loading || !isStepValid(activeStep)}
                        startIcon={loading ? <CircularProgress size={20} /> : <CheckIcon />}
                    >
                        {loading ? 'Creating...' : 'Create Chart'}
                    </Button>
                ) : (
                    <Button
                        onClick={handleNext}
                        variant="contained"
                        disabled={!isStepValid(activeStep) || loading}
                        endIcon={<ArrowForwardIcon />}
                    >
                        Next
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default AddChartDialog;
