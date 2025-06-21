import { useNavigate, useParams } from 'react-router-dom';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { SourceDetail } from '../types/source';
import { useState, useEffect } from 'react';
import Grid from '@mui/material/Grid';
import ImageIcon from '@mui/icons-material/Image';
import TableChartIcon from '@mui/icons-material/TableChart';
import StorageIcon from '@mui/icons-material/Storage';
import GoogleIcon from '@mui/icons-material/Google';
import connectorCsvIcon from '../assets/connector-csv.png';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import CircularProgress from '@mui/material/CircularProgress';
import { API_CONFIG } from '../config/api';
import { Stack } from '@mui/material';
import CustomizedDataGrid from '../components/CustomizedDataGrid';
import { GridColDef } from '@mui/x-data-grid';

const getSourceTypeIcon = (type: number) => {
    switch (type) {
        case 1:
            return () => <img src={connectorCsvIcon} alt="CSV" style={{ width: 24, height: 24 }} />;
        case 2:
            return TableChartIcon;
        case 3:
            return StorageIcon;
        case 4:
            return GoogleIcon;
        default:
            return ImageIcon;
    }
};

const getSourceTypeName = (type: number) => {
    switch (type) {
        case 1:
            return 'CSV';
        case 2:
            return 'Excel';
        case 3:
            return 'MySQL';
        case 4:
            return 'Google Sheet';
        default:
            return 'Unknown';
    }
};

