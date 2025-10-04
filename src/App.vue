<template>
  <v-app>
    <!-- 顶部导航 -->
    <v-app-bar
      color="primary"
      dark
      flat
      height="64"
      app
    >
      <v-toolbar-title class="hero-title">{{ $t("appTitle") }}</v-toolbar-title>
      <v-spacer></v-spacer>

      <!-- 语言切换 -->
      <v-select
        v-model="$i18n.locale"
        :items="[
          { text: 'English', value: 'en' },
          { text: '中文', value: 'zh' },
          { text: 'Tiếng Việt', value: 'vi' },
          { text: 'Español', value: 'es' }
        ]"
        dense
        outlined
        hide-details
        style="max-width: 150px"
        @change="onLangChange"
      ></v-select>
    </v-app-bar>

    <!-- 页面内容 -->
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: "App",
  methods: {
    onLangChange(lang) {
      localStorage.setItem("lang", lang);
    }
  }
};
</script>

<style>
.hero-title {
  font-family: 'Archivo Black', sans-serif;
  font-weight: 400;
  font-size: 30px;
  letter-spacing: 4px;
  text-transform: uppercase;
  color: white;
  text-shadow: 1px 1px 6px rgba(0, 0, 0, 0.3);
}

/* 去掉 v-main 自动留白 */
.v-main {
  padding-top: 0 !important;
}
</style>
