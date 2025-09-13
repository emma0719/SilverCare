<template>
  <v-container class="pa-6" style="max-width: 700px;">
    <h2 class="mb-6 text-center">Medication Reminders</h2>

    <!-- ✅ 调试输出，方便确认 careRecipients 是否获取成功 -->


    <!-- Reminder 列表 -->
    <v-card class="mb-6">
      <v-card-title>
        Existing Reminders
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="openDialog()">+ Add Reminder</v-btn>
      </v-card-title>
      <v-divider></v-divider>

      <v-list two-line>
        <v-list-item v-for="rem in reminders" :key="rem.id">
          <v-list-item-content>
            <v-list-item-title>
              {{ rem.medTitle }} ({{ rem.dosageText }})
            </v-list-item-title>
            <v-list-item-subtitle>
              {{ rem.repeatType }} | {{ rem.startDate }} - {{ rem.endDate }}
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-action>
            <v-btn icon @click="editReminder(rem)">
              <v-icon color="blue">mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon @click="deleteReminder(rem.id)">
              <v-icon color="red">mdi-delete</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list>
    </v-card>

    <!-- 表单对话框 -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ editing ? 'Edit Reminder' : 'New Reminder' }}
        </v-card-title>
        <v-card-text>
          <v-form ref="form" @submit.prevent="saveReminder">
            <v-text-field v-model="form.medTitle" label="Medication Title" required />
            <v-text-field v-model="form.dosageText" label="Dosage" required />

            <v-select
              v-model="form.careRecipientId"
              :items="careRecipients"
              item-text="fullName"
              item-value="id"
              label="Care Recipient"
              required
            />

            <v-select
              v-model="form.repeatType"
              :items="['DAILY','WEEKLY']"
              label="Repeat Type"
              required
            />

            <v-row>
              <v-col>
                <v-text-field v-model="form.startDate" label="Start Date" type="date" />
              </v-col>
              <v-col>
                <v-text-field v-model="form.endDate" label="End Date" type="date" />
              </v-col>
            </v-row>

            <v-combobox
              v-model="form.timePoints"
              label="Time Points"
              multiple
              chips
              clearable
              hint="e.g. 08:00, 12:00"
            />

            <v-switch v-model="form.active" label="Active" />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="dialog=false">Cancel</v-btn>
          <v-btn color="primary" @click="saveReminder">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar 提示 -->
    <v-snackbar v-model="snackbar.show" :timeout="3000" top right>
      {{ snackbar.text }}
      <v-btn color="red" text @click="snackbar.show = false">Close</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import api from "@/service/api"; // ✅ 使用封装的 axios 实例

export default {
  name: "ReminderFormView",
  data() {
    return {
      reminders: [],
      careRecipients: [], // 从后端 API 获取
      dialog: false,
      editing: false,
      form: {
        id: null,
        medTitle: "",
        dosageText: "",
        careRecipientId: null,
        repeatType: "",
        startDate: "",
        endDate: "",
        timePoints: [],
        active: true,
      },
      snackbar: {
        show: false,
        text: "",
      },
    };
  },
  methods: {
    async fetchReminders() {
      try {
        const res = await api.get("/reminders");
        this.reminders = res.data;
      } catch (err) {
        this.showSnackbar("⚠️ Failed to fetch reminders");
        console.error(err);
      }
    },
    async fetchCareRecipients() {
      try {
        const res = await api.get("/care-recipients");
        this.careRecipients = res.data;
      } catch (err) {
        this.showSnackbar("⚠️ Failed to fetch care recipients");
        console.error(err);
      }
    },
    openDialog() {
      this.editing = false;
      this.form = {
        id: null,
        medTitle: "",
        dosageText: "",
        careRecipientId: null,
        repeatType: "",
        startDate: "",
        endDate: "",
        timePoints: [],
        active: true,
      };
      this.dialog = true;
      this.fetchCareRecipients(); // ✅ 打开时刷新 CareRecipients
    },
    editReminder(rem) {
      this.editing = true;
      this.form = JSON.parse(JSON.stringify(rem));
      this.dialog = true;
    },
    async saveReminder() {
      try {
        if (this.editing) {
          await api.put(`/reminders/${this.form.id}`, this.form);
          this.showSnackbar("✅ Reminder updated");
        } else {
          await api.post("/reminders", this.form);
          this.showSnackbar("✅ Reminder added");
        }
        this.dialog = false;
        this.fetchReminders();
      } catch (err) {
        this.showSnackbar("⚠️ Save reminder failed");
        console.error(err);
      }
    },
    async deleteReminder(id) {
      try {
        await api.delete(`/reminders/${id}`);
        this.fetchReminders();
        this.showSnackbar("🗑️ Reminder deleted");
      } catch (err) {
        this.showSnackbar("⚠️ Delete reminder failed");
        console.error(err);
      }
    },
    showSnackbar(message) {   // ✅ 修复：新增 showSnackbar 方法
      this.snackbar.text = message;
      this.snackbar.show = true;
    },
  },
  mounted() {
    this.fetchReminders();
    this.fetchCareRecipients();
  },
};
</script>
