// API基础URL配置
export const API_BASE_URL = 'http://127.0.0.1:8083';

// 其他API相关配置
export const API_TIMEOUT = 10000; // 请求超时时间（毫秒）

// 添加请求拦截器
uni.addInterceptor('request', {
    invoke(args) {
        // 请求发送前拦截
        const token = localStorage.getItem('jwtToken'); // 从localStorage中获取令牌
        if (token) {
            // 如果存在令牌，添加到请求头中
            args.header = {
                ...args.header,
                'Authorization': 'Bearer ' + token
            };
        }
        return args;
    },
    success(res) {
        // 请求成功后拦截
        // 您可以在这里处理统一的响应逻辑，例如检查状态码、处理错误等
        if (res.statusCode === 401) {
            // 令牌无效或过期
            localStorage.removeItem('jwtToken'); // 清除令牌
            uni.showToast({
                title: '登录已过期，请重新登录',
                icon: 'none'
            });
            // 跳转到登录页面
            uni.navigateTo({
                url: '/pages/login/login'
            });
        }
        return res;
    },
    fail(err) {
        // 请求失败后拦截
        return err;
    }
});
