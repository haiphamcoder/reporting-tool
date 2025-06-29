import React, { useState, useEffect } from 'react';
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

interface EditChartDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess?: () => void;
    chartId?: string;
}

const steps = [
    'Basic Information',
    'Query Builder',
    'Chart Configuration'
];

const EditChartDialog: React.FC<EditChartDialogProps> = ({
    open,
    onClose,
    onSuccess,
    chartId
}) => {
    const [activeStep, setActiveStep] = useState(0);
    const [loading, setLoading] = useState(false);
    const [fetching, setFetching] = useState(false);
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

    // Fetch chart details when dialog opens
    useEffect(() => {
        if (open && chartId) {
            fetchChartDetails();
        }
    }, [open, chartId]);

    const fetchChartDetails = async () => {
        if (!chartId) return;

        setFetching(true);
        setError(null);

        try {
            const response = await chartApi.getChart(chartId);

            // Handle response format: { code, success, message, result, timestamp }
            if (response.success && response.result) {
                const chart = response.result;

                // Set basic info
                setName(chart.name || '');
                setDescription(chart.description || '');
                setChartType(chart.config?.type || chart.type || 'bar');
                setChartMode(chart.config?.mode || 'basic');

                // Set query data
                if (chart.config?.query_option) {
                    setQueryOption(chart.config.query_option);
                }
                if (chart.sql_query) {
                    setSqlQuery(chart.sql_query);
                }

                // Set chart specific configs
                if (chart.config?.bar_chart_config) {
                    setBarChartConfig(chart.config.bar_chart_config);
                }
                if (chart.config?.pie_chart_config) {
                    setPieChartConfig(chart.config.pie_chart_config);
                }
                if (chart.config?.line_chart_config) {
                    setLineChartConfig(chart.config.line_chart_config);
                }
                if (chart.config?.area_chart_config) {
                    setAreaChartConfig(chart.config.area_chart_config);
                }
                if (chart.config?.table_config) {
                    setTableConfig(chart.config.table_config);
                }

                // Preview data based on chart mode
                await handlePreviewDataFromChart(chart);
            } else {
                setError(response.message || 'Failed to fetch chart details');
            }
        } catch (err: any) {
            setError(err.message || 'Failed to fetch chart details');
        } finally {
            setFetching(false);
        }
    };

    const handlePreviewDataFromChart = async (chart: any) => {
        try {
            let sql_query = '';
            let fields: any[] = [];

            if (chart.config?.mode === 'basic' && chart.config?.query_option) {
                // Convert query_option to SQL query
                const convertRes = await chartApi.convertQuery(chart.config.query_option);
                if (!convertRes.success || !convertRes.result) {
                    throw new Error(convertRes.message || 'Failed to convert query');
                }
                sql_query = convertRes.result;

                // Prepare fields for preview-data API
                fields = (chart.config.query_option.fields || []).map((f: any) => ({
                    field_name: f.alias && f.alias !== '' ? f.alias : f.field_name,
                    data_type: f.data_type,
                    alias: f.alias || ''
                }));
            } else if (chart.config?.mode === 'advanced' && chart.sql_query) {
                // Use SQL query directly
                sql_query = chart.sql_query;
                fields = [];
            } else {
                // No data to preview
                return;
            }

            if (!sql_query) {
                return;
            }

            // Call preview-data API
            const previewRes = await chartApi.previewData({ sql_query, fields });
            if (previewRes.success) {
                setPreviewData(previewRes.result);
                setPreviewSuccess(true);
            } else {
                setError(previewRes.message || 'Failed to preview data');
            }
        } catch (err: any) {
            setError(err.message || 'Failed to preview data');
        }
    };

    const handlePreviewData = async (data?: any) => {
        if (data) {
            setPreviewData(data);
            setPreviewSuccess(true);
            return;
        }

        setPreviewSuccess(false);
        setError(null);

        try {
            let sql_query = '';
            let fields: any[] = [];

            if (chartMode === 'basic') {
                // Convert query_option to SQL query
                const convertRes = await chartApi.convertQuery(queryOption);
                if (!convertRes.success || !convertRes.result) {
                    throw new Error(convertRes.message || 'Failed to convert query');
                }
                sql_query = convertRes.result;

                // Prepare fields for preview-data API
                fields = (queryOption.fields || []).map((f: any) => ({
                    field_name: f.alias && f.alias !== '' ? f.alias : f.field_name,
                    data_type: f.data_type,
                    alias: f.alias || ''
                }));
            } else {
                // Use SQL query directly
                sql_query = sqlQuery;
                fields = [];
            }

            if (!sql_query) {
                throw new Error('No SQL query available');
            }

            // Call preview-data API
            const previewRes = await chartApi.previewData({ sql_query, fields });
            if (previewRes.success) {
                setPreviewData(previewRes.result);
                setPreviewSuccess(true);
            } else {
                setError(previewRes.message || 'Failed to preview data');
            }
        } catch (err: any) {
            setError(err.message || 'Failed to preview data');
        }
    };

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
        if (!loading && !fetching) {
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

    const handleUpdateChart = async () => {
        if (!chartId) return;

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

            const chartData: CreateChartRequest = {
                name: name.trim(),
                description: description.trim(),
                config: chartConfig
            };

            if (chartMode === 'advanced') {
                chartData.sql_query = sqlQuery;
            }

            const response = await chartApi.updateChart(chartId, chartData);

            if (onSuccess) {
                onSuccess();
            }

            handleClose();
        } catch (err: any) {
            setError(err.message || 'Failed to update chart. Please try again.');
        } finally {
            setLoading(false);
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
                        onQueryOptionChange={setQueryOption}
                        sqlQuery={sqlQuery}
                        onSqlQueryChange={setSqlQuery}
                        onPreviewData={handlePreviewData}
                        onPreviewSuccess={setPreviewSuccess}
                    />
                );
            case 2:
                return (
                    <Step3ChartConfig
                        chartType={chartType}
                        barChartConfig={barChartConfig}
                        onBarChartConfigChange={setBarChartConfig}
                        pieChartConfig={pieChartConfig}
                        onPieChartConfigChange={setPieChartConfig}
                        lineChartConfig={lineChartConfig}
                        onLineChartConfigChange={setLineChartConfig}
                        areaChartConfig={areaChartConfig}
                        onAreaChartConfigChange={setAreaChartConfig}
                        tableConfig={tableConfig}
                        onTableConfigChange={setTableConfig}
                        previewData={previewData}
                    />
                );
            default:
                return null;
        }
    };

    const isStepValid = (step: number) => {
        switch (step) {
            case 0:
                return name.trim() !== '';
            case 1:
                if (chartMode === 'basic') {
                    return queryOption.fields.length > 0 && queryOption.fields[0].field_name !== '';
                } else {
                    return sqlQuery.trim() !== '';
                }
            case 2:
                return true;
            default:
                return false;
        }
    };

    if (fetching) {
        return (
            <Dialog open={open} maxWidth="md" fullWidth>
                <DialogContent>
                    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                        <CircularProgress />
                    </Box>
                </DialogContent>
            </Dialog>
        );
    }

    return (
        <Dialog open={open}
            onClose={handleClose}
            maxWidth="lg"
            fullWidth
            PaperProps={{
                sx: {
                    minHeight: '80vh',
                    maxHeight: '90vh'
                }
            }}>
            <DialogTitle>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="h6">Edit Chart</Typography>
                    <IconButton onClick={handleClose} disabled={loading}>
                        <CloseIcon />
                    </IconButton>
                </Box>
            </DialogTitle>

            <DialogContent>
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}

                <Stepper activeStep={activeStep} sx={{ mb: 3 }}>
                    {steps.map((label, index) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>

                {renderStepContent()}
            </DialogContent>

            <DialogActions sx={{ p: 3, pt: 0 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                    <Button
                        onClick={handleBack}
                        disabled={activeStep === 0 || loading}
                        startIcon={<ArrowBackIcon />}
                    >
                        Back
                    </Button>
                    <Box>
                        <Button
                            onClick={handleClose}
                            disabled={loading}
                            sx={{ mr: 1 }}
                        >
                            Cancel
                        </Button>
                        {activeStep === steps.length - 1 ? (
                            <Button
                                variant="contained"
                                onClick={handleUpdateChart}
                                disabled={loading || !isStepValid(activeStep)}
                                startIcon={loading ? <CircularProgress size={20} /> : <CheckIcon />}
                            >
                                {loading ? 'Updating...' : 'Update Chart'}
                            </Button>
                        ) : (
                            <Button
                                variant="contained"
                                onClick={handleNext}
                                disabled={!isStepValid(activeStep) || loading}
                                endIcon={<ArrowForwardIcon />}
                            >
                                Next
                            </Button>
                        )}
                    </Box>
                </Box>
            </DialogActions>
        </Dialog>
    );
};

export default EditChartDialog; 