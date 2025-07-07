export interface ChartSummary {
    id: string;
    name: string;
    description: string;
    type: string;
    updated_at: string;
    created_at: string;
    can_edit?: boolean;
    can_share?: boolean;
    owner?: {
        id: string;
        name: string;
        avatar?: string;
    };
}

export type ChartType = 'table' | 'bar' | 'line' | 'area' | 'pie';
export type ChartMode = 'basic' | 'advanced';

// Enhanced Field Config with function and expression support
export interface FieldConfig {
    field_name: string;
    data_type: string;
    alias: string;
    source_id?: string;
    source_name?: string;
    field_mapping?: string;
    table_name?: string;
    table_alias?: string;
    // New fields for advanced functionality
    function?: 'SUM' | 'COUNT' | 'AVG' | 'MIN' | 'MAX' | 'DISTINCT' | 'CONCAT' | 'UPPER' | 'LOWER' | 'DATE_FORMAT' | 'YEAR' | 'MONTH' | 'DAY';
    expression?: string; // For custom expressions like CASE WHEN...
    is_aggregate?: boolean;
    is_expression?: boolean;
}

// Generalized Filter Tree structure
export type FilterNode = FilterCondition | FilterGroup;

export type Field = {
    source_id: string;
    source_name: string;
    table_name: string;
    table_alias: string;
    field_mapping: string;
    field_type: string;
    field_name: string;
    alias: string;
}

export interface FilterCondition {
    type: 'condition';
    id: string;
    operator: '' | 'EQ' | 'NE' | 'GT' | 'GTE' | 'LT' | 'LTE' | 'LIKE' | 'IN' | 'NOT_IN' | 'BETWEEN' | 'IS_NULL' | 'IS_NOT_NULL' | 'REGEXP';
    value: string | number | boolean | Array<string | number>;
    source_field: Field;
    compare_with_other_field: boolean;
    target_field: Field;
}

export interface FilterGroup {
    type: 'group';
    id: string;
    op: 'AND' | 'OR';
    elements: FilterNode[];
}

export interface SortConfig {
    field: string;
    source_id?: string;
    source_name?: string;
    table_name?: string;
    table_alias?: string;
    direction: 'ASC' | 'DESC';
}

export interface PaginationConfig {
    limit?: number;
    offset?: number;
    page?: number;
    size?: number;
}

export interface AggregationConfig {
    field: string;
    function: 'SUM' | 'AVG' | 'COUNT' | 'MIN' | 'MAX' | 'DISTINCT';
    alias: string;
}

// Enhanced Join Configuration
export interface JoinConditionConfig {
    left_table: string;
    left_field: string;
    left_table_name?: string;
    left_table_alias?: string;
    right_table: string;
    right_field: string;
    right_table_name?: string;
    right_table_alias?: string;
    operator: 'EQ' | 'GT' | 'GTE' | 'LT' | 'LTE' | 'NE';
}

export interface JoinConfig {
    table: string;
    table_alias: string;
    type: 'INNER' | 'LEFT' | 'RIGHT' | 'CROSS' | 'NATURAL_LEFT' | 'NATURAL_RIGHT';
    conditions: JoinConditionConfig[];
    using_fields?: string[]; // For USING clause
}

// Having condition for GROUP BY
export interface HavingConfig {
    field: string;
    source_id?: string;
    source_name?: string;
    table_name?: string;
    table_alias?: string;
    operator: 'EQ' | 'NE' | 'GT' | 'GTE' | 'LT' | 'LTE' | 'LIKE' | 'IN' | 'NOT_IN';
    value: string | number | boolean | Array<string | number>;
    function?: 'SUM' | 'COUNT' | 'AVG' | 'MIN' | 'MAX';
}

// Enhanced Query Option
export interface QueryOption {
    table?: string; // Main table
    table_alias?: string; // Main table alias
    fields: FieldConfig[];
    filters?: FilterNode;// For complex WHERE conditions
    group_by?: string[];
    having?: HavingConfig[];
    sort?: SortConfig[];
    pagination?: PaginationConfig;
    aggregations?: AggregationConfig[];
    joins?: JoinConfig[];
    distinct?: boolean;
    limit?: number;
    offset?: number;
}

export interface BarChartConfig {
    x_axis: string;
    x_axis_label?: string;
    y_axis: string;
    y_axis_label?: string;
    orientation?: 'vertical' | 'horizontal';
    stacked?: boolean;
    colors?: string[];
}

export interface PieChartConfig {
    label_field: string;
    value_field: string;
    show_percentage?: boolean;
    show_legend?: boolean;
    colors?: string[];
    donut?: boolean;
}

export interface LineChartConfig {
    x_axis: string;
    x_axis_label?: string;
    y_axis: string;
    y_axis_label?: string;
    smooth?: boolean;
    show_points?: boolean;
    fill_area?: boolean;
    colors?: string[];
}

export interface AreaChartConfig {
    x_axis: string;
    x_axis_label?: string;
    y_axis: string;
    y_axis_label?: string;
    stacked?: boolean;
    opacity?: number;
    colors?: string[];
}

export interface TableColumnConfig {
    field: string;
    header: string;
    sortable?: boolean;
    width?: string;
    align?: 'left' | 'center' | 'right';
}

export interface TableConfig {
    columns: TableColumnConfig[];
    show_header?: boolean;
    striped?: boolean;
    bordered?: boolean;
    pagination?: boolean;
    page_size?: number;
}

export interface ChartConfig {
    type: ChartType;
    mode: ChartMode;
    query_option?: QueryOption;
    bar_chart_config?: BarChartConfig;
    pie_chart_config?: PieChartConfig;
    line_chart_config?: LineChartConfig;
    area_chart_config?: AreaChartConfig;
    table_config?: TableConfig;
}

export interface CreateChartRequest {
    name: string;
    description: string;
    config: ChartConfig;
    sql_query?: string;
}

export interface ChartData {
    columns: string[];
    rows: any[][];
    total_rows?: number;
}

export interface ChartPreviewResponse {
    data: ChartData;
    success: boolean;
    message?: string;
}

// Mapping các toán tử theo kiểu dữ liệu cho FilterCondition
export const OPERATORS_BY_DATA_TYPE: Record<string, Array<FilterCondition['operator']>> = {
  TEXT: ['EQ', 'NE', 'LIKE', 'IN', 'NOT_IN', 'IS_NULL', 'IS_NOT_NULL', 'REGEXP'],
  BIGINT: ['EQ', 'NE', 'GT', 'GTE', 'LT', 'LTE', 'IN', 'NOT_IN', 'BETWEEN', 'IS_NULL', 'IS_NOT_NULL'],
  boolean: ['EQ', 'NE', 'IS_NULL', 'IS_NOT_NULL'],
  date: ['EQ', 'NE', 'GT', 'GTE', 'LT', 'LTE', 'BETWEEN', 'IS_NULL', 'IS_NOT_NULL'],
};

export function getOperatorsForDataType(dataType: string): Array<FilterCondition['operator']> {
  console.log(dataType);
    return OPERATORS_BY_DATA_TYPE[dataType] || ['EQ', 'NE', 'IS_NULL', 'IS_NOT_NULL'];
}