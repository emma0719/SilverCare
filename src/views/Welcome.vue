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

    <!-- Hero 区域 -->
    <v-container fluid class="py-16 hero-section">
      <v-row align="center">
        <!-- 左侧文字 -->
        <v-col cols="12" md="6" class="text-left">
          <h1 class="hero-heading mb-4">
            Smart Care<br> Made Simple
          </h1>
          <p class="hero-subheading mb-6">
            Track health data, set medication reminders, and manage daily schedules with ease.
          </p>
        </v-col>

        <!-- 右侧按钮 -->
        <v-col cols="12" md="6" class="d-flex justify-end align-center hero-link">
          <v-btn color="primary" size="large" class="mr-4" @click="goToLogin">
            Login
          </v-btn>
          <router-link
            to="/setup"
            class="white--text text-decoration-none font-weight-medium hero-link"
          >
            Create Account
          </router-link>
        </v-col>
      </v-row>
    </v-container>

    <!-- 服务卡片区 -->
    <v-container fluid class="py-12 service-section">
      <v-row>
        <v-col
          cols="12"
          sm="6"
          md="3"
          v-for="service in services"
          :key="service.title"
        >
          <v-card class="pa-6 text-center service-card" elevation="4">
            <v-icon size="40" color="primary">{{ service.icon }}</v-icon>
            <h3 class="text-h6 font-weight-bold mt-4">{{ service.title }}</h3>
            <p class="text-body-2 mt-2">{{ service.desc }}</p>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- Footer -->
    <v-footer color="primary" dark class="pa-6 footer-fixed">
      <v-row class="text-center" no-gutters>
        <v-col cols="12" md="4">
          Reach us: 1-888-205-0861 | M–F 8am–6pm
        </v-col>
        <v-col cols="12" md="4">We care about your feelings.</v-col>
        <v-col cols="12" md="4">
          Developer Instagram
          <a
            href="https://www.instagram.com/emmaaayh_/"
            class="text-white text--underline"
          >
            Emma Lee
          </a>
        </v-col>
      </v-row>
    </v-footer>
  </v-app>
</template>

<script>
export default {
  name: "WelcomePage",
  data() {
    return {
      drawer: false,
      services: [
        { icon: "mdi-pill", title: "Medication Tracking", desc: "Never miss a dose with smart reminders." },
        { icon: "mdi-heart-pulse", title: "Health Monitoring", desc: "Keep track of vital signs and wellness data." },
        { icon: "mdi-calendar-check", title: "Schedule Management", desc: "Stay organized with daily routines and appointments." },
        { icon: "mdi-message-text", title: "SMS Alerts", desc: "Send reminders directly to caregivers or patients." },
      ],
    };
  },
  methods: {
    goToLogin() {
      this.$router.push({ name: "Login" }); // 跳转到 LoginView
    },
  },
};
</script>

<style scoped>
/* 样式保持不变 */
.v-application {
  background: url("@/assets/welcomebg.jpg") center center / cover no-repeat !important;
}
.hero-heading {
  font-family: 'Archivo Black', sans-serif;
  font-size: 4rem;
  font-weight: 400;
  color: #ffffff;
  margin-left: auto;
}
.hero-subheading {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.5rem;
  font-weight: 400;
  color: #f6f6f6;
  max-width: 600px;
}
.hero-link {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1rem;
  margin-top: 12px;
  margin-right: auto;
}
.footer-fixed {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
}
.service-section {
  background: transparent !important;
  margin-top: 200px;
}
.service-card {
  background-color: rgba(255, 255, 255, 0.85);
  transition: background-color 0.3s ease;
}
.service-card:hover {
  background-color: rgba(255, 255, 255, 0.95);
}
.text--underline {
  text-decoration: underline;
}
.hero-title {
  font-family: 'Archivo Black', sans-serif;
  font-weight: 400;   /* Archivo Black 只有 Regular 400 粗度 */
  font-size: 30px;
  letter-spacing: 4px;
  text-transform: uppercase;
  color: white;
  text-shadow: 1px 1px 6px rgba(0, 0, 0, 0.3);
}
</style>
