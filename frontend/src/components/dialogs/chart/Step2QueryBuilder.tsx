import React, { useState, useEffect, useCallback } from 'react';
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
    const [sourceMappings, setSourceMappings] = useState<{ [sourceId: string]: any[] }>({}); // { sourceId: mapping[] }
    const [allSourceFields, setAllSourceFields] = useState<any[]>([]); // [{ source_id, source_name, field_mapping, field_type }]

    // Load sources on component mount
    useEffect(() => {
        const loadSources = async () => {
            setSourcesLoading(true);
            try {
                const response = await chartApi.getSourcesList();
                if (response.success && response.data) {
                    setSources(response.data);
                    // Tổng hợp tất cả các field từ field_mapping của tất cả các source, kèm source_id và source_name
                    const allFields = response.data.flatMap((source: any) =>
                        (source.field_mapping || []).map((field: any) => ({
                            source_id: source.id,
                            source_name: source.name,
                            field_name: field.field_name,
                            data_type: field.data_type
                        }))
                    );
                    setAllSourceFields(allFields);
                }
            } catch (error) {
                console.error('Failed to load sources:', error);
            } finally {
                setSourcesLoading(false);
            }
        };

        loadSources();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Hàm lấy chi tiết source và lưu mapping vào state
    const fetchSourceMapping = useCallback(async (sourceId: string, sourceName: string) => {
        if (!sourceId || sourceMappings[sourceId]) return;
        try {
            const detail = await chartApi.getDetailsSource(sourceId);
            console.log('detail', detail);
            const mapping = detail.mapping || [];
            setSourceMappings(prev => ({ ...prev, [sourceId]: mapping.map((m: any) => ({ ...m, source_id: sourceId, source_name: sourceName })) }));
        } catch (e) {
            // handle error nếu cần
        }
    }, [sourceMappings]);

    // Khi chọn main table hoặc join table, fetch mapping nếu chưa có
    useEffect(() => {
        // Main table
        if (queryOption.table) {
            const mainSource = sources.find(s => s.id === queryOption.table);
            if (mainSource) fetchSourceMapping(mainSource.id, mainSource.name);
        }
        // Join tables
        (queryOption.joins || []).forEach(join => {
            if (join.table) {
                const joinSource = sources.find(s => s.id === join.table);
                if (joinSource) fetchSourceMapping(joinSource.id, joinSource.name);
            }
        });
    }, [queryOption.table, queryOption.joins, sources, fetchSourceMapping]);

    // Tổng hợp field cho combobox từ các source đã chọn (main + join)
    useEffect(() => {
        let fields: any[] = [];
        // Main table
        if (queryOption.table && sourceMappings[queryOption.table]) {
            const mainSource = sources.find(s => s.id === queryOption.table);
            fields = fields.concat(
                sourceMappings[queryOption.table].map(f => ({
                    source_id: queryOption.table,
                    source_name: mainSource?.name || '',
                    field_mapping: f.field_mapping,
                    field_name: f.field_name,
                    field_type: f.field_type
                }))
            );
        }
        // Join tables
        (queryOption.joins || []).forEach(join => {
            if (join.table && sourceMappings[join.table]) {
                const joinSource = sources.find(s => s.id === join.table);
                fields = fields.concat(
                    sourceMappings[join.table].map(f => ({
                        source_id: join.table,
                        source_name: joinSource?.name || '',
                        field_mapping: f.field_mapping,
                        field_name: f.field_name,
                        field_type: f.field_type
                    }))
                );
            }
        });
        setAllSourceFields(fields);
    }, [queryOption.table, queryOption.joins, sourceMappings, sources]);

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

    // Khi user chọn field mới, tự động cập nhật data_type tương ứng
    const handleFieldNameChange = (index: number, value: string) => {
        // value dạng: source_id.field_mapping
        const [source_id, ...fieldMappingParts] = value.split('.');
        const field_mapping = fieldMappingParts.join('.');
        const found = allSourceFields.find(f => f.source_id === source_id && f.field_mapping === field_mapping);
        if (found) {
            handleUpdateField(index, {
                field_name: found.field_name,
                data_type: found.field_type,
                source_id: found.source_id,
                source_name: found.source_name,
                field_mapping: found.field_mapping
            });
        }
    };

    // Lấy danh sách operator phù hợp với data_type
    const getOperatorsByDataType = (dataType?: string): FilterConfig['operator'][] => {
        if (!dataType) return ['EQ', 'NE'];
        const type = dataType.toLowerCase();
        if (type.includes('char') || type.includes('text') || type === 'string') {
            return ['EQ', 'NE', 'LIKE', 'IN', 'NOT_IN'];
        }
        if (type.includes('int') || type.includes('float') || type.includes('double') || type === 'number' || type === 'decimal') {
            return ['EQ', 'NE', 'GT', 'GTE', 'LT', 'LTE', 'IN', 'NOT_IN'];
        }
        if (type.includes('date') || type.includes('time')) {
            return ['EQ', 'NE', 'GT', 'GTE', 'LT', 'LTE'];
        }
        if (type === 'boolean' || type === 'bool') {
            return ['EQ', 'NE'];
        }
        return ['EQ', 'NE'];
    };

    // Khi user chọn field cho filter, tự động lưu data_type
    const handleFilterFieldChange = (index: number, value: string) => {
        const [source_id, ...fieldMappingParts] = value.split('.');
        const field_mapping = fieldMappingParts.join('.');
        const found = allSourceFields.find(f => f.source_id === source_id && f.field_mapping === field_mapping);
        if (found) {
            handleUpdateFilter(index, {
                field: found.field_name,
                data_type: found.field_type,
                source_id: found.source_id,
                source_name: found.source_name,
                field_mapping: found.field_mapping,
                operator: getOperatorsByDataType(found.field_type)[0] // default operator
            });
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

                    {queryOption.fields.map((field, index) => {
                        // Danh sách field chưa được chọn ở các dòng khác
                        const selectedFieldKeys = queryOption.fields
                            .filter((_, i) => i !== index)
                            .map(f => f.source_id && f.field_mapping ? `${f.source_id}.${f.field_mapping}` : '');
                        const availableFields = allSourceFields.filter(f => !selectedFieldKeys.includes(`${f.source_id}.${f.field_mapping}`));
                        // Xác định trạng thái loading mapping
                        const mainTableSelected = !!queryOption.table;
                        const mainTableMappingLoaded = queryOption.table && sourceMappings[queryOption.table];
                        const joinTableIds = (queryOption.joins || []).map(j => j.table).filter(Boolean);
                        const joinMappingsLoaded = joinTableIds.every(jid => sourceMappings[jid]);
                        const mappingLoading = mainTableSelected && (!mainTableMappingLoaded || !joinMappingsLoaded);
                        // Sửa lại: chỉ disable khi chưa chọn bảng hoặc mapping đang loading
                        const disableFieldSelect = !mainTableSelected || mappingLoading;
                        return (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                                <Grid item xs={4}>
                                    <FormControl fullWidth>
                                        <InputLabel>Field Name</InputLabel>
                                        <Select
                                            size="small"
                                            value={field.source_id && field.field_mapping ? `${field.source_id}.${field.field_mapping}` : ''}
                                            label="Field Name"
                                            onChange={(e) => handleFieldNameChange(index, e.target.value)}
                                            disabled={disableFieldSelect}
                                            renderValue={selected => {
                                                if (mappingLoading) return 'Đang tải fields...';
                                                if (!mainTableSelected) return 'Chọn bảng để lấy field';
                                                if (!selected) return '';
                                                const f = allSourceFields.find(f => `${f.source_id}.${f.field_mapping}` === selected);
                                                return f ? `${f.source_name}.${f.field_mapping}` : '';
                                            }}
                                        >
                                            {mappingLoading && (
                                                <MenuItem disabled>
                                                    <CircularProgress size={18} sx={{ mr: 1 }} /> Đang tải fields...
                                                </MenuItem>
                                            )}
                                            {!mainTableSelected && (
                                                <MenuItem disabled>Chọn bảng để lấy field</MenuItem>
                                            )}
                                            {availableFields.length === 0 && !mappingLoading && mainTableSelected && (
                                                <MenuItem disabled>Không còn field để chọn</MenuItem>
                                            )}
                                            {availableFields.map((f, i) => (
                                                <MenuItem key={i} value={`${f.source_id}.${f.field_mapping}`}>
                                                    {`${f.source_name}.${f.field_mapping}`}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                        {/* Debug: Hiển thị số lượng field thực tế */}
                                        <Box sx={{ mt: 0.5 }}>
                                            <Typography variant="caption" color="text.secondary">
                                                {`Số field khả dụng: ${allSourceFields.length}`}
                                            </Typography>
                                        </Box>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={3}>
                                    <TextField
                                        fullWidth
                                        label="Data Type"
                                        size="small"
                                        value={field.data_type || ''}
                                        InputProps={{ readOnly: true }}
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={3}>
                                    <TextField
                                        fullWidth
                                        size="small"
                                        label="Alias"
                                        value={field.alias}
                                        onChange={(e) => handleUpdateField(index, { alias: e.target.value })}
                                        placeholder="Display name"
                                        disabled={disableFieldSelect || !(field.source_id && field.field_mapping)}
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
                        );
                    })}
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

                    {(queryOption.filters || []).map((filter, index) => {
                        // Danh sách field chưa được chọn ở các filter khác
                        const selectedFieldKeys = (queryOption.filters || [])
                            .filter((_, i) => i !== index)
                            .map(f => f.source_id && f.field_mapping ? `${f.source_id}.${f.field_mapping}` : '');
                        const availableFields = allSourceFields.filter(f => !selectedFieldKeys.includes(`${f.source_id}.${f.field_mapping}`));
                        const disableFieldSelect = !queryOption.table || Object.keys(sourceMappings).length === 0;
                        const operatorOptions = getOperatorsByDataType(filter.data_type);
                        return (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                                <Grid item xs={3}>
                                    <FormControl fullWidth>
                                        <InputLabel>Field</InputLabel>
                                        <Select
                                            size="small"
                                            value={filter.source_id && filter.field_mapping ? `${filter.source_id}.${filter.field_mapping}` : ''}
                                            label="Field"
                                            onChange={(e) => handleFilterFieldChange(index, e.target.value)}
                                            disabled={disableFieldSelect}
                                            renderValue={selected => {
                                                if (!queryOption.table) return 'Chọn bảng để lấy field';
                                                if (!selected) return '';
                                                const f = allSourceFields.find(f => `${f.source_id}.${f.field_mapping}` === selected);
                                                return f ? `${f.source_name}.${f.field_mapping}` : '';
                                            }}
                                        >
                                            {!queryOption.table && (
                                                <MenuItem disabled>Chọn bảng để lấy field</MenuItem>
                                            )}
                                            {availableFields.length === 0 && queryOption.table && (
                                                <MenuItem disabled>Không còn field để chọn</MenuItem>
                                            )}
                                            {availableFields.map((f, i) => (
                                                <MenuItem key={i} value={`${f.source_id}.${f.field_mapping}`}>{`${f.source_name}.${f.field_mapping}`}</MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={2}>
                                    <FormControl fullWidth>
                                        <InputLabel>Operator</InputLabel>
                                        <Select
                                            value={filter.operator}
                                            size="small"
                                            onChange={(e) => handleUpdateFilter(index, { operator: e.target.value as any })}
                                            label="Operator"
                                            disabled={!filter.data_type}
                                        >
                                            {operatorOptions.map(op => (
                                                <MenuItem key={op} value={op}>{
                                                    op === 'EQ' ? '=' :
                                                    op === 'NE' ? '≠' :
                                                    op === 'GT' ? '>' :
                                                    op === 'GTE' ? '≥' :
                                                    op === 'LT' ? '<' :
                                                    op === 'LTE' ? '≤' :
                                                    op === 'LIKE' ? 'LIKE' :
                                                    op === 'IN' ? 'IN' :
                                                    op === 'NOT_IN' ? 'NOT IN' : op
                                                }</MenuItem>
                                            ))}
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
                                        disabled={!filter.data_type}
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
                        );
                    })}
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