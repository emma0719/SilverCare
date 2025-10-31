// src/main.js
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'

// 1️⃣ 引入多语言（路径改为 /i18n/）
import VueI18n from 'vue-i18n'
import en from './i18n/en.json'
import zh from './i18n/zh.json'
import vi from './i18n/vi.json'
import es from './i18n/es.json'

Vue.use(VueI18n)

// 2️⃣ 注册全局组件
import BackToHomeButton from '@/components/BackToHomeButton.vue'
Vue.component('BackToHomeButton', BackToHomeButton)

Vue.config.productionTip = false

// 3️⃣ 创建 i18n 实例
const messages = { en, zh, vi, es }
const savedLang = localStorage.getItem('lang')
const locale = savedLang && messages[savedLang] ? savedLang : 'en'

const i18n = new VueI18n({
  locale,
  fallbackLocale: 'en',
  messages
})

// 4️⃣ 挂载 Vue 实例
new Vue({
  router,
  vuetify,
  i18n,
  render: h => h(App)
}).$mount('#app')
