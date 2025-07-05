import { CreateChartRequest, ChartPreviewResponse } from '../../types/chart';
import { API_CONFIG } from '../../config/api';
import { SourceDetail } from '../../types/source';

export const chartApi = {
    // Create new chart
    createChart: async (chartData: CreateChartRequest) => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(chartData),
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    },

    // Preview chart data
    previewChartData: async (chartData: CreateChartRequest): Promise<ChartPreviewResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}/preview`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(chartData),
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    },

    // Get chart by ID
    getChart: async (chartId: string) => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}/${chartId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
            },
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    },

    // Update chart
    updateChart: async (chartId: string, chartData: CreateChartRequest) => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}/${chartId}`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(chartData),
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    },

    // Delete chart
    deleteChart: async (chartId: string) => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}/${chartId}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
            },
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    },

    // Clone chart
    cloneChart: async (chartId: string) => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS_CLONE.replace(':id', chartId)}`, {
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
        
        return response.json();
    },

    // Get all charts
    getCharts: async () => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHARTS}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
            },
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    },

    getSourcesList: async (): Promise<{ success: boolean; data?: any[]; message?: string }> => {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}?page=0&limit=1000`, {
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
            if (data.success) {
                return {
                    success: true,
                    data: data.result.sources || []
                };
            } else {
                return {
                    success: false,
                    message: data.message || 'Failed to fetch sources'
                };
            }
        } catch (error) {
            console.error('Error fetching sources:', error);
            return {
                success: false,
                message: error instanceof Error ? error.message : 'Failed to fetch sources'
            };
        }
    },

    getDetailsSource: async (sourceId: string): Promise<SourceDetail> => {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SOURCES}/${sourceId}`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return response.json().then(data => data.result);
        } catch (error) {
            console.error('Error fetching source details:', error);
            throw error;
        }
    },
    
    // Convert queryOption to SQL query
    convertQuery: async (queryOption: any): Promise<{ success: boolean; result?: string; message?: string }> => {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/charts/convert-query`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify(queryOption),
            });
            const data = await response.json();
            return {
                success: data.success,
                result: data.result,
                message: data.message
            };
        } catch (error) {
            return {
                success: false,
                message: error instanceof Error ? error.message : 'Failed to convert query'
            };
        }
    },

    // Preview data with SQL query and fields
    previewData: async (body: { sql_query: string; fields: any[]; group_by?: any[] }): Promise<any> => {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}/data-processing/charts/preview-data`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify(body),
            });
            return response.json();
        } catch (error) {
            return {
                success: false,
                message: error instanceof Error ? error.message : 'Failed to preview data'
            };
        }
    },
}; 