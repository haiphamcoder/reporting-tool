import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // Load env file based on `mode` in the current working directory.
  // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
  const env = loadEnv(mode, '.', '')
  
  const isProduction = mode === 'production'
  const isDevelopment = mode === 'development' || mode === 'dev'
  
  return {
    plugins: [react()],
    
    // Environment-specific configurations
    define: {
      __APP_ENV__: JSON.stringify(mode),
      __IS_PRODUCTION__: JSON.stringify(isProduction),
      __IS_DEVELOPMENT__: JSON.stringify(isDevelopment),
    },
    
    // Development server configuration
    server: isDevelopment ? {
      port: 3000,
      host: true,
      open: true,
      cors: true,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8765',
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    } : undefined,
    
    // Build configuration
    build: {
      outDir: 'dist',
      sourcemap: isDevelopment,
      minify: isProduction,
      rollupOptions: {
        output: {
          manualChunks: isProduction ? {
            vendor: ['react', 'react-dom'],
            mui: ['@mui/material', '@mui/icons-material'],
            charts: ['chart.js', 'react-chartjs-2']
          } : undefined
        }
      }
    },
    
    // Optimizations
    optimizeDeps: {
      include: ['react', 'react-dom', '@mui/material', '@mui/icons-material']
    },
    
    // Environment variables
    envPrefix: 'VITE_'
  }
})
