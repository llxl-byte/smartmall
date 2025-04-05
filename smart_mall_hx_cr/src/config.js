export const API_BASE_URL = 'http://localhost:8083';

export function getApiUrl(endpoint) {
  return `${API_BASE_URL}${endpoint}`;
}