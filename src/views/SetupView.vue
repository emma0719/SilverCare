<template>
  <v-container class="register-wrap py-12">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="10" md="8" lg="5">
        <v-card class="elevated-card">
          <!-- 顶部品牌区 -->
<!-- 顶部品牌区（替换原来的 .hero 内容） -->
<div class="hero">
  <div class="brand-row">
    <v-avatar size="36" class="brand-logo">
      <v-img src="/logo.png" contain></v-img>
    </v-avatar>

    <div class="brand-text">
      <div class="brand-name text-truncate">SilverCare</div>
      <div class="brand-tagline">{{ $t('register.title') }}</div>
    </div>
  </div>
</div>


          <v-card-text class="pt-6 pb-0">
            <v-form ref="formRef" v-model="isValid" @submit.prevent="onSubmit">
              <v-text-field
                v-model.trim="form.username"
                :label="$t('register.username')"
                :rules="[rules.required, rules.min3]"
                prepend-inner-icon="mdi-account"
                dense
                outlined
                clearable
                autocomplete="username"
              />

              <v-text-field
                v-model.trim="form.email"
                :label="$t('register.email')"
                :rules="[rules.required, rules.email]"
                prepend-inner-icon="mdi-email"
                dense
                outlined
                clearable
                autocomplete="email"
                type="email"
              />

              <v-text-field
                v-model="form.password"
                :type="showPwd ? 'text' : 'password'"
                :label="$t('register.password')"
                :rules="[rules.required, rules.pwd]"
                prepend-inner-icon="mdi-lock"
                :append-icon="showPwd ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append="showPwd = !showPwd"
                dense
                outlined
                autocomplete="new-password"
              />
              <v-progress-linear
                :value="passwordStrength.value"
                :stream="false"
                height="6"
                class="mb-2"
              />
              <div class="caption mb-4">{{ passwordStrength.label }}</div>

              <v-text-field
                v-model="form.confirmPassword"
                :type="showConfirm ? 'text' : 'password'"
                :label="$t('register.confirmPassword')"
                :rules="[rules.required, confirmRule]"
                prepend-inner-icon="mdi-lock-check"
                :append-icon="showConfirm ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append="showConfirm = !showConfirm"
                dense
                outlined
                autocomplete="new-password"
              />

              <!-- 角色：chips 选择 -->
              <v-select
                v-model="form.role"
                :items="roleItems"
                item-text="text"
                item-value="value"
                :label="$t('register.role')"
                :rules="[rules.required]"
                dense
                outlined
                chips
              >
                <template v-slot:selection="{ item }">
                  <v-chip small>{{ item.text }}</v-chip>
                </template>
              </v-select>

              <!-- 语言 -->
              <v-select
                v-model="form.preferredLanguage"
                :items="langItems"
                item-text="text"
                item-value="value"
                label="Preferred Language"
                :rules="[rules.required]"
                dense
                outlined
              >
                <template v-slot:item="{ item }">
                  <div class="d-flex align-center">
                    <span class="mr-2">{{ item.flag }}</span>
                    <span>{{ item.text }}</span>
                  </div>
                </template>
                <template v-slot:selection="{ item }">
                  <div class="d-flex align-center">
                    <span class="mr-2">{{ item.flag }}</span>
                    <span>{{ item.text }}</span>
                  </div>
                </template>
              </v-select>

              <v-checkbox
                v-model="agree"
                :rules="[v => v || 'Please accept terms']"
                :label="$t('I agree to the Terms of Service and Privacy Policy')"
                class="mt-1"
                dense
              />

              <v-row class="mt-2" dense>
                <v-col cols="12" sm="6">
                  <v-btn
                    type="submit"
                    color="primary"
                    block
                    :loading="loading"
                    :disabled="!isValid || loading"
                  >
                    {{ $t('register.button') }}
                  </v-btn>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-btn text block @click="$router.push('/login')">
                    {{ $t('register.back') }}
                  </v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>

          <v-divider class="mt-4" />

          <v-card-actions class="justify-center">
            <div class="hint">
              Already have an account?
              <v-btn text small color="primary" @click="$router.push('/login')">Sign In</v-btn>
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
  name: 'RegisterView',
  data() {
    return {
      loading: false,
      isValid: false,
      showPwd: false,
      showConfirm: false,
      agree: true,
      snackbar: { show: false, msg: '' },
      form: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: '',
        preferredLanguage: 'en'
      },
      roleItems: [
        { text: 'Family', value: 'FAMILY' },
        { text: 'Caregiver', value: 'CAREGIVER' },
        { text: 'Admin', value: 'ADMIN' }
      ],
      langItems: [
        { text: 'English', value: 'en', flag: '🇺🇸' },
        { text: '中文', value: 'zh', flag: '🇨🇳' },
        { text: 'Tiếng Việt', value: 'vi', flag: '🇻🇳' },
        { text: 'Español', value: 'es', flag: '🇪🇸' }
      ],
      rules: {
        required: v => !!v || 'Required',
        min3: v => (v && v.length >= 3) || 'At least 3 characters',
        email: v => /.+@.+\..+/.test(v) || 'Invalid email',
        pwd: v =>
          /^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(v) ||
          'Min 8 chars, include letters and numbers'
      }
    }
  },
  computed: {
    confirmRule() {
      return v => v === this.form.password || 'Passwords do not match'
    },
    passwordStrength() {
      const p = this.form.password || ''
      let score = 0
      if (p.length >= 8) score += 30
      if (/[A-Z]/.test(p) && /[a-z]/.test(p)) score += 25
      if (/\d/.test(p)) score += 25
      if (/[^A-Za-z0-9]/.test(p)) score += 20
      const label =
        score >= 80 ? 'Strong' : score >= 50 ? 'Medium' : p ? 'Weak' : ''
      return { value: Math.min(score, 100), label }
    }
  },
  methods: {
    async onSubmit() {
      const ok = await this.$refs.formRef.validate()
      if (!ok) return
      if (!this.agree) {
        this.snackbar = { show: true, msg: 'Please accept the terms' }
        return
      }
      this.loading = true
      try {
        const payload = { ...this.form }
        await api.post('/auth/register', payload)

        // 保存语言偏好并切换
        localStorage.setItem('lang', payload.preferredLanguage)
        if (this.$i18n) this.$i18n.locale = payload.preferredLanguage

        this.$router.push('/login')
      } catch (err) {
        const msg =
          err?.response?.data?.message ||
          'Registration failed, please try again.'
        this.snackbar = { show: true, msg }
        // eslint-disable-next-line no-console
        console.error('Register failed:', err?.response?.data || err?.message)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register-wrap {
  min-height: 100vh;
  background: linear-gradient(180deg, #f6f9fc 0%, #ffffff 100%);
}
.elevated-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(20, 30, 50, 0.08);
}
.hero {
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  color: white;
  padding: 20px 24px;
}
.brand {
  display: flex;
  align-items: center;
}
.brand-text .title {
  font-size: 20px;
  line-height: 1.1;
}
.brand-text .subtitle {
  opacity: 0.9;
  font-size: 14px;
}
.caption {
  color: rgba(0, 0, 0, 0.54);
}
.hint {
  color: rgba(0, 0, 0, 0.6);
}
</style>
