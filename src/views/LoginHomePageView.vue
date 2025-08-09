<template>
  <v-app>
    <!-- 顶部 App Bar -->
    <v-app-bar
      color="primary"
      dark
      flat
      height="50"
      class="custom-app-bar"
      app
    >
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-toolbar-title class="hero-title">SilverCare</v-toolbar-title>
    </v-app-bar>

    <!-- 左侧抽屉 -->
    <v-navigation-drawer
      v-model="drawer"
      app
      width="256"
      temporary
      :scrim="true"
    >
      <v-list dense>
        <v-list-item-group v-model="group" color="primary">
          <v-list-item
            v-for="it in items"
            :key="it.value"
            @click="onSelect(it)"
          >
            <v-list-item-icon>
              <v-icon>{{ it.prependIcon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ it.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>

      <!-- 底部 Logout 按钮 -->
      <template v-slot:append>
        <v-divider></v-divider>
        <v-list>
          <v-list-item @click="logout">
            <v-list-item-icon>
              <v-icon color="red">mdi-logout</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title class="red--text">Logout</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </template>
    </v-navigation-drawer>
  </v-app>
</template>

<script>
export default {
  name: 'LoginHomePageView',
  data () {
    return {
      drawer: false,     // 默认关闭
      group: 'profile',
      items: [
        { title: 'Profile', value: 'profile', prependIcon: 'mdi-account-circle' },
        { title: 'Enter Vitals', value: 'enterVitals', prependIcon: 'mdi-heart-plus' },
        { title: 'Vitals Report', value: 'vitalsReport', prependIcon: 'mdi-chart-line' },
        { title: 'Medication Reminders', value: 'medReminders', prependIcon: 'mdi-pill-clock' },
      ],
    }
  },
  methods: {
    onSelect (it) {
      this.group = it.value
      this.drawer = false // 选中后收起抽屉
    },
    logout () {
      this.drawer = false
      this.$router.push('/') // 或者 this.$router.push({ name: 'WelcomePage' })
    }
  }
}
</script>

<style scoped>
.custom-app-bar { padding-left: 8px; }
.hero-title {
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 4px;
}
</style>
