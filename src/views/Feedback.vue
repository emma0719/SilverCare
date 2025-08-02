<template>
  <v-app>
    <!-- Header 顶部栏 -->
    <v-app-bar color="primary" dark dense flat>
      <v-btn icon @click="$router.push('/')">
        <v-icon>mdi-home</v-icon>
      </v-btn>
      <v-toolbar-title class="font-weight-bold">Feedback</v-toolbar-title>
    </v-app-bar>

    <!-- 内容区域 -->
    <v-container class="pa-4" max-width="600px">
      <v-card>
        <v-card-title class="headline">We Value Your Feedback</v-card-title>

        <v-card-text>
          <v-form ref="formRef">
            <v-text-field
              label="Name"
              v-model="form.name"
              required
            ></v-text-field>

            <v-text-field
              label="Email"
              v-model="form.email"
              type="email"
              required
            ></v-text-field>

            <v-select
              label="Feedback Type"
              :items="feedbackTypes"
              v-model="form.type"
              required
            ></v-select>

            <v-textarea
              label="Your Message"
              v-model="form.message"
              auto-grow
              required
            ></v-textarea>

            <v-rating
              v-model="form.rating"
              background-color="grey lighten-1"
              color="amber"
              large
              length="5"
              class="mt-4"
            ></v-rating>

            <v-checkbox
              v-model="form.contact"
              label="I am open to being contacted regarding this feedback"
              class="mt-2"
            ></v-checkbox>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="submitFeedbackbtn">Submit</v-btn>
        </v-card-actions>
      </v-card>
    </v-container>

    <!-- Footer -->
    <v-footer color="primary" padless class="mt-8">
      <v-row class="text-center white--text pa-6" no-gutters>
        <v-col cols="12" md="4">
          Reach us by phone: 1-888-205-0861 | M–F 8am–6pm
        </v-col>
        <v-col cols="12" md="4">We care about your feelings.</v-col>

        <v-col cols="12" md="4">
          Developer Instagram
          <a
            href="https://www.instagram.com/emmaaayh_/"
            class="text-white text--underline"
            target="_blank"
            rel="noopener"
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
  name: "FeedbackForm",
  data() {
    return {
      form: {
        name: "",
        email: "",
        type: "",
        message: "",
        rating: 0,
        contact: false,
      },
      feedbackTypes: ["Service", "Website", "Health Reminder", "Other"],
    };
  },
  methods: {
    submitFeedback() {
      if (
        !this.form.name ||
        !this.form.email ||
        !this.form.type ||
        !this.form.message
      ) {
        this.$toast?.error("Please complete all required fields.");
        return;
      }

      console.log("Feedback submitted:", this.form);
      this.$toast?.success("Thank you for your feedback!");
      this.resetForm();
    },
    resetForm() {
      this.form = {
        name: "",
        email: "",
        type: "",
        message: "",
        rating: 0,
        contact: false,
      };
      this.$refs.formRef?.reset();
    },
    async submitFeedbackbtn(){
      try{
        // await this.sendForm();
        console.log('即將跳轉成功頁面');
        this.$router.push('/submit-success');
      } catch(error){
        console.error('Submission Fail',error);
      }
    }
  },
};
</script>

<style scoped>
.v-card-title {
  font-weight: bold;
  font-size: 1.4rem;
}
</style>
