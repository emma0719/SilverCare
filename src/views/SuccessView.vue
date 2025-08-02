<template>
  <v-app>
    <v-app-bar flat height="120" class="custom-app-bar">
      <v-toolbar-title class="hero-title">SilverCare</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <v-container class="text-center pa-10">
        <v-icon color="green" size="80">mdi-check-circle</v-icon>
        <h2 class="mt-4">Successful</h2>
        <p>Thank you for your feedback</p>
        <v-btn color="primary" class="mt-6" @click="$router.push('/')">Back to HomePage</v-btn>
      </v-container>
    </v-main>

    <v-footer color="primary" dark>
      <v-col class="text-center">©2025 SilverCare</v-col>
    </v-footer>
  </v-app>
</template>

<script>
export default {
  name: "SuccessView",
};
</script>

<style scoped>
.custom-app-bar {
  background-image: url('@/assets/welcomebg.jpg');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-title {
  font-size: 40px;
  font-family: 'Cinzel', serif;
  font-weight: bold;
  color: white;
  letter-spacing: 2px;
  text-shadow: 2px 2px 6px rgba(0, 0, 0, 0.4);
}
</style>
