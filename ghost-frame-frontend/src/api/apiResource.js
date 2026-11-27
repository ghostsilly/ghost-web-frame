import request from './request'

/** 扫描并比对，只读：POST /ghost/api-resource/scan-diff */
export function scanDiff() {
  return request.post('/api-resource/scan-diff')
}

/** 应用已勾选的差异：POST /ghost/api-resource/apply */
export function applyDiff(data) {
  return request.post('/api-resource/apply', data)
}

/** 当前表内接口清单：GET /ghost/api-resource/list */
export function listResources() {
  return request.get('/api-resource/list')
}
