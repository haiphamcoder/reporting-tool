import React, { useEffect, useRef, useState } from 'react';
import { Box, Typography, Chip, Paper } from '@mui/material';
import Editor from '@monaco-editor/react';

interface SqlEditorProps {
    value: string;
    onChange: (value: string) => void;
    sources: any[];
    placeholder?: string;
    height?: string;
}

const SqlEditor: React.FC<SqlEditorProps> = ({
    value,
    onChange,
    sources,
    placeholder = "Enter your SQL query here...",
    height = "400px"
}) => {
    const editorRef = useRef<any>(null);
    const [mounted, setMounted] = useState(false);

    useEffect(() => {
        setMounted(true);
    }, []);

    const handleEditorDidMount = (editor: any) => {
        editorRef.current = editor;
        
        // Add SQL syntax highlighting
        editor.getModel()?.updateOptions({
            tabSize: 2,
            insertSpaces: true,
            wordWrap: 'on'
        });
    };

    const handleEditorChange = (value: string | undefined) => {
        onChange(value || '');
    };

    if (!mounted) {
        return (
            <Paper 
                variant="outlined" 
                sx={{ 
                    p: 2, 
                    height, 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    backgroundColor: 'grey.50'
                }}
            >
                <Typography color="text.secondary">Loading editor...</Typography>
            </Paper>
        );
    }

    return (
        <Box sx={{ width: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="subtitle2" color="text.secondary">
                    SQL Query Editor
                </Typography>
                {sources.length > 0 && (
                    <Chip 
                        label={`${sources.reduce((total, source) => total + (source.mapping?.filter((f: any) => !f.is_hidden).length || 0), 0)} fields available`} 
                        size="small" 
                        variant="outlined" 
                        color="info"
                    />
                )}
            </Box>
            
            <Paper variant="outlined" sx={{ overflow: 'hidden' }}>
                <Editor
                    height={height}
                    defaultLanguage="sql"
                    value={value}
                    onChange={handleEditorChange}
                    onMount={handleEditorDidMount}
                    options={{
                        minimap: { enabled: false },
                        scrollBeyondLastLine: false,
                        fontSize: 14,
                        lineNumbers: 'on',
                        roundedSelection: false,
                        scrollbar: {
                            vertical: 'visible',
                            horizontal: 'visible'
                        },
                        automaticLayout: true,
                        wordWrap: 'on',
                        suggestOnTriggerCharacters: true,
                        quickSuggestions: true,
                        parameterHints: {
                            enabled: true
                        }
                    }}
                    theme="vs-light"
                />
            </Paper>
        </Box>
    );
};

export default SqlEditor; 