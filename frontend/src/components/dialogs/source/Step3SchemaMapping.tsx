import React from 'react';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

interface Step3SchemaMappingProps {
  addForm: any;
  setAddForm: (form: any) => void;
}

const Step3SchemaMapping: React.FC<Step3SchemaMappingProps> = ({
  addForm,
  setAddForm,
}) => {
  return (
    <Stack spacing={3} sx={{ mt: 2 }}>
      <Box>
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Schema Mapping
        </Typography>
        {addForm.schema ? (
          <Box sx={{ mt: 2 }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ border: '1px solid #ccc', padding: 4 }}>Field Name</th>
                  <th style={{ border: '1px solid #ccc', padding: 4 }}>Field Mapping</th>
                  <th style={{ border: '1px solid #ccc', padding: 4 }}>Field Type</th>
                  <th style={{ border: '1px solid #ccc', padding: 4 }}>Is Hidden</th>
                </tr>
              </thead>
              <tbody>
                {addForm.schema.map((row: any, idx: number) => (
                  <tr key={row.field_name}>
                    <td style={{ border: '1px solid #ccc', padding: 4 }}>{row.field_name}</td>
                    <td style={{ border: '1px solid #ccc', padding: 4 }}>{row.field_mapping}</td>
                    <td style={{ border: '1px solid #ccc', padding: 4 }}>
                      <select
                        value={row.field_type}
                        onChange={e => {
                          const newSchema = [...addForm.schema];
                          newSchema[idx] = { ...newSchema[idx], field_type: e.target.value };
                          setAddForm((prev: any) => ({ ...prev, schema: newSchema }));
                        }}
                      >
                        <option value="TEXT">TEXT</option>
                        <option value="BIGINT">BIGINT</option>
                        <option value="DOUBLE">DOUBLE</option>
                        {/* Thêm các type khác nếu cần */}
                      </select>
                    </td>
                    <td style={{ border: '1px solid #ccc', padding: 4, textAlign: 'center' }}>
                      <input
                        type="checkbox"
                        checked={row.is_hidden}
                        onChange={e => {
                          const newSchema = [...addForm.schema];
                          newSchema[idx] = { ...newSchema[idx], is_hidden: e.target.checked };
                          setAddForm((prev: any) => ({ ...prev, schema: newSchema }));
                        }}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Box>
        ) : (
          <Typography variant="body2" color="text.secondary">Loading schema...</Typography>
        )}
      </Box>
    </Stack>
  );
};

export default Step3SchemaMapping; 