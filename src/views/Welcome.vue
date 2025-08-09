<template>
  <div class="welcome-page">
    <!-- top bar-->
 <v-app-bar
      color="primary"
      dark
      flat
      height="50"
      class="custom-app-bar"
      app
      clipped-left
    >
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-toolbar-title class="hero-title">SilverCare</v-toolbar-title>
    </v-app-bar>


    <!-- main layout -->
    <v-container fluid class="fill-height">
      <v-row class="fill-height">
        <!-- function btn: 4 1 feedback -->
        <v-col
          cols="12"
          md="6"
          class="d-flex flex-column justify-center align-center"
        >
          <v-container class="pa-0" style="max-width: 400px">
            <v-row dense>
              <v-col cols="6" class="d-flex justify-center mb-4">
                <v-tooltip bottom>
                  <template #activator="{ on, attrs }">
                    <v-btn
                      v-bind="attrs"
                      v-on="on"
                      color="blue-lighten-5"
                      style="color: #0d47a1"
                      class="font-weight-bold"
                      width="160"
                      height="160"
                    >
                      Health<br />Monitor
                    </v-btn>
                  </template>
                  <span
                    >Record and view health data like blood pressure or heart
                    rate.</span
                  >
                </v-tooltip>
              </v-col>

              <v-col cols="6" class="d-flex justify-center mb-4">
                <v-tooltip bottom>
                  <template #activator="{ on, attrs }">
                    <v-btn
                      v-bind="attrs"
                      v-on="on"
                      color="blue-lighten-5"
                      style="color: #0d47a1"
                      class="font-weight-bold"
                      width="160"
                      height="160"
                    >
                      Medicine<br />Reminder
                    </v-btn>
                  </template>
                  <span>Set reminders for medications and track doses.</span>
                </v-tooltip>
              </v-col>

              <v-col cols="6" class="d-flex justify-center">
                <v-tooltip bottom>
                  <template #activator="{ on, attrs }">
                    <v-btn
                      v-bind="attrs"
                      v-on="on"
                      color="blue-lighten-5"
                      style="color: #0d47a1"
                      class="font-weight-bold"
                      width="160"
                      height="160"
                    >
                      Daily<br />Schedule
                    </v-btn>
                  </template>
                  <span>View daily routines and upcoming appointments.</span>
                </v-tooltip>
              </v-col>

              <v-col cols="6" class="d-flex justify-center">
                <v-btn
                  v-bind="attrs"
                  v-on="on"
                  color="blue-lighten-5"
                  style="color: #0d47a1"
                  class="font-weight-bold"
                  width="160"
                  height="160"
                  @click="$router.push('/feedback')"
                >
                  Feedback
                </v-btn>
              </v-col>
            </v-row>
          </v-container>
        </v-col>

        <!-- 右侧登录卡片 -->
        <v-col cols="12" md="6" class="d-flex align-center justify-center">
          <v-card class="pa-6" max-width="360" elevation="8" rounded>
            <v-img
              class="mx-auto mb-6"
              max-width="228"
              src="@/assets/welcomelogo-removebg-preview.png"
              contain
            />

            <div class="text-subtitle-1 mb-2">Account</div>

            <v-text-field
              v-model="email"
              dense
              label="Email address"
              prepend-inner-icon="mdi-email-outline"
              outlined
            />

            <div
              class="text-subtitle-1 d-flex align-center justify-space-between mt-4"
            >
              Password
              <a class="text-caption text-decoration-none blue--text" href="#">
                Forgot password?
              </a>
            </div>

            <v-text-field
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              dense
              label="Enter your password"
              prepend-inner-icon="mdi-lock-outline"
              :append-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append="showPassword = !showPassword"
              outlined
            />

            <v-alert class="mb-6" dense type="warning" text>
              After 3 failed login attempts, your account will be locked for 3
              hours.
            </v-alert>

            <v-btn color="primary" block large class="mb-4" @click="login">
              Log In
            </v-btn>

            <div class="text-center">
              <router-link to="/setup" class="blue--text text-decoration-none">
                Sign up now <v-icon right>mdi-chevron-right</v-icon>
              </router-link>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    
    <v-footer color="white" padless>
      <v-row class="text-center text-white pa-6" no-gutters>
        <v-col cols="12" md="4">
          Reach us by phone: 1-888-205-0861 | M–F 8am–6pm
        </v-col>

        <v-col cols="12" md="4"> We care about your feelings. </v-col>

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
  </div>
</template>

<script>
export default {
  name: "WelcomePage",
  data() {
    return {
      email: "",
      password: "",
      showPassword: false,
    };
  },
  methods: {
    login() {
     this.$router.push({name: 'LoginHomePage'})

    },
  },
};
</script>

<style scoped>
.welcome-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

/* 背景图层 */
.welcome-page::before {
  content: "";
  position: absolute;
  inset: 0;
  background: url("@/assets/welcomebg.jpg") center center / cover no-repeat;
  opacity: 0.5;
  z-index: 0;
}

/* 内容位于背景图之上 */
.welcome-page > * {
  position: relative;
  z-index: 1;
}

/* 页脚下划线链接样式 */
.text--underline {
  text-decoration: underline;
}

.hero-title {
  font-size: 20px;
  font-family: 'Sora', sans-serif;
  font-weight: 400;
  color: white;
  letter-spacing: 6px;
  text-transform: uppercase;
  text-shadow: 1px 1px 6px rgba(0, 0, 0, 0.3);
}
</style>
