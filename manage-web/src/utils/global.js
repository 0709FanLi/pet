/**
 * 是否是 生产环境
 * @return {boolean}
 */
export function isEnvProduction() {
    return import.meta.env.MODE === 'production';
}

/**
 * 是否是 测试环境
 */
export function isEnvDevelopment() {
    return import.meta.env.MODE === 'development';
}

/**
 * 是否是 本地调试环境
 * @return {boolean}
 */
export function isEnvLocal() {
    return import.meta.env.MODE === 'development' && 
           (location.hostname === 'localhost' || 
            location.hostname === '127.0.0.1' || 
            location.hostname.startsWith('192.168.') ||
            location.hostname.startsWith('10.') ||
            location.hostname.endsWith('.local'));
}