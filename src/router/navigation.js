/**
 * 路由工具方法
 */
import router from './index';
import { ROUTER_PATHS} from './constants';

/**
 * 路由跳转，可携带参数
 * @param {string} path - 路由路径
 * @param {Object} params - 路由参数对象
 */
export const routerTo = (path, params = {}) => {
    // 参数处理
    if (Object.keys(params).length > 0) {
        try {
            const queryString = encodeURIComponent(JSON.stringify(params));
            router.push({
                path,
                query: { data: queryString }
            });
        } catch (error) {
            console.error('Error encoding params:', error);
            router.push(path);
        }
    } else {
        router.push(path);
    }
};

/**
 * 清空路由栈并跳转
 * @param {string} path - 路由路径
 * @param {Object} params - 路由参数对象
 */
export const routerReplace = (path, params = {}) => {
    if (Object.keys(params).length > 0) {
        try {
            const queryString = encodeURIComponent(JSON.stringify(params));
            router.replace({
                path,
                query: { data: queryString }
            });
        } catch (error) {
            console.error('Error encoding params:', error);
            router.replace(path);
        }
    } else {
        router.replace(path);
    }
};

/**
 * 返回上{count}页
 * @param {number} count - 回退的层数，默认为-1表示回退一层
 */
export const routerBack = (count = -1) => {
    router.go(count);
};

/**
 * 获取路由参数
 * @returns {Object} 解析后的参数对象
 */
export const getRouterParams = () => {
    const route = router.currentRoute.value;
    
    if (route.query && route.query.data) {
        try {
            const decodedString = decodeURIComponent(route.query.data);
    
            const parsedParams = JSON.parse(decodedString);
            
            return parsedParams;
        } catch (error) {
            console.error('Failed to parse route params:', error);
            return {};
        }
    }
    return {};
};

/**
 * 跳转到登录页
 * @param {Object} params - 登录后重定向信息
 */
export const routerToLogin = (params = {}) => {
    routerReplace(ROUTER_PATHS.LOGIN, params);
};