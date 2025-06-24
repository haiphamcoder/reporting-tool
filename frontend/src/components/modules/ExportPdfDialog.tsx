import React, { useState } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    FormControlLabel, Checkbox, Button, Typography, Slider, Select, MenuItem, InputLabel, FormControl, OutlinedInput, ListItemText, Box
} from '@mui/material';

interface ChartOption {
    id: string;
    name: string;
}

interface ExportPdfDialogProps {
    open: boolean;
    onClose: () => void;
    onExport: (options: {
        printTitle: boolean;
        printDescription: boolean;
        selectedChartIds: string[];
        scale: number;
        layout: 'vertical' | '2col' | '3col';
    }) => void;
    charts: ChartOption[];
    defaultScale?: number;
}

const ExportPdfDialog: React.FC<ExportPdfDialogProps> = ({ open, onClose, onExport, charts, defaultScale = 3 }) => {
    const [printTitle, setPrintTitle] = useState(true);
    const [printDescription, setPrintDescription] = useState(true);
    const [selectedChartIds, setSelectedChartIds] = useState<string[]>(charts.map(c => c.id));
    const [scale, setScale] = useState(defaultScale);
    const [layout, setLayout] = useState<'vertical' | '2col' | '3col'>('vertical');

    const handleSelectAll = () => {
        setSelectedChartIds(charts.map(c => c.id));
    };
    const handleDeselectAll = () => {
        setSelectedChartIds([]);
    };
    const handleChangeCharts = (event: any) => {
        const value = event.target.value;
        setSelectedChartIds(typeof value === 'string' ? value.split(',') : value);
    };
    const handleExport = () => {
        onExport({ printTitle, printDescription, selectedChartIds, scale, layout });
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
            <DialogTitle>Export PDF Options</DialogTitle>
            <DialogContent>
                <FormControlLabel
                    control={<Checkbox checked={printTitle} onChange={e => setPrintTitle(e.target.checked)} />}
                    label="Include report title"
                />
                <FormControlLabel
                    control={<Checkbox checked={printDescription} onChange={e => setPrintDescription(e.target.checked)} />}
                    label="Include report description"
                />
                <Box mt={2}>
                    <Typography fontWeight={500} mb={1}>Select charts to export</Typography>
                    <FormControl fullWidth>
                        <InputLabel id="select-charts-label">Charts</InputLabel>
                        <Select
                            labelId="select-charts-label"
                            multiple
                            value={selectedChartIds}
                            onChange={handleChangeCharts}
                            input={<OutlinedInput label="Charts" />}
                            renderValue={(selected) =>
                                charts.filter(c => selected.includes(c.id)).map(c => c.name).join(', ')
                            }
                        >
                            <MenuItem disabled value="">
                                <em>Select charts</em>
                            </MenuItem>
                            {charts.map((chart) => (
                                <MenuItem key={chart.id} value={chart.id}>
                                    <Checkbox checked={selectedChartIds.indexOf(chart.id) > -1} />
                                    <ListItemText primary={chart.name} />
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <Box mt={1} display="flex" gap={1}>
                        <Button size="small" onClick={handleSelectAll}>Select all</Button>
                        <Button size="small" onClick={handleDeselectAll}>Deselect all</Button>
                    </Box>
                </Box>
                <Box mt={3}>
                    <Typography fontWeight={500} mb={1}>Chart layout</Typography>
                    <FormControl fullWidth>
                        <Select
                            value={layout}
                            onChange={e => setLayout(e.target.value as any)}
                        >
                            <MenuItem value="vertical">Vertical (1 chart/row)</MenuItem>
                            <MenuItem value="2col">2 columns</MenuItem>
                            <MenuItem value="3col">3 columns</MenuItem>
                        </Select>
                    </FormControl>
                </Box>
                <Box mt={3}>
                    <Typography gutterBottom>Image quality (scale): {scale}</Typography>
                    <Slider
                        value={scale}
                        min={1}
                        max={5}
                        step={1}
                        marks
                        valueLabelDisplay="auto"
                        onChange={(_, value) => setScale(value as number)}
                    />
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cancel</Button>
                <Button variant="contained" onClick={handleExport} disabled={selectedChartIds.length === 0}>Export PDF</Button>
            </DialogActions>
        </Dialog>
    );
};

export default ExportPdfDialog; 