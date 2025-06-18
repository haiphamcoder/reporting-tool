import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

type ContentType = 'home' | 'sources' | 'charts' | 'reports' | 'settings' | 'about' | 'feedback';

interface ContentContextType {
  currentContent: ContentType;
  setCurrentContent: (content: ContentType) => void;
}

const ContentContext = createContext<ContentContextType | undefined>(undefined);

export function ContentProvider({ children }: { children: ReactNode }) {
  const location = useLocation();
  const navigate = useNavigate();
  const [currentContent, setCurrentContentState] = useState<ContentType>('home');

  // Đồng bộ state với URL
  useEffect(() => {
    const match = location.pathname.match(/\/dashboard\/(\w+)/);
    if (match && match[1] && match[1] !== currentContent) {
      setCurrentContentState(match[1] as ContentType);
    }
  }, [location.pathname]);

  // Khi đổi content, đổi cả URL
  const setCurrentContent = (content: ContentType) => {
    setCurrentContentState(content);
    navigate(`/dashboard/${content}`);
  };

  return (
    <ContentContext.Provider value={{ currentContent, setCurrentContent }}>
      {children}
    </ContentContext.Provider>
  );
}

export function useContent() {
  const context = useContext(ContentContext);
  if (context === undefined) {
    throw new Error('useContent must be used within a ContentProvider');
  }
  return context;
} 