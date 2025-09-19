<template>
  <v-app>
    <!-- 顶部 App Bar -->
    <v-app-bar app color="primary" dark flat height="64">
      <v-toolbar-title class="hero-title">SILVERCARE</v-toolbar-title>
    </v-app-bar>

    <!-- 主体内容 -->
    <v-main class="pt-12"> <!-- 给 App Bar 留空间 -->
      <v-container class="dashboard-container">
        <!-- 欢迎语 -->
        <h2 class="mb-10 welcome-text">Welcome, {{ username }}</h2>

        <!-- Card Grid -->
        <v-row dense>
          <v-col cols="12" sm="6" md="4" v-for="card in cards" :key="card.title">
            <v-card
              class="pa-8 text-center flat-card"
              elevation="0"
              @click="onCardClick(card)"
            >
              <v-icon size="40" color="primary" class="mb-4">{{ card.icon }}</v-icon>
              <h3 class="text-h6 font-weight-bold">{{ card.title }}</h3>
              <p class="text-body-2 grey--text mt-2">{{ card.subtitle }}</p>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: "DashboardView",
  data() {
    return {
      username: localStorage.getItem("username") || "User",
      cards: [
        { title: "Your care-recipient", subtitle: "Click to modify care recipient", icon: "mdi-account-multiple-outline", route: "/care-recipient" },
        { title: "Vital data", subtitle: "Click to check vital data", icon: "mdi-heart-pulse", route: "/vital-data" },
        { title: "Reminder", subtitle: "Click to modify medical reminders", icon: "mdi-bell-outline", route: "reminder" }, // 用标记提醒特殊处理
        { title: "Personal profile", subtitle: "Modify your personal info", icon: "mdi-account-outline", route: "/profile" },
        { title: "Setting", subtitle: "Adjust preferences", icon: "mdi-cog-outline", route: "/settings" },
        { title: "Log out", subtitle: "Exit and return to home page", icon: "mdi-logout", route: "logout" },
      ],
    };
  },
  methods: {
    onCardClick(card) {
      if (card.route === "logout") {
        localStorage.clear();
        this.$router.push("/");
      } else if (card.route === "reminder") {
        // ✅ 特殊处理 Reminder 卡片
        this.$router.push({ name: "ReminderForm" });
      } else {
        this.$router.push(card.route);
      }
    },
  },
};
</script>

<style scoped>
.hero-title {
  font-family: 'Archivo Black', sans-serif;
  font-weight: 400;
  font-size: 24px;
  letter-spacing: 3px;
}

.welcome-text {
  font-family: 'Archivo Black', sans-serif;
  font-size: 2rem;
  color: #333;
}

/* 扁平化卡片 */
.flat-card {
  border-radius: 16px;
  background-color: #fafafa;
  transition: all 0.2s ease;
  cursor: pointer;
}
.flat-card:hover {
  background-color: #f0f4ff; /* hover 时轻微高亮 */
  border: 1px solid #1976d2;
}
.dashboard-container {
  padding-top: 150px; /* 调整数值决定下移多少 */
}
</style>
