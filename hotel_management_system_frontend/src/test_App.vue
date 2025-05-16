// hotel_management_system_frontend/src/test_App.vue

import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import App from './App.vue'

describe('App.vue', () => {
  it('renders router-view', () => {
    const wrapper = mount(App, {
      global: {
        stubs: ['router-view']
      }
    })
    expect(wrapper.findComponent({ name: 'RouterView' }).exists()).toBe(true)
  })
})