import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const isProd = mode === 'production'

  const viteEnv = {}
  for (const key in env) {
    if (key.startsWith('VITE_')) {
      viteEnv[key] = env[key]
    }
  }

  return {
    plugins: [
      vue(),
      Components({
        resolvers: [ElementPlusResolver()],
      }),
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
      "process.env": JSON.stringify({...viteEnv, BUILD_TARGET: 'mobile'}),
    },
    server: {
      host: '0.0.0.0',
      open: true,
      port: 3182,
    },
    build: {
      rollupOptions: {
        input: {
          main: path.resolve(__dirname, 'mobile.html')
        }
      },
      outDir: "dist-mobile",
      assetsDir: "assets",
    },
  };
});