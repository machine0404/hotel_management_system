import { mount, flushPromises } from '@vue/test-utils'
import LoginRegister from '../views/LoginRegister.vue'
import { describe, it, expect, vi } from 'vitest'
import * as userApi from '../api/user'

describe('LoginRegister.vue', () => {
  it('登录成功后跳转到 /findroom', async () => {
    // mock loginApi
    vi.spyOn(userApi, 'loginApi').mockResolvedValue({ message: '登录成功' })
    const push = vi.fn()
    const wrapper = mount(LoginRegister, {
      global: {
        mocks: {
          $router: { push }
        }
      }
    })
    // 填写表单
    await wrapper.find('input[placeholder="用户名"]').setValue('testuser')
    await wrapper.find('input[placeholder="密码"]').setValue('123456')
    // 点击登录
    await wrapper.find('.login-form button').trigger('click')
    await flushPromises()
    expect(push).toHaveBeenCalledWith('/findroom')
  })
})