export interface ChartSummary {
    id: string;
    name: string;
    description: string;
    type: string;
    updated_at: string;
    created_at: string;
}

export type ChartType = 'table' | 'bar' | 'line' | 'area' | 'pie';
export type ChartMode = 'basic' | 'advanced';

export interface FieldConfig {
    field_name: string;
    data_type: string;
    alias: string;
}

export interface FilterConfig {
    field: string;
    operator: 'EQ' | 'NE' | 'GT' | 'GTE' | 'LT' | 'LTE' | 'LIKE' | 'IN' | 'NOT_IN';
    value: string | number | boolean | Array<string | number>;
}

export interface SortConfig {
    field: string;
    direction: 'ASC' | 'DESC';
}

export interface PaginationConfig {
    page: number;
    size: number;
}

export interface AggregationConfig {
    field: string;
    function: 'SUM' | 'AVG' | 'COUNT' | 'MIN' | 'MAX';
    alias: string;
}

export interface JoinConditionConfig {
    left_field: string;
    right_field: string;
    operator: 'EQ' | 'GT' | 'GTE' | 'LT' | 'LTE';
}

export interface JoinConfig {
    table: string;
    type: 'INNER' | 'LEFT' | 'RIGHT' | 'FULL';
    conditions: JoinConditionConfig[];
    alias: string;
}

export interface QueryOption {
    table?: string; // Main table
    fields: FieldConfig[];
    filters?: FilterConfig[];
    group_by?: string[];
    sort?: SortConfig[];
    pagination?: PaginationConfig;
    aggregations?: AggregationConfig[];
    joins?: JoinConfig[];
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