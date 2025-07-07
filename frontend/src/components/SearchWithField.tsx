import { useState, useEffect } from 'react';
import FormControl from '@mui/material/FormControl';
import InputAdornment from '@mui/material/InputAdornment';
import OutlinedInput from '@mui/material/OutlinedInput';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import InputLabel from '@mui/material/InputLabel';

interface SearchField {
  field: string;
  label: string;
}

interface SearchWithFieldProps {
  value?: string;
  onSearchChange?: (searchTerm: string, selectedField: string) => void;
  placeholder?: string;
  debounceMs?: number;
  fields?: SearchField[];
  selectedField?: string;
  onFieldChange?: (field: string) => void;
}

export default function SearchWithField({ 
  value = '', 
  onSearchChange, 
  placeholder = "Search…",
  debounceMs = 300,
  fields = [],
  selectedField = '',
  onFieldChange
}: SearchWithFieldProps) {
  const [searchTerm, setSearchTerm] = useState(value);
  const [currentField, setCurrentField] = useState(selectedField);

  // Debounced search effect
  useEffect(() => {
    const timer = setTimeout(() => {
      if (onSearchChange) {
        onSearchChange(searchTerm, currentField);
      }
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [searchTerm, currentField, onSearchChange, debounceMs]);

  // Update local state when prop value changes
  useEffect(() => {
    setSearchTerm(value);
  }, [value]);

  // Update local field when prop changes
  useEffect(() => {
    setCurrentField(selectedField);
  }, [selectedField]);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleFieldChange = (event: any) => {
    const newField = event.target.value;
    setCurrentField(newField);
    if (onFieldChange) {
      onFieldChange(newField);
    }
  };

  return (
    <Stack direction="row" spacing={1} alignItems="center">
      <FormControl sx={{ width: { xs: '100%', md: '25ch' } }} variant="outlined">
        <OutlinedInput
          size="small"
          id="search"
          placeholder={placeholder}
          value={searchTerm}
          onChange={handleChange}
          sx={{ flexGrow: 1 }}
          startAdornment={
            <InputAdornment position="start" sx={{ color: 'text.primary' }}>
              <SearchRoundedIcon fontSize="small" />
            </InputAdornment>
          }
          inputProps={{
            'aria-label': 'search',
          }}
        />
      </FormControl>
      {fields.length > 0 && (
        <FormControl sx={{ minWidth: 120 }} size="small">
          <InputLabel id="field-select-label">Field</InputLabel>
          <Select
            labelId="field-select-label"
            value={currentField}
            label="Field"
            onChange={handleFieldChange}
            size="small"
          >
            {fields.map((field) => (
              <MenuItem key={field.field} value={field.field}>
                {field.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      )}
    </Stack>
  );
} 