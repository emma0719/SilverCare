<template>
  <v-container class="profile-shell">
    <v-card class="profile-card">

      <!-- 顶部品牌横幅 / Banner -->
      <div class="banner">
        <div class="banner-left">
          <div class="avatar-wrap">
            <v-avatar size="96" class="avatar-ring">
              <img v-if="profile.avatarUrl" :src="profile.avatarUrl" alt="avatar" />
              <v-icon v-else size="96" color="grey lighten-1">mdi-account-circle</v-icon>
            </v-avatar>
            <span class="status-dot" :class="{ online: true }"></span>
          </div>

          <div class="headline">
            <div class="name-line">
              <span class="name">{{ profile.fullName || profile.username }}</span>
              <v-chip small :color="roleColor" text-color="white" class="ml-2">
                {{ (profile.role || '').toUpperCase() }}
              </v-chip>
            </div>
            <div class="muted">
              Member since {{ niceDate(profile.createdAt) }}
            </div>
          </div>
        </div>

        <div class="banner-right">
          <v-btn color="primary" depressed @click="openEdit">
            <v-icon left>mdi-pencil</v-icon> Edit Profile
          </v-btn>
        </div>
      </div>

      <v-divider></v-divider>

      <!-- 加载骨架屏 -->
      <v-skeleton-loader
        v-if="loading"
        type="article, actions"
        class="px-6 py-8"
      />

      <!-- 内容 -->
      <v-card-text v-else class="px-6 py-6">
        <v-row dense>
          <v-col cols="12" md="6">
            <v-card class="section-card">
              <v-card-title class="section-title">
                <v-icon small class="mr-2">mdi-account-outline</v-icon>
                Contact
              </v-card-title>
              <v-divider></v-divider>
              <v-list two-line dense>
                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Email</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.email || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-email-outline</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Phone</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.phoneNumber || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-phone-outline</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Location</v-list-item-title>
                    <v-list-item-subtitle>
                      {{ profile.city ? profile.city + ', ' : '' }}{{ profile.country || '-' }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-map-marker-outline</v-icon></v-list-item-icon>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>

          <v-col cols="12" md="6">
            <v-card class="section-card">
              <v-card-title class="section-title">
                <v-icon small class="mr-2">mdi-tune</v-icon>
                Preferences
              </v-card-title>
              <v-divider></v-divider>
              <v-list two-line dense>
                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Language</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.languagePreference || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-translate</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Units</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.preferredUnits || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-ruler-square</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Theme</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.theme || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-theme-light-dark</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Timezone</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.timezone || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-clock-outline</v-icon></v-list-item-icon>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>
        </v-row>

        <v-row dense class="mt-2">
          <v-col cols="12" md="6">
            <v-card class="section-card">
              <v-card-title class="section-title">
                <v-icon small class="mr-2">mdi-briefcase-outline</v-icon>
                Professional
              </v-card-title>
              <v-divider></v-divider>
              <v-list two-line dense>
                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Organization</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.organization || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-office-building</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Job Title</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.jobTitle || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-badge-account-outline</v-icon></v-list-item-icon>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>

          <v-col cols="12" md="6">
            <v-card class="section-card">
              <v-card-title class="section-title">
                <v-icon small class="mr-2">mdi-shield-outline</v-icon>
                Security & Notifications
              </v-card-title>
              <v-divider></v-divider>
              <v-list two-line dense>
                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Two-Factor Auth</v-list-item-title>
                    <v-list-item-subtitle>{{ profile.twoFactorEnabled ? 'Enabled' : 'Disabled' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon>
                    <v-icon :color="profile.twoFactorEnabled ? 'green' : 'grey'">mdi-shield-lock-outline</v-icon>
                  </v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Notifications</v-list-item-title>
                    <v-list-item-subtitle>
                      Email: {{ yesNo(profile.notifyEmail) }} ·
                      SMS: {{ yesNo(profile.notifySms) }} ·
                      Push: {{ yesNo(profile.notifyPush) }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-bell-outline</v-icon></v-list-item-icon>
                </v-list-item>

                <v-list-item>
                  <v-list-item-content>
                    <v-list-item-title>Last Login</v-list-item-title>
                    <v-list-item-subtitle>{{ niceDateTime(profile.lastLoginAt) || '-' }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-icon><v-icon>mdi-login-variant</v-icon></v-list-item-icon>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>
        </v-row>

        <div class="footer">
          <BackToHomeButton />
        </div>
      </v-card-text>
    </v-card>

    <!-- 编辑弹窗 -->
    <v-dialog v-model="dialog" max-width="760px" eager>
      <v-card>
        <v-card-title class="py-4">
          <v-icon left>mdi-pencil</v-icon> Edit Profile
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text class="pt-4">
          <v-form ref="form" @submit.prevent="save">
            <v-row dense>
              <v-col cols="12" md="6">
                <v-text-field v-model="form.fullName" label="Full Name" outlined dense />
                <v-text-field v-model="form.email" label="Email" outlined dense />
                <v-text-field v-model="form.phoneNumber" label="Phone" outlined dense />
                <v-text-field v-model="form.avatarUrl" label="Avatar URL" outlined dense />
                <v-text-field v-model="form.dateOfBirth" type="date" label="Date of Birth" outlined dense />
                <v-text-field v-model="form.organization" label="Organization" outlined dense />
                <v-text-field v-model="form.jobTitle" label="Job Title" outlined dense />
              </v-col>

              <v-col cols="12" md="6">
                <v-text-field v-model="form.country" label="Country" outlined dense />
                <v-text-field v-model="form.city" label="City" outlined dense />
                <v-text-field v-model="form.timezone" label="Timezone" placeholder="e.g. America/Los_Angeles" outlined dense />

                <v-select v-model="form.languagePreference" :items="langItems" label="Language" outlined dense />
                <v-select v-model="form.preferredUnits" :items="unitItems" label="Units" outlined dense />
                <v-select v-model="form.theme" :items="themeItems" label="Theme" outlined dense />

                <v-switch v-model="form.notifyEmail" label="Email Notifications" class="mt-1" />
                <v-switch v-model="form.notifySms" label="SMS Notifications" class="mt-1" />
                <v-switch v-model="form.notifyPush" label="Push Notifications" class="mt-1" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions class="px-6 py-4">
          <v-spacer />
          <v-btn text @click="dialog=false">Cancel</v-btn>
          <v-btn color="primary" depressed @click="save">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 提示 -->
    <v-snackbar v-model="snackbar.show" :timeout="2500" top right>
      {{ snackbar.text }}
      <v-btn text color="red" @click="snackbar.show=false">Close</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import axios from "axios";
import BackToHomeButton from "@/components/BackToHomeButton.vue";

export default {
  name: "PersonalProfileView",
  components: { BackToHomeButton },
  data() {
    return {
      loading: true,
      profile: {},
      dialog: false,
      form: {},
      snackbar: { show: false, text: "" },
      langItems: ["en", "zh", "es", "vi"],
      unitItems: ["METRIC", "IMPERIAL"],
      themeItems: ["LIGHT", "DARK", "SYSTEM"],
    };
  },
  computed: {
    roleColor() {
      const role = (this.profile.role || "").toUpperCase();
      if (role === "ADMIN") return "deep-purple accent-4";
      if (role === "CAREGIVER") return "teal darken-1";
      return "indigo"; // FAMILY / default
    }
  },
  methods: {
    api() {
      const token = localStorage.getItem("token");
      return axios.create({
        baseURL: "http://localhost:8081",
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
    },
    async load() {
      this.loading = true;
      try {
        const { data } = await this.api().get("/api/users/me");
        this.profile = data || {};
      } catch (e) {
        console.error(e);
        this.toast("⚠️ Failed to load profile");
      } finally {
        this.loading = false;
      }
    },
    openEdit() {
      this.form = { ...this.profile };
      this.dialog = true;
    },
    async save() {
      try {
        const payload = { ...this.form };
        // 防止误改不可编辑字段
        delete payload.id;
        delete payload.username;
        delete payload.role;
        delete payload.active;
        delete payload.createdAt;
        delete payload.updatedAt;
        delete payload.lastLoginAt;

        const { data } = await this.api().put("/api/users/me", payload);
        this.profile = data;
        this.dialog = false;
        this.toast("✅ Profile updated");
      } catch (e) {
        console.error(e);
        this.toast("⚠️ Update failed");
      }
    },
    toast(msg) {
      this.snackbar.text = msg;
      this.snackbar.show = true;
    },
    yesNo(v) { return v ? 'Yes' : 'No'; },
    niceDate(d) {
      if (!d) return '-';
      try { return new Date(d).toISOString().slice(0,10); } catch { return d; }
    },
    niceDateTime(d) {
      if (!d) return '-';
      try { return new Date(d).toLocaleString(); } catch { return d; }
    }
  },
  mounted() { this.load(); }
};
</script>

<style scoped>
.profile-shell {
  min-height: calc(100vh - 64px);
  padding-top: 96px;
  display: flex;
  justify-content: center;
}

.profile-card {
  width: 100%;
  max-width: 1100px;
  border-radius: 18px;
  box-shadow: 0 8px 28px rgba(20, 40, 80, 0.10);
  overflow: hidden;
  background: #fff;
}

/* Banner */
.banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  background: linear-gradient(180deg, #f7f9ff 0%, #ffffff 80%);
}
.banner-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.avatar-wrap {
  position: relative;
}
.avatar-ring {
  border: 3px solid rgba(25, 118, 210, 0.25);
}
.status-dot {
  position: absolute;
  right: 2px;
  bottom: 4px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid #fff;
  background: #9e9e9e;
}
.status-dot.online { background: #4caf50; }
.headline .name-line { display: flex; align-items: center; flex-wrap: wrap; }
.name {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: .2px;
}
.muted {
  color: #6b7280;
  font-size: 13px;
  margin-top: 2px;
}

/* Section cards */
.section-card {
  border-radius: 14px;
  box-shadow: 0 6px 16px rgba(16, 24, 40, 0.06);
}
.section-title {
  font-weight: 700;
  letter-spacing: .2px;
}

/* Footer button area */
.footer {
  display: flex;
  justify-content: center;
  padding-top: 8px;
}
</style>
