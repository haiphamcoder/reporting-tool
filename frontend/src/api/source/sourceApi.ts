import { API_CONFIG } from '../../config/api';
import {
  SourcesResponse,
  InitSourceRequest,
  InitSourceResponse,
  GetSchemaResponse,
  ConfirmSchemaRequest,
  ConfirmSchemaResponse,
  SubmitImportResponse,
  SourceDetailsResponse,
  GetExcelSheetsResponse,
  ConfirmSheetRequest,
  ConfirmSheetResponse,
  UploadFileResponse
} from './types';

export const sourceApi = {
  // Lấy danh sách sources với pagination và search
  getSources: async (page: number = 0, pageSize: number = 10, search: string = ''): Promise<SourcesResponse> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('limit', pageSize.toString());
    if (search.trim()) {
      params.append('search', search.trim());
    }

    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}?${params.toString()}`, {
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

    return response.json();
  },

  // Khởi tạo source mới
  initSource: async (data: InitSourceRequest): Promise<InitSourceResponse> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.INIT_SOURCES}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to init source');
    }

    return response.json();
  },

  // Upload file cho source
  uploadFile: async (sourceId: string, file: File): Promise<UploadFileResponse> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_UPLOAD_FILE}?source-id=${sourceId}`, {
      method: 'POST',
      credentials: 'include',
      body: formData,
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to upload file');
    }

    return response.json();
  },

  // Lấy schema của source
  getSchema: async (sourceId: string): Promise<GetSchemaResponse> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_GET_SCHEMA}/${sourceId}`, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error('Failed to fetch schema');
    }

    return response.json();
  },

  // Xác nhận schema
  confirmSchema: async (data: ConfirmSchemaRequest): Promise<ConfirmSchemaResponse> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_CONFIRM_SCHEMA}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to confirm schema');
    }

    return response.json();
  },

  // Submit import job
  submitImport: async (sourceId: string): Promise<SubmitImportResponse> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_SUBMIT_IMPORT}/${sourceId}`, {
      method: 'POST',
      credentials: 'include',
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to ingest data');
    }

    return response.json();
  },

  // Lấy chi tiết source
  getSourceDetails: async (sourceId: string): Promise<SourceDetailsResponse> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_DETAILS.replace(':id', sourceId)}`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error('Failed to fetch source details');
    }

    return response.json();
  },

  // Xóa source
  deleteSource: async (sourceId: string): Promise<any> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_DETAILS.replace(':id', sourceId)}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete source');
    }

    return response.json();
  },

  // Cập nhật source
  updateSource: async (sourceId: string, data: any): Promise<any> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_DETAILS.replace(':id', sourceId)}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update source');
    }

    return response.json();
  },

  // Preview data của source
  previewSourceData: async (sourceId: string, limit: number = 100): Promise<any> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_PREVIEW.replace(':source_id', sourceId)}?limit=${limit}`, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error('Failed to fetch preview data');
    }

    return response.json();
  },

  // Get Excel sheets
  getExcelSheets: async (filePath: string): Promise<GetExcelSheetsResponse> => {
    const params = new URLSearchParams();
    params.append('file-path', filePath);

    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.EXCEL_GET_SHEETS}?${params.toString()}`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to get Excel sheets');
    }

    return response.json();
  },

  // Confirm Excel sheet
  confirmSheet: async (sourceId: string, data: ConfirmSheetRequest): Promise<ConfirmSheetResponse> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_CONFIRM_SHEET.replace(':id', sourceId)}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to confirm sheet');
    }

    return response.json();
  },

  // Clone source
  cloneSource: async (sourceId: string): Promise<any> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCE_CLONE.replace(':id', sourceId)}`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to clone source');
    }

    return response.json();
  }
}; 