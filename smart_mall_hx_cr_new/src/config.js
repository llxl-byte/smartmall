// 获取当前环境的API基础URL
const getApiBaseUrl = () => {
  // 检查是否在GitHub Codespaces环境
  if (window.location.hostname.includes('.app.github.dev')) {
    // 在GitHub Codespaces环境中，使用相同的域名，但端口号改为8083
    // 修改为使用HTTP协议而非HTTPS
    const domain = window.location.hostname;
    return `http://${domain.replace('-8080', '-8083')}`;
  }
  // 检查是否在其他网络环境
  else if (window.location.hostname !== 'localhost') {
    // 在其他网络环境下，使用http协议和8083端口
    return `http://${window.location.hostname}:8083`;
  }

  // 本地开发环境，使用localhost和8083端口
  return 'http://localhost:8083';
};

export const API_BASE_URL = getApiBaseUrl();

// 打印 API_BASE_URL 便于调试
console.log('API_BASE_URL:', API_BASE_URL);

export function getApiUrl(endpoint) {
  return `${API_BASE_URL}${endpoint}`;
}
