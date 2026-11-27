import request from './request'

/** 用户分页：GET /ghost/user/page，Query 参数 */
export function pageUsers(params) {
  return request.get('/user/page', { params })
}
