import React from 'react';
import { Box, Grid, FormControl, InputLabel, Select, MenuItem, IconButton, Typography, Button } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { FilterGroup, FilterNode, FilterCondition, Field } from '../../../../types/chart';
import FilterConditionComponent from './FilterConditionComponent';

interface FilterGroupComponentProps {
    group: FilterGroup;
    allSourceFields: Field[];
    onChange: (group: FilterGroup) => void;
    onDelete: () => void;
}

const FilterGroupComponent: React.FC<FilterGroupComponentProps> = ({ group, allSourceFields, onChange, onDelete }) => {
    // Thay đổi toán tử nhóm (AND/OR)
    const handleOpChange = (op: 'AND' | 'OR') => {
        onChange({ ...group, op });
    };

    // Thay đổi 1 node con
    const handleElementChange = (idx: number, node: FilterNode) => {
        const newElements = [...group.elements];
        newElements[idx] = node;
        onChange({ ...group, elements: newElements });
    };

    // Xóa 1 node con
    const handleElementDelete = (idx: number) => {
        const newElements = group.elements.filter((_, i) => i !== idx);
        onChange({ ...group, elements: newElements });
    };

    // Thêm điều kiện mới
    const handleAddCondition = () => {
        const newCondition: FilterCondition = {
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
        };
        onChange({ ...group, elements: [...group.elements, newCondition] });
    };

    // Thêm nhóm con mới
    const handleAddGroup = () => {
        const newGroup: FilterGroup = {
            type: 'group',
            id: Math.random().toString(36).substring(2, 10),
            op: 'AND',
            elements: []
        };
        onChange({ ...group, elements: [...group.elements, newGroup] });
    };

    return (
        <Box sx={{ border: '1px solid #ddd', borderRadius: 1, p: 2, mb: 2, background: '#fafbfc' }}>
            <Grid container alignItems="center" spacing={1} sx={{ mb: 1 }}>
                <Grid item>
                    <Typography fontWeight="bold">Group</Typography>
                </Grid>
                <Grid item>
                    <FormControl size="small">
                        <InputLabel>Operator</InputLabel>
                        <Select
                            value={group.op}
                            label="Operator"
                            onChange={e => handleOpChange(e.target.value as 'AND' | 'OR')}
                        >
                            <MenuItem value="AND">AND</MenuItem>
                            <MenuItem value="OR">OR</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item>
                    <IconButton color="error" onClick={onDelete} size="small">
                        <DeleteIcon />
                    </IconButton>
                </Grid>
            </Grid>
            <Box sx={{ ml: 2 }}>
                {group.elements.length === 0 && (
                    <Typography variant="body2" color="text.secondary" sx={{ fontStyle: 'italic', mb: 1 }}>
                        No conditions in this group.
                    </Typography>
                )}
                {group.elements.map((el, idx) => (
                    <Box key={el.id} sx={{ mb: 1 }}>
                        {el.type === 'condition' ? (
                            <FilterConditionComponent
                                condition={el as FilterCondition}
                                allSourceFields={allSourceFields}
                                onChange={cond => handleElementChange(idx, cond)}
                                onDelete={() => handleElementDelete(idx)}
                            />
                        ) : (
                            <FilterGroupComponent
                                group={el as FilterGroup}
                                allSourceFields={allSourceFields}
                                onChange={g => handleElementChange(idx, g)}
                                onDelete={() => handleElementDelete(idx)}
                            />
                        )}
                    </Box>
                ))}
                <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
                    <Button size="small" variant="outlined" startIcon={<AddIcon />} onClick={handleAddCondition}>
                        Add condition
                    </Button>
                    <Button size="small" variant="outlined" startIcon={<AddIcon />} onClick={handleAddGroup}>
                        Add group
                    </Button>
                </Box>
            </Box>
        </Box>
    );
};

export default FilterGroupComponent;
