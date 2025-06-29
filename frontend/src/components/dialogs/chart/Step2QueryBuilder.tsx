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
import { ChartMode, QueryOption, FieldConfig, FilterConfig, ChartType } from '../../../types/chart';
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
    }, [queryOption.table, sources, fetchSourceMapping]);

    // Tổng hợp field cho combobox từ các source đã chọn (main table only)
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
        setAllSourceFields(fields);
    }, [queryOption.table, sourceMappings, sources]);

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

                // 1. Gọi API convertQuery
                const convertRes = await chartApi.convertQuery(queryOption);
                if (!convertRes.success || !convertRes.result) {
                    throw new Error(convertRes.message || 'Failed to convert query');
                }
                const sql_query = convertRes.result;
                // 2. Chuẩn bị fields cho API previewData (chỉ lấy field_name, data_type, alias)
                const fields = (queryOption.fields || []).map(f => ({
                    field_name: f.alias && f.alias !== '' ? f.alias : f.field_name,
                    data_type: f.data_type,
                    alias: f.alias || ''
                }));
                // 3. Gọi API previewData (KHÔNG truyền group_by)
                const previewRes = await chartApi.previewData({ sql_query, fields });
                if (previewRes.success) {
                    setPreviewData(previewRes.result);
                    onPreviewData(previewRes.result);
                    onPreviewSuccess(true);
                } else {
                    throw new Error(previewRes.message || 'Failed to preview data');
                }
                return;
            }

            // Advanced mode - use new logic
            if (!sqlQuery.trim()) {
                throw new Error('SQL query is required');
            }

            // Call preview-data API directly with SQL query
            const previewRes = await chartApi.previewData({ 
                sql_query: sqlQuery, 
                fields: [] 
            });
            
            if (previewRes.success) {
                setPreviewData(previewRes.result);
                onPreviewData(previewRes.result);
                onPreviewSuccess(true);
            } else {
                throw new Error(previewRes.message || 'Failed to preview data');
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
                        const mappingLoading = mainTableSelected && !mainTableMappingLoaded;
                        // Sửa lại: chỉ disable khi chưa chọn bảng hoặc mapping đang loading
                        const disableFieldSelect = !mainTableSelected || mappingLoading;
                        return (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                                <Grid item xs={4}>
                                    <FormControl fullWidth>
                                        <InputLabel size="small">Field Name</InputLabel>
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
                                                {`Total available fields: ${allSourceFields.length}`}
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
                                        <InputLabel size="small">Field</InputLabel>
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

            {/* Sort Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Sort Order
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={() => {
                                const newSort = {
                                    field: '',
                                    direction: 'ASC' as 'ASC' | 'DESC'
                                };
                                onQueryOptionChange({
                                    ...queryOption,
                                    sort: [...(queryOption.sort || []), newSort]
                                });
                            }}
                            variant="outlined"
                            size="small"
                        >
                            Add Sort
                        </Button>
                    </Box>

                    {(queryOption.sort || []).map((sortItem, index) => (
                        <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                            <Grid item xs={6}>
                                <FormControl fullWidth>
                                    <InputLabel size="small">Field</InputLabel>
                                    <Select
                                        size="small"
                                        value={sortItem.field}
                                        onChange={(e) => {
                                            const updatedSort = [...(queryOption.sort || [])];
                                            updatedSort[index] = { ...updatedSort[index], field: e.target.value };
                                            onQueryOptionChange({
                                                ...queryOption,
                                                sort: updatedSort
                                            });
                                        }}
                                        label="Field"
                                    >
                                        {allSourceFields.map((f, i) => (
                                            <MenuItem key={i} value={f.field_name}>
                                                {`${f.source_name}.${f.field_mapping}`}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={4}>
                                <FormControl fullWidth>
                                    <InputLabel size="small">Order</InputLabel>
                                    <Select
                                        size="small"
                                        value={sortItem.direction}
                                        onChange={(e) => {
                                            const updatedSort = [...(queryOption.sort || [])];
                                            updatedSort[index] = { ...updatedSort[index], direction: e.target.value as 'ASC' | 'DESC' };
                                            onQueryOptionChange({
                                                ...queryOption,
                                                sort: updatedSort
                                            });
                                        }}
                                        label="Order"
                                    >
                                        <MenuItem value="ASC">Ascending (A-Z)</MenuItem>
                                        <MenuItem value="DESC">Descending (Z-A)</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={2}>
                                <IconButton
                                    size="small"
                                    onClick={() => {
                                        const updatedSort = (queryOption.sort || []).filter((_, i) => i !== index);
                                        onQueryOptionChange({
                                            ...queryOption,
                                            sort: updatedSort
                                        });
                                    }}
                                    color="error"
                                    sx={{ alignSelf: 'center', justifySelf: 'center' }}
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Grid>
                        </Grid>
                    ))}

                    {(!queryOption.sort || queryOption.sort.length === 0) && (
                        <Typography variant="body2" color="text.secondary" sx={{ fontStyle: 'italic' }}>
                            No sort order specified. Results will be returned in default order.
                        </Typography>
                    )}
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

            {/* HIỂN THỊ PREVIEW DATA NGAY DƯỚI NÚT */}
            {previewData && (
                <DataPreview
                    data={previewData}
                    showSuccess={true}
                />
            )}

            {chartMode === 'basic' ? renderBasicMode() : renderAdvancedMode()}
        </Box>
    );
};

export default Step2QueryBuilder; 