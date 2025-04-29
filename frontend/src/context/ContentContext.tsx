import { createContext, useContext, useState, ReactNode } from 'react';

type ContentType = 'home' | 'sources' | 'charts' | 'reports' | 'settings' | 'about' | 'feedback';

interface ContentContextType {
  currentContent: ContentType;
  setCurrentContent: (content: ContentType) => void;
}

const ContentContext = createContext<ContentContextType | undefined>(undefined);

export function ContentProvider({ children }: { children: ReactNode }) {
  const [currentContent, setCurrentContent] = useState<ContentType>('home');

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