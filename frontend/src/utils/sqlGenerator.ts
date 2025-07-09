import { QueryOption, FilterNode, FilterCondition, FilterGroup } from '../types/chart';

// Helper function để lấy table prefix - đồng nhất với Step2QueryBuilder
const getTablePrefix = (sources: any[], source_id?: string, alias?: string, isMainTable: boolean = false, mainTableAlias?: string) => {
    if (alias) return alias;
    if (!source_id) return '';
    const src = sources.find(s => s.id === source_id);
    if (!src) return '';

    // Nếu là main table và có alias, sử dụng alias
    if (isMainTable && mainTableAlias) {
        return mainTableAlias;
    }

    // Ưu tiên table_name trước
    return src.table_name || '';
};

// Convert filter node to SQL - sử dụng logic đồng nhất
function filterConditionNodeToSql(node: FilterCondition, sources: any[], mainTableAlias?: string, mainTableId?: string): string {
    if (node.source_field === undefined) return '';
    const field = node.source_field;

    // Logic đồng nhất với Step2QueryBuilder: sử dụng table_name trước
    let sourcePrefix = field.table_name || '';
    // Nếu là main table và có alias, sử dụng alias
    if (mainTableAlias && field.source_id) {
        const mainSource = sources.find(s => s.id === field.source_id);
        if (mainSource && mainSource.id === mainTableId) {
            sourcePrefix = mainTableAlias;
        }
    }

    const fieldSql = `${sourcePrefix}.${field.field_mapping}`;
    const operator = (() => {
        switch (node.operator) {
            case 'EQ': return '=';
            case 'NE': return '!=';
            case 'GT': return '>';
            case 'GTE': return '>=';
            case 'LT': return '<';
            case 'LTE': return '<=';
            case 'LIKE': return 'LIKE';
            case 'IN': return 'IN';
            case 'NOT_IN': return 'NOT IN';
            case 'BETWEEN': return 'BETWEEN';
            case 'IS_NULL': return 'IS NULL';
            case 'IS_NOT_NULL': return 'IS NOT NULL';
            case 'REGEXP': return 'REGEXP';
            default: return '';
        }
    })();
    if (operator === '') return '';
    if (node.compare_with_other_field) {
        if (node.target_field === undefined) return '';
        const targetField = node.target_field;

        // Logic tương tự cho target field
        let targetPrefix = targetField.table_name || '';
        if (mainTableAlias && targetField.source_id) {
            const mainSource = sources.find(s => s.id === targetField.source_id);
            if (mainSource && mainSource.id === mainTableId) {
                targetPrefix = mainTableAlias;
            }
        }

        const targetFieldSql = `${targetPrefix}.${targetField.field_mapping}`;
        return `${fieldSql} ${operator} ${targetFieldSql}`;
    }
    const valueSql = node.value;
    return `${fieldSql} ${operator} '${valueSql}'`;
}

function filterGroupNodeToSql(node: FilterGroup, sources: any[], mainTableAlias?: string, mainTableId?: string): string {
    if (node.elements.length === 0) return '';
    const conditions = node.elements.map(el => filterNodeToSql(el, sources, mainTableAlias, mainTableId));
    return `(${conditions.join(` ${node.op} `)})`;
}

function filterNodeToSql(node: FilterNode, sources: any[], mainTableAlias?: string, mainTableId?: string): string {
    if (!node) return '';
    if (node.type === 'condition') {
        return filterConditionNodeToSql(node, sources, mainTableAlias, mainTableId);
    }
    else if (node.type === 'group') {
        return filterGroupNodeToSql(node, sources, mainTableAlias, mainTableId);
    }
    return '';
}

