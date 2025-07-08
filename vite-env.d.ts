/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string;
  readonly VITE_APP_LOGO: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}