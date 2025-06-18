import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { API_CONFIG } from '../config/api';

interface Statistics {
  sources: {
    count: number;
    data: number[];
  };
  charts: {
    count: number;
    data: number[];
  };
  reports: {
    count: number;
    data: number[];
  };
}

interface StatisticsContextType {
  statistics: Statistics | null;
  loading: boolean;
  error: string | null;
  refreshStatistics: () => Promise<void>;
}

const StatisticsContext = createContext<StatisticsContextType | undefined>(undefined);

export function StatisticsProvider({ children }: { children: React.ReactNode }) {
  const [statistics, setStatistics] = useState<Statistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [hasInitialized, setHasInitialized] = useState(false);

  const fetchStatistics = useCallback(async (forceRefresh = false) => {
    // Prevent duplicate requests unless forced refresh
    if (loading && !forceRefresh) {
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.STATISTICS}`, {
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
      
      // Handle API response format
      if (data.success && data.result) {
        const result = data.result;
        const transformedStatistics: Statistics = {
          sources: {
            count: result.source_statistic?.total || 0,
            data: result.source_statistic?.data || []
          },
          charts: {
            count: result.chart_statistic?.total || 0,
            data: result.chart_statistic?.data || []
          },
          reports: {
            count: result.report_statistic?.total || 0,
            data: result.report_statistic?.data || []
          }
        };
        setStatistics(transformedStatistics);
      } else {
        throw new Error('Invalid statistics data format');
      }
    } catch (err) {
      setError('Failed to fetch statistics');
      console.error('Error fetching statistics:', err);
      
      // Set fallback data to ensure UI always shows something
      setStatistics({
        sources: {
          count: 0,
          data: [0, 0, 0, 0, 0, 0, 0]
        },
        charts: {
          count: 0,
          data: [0, 0, 0, 0, 0, 0, 0]
        },
        reports: {
          count: 0,
          data: [0, 0, 0, 0, 0, 0, 0]
        }
      });
    } finally {
      setLoading(false);
      setHasInitialized(true);
    }
  }, [loading]);

  useEffect(() => {
    if (!hasInitialized) {
      fetchStatistics();
    }
  }, [fetchStatistics, hasInitialized]);

  return (
    <StatisticsContext.Provider
      value={{
        statistics,
        loading,
        error,
        refreshStatistics: () => fetchStatistics(true),
      }}
    >
      {children}
    </StatisticsContext.Provider>
  );
}

export function useStatistics() {
  const context = useContext(StatisticsContext);
  if (context === undefined) {
    throw new Error('useStatistics must be used within a StatisticsProvider');
  }
  return context;
} 