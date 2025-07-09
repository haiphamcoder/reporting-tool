import React, { useState, useEffect, useCallback, useMemo } from 'react';
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
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from '@mui/material';
import {
    Add as AddIcon,
    Delete as DeleteIcon,
    Preview as PreviewIcon,
    Code as CodeIcon,
    Build as BuildIcon,
    TableChart as TableIcon,
    Visibility as VisibilityIcon,
} from '@mui/icons-material';
import { ChartMode, QueryOption, FieldConfig, FilterCondition, FilterNode, FilterGroup } from '../../../types/chart';
import { chartApi } from '../../../api/chart/chartApi';
import DataPreview from './DataPreview';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { tomorrow } from 'react-syntax-highlighter/dist/esm/styles/prism';
import FilterComponent from './query-builder/FilterComponent';
import { generateSqlFromQueryOption } from '../../../utils/sqlGenerator';

interface Step2QueryBuilderProps {
    chartMode: ChartMode;
    queryOption: QueryOption;
    sqlQuery: string;
    onQueryOptionChange: (queryOption: QueryOption) => void;
    onSqlQueryChange: (sqlQuery: string) => void;
    onPreviewData: (data: any) => void;
    onPreviewSuccess: (success: boolean) => void;
    onSourcesChange?: (sources: any[]) => void;
}

