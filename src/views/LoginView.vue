<template>
  <v-container class="pa-10" style="max-width: 500px;">
    <h2 class="text-center mb-6">Login to your account</h2>
    <v-form @submit.prevent="submitLogin">
      <v-text-field
        v-model="form.username"
        label="Username"
        required
      />
      <v-text-field
        v-model="form.password"
        label="Password"
        type="password"
        required
      />

      <v-row class="mt-4">
        <v-col cols="12">
          <v-btn type="submit" color="primary" block :disabled="loading">
            {{ loading ? 'Processing...' : 'Login' }}
          </v-btn>
        </v-col>
      </v-row>
    </v-form>
  </v-container>
</template>

<script>
import api from "@/service/api";

export default {
  name: "LoginView",
  data() {
    return {
      loading: false,
      form: {
        username: "",
        password: ""
      }
    };
  },
  methods: {
    async submitLogin() {
      this.loading = true;
      try {
        const res = await api.post("/auth/login", {
          username: this.form.username,   // ✅ 后端需要 username
          password: this.form.password
        });

        console.log("Login success:", res.data);

        // 保存 token 和用户名
        localStorage.setItem("token", res.data.token);
        localStorage.setItem("username", res.data.username);

        // 跳转到登录后的主页
        this.$router.push({ name: "LoginHomePage" });
      } catch (err) {
        if (err.response) {
          console.error("Login failed:", err.response.data);
        } else {
          console.error("Login failed:", err.message);
        }
        alert("Login failed, please check your username and password.");
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
