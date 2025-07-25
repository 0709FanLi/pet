/**
 * 认证相关工具函数
 * 处理token存储、获取等认证逻辑
 */

/**
 * 获取访问令牌
 * @returns {string | null} 访问令牌或null
 */
export function getAccessToken(): string | null {
    try {
        // 从localStorage获取token
        const token = localStorage.getItem('token');
        return token;
    } catch (error) {
        console.error('获取访问令牌失败:', error);
        return null;
    }
}

/**
 * 设置访问令牌
 * @param {string} token 访问令牌
 */
export function setAccessToken(token: string): void {
    try {
        localStorage.setItem('token', token);
    } catch (error) {
        console.error('设置访问令牌失败:', error);
    }
}

/**
 * 移除访问令牌
 */
export function removeAccessToken(): void {
    try {
        localStorage.removeItem('token');
    } catch (error) {
        console.error('移除访问令牌失败:', error);
    }
}

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
export function isAuthenticated(): boolean {
    const token = getAccessToken();
    return !!token;
}