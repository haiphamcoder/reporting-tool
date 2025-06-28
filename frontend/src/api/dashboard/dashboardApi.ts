import { API_CONFIG } from '../../config/api';

// Types for dashboard data
export interface TimeSeriesData {
  labels: string[];
  sources: number[];
  charts: number[];
  reports: number[];
}

export interface ConnectorStats {
  csv: number;
  excel: number;
  api: number;
  googleSheets: number;
}

export interface PerformanceMetrics {
  avgLoadTime: number;
  successRate: number;
  activeUsers: number;
}

export interface ActivityItem {
  id: string;
  type: 'source' | 'chart' | 'report';
  action: 'created' | 'updated' | 'deleted';
  name: string;
  timestamp: string;
  user: string;
}

export interface DashboardData {
  timeSeriesData: TimeSeriesData;
  connectorStats: ConnectorStats;
  recentActivity: ActivityItem[];
  performanceMetrics: PerformanceMetrics;
}

export const dashboardApi = {
  // Get time series data for growth trends
  getTimeSeriesData: async (): Promise<TimeSeriesData> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/dashboard/time-series`, {
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
        return data.result;
      } else {
        throw new Error('Invalid time series data format');
      }
    } catch (error) {
      console.error('Error fetching time series data:', error);
      // Return fallback data for development
      return {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        sources: [12, 19, 15, 25, 22, 30],
        charts: [8, 12, 10, 18, 15, 22],
        reports: [5, 8, 6, 12, 10, 15]
      };
    }
  },

  // Get connector statistics
  getConnectorStats: async (): Promise<ConnectorStats> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/dashboard/connector-stats`, {
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
        return data.result;
      } else {
        throw new Error('Invalid connector stats format');
      }
    } catch (error) {
      console.error('Error fetching connector stats:', error);
      // Return fallback data for development
      return {
        csv: 15,
        excel: 12,
        api: 8,
        googleSheets: 5
      };
    }
  },

  // Get recent activity feed
  getRecentActivity: async (limit: number = 10): Promise<ActivityItem[]> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/dashboard/recent-activity?limit=${limit}`, {
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
        return data.result;
      } else {
        throw new Error('Invalid activity data format');
      }
    } catch (error) {
      console.error('Error fetching recent activity:', error);
      // Return fallback data for development
      return [
        {
          id: '1',
          type: 'source',
          action: 'created',
          name: 'Sales Data Q1',
          timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
          user: 'John Doe'
        },
        {
          id: '2',
          type: 'chart',
          action: 'updated',
          name: 'Revenue Chart',
          timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000).toISOString(),
          user: 'Jane Smith'
        },
        {
          id: '3',
          type: 'report',
          action: 'created',
          name: 'Monthly Report',
          timestamp: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(),
          user: 'Mike Johnson'
        }
      ];
    }
  },

  // Get performance metrics
  getPerformanceMetrics: async (): Promise<PerformanceMetrics> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/dashboard/performance`, {
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
        return data.result;
      } else {
        throw new Error('Invalid performance metrics format');
      }
    } catch (error) {
      console.error('Error fetching performance metrics:', error);
      // Return fallback data for development
      return {
        avgLoadTime: 1.2,
        successRate: 98.5,
        activeUsers: 45
      };
    }
  },

  // Get top sources by usage/creation
  getTopSources: async (limit: number = 5): Promise<any[]> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/sources?page=0&limit=${limit}&sort=created_at,desc`, {
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
      if (data.success && data.result && data.result.sources) {
        return data.result.sources;
      } else {
        return [];
      }
    } catch (error) {
      console.error('Error fetching top sources:', error);
      return [];
    }
  },

  // Get top charts by usage/creation
  getTopCharts: async (limit: number = 5): Promise<any[]> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/charts?page=0&limit=${limit}&sort=created_at,desc`, {
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
      if (data.success && data.result && data.result.charts) {
        return data.result.charts;
      } else {
        return [];
      }
    } catch (error) {
      console.error('Error fetching top charts:', error);
      return [];
    }
  },

  // Get top reports by usage/creation
  getTopReports: async (limit: number = 5): Promise<any[]> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/reporting/reports?page=0&limit=${limit}&sort=created_at,desc`, {
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
      if (data.success && data.result && data.result.reports) {
        return data.result.reports;
      } else {
        return [];
      }
    } catch (error) {
      console.error('Error fetching top reports:', error);
      return [];
    }
  },

  // Get all dashboard data in one call
  getAllDashboardData: async (): Promise<DashboardData> => {
    try {
      const [
        timeSeriesData,
        connectorStats,
        recentActivity,
        performanceMetrics
      ] = await Promise.all([
        dashboardApi.getTimeSeriesData(),
        dashboardApi.getConnectorStats(),
        dashboardApi.getRecentActivity(),
        dashboardApi.getPerformanceMetrics()
      ]);

      return {
        timeSeriesData,
        connectorStats,
        recentActivity,
        performanceMetrics
      };
    } catch (error) {
      console.error('Error fetching all dashboard data:', error);
      throw error;
    }
  }
}; 