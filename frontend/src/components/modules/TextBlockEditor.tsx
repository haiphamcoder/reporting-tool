import React from 'react';
import { LexicalComposer } from '@lexical/react/LexicalComposer';
import { RichTextPlugin } from '@lexical/react/LexicalRichTextPlugin';
import { ContentEditable } from '@lexical/react/LexicalContentEditable';
import { HistoryPlugin } from '@lexical/react/LexicalHistoryPlugin';
import { OnChangePlugin } from '@lexical/react/LexicalOnChangePlugin';
import { $getRoot, $getSelection, EditorState, FORMAT_TEXT_COMMAND, FORMAT_ELEMENT_COMMAND, $createParagraphNode, $createTextNode } from 'lexical';
import { Box, Button, Stack, IconButton, Tooltip, Select, MenuItem } from '@mui/material';
import { LexicalErrorBoundary } from '@lexical/react/LexicalErrorBoundary';
import { useLexicalComposerContext } from '@lexical/react/LexicalComposerContext';
import { useTheme } from '@mui/material/styles';
import FormatBoldIcon from '@mui/icons-material/FormatBold';
import FormatItalicIcon from '@mui/icons-material/FormatItalic';
import FormatAlignLeftIcon from '@mui/icons-material/FormatAlignLeft';
import FormatAlignCenterIcon from '@mui/icons-material/FormatAlignCenter';
import FormatAlignRightIcon from '@mui/icons-material/FormatAlignRight';
import FormatSizeIcon from '@mui/icons-material/FormatSize';

