import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // Element Plus 按需自动导入：模板里直接写 el-* 组件即可，样式随组件引入
    AutoImport({ resolvers: [ElementPlusResolver()] }),
    Components({ resolvers: [ElementPlusResolver()] }),
  ],

  server: {
    host: true,
    port: 5187,
    proxy: {
      '/ghost': {
        target: 'http://localhost:10583',
        changeOrigin: true,
      }
    }
  }

})
