import Box from '@mui/material/Box';
import { useStatistics } from '../context/StatisticsContext';
import { useEffect, useRef } from 'react';
import Settings from './modules/Settings';
import Home from './modules/Home';
import Sources from './modules/Sources';
import Charts from './modules/Charts';
import Reports from './modules/Reports';
import UserManagement from './modules/UserManagement';
import { useParams } from 'react-router-dom';

export default function MainGrid() {
  const { section } = useParams();
  const { refreshStatistics } = useStatistics();
  const hasCalledStatsRef = useRef(false);

  const currentContent = section || 'home';

  // Fetch statistics when on home page only
  useEffect(() => {
    if (currentContent === 'home' && !hasCalledStatsRef.current) {
      hasCalledStatsRef.current = true;
      refreshStatistics();
    } else if (currentContent !== 'home') {
      // Reset flag when not on home page
      hasCalledStatsRef.current = false;
    }
  }, [currentContent, refreshStatistics]);

  const renderContent = () => {
    switch (currentContent) {
      case 'home':
        return <Home />;
      case 'sources':
        return <Sources />;
      case 'charts':
        return <Charts />;
      case 'reports':
        return <Reports />;
      case 'users':
      case 'user-management':
        return <UserManagement />;
      case 'settings':
        return <Settings />;
      default:
        return <Home />;
    }
  };

  return (
    <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>
      {renderContent()}
    </Box>
  );
}

