import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify' // 如果你项目有
import en from './locales/en.json'
import zh from './locales/zh.json'
import vi from './locales/vi.json'
import es from './locales/es.json'
// ⬇️ 新增：导入并全局注册
import BackToHomeButton from '@/components/BackToHomeButton.vue'
// 若 @ 无法解析，用相对路径： './components/BackToHomeButton.vue'
Vue.component('BackToHomeButton', BackToHomeButton)

Vue.config.productionTip = false

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app')


const i18n = new VueI18n({
  locale: localStorage.getItem('lang') || 'en',
  fallbackLocale: 'en',
  messages: { en, zh, vi, es }
})
