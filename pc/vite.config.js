import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // Load env file based on `mode` in the current working directory.
  // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
  const env = loadEnv(mode, process.cwd(), '')

  console.log('env', mode)
  const isProd = mode === 'production'
  const isDev = mode === 'development'

  const viteEnv = {}
  // Only expose VITE_ prefixed variables to process.env
  for (const key in env) {
    if (key.startsWith('VITE_')) {
      viteEnv[key] = env[key]
    }
  }

  return {
    plugins: [
      vue(),
      //  自动引入Element Plus 组件 如 ElButton
      Components({
        resolvers: [ElementPlusResolver()],
      }),
      // 自动引入 Element Plus 相关的 API（如 ElMessage、ElNotification 等）
      AutoImport({
        resolvers: [ElementPlusResolver()],
      }),
    ],
    resolve: {
      alias: {
        "@": path.resolve(process.cwd(), "src"),
      },
    },
    define: {
      "process.env": JSON.stringify({...viteEnv, BUILD_TARGET: 'pc'}),
    },
    server: {
      host: '0.0.0.0',
      open: true,
      port: 3180,
    },
    build: {
      outDir: "dist",
      assetsDir: "assets",
    },
  };
});
