import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    open: true
  },
  build: {
    outDir: 'dist'
  },
  define: {
    __UNI_PLATFORM__: JSON.stringify(process.env.UNI_PLATFORM || 'h5')
  },
  resolve: {
    alias: {
      '@': './'
    }
  },
  root: '.',
  publicDir: 'static'
})
