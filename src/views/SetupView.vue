<template>
  <v-container class="pa-10" style="max-width: 500px;">
    <h2 class="text-center mb-6">Please complete the following infos</h2>
    <v-form @submit.prevent="submitSetup">
      <v-text-field v-model="form.username" label="Username" required />
      <v-text-field v-model="form.email" label="Email" type="email" required />
      <v-text-field v-model="form.password" label="Password" type="password" required />
      <v-text-field v-model="form.confirmPassword" label="Confirm Password" type="password" required />

      <v-select
        v-model="form.role"
        :items="['USER', 'CAREGIVER', 'ADMIN']"  
        label="Role"
        required
      />

      <v-row class="mt-4" justify="space-between">
        <v-col cols="6">
          <v-btn type="submit" color="primary" block :disabled="loading">
            {{ loading ? 'Processing...' : 'Complete Setup' }}
          </v-btn>
        </v-col>

        <v-col cols="6">
          <v-btn color="grey lighten-1" text block @click="$router.push('/')">
            Back to Home
          </v-btn>
        </v-col>
      </v-row>
    </v-form>
  </v-container>
</template>

<script>
import api from "@/service/api"; // axios 封装实例

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
        role: ""
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
        const res = await api.post("/auth/register", {
          username: this.form.username,
          email: this.form.email,
          password: this.form.password,
          role: this.form.role
        });

        console.log("Register success:", res.data);
        // 注册成功后跳转到 login
        this.$router.push("/login");
      } catch (err) {
        if (err.response) {
          console.error("Register failed:", err.response.data);
        } else {
          console.error("Register failed:", err.message);
        }
        alert("Registration failed, please try again.");
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
