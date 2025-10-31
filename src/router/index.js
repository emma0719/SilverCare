import Vue from "vue";
import VueRouter from "vue-router";
import WelcomeView from "@/views/Welcome.vue";
import SetupView from "@/views/SetupView.vue"; // 注册页真实文件

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "welcomepage",
    component: WelcomeView,
  },
  {
    path: "/login",
    name: "Login",
    component: () =>
      import(/* webpackChunkName: "login" */ "@/views/LoginView.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: SetupView, // 对外叫 Register，实际用 SetupView
  },
  {
    path: "/setup",
    redirect: { name: "Register" }, // 老路径兼容
  },
  {
    path: "/app",
    name: "LoginHomePage",
    component: () =>
      import(/* webpackChunkName: "home" */ "@/views/LoginHomePageView.vue"),
  },
  {
    path: "/reminder",
    name: "ReminderForm",
    component: () =>
      import(/* webpackChunkName: "reminder" */ "@/views/ReminderFormView.vue"),
  },
  {
    path: "/vitals",
    name: "VitalSigns",
    component: () =>
      import(/* webpackChunkName: "vitals" */ "@/views/VitalSignsView.vue"),
  },
  {
    path: "/care-recipient",
    name: "CareRecipient",
    component: () =>
      import(
        /* webpackChunkName: "care-recipient" */ "@/views/CareRecipientView.vue"
      ),
  },
  {
    path: "/profile",
    name: "PersonalProfile",
    component: () =>
      import(
        /* webpackChunkName: "profile" */ "@/views/PersonalProfileView.vue"
      ),
  },
  {
    path: "/about",
    name: "about",
    component: () =>
      import(/* webpackChunkName: "about" */ "@/views/AboutView.vue"),
  },
  {
    path: "/feedback",
    name: "feedback",
    component: () =>
      import(/* webpackChunkName: "feedback" */ "@/views/Feedback.vue"),
  },
  {
    path: "/submit-success",
    name: "SubmitSuccess",
    component: () =>
      import(
        /* webpackChunkName: "success" */ "@/views/SuccessView.vue"
      ),
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
  scrollBehavior() {
    return { x: 0, y: 0 };
  },
});

export default router;
