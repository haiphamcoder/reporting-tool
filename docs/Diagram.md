# Diagram

```mermaid
graph TB
    Client[Client Browser] --> Frontend[Frontend Service]
    Frontend --> APIGateway[API Gateway]
    
    subgraph Microservices
        APIGateway --> Auth[Authentication Service]
        APIGateway --> User[User Management Service]
        APIGateway --> Report[Reporting Service]
        APIGateway --> DataProc[Data Processing Service]
        APIGateway --> Storage[Storage Service]
        APIGateway --> Integrated[Integrated Service]
    end
    
    subgraph Discovery
        Eureka[Eureka Discovery Server]
    end
    
    subgraph Databases
        MySQL[(MySQL)]
        TiDB[(TiDB)]
        HDFS[(HDFS)]
    end
    
    Auth --> MySQL
    User --> MySQL
    Report --> TiDB
    DataProc --> HDFS
    Storage --> HDFS
    Integrated --> MySQL
    Integrated --> TiDB
    Integrated --> HDFS
```

```mermaid
erDiagram
    USERS ||--o{ REPORTS : creates
    USERS {
        int id PK
        string username
        string email
        string password
        string role
    }
    
    REPORTS ||--o{ REPORT_FILES : contains
    REPORTS {
        int id PK
        int user_id FK
        string title
        string description
        datetime created_at
        string status
    }
    
    REPORT_FILES {
        int id PK
        int report_id FK
        string file_path
        string file_type
        datetime uploaded_at
    }
    
    PROCESSING_JOBS ||--o{ REPORT_FILES : processes
    PROCESSING_JOBS {
        int id PK
        int file_id FK
        string status
        datetime started_at
        datetime completed_at
    }
```

```mermaid
sequenceDiagram
    participant Client
    participant Frontend
    participant Gateway
    participant Auth
    participant Report
    participant Storage
    participant DataProc
    
    Client->>Frontend: Login Request
    Frontend->>Gateway: Forward Login
    Gateway->>Auth: Authenticate
    Auth-->>Client: JWT Token
    
    Client->>Frontend: Create Report
    Frontend->>Gateway: Submit Report
    Gateway->>Report: Create Report
    Report->>Storage: Store Files
    Storage->>DataProc: Process Data
    DataProc-->>Report: Processing Complete
    Report-->>Client: Report Created
```

```mermaid
graph TB
    subgraph Client
        Browser[Web Browser]
    end
    
    subgraph Docker Containers
        subgraph Frontend
            FrontendContainer[Frontend:80]
        end
        
        subgraph Backend Services
            GatewayContainer[API Gateway:8765]
            AuthContainer[Auth Service:8081]
            UserContainer[User Service:8082]
            ReportContainer[Report Service:8083]
            DataProcContainer[Data Processing:8084]
            StorageContainer[Storage Service:8086]
            IntegratedContainer[Integrated Service:8085]
            EurekaContainer[Eureka Server:8761]
        end
        
        subgraph Databases
            MySQLContainer[MySQL:3306]
            TiDBContainer[TiDB:4000]
            HDFSContainer[HDFS:9000]
        end
    end
    
    Browser --> FrontendContainer
    FrontendContainer --> GatewayContainer
    GatewayContainer --> AuthContainer
    GatewayContainer --> UserContainer
    GatewayContainer --> ReportContainer
    GatewayContainer --> DataProcContainer
    GatewayContainer --> StorageContainer
    GatewayContainer --> IntegratedContainer
    
    AuthContainer --> EurekaContainer
    UserContainer --> EurekaContainer
    ReportContainer --> EurekaContainer
    DataProcContainer --> EurekaContainer
    StorageContainer --> EurekaContainer
    IntegratedContainer --> EurekaContainer
```

## Comprehensive System Design Diagram

```mermaid
graph TB
    subgraph Client Layer
        FE[Frontend Application]
    end

    subgraph Gateway Layer
        API[API Gateway]
    end

    subgraph Service Discovery
        Eureka[Eureka Discovery Server]
    end

    subgraph Core Services
        Auth[Authentication Service]
        UserMgmt[User Management Service]
        Report[Reporting Service]
        DataProc[Data Processing Service]
        Storage[Storage Service]
        Integ[Integrated Service]
    end

    subgraph Data Storage Layer
        MySQL[(MySQL Server)]
        HDFS[(HDFS Standalone)]
        TiDB[(TiDB Server)]
    end

    FE --> API
    API --> Eureka
    Eureka --> Auth
    Eureka --> UserMgmt
    Eureka --> Report
    Eureka --> DataProc
    Eureka --> Storage
    Eureka --> Integ

    Auth --> MySQL
    UserMgmt --> MySQL
    Report --> MySQL
    Report --> HDFS
    Report --> TiDB
    DataProc --> HDFS
    DataProc --> TiDB
    Storage --> TiDB
    Integ --> MySQL
```

## Relationship with Discovery Clients

```mermaid
sequenceDiagram
    participant Client as Discovery Client
    participant Eureka as Eureka Server
    participant Other as Other Services

    Client->>Eureka: 1. Register Service
    Eureka-->>Client: 2. Registration Confirmation
    
    loop Heartbeat
        Client->>Eureka: 3. Send Heartbeat
        Eureka-->>Client: 4. Heartbeat Response
    end

    Other->>Eureka: 5. Query Available Services
    Eureka-->>Other: 6. Return Service List
    Other->>Client: 7. Direct Communication
```

## Database design diagram

```mermaid
erDiagram
    %% MySQL Database Schema
    User {
        bigint id PK
        string username
        string email
        string password
        string full_name
        string role
        boolean is_active
        datetime created_at
        datetime updated_at
    }

    RefreshToken {
        bigint id PK
        bigint user_id FK
        string token
        datetime expiry_date
        datetime created_at
    }

    SourceConnector {
        bigint id PK
        bigint user_id FK
        string name
        string type
        string connection_string
        string credentials
        boolean is_active
        datetime created_at
        datetime updated_at
    }

    Chart {
        bigint id PK
        bigint user_id FK
        bigint source_id FK
        string name
        string type
        string query
        json configuration
        datetime created_at
        datetime updated_at
    }

    Report {
        bigint id PK
        bigint user_id FK
        string name
        string description
        boolean is_public
        datetime created_at
        datetime updated_at
    }

    ReportChart {
        bigint report_id FK
        bigint chart_id FK
        int position
    }

    %% TiDB Database Schema
    SourceData {
        bigint id PK
        bigint source_id FK
        string table_name
        json schema
        datetime last_updated
    }

    ProcessedData {
        bigint id PK
        bigint source_id FK
        string data_type
        json data
        datetime processed_at
    }

    %% Relationships
    User ||--o{ RefreshToken : "has"
    User ||--o{ SourceConnector : "owns"
    User ||--o{ Chart : "creates"
    User ||--o{ Report : "creates"
    SourceConnector ||--o{ Chart : "used_by"
    Chart }o--o{ Report : "belongs_to"
    SourceConnector ||--o{ SourceData : "generates"
    SourceConnector ||--o{ ProcessedData : "processes"

    %% HDFS Structure
    subgraph HDFS
        RawData["/raw_data"]
        FileStorage["/file_storage"]
    end
```