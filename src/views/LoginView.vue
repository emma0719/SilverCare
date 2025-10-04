<template>
  <v-container class="pa-10" style="max-width: 500px;">
    <h2 class="text-center mb-6">Login to your account</h2>

    <v-alert
      v-if="error"
      type="error"
      dense
      class="mb-4"
    >
      {{ error }}
    </v-alert>

    <v-form @submit.prevent="submitLogin">
      <v-text-field
        v-model="form.username"
        label="Username"
        autocomplete="username"
        required
      />
      <v-text-field
        v-model="form.password"
        label="Password"
        type="password"
        autocomplete="current-password"
        required
      />

      <v-row class="mt-4">
        <v-col cols="12">
          <v-btn
            type="submit"
            color="primary"
            block
            :loading="loading"
            :disabled="loading || !form.username || !form.password"
          >
            Login
          </v-btn>
        </v-col>
      </v-row>
    </v-form>
  </v-container>
</template>

<script>
import api from "@/service/api"; // ✅ 注意是 service（没有 s）

export default {
  name: "LoginView",
  data() {
    return {
      loading: false,
      error: "",
      form: {
        username: "",
        password: ""
      }
    };
  },
  methods: {
    async submitLogin() {
      this.loading = true;
      this.error = "";
      try {
        const { data } = await api.post("/auth/login", {
          username: this.form.username.trim(),
          password: this.form.password
        });

        // 保存登录信息
        localStorage.setItem("token", data.token);
        localStorage.setItem("username", data.username);
        localStorage.setItem("role", data.role);

        // 跳转到登录后的首页
        this.$router.push({ name: "LoginHomePage" });
      } catch (e) {
        console.error("login error:", e?.response?.data || e.message);
        this.error = "Login failed. Please check your username and password.";
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
