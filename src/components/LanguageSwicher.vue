<template>
  <v-menu offset-y>
    <template v-slot:activator="{ on, attrs }">
      <v-btn v-on="on" v-bind="attrs" icon>
        <v-icon>mdi-translate</v-icon>
      </v-btn>
    </template>

    <v-list>
      <v-list-item
        v-for="(label, code) in languages"
        :key="code"
        @click="changeLang(code)"
      >
        <v-list-item-title>{{ label }}</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  name: 'LanguageSwitcher',
  data() {
    return {
      languages: {
        en: 'English',
        zh: '中文',
        vi: 'Tiếng Việt',
        es: 'Español'
      }
    }
  },
  methods: {
    changeLang(code) {
      this.$i18n.locale = code
      localStorage.setItem('lang', code)
      window.location.reload()
    }
  }
}
</script>
