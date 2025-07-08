import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Typography,
    Box,
    Alert,
    IconButton,
    List,
    ListItem,
    ListItemIcon,
    ListItemText
} from '@mui/material';
import {
    Close as CloseIcon,
    Info as InfoIcon,
    Visibility as VisibilityIcon,
    Warning as WarningIcon
} from '@mui/icons-material';

interface SharePermissionWarningDialogProps {
    open: boolean;
    onClose: () => void;
    onConfirm: () => void;
    type: 'chart' | 'report';
    itemName: string;
}

export default function SharePermissionWarningDialog({
    open,
    onClose,
    onConfirm,
    type,
    itemName
}: SharePermissionWarningDialogProps) {
    const getWarningContent = () => {
        if (type === 'chart') {
            return {
                title: 'Share Chart Permission Notice',
                message: `When you share the chart "${itemName}", users will automatically be granted view permissions to all related data sources.`,
                details: [
                    'Users will be able to view the data sources used in this chart',
                    'This ensures they can access the data needed to display the chart',
                    'Source permissions are managed automatically by the system'
                ]
            };
        } else {
            return {
                title: 'Share Report Permission Notice',
                message: `When you share the report "${itemName}", users will automatically be granted view permissions to all related charts and data sources.`,
                details: [
                    'Users will be able to view all charts included in this report',
                    'Users will be able to view all data sources used by the charts',
                    'This ensures they can access all data needed to display the report',
                    'Chart and source permissions are managed automatically by the system'
                ]
            };
        }
    };

    const content = getWarningContent();

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="sm"
            fullWidth
            PaperProps={{
                sx: {
                    borderRadius: 2
                }
            }}
        >
            <DialogTitle sx={{ m: 0, p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <WarningIcon color="warning" />
                    <Typography variant="h6">
                        {content.title}
                    </Typography>
                </Box>
                <IconButton
                    aria-label="close"
                    onClick={onClose}
                    sx={{
                        color: (theme) => theme.palette.grey[500],
                    }}
                >
                    <CloseIcon />
                </IconButton>
            </DialogTitle>

            <DialogContent sx={{ p: 3 }}>
                <Alert severity="info" sx={{ mb: 3 }}>
                    <Typography variant="body1" sx={{ fontWeight: 500 }}>
                        {content.message}
                    </Typography>
                </Alert>

                <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 600, mb: 2 }}>
                    What this means:
                </Typography>

                <List dense>
                    {content.details.map((detail, index) => (
                        <ListItem key={index} sx={{ px: 0 }}>
                            <ListItemIcon sx={{ minWidth: 32 }}>
                                <VisibilityIcon fontSize="small" color="primary" />
                            </ListItemIcon>
                            <ListItemText
                                primary={detail}
                                primaryTypographyProps={{ fontSize: '0.875rem' }}
                            />
                        </ListItem>
                    ))}
                </List>

                <Box sx={{ mt: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
                    <Typography variant="body2" color="text.secondary">
                        <strong>Note:</strong> The backend will automatically handle permission management to ensure users have the necessary access to view shared content.
                    </Typography>
                </Box>
            </DialogContent>

            <DialogActions sx={{ p: 2, gap: 1 }}>
                <Button onClick={onClose} variant="outlined">
                    Cancel
                </Button>
                <Button 
                    onClick={onConfirm} 
                    variant="contained" 
                    color="primary"
                    startIcon={<InfoIcon />}
                >
                    I Understand, Continue Sharing
                </Button>
            </DialogActions>
        </Dialog>
    );
} 