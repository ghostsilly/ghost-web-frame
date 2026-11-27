export const API_STATUS_LABEL = {
  EXISTS: '代码中存在',
  REMOVED: '代码中已移除',
}

export const FIELD_LABEL = {
  module: '模块',
  summary: '说明',
  handler: '处理方法',
  status: '状态',
}

export function apiStatusLabel(status) {
  return API_STATUS_LABEL[status] ?? status ?? ''
}

export function fieldLabel(field) {
  return FIELD_LABEL[field] ?? field
}
