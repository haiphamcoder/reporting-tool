import React, { useState, useEffect, useCallback } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    List,
    ListItem,
    ListItemButton,
    ListItemAvatar,
    ListItemText,
    ListItemSecondaryAction,
    IconButton,
    Avatar,
    Box,
    Typography,
    FormControl,
    Select,
    MenuItem,
    CircularProgress,
    Divider,
    Alert,
    Stack
} from '@mui/material';
import {
    Close as CloseIcon,
    Search as SearchIcon,
    PersonAdd as PersonAddIcon,
    Delete as DeleteIcon
} from '@mui/icons-material';
import { API_CONFIG } from '../../config/api';
import SharePermissionWarningDialog from './SharePermissionWarningDialog';

interface User {
    id: string;
    first_name: string;
    last_name: string;
    email: string;
    avatar_url?: string;
    email_verified: boolean;
    role: string;
    provider: string;
}

interface SharedUser {
    id: string; // Always string to prevent type conversion issues
    name: string;
    email: string;
    avatar: string;
    permission: 'view' | 'edit';
}

interface ShareReportDialogProps {
    open: boolean;
    onClose: () => void;
    reportId: string;
    reportName?: string;
    onSuccess?: (message: string) => void; // Callback for success
}

export default function ShareReportDialog({
    open,
    onClose,
    reportId,
    reportName = 'Report',
    onSuccess
}: ShareReportDialogProps) {
    const [loading, setLoading] = useState(false);
    const [searchLoading, setSearchLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Current shared users
    const [sharedUsers, setSharedUsers] = useState<SharedUser[]>([]);

    // Search functionality
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState<User[]>([]);
    const [showSearchResults, setShowSearchResults] = useState(false);

    // Selected user for adding
    const [, setSelectedUser] = useState<User | null>(null);
    const [selectedPermission, setSelectedPermission] = useState<'view' | 'edit'>('view');

    // Warning dialog state
    const [showWarningDialog, setShowWarningDialog] = useState(false);

    // Utility function to ensure ID is always string
    const ensureStringId = (id: any): string => {
        return String(id);
    };

    // Fetch current shared users
    const fetchSharedUsers = useCallback(async () => {
        if (!reportId) return;

        try {
            setLoading(true);
            setError(null);

            const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/reports/${reportId}/share`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to fetch shared users');
            }

            const data = await response.json();

            if (data.success) {
                // Shared users API returns data in the correct format already
                console.log('Shared users fetched:', data.result); // Debug log

                // Ensure all IDs are strings to prevent type conversion issues
                const processedSharedUsers = (data.result || []).map((user: any) => ({
                    ...user,
                    id: ensureStringId(user.id) // Ensure ID is always a string
                }));

                console.log('Processed shared users:', processedSharedUsers); // Debug log
                setSharedUsers(processedSharedUsers);
            } else {
                console.log('Failed to fetch shared users:', data.message); // Debug log
                setError(data.message || 'Failed to fetch shared users');
            }
        } catch (error) {
            console.error('Error fetching shared users:', error);
            setError(error instanceof Error ? error.message : 'Failed to fetch shared users');
        } finally {
            setLoading(false);
        }
    }, [reportId]);

    // Search users
    const searchUsers = useCallback(async (search: string) => {
        if (!search.trim()) {
            setSearchResults([]);
            setShowSearchResults(false);
            return;
        }

        try {
            setSearchLoading(true);
            console.log('Searching users with term:', search); // Debug log

            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USER_MANAGEMENT}?search=${encodeURIComponent(search.trim())}`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
            });

            console.log('Search response status:', response.status); // Debug log

            if (!response.ok) {
                throw new Error(`Failed to search users: ${response.status}`);
            }

            const data = await response.json();
            console.log('Search response data:', data); // Debug log

            if (data.success) {
                // Extract users from the result.users array
                const users = data.result?.users || [];
                console.log('API returned users:', users); // Debug log

                // Filter out users that are already shared
                console.log('Current shared users IDs:', sharedUsers.map((u: SharedUser) => ({ id: u.id, type: typeof u.id }))); // Debug log
                console.log('Search results IDs:', users.map((u: User) => ({ id: u.id, type: typeof u.id }))); // Debug log

                const filteredResults = users.filter((user: User) =>
                    !sharedUsers.some(sharedUser => ensureStringId(sharedUser.id) === ensureStringId(user.id))
                );
                console.log('Filtered results:', filteredResults); // Debug log
                setSearchResults(filteredResults);
                setShowSearchResults(true);
            } else {
                console.log('Search failed:', data.message); // Debug log
                setSearchResults([]);
                setShowSearchResults(false);
            }
        } catch (error) {
            console.error('Error searching users:', error);
            setSearchResults([]);
            setShowSearchResults(false);
        } finally {
            setSearchLoading(false);
        }
    }, [sharedUsers]);

    // Handle search input change with debounce
    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value;
        console.log('Search input changed:', value); // Debug log
        setSearchTerm(value);

        // Clear previous timeout
        if ((window as any).searchTimeout) {
            clearTimeout((window as any).searchTimeout);
        }

        if (value.trim()) {
            // Debounce search to avoid too many API calls
            (window as any).searchTimeout = setTimeout(() => {
                console.log('Executing search for:', value); // Debug log
                searchUsers(value);
            }, 300);
        } else {
            setSearchResults([]);
            setShowSearchResults(false);
        }
    };

    // Add user to shared list
    const handleAddUser = (user: User) => {
        const newSharedUser: SharedUser = {
            id: ensureStringId(user.id), // Ensure ID is always a string
            name: `${user.first_name} ${user.last_name}`.trim(),
            email: user.email,
            avatar: user.avatar_url || '',
            permission: selectedPermission
        };

        console.log('Adding new shared user:', newSharedUser); // Debug log
        setSharedUsers(prev => [...prev, newSharedUser]);
        setSelectedUser(null);
        setSelectedPermission('view');
        setSearchTerm('');
        setSearchResults([]);
        setShowSearchResults(false);
    };

    // Remove user from shared list
    const handleRemoveUser = (userId: string) => {
        setSharedUsers(prev => prev.filter(user => ensureStringId(user.id) !== ensureStringId(userId)));
    };

    // Update user permission
    const handlePermissionChange = (userId: string, permission: 'view' | 'edit') => {
        setSharedUsers(prev =>
            prev.map(user =>
                ensureStringId(user.id) === ensureStringId(userId) ? { ...user, permission } : user
            )
        );
    };

    // Save shared configuration
    const handleSave = async () => {
        // Show warning dialog first if there are users to share with
        if (sharedUsers.length > 0) {
            setShowWarningDialog(true);
            return;
        }
        
        // If no users to share, proceed directly
        await performSave();
    };

    // Actual save operation
    const performSave = async () => {
        try {
            setLoading(true);
            setError(null);

            const payload = {
                users: sharedUsers.map(user => ({
                    id: ensureStringId(user.id), // Ensure ID is always a string
                    permission: user.permission
                }))
            };

            console.log('Saving payload:', payload); // Debug log

            const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/reports/${reportId}/share`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                throw new Error('Failed to update shared configuration');
            }

            const data = await response.json();

            if (data.success) {
                // Call parent success callback and close dialog
                if (onSuccess) {
                    onSuccess('Shared configuration updated successfully');
                }
                onClose();
            } else {
                setError(data.message || 'Failed to update shared configuration');
            }
        } catch (error) {
            console.error('Error updating shared configuration:', error);
            setError(error instanceof Error ? error.message : 'Failed to update shared configuration');
        } finally {
            setLoading(false);
        }
    };

    // Handle warning dialog confirmation
    const handleWarningConfirm = () => {
        setShowWarningDialog(false);
        performSave();
    };

    // Handle warning dialog close
    const handleWarningClose = () => {
        setShowWarningDialog(false);
    };

    // Reset form when dialog opens/closes
    useEffect(() => {
        if (open) {
            fetchSharedUsers();
            setSearchTerm('');
            setSearchResults([]);
            setShowSearchResults(false);
            setSelectedUser(null);
            setSelectedPermission('view');
            setError(null);
        }
    }, [open, fetchSharedUsers]);

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="md"
            fullWidth
            PaperProps={{
                sx: {
                    minHeight: '600px',
                    maxHeight: '80vh'
                }
            }}
        >
            <DialogTitle sx={{ m: 0, p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Typography variant="h6">
                    Share Report: {reportName}
                </Typography>
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

            <DialogContent dividers sx={{ p: 3 }}>
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}

                {/* Search Section */}
                <Box sx={{ mb: 3 }}>
                    <Typography variant="subtitle1" gutterBottom>
                        Add Users
                    </Typography>
                    <Stack direction="row" spacing={2} alignItems="flex-start">
                        <TextField
                            fullWidth
                            placeholder="Search users by name or email..."
                            value={searchTerm}
                            onChange={handleSearchChange}
                            InputProps={{
                                startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                                endAdornment: searchLoading && <CircularProgress size={20} />
                            }}
                            size="small"
                        />
                    </Stack>

                    {/* Search Results */}
                    {searchLoading && (
                        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center', p: 2 }}>
                            <CircularProgress size={20} />
                        </Box>
                    )}

                    {showSearchResults && searchResults.length > 0 && !searchLoading && (
                        <Box sx={{
                            mt: 2,
                            maxHeight: 250,
                            overflow: 'auto',
                            border: 1,
                            borderColor: 'divider',
                            borderRadius: 1,
                            backgroundColor: 'background.paper',
                            boxShadow: 2
                        }}>
                            <List dense sx={{ p: 0 }}>
                                {searchResults.map((user) => (
                                    <ListItem key={user.id} sx={{ px: 0 }}>
                                        <ListItemButton
                                            onClick={() => handleAddUser(user)}
                                            sx={{
                                                cursor: 'pointer',
                                                '&:hover': {
                                                    backgroundColor: 'action.hover'
                                                }
                                            }}
                                        >
                                            <ListItemAvatar>
                                                <Avatar
                                                    src={user.avatar_url}
                                                    alt={`${user.first_name} ${user.last_name}`}
                                                    sx={{ width: 32, height: 32 }}
                                                >
                                                    {user.first_name.charAt(0).toUpperCase()}
                                                </Avatar>
                                            </ListItemAvatar>
                                            <ListItemText
                                                primary={`${user.first_name} ${user.last_name}`}
                                                secondary={user.email}
                                                primaryTypographyProps={{ fontSize: '0.875rem', fontWeight: 500 }}
                                                secondaryTypographyProps={{ fontSize: '0.75rem' }}
                                            />
                                            <ListItemSecondaryAction>
                                                <IconButton
                                                    edge="end"
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        handleAddUser(user);
                                                    }}
                                                    size="small"
                                                    sx={{
                                                        color: 'primary.main',
                                                        '&:hover': {
                                                            backgroundColor: 'primary.light',
                                                            color: 'white'
                                                        }
                                                    }}
                                                >
                                                    <PersonAddIcon fontSize="small" />
                                                </IconButton>
                                            </ListItemSecondaryAction>
                                        </ListItemButton>
                                    </ListItem>
                                ))}
                            </List>
                        </Box>
                    )}

                    {showSearchResults && searchResults.length === 0 && !searchLoading && searchTerm.trim() && (
                        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                            No users found
                        </Typography>
                    )}
                </Box>

                <Divider sx={{ my: 2 }} />

                {/* Current Shared Users */}
                <Box>
                    <Typography variant="subtitle1" gutterBottom>
                        Shared Users ({sharedUsers.length})
                    </Typography>

                    {loading ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                            <CircularProgress />
                        </Box>
                    ) : sharedUsers.length === 0 ? (
                        <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
                            No users are currently shared with this report
                        </Typography>
                    ) : (
                        <List>
                            {sharedUsers.map((user) => (
                                <ListItem key={user.id} sx={{ px: 0 }}>
                                    <ListItemAvatar>
                                        <Avatar
                                            src={user.avatar}
                                            alt={user.name}
                                            sx={{ width: 32, height: 32 }}
                                        >
                                            {user.name.charAt(0).toUpperCase()}
                                        </Avatar>
                                    </ListItemAvatar>
                                    <ListItemText
                                        primary={user.name}
                                        secondary={user.email}
                                        primaryTypographyProps={{ fontSize: '0.875rem', fontWeight: 500 }}
                                        secondaryTypographyProps={{ fontSize: '0.75rem' }}
                                    />
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <FormControl size="small" sx={{ minWidth: 100 }}>
                                            <Select
                                                value={user.permission}
                                                onChange={(e) => handlePermissionChange(user.id, e.target.value as 'view' | 'edit')}
                                                size="small"
                                                sx={{
                                                    '& .MuiSelect-select': {
                                                        fontSize: '0.75rem',
                                                        py: 0.5
                                                    }
                                                }}
                                            >
                                                <MenuItem value="view" sx={{ fontSize: '0.75rem' }}>View</MenuItem>
                                                <MenuItem value="edit" sx={{ fontSize: '0.75rem' }}>Edit</MenuItem>
                                            </Select>
                                        </FormControl>
                                        <IconButton
                                            onClick={() => handleRemoveUser(user.id)}
                                            size="small"
                                            color="error"
                                            sx={{
                                                '&:hover': {
                                                    backgroundColor: 'error.light',
                                                    color: 'white'
                                                }
                                            }}
                                        >
                                            <DeleteIcon fontSize="small" />
                                        </IconButton>
                                    </Box>
                                </ListItem>
                            ))}
                        </List>
                    )}
                </Box>
            </DialogContent>

            <DialogActions sx={{ p: 2 }}>
                <Button onClick={onClose} disabled={loading}>
                    Cancel
                </Button>
                <Button
                    onClick={handleSave}
                    variant="contained"
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={16} /> : null}
                >
                    {loading ? 'Saving...' : 'Save Changes'}
                </Button>
            </DialogActions>

            {/* Warning Dialog */}
            <SharePermissionWarningDialog
                open={showWarningDialog}
                onClose={handleWarningClose}
                onConfirm={handleWarningConfirm}
                type="report"
                itemName={reportName}
            />
        </Dialog>
    );
}