const Step2QueryBuilder: React.FC<Step2QueryBuilderProps> = ({
    chartMode,
    queryOption,
    sqlQuery,
    onQueryOptionChange,
    onSqlQueryChange,
    onPreviewData,
    onPreviewSuccess,
    onSourcesChange
}) => {
    const [previewLoading, setPreviewLoading] = useState(false);
    const [previewError, setPreviewError] = useState<string | null>(null);
    const [previewData, setPreviewData] = useState<any>(null);
    const [sources, setSources] = useState<any[]>([]);
    const [sourcesLoading, setSourcesLoading] = useState(false);
    const [sourceMappings, setSourceMappings] = useState<{ [sourceId: string]: any[] }>({}); // { sourceId: mapping[] }
    const [showSqlPreview, setShowSqlPreview] = useState(false);
    const [sqlPreviewText, setSqlPreviewText] = useState('');
    const [sqlPreviewLoading, setSqlPreviewLoading] = useState(false);

    const AGG_FUNCTIONS: FieldConfig['function'][] = ['SUM', 'COUNT', 'AVG', 'MIN', 'MAX'];

    // Load sources on component mount
    useEffect(() => {
        const loadSources = async () => {
            setSourcesLoading(true);
            try {
                const response = await chartApi.getSourcesList();
                if (response.success && response.data) {
                    setSources(response.data);
                    // Gọi callback để cập nhật sources lên parent
                    if (onSourcesChange) {
                        onSourcesChange(response.data);
                    }
                    // Tổng hợp tất cả các field từ field_mapping của tất cả các source, kèm source_id và source_name
                    // Removed setAllSourceFields as we now use useMemo
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
            // Cập nhật lại sources để bổ sung table_name nếu có
            if (detail.table_name) {
                setSources(prevSources => prevSources.map(src =>
                    src.id === sourceId ? { ...src, table_name: detail.table_name } : src
                ));
            }
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
        // Join tables: luôn fetch detail để cập nhật table_name
        if (queryOption.joins && queryOption.joins.length > 0) {
            queryOption.joins.forEach(join => {
                const joinSource = sources.find(s => s.id === join.table);
                if (joinSource) fetchSourceMapping(joinSource.id, joinSource.name);
            });
        }
    }, [queryOption.table, queryOption.joins, sources, fetchSourceMapping]);

    // Tổng hợp field cho combobox từ các source đã chọn (main table + join tables)
    const allSourceFields = useMemo(() => {
        let fields: any[] = [];
        // Main table
        if (queryOption.table && sourceMappings[queryOption.table] && Array.isArray(sourceMappings[queryOption.table])) {
            const mainSource = sources.find(s => s.id === queryOption.table);
            const mainPrefix = queryOption.table_alias || mainSource?.table_name || mainSource?.name || '';
            fields = fields.concat(
                (Array.isArray(sourceMappings[queryOption.table]) ? sourceMappings[queryOption.table] : []).map(f => ({
                    source_id: queryOption.table,
                    source_name: mainSource?.name || '',
                    table_name: mainSource?.table_name || mainSource?.name || '',
                    table_alias: queryOption.table_alias || '',
                    field_mapping: f?.field_mapping || '',
                    field_name: f?.field_name || '',
                    field_type: f?.field_type || '',
                    prefix: mainPrefix
                }))
            );
        }
        // Join tables
        if (queryOption.joins && queryOption.joins.length > 0) {
            queryOption.joins.forEach(join => {
                if (join.table && sourceMappings[join.table] && Array.isArray(sourceMappings[join.table])) {
                    const joinSource = sources.find(s => s.id === join.table);
                    const joinPrefix = join.table_alias || joinSource?.table_name || joinSource?.name || '';
                    fields = fields.concat(
                        (Array.isArray(sourceMappings[join.table]) ? sourceMappings[join.table] : []).map(f => ({
                            source_id: join.table,
                            source_name: joinSource?.name || '',
                            table_name: joinSource?.table_name || joinSource?.name || '',
                            table_alias: join.table_alias || '',
                            field_mapping: f?.field_mapping || '',
                            field_name: f?.field_name || '',
                            field_type: f?.field_type || '',
                            prefix: joinPrefix
                        }))
                    );
                }
            });
        }
        return fields;
    }, [queryOption.table, queryOption.table_alias, queryOption.joins, sourceMappings, sources]);

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

                // 1. Sinh SQL từ frontend
                let sql_query = '';
                try {
                    sql_query = generateSqlFromQueryOption(queryOption, sources);
                    // Set SQL query toàn cục để sử dụng ở step3
                    onSqlQueryChange(sql_query);
                } catch (frontendError) {
                    throw new Error('Failed to generate SQL from query options.');
                }
                // 2. Chuẩn bị fields cho API previewData (xử lý đúng cho field function)
                const previewFields = (queryOption.fields || []).map(f => {
                    if (f.alias && f.alias !== '') {
                        return {
                            field_name: f.alias,
                            data_type: f.data_type,
                            alias: f.alias
                        };
                    } else if (f.function && f.field_mapping && f.source_id) {
                        // Lấy prefix đúng
                        const src = sources.find(s => s.id === f.source_id);
                        const prefix = (queryOption.table_alias && src && src.id === queryOption.table) ? queryOption.table_alias : (src?.table_name || src?.name || '');
                        return {
                            field_name: `${f.function}(${prefix}.${f.field_mapping})`,
                            data_type: f.data_type,
                            alias: ''
                        };
                    } else {
                        return {
                            field_name: f.field_name,
                            data_type: f.data_type,
                            alias: ''
                        };
                    }
                });
                // 3. Gọi API previewData (KHÔNG truyền group_by)
                const previewDataRes = await chartApi.previewData({ sql_query, fields: previewFields });
                if (previewDataRes.success) {
                    setPreviewData(previewDataRes.result);
                    onPreviewData(previewDataRes.result);
                    onPreviewSuccess(true);
                } else {
                    throw new Error(previewDataRes.message || 'Failed to preview data');
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

    const handlePreviewSql = async () => {
        setSqlPreviewLoading(true);
        try {
            if (chartMode === 'basic') {
                if (!queryOption.table) {
                    throw new Error('Please select a main table');
                }
                if (!queryOption.fields || queryOption.fields.length === 0) {
                    throw new Error('Please add at least one field');
                }
                // Chỉ kiểm tra các bảng đã được chọn (có id)
                const allTableIds = [queryOption.table, ...(queryOption.joins || []).map(j => j.table).filter(Boolean)];
                const allReady = allTableIds.every(id => {
                    const src = sources.find(s => s.id === id);
                    return !!(src && src.table_name);
                });
                if (!allReady) {
                    setSqlPreviewText('Đang tải thông tin bảng, vui lòng thử lại sau 1-2 giây...');
                    setShowSqlPreview(true);
                    setSqlPreviewLoading(false);
                    return;
                }
                // Sinh SQL từ frontend trước
                let sql = '';
                try {
                    sql = generateSqlFromQueryOption(queryOption, sources);
                    // Set SQL query toàn cục để sử dụng ở step3
                    onSqlQueryChange(sql);
                } catch (frontendError) {
                    console.warn('Frontend SQL generation failed, falling back to API:', frontendError);
                    // Fallback: gọi API nếu frontend generation thất bại
                    const convertRes = await chartApi.convertQuery(queryOption);
                    if (!convertRes.success || !convertRes.result) {
                        throw new Error(convertRes.message || 'Failed to convert query');
                    }
                    sql = convertRes.result;
                    // Set SQL query toàn cục từ API result
                    onSqlQueryChange(sql);
                }
                const formattedSql = formatSql(sql);
                setSqlPreviewText(formattedSql);
                setShowSqlPreview(true);
            } else {
                // Advanced mode - show the SQL directly
                const formattedSql = formatSql(sqlQuery);
                setSqlPreviewText(formattedSql);
                setShowSqlPreview(true);
            }
        } catch (error) {
            console.error('SQL Preview error:', error);
            setPreviewError(error instanceof Error ? error.message : 'Failed to generate SQL');
        } finally {
            setSqlPreviewLoading(false);
        }
    };

    // Hàm format SQL để hiển thị đẹp hơn
    const formatSql = (sql: string): string => {
        if (!sql) return 'No SQL generated';

        // Thêm line breaks sau các keywords chính
        let formatted = sql
            .replace(/\bSELECT\b/gi, '\nSELECT')
            .replace(/\bFROM\b/gi, '\nFROM')
            .replace(/\bWHERE\b/gi, '\nWHERE')
            .replace(/\bGROUP BY\b/gi, '\nGROUP BY')
            .replace(/\bORDER BY\b/gi, '\nORDER BY')
            .replace(/\bHAVING\b/gi, '\nHAVING')
            .replace(/\bLIMIT\b/gi, '\nLIMIT')
            .replace(/\bOFFSET\b/gi, '\nOFFSET')
            .replace(/\bJOIN\b/gi, '\nJOIN')
            .replace(/\bLEFT JOIN\b/gi, '\nLEFT JOIN')
            .replace(/\bRIGHT JOIN\b/gi, '\nRIGHT JOIN')
            .replace(/\bINNER JOIN\b/gi, '\nINNER JOIN')
            .replace(/\bCROSS JOIN\b/gi, '\nCROSS JOIN')
            .replace(/\bON\b/gi, '\n  ON')
            .replace(/\bAND\b/gi, '\n  AND')
            .replace(/\bOR\b/gi, '\n  OR');

        // Thêm indentation cho các điều kiện
        formatted = formatted.replace(/\n\s*ON\s+/g, '\n  ON ');
        formatted = formatted.replace(/\n\s*AND\s+/g, '\n  AND ');
        formatted = formatted.replace(/\n\s*OR\s+/g, '\n  OR ');

        // Loại bỏ line break đầu tiên nếu có
        formatted = formatted.replace(/^\n+/, '');

        return formatted;
    };

    function filterConditionNodeToSql(node: FilterCondition): string {
        if (node.source_field === undefined) return '';
        const field = node.source_field;
        const fieldSql = `${field.table_alias || field.table_name}.${field.field_mapping}`;
        const operator = (() => {
            switch (node.operator) {
                case 'EQ': return '=';
                case 'NE': return '!=';
                case 'GT': return '>';
                case 'GTE': return '>=';
                case 'LT': return '<';
                case 'LTE': return '<=';
                case 'LIKE': return 'LIKE';
                case 'IN': return 'IN';
                case 'NOT_IN': return 'NOT IN';
                case 'BETWEEN': return 'BETWEEN';
                case 'IS_NULL': return 'IS NULL';
                case 'IS_NOT_NULL': return 'IS NOT NULL';
                case 'REGEXP': return 'REGEXP';
                default: return '';
            }
        })();
        if (operator === '') return '';
        if (node.compare_with_other_field) {
            if (node.target_field === undefined) return '';
            const targetField = node.target_field;
            const targetFieldSql = `${targetField.table_alias || targetField.table_name}.${targetField.field_mapping}`;
            return `${fieldSql} ${operator} ${targetFieldSql}`;
        }
        const valueSql = node.value;
        return `${fieldSql} ${operator} '${valueSql}'`;
    }

    function filterGroupNodeToSql(node: FilterGroup): string {
        if (node.elements.length === 0) return '';
        const conditions = node.elements.map(filterNodeToSql);
        return `(${conditions.join(` ${node.op} `)})`;
    }

    function filterNodeToSql(node: FilterNode): string {
        if (!node) return '';
        if (node.type === 'condition') {
            return filterConditionNodeToSql(node);
        }
        else if (node.type === 'group') {
            return filterGroupNodeToSql(node);
        }
        return '';
    }

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
                field_mapping: found.field_mapping,
                table_name: found.table_name,
                table_alias: found.table_alias
            });
        }
    };

    // Lấy danh sách function+field đã có trong SELECT (có function, chỉ lấy function hợp lệ cho HAVING)
    const HAVING_FUNCTIONS = ['SUM', 'COUNT', 'AVG', 'MIN', 'MAX'] as const;
    const selectAggFields = (queryOption.fields || []).filter(f => HAVING_FUNCTIONS.includes(f.function as any) && f.field_mapping);

    // Khi SELECT thay đổi, loại bỏ các HAVING không còn hợp lệ
    useEffect(() => {
        if (!queryOption.having || queryOption.having.length === 0) return;
        const validHaving = (queryOption.having || []).filter(having =>
            selectAggFields.some(f => f.function === having.function && f.field_mapping === having.field)
        );
        if (validHaving.length !== (queryOption.having || []).length) {
            onQueryOptionChange({ ...queryOption, having: validHaving });
        }
    }, [queryOption.fields]);

    // Lấy danh sách các trường đã có trong SELECT (bao gồm cả aggregate và non-aggregate)
    const selectFieldsForSort = (queryOption.fields || []).filter(f => f.field_mapping).map(f => {
        let prefix = '';
        if (f.source_id) {
            const src = sources.find(s => s.id === f.source_id);
            prefix = (queryOption.table_alias && src && src.id === queryOption.table) ? queryOption.table_alias : (src?.table_name || src?.name || '');
        }
        return { ...f, _sortKey: f.alias && f.alias.trim() ? f.alias : `${prefix ? prefix + '.' : ''}${f.field_mapping}` };
    });
    const usedSortFields = (queryOption.sort || []).map(s => s.field);

    // Helper function để lấy thông tin field từ source_id và field_mapping
    const getFieldInfo = (sourceId: string, fieldMapping: string) => {
        const found = allSourceFields.find(f => f.source_id === sourceId && f.field_mapping === fieldMapping);
        return found || null;
    };

    // Thêm hàm tạo FilterCondition mặc định
    const getDefaultFilterCondition = (): FilterCondition => ({
        type: 'condition',
        id: Math.random().toString(36).substring(2, 10),
        operator: '',
        value: '',
        source_field: allSourceFields[0] || {
            source_id: '',
            source_name: '',
            field_mapping: '',
            data_type: '',
            alias: ''
        },
        compare_with_other_field: false,
        target_field: allSourceFields[0] || {
            source_id: '',
            source_name: '',
            field_mapping: '',
            data_type: '',
            alias: ''
        }
    });

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

            {/* Join Tables Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Join Tables
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={() => {
                                const newJoin: import('../../../types/chart').JoinConfig = {
                                    table: '',
                                    table_alias: '',
                                    type: 'INNER',
                                    conditions: [
                                        {
                                            left_table: queryOption.table || '',
                                            left_field: '',
                                            right_table: '',  // Sẽ được set khi chọn join table
                                            right_field: '',
                                            operator: 'EQ'
                                        }
                                    ]
                                };
                                onQueryOptionChange({
                                    ...queryOption,
                                    joins: [...(queryOption.joins || []), newJoin]
                                });
                            }}
                            variant="outlined"
                            size="small"
                        >
                            Add Join
                        </Button>
                    </Box>
                    {(queryOption.joins || []).map((join, joinIdx) => (
                        <Box key={joinIdx} sx={{ mb: 2, border: '1px solid #eee', borderRadius: 1, p: 2 }}>
                            <Grid container spacing={2} alignItems="center">
                                <Grid item xs={3}>
                                    <FormControl fullWidth>
                                        <InputLabel size="small">Join Table</InputLabel>
                                        <Select
                                            size="small"
                                            value={join.table}
                                            label="Join Table"
                                            onChange={e => {
                                                const updatedJoins = [...(queryOption.joins || [])];
                                                const selectedTableId = e.target.value;
                                                updatedJoins[joinIdx].table = selectedTableId;
                                                // Cập nhật right_table trong condition khi chọn join table
                                                if (updatedJoins[joinIdx].conditions && updatedJoins[joinIdx].conditions.length > 0) {
                                                    updatedJoins[joinIdx].conditions[0].right_table = selectedTableId;
                                                    // Reset right field khi thay đổi table
                                                    updatedJoins[joinIdx].conditions[0].right_field = '';
                                                    updatedJoins[joinIdx].conditions[0].right_table_name = '';
                                                    updatedJoins[joinIdx].conditions[0].right_table_alias = '';
                                                }
                                                onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                            }}
                                        >
                                            {sources.filter(s => s.id !== queryOption.table).map((source) => (
                                                <MenuItem key={source.id} value={source.id}>{source.name}</MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={2}>
                                    <TextField
                                        fullWidth
                                        size="small"
                                        label="Alias"
                                        value={join.table_alias}
                                        onChange={e => {
                                            const updatedJoins = [...(queryOption.joins || [])];
                                            updatedJoins[joinIdx].table_alias = e.target.value;
                                            onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                        }}
                                    />
                                </Grid>
                                <Grid item xs={2}>
                                    <FormControl fullWidth>
                                        <InputLabel size="small">Type</InputLabel>
                                        <Select
                                            size="small"
                                            value={join.type as 'INNER' | 'LEFT' | 'RIGHT' | 'CROSS' | 'NATURAL_LEFT' | 'NATURAL_RIGHT'}
                                            label="Type"
                                            onChange={e => {
                                                const updatedJoins = [...(queryOption.joins || [])];
                                                updatedJoins[joinIdx].type = e.target.value as 'INNER' | 'LEFT' | 'RIGHT' | 'CROSS' | 'NATURAL_LEFT' | 'NATURAL_RIGHT';
                                                onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                            }}
                                        >
                                            <MenuItem value="INNER">INNER</MenuItem>
                                            <MenuItem value="LEFT">LEFT</MenuItem>
                                            <MenuItem value="RIGHT">RIGHT</MenuItem>
                                            <MenuItem value="CROSS">CROSS</MenuItem>
                                            <MenuItem value="NATURAL_LEFT">NATURAL LEFT</MenuItem>
                                            <MenuItem value="NATURAL_RIGHT">NATURAL RIGHT</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={4}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <Typography variant="body2">ON</Typography>
                                        {/* Chỉ hỗ trợ 1 điều kiện ON cho mỗi join ở UI cơ bản */}
                                        <FormControl size="small" sx={{ minWidth: 100 }}>
                                            <InputLabel>Left Field</InputLabel>
                                            <Select
                                                size="small"
                                                value={join.conditions[0]?.left_field || ''}
                                                label="Left Field"
                                                onChange={e => {
                                                    const updatedJoins = [...(queryOption.joins || [])];
                                                    const fieldValue = e.target.value;
                                                    updatedJoins[joinIdx].conditions[0].left_field = fieldValue;
                                                    
                                                    // Lưu thông tin table_name và table_alias cho left field
                                                    if (fieldValue && join.conditions[0]?.left_table) {
                                                        const fieldInfo = getFieldInfo(join.conditions[0].left_table, fieldValue);
                                                        if (fieldInfo) {
                                                            updatedJoins[joinIdx].conditions[0].left_table_name = fieldInfo.table_name;
                                                            updatedJoins[joinIdx].conditions[0].left_table_alias = fieldInfo.table_alias;
                                                        }
                                                    }
                                                    
                                                    onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                                }}
                                            >
                                                {/* Field của main table */}
                                                {(queryOption.table && Array.isArray(sourceMappings[queryOption.table]) ? sourceMappings[queryOption.table] : []).map((f: any, i: number) => (
                                                    <MenuItem key={i} value={f?.field_mapping || ''}>{f?.field_mapping || ''}</MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                        <FormControl size="small" sx={{ minWidth: 80 }}>
                                            <InputLabel>Op</InputLabel>
                                            <Select
                                                size="small"
                                                value={join.conditions[0]?.operator as 'EQ' | 'NE' | 'GT' | 'GTE' | 'LT' | 'LTE' || 'EQ'}
                                                label="Op"
                                                onChange={e => {
                                                    const updatedJoins = [...(queryOption.joins || [])];
                                                    updatedJoins[joinIdx].conditions[0].operator = e.target.value as 'EQ' | 'NE' | 'GT' | 'GTE' | 'LT' | 'LTE';
                                                    onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                                }}
                                            >
                                                <MenuItem value="EQ">=</MenuItem>
                                                <MenuItem value="NE">≠</MenuItem>
                                                <MenuItem value="GT">&gt;</MenuItem>
                                                <MenuItem value="GTE">≥</MenuItem>
                                                <MenuItem value="LT">&lt;</MenuItem>
                                                <MenuItem value="LTE">≤</MenuItem>
                                            </Select>
                                        </FormControl>
                                        <FormControl size="small" sx={{ minWidth: 100 }}>
                                            <InputLabel>Right Field</InputLabel>
                                            <Select
                                                size="small"
                                                value={join.conditions[0]?.right_field || ''}
                                                label="Right Field"
                                                onChange={e => {
                                                    const updatedJoins = [...(queryOption.joins || [])];
                                                    const fieldValue = e.target.value;
                                                    updatedJoins[joinIdx].conditions[0].right_field = fieldValue;
                                                    
                                                    // Lưu thông tin table_name và table_alias cho right field
                                                    if (fieldValue && join.conditions[0]?.right_table) {
                                                        const fieldInfo = getFieldInfo(join.conditions[0].right_table, fieldValue);
                                                        if (fieldInfo) {
                                                            updatedJoins[joinIdx].conditions[0].right_table_name = fieldInfo.table_name;
                                                            updatedJoins[joinIdx].conditions[0].right_table_alias = fieldInfo.table_alias;
                                                        }
                                                    }
                                                    
                                                    onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                                }}
                                            >
                                                {/* Field của join table */}
                                                {(join.table && Array.isArray(sourceMappings[join.table]) ? sourceMappings[join.table] : []).map((f: any, i: number) => (
                                                    <MenuItem key={i} value={f?.field_mapping || ''}>{f?.field_mapping || ''}</MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                        <IconButton
                                            size="small"
                                            color="error"
                                            onClick={() => {
                                                const updatedJoins = (queryOption.joins || []).filter((_, i) => i !== joinIdx);
                                                onQueryOptionChange({ ...queryOption, joins: updatedJoins });
                                            }}
                                        >
                                            <DeleteIcon />
                                        </IconButton>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Box>
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
                        const mappingLoading = mainTableSelected && !mainTableMappingLoaded;
                        // Sửa lại: chỉ disable khi chưa chọn bảng hoặc mapping đang loading
                        const disableFieldSelect = !mainTableSelected || mappingLoading;
                        return (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }} alignItems="center">
                                <Grid item xs={3}>
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
                                    </FormControl>
                                </Grid>
                                <Grid item xs={2}>
                                    <FormControl fullWidth>
                                        <InputLabel size="small">Function</InputLabel>
                                        <Select
                                            size="small"
                                            value={field.function || ''}
                                            label="Function"
                                            onChange={e => handleUpdateField(index, { function: e.target.value === '' ? undefined : e.target.value as FieldConfig['function'] })}
                                        >
                                            <MenuItem value="">None</MenuItem>
                                            {AGG_FUNCTIONS.map(fn => (
                                                <MenuItem key={fn} value={fn}>{fn}</MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={2}>
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

            {/* Filter Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Filter
                        </Typography>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                            <Button
                                startIcon={<AddIcon />}
                                variant="outlined"
                                size="small"
                                onClick={() => {
                                    // Nếu chưa có filter node
                                    if (!queryOption.filters) {
                                        const newCond = getDefaultFilterCondition();
                                        onQueryOptionChange({
                                            ...queryOption,
                                            filters: newCond
                                        });
                                    } else if (queryOption.filters.type === 'condition') {
                                        // Nếu đã có 1 condition, wrap thành group
                                        const oldCond = queryOption.filters;
                                        const newCond = getDefaultFilterCondition();
                                        const newGroup = {
                                            type: 'group' as const,
                                            id: Math.random().toString(36).substring(2, 10),
                                            op: 'AND' as const,
                                            elements: [oldCond, newCond]
                                        };
                                        onQueryOptionChange({
                                            ...queryOption,
                                            filters: newGroup
                                        });
                                    } else if (queryOption.filters.type === 'group') {
                                        // Nếu đã là group, lồng group cũ và condition mới vào group mới
                                        const oldGroup = queryOption.filters;
                                        const newCond = getDefaultFilterCondition();
                                        const newGroup = {
                                            type: 'group' as const,
                                            id: Math.random().toString(36).substring(2, 10),
                                            op: 'AND' as const,
                                            elements: [oldGroup, newCond]
                                        };
                                        onQueryOptionChange({
                                            ...queryOption,
                                            filters: newGroup
                                        });
                                    }
                                }}
                            >
                                Add Condition
                            </Button>
                            <Button
                                startIcon={<AddIcon />}
                                variant="outlined"
                                size="small"
                                onClick={() => {
                                    // Nếu chưa có filter node, tạo group rỗng
                                    if (!queryOption.filters) {
                                        const newGroup = {
                                            type: 'group' as const,
                                            id: Math.random().toString(36).substring(2, 10),
                                            op: 'AND' as const,
                                            elements: []
                                        };
                                        onQueryOptionChange({
                                            ...queryOption,
                                            filters: newGroup
                                        });
                                    } else {
                                        // Nếu đã có node, lồng node hiện tại và một group mới vào group mới khác
                                        const oldNode = queryOption.filters;
                                        const emptyGroup = {
                                            type: 'group' as const,
                                            id: Math.random().toString(36).substring(2, 10),
                                            op: 'AND' as const,
                                            elements: []
                                        };
                                        const newGroup = {
                                            type: 'group' as const,
                                            id: Math.random().toString(36).substring(2, 10),
                                            op: 'AND' as const,
                                            elements: [oldNode, emptyGroup]
                                        };
                                        onQueryOptionChange({
                                            ...queryOption,
                                            filters: newGroup
                                        });
                                    }
                                }}
                            >
                                Add Group
                            </Button>
                        </Box>
                    </Box>
                    {queryOption.filters ? (
                        <FilterComponent
                            node={queryOption.filters}
                            allSourceFields={allSourceFields}
                            onChange={node => onQueryOptionChange({ ...queryOption, filters: node })}
                            onDelete={() => onQueryOptionChange({ ...queryOption, filters: undefined })}
                        />
                    ) : (
                        <Typography variant="body2" color="text.secondary" sx={{ fontStyle: 'italic' }}>
                            No filter condition. Click 'Add Condition' to start.
                        </Typography>
                    )}
                </CardContent>
            </Card>

            {/* Group By Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Group By
                        </Typography>
                    </Box>
                    {(() => {
                        // Kiểm tra có ít nhất một field có function không
                        const hasAgg = (queryOption.fields || []).some(f => !!f.function);
                        // Tìm các trường không dùng function trong SELECT, chỉ auto-add nếu có field function
                        const selectNonAggFields = hasAgg
                            ? (queryOption.fields || [])
                                .filter(f => !f.function && f.field_mapping && f.source_id)
                                .map(f => {
                                    const src = sources.find(s => s.id === f.source_id);
                                    const prefix = queryOption.table_alias && src && src.id === queryOption.table ? queryOption.table_alias : (src?.table_name || src?.name || '');
                                    return `${prefix}.${f.field_mapping}`;
                                })
                            : [];
                        // Các trường user chọn thêm
                        const userGroupBy = (queryOption.group_by || []).filter(key => !selectNonAggFields.includes(key));
                        // Tổng hợp group by hiển thị
                        const groupByValue = [...selectNonAggFields, ...userGroupBy];
                        return (
                            <FormControl fullWidth>
                                <InputLabel size="small">Fields</InputLabel>
                                <Select
                                    multiple
                                    size="small"
                                    value={groupByValue}
                                    onChange={e => {
                                        const value = e.target.value as string[];
                                        // Không cho bỏ các trường bắt buộc (selectNonAggFields)
                                        const filtered = value.filter(v => !selectNonAggFields.includes(v));
                                        onQueryOptionChange({
                                            ...queryOption,
                                            group_by: filtered
                                        });
                                    }}
                                    label="Fields"
                                    renderValue={selected => (selected as string[]).map(key => {
                                        const f = allSourceFields.find(f => `${f.prefix}.${f.field_mapping}` === key);
                                        return f ? `${f.source_name}.${f.field_mapping}` : key;
                                    }).join(', ')}
                                >
                                    {allSourceFields.map((f, i) => {
                                        const key = `${f.prefix}.${f.field_mapping}`;
                                        const isRequired = selectNonAggFields.includes(key);
                                        return (
                                            <MenuItem key={i} value={key} disabled={isRequired}>
                                                {`${f.source_name}.${f.field_mapping}`}
                                                {isRequired && (
                                                    <Typography component="span" color="primary" sx={{ ml: 1, fontSize: 12 }}>
                                                        (auto)
                                                    </Typography>
                                                )}
                                            </MenuItem>
                                        );
                                    })}
                                </Select>
                            </FormControl>
                        );
                    })()}
                </CardContent>
            </Card>

            {/* Having Section */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Typography variant="subtitle1" fontWeight="medium">
                            Having
                        </Typography>
                        <Button
                            startIcon={<AddIcon />}
                            onClick={() => {
                                // Gợi ý function+field đầu tiên hợp lệ
                                const firstAgg = selectAggFields[0];
                                const newHaving = {
                                    function: (firstAgg?.function || 'SUM') as 'SUM' | 'COUNT' | 'AVG' | 'MIN' | 'MAX',
                                    field: firstAgg?.field_mapping || '',
                                    source_id: firstAgg?.source_id || '',
                                    source_name: firstAgg?.source_name || '',
                                    table_name: firstAgg?.table_name || '',
                                    table_alias: firstAgg?.table_alias || '',
                                    operator: 'GT' as 'GT',
                                    value: ''
                                };
                                onQueryOptionChange({
                                    ...queryOption,
                                    having: [...(queryOption.having || []), newHaving]
                                });
                            }}
                            variant="outlined"
                            size="small"
                            disabled={selectAggFields.length === 0}
                        >
                            Add Having
                        </Button>
                    </Box>
                    {(queryOption.having || []).map((having, index) => {
                        const havingDisabled = selectAggFields.length === 0;
                        // Lấy danh sách function+field hợp lệ
                        const validAggFields = selectAggFields;
                        return (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }} alignItems="center">
                                <Grid item xs={2}>
                                    <FormControl fullWidth disabled={havingDisabled}>
                                        <InputLabel size="small">Function</InputLabel>
                                        <Select
                                            size="small"
                                            value={having.function || ''}
                                            label="Function"
                                            onChange={e => {
                                                const updated = [...(queryOption.having || [])];
                                                updated[index].function = e.target.value as any;
                                                // Nếu field hiện tại không còn hợp lệ với function mới, tự động chọn field đầu tiên hợp lệ
                                                const validFields = validAggFields.filter(f => f.function === e.target.value);
                                                if (!validFields.some(f => f.field_mapping === updated[index].field)) {
                                                    updated[index].field = validFields[0]?.field_mapping || '';
                                                }
                                                onQueryOptionChange({ ...queryOption, having: updated });
                                            }}
                                        >
                                            {Array.from(new Set(validAggFields.map(f => f.function)).values()).map(fn => (
                                                <MenuItem key={fn} value={fn}>{fn}</MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={3}>
                                    <FormControl fullWidth disabled={havingDisabled}>
                                        <InputLabel size="small">Field</InputLabel>
                                        <Select
                                            size="small"
                                            value={having.field || ''}
                                            label="Field"
                                            onChange={e => {
                                                const updated = [...(queryOption.having || [])];
                                                const selectedField = validAggFields.find(f => f.function === having.function && f.field_mapping === e.target.value);
                                                if (selectedField) {
                                                    updated[index].field = selectedField.field_mapping || '';
                                                    updated[index].source_id = selectedField.source_id || '';
                                                    updated[index].source_name = selectedField.source_name || '';
                                                    updated[index].table_name = selectedField.table_name || '';
                                                    updated[index].table_alias = selectedField.table_alias || '';
                                                }
                                                onQueryOptionChange({ ...queryOption, having: updated });
                                            }}
                                        >
                                            {validAggFields.filter(f => f.function === having.function).map((f, i) => (
                                                <MenuItem key={i} value={f.field_mapping}>{`${f.source_name}.${f.field_mapping}`}</MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={2}>
                                    <FormControl fullWidth disabled={havingDisabled}>
                                        <InputLabel size="small">Operator</InputLabel>
                                        <Select
                                            size="small"
                                            value={having.operator}
                                            label="Operator"
                                            onChange={e => {
                                                const updated = [...(queryOption.having || [])];
                                                updated[index].operator = e.target.value as any;
                                                onQueryOptionChange({ ...queryOption, having: updated });
                                            }}
                                        >
                                            <MenuItem value="EQ">=</MenuItem>
                                            <MenuItem value="NE">≠</MenuItem>
                                            <MenuItem value="GT">&gt;</MenuItem>
                                            <MenuItem value="GTE">≥</MenuItem>
                                            <MenuItem value="LT">&lt;</MenuItem>
                                            <MenuItem value="LTE">≤</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={3}>
                                    <TextField
                                        fullWidth
                                        size="small"
                                        label="Value"
                                        value={having.value}
                                        onChange={e => {
                                            const updated = [...(queryOption.having || [])];
                                            updated[index].value = e.target.value;
                                            onQueryOptionChange({ ...queryOption, having: updated });
                                        }}
                                        placeholder="Value"
                                        disabled={havingDisabled}
                                    />
                                </Grid>
                                <Grid item xs={2}>
                                    <IconButton
                                        size="small"
                                        onClick={() => {
                                            const updated = (queryOption.having || []).filter((_, i) => i !== index);
                                            onQueryOptionChange({ ...queryOption, having: updated });
                                        }}
                                        color="error"
                                        sx={{ alignSelf: 'center', justifySelf: 'center' }}
                                        disabled={havingDisabled}
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
                                // Gợi ý field đầu tiên chưa được sort
                                const firstField = selectFieldsForSort.find(f => f.field_mapping && !usedSortFields.includes(f.field_mapping));
                                if (!firstField) return;
                                onQueryOptionChange({
                                    ...queryOption,
                                    sort: [...(queryOption.sort || []), { 
                                        field: firstField.field_mapping || '',
                                        source_id: firstField.source_id || '',
                                        source_name: firstField.source_name || '',
                                        table_name: firstField.table_name || '',
                                        table_alias: firstField.table_alias || '',
                                        direction: 'ASC' 
                                    }]
                                });
                            }}
                            variant="outlined"
                            size="small"
                            disabled={selectFieldsForSort.length === 0 || usedSortFields.length >= selectFieldsForSort.length}
                        >
                            Add Sort
                        </Button>
                    </Box>
                    {(queryOption.sort || []).map((sort, index) => {
                        // Danh sách field chưa được chọn ở các dòng khác + field hiện tại
                        const availableFields = selectFieldsForSort.filter(f => {
                            return !f.field_mapping || !usedSortFields.includes(f.field_mapping) || sort.field === f.field_mapping;
                        });
                        return (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }} alignItems="center">
                                <Grid item xs={6}>
                                    <FormControl fullWidth>
                                        <InputLabel size="small">Field</InputLabel>
                                        <Select
                                            size="small"
                                            value={sort.field}
                                            label="Field"
                                            onChange={e => {
                                                const updated = [...(queryOption.sort || [])];
                                                const selectedField = availableFields.find(f => f.field_mapping === e.target.value);
                                                if (selectedField) {
                                                    updated[index].field = selectedField.field_mapping || '';
                                                    updated[index].source_id = selectedField.source_id || '';
                                                    updated[index].source_name = selectedField.source_name || '';
                                                    updated[index].table_name = selectedField.table_name || '';
                                                    updated[index].table_alias = selectedField.table_alias || '';
                                                }
                                                onQueryOptionChange({ ...queryOption, sort: updated });
                                            }}
                                        >
                                            {availableFields.map((f, i) => {
                                                return (
                                                    <MenuItem key={i} value={f.field_mapping}>
                                                        {f.alias && f.alias.trim() ? f.alias : `${f.source_name}.${f.field_mapping}`}
                                                    </MenuItem>
                                                );
                                            })}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={4}>
                                    <FormControl fullWidth>
                                        <InputLabel size="small">Direction</InputLabel>
                                        <Select
                                            size="small"
                                            value={sort.direction}
                                            label="Direction"
                                            onChange={e => {
                                                const updated = [...(queryOption.sort || [])];
                                                updated[index].direction = e.target.value as 'ASC' | 'DESC';
                                                onQueryOptionChange({ ...queryOption, sort: updated });
                                            }}
                                        >
                                            <MenuItem value="ASC">ASC</MenuItem>
                                            <MenuItem value="DESC">DESC</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={2}>
                                    <IconButton
                                        size="small"
                                        onClick={() => {
                                            const updated = (queryOption.sort || []).filter((_, i) => i !== index);
                                            onQueryOptionChange({ ...queryOption, sort: updated });
                                        }}
                                        color="error"
                                        sx={{ alignSelf: 'center', justifySelf: 'center' }}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </Grid>
                            </Grid>
                        );
                    })}

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

                <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button
                        variant="outlined"
                        startIcon={sqlPreviewLoading ? <CircularProgress size={20} /> : <VisibilityIcon />}
                        onClick={handlePreviewSql}
                        disabled={sqlPreviewLoading}
                    >
                        {sqlPreviewLoading ? 'Generating...' : 'Preview SQL'}
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={previewLoading ? <CircularProgress size={20} /> : <PreviewIcon />}
                        onClick={handlePreview}
                        disabled={previewLoading}
                    >
                        {previewLoading ? 'Previewing...' : 'Preview Data'}
                    </Button>
                </Box>
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

            {/* SQL Preview Dialog */}
            <Dialog
                open={showSqlPreview}
                onClose={() => setShowSqlPreview(false)}
                maxWidth="md"
                fullWidth
            >
                <DialogTitle>
                    Generated SQL Query
                </DialogTitle>
                <DialogContent>
                    <SyntaxHighlighter
                        language="sql"
                        style={tomorrow}
                        customStyle={{
                            margin: 0,
                            borderRadius: '4px',
                            fontSize: '14px',
                            maxHeight: '400px',
                            overflow: 'auto'
                        }}
                        showLineNumbers={false}
                    >
                        {sqlPreviewText || 'No SQL generated'}
                    </SyntaxHighlighter>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={() => {
                            navigator.clipboard.writeText(sqlPreviewText);
                        }}
                        variant="outlined"
                    >
                        Copy SQL
                    </Button>
                    <Button onClick={() => setShowSqlPreview(false)} variant="contained">
                        Close
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default Step2QueryBuilder; 