// Generate SQL từ queryOption
export const generateSqlFromQueryOption = (queryOption: QueryOption, sources: any[] = []): string => {
    const parts: string[] = [];

    // Main table prefix - đồng nhất với Step2QueryBuilder
    const mainTable = sources.find(s => s.id === queryOption.table);
    if (!mainTable) throw new Error('Main table not found');
    const mainTablePrefix = queryOption.table_alias || mainTable.table_name || '';

    // SELECT clause
    let selectFields: string[] = [];
    if (queryOption.fields && queryOption.fields.length > 0) {
        const validFieldsArr = queryOption.fields
            .filter(field => (field.field_mapping && field.field_mapping.trim()) || (field.field_name && field.field_name.trim()))
            .map(field => {
                let prefix = '';
                if (field.table_name) {
                    prefix = field.table_name;
                } else {
                    prefix = getTablePrefix(sources, field.source_id, undefined, field.source_id === queryOption.table, queryOption.table_alias);
                    if (!prefix) prefix = mainTablePrefix;
                }
                let fieldExpr = '';
                // Nếu có function
                if (field.function) {
                    fieldExpr = `${field.function}(${prefix}.${field.field_mapping || field.field_name})`;
                } else if (field.is_expression && field.expression) {
                    fieldExpr = field.expression;
                } else {
                    fieldExpr = `${prefix}.${field.field_mapping || field.field_name}`;
                }
                // Thêm alias nếu có
                if (field.alias && field.alias.trim()) {
                    const safeAlias = /[^a-zA-Z0-9_]/.test(field.alias) ? `"${field.alias}"` : field.alias;
                    return `${fieldExpr} AS ${safeAlias}`;
                }
                return fieldExpr;
            });
        selectFields = validFieldsArr;
    }
    parts.push(`SELECT ${queryOption.distinct ? 'DISTINCT ' : ''}${selectFields.length > 0 ? selectFields.join(', ') : '*'}`);

    // FROM clause - sử dụng table_name trước
    let fromClause = `FROM ${mainTable.table_name || ''}`;
    if (queryOption.table_alias) {
        fromClause += ` AS ${queryOption.table_alias}`;
    }
    parts.push(fromClause);

    // JOIN clauses
    if (queryOption.joins && queryOption.joins.length > 0) {
        // Map để theo dõi các bảng đã JOIN và alias tương ứng
        const joinedTables = new Map<string, string>();
        let aliasCounter = 1;

        queryOption.joins.forEach(join => {
            const joinTable = sources.find(s => s.id === join.table);
            if (!joinTable) return; // Bỏ qua nếu thiếu bảng
            let joinClause = '';
            // Xác định loại JOIN
            switch (join.type) {
                case 'INNER': joinClause = 'INNER JOIN'; break;
                case 'LEFT': joinClause = 'LEFT JOIN'; break;
                case 'RIGHT': joinClause = 'RIGHT JOIN'; break;
                case 'CROSS': joinClause = 'CROSS JOIN'; break;
                case 'NATURAL_LEFT': joinClause = 'NATURAL LEFT JOIN'; break;
                case 'NATURAL_RIGHT': joinClause = 'NATURAL RIGHT JOIN'; break;
                default: joinClause = 'INNER JOIN';
            }
            // Xác định alias cho bảng JOIN - sử dụng table_name trước
            let tableAlias = '';
            const tableName = joinTable.table_name || '';
            if (join.table_alias) {
                tableAlias = join.table_alias;
            } else {
                if (joinedTables.has(tableName)) {
                    tableAlias = `t${aliasCounter++}`;
                } else {
                    tableAlias = tableName;
                }
            }
            joinedTables.set(tableName, tableAlias);
            joinClause += ` ${tableName}`;
            if (tableAlias !== tableName) {
                joinClause += ` AS ${tableAlias}`;
            }
            // Thêm điều kiện JOIN (chỉ lấy điều kiện đủ field)
            if (join.conditions && join.conditions.length > 0) {
                const conditions = join.conditions
                    .filter(condition => condition.left_field && condition.right_field && condition.operator)
                                            .map(condition => {
                            // Ưu tiên sử dụng table_name từ condition data
                            let leftPrefix = '';
                            if (condition.left_table_name) {
                                leftPrefix = condition.left_table_name;
                            } else {
                                const leftSrc = sources.find(s => s.id === condition.left_table) || mainTable;
                                if (leftSrc.id === joinTable.id) {
                                    leftPrefix = tableAlias;
                                } else if (leftSrc.id === mainTable.id) {
                                    leftPrefix = mainTablePrefix;
                                } else {
                                    const leftTableName = leftSrc.table_name || '';
                                    leftPrefix = joinedTables.get(leftTableName) || leftTableName;
                                }
                            }
                            
                            let rightPrefix = '';
                            if (condition.right_table_name) {
                                rightPrefix = condition.right_table_name;
                            } else {
                                const rightSrc = sources.find(s => s.id === condition.right_table) || joinTable;
                                if (rightSrc.id === joinTable.id) {
                                    rightPrefix = tableAlias;
                                } else if (rightSrc.id === mainTable.id) {
                                    rightPrefix = mainTablePrefix;
                                } else {
                                    const rightTableName = rightSrc.table_name || '';
                                    rightPrefix = joinedTables.get(rightTableName) || rightTableName;
                                }
                            }
                            
                            const operator = condition.operator === 'EQ' ? '=' :
                                condition.operator === 'NE' ? '!=' :
                                    condition.operator === 'GT' ? '>' :
                                        condition.operator === 'GTE' ? '>=' :
                                            condition.operator === 'LT' ? '<' :
                                                condition.operator === 'LTE' ? '<=' : '=';
                            return `${leftPrefix}.${condition.left_field} ${operator} ${rightPrefix}.${condition.right_field}`;
                        });
                if (conditions.length > 0) {
                    joinClause += ` ON ${conditions.join(' AND ')}`;
                } else {
                    return; // Bỏ qua JOIN nếu không có điều kiện hợp lệ
                }
            } else {
                return; // Bỏ qua JOIN nếu không có điều kiện
            }
            parts.push(joinClause);
        });
    }

    // WHERE clause - truyền mainTableAlias và mainTableId
    const whereString = filterNodeToSql(queryOption.filters as FilterNode, sources, queryOption.table_alias, queryOption.table);
    if (whereString && whereString !== '') {
        parts.push(`WHERE ${whereString}`);
    }

    // GROUP BY clause
    if ((queryOption.group_by && queryOption.group_by.length > 0) || (queryOption.fields && queryOption.fields.length > 0)) {
        const selectNonAggFields = (queryOption.fields || [])
            .filter(f => !f.function && f.field_mapping && f.source_id)
            .map(f => {
                let prefix = '';
                if (f.table_name) {
                    prefix = f.table_name;
                } else {
                    const src = sources.find(s => s.id === f.source_id);
                    prefix = queryOption.table_alias && src && src.id === queryOption.table ? queryOption.table_alias : (src?.table_name || '');
                }
                return `${prefix}.${f.field_mapping}`;
            });
        const userGroupBy = (queryOption.group_by || []).filter(key => !selectNonAggFields.includes(key));
        const groupByFields = [...selectNonAggFields, ...userGroupBy].map(key => {
            const [prefix, ...fieldParts] = key.split('.');
            const fieldMapping = fieldParts.join('.');
            let realPrefix = prefix;
            const aliasExists = queryOption.joins && queryOption.joins.some(j => j.table_alias === prefix);
            if (!aliasExists) {
                const src = sources.find(s => s.table_name === prefix);
                if (src && src.table_name && src.table_name !== prefix) {
                    realPrefix = src.table_name;
                }
            }
            return `${realPrefix}.${fieldMapping}`;
        });
        if (groupByFields.length > 0) {
            parts.push(`GROUP BY ${groupByFields.join(', ')}`);
        }
    }

    // HAVING clause - sử dụng logic đồng nhất
    if (queryOption.having && queryOption.having.length > 0) {
        const havingConditions = queryOption.having
            .filter(having => having.field && having.operator && having.function && (having.value !== undefined && having.value !== ''))
            .map(having => {
                let prefix = mainTablePrefix;
                // Nếu có table_name từ field data, sử dụng trực tiếp
                if (having.table_name) {
                    prefix = having.table_name;
                } else if (having.source_id) {
                    const src = sources.find(s => s.id === having.source_id);
                    if (src) {
                        prefix = (queryOption.table_alias && src.id === queryOption.table) ? queryOption.table_alias : (src.table_name || '');
                    }
                }
                // Chuyển operator sang ký hiệu SQL
                const operator = having.operator === 'EQ' ? '=' :
                    having.operator === 'NE' ? '!=' :
                        having.operator === 'GT' ? '>' :
                            having.operator === 'GTE' ? '>=' :
                                having.operator === 'LT' ? '<' :
                                    having.operator === 'LTE' ? '<=' :
                                        having.operator;
                return `${having.function}(${prefix}.${having.field}) ${operator} ${typeof having.value === 'string' ? `'${having.value}'` : having.value}`;
            });
        if (havingConditions.length > 0) {
            parts.push(`HAVING ${havingConditions.join(' AND ')}`);
        }
    }

    // ORDER BY clause
    if (queryOption.sort && queryOption.sort.length > 0) {
        const orderByClause = queryOption.sort.map(sort => {
            // Tìm field trong SELECT theo field_mapping
            const selectField = (queryOption.fields || []).find(f => f.field_mapping === sort.field);
            if (selectField) {
                // Nếu có alias, sort theo alias
                if (selectField.alias && selectField.alias.trim()) {
                    // Nếu alias có ký tự đặc biệt, bọc nháy kép
                    const safeAlias = /[^a-zA-Z0-9_]/.test(selectField.alias) ? `"${selectField.alias}"` : selectField.alias;
                    return `${safeAlias} ${sort.direction}`;
                }
                // Nếu không có alias, sort theo prefix.field_mapping
                let prefix = '';
                if (selectField.table_name) {
                    prefix = selectField.table_name;
                } else if (selectField.source_id) {
                    const src = sources.find(s => s.id === selectField.source_id);
                    prefix = (queryOption.table_alias && src && src.id === queryOption.table) ? queryOption.table_alias : (src?.table_name || '');
                }
                return `${prefix ? prefix + '.' : ''}${selectField.field_mapping} ${sort.direction}`;
            }
            // Nếu không tìm thấy, fallback giữ nguyên (có thể là alias)
            return `${sort.field} ${sort.direction}`;
        }).join(', ');
        parts.push(`ORDER BY ${orderByClause}`);
    }

    // LIMIT clause
    if (queryOption.limit) {
        parts.push(`LIMIT ${queryOption.limit}`);
    }
    // OFFSET clause
    if (queryOption.offset) {
        parts.push(`OFFSET ${queryOption.offset}`);
    }
    return parts.join(' ');
}; 