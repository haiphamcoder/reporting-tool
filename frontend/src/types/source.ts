export interface SourceSummary {
  id: string;
  name: string;
  description: string;
  type: number;
  status: string;
  created_at: string;
  updated_at: string;
}

export interface SourceMapping {
  field_name: string;
  field_mapping: string;
  field_type: string;
  is_hidden: boolean;
}

export interface SourceDetail {
  id: string;
  name: string;
  description: string;
  connector_type: number;
  table_name?: string;
  config?: any;
  mapping?: SourceMapping[];
  status: number;
  last_sync_time?: string;
  created_at?: string;
  modified_at?: string;
  is_deleted?: boolean;
  is_starred?: boolean;
  user_id?: string;
} 