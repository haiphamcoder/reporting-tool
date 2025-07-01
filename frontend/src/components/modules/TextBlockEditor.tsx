import React from 'react';
import { LexicalComposer } from '@lexical/react/LexicalComposer';
import { RichTextPlugin } from '@lexical/react/LexicalRichTextPlugin';
import { ContentEditable } from '@lexical/react/LexicalContentEditable';
import { HistoryPlugin } from '@lexical/react/LexicalHistoryPlugin';
import { OnChangePlugin } from '@lexical/react/LexicalOnChangePlugin';
import { $getRoot, $getSelection, EditorState, FORMAT_TEXT_COMMAND, FORMAT_ELEMENT_COMMAND } from 'lexical';
import { Box, Button, Stack, IconButton, Tooltip, Select, MenuItem } from '@mui/material';
import { LexicalErrorBoundary } from '@lexical/react/LexicalErrorBoundary';
import FormatBoldIcon from '@mui/icons-material/FormatBold';
import FormatItalicIcon from '@mui/icons-material/FormatItalic';
import FormatAlignLeftIcon from '@mui/icons-material/FormatAlignLeft';
import FormatAlignCenterIcon from '@mui/icons-material/FormatAlignCenter';
import FormatAlignRightIcon from '@mui/icons-material/FormatAlignRight';
import FormatSizeIcon from '@mui/icons-material/FormatSize';
import { useLexicalComposerContext } from '@lexical/react/LexicalComposerContext';
import { useTheme } from '@mui/material/styles';

interface TextBlockEditorProps {
  value: string;
  onChange: (value: string) => void;
  onSave: () => void;
  onCancel: () => void;
  autoFocus?: boolean;
}

function editorStateToString(editorState: EditorState): string {
  let text = '';
  editorState.read(() => {
    text = $getRoot().getTextContent();
  });
  return text;
}

const FONT_SIZES = [12, 14, 16, 18, 20, 24, 28, 32];

const Toolbar: React.FC = () => {
  const [editor] = useLexicalComposerContext();
  const [align, setAlign] = React.useState('left');
  const [fontSize, setFontSize] = React.useState(16);

  const applyFormat = (format: 'bold' | 'italic') => {
    editor.dispatchCommand(FORMAT_TEXT_COMMAND, format);
  };
  const applyAlign = (align: 'left' | 'center' | 'right') => {
    editor.dispatchCommand(FORMAT_ELEMENT_COMMAND, align);
    setAlign(align);
  };
  const applyFontSize = (size: number) => {
    editor.update(() => {
      const selection = $getSelection();
      if (selection && selection.getNodes) {
        selection.getNodes().forEach((node: any) => {
          if (node.setStyle) {
            node.setStyle(`font-size: ${size}px`);
          }
        });
      }
    });
    setFontSize(size);
  };
  return (
    <Stack direction="row" gap={1} alignItems="center" mb={1}>
      <Tooltip title="Bold"><IconButton size="small" onClick={() => applyFormat('bold')}><FormatBoldIcon /></IconButton></Tooltip>
      <Tooltip title="Italic"><IconButton size="small" onClick={() => applyFormat('italic')}><FormatItalicIcon /></IconButton></Tooltip>
      <Tooltip title="Align Left"><IconButton size="small" onClick={() => applyAlign('left')}><FormatAlignLeftIcon /></IconButton></Tooltip>
      <Tooltip title="Align Center"><IconButton size="small" onClick={() => applyAlign('center')}><FormatAlignCenterIcon /></IconButton></Tooltip>
      <Tooltip title="Align Right"><IconButton size="small" onClick={() => applyAlign('right')}><FormatAlignRightIcon /></IconButton></Tooltip>
      <Tooltip title="Font Size">
        <Select size="small" value={fontSize} onChange={e => applyFontSize(Number(e.target.value))} startAdornment={<FormatSizeIcon />}>
          {FONT_SIZES.map(size => <MenuItem key={size} value={size}>{size}</MenuItem>)}
        </Select>
      </Tooltip>
    </Stack>
  );
};

const TextBlockEditor: React.FC<TextBlockEditorProps> = ({ value, onChange, onSave, onCancel, autoFocus }) => {
  const theme = useTheme();
  const initialConfig = {
    namespace: 'TextBlockEditor',
    theme: {
      paragraph: 'editor-paragraph',
    },
    onError: (error: Error) => {
      throw error;
    },
  };

  // Lexical does not support controlled value directly, so we only set initial value
  return (
    <LexicalComposer initialConfig={initialConfig}>
      <Box
        sx={{
          border: `solid ${theme.palette.divider}`,
          borderRadius: 1,
          background: theme.palette.background.paper,
          position: 'relative',
          minHeight: 100,
          p: 0,
        }}
        
      >
        <Box sx={{ position: 'absolute', top: 8, right: 8, zIndex: 2 }}>
          <Toolbar />
        </Box>
        <RichTextPlugin
          contentEditable={
            <ContentEditable
              style={{
                minHeight: 60,
                outline: 'none',
                fontSize: 16,
                padding: '44px 12px 12px 12px',
                boxSizing: 'border-box',
                background: theme.palette.background.paper,
                zIndex: 1,
                whiteSpace: 'pre-line',
              }}
              autoFocus={autoFocus}
            />
          }
          ErrorBoundary={LexicalErrorBoundary}
        />
        <HistoryPlugin />
        <OnChangePlugin onChange={editorState => onChange(editorStateToString(editorState))} />
        <Stack direction="row" gap={1} mt={2} mb={1} ml={1} justifyContent="flex-start">
          <Button variant="contained" size="small" onClick={onSave}>
            Save
          </Button>
          <Button variant="outlined" size="small" onClick={onCancel}>
            Cancel
          </Button>
        </Stack>
      </Box>
    </LexicalComposer>
  );
};

export default TextBlockEditor; 