// 获取当前 Codespace 的域名
const getCodespaceUrl = () => {
  if (window.location.hostname.includes('.app.github.dev')) {
    // 在 GitHub Codespaces 环境中，使用相同的域名，但端口号改为 8083
    const domain = window.location.hostname;
    return `https://${domain.replace('-8080', '-8083')}`;
  }
  return 'http://localhost:8083'; // 本地开发环境
};

export const API_BASE_URL = getCodespaceUrl();

// 打印 API_BASE_URL 便于调试
console.log('API_BASE_URL:', API_BASE_URL);

export function getApiUrl(endpoint) {
  return `${API_BASE_URL}${endpoint}`;
}