export default function SourceEditPage() {
    const navigate = useNavigate();
    const { sourceId } = useParams<{ sourceId: string }>();
    const [formData, setFormData] = useState<SourceDetail | null>(null);
    const [tab, setTab] = useState(0);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Define columns for the mapping DataGrid
    const mappingColumns: GridColDef[] = [
        {
            field: 'field_name',
            headerName: 'Field Name',
            flex: 1,
            minWidth: 150,
        },
        {
            field: 'field_mapping',
            headerName: 'Field Mapping',
            flex: 1,
            minWidth: 150,
        },
        {
            field: 'field_type',
            headerName: 'Field Type',
            flex: 1,
            minWidth: 120,
        },
        {
            field: 'is_hidden',
            headerName: 'Hidden',
            flex: 0.5,
            minWidth: 100,
            valueGetter: (params: any) => params.value ? 'Yes' : 'No',
        },
    ];

    useEffect(() => {
        if (!sourceId) {
            setError('Invalid source ID');
            return;
        }

        setLoading(true);
        setError(null);
        fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}/${sourceId}`, {
            credentials: 'include',
        })
            .then(async (res) => {
                if (!res.ok) throw new Error('Failed to fetch source detail');
                const data = await res.json();
                console.log(data);
                if (data && data.result) {
                    console.log(data.result);
                    setFormData(data.result);
                } else {
                    setError('No detail data');
                }
            })
            .catch((e) => setError(e.message))
            .finally(() => setLoading(false));
    }, [sourceId]);

    const handleChange = (field: keyof SourceDetail, value: string) => {
        setFormData((prev) => {
            if (!prev) return prev;
            const next = { ...prev, [field]: value };
            if (!next.id || next.name === undefined || next.connector_type === undefined) return prev;
            return next;
        });
    };

    const handleSave = async () => {
        if (!formData || !sourceId) return;

        setSaving(true);
        setError(null);

        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}/${sourceId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    name: formData.name,
                    description: formData.description,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to update source');
            }

            const result = await response.json();
            console.log('Source updated successfully:', result);

            // Navigate back to sources page with success parameter
            navigate('/dashboard/sources?success=updated');
        } catch (error) {
            console.error('Error updating source:', error);
            setError(error instanceof Error ? error.message : 'Failed to update source');
        } finally {
            setSaving(false);
        }
    };

    const handleBack = () => {
        navigate('/dashboard/sources');
    };

    const SourceTypeIcon = getSourceTypeIcon(formData?.connector_type || 0);

    if (!sourceId) {
        return <Box p={2}>Invalid source ID</Box>;
    }

    if (loading || !formData) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Box sx={{ p: 2 }}>
                <Typography color="error">{error}</Typography>
                <Button onClick={handleBack} variant="outlined" sx={{ mt: 2 }}>
                    Go Back
                </Button>
            </Box>
        );
    }

    return (
        <Stack gap={2} sx={{ height: '100%', width: '100%' }}>
            <Stack direction="row" justifyContent="start" alignItems="center" gap={1}>
                <IconButton
                    onClick={handleBack}
                    sx={{
                        mr: 2,
                        border: '1px solid',
                        borderColor: 'divider',
                        '&:hover': {
                            backgroundColor: 'action.hover',
                        }
                    }}
                >
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h5" component="h1" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    Edit
                </Typography>

            </Stack>
            <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
                <Tab label="General" />
                <Tab label="Mapping" />
            </Tabs>
            {tab === 0 && (
                <Box sx={{ bgcolor: 'background.paper', borderRadius: 1, p: 2 }}>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Source ID
                                </Typography>
                                <TextField
                                    value={formData.id}
                                    fullWidth
                                    disabled
                                    InputProps={{
                                        sx: {
                                            bgcolor: 'action.disabledBackground',
                                            '& input': {
                                                py: 1.5
                                            }
                                        }
                                    }}
                                />
                            </Box>
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Source Type
                                </Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <SourceTypeIcon />
                                    <TextField
                                        value={getSourceTypeName(formData.connector_type || 0)}
                                        fullWidth
                                        disabled
                                        InputProps={{
                                            sx: {
                                                bgcolor: 'action.disabledBackground',
                                                '& input': {
                                                    py: 1.5
                                                }
                                            }
                                        }}
                                    />
                                </Box>
                            </Box>
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Source Name
                                </Typography>
                                <TextField
                                    value={formData.name}
                                    onChange={(e) => handleChange('name', e.target.value)}
                                    fullWidth
                                    placeholder="Enter source name"
                                    InputProps={{
                                        sx: {
                                            '& input': {
                                                py: 1.5
                                            }
                                        }
                                    }}
                                />
                            </Box>
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Description
                                </Typography>
                                <TextField
                                    value={formData.description}
                                    onChange={(e) => handleChange('description', e.target.value)}
                                    rows={3}
                                    fullWidth
                                    placeholder="Enter source description"
                                    InputProps={{
                                        sx: {
                                            '& textarea': {
                                                display: 'flex',
                                                alignItems: 'center',
                                                minHeight: '72px',
                                                padding: '16.5px 14px',
                                            }
                                        }
                                    }}
                                />
                            </Box>
                        </Grid>

                        <Grid item xs={12}>
                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 2 }}>
                                <Button onClick={handleBack} variant="outlined" disabled={saving}>
                                    Cancel
                                </Button>
                                <Button
                                    onClick={handleSave}
                                    variant="contained"
                                    disabled={saving}
                                    startIcon={saving ? <CircularProgress size={20} /> : null}
                                >
                                    {saving ? 'Saving...' : 'Save Changes'}
                                </Button>
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            )}
            {tab === 1 && (
                <Box sx={{ bgcolor: 'background.paper', borderRadius: 0, p: 0 }}>
                    <CustomizedDataGrid
                        rows={Array.isArray(formData.mapping) ? formData.mapping.map((field, idx) => ({
                            id: idx,
                            ...field
                        })) : []}
                        columns={mappingColumns}
                        sx={{
                            height: '100% !important',
                            minHeight: '500px',
                            maxHeight: '500px',
                            '& .MuiDataGrid-cell:focus': { outline: 'none' },
                            '& .MuiDataGrid-row': {
                                borderBottom: '1px solid #e0e0e0',
                            },
                            '& .MuiDataGrid-row:hover': {
                                backgroundColor: 'rgba(25, 118, 210, 0.08)',
                            },
                            '& .MuiDataGrid-virtualScroller': {
                                overflow: 'auto !important',
                            },
                            '& .MuiDataGrid-main': {
                                height: '100% !important',
                            },
                        }}
                        disableColumnMenu
                        disableRowSelectionOnClick
                        loading={loading}
                        hideFooterSelectedRowCount
                    />
                </Box>
            )}
        </Stack>
    );
} 