<template>
  <div id="userRegisterPage">
    <h2 class="title">AI Code Generator – Sign Up</h2>
    <div class="desc">Generate complete apps without writing a single line of code</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: 'Please enter your account' }]">
        <a-input v-model:value="formState.userAccount" placeholder="Account" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: 'Please enter your password' },
          { min: 8, message: 'Password must be at least 8 characters' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="Password" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: 'Please confirm your password' },
          { min: 8, message: 'Password must be at least 8 characters' },
          { validator: validateCheckPassword },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="Confirm Password" />
      </a-form-item>
      <div class="tips">
        Already have an account?
        <RouterLink to="/user/login">Sign In</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Sign Up</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController'
import { message } from 'ant-design-vue'

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

/**
 * 验证确认密码
 */
const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value && value !== formState.userPassword) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

/**
 * 提交表单
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  const res = await userRegister(values)
  if (res.data.code === 0) {
    message.success('Registered successfully')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('Registration failed, ' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 80px auto;
  padding: 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.title {
  text-align: center;
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 700;
}

.desc {
  text-align: center;
  color: #888;
  margin-bottom: 24px;
  font-size: 13px;
}

.tips {
  margin-bottom: 16px;
  color: #888;
  font-size: 13px;
  text-align: right;
}
</style>
