<template>
  <v-container class="login-wrap py-12">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="10" md="8" lg="5">
        <v-card class="elevated-card">
          <!-- 品牌抬头 -->
          <div class="hero">
            <div class="brand-row">
              <v-avatar size="36" class="brand-logo">
                <v-img src="/logo.png" contain></v-img>
              </v-avatar>
              <div class="brand-text">
                <div class="brand-name text-truncate">SilverCare</div>
                <div class="brand-tagline">{{ $t('login.title') }}</div>
              </div>
            </div>
          </div>

          <v-card-text class="pt-6 pb-0">
            <v-form ref="formRef" v-model="isValid" @submit.prevent="onSubmit">
              <v-text-field
                v-model.trim="form.username"
                :label="$t('login.username')"
                :rules="[rules.required]"
                prepend-inner-icon="mdi-account"
                dense
                outlined
                clearable
                autocomplete="username"
              />

              <v-text-field
                v-model="form.password"
                :type="showPwd ? 'text' : 'password'"
                :label="$t('login.password')"
                :rules="[rules.required]"
                prepend-inner-icon="mdi-lock"
                :append-icon="showPwd ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append="showPwd = !showPwd"
                @keydown.native="checkCaps($event)"
                dense
                outlined
                autocomplete="current-password"
              />
              <div v-if="capsOn" class="caption warning mb-2">
                Caps Lock is on
              </div>

              <div class="d-flex align-center justify-space-between mt-1">
                <v-checkbox
                  v-model="remember"
                  hide-details
                  dense
                  class="my-0"
                  label="Keep me signed in"
                />
                <v-btn text small color="primary" @click="goReset">
                  Forgot password?
                </v-btn>
              </div>

              <v-btn
                type="submit"
                color="primary"
                block
                class="mt-4"
                :loading="loading"
                :disabled="!isValid || loading"
              >
                {{ $t('login.button') }}
              </v-btn>

              <div class="divider my-6">
                <span>or</span>
              </div>

              <!-- 预留：SSO 按钮（需要时接 OAuth） -->
              <v-btn block outlined class="mb-2" @click="signinWith('google')">
                <v-icon left>mdi-google</v-icon> Continue with Google
              </v-btn>
              <v-btn block outlined @click="signinWith('microsoft')">
                <v-icon left>mdi-microsoft</v-icon> Continue with Microsoft
              </v-btn>
            </v-form>
          </v-card-text>

          <v-divider class="mt-4" />

          <v-card-actions class="justify-center">
            <div class="hint">
              New to SilverCare?
              <!-- 关键：使用命名路由跳转到注册页 -->
              <v-btn text small color="primary" :to="{ name: 'Register' }">
                Create account
              </v-btn>
            </div>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- 错误提示 -->
    <v-snackbar v-model="snackbar.show" :timeout="4000" color="error" top right>
      {{ snackbar.msg }}
      <v-btn text @click="snackbar.show = false">Close</v-btn>
    </v-snackbar>

    <!-- 全局遮罩 loading（可选） -->
    <v-overlay :value="loading" opacity="0.08">
      <v-progress-circular indeterminate size="48" />
    </v-overlay>
  </v-container>
</template>

<script>
import api from '@/service/api'

export default {
  name: 'LoginView',
  data() {
    return {
      loading: false,
      isValid: false,
      showPwd: false,
      capsOn: false,
      remember: true,
      snackbar: { show: false, msg: '' },
      form: {
        username: '',
        password: ''
      },
      rules: {
        required: v => !!v || 'Required'
      }
    }
  },
  methods: {
    checkCaps(e) {
      const caps = e.getModifierState && e.getModifierState('CapsLock')
      this.capsOn = !!caps
    },
    async onSubmit() {
      const ok = await this.$refs.formRef.validate()
      if (!ok) return
      this.loading = true
      try {
        const payload = {
          username: this.form.username.trim(),
          password: this.form.password
        }
        const { data } = await api.post('/auth/login', payload)
        console.log('Login raw response:', data)

        // ✅ 修复：提取 token 在 data.data 下
        const token = data?.data?.token
        const username = data?.data?.username
        const role = data?.data?.role
        const lang = data?.data?.preferredLanguage

        if (token) {
          if (this.remember) {
            localStorage.setItem('token', token)
          } else {
            sessionStorage.setItem('token', token)
          }
          localStorage.setItem('username', username || '')
          localStorage.setItem('role', role || '')
          if (lang) {
            localStorage.setItem('lang', lang)
            if (this.$i18n) this.$i18n.locale = lang
          }
          console.log('Token saved:', token.substring(0, 30) + '...')
        } else {
          console.warn('No token found in login response:', data)
        }

        this.$router.push({ name: 'LoginHomePage' })
      } catch (e) {
        console.error('login error:', e?.response?.data || e.message)
        const msg =
          e?.response?.data?.message ||
          this.$t('login.error') ||
          'Login failed.'
        this.snackbar = { show: true, msg }
      } finally {
        this.loading = false
      }
    },
    signinWith(provider) {
      this.snackbar = { show: true, msg: `SSO with ${provider} is not enabled yet.` }
    },
    goReset() {
      this.snackbar = { show: true, msg: 'Please contact support to reset password.' }
    }
  }
}
</script>


<style scoped>
.login-wrap {
  min-height: 100vh;
  background: linear-gradient(180deg, #f6f9fc 0%, #ffffff 100%);
}
.elevated-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(20, 30, 50, 0.08);
}

/* 品牌抬头与注册页保持一致风格 */
.hero {
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  color: #fff;
  padding: 20px 24px;
}
.brand-row {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 64px;
}
.brand-logo .v-image {
  border-radius: 8px;
}
.brand-text { min-width: 0; }
.brand-name {
  font-weight: 700;
  font-size: 20px;
  line-height: 1.2;
  letter-spacing: 0.2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.brand-tagline {
  opacity: 0.92;
  font-size: 13px;
  line-height: 1.2;
  margin-top: 2px;
}

/* 分隔符 */
.divider {
  position: relative;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
}
.divider::before,
.divider::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 36%;
  height: 1px;
  background: rgba(0, 0, 0, 0.12);
}
.divider::before { left: 0; }
.divider::after { right: 0; }
.divider > span {
  padding: 0 8px;
  font-size: 12px;
}

/* 提示色 */
.caption.warning { color: #ef4444; }

.hint { color: rgba(0, 0, 0, 0.6); }

@media (max-width: 400px) {
  .brand-name { font-size: 18px; }
}
</style>
