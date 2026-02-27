<template>
  <div id="userLoginPage">
    <h2 class="title">AI Code Generator – Sign In</h2>
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
      <div class="tips">
        Don't have an account?
        <RouterLink to="/user/register">Sign Up</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Sign In</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userLogin } from '@/api/userController'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const handleSubmit = async (values: any) => {
  const res = await userLogin(values)
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('Signed in successfully')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('Sign in failed, ' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
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
