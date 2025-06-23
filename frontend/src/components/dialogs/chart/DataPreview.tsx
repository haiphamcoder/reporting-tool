import React from 'react';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Chip,
    Alert
} from '@mui/material';
import { CheckCircle as SuccessIcon } from '@mui/icons-material';

interface DataPreviewProps {
    data: any;
    showSuccess?: boolean;
}

const DataPreview: React.FC<DataPreviewProps> = ({ data, showSuccess = false }) => {
    // Hỗ trợ cả kiểu cũ (columns/rows) và kiểu mới (schema/records)
    let columns: string[] = [];
    let rows: any[] = [];

    if (data) {
        if (data.columns && data.rows) {
            // Kiểu cũ
            columns = data.columns;
            rows = data.rows;
        } else if (data.schema && data.records) {
            // Kiểu mới
            columns = data.schema.filter((col: any) => !col.is_hidden).map((col: any) => col.field_name);
            rows = data.records;
        }
    }

    if (!columns.length || !rows.length) {
        return null;
    }

    return (
        <Box sx={{ mt: 2 }}>
            {showSuccess && (
                <Alert 
                    severity="success" 
                    icon={<SuccessIcon />}
                    sx={{ mb: 2 }}
                >
                    Data preview successful! Found {rows.length} rows with {columns.length} columns.
                </Alert>
            )}

            <Typography variant="subtitle1" gutterBottom>
                Data Preview
            </Typography>

            <Paper sx={{ width: '100%', overflow: 'hidden' }}>
                <TableContainer sx={{ maxHeight: 300 }}>
                    <Table stickyHeader size="small">
                        <TableHead>
                            <TableRow>
                                {columns.map((column: string, index: number) => (
                                    <TableCell 
                                        key={index}
                                        sx={{ 
                                            fontWeight: 'bold',
                                            backgroundColor: 'primary.50'
                                        }}
                                    >
                                        {column}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.slice(0, 10).map((row: any, rowIndex: number) => (
                                <TableRow key={rowIndex}>
                                    {columns.map((col, cellIndex) => (
                                        <TableCell key={cellIndex}>
                                            {typeof row[col] === 'boolean' ? (
                                                <Chip 
                                                    label={row[col] ? 'Yes' : 'No'} 
                                                    color={row[col] ? 'success' : 'default'}
                                                    size="small"
                                                />
                                            ) : row[col] === null || row[col] === undefined ? (
                                                <Typography variant="body2" color="text.secondary">
                                                    null
                                                </Typography>
                                            ) : (
                                                String(row[col])
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>

            {rows.length > 10 && (
                <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                    Showing first 10 rows of {rows.length} total rows
                </Typography>
            )}

            <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                <Chip 
                    label={`${columns.length} columns`} 
                    color="primary" 
                    variant="outlined" 
                    size="small"
                />
                <Chip 
                    label={`${rows.length} rows`} 
                    color="secondary" 
                    variant="outlined" 
                    size="small"
                />
            </Box>
        </Box>
    );
};

export default DataPreview; 