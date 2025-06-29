import React, { useState } from 'react';
import {
    Box,
    Typography,
    Accordion,
    AccordionSummary,
    AccordionDetails,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    Chip,
    IconButton,
    Tooltip,
    Paper,
    Divider
} from '@mui/material';
import {
    ExpandMore as ExpandMoreIcon,
    TableChart as TableIcon,
    Storage as FieldIcon,
    ContentCopy as CopyIcon,
    Info as InfoIcon
} from '@mui/icons-material';

interface SqlHelperProps {
    sources: Array<{
        id: string;
        name: string;
        description?: string;
        table_name: string;
        mapping: Array<{
            field_name: string;
            field_mapping: string;
            field_type: string;
            is_hidden: boolean;
        }>;
    }>;
    onFieldClick?: (field: string) => void;
    onTableClick?: (table: string) => void;
}

const SqlHelper: React.FC<SqlHelperProps> = ({
    sources,
    onFieldClick,
    onTableClick
}) => {
    const [expanded, setExpanded] = useState<string | false>('panel1');

    const handleAccordionChange = (panel: string) => (event: React.SyntheticEvent, isExpanded: boolean) => {
        setExpanded(isExpanded ? panel : false);
    };

    const copyToClipboard = (text: string) => {
        navigator.clipboard.writeText(text);
    };

    const handleFieldClick = (field: {
        field_mapping: string;
        field_type: string;
    }, source: {
        table_name: string;
        name: string;
    }) => {
        const fieldText = `${source.table_name}.${field.field_mapping}`;
        if (onFieldClick) {
            onFieldClick(fieldText);
        }
        copyToClipboard(fieldText);
    };

    const handleTableClick = (source: {
        table_name: string;
        name: string;
    }) => {
        if (onTableClick) {
            onTableClick(source.table_name);
        }
        copyToClipboard(source.table_name);
    };

    // Calculate total fields count
    const totalFields = sources.reduce((total, source) => 
        total + (source.mapping?.filter(f => !f.is_hidden).length || 0), 0
    );

    const getDataTypeColor = (dataType: string) => {
        const type = dataType.toLowerCase();
        if (type.includes('char') || type.includes('text') || type === 'string') {
            return 'primary';
        }
        if (type.includes('int') || type.includes('float') || type.includes('double') || type === 'number' || type === 'decimal') {
            return 'success';
        }
        if (type.includes('date') || type.includes('time')) {
            return 'warning';
        }
        if (type === 'boolean' || type === 'bool') {
            return 'error';
        }
        return 'default';
    };

    return (
        <Box>
            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <InfoIcon color="primary" />
                SQL Helper
            </Typography>

            <Paper elevation={1} sx={{ mb: 2, p: 2 }}>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    💡 Click on any field or table name to copy it to clipboard
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    💡 Use Ctrl+Space in the SQL editor for autocomplete
                </Typography>
            </Paper>

            <Accordion 
                expanded={expanded === 'panel1'} 
                onChange={handleAccordionChange('panel1')}
            >
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="subtitle1" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <TableIcon color="primary" />
                        Available Tables ({sources.length})
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <List dense>
                        {sources.map((source) => (
                            <ListItem 
                                key={source.id}
                                sx={{ 
                                    cursor: 'pointer',
                                    '&:hover': { backgroundColor: 'action.hover' },
                                    borderRadius: 1,
                                    mb: 0.5
                                }}
                                onClick={() => handleTableClick(source)}
                                secondaryAction={
                                    <Tooltip title="Copy table name">
                                        <IconButton 
                                            edge="end" 
                                            size="small"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                copyToClipboard(source.table_name);
                                            }}
                                        >
                                            <CopyIcon fontSize="small" />
                                        </IconButton>
                                    </Tooltip>
                                }
                            >
                                <ListItemIcon>
                                    <TableIcon color="primary" fontSize="small" />
                                </ListItemIcon>
                                <ListItemText
                                    primary={source.table_name}
                                    secondary={source.description || 'No description'}
                                />
                            </ListItem>
                        ))}
                    </List>
                </AccordionDetails>
            </Accordion>

            <Accordion 
                expanded={expanded === 'panel2'} 
                onChange={handleAccordionChange('panel2')}
            >
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="subtitle1" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <FieldIcon color="primary" />
                        Available Fields ({totalFields})
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    {sources.map((source) => {
                        const sourceFields = source.mapping?.filter(f => !f.is_hidden) || [];
                        if (sourceFields.length === 0) return null;

                        return (
                            <Box key={source.id} sx={{ mb: 2 }}>
                                <Typography variant="subtitle2" color="primary" sx={{ mb: 1, fontWeight: 'bold' }}>
                                    {source.table_name}
                                </Typography>
                                <List dense>
                                    {sourceFields.map((field: any, index: number) => (
                                        <ListItem 
                                            key={`${source.id}-${index}`}
                                            sx={{ 
                                                cursor: 'pointer',
                                                '&:hover': { backgroundColor: 'action.hover' },
                                                borderRadius: 1,
                                                mb: 0.5,
                                                pl: 2
                                            }}
                                            onClick={() => handleFieldClick(field, source)}
                                            secondaryAction={
                                                <Tooltip title="Copy field name">
                                                    <IconButton 
                                                        edge="end" 
                                                        size="small"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            copyToClipboard(`${source.table_name}.${field.field_mapping}`);
                                                        }}
                                                    >
                                                        <CopyIcon fontSize="small" />
                                                    </IconButton>
                                                </Tooltip>
                                            }
                                        >
                                            <ListItemIcon>
                                                <FieldIcon color="secondary" fontSize="small" />
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={field.field_mapping}
                                                secondary={
                                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
                                                        <Chip 
                                                            label={field.field_type} 
                                                            size="small" 
                                                            color={getDataTypeColor(field.field_type) as any}
                                                            variant="outlined"
                                                        />
                                                    </Box>
                                                }
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                                {source !== sources[sources.length - 1] && <Divider sx={{ my: 1 }} />}
                            </Box>
                        );
                    })}
                </AccordionDetails>
            </Accordion>

            <Accordion 
                expanded={expanded === 'panel3'} 
                onChange={handleAccordionChange('panel3')}
            >
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="subtitle1" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <InfoIcon color="primary" />
                        SQL Examples
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Box>
                            <Typography variant="subtitle2" color="primary" gutterBottom>
                                Basic SELECT
                            </Typography>
                            <Paper 
                                variant="outlined" 
                                sx={{ p: 1, backgroundColor: 'grey.50', fontFamily: 'monospace', fontSize: '0.875rem' }}
                            >
                                SELECT field1, field2 FROM table_name
                            </Paper>
                        </Box>
                        
                        <Box>
                            <Typography variant="subtitle2" color="primary" gutterBottom>
                                SELECT with WHERE
                            </Typography>
                            <Paper 
                                variant="outlined" 
                                sx={{ p: 1, backgroundColor: 'grey.50', fontFamily: 'monospace', fontSize: '0.875rem' }}
                            >
                                SELECT field1, field2 FROM table_name WHERE field1 = 'value'
                            </Paper>
                        </Box>
                        
                        <Box>
                            <Typography variant="subtitle2" color="primary" gutterBottom>
                                SELECT with JOIN
                            </Typography>
                            <Paper 
                                variant="outlined" 
                                sx={{ p: 1, backgroundColor: 'grey.50', fontFamily: 'monospace', fontSize: '0.875rem' }}
                            >
                                SELECT t1.field1, t2.field2 FROM table1 t1 JOIN table2 t2 ON t1.id = t2.id
                            </Paper>
                        </Box>
                        
                        <Box>
                            <Typography variant="subtitle2" color="primary" gutterBottom>
                                SELECT with GROUP BY
                            </Typography>
                            <Paper 
                                variant="outlined" 
                                sx={{ p: 1, backgroundColor: 'grey.50', fontFamily: 'monospace', fontSize: '0.875rem' }}
                            >
                                SELECT field1, COUNT(*) FROM table_name GROUP BY field1
                            </Paper>
                        </Box>
                    </Box>
                </AccordionDetails>
            </Accordion>
        </Box>
    );
};

export default SqlHelper;