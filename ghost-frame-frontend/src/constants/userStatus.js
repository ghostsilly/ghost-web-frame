export const STATUS_LABEL = {
  NORMAL: '正常',
  LOCKED: '禁用',
}

export function statusLabel(status) {
  return STATUS_LABEL[status] ?? '未知状态'
}
