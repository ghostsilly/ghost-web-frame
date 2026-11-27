// 后端 UserRole 经 Jackson 序列化为枚举名
export const ROLE_LABEL = {
  USER: '普通用户',
  ADMIN: '管理员',
  SUPER_ADMIN: '超级管理员',
}

export function roleLabel(role) {
  return ROLE_LABEL[role] ?? '未知角色'
}
