import axios from "axios";
import router from "@/router";
import store from "@/store";
import { ElMessage } from "element-plus";

const BASE_URL = process.env.NODE_HOST;

axios.defaults.timeout = 5000; // 超时时间设置
axios.defaults.withCredentials = true; // true允许跨域
axios.defaults.baseURL = BASE_URL;
// Content-Type 响应头 - 修复为 JSON 格式
axios.defaults.headers.post["Content-Type"] = "application/json;charset=UTF-8";

// 响应拦截器
axios.interceptors.response.use(
  (response) => {
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    // 否则的话抛出错误
    if (response.status === 200) {
      return Promise.resolve(response);
    } else {
      return Promise.reject(response);
    }
  },
  // 服务器状态码不是2开头的的情况
  (error) => {
    if (error.response) {
      // 检查是否是用户被删除的情况
      if (error.response.data && error.response.data.message) {
        const message = error.response.data.message;
        if (message.includes('用户不存在') || message.includes('用户已被删除') || message.includes('账户不存在')) {
          // 清除用户登录状态
          store.commit('setToken', false);
          store.commit('setUserId', '');
          store.commit('setUsername', '');
          store.commit('setUserPic', '');
          
          // 显示提示信息
          ElMessage({
            message: '您的账户已被删除，请重新登录',
            type: 'warning'
          });
          
          // 跳转到登录页面
          router.replace({
            path: "/sign-in",
            query: {}
          });
          
          return Promise.reject(error.response);
        }
      }
      
      if (error.response.status) {
        switch (error.response.status) {
          // 400: 请求错误，可能是session失效或参数错误
          case 400: {
            // 检查错误信息，判断是否为session相关问题
            const errorMessage = error.response.data?.message || '';
            if (errorMessage.includes('session') || errorMessage.includes('登录') || errorMessage.includes('认证')) {
              // Session失效，清除登录状态
              store.commit('setToken', false);
              store.commit('clearUserInfo');
              localStorage.removeItem('dataStore');
              
              ElMessage({
                message: 'Session已失效，请重新登录',
                type: 'warning'
              });
              
              router.replace({
                path: "/sign-in",
                query: { redirect: router.currentRoute.value.fullPath }
              });
            } else {
              // 其他400错误，显示具体错误信息
              ElMessage({
                message: errorMessage || '请求参数错误',
                type: 'error'
              });
            }
            break;
          }
          // 401: 未登录或认证失败
          case 401: {
            // 清除登录状态
            store.commit('setToken', false);
            store.commit('clearUserInfo');
            localStorage.removeItem('dataStore');
            
            ElMessage({
              message: '认证失败，请重新登录',
              type: 'warning'
            });
            
            router.replace({
              path: "/sign-in",
              query: { redirect: router.currentRoute.value.fullPath }
            });
            break;
          }
          case 403:
            // console.log('管理员权限已修改请重新登录')
            // 跳转登录页面，并将要浏览的页面fullPath传过去，登录成功后跳转需要访问的页面
            setTimeout(() => {
              router.replace({
                path: "/sign-in",
                query: {
                  // redirect: router.currentRoute.fullPath
                },
              });
            }, 1000);
            break;

          // 404请求不存在
          case 404:
            // console.log('请求页面飞到火星去了')
            break;
        }
      }
      return Promise.reject(error.response);
    }
  }
);


export function getBaseURL() {
  return BASE_URL;
}

/**
 * 封装get方法
 * @param url
 * @param data
 * @returns {Promise}
 */
export function get<T = any>(url, params?: object): Promise<T> {
  return new Promise((resolve, reject) => {
    axios.get(url, params).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}

/**
 * 封装post请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function post(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.post(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}

/**
 * 封装delete请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function deletes(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.delete(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}

/**
 * 封装put请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function put(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.put(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}
