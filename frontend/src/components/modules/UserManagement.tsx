import { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Typography,
  Button,
  IconButton,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Avatar,
  Stack
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
  Person as PersonIcon,
  AdminPanelSettings as AdminIcon,
  Person as UserIcon,
  Security as SecurityIcon
} from '@mui/icons-material';
import { API_CONFIG } from '../../config/api';
import { useAuth } from '../../context/AuthContext';
import { GridColDef, GridPaginationModel, GridRenderCellParams } from '@mui/x-data-grid';
import CustomizedDataGrid from '../CustomizedDataGrid';
import DeleteConfirmationDialog from '../dialogs/DeleteConfirmationDialog';
import { useLocation } from 'react-router-dom';
import Search from '../Search';

interface User {
  id: string; // Changed from user_id to id
  username?: string; // Optional since API doesn't return it
  email: string;
  first_name: string;
  last_name: string;
  role: string;
  email_verified: boolean;
  provider: string;
  first_login?: boolean; // Optional since API doesn't return it
  avatar_url?: string;
}

interface UserFormData {
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  role: string;
  password: string;
}

interface UserMetadata {
  total_elements: number;
  number_of_elements: number;
  total_pages: number;
  current_page: number;
  page_size: number;
}

export default function UserManagement() {
  const { user, isLoading } = useAuth();
  const location = useLocation();
  const isAdmin = user?.role === 'admin';

  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [metadata, setMetadata] = useState<UserMetadata>({
    total_elements: 0,
    number_of_elements: 0,
    total_pages: 0,
    current_page: 0,
    page_size: 10
  });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<User | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [formData, setFormData] = useState<UserFormData>({
    username: '',
    email: '',
    first_name: '',
    last_name: '',
    role: 'user',
    password: ''
  });

  // Search state
  const [searchTerm, setSearchTerm] = useState('');

  // Function to update URL with pagination parameters
  const updateURLWithPagination = useCallback((page: number, size: number, search: string = '') => {
    const urlParams = new URLSearchParams(location.search);
    urlParams.set('page', page.toString());
    urlParams.set('pageSize', size.toString());
    
    if (search) {
      urlParams.set('search', search);
    } else {
      urlParams.delete('search');
    }
    
    // Preserve other URL parameters
    const newSearch = urlParams.toString();
    const newPath = `${location.pathname}${newSearch ? `?${newSearch}` : ''}`;
    
    window.history.replaceState(null, '', newPath);
  }, [location.search, location.pathname]);

  // Function to get pagination parameters from URL
  const getPaginationFromURL = () => {
    const urlParams = new URLSearchParams(location.search);
    const page = parseInt(urlParams.get('page') || '0', 10);
    const size = parseInt(urlParams.get('pageSize') || '10', 10);
    const search = urlParams.get('search') || '';
    return { page, size, search };
  };

  // Initialize URL with default pagination values on first load
  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const hasPageParam = urlParams.has('page');
    const hasPageSizeParam = urlParams.has('pageSize');
    const search = urlParams.get('search') || '';
    
    // Update search term from URL
    setSearchTerm(search);
    
    // If URL doesn't have pagination parameters, add default values
    if (!hasPageParam || !hasPageSizeParam) {
      const defaultPage = hasPageParam ? parseInt(urlParams.get('page') || '0', 10) : 0;
      const defaultSize = hasPageSizeParam ? parseInt(urlParams.get('pageSize') || '10', 10) : 10;
      
      urlParams.set('page', defaultPage.toString());
      urlParams.set('pageSize', defaultSize.toString());
      
      const newSearch = urlParams.toString();
      const newPath = `${location.pathname}?${newSearch}`;
      window.history.replaceState(null, '', newPath);
    }
  }, [location.pathname]); // Add location.pathname to dependencies

  const fetchUsers = useCallback(async (page: number = 0, pageSize: number = 10, search: string = '') => {
    try {
      setLoading(true);
      setError(null);

      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('size', pageSize.toString());
      if (search.trim()) {
        params.append('search', search.trim());
      }

      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USER_MANAGEMENT}?${params.toString()}`, {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      if (data.success && data.result) {
        setUsers(data.result.users || []);
        setMetadata(data.result.metadata || {
          total_elements: 0,
          number_of_elements: 0,
          total_pages: 0,
          current_page: page,
          page_size: pageSize
        });
        setLoading(false);
      } else {
        setError(data.message || 'Failed to fetch users');
        setLoading(false);
      }
    } catch (error) {
      console.error('Error fetching users:', error);
      setError(error instanceof Error ? error.message : 'Failed to fetch users');
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const { page, size, search } = getPaginationFromURL();
    fetchUsers(page, size, search);
  }, [fetchUsers]); // Listen to fetchUsers changes

  // Update search term when URL changes (e.g., browser back/forward)
  useEffect(() => {
    const { search } = getPaginationFromURL();
    setSearchTerm(search);
  }, [location.search]);

  useEffect(() => {
    console.log('Users state changed:', users);
  }, [users]);

  useEffect(() => {
    console.log('Metadata state changed:', metadata);
  }, [metadata]);

  const handlePageChange = useCallback((model: GridPaginationModel) => {
    updateURLWithPagination(model.page, model.pageSize, searchTerm);
    fetchUsers(model.page, model.pageSize, searchTerm);
  }, [searchTerm, updateURLWithPagination, fetchUsers]);

  const handleSearchChange = useCallback((searchTerm: string) => {
    // Update local state
    setSearchTerm(searchTerm);
    
    // Reset to first page when searching
    const newPage = 0;
    updateURLWithPagination(newPage, metadata.page_size, searchTerm);
    fetchUsers(newPage, metadata.page_size, searchTerm);
  }, [metadata.page_size, fetchUsers, updateURLWithPagination]);

  const handleRefresh = useCallback(() => {
    fetchUsers(metadata.current_page, metadata.page_size, searchTerm);
  }, [metadata.current_page, metadata.page_size, searchTerm, fetchUsers]);

  const handleAddUser = () => {
    setEditingUser(null);
    setFormData({
      username: '',
      email: '',
      first_name: '',
      last_name: '',
      role: 'user',
      password: ''
    });
    setDialogOpen(true);
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
    setFormData({
      username: user.username || user.email.split('@')[0],
      email: user.email,
      first_name: user.first_name,
      last_name: user.last_name,
      role: user.role,
      password: ''
    });
    setDialogOpen(true);
  };

  const handleDeleteUser = (user: User) => {
    setUserToDelete(user);
    setDeleteDialogOpen(true);
  };

  const handleSaveUser = async () => {
    try {
      setError(null);
      setSuccess(null);

      const url = editingUser
        ? `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USER_MANAGEMENT}/users/${editingUser.id}`
        : `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USER_MANAGEMENT}/users`;

      const method = editingUser ? 'PUT' : 'POST';

      const requestBody = editingUser
        ? {
          username: formData.username,
          email: formData.email,
          first_name: formData.first_name,
          last_name: formData.last_name,
          role: formData.role,
          ...(formData.password && { password: formData.password })
        }
        : {
          username: formData.username,
          email: formData.email,
          first_name: formData.first_name,
          last_name: formData.last_name,
          role: formData.role,
          password: formData.password
        };

      const response = await fetch(url, {
        method,
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to save user');
      }

      const data = await response.json();

      if (data.success) {
        setSuccess(editingUser ? 'User updated successfully' : 'User created successfully');
        setDialogOpen(false);
        fetchUsers(metadata.current_page, metadata.page_size, searchTerm);
      } else {
        setError(data.message || 'Failed to save user');
      }
    } catch (error) {
      console.error('Error saving user:', error);
      setError(error instanceof Error ? error.message : 'Failed to save user');
    }
  };

  const handleConfirmDelete = async () => {
    if (!userToDelete) return;

    try {
      setError(null);
      setSuccess(null);

      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USER_MANAGEMENT}/users/${userToDelete.id}`, {
        method: 'DELETE',
        credentials: 'include',
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to delete user');
      }

      const data = await response.json();

      if (data.success) {
        setSuccess('User deleted successfully');
        setDeleteDialogOpen(false);
        setUserToDelete(null);
        fetchUsers(metadata.current_page, metadata.page_size, searchTerm);
      } else {
        setError(data.message || 'Failed to delete user');
      }
    } catch (error) {
      console.error('Error deleting user:', error);
      setError(error instanceof Error ? error.message : 'Failed to delete user');
    }
  };

  const getRoleColor = (role: string) => {
    switch (role) {
      case 'admin':
        return 'error';
      case 'user':
        return 'primary';
      default:
        return 'default';
    }
  };

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'admin':
        return <AdminIcon fontSize="small" />;
      case 'user':
        return <UserIcon fontSize="small" />;
      default:
        return <PersonIcon fontSize="small" />;
    }
  };

  const userColumns: GridColDef[] = [
    {
      field: 'user',
      headerName: 'User',
      flex: 1.5,
      minWidth: 250,
      renderCell: (params: GridRenderCellParams<User>) => (
        <Stack direction="row" spacing={2} alignItems="center" sx={{ height: '100%', width: '100%' }}>
          <Avatar
            src={params.row.avatar_url}
            alt={`${params.row.first_name} ${params.row.last_name}`}
            sx={{ width: 32, height: 32 }}
          >
            {params.row.first_name?.[0]}{params.row.last_name?.[0]}
          </Avatar>
          <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
            <Typography variant="body2" fontWeight="medium">
              {params.row.first_name} {params.row.last_name}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              @{params.row.username || params.row.email.split('@')[0]}
            </Typography>
          </Box>
        </Stack>
      ),
    },
    {
      field: 'email',
      headerName: 'Email',
      flex: 1,
      minWidth: 200,
    },
    {
      field: 'role',
      headerName: 'Role',
      flex: 0.8,
      minWidth: 120,
      renderCell: (params: GridRenderCellParams<User>) => (
        <Chip
          icon={getRoleIcon(params.value)}
          label={params.value}
          color={getRoleColor(params.value) as any}
          size="small"
          variant="outlined"
        />
      ),
    },
    {
      field: 'provider',
      headerName: 'Provider',
      flex: 0.8,
      minWidth: 120,
      renderCell: (params: GridRenderCellParams<User>) => (
        <Chip
          label={params.value}
          size="small"
          variant="outlined"
        />
      ),
    },
    {
      field: 'email_verified',
      headerName: 'Email Verified',
      flex: 0.8,
      minWidth: 140,
      renderCell: (params: GridRenderCellParams<User>) => (
        <Chip
          label={params.value ? 'Verified' : 'Not Verified'}
          color={params.value ? 'success' : 'warning'}
          size="small"
          variant="outlined"
        />
      ),
    },
    {
      field: 'first_login',
      headerName: 'First Login',
      flex: 0.8,
      minWidth: 120,
      renderCell: (params: GridRenderCellParams<User>) => (
        <Chip
          label={params.value ? 'Yes' : 'No'}
          color={params.value ? 'warning' : 'success'}
          size="small"
          variant="outlined"
        />
      ),
    },
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 0.8,
      minWidth: 120,
      sortable: false,
      renderCell: (params: GridRenderCellParams<User>) => {
        if (!params.row) return null;
        return (
          <Stack
            direction="row"
            spacing={1}
            justifyContent="flex-end"
            alignItems="center"
            sx={{ height: '100%', width: '100%' }}
          >
            <IconButton
              color="primary"
              size="small"
              onClick={(e) => {
                e.stopPropagation();
                handleEditUser(params.row);
              }}
            >
              <EditIcon />
            </IconButton>
            <IconButton
              color="error"
              size="small"
              onClick={(e) => {
                e.stopPropagation();
                handleDeleteUser(params.row);
              }}
            >
              <DeleteIcon />
            </IconButton>
          </Stack>
        );
      },
    },
  ];

  // Show loading while authentication is being checked
  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  // If user is not admin, show access denied message
  if (!isAdmin) {
    return (
      <Box sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '400px',
        textAlign: 'center'
      }}>
        <SecurityIcon sx={{ fontSize: 64, color: 'warning.main', mb: 2 }} />
        <Typography variant="h5" gutterBottom>
          Access Denied
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ maxWidth: 400 }}>
          You don't have permission to access the User Management section.
          Only administrators can manage users.
        </Typography>
      </Box>
    );
  }

  // Error boundary for DataGrid
  const renderDataGrid = () => {
    try {
      return (
        <CustomizedDataGrid
          rows={users}
          columns={userColumns}
          getRowId={(row: User) => row.id}
          // sx={{ '& .MuiDataGrid-cell:focus': { outline: 'none' } }}
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
          columnBufferPx={2}
          paginationMode="server"
          rowCount={metadata.total_elements}
          pageSizeOptions={[10, 25, 50]}
          paginationModel={{
            page: metadata.current_page,
            pageSize: metadata.page_size
          }}
          onPaginationModelChange={handlePageChange}
          loading={loading}
        />
      );
    } catch (error) {
      console.error('DataGrid error:', error);
      return (
        <Box sx={{ p: 2, textAlign: 'center' }}>
          <Typography color="error" gutterBottom>
            DataGrid Error: {error instanceof Error ? error.message : 'Unknown error'}
          </Typography>
          <Button
            variant="outlined"
            onClick={() => { }} // Placeholder for now, as dataGridError state is removed
          >
            Try Again
          </Button>
        </Box>
      );
    }
  };

  return (
    <Stack gap={2} >
      <Typography variant="h4" component="h2" gutterBottom>
        Users
      </Typography>
      <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
        <Search 
          value={searchTerm}
          onSearchChange={handleSearchChange}
          placeholder="Search users..."
        />
        <Stack direction="row" gap={1}>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={handleRefresh}
            disabled={loading}
            sx={{ minWidth: '120px' }}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleAddUser}
            sx={{ minWidth: '140px', maxWidth: '140px' }}
          >
            Add User
          </Button>
        </Stack>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
          {success}
        </Alert>
      )}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      ) : users.length === 0 ? (
        <Box sx={{ textAlign: 'center', p: 4 }}>
          <Typography variant="h6" color="text.secondary">
            No users found
          </Typography>
          <Button
            variant="outlined"
            onClick={() => fetchUsers(0, 10, searchTerm)}
            sx={{ mt: 2 }}
          >
            Try Again
          </Button>
        </Box>
      ) : (
        renderDataGrid()
      )}

      {/* Add/Edit User Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {editingUser ? 'Edit User' : 'Add New User'}
        </DialogTitle>
        <DialogContent>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <TextField
              label="Username"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              fullWidth
              required
            />
            <TextField
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              fullWidth
              required
            />
            <Stack direction="row" spacing={2}>
              <TextField
                label="First Name"
                value={formData.first_name}
                onChange={(e) => setFormData({ ...formData, first_name: e.target.value })}
                fullWidth
                required
              />
              <TextField
                label="Last Name"
                value={formData.last_name}
                onChange={(e) => setFormData({ ...formData, last_name: e.target.value })}
                fullWidth
                required
              />
            </Stack>
            <FormControl fullWidth>
              <InputLabel>Role</InputLabel>
              <Select
                value={formData.role}
                label="Role"
                onChange={(e) => setFormData({ ...formData, role: e.target.value })}
              >
                <MenuItem value="user">User</MenuItem>
                <MenuItem value="admin">Admin</MenuItem>
              </Select>
            </FormControl>
            <TextField
              label={editingUser ? "New Password (leave blank to keep current)" : "Password"}
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              fullWidth
              required={!editingUser}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSaveUser} variant="contained">
            {editingUser ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <DeleteConfirmationDialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
        onConfirm={handleConfirmDelete}
        itemName={`${userToDelete?.first_name} ${userToDelete?.last_name}`}
        itemType="user"
        title="Delete User"
        confirmButtonText="Delete User"
        severity="error"
      />
    </Stack>
  );
} 