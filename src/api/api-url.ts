/**
 * API URL常量定义
 * 集中管理所有接口地址，便于维护和AI识别
 */

// 认证相关API
export const API_AUTH_LOGIN = '/api/auth/login';
export const API_AUTH_REFRESH_TOKEN = '/v1/admin/refresh-token';
export const API_AUTH_RESET_PASSWORD_REQUEST = '/api/auth/reset-password-request';
export const API_AUTH_RESET_PASSWORD = '/api/auth/reset-password';


// 用户相关API
export const API_USER_ACCOUNT_INFO = '/api/user/account-info';


// 管理员相关API
export const API_ADMIN_USER_LIST = '/api/admin/list';
export const API_ADMIN_USER_CREATE = '/api/admin/create';
export const API_ADMIN_USER_ACTIVATE = '/api/admin/activate';
export const API_ADMIN_USER_DEACTIVATE = '/api/admin/deactivate';
export const API_ADMIN_RESET_PASSWORD = '/api/admin/reset-password';


// 会话相关API
export const API_SESSION_LIST = '/api/session/list';
export const API_SESSION_CREATE = '/api/session/create';
export const API_SESSION_UPDATE = '/api/session/update';
export const API_SESSION_DELETE = '/api/session/delete';
export const API_SESSION_SHARE = '/api/session/share';
export const API_SESSION_SHARED = '/api/session/shared';


// 消息相关API
export const API_MESSAGE_SEND = '/api/message/send';
export const API_MESSAGE_LIST = '/api/message/list';
export const API_MESSAGE_STREAM = '/api/message/stream';


// 角色相关API
export const API_CHARACTER_LIST = '/api/character/list';
export const API_CHARACTER_CREATE = '/api/character/create';
export const API_CHARACTER_UPDATE = '/api/character/update';
export const API_CHARACTER_DELETE = '/api/character/delete';
export const API_CHARACTER_DETAIL = '/api/character/detail';
export const API_CHARACTER_CHAT_LIST = '/api/character/sessions';
export const API_CHARACTER_SESSION_CREATE = '/api/character/api/create';
export const API_CHARACTER_SESSION_DELETE = '/api/character/session/delete';
export const API_CHARACTER_SESSION_REMARK = '/api/character/session/remark';
export const API_CHARACTER_SEND = '/api/character/send';
export const API_CHARACTER_MESSAGE_LIST = '/api/character/messages';


// 图片工具相关API
export const API_IMAGE_TOOL_LIST = '/api/tools/image/list';
export const API_IMAGE_TOOL_CHAT = '/api/tools/image/chat';
export const API_IMAGE_TOOL_SESSIONS = '/api/tools/image/sessions';
export const API_IMAGE_TOOL_CREATE = '/api/tools/image/create';
export const API_IMAGE_TOOL_DETAIL = '/api/tools/image/detail';
export const API_IMAGE_TOOL_DELETE = '/api/tools/image/delete';
export const API_IMAGE_TOOL_MESSAGES = '/api/tools/image/messages';
export const API_IMAGE_TOOL_SESSION_DELETE = '/api/tools/image/session/delete';
export const API_IMAGE_TOOL_SESSION_REMARK = '/api/tools/image/session/remark';


// 文字工具相关API
export const API_TEXT_TOOL_LIST = '/api/tools/text/list';
export const API_TEXT_TOOL_CHAT = '/api/tools/text/chat';
export const API_TEXT_TOOL_SESSIONS = '/api/tools/text/sessions';
export const API_TEXT_TOOL_CREATE = '/api/tools/text/create';
export const API_TEXT_TOOL_DETAIL = '/api/tools/text/detail';
export const API_TEXT_TOOL_DELETE = '/api/tools/text/delete';
export const API_TEXT_TOOL_MESSAGES = '/api/tools/text/messages';
export const API_TEXT_TOOL_SESSION_DELETE = '/api/tools/text/session/delete';
export const API_TEXT_TOOL_SESSION_REMARK = '/api/tools/text/session/remark';


// 上传相关API
export const API_UPLOAD_PREPARE = '/api/upload/prepare';
export const API_UPLOAD_CONFIRM = '/api/upload/confirm';


// 模型相关API
export const API_MODEL_AVAILABLE = '/api/model/available';


// AI媒体生成相关API
export const API_GENERATION_TEXT_TO_IMAGE = '/api/generation/text-to-image';
export const API_GENERATION_TEXT_TO_VIDEO = '/api/generation/text-to-video';
export const API_GENERATION_IMAGE_TO_VIDEO = '/api/generation/image-to-video';
export const API_GENERATION_STATUS = '/api/generation/status';
export const API_GENERATION_RESULT = '/api/generation/result';
export const API_GENERATION_HISTORY = '/api/generation/history';
export const API_GENERATION_FAVORITES = '/api/generation/favorites';
export const API_GENERATION_FAVORITE_TOGGLE = '/api/generation/favorite';
export const API_GENERATION_DELETE = '/api/generation/delete';

