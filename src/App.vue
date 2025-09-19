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
      <v-toolbar-title class="hero-title">SilverCare</v-toolbar-title>
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
