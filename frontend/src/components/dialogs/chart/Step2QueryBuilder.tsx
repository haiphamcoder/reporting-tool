import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Grid,
    Card,
    CardContent,
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    IconButton,
    Alert,
    CircularProgress,
} from '@mui/material';
import {
    Add as AddIcon,
    Delete as DeleteIcon,
    Preview as PreviewIcon,
    Code as CodeIcon,
    Build as BuildIcon,
    TableChart as TableIcon
} from '@mui/icons-material';
import { ChartMode, QueryOption, FieldConfig, FilterConfig, ChartType, AggregationConfig, JoinConfig, JoinConditionConfig } from '../../../types/chart';
import { chartApi } from '../../../api/chart/chartApi';
import DataPreview from './DataPreview';

interface Step2QueryBuilderProps {
    chartMode: ChartMode;
    queryOption: QueryOption;
    sqlQuery: string;
    onQueryOptionChange: (queryOption: QueryOption) => void;
    onSqlQueryChange: (sqlQuery: string) => void;
    onPreviewData: (data: any) => void;
    onPreviewSuccess: (success: boolean) => void;
}

const Step2QueryBuilder: React.FC<Step2QueryBuilderProps> = ({
    chartMode,
    queryOption,
    sqlQuery,
    onQueryOptionChange,
    onSqlQueryChange,
    onPreviewData,
    onPreviewSuccess
}) => {
    const [previewLoading, setPreviewLoading] = useState(false);
    const [previewError, setPreviewError] = useState<string | null>(null);
    const [previewData, setPreviewData] = useState<any>(null);
    const [sources, setSources] = useState<any[]>([]);
    const [sourcesLoading, setSourcesLoading] = useState(false);

    // Load sources on component mount
    useEffect(() => {
        const loadSources = async () => {
            setSourcesLoading(true);
            try {
                const response = await chartApi.getSourcesList();
                if (response.success && response.data) {
                    setSources(response.data);
                }
            } catch (error) {
                console.error('Failed to load sources:', error);
            } finally {
                setSourcesLoading(false);
            }
        };

        loadSources();
    }, []);

    const handleAddField = () => {
        const newField: FieldConfig = {
            field_name: '',
            data_type: 'VARCHAR',
            alias: ''
        };
        onQueryOptionChange({
            ...queryOption,
            fields: [...queryOption.fields, newField]
        });
    };

    const handleUpdateField = (index: number, field: Partial<FieldConfig>) => {
        const updatedFields = [...queryOption.fields];
        updatedFields[index] = { ...updatedFields[index], ...field };
        onQueryOptionChange({
            ...queryOption,
            fields: updatedFields
        });
    };

    const handleRemoveField = (index: number) => {
        const updatedFields = queryOption.fields.filter((_, i) => i !== index);
        onQueryOptionChange({
            ...queryOption,
            fields: updatedFields
        });
    };

    const handleAddFilter = () => {
        const newFilter: FilterConfig = {
            field: '',
            operator: 'EQ',
            value: ''
        };
        onQueryOptionChange({
            ...queryOption,
            filters: [...(queryOption.filters || []), newFilter]
        });
    };

    const handleUpdateFilter = (index: number, filter: Partial<FilterConfig>) => {
        const updatedFilters = [...(queryOption.filters || [])];
        updatedFilters[index] = { ...updatedFilters[index], ...filter };
        onQueryOptionChange({
            ...queryOption,
            filters: updatedFilters
        });
    };

    const handleRemoveFilter = (index: number) => {
        const updatedFilters = (queryOption.filters || []).filter((_, i) => i !== index);
        onQueryOptionChange({
            ...queryOption,
            filters: updatedFilters
        });
    };

    const handleAddAggregation = () => {
        const newAggregation: AggregationConfig = {
            field: '',
            function: 'SUM',
            alias: ''
        };
        onQueryOptionChange({
            ...queryOption,
            aggregations: [...(queryOption.aggregations || []), newAggregation]
        });
    };

    const handleUpdateAggregation = (index: number, aggregation: Partial<AggregationConfig>) => {
        const updatedAggregations = [...(queryOption.aggregations || [])];
        updatedAggregations[index] = { ...updatedAggregations[index], ...aggregation };
        onQueryOptionChange({
            ...queryOption,
            aggregations: updatedAggregations
        });
    };

    const handleRemoveAggregation = (index: number) => {
        const updatedAggregations = (queryOption.aggregations || []).filter((_, i) => i !== index);
        onQueryOptionChange({
            ...queryOption,
            aggregations: updatedAggregations
        });
    };

    const handleAddJoin = () => {
        const newJoin: JoinConfig = {
            table: '',
            type: 'INNER',
            conditions: [],
            alias: ''
        };
        onQueryOptionChange({
            ...queryOption,
            joins: [...(queryOption.joins || []), newJoin]
        });
    };

    const handleUpdateJoin = (index: number, join: Partial<JoinConfig>) => {
        const updatedJoins = [...(queryOption.joins || [])];
        updatedJoins[index] = { ...updatedJoins[index], ...join };
        onQueryOptionChange({
            ...queryOption,
            joins: updatedJoins
        });
    };

    const handleRemoveJoin = (index: number) => {
        const updatedJoins = (queryOption.joins || []).filter((_, i) => i !== index);
        onQueryOptionChange({
            ...queryOption,
            joins: updatedJoins
        });
    };

    const handleAddJoinCondition = (joinIndex: number) => {
        const newCondition: JoinConditionConfig = {
            left_field: '',
            right_field: '',
            operator: 'EQ'
        };
        const updatedJoins = [...(queryOption.joins || [])];
        updatedJoins[joinIndex] = {
            ...updatedJoins[joinIndex],
            conditions: [...(updatedJoins[joinIndex].conditions || []), newCondition]
        };
        onQueryOptionChange({
            ...queryOption,
            joins: updatedJoins
        });
    };

    const handleUpdateJoinCondition = (joinIndex: number, conditionIndex: number, condition: Partial<JoinConditionConfig>) => {
        const updatedJoins = [...(queryOption.joins || [])];
        const updatedConditions = [...(updatedJoins[joinIndex].conditions || [])];
        updatedConditions[conditionIndex] = { ...updatedConditions[conditionIndex], ...condition };
        updatedJoins[joinIndex] = {
            ...updatedJoins[joinIndex],
            conditions: updatedConditions
        };
        onQueryOptionChange({
            ...queryOption,
            joins: updatedJoins
        });
    };

    const handleRemoveJoinCondition = (joinIndex: number, conditionIndex: number) => {
        const updatedJoins = [...(queryOption.joins || [])];
        const updatedConditions = (updatedJoins[joinIndex].conditions || []).filter((_, i) => i !== conditionIndex);
        updatedJoins[joinIndex] = {
            ...updatedJoins[joinIndex],
            conditions: updatedConditions
        };
        onQueryOptionChange({
            ...queryOption,
            joins: updatedJoins
        });
    };

    const handlePreview = async () => {
        setPreviewLoading(true);
        setPreviewError(null);

        try {
            // Validate required fields for basic mode
            if (chartMode === 'basic') {
                if (!queryOption.table) {
                    throw new Error('Please select a main table');
                }
                if (!queryOption.fields || queryOption.fields.length === 0) {
                    throw new Error('Please add at least one field');
                }
            }

            // Create chart data for preview
            const chartData = {
                name: 'Preview Chart',
                description: 'Preview chart for data validation',
                config: {
                    type: 'table' as ChartType, // Default type for preview
                    mode: chartMode,
                    ...(chartMode === 'basic' && { query_option: queryOption }),
                    ...(chartMode === 'advanced' && { sql_query: sqlQuery })
                }
            };

            // Call actual API for preview
            const response = await chartApi.previewChartData(chartData);

            if (response.success) {
                setPreviewData(response.data);
                onPreviewData(response.data);
                onPreviewSuccess(true);
            } else {
                throw new Error(response.message || 'Failed to preview data');
            }
        } catch (error) {
            console.error('Preview error:', error);
            setPreviewError(error instanceof Error ? error.message : 'Failed to preview data. Please check your query.');
            onPreviewSuccess(false);
        } finally {
            setPreviewLoading(false);
        }
    };

    const renderBasicMode = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                Query Builder
            </Typography>

            {/* Table Selection Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                        <TableIcon />
                        <Typography variant="subtitle1" fontWeight="medium">
                            Main Table
                        </Typography>
                    </Box>
                    <FormControl fullWidth>
                        <InputLabel size="small">Select Table</InputLabel>
                        <Select
                            value={queryOption.table || ''}
                            size="small"
                            onChange={(e) => onQueryOptionChange({
                                ...queryOption,
                                table: e.target.value
                            })}
                            label="Select Table"
                            disabled={sourcesLoading}
                        >
                            {sourcesLoading ? (
                                <MenuItem disabled>
                                    <CircularProgress size={20} sx={{ mr: 1 }} />
                                    Loading sources...
                                </MenuItem>
                            ) : (
                                sources.map((source) => (
                                    <MenuItem key={source.id} value={source.id}>
                                        {source.name} {source.description && `(${source.description})`}
                                    </MenuItem>
                                ))
                            )}
                        </Select>
                    </FormControl>
                </CardContent>
            </Card>

            {/* Joins Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Table Joins
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={handleAddJoin}
                            variant="outlined"
                            size="small"
                        >
                            Add Join
                        </Button>
                    </Box>

                    {(queryOption.joins || []).map((join, joinIndex) => (
                        <Card key={joinIndex} sx={{ mb: 2, border: '1px solid #e0e0e0' }}>
                            <CardContent>
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                    <Typography variant="subtitle2" color="primary">
                                        Join {joinIndex + 1}
                                    </Typography>
                                    <IconButton
                                        size="small"
                                        onClick={() => handleRemoveJoin(joinIndex)}
                                        color="error"
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </Box>

                                <Grid container spacing={2} sx={{ mb: 2 }}>
                                    <Grid item xs={3}>
                                        <FormControl fullWidth>
                                            <InputLabel>Join Type</InputLabel>
                                            <Select
                                                size="small"
                                                value={join.type}
                                                onChange={(e) => handleUpdateJoin(joinIndex, { type: e.target.value as any })}
                                                label="Join Type"
                                            >
                                                <MenuItem value="INNER">INNER JOIN</MenuItem>
                                                <MenuItem value="LEFT">LEFT JOIN</MenuItem>
                                                <MenuItem value="RIGHT">RIGHT JOIN</MenuItem>
                                                <MenuItem value="FULL">FULL JOIN</MenuItem>
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <FormControl fullWidth size="small">
                                            <InputLabel>Table</InputLabel>
                                            <Select
                                                size="small"
                                                value={join.table}
                                                onChange={(e) => handleUpdateJoin(joinIndex, { table: e.target.value })}
                                                label="Table"
                                            >
                                                {sources.map((source) => (
                                                    <MenuItem key={source.id} value={source.id}>
                                                        {source.name}
                                                    </MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                    <Grid item xs={3}>
                                        <TextField
                                            fullWidth
                                            size="small"
                                            label="Alias"
                                            value={join.alias}
                                            onChange={(e) => handleUpdateJoin(joinIndex, { alias: e.target.value })}
                                            placeholder="Table alias"
                                        />
                                    </Grid>
                                </Grid>

                                {/* Join Conditions */}
                                <Box sx={{ mb: 2 }}>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                        <Typography variant="body2" color="text.secondary">
                                            Join Conditions
                                        </Typography>
                                        <Button
                                            size="small"
                                            onClick={() => handleAddJoinCondition(joinIndex)}
                                            variant="text"
                                            startIcon={<AddIcon />}
                                        >
                                            Add Condition
                                        </Button>
                                    </Box>

                                    {(join.conditions || []).map((condition, conditionIndex) => (
                                        <Grid container spacing={2} key={conditionIndex} sx={{ mb: 1 }}>
                                            <Grid item xs={4}>
                                                <TextField
                                                    fullWidth
                                                    size="small"
                                                    label="Left Field"
                                                    value={condition.left_field}
                                                    onChange={(e) => handleUpdateJoinCondition(joinIndex, conditionIndex, { left_field: e.target.value })}
                                                    placeholder="table.field"
                                                />
                                            </Grid>
                                            <Grid item xs={2}>
                                                <FormControl fullWidth>
                                                    <InputLabel>Operator</InputLabel>
                                                    <Select
                                                        size="small"
                                                        value={condition.operator}
                                                        onChange={(e) => handleUpdateJoinCondition(joinIndex, conditionIndex, { operator: e.target.value as any })}
                                                        label="Operator"
                                                    >
                                                        <MenuItem value="EQ">=</MenuItem>
                                                        <MenuItem value="GT">&gt;</MenuItem>
                                                        <MenuItem value="GTE">≥</MenuItem>
                                                        <MenuItem value="LT">&lt;</MenuItem>
                                                        <MenuItem value="LTE">≤</MenuItem>
                                                    </Select>
                                                </FormControl>
                                            </Grid>
                                            <Grid item xs={4}>
                                                <TextField
                                                    fullWidth
                                                    size="small"
                                                    label="Right Field"
                                                    value={condition.right_field}
                                                    onChange={(e) => handleUpdateJoinCondition(joinIndex, conditionIndex, { right_field: e.target.value })}
                                                    placeholder="table.field"
                                                />
                                            </Grid>
                                            <Grid item xs={2}>
                                                <IconButton
                                                    size="small"
                                                    onClick={() => handleRemoveJoinCondition(joinIndex, conditionIndex)}
                                                    color="error"
                                                >
                                                    <DeleteIcon />
                                                </IconButton>
                                            </Grid>
                                        </Grid>
                                    ))}
                                </Box>
                            </CardContent>
                        </Card>
                    ))}
                </CardContent>
            </Card>

            {/* Fields Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Fields
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={handleAddField}
                            variant="outlined"
                            size="small"
                        >
                            Add Field
                        </Button>
                    </Box>

                    {queryOption.fields.map((field, index) => (
                        <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                            <Grid item xs={4}>
                                <TextField
                                    fullWidth
                                    label="Field Name"
                                    size="small"
                                    value={field.field_name}
                                    onChange={(e) => handleUpdateField(index, { field_name: e.target.value })}
                                    placeholder="e.g., customer_name, SUM(sales)"
                                />
                            </Grid>
                            <Grid item xs={3}>
                                <FormControl fullWidth>
                                    <InputLabel>Data Type</InputLabel>
                                    <Select
                                        size="small"
                                        value={field.data_type}
                                        onChange={(e) => handleUpdateField(index, { data_type: e.target.value })}
                                        label="Data Type"
                                    >
                                        <MenuItem value="VARCHAR">VARCHAR</MenuItem>
                                        <MenuItem value="INTEGER">INTEGER</MenuItem>
                                        <MenuItem value="DECIMAL">DECIMAL</MenuItem>
                                        <MenuItem value="DATE">DATE</MenuItem>
                                        <MenuItem value="BOOLEAN">BOOLEAN</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={3}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    label="Alias"
                                    value={field.alias}
                                    onChange={(e) => handleUpdateField(index, { alias: e.target.value })}
                                    placeholder="Display name"
                                />
                            </Grid>
                            <Grid item xs={2}>
                                <IconButton
                                    size="small"
                                    onClick={() => handleRemoveField(index)}
                                    color="error"
                                    sx={{ alignSelf: 'center', justifySelf: 'center' }}
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Grid>
                        </Grid>
                    ))}
                </CardContent>
            </Card>

            {/* Aggregations Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Aggregations
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={handleAddAggregation}
                            variant="outlined"
                            size="small"
                        >
                            Add Aggregation
                        </Button>
                    </Box>

                    {(queryOption.aggregations || []).map((aggregation, index) => (
                        <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                            <Grid item xs={4}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    label="Field"
                                    value={aggregation.field}
                                    onChange={(e) => handleUpdateAggregation(index, { field: e.target.value })}
                                    placeholder="Field to aggregate"
                                />
                            </Grid>
                            <Grid item xs={3}>
                                <FormControl fullWidth>
                                    <InputLabel>Function</InputLabel>
                                    <Select
                                        size="small"
                                        value={aggregation.function}
                                        onChange={(e) => handleUpdateAggregation(index, { function: e.target.value as any })}
                                        label="Function"
                                    >
                                        <MenuItem value="SUM">SUM</MenuItem>
                                        <MenuItem value="AVG">AVG</MenuItem>
                                        <MenuItem value="COUNT">COUNT</MenuItem>
                                        <MenuItem value="MIN">MIN</MenuItem>
                                        <MenuItem value="MAX">MAX</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={3}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    label="Alias"
                                    value={aggregation.alias}
                                    onChange={(e) => handleUpdateAggregation(index, { alias: e.target.value })}
                                    placeholder="Display name"
                                />
                            </Grid>
                            <Grid item xs={2}>
                                <IconButton
                                    size="small"
                                    onClick={() => handleRemoveAggregation(index)}
                                    color="error"
                                    sx={{ alignSelf: 'center', justifySelf: 'center' }}
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Grid>
                        </Grid>
                    ))}
                </CardContent>
            </Card>

            {/* Filters Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Filters
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={handleAddFilter}
                            variant="outlined"
                            size="small"
                        >
                            Add Filter
                        </Button>
                    </Box>

                    {(queryOption.filters || []).map((filter, index) => (
                        <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                            <Grid item xs={3}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    label="Field"
                                    value={filter.field}
                                    onChange={(e) => handleUpdateFilter(index, { field: e.target.value })}
                                    placeholder="Field name"
                                />
                            </Grid>
                            <Grid item xs={2}>
                                <FormControl fullWidth>
                                    <InputLabel>Operator</InputLabel>
                                    <Select
                                        value={filter.operator}
                                        size="small"
                                        onChange={(e) => handleUpdateFilter(index, { operator: e.target.value as any })}
                                        label="Operator"
                                    >
                                        <MenuItem value="EQ">=</MenuItem>
                                        <MenuItem value="NE">≠</MenuItem>
                                        <MenuItem value="GT">&gt;</MenuItem>
                                        <MenuItem value="GTE">≥</MenuItem>
                                        <MenuItem value="LT">&lt;</MenuItem>
                                        <MenuItem value="LTE">≤</MenuItem>
                                        <MenuItem value="LIKE">LIKE</MenuItem>
                                        <MenuItem value="IN">IN</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={5}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    label="Value"
                                    value={filter.value}
                                    onChange={(e) => handleUpdateFilter(index, { value: e.target.value })}
                                    placeholder="Filter value"
                                />
                            </Grid>
                            <Grid item xs={2}>
                                <IconButton
                                    size="small"
                                    onClick={() => handleRemoveFilter(index)}
                                    color="error"
                                    sx={{ alignSelf: 'center', justifySelf: 'center' }}
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Grid>
                        </Grid>
                    ))}
                </CardContent>
            </Card>

            {/* Group By Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Typography variant="subtitle1" fontWeight="medium" sx={{ mb: 2 }}>
                        Group By
                    </Typography>
                    <TextField
                        fullWidth
                        size="small"
                        label="Group By Fields"
                        value={queryOption.group_by?.join(', ') || ''}
                        onChange={(e) => onQueryOptionChange({
                            ...queryOption,
                            group_by: e.target.value.split(',').map(f => f.trim()).filter(f => f)
                        })}
                        placeholder="field1, field2, field3"
                        helperText="Separate multiple fields with commas"
                    />
                </CardContent>
            </Card>

            {/* Sort Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Typography variant="subtitle1" fontWeight="medium" sx={{ mb: 2 }}>
                        Sort Order
                    </Typography>
                    <TextField
                        size="small"
                        fullWidth
                        label="Sort Fields"
                        value={queryOption.sort?.map(s => `${s.field} ${s.direction}`).join(', ') || ''}
                        onChange={(e) => {
                            const sortFields = e.target.value.split(',').map(f => f.trim()).filter(f => f);
                            const sort = sortFields.map(field => {
                                const [fieldName, direction] = field.split(' ');
                                return {
                                    field: fieldName,
                                    direction: (direction || 'ASC') as 'ASC' | 'DESC'
                                };
                            });
                            onQueryOptionChange({
                                ...queryOption,
                                sort
                            });
                        }}
                        placeholder="field1 ASC, field2 DESC"
                        helperText="Format: field direction (ASC/DESC)"
                    />
                </CardContent>
            </Card>
        </Box>
    );

    const renderAdvancedMode = () => (
        <Box>
            <Typography variant="h6" gutterBottom>
                SQL Query Editor
            </Typography>

            <Card>
                <CardContent>
                    <TextField
                        fullWidth
                        multiline
                        rows={12}
                        label="SQL Query"
                        value={sqlQuery}
                        onChange={(e) => onSqlQueryChange(e.target.value)}
                        placeholder="SELECT column1, column2 FROM table WHERE condition GROUP BY column1 ORDER BY column1"
                        sx={{
                            '& .MuiInputBase-root': {
                                fontFamily: 'monospace',
                                fontSize: '14px'
                            }
                        }}
                    />
                </CardContent>
            </Card>
        </Box>
    );

    return (
        <Box sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {chartMode === 'basic' ? <BuildIcon /> : <CodeIcon />}
                    <Typography variant="h6">
                        {chartMode === 'basic' ? 'Query Builder' : 'SQL Editor'}
                    </Typography>
                </Box>

                <Button
                    variant="contained"
                    startIcon={previewLoading ? <CircularProgress size={20} /> : <PreviewIcon />}
                    onClick={handlePreview}
                    disabled={previewLoading}
                >
                    {previewLoading ? 'Previewing...' : 'Preview Data'}
                </Button>
            </Box>

            {previewError && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {previewError}
                </Alert>
            )}

            {chartMode === 'basic' ? renderBasicMode() : renderAdvancedMode()}

            {/* Data Preview */}
            {previewData && (
                <DataPreview
                    data={previewData}
                    showSuccess={true}
                />
            )}
        </Box>
    );
};

export default Step2QueryBuilder; 