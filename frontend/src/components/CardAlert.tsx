import React, { useEffect, useState } from 'react';
import { Alert, AlertColor, Snackbar, Slide, SlideProps } from '@mui/material';

interface CardAlertProps {
    open: boolean;
    message: string;
    severity: AlertColor;
    onClose: () => void;
    autoHideDuration?: number;
    position?: 'top' | 'bottom' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
}

function SlideTransition(props: SlideProps) {
    return <Slide {...props} direction="up" />;
}

export default function CardAlert({
    open,
    message,
    severity,
    onClose,
    autoHideDuration = 4000,
    position = 'top-right'
}: CardAlertProps) {
    const [isVisible, setIsVisible] = useState(false);

    console.log('CardAlert - open:', open);
    console.log('CardAlert - message:', message);
    console.log('CardAlert - isVisible:', isVisible);

    useEffect(() => {
        if (open) {
            console.log('CardAlert - Setting isVisible to true');
            setIsVisible(true);
        }
    }, [open]);

    const handleClose = (_event?: React.SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setIsVisible(false);
        setTimeout(() => {
            onClose();
        }, 300); // Delay to allow animation to complete
    };

    const getAnchorOrigin = () => {
        switch (position) {
            case 'top':
                return { vertical: 'top' as const, horizontal: 'center' as const };
            case 'bottom':
                return { vertical: 'bottom' as const, horizontal: 'center' as const };
            case 'top-left':
                return { vertical: 'top' as const, horizontal: 'left' as const };
            case 'top-right':
                return { vertical: 'top' as const, horizontal: 'right' as const };
            case 'bottom-left':
                return { vertical: 'bottom' as const, horizontal: 'left' as const };
            case 'bottom-right':
                return { vertical: 'bottom' as const, horizontal: 'right' as const };
            default:
                return { vertical: 'top' as const, horizontal: 'right' as const };
        }
    };

    return (
        <Snackbar
            open={isVisible}
            autoHideDuration={autoHideDuration}
            onClose={handleClose}
            anchorOrigin={getAnchorOrigin()}
            TransitionComponent={SlideTransition}
            sx={{
                '& .MuiSnackbar-root': {
                    zIndex: 9999,
                },
                '& .MuiAlert-root': {
                    minWidth: 300,
                    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.15)',
                    borderRadius: 2,
                    '& .MuiAlert-icon': {
                        fontSize: '1.5rem',
                    },
                    '& .MuiAlert-message': {
                        fontSize: '0.95rem',
                        fontWeight: 500,
                    },
                },
            }}
        >
            <Alert
                onClose={handleClose}
                severity={severity}
                variant="filled"
                sx={{
                    width: '100%',
                    '&.MuiAlert-filledSuccess': {
                        backgroundColor: '#2e7d32',
                        color: 'white',
                    },
                    '&.MuiAlert-filledError': {
                        backgroundColor: '#d32f2f',
                        color: 'white',
                    },
                    '&.MuiAlert-filledWarning': {
                        backgroundColor: '#ed6c02',
                        color: 'white',
                    },
                    '&.MuiAlert-filledInfo': {
                        backgroundColor: '#0288d1',
                        color: 'white',
                    },
                }}
            >
                {message}
            </Alert>
        </Snackbar>
    );
}
