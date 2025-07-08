/**
 * 基础数据模型
 * 提供所有模型的基类和通用工具
 */

/**
 * 基础响应数据类
 * 所有数据模型的基类，提供通用方法
 */
export abstract class BaseResponseData {
    /**
     * 将原始数据转换为类实例
     */
    static fromJson<T extends BaseResponseData>(this: new () => T, data: any): T {
        const instance = new this();
        Object.assign(instance, data);
        return instance;
    }

    /**
     * 将类实例转换为普通对象
     */
    toJson(): Record<string, any> {
        return JSON.parse(JSON.stringify(this));
    }

    /**
     * 获取类型名称（用于调试）
     */
    get typeName(): string {
        return this.constructor.name;
    }

    /**
     * 克隆当前实例
     */
    clone<T extends BaseResponseData>(this: T): T {
        const Constructor = this.constructor as new () => T;
        return Constructor.prototype.constructor.fromJson(this.toJson());
    }
}

/**
 * 通用响应数据类
 * 用于处理不需要特定结构的API响应
 */
export class GenericResponseData extends BaseResponseData {
    // 可以包含任意数据
    [key: string]: any;
}

