import React from 'react';
import Grid from '@mui/material/Grid2';

import {
    Box,
    Stack,
    Typography,
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

interface SourceDetailProps {
    source: {
        id?: string;
        name?: string;
        description?: string;
        connector_type?: number;
        table_name?: string;
        config?: any;
        mapping?: Array<{
            field_name: string;
            field_mapping: string;
            field_type: string;
            is_hidden: boolean;
        }>;
        status?: number;
        last_sync_time?: string;
        created_at?: string;
        modified_at?: string;
    };
    onBack: () => void;
}

const SourceDetail: React.FC<SourceDetailProps> = ({ source, onBack }) => {
    if (!source) {
        return (
            <Box sx={{ p: 2 }}>
                <Typography>No source data available</Typography>
            </Box>
        );
    }

    return (
        <Stack gap={2}>
            <Stack direction="row" alignItems="center" spacing={2}>
                <IconButton onClick={onBack} size="small">
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" component="h2">
                    {source.name || 'Unnamed Source'}
                </Typography>
            </Stack>
            <Box sx={{ p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                <Grid container spacing={2}>
                    {source.id && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">ID</Typography>
                            <Typography variant="body1">{source.id}</Typography>
                        </Grid>
                    )}
                    {source.name && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Name</Typography>
                            <Typography variant="body1">{source.name}</Typography>
                        </Grid>
                    )}
                    {source.description && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Description</Typography>
                            <Typography variant="body1">{source.description}</Typography>
                        </Grid>
                    )}
                    {source.connector_type !== undefined && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Connector Type</Typography>
                            <Typography variant="body1">{source.connector_type}</Typography>
                        </Grid>
                    )}
                    {source.table_name && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Table Name</Typography>
                            <Typography variant="body1">{source.table_name}</Typography>
                        </Grid>
                    )}
                    {source.status !== undefined && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Status</Typography>
                            <Typography variant="body1">{source.status === 1 ? 'Active' : 'Inactive'}</Typography>
                        </Grid>
                    )}
                    {source.last_sync_time && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Last Sync Time</Typography>
                            <Typography variant="body1">{new Date(source.last_sync_time).toLocaleString()}</Typography>
                        </Grid>
                    )}
                    {source.created_at && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Created At</Typography>
                            <Typography variant="body1">{new Date(source.created_at).toLocaleString()}</Typography>
                        </Grid>
                    )}
                    {source.modified_at && (
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Typography variant="subtitle2" color="text.secondary">Modified At</Typography>
                            <Typography variant="body1">{new Date(source.modified_at).toLocaleString()}</Typography>
                        </Grid>
                    )}
                </Grid>

                {source.config && (
                    <Box sx={{ mt: 3 }}>
                        <Typography variant="subtitle1" gutterBottom>Configuration</Typography>
                        <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                            <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>
                                {JSON.stringify(source.config, null, 2)}
                            </pre>
                        </Box>
                    </Box>
                )}

                {source.mapping && source.mapping.length > 0 && (
                    <Box sx={{ mt: 3 }}>
                        <Typography variant="subtitle1" gutterBottom>Field Mappings</Typography>
                        <TableContainer>
                            <Table size="small">
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Field Name</TableCell>
                                        <TableCell>Field Mapping</TableCell>
                                        <TableCell>Field Type</TableCell>
                                        <TableCell>Hidden</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {source.mapping.map((field, index) => (
                                        <TableRow key={index}>
                                            <TableCell>{field.field_name}</TableCell>
                                            <TableCell>{field.field_mapping}</TableCell>
                                            <TableCell>{field.field_type}</TableCell>
                                            <TableCell>{field.is_hidden ? 'Yes' : 'No'}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Box>
                )}
            </Box>
        </Stack>
    );
};

export default SourceDetail; 