import { ReportDetail } from '../../types/report';
import { API_CONFIG } from '../../config/api';

// Lấy chi tiết report
export async function getReportDetail(reportId: string): Promise<ReportDetail> {
    const res = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${reportId}`, {
        credentials: 'include',
    });
    if (!res.ok) throw new Error('Failed to fetch report detail');
    const data = await res.json();
    if (!data.success) throw new Error(data.message || 'Failed to fetch report detail');
    return data.result;
}

// Tạo mới report
export async function createReport(report: Partial<ReportDetail>): Promise<ReportDetail> {
    const res = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(report),
    });
    if (!res.ok) throw new Error('Failed to create report');
    const data = await res.json();
    if (!data.success) throw new Error(data.message || 'Failed to create report');
    return data.result;
}

// Cập nhật report
export async function updateReport(reportId: string, data: Partial<ReportDetail>): Promise<ReportDetail> {
    const res = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${reportId}`, {
        method: 'PUT',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to update report');
    const result = await res.json();
    if (!result.success) throw new Error(result.message || 'Failed to update report');
    return result.result;
}

// Clone report
export async function cloneReport(reportId: string): Promise<any> {
    const res = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS_CLONE.replace(':id', reportId)}`, {
        method: 'GET',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
    });
    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
    return res.json();
}

// Get reports list with pagination and search
export async function getReports(page: number = 0, pageSize: number = 10, search: string = ''): Promise<any> {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('limit', pageSize.toString());
    if (search.trim()) {
        params.append('search', search.trim());
    }
    
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}?${params.toString()}`, {
        method: 'GET',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
    });
    if (!response.ok) {
        throw new Error('Failed to fetch reports');
    }
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
        throw new TypeError("Response was not JSON");
    }
    return response.json();
}

// Delete report
export async function deleteReport(reportId: string): Promise<any> {
    const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REPORTS}/${reportId}`, {
        method: 'DELETE',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
} 