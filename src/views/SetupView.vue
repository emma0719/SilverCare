<template>
  <v-container class="pa-10" style="max-width: 500px;">
    <h2 class="text-center mb-6">Please complete the following infos</h2>
    <v-form @submit.prevent="submitSetup">
      <v-text-field v-model="form.username" label="Username" required></v-text-field>
      <v-text-field v-model="form.email" label="Email" type="email" required></v-text-field>
      <v-text-field v-model="form.password" label="Password" type="password" required></v-text-field>
      <v-text-field v-model="form.confirmPassword" label="Confirm Password" type="password" required></v-text-field>
      <v-select
        v-model="form.role"
        :items="['User', 'Caregiver', 'Admin']"
        label="Role"
        required
      ></v-select>

     <!-- 替换原来的单个按钮 -->
<v-row class="mt-4" justify="space-between">
  <v-col cols="6">
    <v-btn
      type="submit"
      color="primary"
      block
      :disabled="loading"
    >
      {{ loading ? 'Processing...' : 'Complete Setup' }}
    </v-btn>
  </v-col>

  <v-col cols="6">
    <v-btn
      color="grey lighten-1"
      text
      block
      @click="$router.push('/')"
    >
      Back to Home
    </v-btn>
  </v-col>
</v-row>

    </v-form>
  </v-container>
</template>

<script>
export default {
  name: "SetupView",
  data() {
    return {
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
    submitSetup() {
      if (this.form.password !== this.form.confirmPassword) {
        alert("Passwords do not match!");
        return;
      }
      console.log("Setup submitted:", this.form);
      this.$router.push("/login");
    }
  }
};
</script>
