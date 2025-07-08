import React from 'react';
import { Box, Grid, FormControl, InputLabel, Select, MenuItem, TextField, IconButton, Checkbox, FormControlLabel } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { FilterCondition, Field } from '../../../../types/chart';
import { getOperatorsForDataType } from '../../../../types/chart';

interface FilterConditionProps {
    condition: FilterCondition;
    allSourceFields: Field[];
    onChange: (condition: FilterCondition) => void;
    onDelete: () => void;
}

export const FilterConditionComponent: React.FC<FilterConditionProps> = ({ condition, allSourceFields, onChange, onDelete }) => {
    // Tự động chọn field đầu tiên khi mount nếu chưa có
    // React.useEffect(() => {
    //     if (!condition.source_field && allSourceFields.length > 0 && allSourceFields[0]) {
    //         onChange({
    //             ...condition,
    //             source_field: allSourceFields[0],
    //             operator: 'NONE',
    //             value: ''
    //             // target_field is omitted
    //         });
    //     }
    //     // eslint-disable-next-line react-hooks/exhaustive-deps
    // }, [allSourceFields]);

    const handleFieldChange = (field: Field | undefined) => {
        if (!field) return;
        onChange({
            ...condition,
            source_field: field,
            operator: '', // reset operator
            value: ''    // reset value
            // target_field is omitted
        });
    };
    const handleOperatorChange = (operator: FilterCondition['operator']) => {
        onChange({ ...condition, operator });
    };
    const handleValueChange = (value: any) => {
        onChange({ ...condition, value });
    };
    const handleCompareWithOtherField = (checked: boolean) => {
        onChange({ ...condition, compare_with_other_field: checked });
    };
    const handleTargetFieldChange = (field: Field) => {
        onChange({ ...condition, target_field: field });
    };

    // Lấy danh sách operator phù hợp với kiểu dữ liệu của trường nguồn
    const operatorOptions = React.useMemo(() => {
        const dataType = condition.source_field?.field_type || '';
        const ops = getOperatorsForDataType(dataType);
        // Luôn có option NONE đầu tiên nếu muốn xóa operator
        return [{ value: '', label: 'NONE' },
            ...ops.filter(op => op !== '').map(op => {
                // Map operator code sang label
                switch (op) {
                    case 'EQ': return { value: 'EQ', label: '=' };
                    case 'NE': return { value: 'NE', label: '≠' };
                    case 'GT': return { value: 'GT', label: '>' };
                    case 'GTE': return { value: 'GTE', label: '≥' };
                    case 'LT': return { value: 'LT', label: '<' };
                    case 'LTE': return { value: 'LTE', label: '≤' };
                    case 'LIKE': return { value: 'LIKE', label: 'LIKE' };
                    case 'IN': return { value: 'IN', label: 'IN' };
                    case 'NOT_IN': return { value: 'NOT_IN', label: 'NOT IN' };
                    case 'BETWEEN': return { value: 'BETWEEN', label: 'BETWEEN' };
                    case 'IS_NULL': return { value: 'IS_NULL', label: 'IS NULL' };
                    case 'IS_NOT_NULL': return { value: 'IS_NOT_NULL', label: 'IS NOT NULL' };
                    case 'REGEXP': return { value: 'REGEXP', label: 'REGEXP' };
                    default: return { value: op, label: op };
                }
            })
        ];
    }, [condition.source_field?.field_type]);

    return (
        <Box sx={{ mb: 1 }}>
            <Grid container spacing={1} alignItems="center">
                <Grid item xs={3}>
                    <FormControl fullWidth size="small">
                        <InputLabel>Field</InputLabel>
                        <Select
                            value={condition.source_field ? `${condition.source_field.source_id}.${condition.source_field.field_mapping}` : allSourceFields[0] ? `${allSourceFields[0].source_id}.${allSourceFields[0].field_mapping}` : ''}
                            label="Field"
                            onChange={e => {
                                const [source_id, ...fieldMappingParts] = e.target.value.split('.');
                                const field_mapping = fieldMappingParts.join('.');
                                const field = allSourceFields.find(f => f.source_id === source_id && f.field_mapping === field_mapping);
                                if (field) handleFieldChange(field);
                            }}
                        >
                            {allSourceFields.map((f, i) => (
                                <MenuItem key={i} value={`${f.source_id}.${f.field_mapping}`}>{`${f.source_name}.${f.field_mapping}`}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={2}>
                    <FormControl fullWidth size="small">
                        <InputLabel>Operator</InputLabel>
                        <Select
                            value={condition.operator}
                            label="Operator"
                            onChange={e => handleOperatorChange(e.target.value as FilterCondition['operator'])}
                        >
                            {operatorOptions.map(op => (
                                <MenuItem key={op.value} value={op.value}>{op.label}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={4}>
                    {condition.operator === 'IS_NULL' || condition.operator === 'IS_NOT_NULL' ? null : (
                        condition.compare_with_other_field ? (
                            <FormControl fullWidth size="small">
                                <InputLabel>Other Field</InputLabel>
                                <Select
                                    value={condition.target_field ? `${condition.target_field.source_id}.${condition.target_field.field_mapping}` : ''}
                                    label="Field"
                                    onChange={e => {
                                        const [source_id, ...fieldMappingParts] = e.target.value.split('.');
                                        const field_mapping = fieldMappingParts.join('.');
                                        const field = allSourceFields.find(f => f.source_id === source_id && f.field_mapping === field_mapping);
                                        if (field) handleTargetFieldChange(field);
                                    }}
                                >
                                    {allSourceFields.map((f, i) => (
                                        <MenuItem key={i} value={`${f.source_id}.${f.field_mapping}`}>{`${f.source_name}.${f.field_mapping}`}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        ) : (
                            <TextField
                                fullWidth
                                size="small"
                                label="Value"
                                value={condition.value as any}
                                onChange={e => handleValueChange(e.target.value)}
                                disabled={condition.operator === ''}
                            />
                        )
                    )}
                </Grid>
                <Grid item xs={2}>
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={condition.compare_with_other_field}
                                onChange={e => handleCompareWithOtherField(e.target.checked)}
                            />
                        }
                        label="Compare with other field"
                    />
                </Grid>
                <Grid item xs={1}>
                    <IconButton color="error" onClick={onDelete}>
                        <DeleteIcon />
                    </IconButton>
                </Grid>
            </Grid>
        </Box>
    );
};

export default FilterConditionComponent;
