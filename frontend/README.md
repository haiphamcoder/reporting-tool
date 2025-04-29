# CDP Hub UI

This is the UI for the CDP Hub. It is a React application that uses the Material-UI library.

## Features

- Modern React application built with TypeScript
- Material-UI (MUI) components and theming
- Responsive design that works on mobile and desktop
- Dark/light mode theme switching
- Authentication flows (sign in, sign up, forgot password)
- Side navigation menu with mobile support
- Profile and account management

## Architecture

```mermaid
graph TB
    User((User))

    subgraph "CDP Hub UI System"
        subgraph "Frontend Container (React)"
            UI["CDP Hub UI<br>(React + TypeScript + Vite)"]
            
            subgraph "Core Components"
                Router["Router<br>(React Router)"]
                AuthProvider["Auth Provider<br>(React Context)"]
                ProtectedRoute["Protected Route<br>(React Component)"]
            end

            subgraph "Page Components"
                Dashboard["Dashboard<br>(React Component)"]
                SignIn["Sign In Page<br>(React Component)"]
                SignUp["Sign Up Page<br>(React Component)"]
            end

            subgraph "UI Components"
                AppNavbar["App Navbar<br>(MUI Component)"]
                Header["Header<br>(MUI Component)"]
                MainGrid["Main Grid<br>(MUI Component)"]
                ThemeProvider["App Theme<br>(MUI Theme)"]
                SearchComp["Search<br>(MUI Component)"]
                MenuComponents["Menu Components<br>(MUI Component)"]
            end
        end

        subgraph "API Container"
            APIConfig["API Configuration<br>(TypeScript)"]
        end
    end

    subgraph "External Systems"
        AuthServer["Authentication Server<br>(Port 8080)"]
        WebServer["Web Server<br>(Nginx)"]
    end

    %% User Interactions
    User -->|"Accesses UI<br>(Port 3000)"| WebServer
    WebServer -->|"Serves"| UI

    %% Core Component Relations
    UI -->|"Uses"| Router
    UI -->|"Uses"| AuthProvider
    Router -->|"Protects Routes"| ProtectedRoute
    ProtectedRoute -->|"Guards"| Dashboard

    %% Page Component Relations
    Router -->|"Routes to"| SignIn
    Router -->|"Routes to"| SignUp
    Router -->|"Routes to"| Dashboard

    %% UI Component Relations
    Dashboard -->|"Renders"| AppNavbar
    Dashboard -->|"Renders"| Header
    Dashboard -->|"Renders"| MainGrid
    UI -->|"Applies"| ThemeProvider
    AppNavbar -->|"Contains"| SearchComp
    AppNavbar -->|"Contains"| MenuComponents

    %% API Relations
    AuthProvider -->|"Authenticates via"| APIConfig
    APIConfig -->|"Communicates with<br>/api/v1/auth"| AuthServer
```

## Getting Started

### Prerequisites

- Node.js 16.x or later
- npm or yarn package manager

### Installation

1. Clone the repository

```bash
git clone https://github.com/haiphamcoder/cdp-hub-ui.git
cd cdp-hub-ui
```

2. Install dependencies

```bash
npm install
```

3. Start the development server

```bash
npm run dev
```

4. Open your browser and navigate to `http://localhost:5173` to see the application in action.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for the latest changes.