interface TextBlockEditorProps {
  value: string;
  format?: any;
  onChange: (value: string, format?: any) => void;
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

// Extract format from editor state with correct keys
function extractFormatFromEditor(editorState: EditorState) {
  let format: any = {};

  editorState.read(() => {
    const root = $getRoot();
    const firstChild = root.getFirstChild();

    console.log('Extracting format from editor state...');
    console.log('First child:', firstChild);

    if (firstChild && (firstChild as any).getStyle) {
      // Get paragraph alignment - try multiple approaches
      const style = (firstChild as any).getStyle();
      console.log('Paragraph style:', style);

      // Check for text-align in style - be more specific
      console.log('Checking paragraph style for alignment:', style);
      if (style.includes('text-align: center')) {
        format.text_align = 'center';
        console.log('Detected center alignment from style');
      } else if (style.includes('text-align: right')) {
        format.text_align = 'right';
        console.log('Detected right alignment from style');
      } else if (style.includes('text-align: justify')) {
        format.text_align = 'justify';
        console.log('Detected justify alignment from style');
      } else if (style.includes('text-align: left')) {
        format.text_align = 'left';
        console.log('Detected left alignment from style');
      } else {
        format.text_align = 'left';
        console.log('Defaulting to left alignment');
      }

      // Also check for alignment in the element itself
      if ((firstChild as any).getFormat) {
        const elementFormat = (firstChild as any).getFormat();
        console.log('Element format:', elementFormat);

        // Lexical uses different format flags for alignment
        // But let's prioritize the style over format flags
        if (style.includes('text-align: center')) {
          format.text_align = 'center';
        } else if (style.includes('text-align: right')) {
          format.text_align = 'right';
        } else if (style.includes('text-align: justify')) {
          format.text_align = 'justify';
        } else if (style.includes('text-align: left')) {
          format.text_align = 'left';
        } else {
          // Fallback to format flags only if no style found
          if (elementFormat & 1) format.text_align = 'left';
          else if (elementFormat & 2) format.text_align = 'center';
          else if (elementFormat & 4) format.text_align = 'right';
          else if (elementFormat & 8) format.text_align = 'justify';
        }
      }

      // Get text formatting from first text node
      const textNode = (firstChild as any).getFirstChild();
      console.log('Text node:', textNode);

      if (textNode && (textNode as any).getFormat && (textNode as any).getStyle) {
        const textStyle = (textNode as any).getStyle();
        const formatFlags = (textNode as any).getFormat();

        console.log('Text style:', textStyle);
        console.log('Format flags:', formatFlags);

        // Check bold (format flag 1)
        if (formatFlags & 1) {
          format.font_weight = 'bold';
          console.log('Detected bold');
        } else {
          format.font_weight = 'normal';
        }

        // Check italic (format flag 2)
        if (formatFlags & 2) {
          format.font_style = 'italic';
          console.log('Detected italic');
        } else {
          format.font_style = 'normal';
        }

        // Check underline (format flag 4)
        if (formatFlags & 4) {
          format.underline = true;
          console.log('Detected underline');
        }

        // Check strikethrough (format flag 8)
        if (formatFlags & 8) {
          format.strikethrough = true;
          console.log('Detected strikethrough');
        }

        // Extract font size
        console.log('Extracting font size from text style:', textStyle);
        const fontSizeMatch = textStyle.match(/font-size:\s*(\d+)px/);
        if (fontSizeMatch) {
          format.font_size = parseInt(fontSizeMatch[1]);
          console.log('Detected font size:', format.font_size);
        } else {
          format.font_size = 16; // default
          console.log('No font size found, using default:', format.font_size);
        }

        // Extract color
        const colorMatch = textStyle.match(/color:\s*([^;]+)/);
        if (colorMatch) {
          format.color = colorMatch[1].trim();
          console.log('Detected color:', format.color);
        }

        // Extract background color
        const bgColorMatch = textStyle.match(/background-color:\s*([^;]+)/);
        if (bgColorMatch) {
          format.background_color = bgColorMatch[1].trim();
          console.log('Detected background color:', format.background_color);
        }
      }
    }

    console.log('Final extracted format:', format);
  });

  return format;
}

// Plugin to initialize editor with initial value only once
const InitialValuePlugin: React.FC<{ initialValue: string; initialFormat?: any }> = ({ initialValue, initialFormat }) => {
  const [editor] = useLexicalComposerContext();
  const [isInitialized, setIsInitialized] = React.useState(false);

  React.useEffect(() => {
    if (!isInitialized && initialValue !== undefined) {
      editor.update(() => {
        const root = $getRoot();
        root.clear();

        if (initialValue) {
          const paragraph = $createParagraphNode();

          // Apply paragraph formatting FIRST, before adding text
          if (initialFormat?.text_align) {
            const alignStyle = `text-align: ${initialFormat.text_align} !important`;
            console.log('Setting paragraph alignment:', alignStyle);
            paragraph.setStyle(alignStyle);

            // Also set format flags for alignment if needed
            let elementFormat = 0;
            if (initialFormat.text_align === 'left') elementFormat |= 1;
            else if (initialFormat.text_align === 'center') elementFormat |= 2;
            else if (initialFormat.text_align === 'right') elementFormat |= 4;
            else if (initialFormat.text_align === 'justify') elementFormat |= 8;

            if (elementFormat > 0) {
              console.log('Setting element format flags:', elementFormat);
              (paragraph as any).setFormat(elementFormat);
            }
          }

          const textNode = $createTextNode(initialValue);

          // Apply text formatting
          if (initialFormat) {
            let format = 0;
            if (initialFormat.font_weight === 'bold') format |= 1;
            if (initialFormat.font_style === 'italic') format |= 2;
            if (initialFormat.underline) format |= 4;
            if (initialFormat.strikethrough) format |= 8;

            console.log('Setting text format flags:', format);
            textNode.setFormat(format);

            // Apply styles
            let style = '';
            if (initialFormat.font_size) {
              style += `font-size: ${initialFormat.font_size}px; `;
              console.log('Setting font size:', initialFormat.font_size);
            }
            if (initialFormat.color) style += `color: ${initialFormat.color}; `;
            if (initialFormat.background_color) style += `background-color: ${initialFormat.background_color}; `;

            if (style) {
              console.log('Setting text style:', style);
              textNode.setStyle(style);
            }
          }

          // Append text to paragraph, then paragraph to root
          paragraph.append(textNode);
          root.append(paragraph);

          // Force a re-render after a short delay to ensure alignment is applied
          if (initialFormat?.text_align) {
            setTimeout(() => {
              editor.update(() => {
                const root = $getRoot();
                const firstChild = root.getFirstChild();
                if (firstChild && (firstChild as any).setStyle) {
                  const forceAlignStyle = `text-align: ${initialFormat.text_align} !important`;
                  console.log('Force setting alignment style:', forceAlignStyle);
                  (firstChild as any).setStyle(forceAlignStyle);

                  // Also set format flags
                  let elementFormat = 0;
                  if (initialFormat.text_align === 'left') elementFormat |= 1;
                  else if (initialFormat.text_align === 'center') elementFormat |= 2;
                  else if (initialFormat.text_align === 'right') elementFormat |= 4;
                  else if (initialFormat.text_align === 'justify') elementFormat |= 8;

                  if (elementFormat > 0) {
                    console.log('Force setting element format flags:', elementFormat);
                    (firstChild as any).setFormat(elementFormat);
                  }
                }
              });
            }, 100);

            // Add another delay to ensure it's applied
            setTimeout(() => {
              editor.update(() => {
                const root = $getRoot();
                const firstChild = root.getFirstChild();
                if (firstChild && (firstChild as any).setStyle) {
                  const finalAlignStyle = `text-align: ${initialFormat.text_align} !important`;
                  console.log('Final force setting alignment style:', finalAlignStyle);
                  (firstChild as any).setStyle(finalAlignStyle);
                }
              });
            }, 300);
          }
        } else {
          const paragraph = $createParagraphNode();
          root.append(paragraph);
        }
      });
      setIsInitialized(true);
    }
  }, [editor, initialValue, initialFormat, isInitialized]);

  return null;
};

const FONT_SIZES = [12, 14, 16, 18, 20, 24, 28, 32];

const Toolbar: React.FC<{ initialFormat?: any }> = ({ initialFormat }) => {
  const [editor] = useLexicalComposerContext();
  const theme = useTheme();
  const [currentAlign, setCurrentAlign] = React.useState(initialFormat?.text_align || 'left');
  const [currentFontSize, setCurrentFontSize] = React.useState(initialFormat?.font_size || 16);
  const [isBold, setIsBold] = React.useState(false);
  const [isItalic, setIsItalic] = React.useState(false);
  const [, setIsUnderline] = React.useState(false);
  const [, setIsStrikethrough] = React.useState(false);

  // Update toolbar state when initialFormat changes
  React.useEffect(() => {
    if (initialFormat) {
      if (initialFormat.text_align) {
        setCurrentAlign(initialFormat.text_align);
        console.log('Toolbar: Updated align to:', initialFormat.text_align);
      }
      if (initialFormat.font_size) {
        setCurrentFontSize(initialFormat.font_size);
        console.log('Toolbar: Updated font size to:', initialFormat.font_size);
      }
    }
  }, [initialFormat]);

  // Detect current format from editor
  React.useEffect(() => {
    const detectCurrentFormat = () => {
      editor.getEditorState().read(() => {
        const root = $getRoot();
        const firstChild = root.getFirstChild();
        if (firstChild && (firstChild as any).getStyle) {
          const style = (firstChild as any).getStyle();
          // Detect alignment
          if (style.includes('text-align: center')) {
            setCurrentAlign('center');
          } else if (style.includes('text-align: right')) {
            setCurrentAlign('right');
          } else if (style.includes('text-align: left')) {
            setCurrentAlign('left');
          }
          // Detect font size from text node
          const textNode = (firstChild as any).getFirstChild();
          if (textNode && (textNode as any).getStyle && (textNode as any).getFormat) {
            const textStyle = (textNode as any).getStyle();
            const fontSizeMatch = textStyle.match(/font-size:\s*(\d+)px/);
            if (fontSizeMatch) {
              const detectedSize = parseInt(fontSizeMatch[1]);
              setCurrentFontSize(detectedSize);
              console.log('Toolbar: Detected font size:', detectedSize);
            }
            // Detect format flags
            const formatFlags = (textNode as any).getFormat();
            setIsBold(!!(formatFlags & 1));
            setIsItalic(!!(formatFlags & 2));
            setIsUnderline(!!(formatFlags & 4));
            setIsStrikethrough(!!(formatFlags & 8));
          }
        }
      });
    };
    // Detect format after a short delay to ensure editor is ready
    const timeoutId = setTimeout(detectCurrentFormat, 200);
    // Detect format after a longer delay to catch late changes (e.g. after InitialValuePlugin)
    const timeoutId2 = setTimeout(detectCurrentFormat, 800);
    // Use MutationObserver to detect DOM changes
    const editorElement = document.querySelector('[contenteditable="true"]');
    if (editorElement) {
      const observer = new MutationObserver(() => {
        detectCurrentFormat();
      });
      observer.observe(editorElement, {
        attributes: true,
        childList: true,
        subtree: true,
        attributeFilter: ['style']
      });
      return () => {
        clearTimeout(timeoutId);
        clearTimeout(timeoutId2);
        observer.disconnect();
      };
    }
    return () => {
      clearTimeout(timeoutId);
      clearTimeout(timeoutId2);
    };
  }, [editor]);

  const applyFormat = (format: 'bold' | 'italic') => {
    console.log('Applying format:', format);
    editor.dispatchCommand(FORMAT_TEXT_COMMAND, format);
  };
  const applyAlign = (align: 'left' | 'center' | 'right') => {
    console.log('Applying align:', align);
    setCurrentAlign(align);
    editor.update(() => {
      const root = $getRoot();
      const firstChild = root.getFirstChild();
      if (firstChild && (firstChild as any).setStyle) {
        const alignStyle = `text-align: ${align} !important`;
        (firstChild as any).setStyle(alignStyle);
        if ((firstChild as any).setFormat) {
          let elementFormat = 0;
          if (align === 'left') elementFormat |= 1;
          else if (align === 'center') elementFormat |= 2;
          else if (align === 'right') elementFormat |= 4;
          (firstChild as any).setFormat(elementFormat);
        }
      }
    });
    editor.dispatchCommand(FORMAT_ELEMENT_COMMAND, align);
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
    setCurrentFontSize(size);
  };
  return (
    <Stack direction="row" gap={1} alignItems="center" mb={1}>
      <Tooltip title="Bold">
        <IconButton size="small" onClick={() => applyFormat('bold')}
          color={isBold ? 'primary' : 'default'}
          sx={isBold ? { backgroundColor: theme.palette.action.selected, '&:hover': { backgroundColor: theme.palette.primary.main, color: '#fff' } } : {}}>
          <FormatBoldIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Italic">
        <IconButton size="small" onClick={() => applyFormat('italic')}
          color={isItalic ? 'primary' : 'default'}
          sx={isItalic ? { backgroundColor: theme.palette.action.selected, '&:hover': { backgroundColor: theme.palette.primary.main, color: '#fff' } } : {}}>
          <FormatItalicIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Align Left">
        <IconButton size="small" onClick={() => applyAlign('left')}
          color={currentAlign === 'left' ? 'primary' : 'default'}
          sx={currentAlign === 'left' ? { backgroundColor: theme.palette.action.selected, '&:hover': { backgroundColor: theme.palette.primary.main, color: '#fff' } } : {}}>
          <FormatAlignLeftIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Align Center">
        <IconButton size="small" onClick={() => applyAlign('center')}
          color={currentAlign === 'center' ? 'primary' : 'default'}
          sx={currentAlign === 'center' ? { backgroundColor: theme.palette.action.selected, '&:hover': { backgroundColor: theme.palette.primary.main, color: '#fff' } } : {}}>
          <FormatAlignCenterIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Align Right">
        <IconButton size="small" onClick={() => applyAlign('right')}
          color={currentAlign === 'right' ? 'primary' : 'default'}
          sx={currentAlign === 'right' ? { backgroundColor: theme.palette.action.selected, '&:hover': { backgroundColor: theme.palette.primary.main, color: '#fff' } } : {}}>
          <FormatAlignRightIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Font Size">
        <Select size="small" value={currentFontSize} onChange={e => applyFontSize(Number(e.target.value))} startAdornment={<FormatSizeIcon />}>
          {FONT_SIZES.map(size => <MenuItem key={size} value={size}>{size}</MenuItem>)}
        </Select>
      </Tooltip>
    </Stack>
  );
};

const TextBlockEditor: React.FC<TextBlockEditorProps> = ({ value, format, onChange, onSave, onCancel, autoFocus }) => {
  const theme = useTheme();
  const initialConfig = {
    namespace: 'TextBlockEditor',
    theme: {
      paragraph: 'editor-paragraph',
    },
    onError: (error: Error) => {
      console.error('Lexical editor error:', error);
    },
  };

  const handleChange = React.useCallback((editorState: EditorState) => {
    const text = editorStateToString(editorState);
    const extractedFormat = extractFormatFromEditor(editorState);
    console.log('Extracted format:', extractedFormat);
    onChange(text, extractedFormat);
  }, [onChange]);

  // Force apply initial alignment after editor mounts
  React.useEffect(() => {
    if (format?.text_align) {
      const timeoutId = setTimeout(() => {
        const editorElement = document.querySelector('[contenteditable="true"]');
        if (editorElement) {
          const paragraph = editorElement.querySelector('p');
          if (paragraph) {
            paragraph.style.textAlign = format.text_align;
            console.log('Force applied initial alignment via DOM:', format.text_align);
          }
        }
      }, 50);
      return () => clearTimeout(timeoutId);
    }
  }, [format?.text_align]);

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
          '& .lexical-content-editable': {
            '& [data-lexical-text="true"]': {
              textAlign: 'inherit !important',
            },
            '& p': {
              textAlign: 'inherit !important',
            },
            '& [style*="text-align: center"]': {
              textAlign: 'center !important',
            },
            '& [style*="text-align: right"]': {
              textAlign: 'right !important',
            },
            '& [style*="text-align: left"]': {
              textAlign: 'left !important',
            },
            // Force initial alignment based on format prop
            ...(format?.text_align && {
              '& p:first-child': {
                textAlign: `${format.text_align} !important`,
              },
            }),
          },
        }}
      >
        <Box sx={{ position: 'absolute', top: 8, right: 8, zIndex: 2 }}>
          <Toolbar initialFormat={format} />
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
                whiteSpace: 'pre-wrap',
                wordWrap: 'break-word',
                zIndex: 1,
                textAlign: 'inherit',
              }}
              className="lexical-content-editable"
              autoFocus={autoFocus}
            />
          }
          ErrorBoundary={LexicalErrorBoundary}
        />
        <HistoryPlugin />
        <OnChangePlugin onChange={handleChange} />
        <InitialValuePlugin initialValue={value} initialFormat={format} />
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