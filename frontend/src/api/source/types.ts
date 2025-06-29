export interface SourceSummary {
  id: string;
  name: string;
  description: string;
  connector_type: number;
  status: string;
  created_at: string;
  updated_at: string;
}

export interface SourceMetadata {
  total_elements: number;
  number_of_elements: number;
  total_pages: number;
  current_page: number;
  page_size: number;
}

export interface SourcesResponse {
  success: boolean;
  result: {
    sources: SourceSummary[];
    metadata: SourceMetadata;
  };
}

export interface InitSourceRequest {
  name: string;
  connector_type: number;
  description?: string;
}

export interface InitSourceResponse {
  success: boolean;
  result: {
    id: string;
  };
}

export interface SchemaField {
  field_name: string;
  field_mapping: string;
  field_type: string;
  is_hidden: boolean;
}

export interface GetSchemaResponse {
  success: boolean;
  result: SchemaField[];
}

export interface ConfirmSchemaRequest {
  id: string;
  mapping: SchemaField[];
}

export interface ConfirmSchemaResponse {
  success: boolean;
  result: {
    message: string;
  };
}

export interface SubmitImportResponse {
  success: boolean;
  result: {
    message: string;
  };
}

export interface SourceDetails {
  id: string;
  name: string;
  description: string;
  connector_type: number;
  status: string;
  connection_config: any;
  schema: SchemaField[];
  created_at: string;
  updated_at: string;
}

export interface SourceDetailsResponse {
  success: boolean;
  result: SourceDetails;
} 