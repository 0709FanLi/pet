/**
 * 基础HTTP客户端
 * 包含axios配置、拦截器等基础功能
 */
import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { getAccessToken } from '../utils/auth.ts';
import { ApiResponse } from './definitions';
import { DeviceDetector } from '@/utils/device-detector';

// 简化的设备检测
const detector = DeviceDetector.getInstance();
const isMobile = detector.isMobile;

/**
 * HTTP客户端类
 * 封装axios，提供统一的请求配置和错误处理
 */
export class HttpClient {
    private static instance: AxiosInstance;

    /**
     * 获取axios实例
     */
    static getInstance(): AxiosInstance {
        if (!this.instance) {
            this.createInstance();
        }
        return this.instance;
    }

    /**
     * 创建axios实例
     */
    private static createInstance(): void {
        const baseURL = (process.env.VITE_BASE_URL || '');
        const timeout = 100000;

        this.instance = axios.create({
            baseURL,
            timeout,
            withCredentials: false,
        });

        this.setupInterceptors();
    }

    /**
     * 设置请求和响应拦截器
     */
    private static setupInterceptors(): void {
        // 请求拦截器
        this.instance.interceptors.request.use(
            (config) => {
                // 添加token
                const token = getAccessToken();
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`;
                }

                // GET请求添加缓存控制
                if (config.method?.toUpperCase() === 'GET') {
                    config.headers['Cache-Control'] = 'no-cache';
                    config.headers['Pragma'] = 'no-cache';
                }

                return config;
            },
            (error) => {
                console.error('请求设置错误:', error);
                return Promise.reject(error);
            }
        );

        // 响应拦截器
        this.instance.interceptors.response.use(
            async (response: AxiosResponse) => {
                const processedData = await this.handleResponse(response);
                // 将处理后的数据放回response.data中
                response.data = processedData;
                return response;
            },
            (error) => {
                return this.handleError(error);
            }
        );
    }

    /**
     * 处理响应数据
     */
    private static async handleResponse(response: AxiosResponse): Promise<ApiResponse> {
        const { status, data } = response;
        const showErrorMessage = (response.config as any).showErrorMessage !== false; // 默认显示错误信息

        // 处理二进制数据
        if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
            if (data.type !== 'application/json') {
                return data;
            }
            try {
                const jsonData = await new Response(data).json();
                return jsonData;
            } catch (e) {
                console.error('Failed to parse blob data:', e);
                return data;
            }
        }

        if (!data) {
            if (showErrorMessage) {
                showToastFun('No response data from server')
            }
            throw new Error('No response data from server');
        }

        if (status === 200) {
            if (data.code === 200) {
                return data;
            } else {
                if (showErrorMessage) {
                    showToastFun(data.message || 'Request failed')
                }
                throw new Error(data.message || 'Request failed');
            }
        } else if (status === 401) {
            await this.handleUnauthorized();
            throw new Error('Unauthorized');
        } else if (status === 500) {
            if (showErrorMessage) {
                showToastFun(data.message || 'Internal server error')
            }
            throw new Error(data.message || 'Internal server error');
        } else {
            if (showErrorMessage) {
                showToastFun(`Request failed, status code: ${status}`)
            }
            throw new Error(`Request failed with status ${status}`);
        }
    }

    /**
     * 处理请求错误
     */
    private static async handleError(error: any): Promise<never> {
        const status = error?.response?.status;
        const showErrorMessage = (error?.config as any)?.showErrorMessage !== false; // 默认显示错误信息

        if (status === 401) {
            await this.handleUnauthorized();
        } else if (error.response) {
            const message = error.response.data?.message || `Server response error: ${status}`;
            if (showErrorMessage) {
                showToastFun(message)
            }
        } else if (error.request) {
            if (showErrorMessage) {
                showToastFun('Network connection error, please check network settings')
            }
        } else {
            if (showErrorMessage) {
                showToastFun(error.message || 'Unknown error occurred')
            }
        }

        console.error('HTTP request error:', error);
        throw error;
    }

    /**
     * 处理未授权错误
     */
    private static async handleUnauthorized(): Promise<void> {
        // 清除本地存储的认证信息
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        // 简单的提示信息
        console.warn('用户认证已过期，请重新登录');
        
        // 跳转到登录页面
        if (typeof window !== 'undefined' && window.location) {
            window.location.href = '/login';
        }
    }
}