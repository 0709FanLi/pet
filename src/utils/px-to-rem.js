/**
 * px转rem工具
 * 基于项目统一基准值 16px = 1rem
 */

let REM_BASE_FONT_SIZE = 16;
/**
 * px转rem的核心方法
 * @param {number|string} pxValue - px值，可以是数字或带px的字符串
 * @returns {string} rem值字符串，如 "1.5rem"
 */
export function pxToRem(pxValue) {
  if(typeof pxValue === 'number' || (typeof pxValue === 'string' && pxValue.includes('px'))) {
    const numericValue = typeof pxValue === 'string' 
      ? parseFloat(pxValue.replace('px', ''))
      : pxValue;
    
    if (isNaN(numericValue) || numericValue === 0) {
      return '0';
    }
    
    const remValue = numericValue / REM_BASE_FONT_SIZE;
    return `${parseFloat(remValue.toFixed(4))}rem`;
  }
  return pxValue;
}

/**
 * 获取数值形式的rem（用于计算）
 * @param {number|string} pxValue - px值
 * @returns {number} rem数值
 */
export function pxToRemNumber(pxValue) {
  if (typeof pxValue !== 'number' && (typeof pxValue !== 'string' || !pxValue.includes('px'))) {
    return pxValue; // 如果不是有效的px值，返回0
  }
  const numericValue = typeof pxValue === 'string' 
    ? parseFloat(pxValue.replace('px', ''))
    : pxValue;
  
  if (isNaN(numericValue)) {
    return 0;
  }
  
  return parseFloat((numericValue / REM_BASE_FONT_SIZE).toFixed(4));
}

/**
 * 批量转换样式对象中的px值
 * @param {Object} styleObj - 样式对象
 * @returns {Object} 转换后的样式对象
 */
export function convertStyleObject(styleObj) {
  const result = {};
  
  for (const [key, value] of Object.entries(styleObj)) {
    if (typeof value === 'string' && value.includes('px')) {
      result[key] = value.replace(/(\d+(?:\.\d+)?)px/g, (match, px) => {
        return pxToRem(parseFloat(px));
      });
    } else {
      result[key] = value;
    }
  }
  
  return result;
}



// 默认导出主要转换函数
export default pxToRem; 