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

  console.log('MainGrid - RENDER - currentContent:', currentContent, 'section:', section);

  // Fetch statistics when on home page only
  useEffect(() => {
    console.log('MainGrid useEffect - currentContent:', currentContent, 'hasCalledStatsRef.current:', hasCalledStatsRef.current);
    if (currentContent === 'home' && !hasCalledStatsRef.current) {
      console.log('Calling refreshStatistics for home page');
      hasCalledStatsRef.current = true;
      refreshStatistics();
    } else if (currentContent !== 'home') {
      // Reset flag when not on home page
      hasCalledStatsRef.current = false;
    }
  }, [currentContent, refreshStatistics]);

  console.log('MainGrid - About to render content for:', currentContent);

  const renderContent = () => {
    console.log('MainGrid renderContent - currentContent:', currentContent);
    switch (currentContent) {
      case 'home':
        console.log('Rendering Home component');
        return <Home />;
      case 'sources':
        console.log('Rendering Sources component');
        return <Sources />;
      case 'charts':
        console.log('Rendering Charts component');
        return <Charts />;
      case 'reports':
        console.log('Rendering Reports component');
        return <Reports />;
      case 'users':
        console.log('Rendering UserManagement component');
        return <UserManagement />;
      case 'settings':
        console.log('Rendering Settings component');
        return <Settings />;
      default:
        console.log('Rendering default Home component');
        return <Home />;
    }
  };

  return (
    <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>
      {renderContent()}
    </Box>
  );
}

