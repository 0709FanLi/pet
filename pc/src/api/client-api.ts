/**
 * 统一API客户端
 * 提供类型安全的请求方法和自动数据转换
 */
import { HttpClient } from './client-http';
import { BaseResponseData } from '../models/base-models';
import { ApiResponse, RequestConfig } from './definitions';

/**
 * API客户端类
 * 封装HTTP请求，提供类型安全和自动数据转换
 */
export class ApiClient {
    /**
     * 发送请求并自动转换响应数据
     */
    static async request<T extends BaseResponseData>(
        config: RequestConfig,
        ResponseClass?: typeof BaseResponseData & (new () => T)
    ): Promise<ApiResponse<T>> {
        try {
            const httpClient = HttpClient.getInstance();
            const response = await httpClient.request({
                ...config,
                method: config.method || 'POST'
            });
            
            if (ResponseClass && response.data && response.data.data) {
                return {
                    code: response.data.code,
                    message: response.data.message,
                    data: ResponseClass.fromJson(response.data.data),
                };
            }
            
            return response.data;
        } catch (error) {
            console.error(`API请求失败 [${config.method || 'POST'}] ${config.url}:`, error);
            throw error;
        }
    }

    /**
     * GET请求
     */
    static async get<T extends BaseResponseData>(
        url: string, 
        params?: Record<string, any>, 
        ResponseClass?: typeof BaseResponseData & (new () => T),
        showErrorMessage?: boolean
    ): Promise<ApiResponse<T>> {
        return this.request({ url, method: 'GET' as any, params, showErrorMessage }, ResponseClass);
    }

    /**
     * POST请求
     */
    static async post<T extends BaseResponseData>(
        url: string, 
        data?: Record<string, any>, 
        ResponseClass?: typeof BaseResponseData & (new () => T),
        showErrorMessage?: boolean
    ): Promise<ApiResponse<T>> {
        return this.request({ url, method: 'POST' as any, data, showErrorMessage }, ResponseClass);
    }

    /**
     * PUT请求
     */
    static async put<T extends BaseResponseData>(
        url: string, 
        data?: Record<string, any>, 
        ResponseClass?: typeof BaseResponseData & (new () => T),
        showErrorMessage?: boolean
    ): Promise<ApiResponse<T>> {
        return this.request({ url, method: 'PUT' as any, data, showErrorMessage }, ResponseClass);
    }

    /**
     * DELETE请求
     */
    static async delete<T extends BaseResponseData>(
        url: string, 
        params?: Record<string, any>, 
        ResponseClass?: typeof BaseResponseData & (new () => T),
        showErrorMessage?: boolean
    ): Promise<ApiResponse<T>> {
        return this.request({ url, method: 'DELETE' as any, params, showErrorMessage }, ResponseClass);
    }
} 