<template>
  <div class="back-wrap">
    <v-btn color="primary" outlined large @click="$router.push('/app')">
      <v-icon left>mdi-arrow-left</v-icon>
      Back to Home
    </v-btn>
  </div>
</template>

<script>
export default {
  name: "BackToHomeButton",
};
</script>

<style scoped>
.back-wrap {
  display: flex;
  justify-content: center;
  padding: 16px 0 20px;
  border-top: 1px solid rgba(0,0,0,0.06);
}
</style>
