import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // 假设后端服务器地址，根据实际情况调整

export const UserAuthApi = {
  async sendCode(data) {
  try {
    const response = await axios.post(`${API_BASE_URL}/api/users/send-code`, data);
    return response.data;
  } catch (error) {
    throw error;
  }
},

  async phoneLogin(data) {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/users/phone-login`, data);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};