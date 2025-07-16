/**
 * 用户认证相关API
 * 包括登录、注册、验证码等接口
 */
import { ApiClient } from './client-api';
import { BaseResponseData } from '../models/base-models';
import { ApiResponse } from './definitions';

/**
 * 登录请求参数
 */
export interface LoginRequest {
    username: string;
    password: string;
}

/**
 * 统一登录/注册请求参数
 */
export interface LoginOrRegisterRequest {
    username: string;
    password: string;
    email: string;
}

/**
 * 手机验证码登录请求参数
 */
export interface PhoneLoginRequest {
    phone: string;
    verificationCode: string;
}

/**
 * 发送验证码请求参数
 */
export interface SendCodeRequest {
    phone: string;
    type: 'auth' | 'reset'; // auth: 登录注册, reset: 重置密码
}

/**
 * 用户信息响应
 */
export class UserInfo extends BaseResponseData {
    id?: number;
    username?: string;
    email?: string;
    phone?: string;
    token?: string;
    
    static fromJson(json: any): BaseResponseData {
        const userInfo = new UserInfo();
        userInfo.id = json.id;
        userInfo.username = json.username;
        userInfo.email = json.email;
        userInfo.phone = json.phone;
        userInfo.token = json.token;
        return userInfo;
    }
}

/**
 * 用户认证API类
 */
export class UserAuthApi {
    /**
     * 用户名密码登录
     */
    static async login(request: LoginRequest): Promise<ApiResponse<any>> {
        return ApiClient.post(
            '/api/users/login',
            request
        );
    }

    /**
     * 统一登录/注册接口
     * 如果用户已存在则直接登录，如果不存在则先注册再登录
     */
    static async loginOrRegister(request: LoginOrRegisterRequest): Promise<ApiResponse<any>> {
        return ApiClient.post(
            '/api/users/login-or-register',
            request
        );
    }

    /**
     * 手机验证码登录/注册
     */
    static async phoneLogin(request: PhoneLoginRequest): Promise<ApiResponse<any>> {
        return ApiClient.post(
            '/api/users/phone-login',
            request
        );
    }

    /**
     * 发送验证码
     */
    static async sendCode(request: SendCodeRequest): Promise<ApiResponse<any>> {
        return ApiClient.post(
            '/api/users/send-code',
            request
        );
    }

    /**
     * 刷新Token
     */
    static async refreshToken(): Promise<ApiResponse<any>> {
        return ApiClient.post(
            '/api/users/refresh-token',
            {}
        );
    }

    /**
     * 退出登录
     */
    static async logout(): Promise<ApiResponse<any>> {
        return ApiClient.post(
            '/api/users/logout'
        );
    }
}