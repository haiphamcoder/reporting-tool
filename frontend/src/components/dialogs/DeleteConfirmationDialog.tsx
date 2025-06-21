import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import WarningIcon from '@mui/icons-material/Warning';
import DeleteIcon from '@mui/icons-material/Delete';

interface DeleteConfirmationDialogProps {
    open: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title?: string;
    message?: string;
    itemName?: string;
    itemType?: string;
    confirmButtonText?: string;
    cancelButtonText?: string;
    loading?: boolean;
    severity?: 'warning' | 'error';
}

const DeleteConfirmationDialog: React.FC<DeleteConfirmationDialogProps> = ({
    open,
    onClose,
    onConfirm,
    title = 'Confirm Delete',
    message,
    itemName,
    itemType = 'item',
    confirmButtonText = 'Delete',
    cancelButtonText = 'Cancel',
    loading = false,
    severity = 'warning'
}) => {
    const defaultMessage = itemName
        ? `Are you sure you want to delete ${itemType} "${itemName}"? This action cannot be undone.`
        : `Are you sure you want to delete this ${itemType}? This action cannot be undone.`;

    const displayMessage = message || defaultMessage;

    const handleConfirm = () => {
        if (!loading) {
            onConfirm();
        }
    };

    const handleClose = () => {
        if (!loading) {
            onClose();
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth="sm"
            fullWidth
            slotProps={{
                paper: {
                    sx: {
                        borderRadius: '10px',
                        border: '1px solid',
                        borderColor: 'divider',
                    }
                }
            }}
        >
            <DialogTitle sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                pb: 1
            }}>
                <WarningIcon
                    color={severity}
                    sx={{ fontSize: 24 }}
                />
                {title}
            </DialogTitle>

            <DialogContent sx={{ pt: 1, pb: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 2 }}>
                    <Box sx={{
                        mt: 0.5,
                        p: 1,
                        borderRadius: '50%',
                        backgroundColor: severity === 'error' ? 'error.light' : 'warning.light',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}>
                        <DeleteIcon
                            color={severity}
                            sx={{ fontSize: 20 }}
                        />
                    </Box>
                    <Typography variant="body1" color="text.primary">
                        {displayMessage}
                    </Typography>
                </Box>
            </DialogContent>

            <DialogActions sx={{ pb: 3, px: 3, gap: 1 }}>
                <Button
                    onClick={handleClose}
                    variant="outlined"
                    disabled={loading}
                    sx={{ minWidth: 100 }}
                >
                    {cancelButtonText}
                </Button>
                <Button
                    onClick={handleConfirm}
                    variant="contained"
                    color={severity}
                    disabled={loading}
                    startIcon={loading ? null : <DeleteIcon />}
                    sx={{ minWidth: 100 }}
                >
                    {loading ? 'Deleting...' : confirmButtonText}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteConfirmationDialog;
