export interface ReportSummary {
    id: string;
    name: string;
    description: string;
    updated_at: string;
    created_at: string;
    owner?: {
        id: string;
        name: string;
        avatar?: string;
    };
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

export interface ReportConfig {
    blocks: ReportBlock[];
}

export interface ReportDetail {
    id: string;
    name: string;
    user_id: string;
    description: string;
    config: ReportConfig;
    is_deleted: boolean;
    created_at: string;
    modified_at: string;
}