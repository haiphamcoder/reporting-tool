export interface ReportSummary {
    id: string;
    name: string;
    description: string;
    number_of_charts: number;
    updated_at: string;
    created_at: string;
}

export type ReportBlockType = 'chart' | 'text';

export interface TextBlockFormat {
    fontFamily?: string;
    fontSize?: number;
    fontWeight?: 'normal' | 'bold' | number;
    fontStyle?: 'normal' | 'italic';
    textAlign?: 'left' | 'center' | 'right' | 'justify';
    color?: string;
    backgroundColor?: string;
    underline?: boolean;
    strikethrough?: boolean;
}

export interface TextBlockContent {
    text: string;
    format?: TextBlockFormat;
}

export interface ChartBlockContent {
    chartId: string;
    chart?: any;
}

export interface ReportBlock {
    id: string;
    type: ReportBlockType;
    content: TextBlockContent | ChartBlockContent;
}

export interface ReportDetail {
    id: string;
    name: string;
    user_id: string;
    description: string;
    blocks: ReportBlock[];
    is_deleted: boolean;
    created_at: string;
    modified_at: string;
}