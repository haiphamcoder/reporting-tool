import React, { useState } from 'react';
import { Button, Box, Typography } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import AddChartDialog from '../AddChartDiaglog';

const ChartDialogDemo: React.FC = () => {
    const [open, setOpen] = useState(false);

    const handleSuccess = (chartId: string) => {
        console.log('Chart created successfully with ID:', chartId);
        // You can add a success notification here
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Chart Dialog Demo
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
                Click the button below to test the Add Chart dialog with 3 steps.
            </Typography>
            
            <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => setOpen(true)}
                size="large"
            >
                Add New Chart
            </Button>

            <AddChartDialog
                open={open}
                onClose={() => setOpen(false)}
                onSuccess={handleSuccess}
            />
        </Box>
    );
};

export default ChartDialogDemo; 