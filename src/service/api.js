import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8081/api',  // 后端接口根路径
  headers: {
    'Content-Type': 'application/json'
  }
});

export default api;
