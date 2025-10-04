<template>
  <v-container class="pa-10" style="max-width: 500px;">
    <h2 class="text-center mb-6">{{ $t("register.title") }}</h2>
    <v-form @submit.prevent="submitSetup">
      <v-text-field v-model="form.username" :label="$t('register.username')" required />
      <v-text-field v-model="form.email" :label="$t('register.email')" type="email" required />
      <v-text-field v-model="form.password" :label="$t('register.password')" type="password" required />
      <v-text-field v-model="form.confirmPassword" :label="$t('register.confirmPassword')" type="password" required />

      <v-select
        v-model="form.role"
        :items="['USER', 'CAREGIVER', 'ADMIN']"
        :label="$t('register.role')"
        required
      />

      <!-- 新增：语言选择 -->
      <v-select
        v-model="form.preferredLanguage"
        :items="[
          { text: 'English', value: 'en' },
          { text: '中文', value: 'zh' },
          { text: 'Tiếng Việt', value: 'vi' },
          { text: 'Español', value: 'es' }
        ]"
        label="Preferred Language"
        required
      />

      <v-row class="mt-4" justify="space-between">
        <v-col cols="6">
          <v-btn type="submit" color="primary" block :disabled="loading">
            {{ loading ? 'Processing...' : $t("register.button") }}
          </v-btn>
        </v-col>

        <v-col cols="6">
          <v-btn color="grey lighten-1" text block @click="$router.push('/')">
            {{ $t("register.back") }}
          </v-btn>
        </v-col>
      </v-row>
    </v-form>
  </v-container>
</template>

<script>
import api from "@/service/api";

export default {
  name: "RegisterView",
  data() {
    return {
      loading: false,
      form: {
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        role: "",
        preferredLanguage: "en" // 默认英文
      }
    };
  },
  methods: {
    async submitSetup() {
      if (this.form.password !== this.form.confirmPassword) {
        alert("Passwords do not match!");
        return;
      }

      this.loading = true;
      try {
        const res = await api.post("/auth/register", this.form);
        console.log("Register success:", res.data);
        this.$router.push("/login");
      } catch (err) {
        console.error("Register failed:", err.response?.data || err.message);
        alert("Registration failed, please try again.");
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
