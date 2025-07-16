import { setStorage, getStorage, removeStorage } from './storage';

const ACCESS_TOKEN_KEY = 'ACCESS_TOKEN';
const ACCESS_TOKEN_TIME_KEY = 'ACCESS_TOKEN_TIME';
const REFRESH_TOKEN_KEY = 'REFRESH_TOKEN';
const REFRESH_TOKEN_TIME_KEY = 'REFRESH_TOKEN_TIME';

/**
 * 将访问令牌和刷新令牌存入存储。
 * @param {object | string} tokenData - 包含 accessToken 和 refreshToken 的对象，或者仅 accessToken 字符串。
 * @param {string} tokenData.accessToken - 访问令牌。
 * @param {string} [tokenData.refreshToken] - 刷新令牌 (可选)。
 */
export function setToken(tokenData) {
  if (tokenData && typeof tokenData === 'object') {
    if (tokenData.access_token) {
      setStorage(ACCESS_TOKEN_KEY, tokenData.access_token);
    }
    if (tokenData.refresh_token) {
      setStorage(REFRESH_TOKEN_KEY, tokenData.refresh_token);
    }
    if(tokenData.expires_in){
      setStorage(ACCESS_TOKEN_TIME_KEY, tokenData.expires_in);
    }

    if(tokenData.refresh_token_expires_in){
      setStorage(REFRESH_TOKEN_TIME_KEY, tokenData.refresh_token_expires_in);
    }
  } else if (typeof tokenData === 'string') {
    // 如果只传入 accessToken 字符串，则作为后备方案
    setStorage(ACCESS_TOKEN_KEY, tokenData);
  } else if (tokenData === null) {
    // 如果传入null，则清除所有token
    removeToken();
  }
}

/**
 * 从存储中获取访问令牌。
 * @returns {string | null} 访问令牌，如果未找到则返回 null。
 */
export function getAccessToken() {
  return getStorage(ACCESS_TOKEN_KEY);
}

/**
 * 从存储中获取访问令牌过期时间。
 * @returns {number | null} 访问令牌过期时间，秒的时间戳，如果未找到则返回 null。
 */
export function getAccessTokenTime() {
  return getStorage(ACCESS_TOKEN_TIME_KEY);
}

/**
 * 从存储中获取刷新令牌。
 * @returns {string | null} 刷新令牌，如果未找到则返回 null。
 */
export function getRefreshToken() {
  return getStorage(REFRESH_TOKEN_KEY);
}

/**
 * 从存储中获取刷新令牌过期时间。
 * @returns {number | null} 刷新令牌过期时间，秒的时间戳，如果未找到则返回 null。
 */
export function getRefreshTokenTime() {
  return getStorage(REFRESH_TOKEN_TIME_KEY);
}


/**
 * 从存储中移除访问令牌和刷新令牌。
 */
export function removeToken() {
  removeStorage(ACCESS_TOKEN_KEY);
  removeStorage(REFRESH_TOKEN_KEY);
  removeStorage(ACCESS_TOKEN_TIME_KEY);
  removeStorage(REFRESH_TOKEN_TIME_KEY);
}
