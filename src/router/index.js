import Vue from "vue";
import VueRouter from "vue-router";
import WelcomeView from "../views/Welcome.vue";
import SetupView from '@/views/SetupView.vue';



Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "welcomepage",
    component: WelcomeView,
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
  {
    path: "/feedback",
    name: "feedback",
    component: () => import("../views/Feedback.vue"),
  },
  {
    path: '/submit-success',
    name: 'SubmitSuccess',
    component:  () => import("../views/SuccessView.vue"),

  },
  {
    path: '/setup',
    name: 'Setup',
    component: SetupView
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
