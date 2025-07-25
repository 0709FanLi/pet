import { createI18n } from 'vue-i18n';
import en from './en.json';

// ===== 常量区 =====
// 支持的语言列表
const supportedLanguages = ['en'];

// 语言包映射
const languageModules = {
  en
};

// 默认语言
const defaultLanguage = 'en';

// ===== 逻辑区 =====
// 获取浏览器语言
const getBrowserLanguage = () => {
  const browserLang = navigator.language.split('-')[0];
  return supportedLanguages.includes(browserLang) ? browserLang : defaultLanguage;
};

// 从本地存储获取用户选择的语言
const getSavedLanguage = () => {
  return localStorage.getItem('language') || null;
};

// 确定使用的语言
const determineLanguage = () => {
  return getSavedLanguage() || getBrowserLanguage() || defaultLanguage;
};

// 创建i18n实例
const i18n = createI18n({
  legacy: false,
  locale: determineLanguage(),
  fallbackLocale: defaultLanguage,
  messages: languageModules
});

// 获取用户语言（当前设置的语言）
const getUserLanguage = () => {
  return getSavedLanguage() || getBrowserLanguage() || defaultLanguage;
};

// 设置用户语言
const setUserLanguage = (lang) => {
  if (!supportedLanguages.includes(lang)) {
    console.error(`Language ${lang} is not supported`);
    return false;
  }
  
  localStorage.setItem('language', lang);
  i18n.global.locale.value = lang;
  document.querySelector('html').setAttribute('lang', lang);
  return true;
};

const t = i18n.global.t;

// ===== 导出区 =====
export { getBrowserLanguage, getUserLanguage, setUserLanguage, t };

// 默认导出i18n实例
export default i18n;
