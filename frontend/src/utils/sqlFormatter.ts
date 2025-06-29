// SQL Formatter utility
export const formatSQL = (sql: string): string => {
    if (!sql || typeof sql !== 'string') {
        return sql;
    }

    // Remove extra whitespace and normalize
    let formatted = sql
        .replace(/\s+/g, ' ')
        .trim();

    // Convert to uppercase for SQL keywords
    const keywords = [
        'SELECT', 'FROM', 'WHERE', 'GROUP BY', 'ORDER BY', 'HAVING', 'JOIN', 'LEFT JOIN', 'RIGHT JOIN', 
        'INNER JOIN', 'OUTER JOIN', 'ON', 'AND', 'OR', 'NOT', 'IN', 'NOT IN', 'LIKE', 'IS NULL', 
        'IS NOT NULL', 'COUNT', 'SUM', 'AVG', 'MIN', 'MAX', 'DISTINCT', 'AS', 'ASC', 'DESC',
        'LIMIT', 'OFFSET', 'CASE', 'WHEN', 'THEN', 'ELSE', 'END', 'INSERT', 'UPDATE', 'DELETE',
        'CREATE', 'ALTER', 'DROP', 'TABLE', 'INDEX', 'PRIMARY KEY', 'FOREIGN KEY', 'UNIQUE', 
        'NOT NULL', 'DEFAULT', 'UNION', 'UNION ALL', 'EXISTS', 'NOT EXISTS', 'BETWEEN'
    ];

    // Create regex pattern for keywords
    const keywordPattern = new RegExp(`\\b(${keywords.join('|')})\\b`, 'gi');
    formatted = formatted.replace(keywordPattern, (match) => match.toUpperCase());

    // Add line breaks for better readability
    formatted = formatted
        .replace(/\bSELECT\b/gi, '\nSELECT')
        .replace(/\bFROM\b/gi, '\nFROM')
        .replace(/\bWHERE\b/gi, '\nWHERE')
        .replace(/\bGROUP BY\b/gi, '\nGROUP BY')
        .replace(/\bORDER BY\b/gi, '\nORDER BY')
        .replace(/\bHAVING\b/gi, '\nHAVING')
        .replace(/\bJOIN\b/gi, '\nJOIN')
        .replace(/\bLEFT JOIN\b/gi, '\nLEFT JOIN')
        .replace(/\bRIGHT JOIN\b/gi, '\nRIGHT JOIN')
        .replace(/\bINNER JOIN\b/gi, '\nINNER JOIN')
        .replace(/\bOUTER JOIN\b/gi, '\nOUTER JOIN')
        .replace(/\bON\b/gi, '\n  ON')
        .replace(/\bAND\b/gi, '\n  AND')
        .replace(/\bOR\b/gi, '\n  OR')
        .replace(/\bUNION\b/gi, '\nUNION')
        .replace(/\bUNION ALL\b/gi, '\nUNION ALL');

    // Clean up multiple line breaks
    formatted = formatted
        .replace(/\n\s*\n/g, '\n')
        .replace(/^\s+/, '')
        .replace(/\s+$/, '');

    return formatted;
};

// Validate SQL query
export const validateSQL = (sql: string): { isValid: boolean; errors: string[] } => {
    const errors: string[] = [];
    
    if (!sql || sql.trim().length === 0) {
        errors.push('SQL query cannot be empty');
        return { isValid: false, errors };
    }

    const upperSQL = sql.toUpperCase();
    
    // Check for basic SELECT statement
    if (!upperSQL.includes('SELECT')) {
        errors.push('Query must contain SELECT statement');
    }
    
    if (!upperSQL.includes('FROM')) {
        errors.push('Query must contain FROM clause');
    }
    
    // Check for balanced parentheses
    const openParens = (sql.match(/\(/g) || []).length;
    const closeParens = (sql.match(/\)/g) || []).length;
    if (openParens !== closeParens) {
        errors.push('Unbalanced parentheses');
    }
    
    // Check for balanced quotes
    const singleQuotes = (sql.match(/'/g) || []).length;
    const doubleQuotes = (sql.match(/"/g) || []).length;
    if (singleQuotes % 2 !== 0) {
        errors.push('Unbalanced single quotes');
    }
    if (doubleQuotes % 2 !== 0) {
        errors.push('Unbalanced double quotes');
    }
    
    // Check for common SQL injection patterns (basic)
    const dangerousPatterns = [
        /;\s*DROP\s+TABLE/i,
        /;\s*DELETE\s+FROM/i,
        /;\s*UPDATE\s+.+\s+SET/i,
        /;\s*INSERT\s+INTO/i,
        /;\s*CREATE\s+TABLE/i,
        /;\s*ALTER\s+TABLE/i
    ];
    
    dangerousPatterns.forEach(pattern => {
        if (pattern.test(sql)) {
            errors.push('Query contains potentially dangerous operations');
        }
    });
    
    return {
        isValid: errors.length === 0,
        errors
    };
};

// Extract table names from SQL query
export const extractTableNames = (sql: string): string[] => {
    const tableNames: string[] = [];
    
    // Simple regex to extract table names after FROM and JOIN
    const fromMatches = sql.match(/\bFROM\s+([^\s,()]+)/gi);
    const joinMatches = sql.match(/\bJOIN\s+([^\s,()]+)/gi);
    
    if (fromMatches) {
        fromMatches.forEach(match => {
            const tableName = match.replace(/\bFROM\s+/i, '').trim();
            if (tableName && !tableNames.includes(tableName)) {
                tableNames.push(tableName);
            }
        });
    }
    
    if (joinMatches) {
        joinMatches.forEach(match => {
            const tableName = match.replace(/\bJOIN\s+/i, '').trim();
            if (tableName && !tableNames.includes(tableName)) {
                tableNames.push(tableName);
            }
        });
    }
    
    return tableNames;
};

// Extract field names from SQL query
export const extractFieldNames = (sql: string): string[] => {
    const fieldNames: string[] = [];
    
    // Extract field names from SELECT clause
    const selectMatch = sql.match(/\bSELECT\s+(.+?)\s+FROM/i);
    if (selectMatch) {
        const selectClause = selectMatch[1];
        // Split by comma and extract field names
        const fields = selectClause.split(',').map(field => {
            return field.trim().split(/\s+AS\s+/i)[0].split('.')[1] || field.trim().split(/\s+AS\s+/i)[0];
        });
        fieldNames.push(...fields);
    }
    
    return fieldNames.filter(field => field && field !== '*');
}; 