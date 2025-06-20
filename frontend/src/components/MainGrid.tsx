import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { useContent } from '../context/ContentContext';
import { useStatistics } from '../context/StatisticsContext';
import Button from '@mui/material/Button';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../config/api';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Settings from './modules/Settings';
import Home from './modules/Home';
import Sources from './modules/Sources';
import Charts from './modules/Charts';
import Reports from './modules/Reports';
import { GridPaginationModel } from '@mui/x-data-grid';
import SourcePreview from './modules/SourcePreview';
import SourceEdit from './modules/SourceEdit';
import AddSourceDialog from './modules/AddSourceDialog';
import { SourceSummary } from '../types/source';
import { useNavigate, useParams } from 'react-router-dom';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

interface SourcesMetadata {
    total_elements: number;
    number_of_elements: number;
    total_pages: number;
    current_page: number;
    page_size: number;
}

interface PreviewData {
    schema: {
        field_name: string;
        field_mapping: string;
        field_type: string;
        is_hidden: boolean;
    }[];
    records: any[];
}

const chartsRows = [
  { id: 1, name: 'Chart 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Chart 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Chart 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

const reportsRows = [
  { id: 1, name: 'Report 1', type: 'Type 1', schedule: 'Daily', lastRun: '2023-01-01' },
  { id: 2, name: 'Report 2', type: 'Type 2', schedule: 'Weekly', lastRun: '2023-01-02' },
  { id: 3, name: 'Report 3', type: 'Type 3', schedule: 'Monthly', lastRun: '2023-01-03' },
];

export default function MainGrid() {
  const { currentContent } = useContent();
  const { refreshStatistics } = useStatistics();
  const [selectedItem, setSelectedItem] = useState<any>(null);
  const [showDetails, setShowDetails] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [, setEditForm] = useState<any>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState<any>(null);
  const [addSourceOpen] = useState(false);
  const [, setConnectors] = useState<any[]>([]);
  const [, setLoadingConnectors] = useState(false);
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  const [needsStatisticsRefresh, setNeedsStatisticsRefresh] = useState(false);
  const [addForm, setAddForm] = useState<any>({
    name: '',
    type: '',
    schedule: '',
    chartType: '',
    dataSource: '',
    description: '',
    connectorType: '',
    connectionConfig: {},
    schemaMapping: {},
    previewData: null,
    selectedFile: null,
    mode: 'normal',
    displayType: 'chart',
    selectedChartType: '',
    sources: [],
    query: {
      filters: [],
      groupBy: [],
      sortBy: [],
      joins: [],
    },
    sqlQuery: '',
    recipients: [],
    format: 'pdf',
    charts: [],
    emailSubject: '',
    emailBody: '',
    advancedSettings: {
      includeDataTable: true,
      includeChart: true,
      pageSize: 'A4',
      orientation: 'portrait',
      header: '',
      footer: '',
    }
  });
  const [addStep, setAddStep] = useState(1);
  const [previewData, setPreviewData] = useState<PreviewData | null>(null);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [previewPagination, setPreviewPagination] = useState({
    page: 0,
    pageSize: 10,
    totalRows: 0
  });

  // Add state for table data
  const [sourcesData, setSourcesData] = useState<SourceSummary[]>([]);
  const [sourcesMetadata, setSourcesMetadata] = useState<SourcesMetadata>({
    total_elements: 0,
    number_of_elements: 0,
    total_pages: 0,
    current_page: 0,
    page_size: 10
  });
  const [chartsData] = useState(chartsRows);
  const [reportsData] = useState(reportsRows);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { source_id } = useParams();

  // Auto show Data Preview if source_id is present in URL
  useEffect(() => {
    if (source_id && sourcesData.length > 0) {
      const found = sourcesData.find(s => s.id.toString() === source_id);
      if (found) {
        setSelectedItem(found);
        setShowDetails(true);
        fetchSourcePreview(source_id);
      }
    } else if (!source_id) {
      setShowDetails(false);
      setSelectedItem(null);
      setPreviewData(null);
    }
  }, [source_id, sourcesData]);

  const fetchSources = async (page: number = 0, pageSize: number = 10) => {
    try {
      setLoading(true);
      console.log('Fetching sources with page:', page, 'pageSize:', pageSize);
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}?page=${page}&size=${pageSize}`, {
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

      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json')) {
        throw new TypeError("Response was not JSON");
      }

      const data = await response.json();
      console.log('API Response:', data);
      
      if (data.success) {
        // Convert all IDs to strings to preserve precision
        const processedSources = data.result.sources.map((source: any) => ({
          ...source,
          id: source.id.toString()
        }));
        console.log('Setting sources data:', processedSources);
        console.log('Setting metadata:', data.result.metadata);
        setSourcesData(processedSources);
        setSourcesMetadata(data.result.metadata);
        setNeedsStatisticsRefresh(true);
      } else {
        console.error('API returned error:', data.message);
      }
    } catch (error) {
      console.error('Error fetching sources:', error);
      // Set empty data on error
      setSourcesData([]);
      setSourcesMetadata({
        total_elements: 0,
        number_of_elements: 0,
        total_pages: 0,
        current_page: 0,
        page_size: pageSize
      });
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (model: GridPaginationModel) => {
    console.log('Page change:', model);
    fetchSources(model.page, model.pageSize);
  };

  useEffect(() => {
    if (currentContent === 'sources') {
      fetchSources();
    }
    if (currentContent === 'home' && needsStatisticsRefresh) {
      refreshStatistics();
      setNeedsStatisticsRefresh(false);
    }
  }, [currentContent, needsStatisticsRefresh]);

  // Fetch statistics on component mount only once
  useEffect(() => {
    // Only fetch if we're on home page initially
    if (currentContent === 'home') {
      refreshStatistics();
    }
  }, []);

  const fetchSourcePreview = async (sourceId: string, page: number = 0, pageSize: number = 10) => {
    try {
      setPreviewLoading(true);
      console.log('Fetching source preview with ID:', sourceId, 'page:', page, 'pageSize:', pageSize);
      
      const url = `${API_CONFIG.BASE_URL}/data-processing/sources/${sourceId}/preview?page=${page}&limit=${pageSize}`;
      console.log('API URL:', url);

      const response = await fetch(url, {
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
      console.log('API Response data:', data);

      if (data.success) {
        setPreviewData(data.result);
        // Assuming the API returns total count in metadata
        setPreviewPagination(prev => ({
          ...prev,
          totalRows: data.result.total_count || 0
        }));
      } else {
        console.error('API returned error:', data.message);
      }
    } catch (error) {
      console.error('Error fetching source preview:', error);
    } finally {
      setPreviewLoading(false);
    }
  };

  const handleRowDoubleClick = async (params: any) => {
    try {
      const sourceId = params.row.id.toString();
      setSelectedItem(params.row);
      setShowDetails(true);
      if (source_id !== sourceId) {
        navigate(`/dashboard/sources/${sourceId}/view-data`);
      }
      await fetchSourcePreview(sourceId);
    } catch (error) {
      console.error('Error handling row double click:', error);
    }
  };

  const handlePreviewPageChange = async (page: number, pageSize: number) => {
    if (!selectedItem) return;
    
    setPreviewPagination(prev => ({
      ...prev,
      page,
      pageSize
    }));
    
    await fetchSourcePreview(selectedItem.id.toString(), page, pageSize);
  };

  const handleBackToList = () => {
    setShowDetails(false);
    setSelectedItem(null);
    setPreviewData(null);
    setPreviewPagination({
      page: 0,
      pageSize: 10,
      totalRows: 0
    });
    navigate('/dashboard/sources');
  };

  const handleEditClick = (row: SourceSummary) => {
    setSelectedItem(row);
    setShowEdit(true);
  };

  const handleEditClose = () => {
    setShowEdit(false);
    setEditForm(null);
  };

  const handleEditSave = async (updatedSource: any) => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}/${updatedSource.id}`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedSource),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      if (data.success) {
        // Refresh the sources list after successful update
        fetchSources(sourcesMetadata.current_page, sourcesMetadata.page_size);
        setNeedsStatisticsRefresh(true);
        handleEditClose();
      }
    } catch (error) {
      console.error('Error updating source:', error);
    }
  };

  const handleDeleteClick = (row: any) => {
    setItemToDelete(row);
    setDeleteDialogOpen(true);
  };

  const handleDeleteClose = () => {
    setDeleteDialogOpen(false);
    setItemToDelete(null);
  };

  const handleDeleteConfirm = async () => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}/${itemToDelete.id}`, {
        method: 'DELETE',
        credentials: 'include',
      });
      const data = await response.json();
      if (data.success) {
        // Refresh the sources list after successful deletion
        fetchSources(sourcesMetadata.current_page, sourcesMetadata.page_size);
        setNeedsStatisticsRefresh(true);
      }
    } catch (error) {
      console.error('Error deleting source:', error);
    }
    handleDeleteClose();
  };

  const handleAddClick = () => {
    setAddDialogOpen(true);
    setAddStep(1);
    setAddForm({
      name: '',
      type: '',
      schedule: '',
      chartType: '',
      dataSource: '',
      description: '',
      connectorType: '',
      connectionConfig: {},
      schemaMapping: {},
      previewData: null,
      selectedFile: null,
      mode: 'normal',
      displayType: 'chart',
      selectedChartType: '',
      sources: [],
      query: {
        filters: [],
        groupBy: [],
        sortBy: [],
        joins: [],
      },
      sqlQuery: '',
      recipients: [],
      format: 'pdf',
      charts: [],
      emailSubject: '',
      emailBody: '',
      advancedSettings: {
        includeDataTable: true,
        includeChart: true,
        pageSize: 'A4',
        orientation: 'portrait',
        header: '',
        footer: '',
      }
    });
  };

  const handleRefresh = () => {
    fetchSources(sourcesMetadata.current_page, sourcesMetadata.page_size);
    setNeedsStatisticsRefresh(true);
  };

  const handleAddNext = async () => {
    if (currentContent === 'sources') {
      if (addStep === 1) {
        setAddStep(2);
      } else if (addStep === 2) {
        setAddStep(3);
      } else if (addStep === 3) {
        // Handle source creation
        try {
          console.log('Creating source:', addForm);
          const formData = new FormData();
          formData.append('name', addForm.name);
          formData.append('connectorType', addForm.connectorType);
          
          if (addForm.selectedFile) {
            formData.append('file', addForm.selectedFile);
          }
          
          if (addForm.connectionConfig) {
            Object.keys(addForm.connectionConfig).forEach(key => {
              if (addForm.connectionConfig[key]) {
                formData.append(key, addForm.connectionConfig[key]);
              }
            });
          }

          const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}`, {
            method: 'POST',
            credentials: 'include',
            body: formData,
          });

          if (response.ok) {
            const data = await response.json();
            if (data.success) {
              // Refresh sources list and statistics
              fetchSources(sourcesMetadata.current_page, sourcesMetadata.page_size);
              setNeedsStatisticsRefresh(true);
              handleAddClose();
            } else {
              console.error('Failed to create source:', data.message);
            }
          } else {
            console.error('Failed to create source:', response.statusText);
          }
        } catch (error) {
          console.error('Error creating source:', error);
        }
      }
    } else if (currentContent === 'charts') {
      if (addStep === 1) {
        setAddStep(2);
      } else if (addStep === 2) {
        setAddStep(3);
      } else if (addStep === 3) {
        // Handle chart creation
        console.log('Creating chart:', addForm);
        handleAddClose();
      }
    } else if (currentContent === 'reports') {
      if (addStep === 1) {
        setAddStep(2);
      } else if (addStep === 2) {
        setAddStep(3);
      } else if (addStep === 3) {
        // Handle report creation
        console.log('Creating report:', addForm);
        handleAddClose();
      }
    }
  };

  const handleAddBack = () => {
    setAddStep(addStep - 1);
  };

  const handleAddClose = () => {
    setAddDialogOpen(false);
    setAddStep(1);
    setAddForm({
      name: '',
      type: '',
      schedule: '',
      chartType: '',
      dataSource: '',
      description: '',
      connectorType: '',
      connectionConfig: {},
      schemaMapping: {},
      previewData: null,
      selectedFile: null,
      mode: 'normal',
      displayType: 'chart',
      selectedChartType: '',
      sources: [],
      query: {
        filters: [],
        groupBy: [],
        sortBy: [],
        joins: [],
      },
      sqlQuery: '',
      recipients: [],
      format: 'pdf',
      charts: [],
      emailSubject: '',
      emailBody: '',
      advancedSettings: {
        includeDataTable: true,
        includeChart: true,
        pageSize: 'A4',
        orientation: 'portrait',
        header: '',
        footer: '',
      }
    });
  };

  useEffect(() => {
    if (addSourceOpen) {
      setLoadingConnectors(true);
      fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CONNECTOR}`, {
        method: 'GET',
        credentials: 'include',
      })
        .then((res) => res.json())
        .then((data) => {
          console.log(data);
          setConnectors(data.data || []);
          setLoadingConnectors(false);
        })
        .catch(() => setLoadingConnectors(false));
    }
  }, [addSourceOpen]);

  const renderDeleteDialog = () => {
    if (!itemToDelete) return null;

    return (
      <Dialog open={deleteDialogOpen} onClose={handleDeleteClose}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete {itemToDelete.name}?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteClose}>Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    );
  };

  const renderDetails = () => {
    if (!selectedItem || !previewData) return null;

    return (
      <SourcePreview
        source={selectedItem}
        previewData={previewData}
        onBack={handleBackToList}
        onPageChange={handlePreviewPageChange}
        totalRows={previewPagination.totalRows}
        pageSize={previewPagination.pageSize}
        currentPage={previewPagination.page}
        loading={previewLoading}
      />
    );
  };

  const renderContent = () => {
    if (showEdit && selectedItem && selectedItem.id) {
      return (
        <SourceEdit
          sourceId={selectedItem.id}
          onBack={handleEditClose}
          onSave={handleEditSave}
        />
      );
    }

    if (showDetails) {
      return renderDetails();
    }

    switch (currentContent) {
      case 'home':
        return (
          <Home />
        );
      case 'sources':
        return (
          <Box sx={{ width: '100%' }}>
            {loading ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
                <Typography>Loading...</Typography>
              </Box>
              // <LoadingPage />
            ) : (
              <Sources
                sourcesData={sourcesData}
                metadata={sourcesMetadata}
                onPageChange={handlePageChange}
                handleEditClick={handleEditClick}
                handleDeleteClick={handleDeleteClick}
                handleRowDoubleClick={handleRowDoubleClick}
                handleAddClick={handleAddClick}
                handleRefresh={handleRefresh}
              />
            )}
          </Box>
        );
      case 'charts':
        return (
          <Charts
            chartsData={chartsData}
            handleEditClick={handleEditClick}
            handleDeleteClick={handleDeleteClick}
            handleRowDoubleClick={handleRowDoubleClick}
            handleAddClick={handleAddClick}
          />
        );
      case 'reports':
        return (
          <Reports
            reportsData={reportsData}
            handleEditClick={handleEditClick}
            handleDeleteClick={handleDeleteClick}
            handleRowDoubleClick={handleRowDoubleClick}
            handleAddClick={handleAddClick}
          />
        );
      case 'settings':
        return (
          <Settings />
        );
      default:
        return null;
    }
  };

  // Reset detail state khi đổi màn (Home, Sources, Charts, Reports, ...)
  useEffect(() => {
    setShowDetails(false);
    setSelectedItem(null);
    setPreviewData(null);
    setShowEdit(false);
    setEditForm(null);
    setAddStep(1);
    setAddForm({
      name: '',
      type: '',
      schedule: '',
      chartType: '',
      dataSource: '',
      description: '',
      connectorType: '',
      connectionConfig: {},
      schemaMapping: {},
      previewData: null,
      selectedFile: null,
      mode: 'normal',
      displayType: 'chart',
      selectedChartType: '',
      sources: [],
      query: {
        filters: [],
        groupBy: [],
        sortBy: [],
        joins: [],
      },
      sqlQuery: '',
      recipients: [],
      format: 'pdf',
      charts: [],
      emailSubject: '',
      emailBody: '',
      advancedSettings: {
        includeDataTable: true,
        includeChart: true,
        pageSize: 'A4',
        orientation: 'portrait',
        header: '',
        footer: '',
      }
    });
    setPreviewPagination({
      page: 0,
      pageSize: 10,
      totalRows: 0
    });
  }, [currentContent]);

  return (
    <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>
      {renderContent()}
      {renderDeleteDialog()}
      <AddSourceDialog
        open={addDialogOpen}
        onClose={handleAddClose}
        addStep={addStep}
        setAddStep={setAddStep}
        addForm={addForm}
        setAddForm={setAddForm}
        currentContent={currentContent}
        sourcesData={sourcesData}
        chartsData={chartsData}
        handleAddNext={handleAddNext}
        handleAddBack={handleAddBack}
      />
      <Backdrop open={previewLoading} sx={{ zIndex: 2000 }}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Box>
  );
}

