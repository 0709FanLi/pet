/**
 * localStorage 存储工具类
 */

// 存储前缀
const PERMANENT_PREFIX = 'al_';  // 永久存储前缀
const TEMPORARY_PREFIX = 'al_t_'; // 临时存储前缀

// 默认过期时间（7天，单位：毫秒）
const DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

// 用户ID，可后期配置
let UID = '';

/**
 * 获取完整的存储键名
 * @param {string} key 原始键名
 * @param {boolean} isTemp 是否为临时存储
 * @param {boolean} useUID 是否使用UID
 * @returns {string} 完整键名
 */
const getFullKey = (key, isTemp = false, useUID = false) => {
    const prefix = isTemp ? TEMPORARY_PREFIX : PERMANENT_PREFIX;
    const uidStr = useUID && UID ? `${UID}_` : '';
    return `${prefix}${uidStr}${key}`;
};


/**
 * 初始化存储工具
 * @param {string} uid 用户ID
 * @param {boolean} clearExpired 是否清除过期存储，默认为true
 * @returns {void}
 */
export function storageInit(uid = '', clearExpired = true) {
    // 设置用户ID
    UID = uid;
    
    // 清除过期存储
    if (clearExpired) {
        storageClearExpired();
    }
};


/**
 * 设置永久存储
 * @param {string} key 键名
 * @param {any} value 存储值
 * @param {boolean} useUID 是否使用UID
 * @returns {void}
 */
export function setStorage(key, value, useUID = false) {
    const fullKey = getFullKey(key, false, useUID);
    
    // 根据不同类型进行存储转换
    if (typeof value === 'object' && value !== null) {
        localStorage.setItem(fullKey, JSON.stringify(value));
    } else {
        localStorage.setItem(fullKey, String(value));
    }
};

/**
 * 获取永久存储
 * @param {string} key 键名
 * @param {boolean} useUID 是否使用UID
 * @returns {any} 存储值
 */
export function getStorage(key, useUID = false) {
    const fullKey = getFullKey(key, false, useUID);
    const value = localStorage.getItem(fullKey);
    
    if (value === null) return null;
    
    // 尝试解析JSON
    try {
        return JSON.parse(value);
    } catch (error) {
        // 不是JSON格式，返回原始值并尝试转换类型
        if (value === 'true') return true;
        if (value === 'false') return false;
        if (!isNaN(value) && value.trim() !== '') return Number(value);
        return value;
    }
};

/**
 * 设置临时存储（带过期时间）
 * @param {string} key 键名
 * @param {any} value 存储值
 * @param {number} expireTime 过期时间（毫秒），默认7天
 * @param {boolean} useUID 是否使用UID
 * @returns {void}
 */
export function setStorageTemp(key, value, expireTime = DEFAULT_EXPIRE_TIME, useUID = false) {
    const fullKey = getFullKey(key, true, useUID);
    const now = new Date().getTime();
    
    const data = {
        value,
        expire: now + expireTime
    };
    
    localStorage.setItem(fullKey, JSON.stringify(data));
};

/**
 * 获取临时存储
 * @param {string} key 键名
 * @param {boolean} useUID 是否使用UID
 * @returns {any} 存储值，如果过期则返回null
 */
export function getStorageTemp(key, useUID = false) {
    const fullKey = getFullKey(key, true, useUID);
    const value = localStorage.getItem(fullKey);
    
    if (value === null) return null;
    
    try {
        const data = JSON.parse(value);
        const now = new Date().getTime();
        
        // 检查是否过期
        if (data.expire && data.expire < now) {
            // 已过期，删除存储并返回null
            localStorage.removeItem(fullKey);
            return null;
        }
        
        const result = data.value;
        
        // 对原始值进行类型转换
        if (typeof result === 'string') {
            if (result === 'true') return true;
            if (result === 'false') return false;
            if (!isNaN(result) && result.trim() !== '') return Number(result);
        }
        
        return result;
    } catch (error) {
        return null;
    }
};

/**
 * 移除存储项
 * @param {string} key 键名
 * @param {boolean} isTemp 是否为临时存储
 * @param {boolean} useUID 是否使用UID
 * @returns {void}
 */
export function removeStorage(key, isTemp = false, useUID = false) {
    const fullKey = getFullKey(key, isTemp, useUID);
    localStorage.removeItem(fullKey);
};

/**
 * 清除所有过期的临时存储
 * @returns {number} 清除的存储项数量
 */
export function clearStorageExpired() {
    const keys = Object.keys(localStorage);
    const now = new Date().getTime();
    let clearCount = 0;
    
    keys.forEach(key => {
        // 只处理临时存储项
        if (key.startsWith(TEMPORARY_PREFIX)) {
            try {
                const value = localStorage.getItem(key);
                if (value) {
                    const data = JSON.parse(value);
                    // 检查是否过期
                    if (data.expire && data.expire < now) {
                        localStorage.removeItem(key);
                        clearCount++;
                    }
                }
            } catch (error) {
                // 解析错误，可能不是有效的临时存储格式，不处理
            }
        }
    });
    
    return clearCount;
};

/**
 * 清除所有相关存储
 * @param {boolean} clearTemp 是否清除临时存储
 * @param {boolean} clearPermanent 是否清除永久存储
 * @param {boolean} onlyUID 是否只清除与当前UID相关的存储
 * @returns {void}
 */
export function clearStorage(clearTemp = true, clearPermanent = true, onlyUID = false) {
    const keys = Object.keys(localStorage);
    
    keys.forEach(key => {
        const isTempKey = key.startsWith(TEMPORARY_PREFIX);
        const isPermanentKey = key.startsWith(PERMANENT_PREFIX);
        
        // 仅清除UID相关存储时的检查
        const isUIDKey = onlyUID && UID ? key.includes(`_${UID}_`) : true;
        
        if (clearTemp && isTempKey && isUIDKey) {
            localStorage.removeItem(key);
        }
        
        if (clearPermanent && isPermanentKey && isUIDKey) {
            localStorage.removeItem(key);
        }
    });
};
