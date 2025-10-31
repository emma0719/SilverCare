import { createI18n } from 'vue-i18n'
import en from './en.json'
import zh from './zh.json'
import vi from './vi.json'
import es from './es.json'

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('lang') || 'en', // 默认英语，可自动记忆上次选择
  fallbackLocale: 'en',
  messages: { en, zh, vi, es }
})

export default i18n
