import { useState, useEffect } from 'react';
import FormControl from '@mui/material/FormControl';
import InputAdornment from '@mui/material/InputAdornment';
import OutlinedInput from '@mui/material/OutlinedInput';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';

interface SearchProps {
  value?: string;
  onSearchChange?: (searchTerm: string) => void;
  placeholder?: string;
  debounceMs?: number;
}

export default function Search({ 
  value = '', 
  onSearchChange, 
  placeholder = "Search…",
  debounceMs = 300 
}: SearchProps) {
  const [searchTerm, setSearchTerm] = useState(value);

  // Debounced search effect
  useEffect(() => {
    const timer = setTimeout(() => {
      if (onSearchChange) {
        onSearchChange(searchTerm);
      }
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [searchTerm, onSearchChange, debounceMs]);

  // Update local state when prop value changes
  useEffect(() => {
    setSearchTerm(value);
  }, [value]);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  return (
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
  );
}
