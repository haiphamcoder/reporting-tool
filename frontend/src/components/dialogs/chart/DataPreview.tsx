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
    if (!data || !data.columns || !data.rows) {
        return null;
    }

    const { columns, rows } = data;

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
                            {rows.slice(0, 10).map((row: any[], rowIndex: number) => (
                                <TableRow key={rowIndex}>
                                    {row.map((cell: any, cellIndex: number) => (
                                        <TableCell key={cellIndex}>
                                            {typeof cell === 'boolean' ? (
                                                <Chip 
                                                    label={cell ? 'Yes' : 'No'} 
                                                    color={cell ? 'success' : 'default'}
                                                    size="small"
                                                />
                                            ) : cell === null || cell === undefined ? (
                                                <Typography variant="body2" color="text.secondary">
                                                    null
                                                </Typography>
                                            ) : (
                                                String(cell)
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