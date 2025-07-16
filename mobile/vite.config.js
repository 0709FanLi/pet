import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { VantResolver } from '@vant/auto-import-resolver';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, path.resolve(process.cwd(), '..'), '')
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
        resolvers: [VantResolver()],
      }),
      AutoImport({
        resolvers: [VantResolver()],
      }),
    ],
    resolve: {
      alias: {
        "@": path.resolve(process.cwd(), "src"),
      },
    },
    // define: {
    //   "process.env": JSON.stringify({...viteEnv, BUILD_TARGET: 'mobile'}),
    // },
    server: {
      host: '0.0.0.0',
      open: true,
      port: 3182,
    },
    build: {
      rollupOptions: {
        input: {
          main: path.resolve(process.cwd(), 'index.html')
        }
      },
      outDir: "dist",
      assetsDir: "assets",
    },
  };
});