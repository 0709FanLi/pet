/**
 * API接口相关定义
 */

// 基础接口
export interface ApiResponse<T = any> {
    code: number;
    data: T;
    message: string;
}

export interface RequestConfig {
    url: string;
    method?: RequestMethod;
    params?: Record<string, any>;
    data?: Record<string, any>;
    headers?: Record<string, string>;
    timeout?: number;
    requireAuth?: boolean;
    showErrorMessage?: boolean;
}

export enum RequestMethod {
    GET = 'GET',
    POST = 'POST',
    PUT = 'PUT',
    DELETE = 'DELETE',
}

// AI媒体相关接口定义
export interface AIMediaGenerateImageRequest {
    prompt: string;
    negative_prompt: string;
    model: string;
    aspect_ratio?: string; // 仅XMIND-G-IMAGEN-3模型时传入
}

export interface AIMediaGenerateImageResponse {
    id: string;
    taskId?: string;
    status: 'pending' | 'processing' | 'completed' | 'failed';
    imageUrl?: string;
    progress?: number;
    error?: string;
    createdAt: string;
    completedAt?: string;
}