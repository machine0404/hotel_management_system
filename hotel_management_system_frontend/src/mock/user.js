// 模拟用户数据
export const mockUserData = {
    id: 1,
    username: 'demo_user',
    email: 'demo@example.com',
    phone: '13800138000',
    gender: 0,
    create_time: '2024-03-15 10:00:00',
    points: 100,
    status: 0
}

// 模拟登录响应
export const mockLoginResponse = {
    token: 'mock_token_12345',
    message: '登录成功'
}

// 模拟修改密码响应
export const mockChangePasswordResponse = {
    data: {
        success: true,
        message: '密码修改成功'
    }
}

// 模拟更新用户信息响应
export const mockUpdateUserResponse = {
    data: {
        success: true,
        message: '更新成功'
    }
} 