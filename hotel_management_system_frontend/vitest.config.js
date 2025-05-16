import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    include: ['src/__tests__/**/*.test.js'],
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      // mock 静态资源
      '@/assets/waoku.jpg': path.resolve(__dirname, 'src/__mocks__/waoku.jpg'),
      '@/assets/wuwu.jpeg': path.resolve(__dirname, 'src/__mocks__/wuwu.jpeg'),
    }
  }
})