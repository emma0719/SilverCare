import Vue from "vue";
import VueRouter from "vue-router";
import WelcomeView from "../views/Welcome.vue";
import SetupView from '@/views/SetupView.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/reminder',
    name: 'ReminderForm',
    component: () => import('@/views/ReminderFormView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/app',
    name: 'LoginHomePage',
    component: () => import('@/views/LoginHomePageView.vue')
  },
  {
    path: "/",
    name: "welcomepage",
    component: WelcomeView,
  },
  {
    path: "/about",
    name: "about",
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
    component: () => import("../views/SuccessView.vue"),
  },
  {
    path: '/setup',
    name: 'Setup',
    component: SetupView
  },
  {
    path: '/vitals',
    name: 'VitalSigns',
    component: () => import('@/views/VitalSignsView.vue')
  },
  {
  path: '/care-recipient',
  name: 'CareRecipient',
  component: () => import('@/views/CareRecipientView.vue')
},
{ path: '/profile', 
  name: 'PersonalProfile',
   component: () => import('@/views/PersonalProfileView.vue') }


];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
