import React, { createContext, useContext, useState, useEffect } from 'react';
import { API_CONFIG } from '../config/api';
interface Statistics {
  sources: {
    count: number;
    trend: 'up' | 'down' | 'neutral';
    data: number[];
  };
  charts: {
    count: number;
    trend: 'up' | 'down' | 'neutral';
    data: number[];
  };
  reports: {
    count: number;
    trend: 'up' | 'down' | 'neutral';
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

  const fetchStatistics = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.STATISTICS}`);
      const data = await response.json();
      setStatistics(data);
    } catch (err) {
      setError('Failed to fetch statistics');
      console.error('Error fetching statistics:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStatistics();
  }, []);

  return (
    <StatisticsContext.Provider
      value={{
        statistics,
        loading,
        error,
        refreshStatistics: fetchStatistics,
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