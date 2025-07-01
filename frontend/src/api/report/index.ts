import { ReportDetail } from '../../types/report';

const API_BASE = '/api/reports';

// Lấy chi tiết report
export async function getReportDetail(reportId: string): Promise<ReportDetail> {
    const res = await fetch(`${API_BASE}/${reportId}`, {
        credentials: 'include',
    });
    if (!res.ok) throw new Error('Failed to fetch report detail');
    const data = await res.json();
    if (!data.success) throw new Error(data.message || 'Failed to fetch report detail');
    return data.result;
}

// Tạo mới report
export async function createReport(report: Partial<ReportDetail>): Promise<ReportDetail> {
    const res = await fetch(`${API_BASE}`, {
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
    const res = await fetch(`${API_BASE}/${reportId}`, {
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