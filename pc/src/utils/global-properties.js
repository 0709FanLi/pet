/**
 * 全局属性管理
 * 用于注册和管理Vue应用的全局属性
 */

/**
 * 最近上传文件列表
 * @returns {Object} 最近文件列表对象
 */
const createRecentFiles = () => {
  return {
    value: [],
    get() { 
      return this.value; 
    },
    set(newFiles) { 
      this.value = newFiles;
      if (this.value.length > 100) {
        this.value = this.value.slice(this.value.length - 100);
      }
    }
  };
};

/**
 * 注册全局属性
 * @param {import('vue').App} app Vue应用实例
 */
export function setupGlobalProperties(app) {
  // 注册全局属性集合
  const properties = {
    // 最近上传的文件列表
    $recentFiles: createRecentFiles(),
    
    // 可在此处添加更多全局属性
    // $propertyName: createPropertyValue(),
  };

  // 将属性注册到全局
  Object.entries(properties).forEach(([key, value]) => {
    app.config.globalProperties[key] = value;
  });
} 