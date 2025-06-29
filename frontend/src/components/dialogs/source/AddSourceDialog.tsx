import React, { useEffect, useState } from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import CardAlert from '../../CardAlert';
import { sourceApi } from '../../../api/source';
import {
  Step1BasicInfo,
  Step2ConnectionConfig,
  Step3SchemaMapping,
  Step4Confirmation
} from './index';

interface AddSourceDialogProps {
  open: boolean;
  onClose: () => void;
  addStep: number;
  setAddStep: (step: number) => void;
  addForm: any;
  setAddForm: (form: any) => void;
  currentContent: string;
  sourcesData: any[];
  chartsData: any[];
  handleAddNext: () => void;
  handleAddBack: () => void;
  onSourceCreated?: () => void; // Callback để reload sources
}

const AddSourceDialog: React.FC<AddSourceDialogProps> = ({
  open,
  onClose,
  addStep,
  setAddStep,
  addForm,
  setAddForm,
  currentContent,
  handleAddNext,
  handleAddBack,
  onSourceCreated,
}) => {
  // State cho CardAlert
  const [showSuccessAlert, setShowSuccessAlert] = useState(false);
  const [showErrorAlert, setShowErrorAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState('');

  const handleAddFormChange = (field: string, value: any) => {
    setAddForm((prev: any) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setAddForm((prev: any) => ({
        ...prev,
        selectedFile: file,
        connectionConfig: {
          ...prev.connectionConfig,
          fileName: file.name,
        }
      }));
    }
  };

  const handleRemoveFile = () => {
    setAddForm((prev: any) => ({
      ...prev,
      selectedFile: null,
      connectionConfig: {
        ...prev.connectionConfig,
        fileName: null,
      }
    }));
  };

  const getStepContent = () => {
    if (currentContent === 'sources') {
      switch (addStep) {
        case 1:
          return (
            <Step1BasicInfo
              addForm={addForm}
              handleAddFormChange={handleAddFormChange}
            />
          );
        case 2:
          return (
            <Step2ConnectionConfig
              addForm={addForm}
              handleAddFormChange={handleAddFormChange}
              handleFileChange={handleFileChange}
              handleRemoveFile={handleRemoveFile}
            />
          );
        case 3:
          return (
            <Step3SchemaMapping
              addForm={addForm}
              setAddForm={setAddForm}
            />
          );
        case 4:
          return (
            <Step4Confirmation
              addForm={addForm}
            />
          );
        default:
          return null;
      }
    } else if (currentContent === 'charts') {
      // ... (giữ nguyên logic cho charts và reports như trong MainGrid)
      return null;
    } else if (currentContent === 'reports') {
      // ... (giữ nguyên logic cho reports như trong MainGrid)
      return null;
    }
    return null;
  };

  // Điều kiện disabled cho nút Next/Create
  const isDisabled = () => {
    if (currentContent === 'sources') {
      if (addStep === 1) return !addForm.name || !addForm.connectorType;
      if (addStep === 2) {
        if ((addForm.connectorType === 'csv' || addForm.connectorType === 'excel') && !addForm.selectedFile) return true;
        if (addForm.connectorType === 'mysql' && (!addForm.connectionConfig.host || !addForm.connectionConfig.port || !addForm.connectionConfig.database || !addForm.connectionConfig.username || !addForm.connectionConfig.password)) return true;
        if (addForm.connectorType === 'gsheet' && !addForm.connectionConfig.url) return true;
      }
    }
    return false;
  };

  // Mapping connectorType string to number
  const connectorTypeToNumber = (type: string) => {
    switch (type) {
      case 'csv': return 1;
      case 'excel': return 2;
      case 'mysql': return 3;
      case 'gsheet': return 4;
      default: return 0;
    }
  };

  // Custom handleAddNext để gọi API ở step 1
  const handleAddNextInternal = async () => {
    if (currentContent === 'sources' && addStep === 1) {
      try {
        const data = await sourceApi.initSource({
          name: addForm.name,
          connector_type: connectorTypeToNumber(addForm.connectorType),
          description: addForm.description || '',
        });
        
        // Nếu thành công thì lưu id vào addForm
        setAddForm((prev: any) => ({ ...prev, sourceId: data.result.id }));
        setAddStep(addStep + 1);
      } catch (err: any) {
        setAlertMessage(err.message || 'Failed to init source');
        setShowErrorAlert(true);
      }
    } else if (currentContent === 'sources' && addStep === 2 && connectorTypeToNumber(addForm.connectorType) === 1) {
      // Step 2, CSV, upload file
      if (!addForm.selectedFile || !addForm.sourceId) {
        setAlertMessage('Please select a file and ensure source is initialized.');
        setShowErrorAlert(true);
        return;
      }
      try {
        await sourceApi.uploadFile(addForm.sourceId, addForm.selectedFile);
        setAddStep(addStep + 1);
      } catch (err: any) {
        setAlertMessage(err.message || 'Failed to upload file');
        setShowErrorAlert(true);
      }
    } else if (currentContent === 'sources' && addStep === 3) {
      // Step 3: Confirm schema và ingest data
      if (!addForm.sourceId || !addForm.schema) {
        setAlertMessage('Missing source or schema');
        setShowErrorAlert(true);
        return;
      }
      try {
        // 1. Confirm schema
        await sourceApi.confirmSchema({
          id: addForm.sourceId,
          mapping: addForm.schema,
        });
        
        // 2. Ingest data
        await sourceApi.submitImport(addForm.sourceId);
        
        // Đóng dialog trước
        onClose();
        
        // Sau đó hiển thị thông báo thành công và reload sources
        setTimeout(() => {
          setAlertMessage('Source created and ingest job submitted!');
          setShowSuccessAlert(true);
          
          // Gọi callback để reload sources
          if (onSourceCreated) {
            onSourceCreated();
          }
        }, 300); // Delay 300ms để đảm bảo dialog đã đóng hoàn toàn
        
      } catch (err: any) {
        setAlertMessage(err.message || 'Failed to create source');
        setShowErrorAlert(true);
      }
    } else {
      handleAddNext();
    }
  };

  // Fetch schema khi vào step 3
  useEffect(() => {
    if (currentContent === 'sources' && addStep === 3 && addForm.sourceId && !addForm.schema) {
      (async () => {
        try {
          const data = await sourceApi.getSchema(addForm.sourceId);
          setAddForm((prev: any) => ({ ...prev, schema: data.result }));
        } catch (err: any) {
          setAlertMessage(err.message || 'Failed to fetch schema');
          setShowErrorAlert(true);
        }
      })();
    }
  }, [addStep, currentContent, addForm.sourceId, addForm.schema, setAddForm]);

  // Reset alert states khi dialog đóng
  useEffect(() => {
    if (!open) {
      setShowSuccessAlert(false);
      setShowErrorAlert(false);
      setAlertMessage('');
    }
  }, [open]);

  return (
    <>
      <Dialog
        open={open}
        onClose={onClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Add {currentContent?.charAt(0).toUpperCase() + currentContent?.slice(1)}
        </DialogTitle>
        <DialogContent>
          {getStepContent()}
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose}>Cancel</Button>
          {addStep > 1 && (
            <Button onClick={handleAddBack}>Back</Button>
          )}
          <Button
            onClick={handleAddNextInternal}
            variant="contained"
            disabled={isDisabled()}
          >
            {currentContent === 'sources' && addStep === 3 ? 'Create' : 'Next'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* CardAlert cho thông báo thành công */}
      <CardAlert
        open={showSuccessAlert}
        severity="success"
        message={alertMessage}
        onClose={() => setShowSuccessAlert(false)}
        autoHideDuration={3000}
        position="bottom-right"
      />

      {/* CardAlert cho thông báo lỗi */}
      <CardAlert
        open={showErrorAlert}
        severity="error"
        message={alertMessage}
        onClose={() => setShowErrorAlert(false)}
        autoHideDuration={5000}
        position="bottom-right"
      />
    </>
  );
};

export default AddSourceDialog; 