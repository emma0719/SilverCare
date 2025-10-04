<template>
  <v-container class="pa-6" style="max-width: 820px;">
    <!-- 返回按钮 -->
    <v-btn color="primary" class="mb-4" @click="goBack">
      <v-icon left>mdi-arrow-left</v-icon>
      Back to Dashboard
    </v-btn>

    <div class="d-flex align-center mb-4">
      <h2 class="mr-4">Medication Reminders</h2>

      <v-spacer></v-spacer>

      <!-- 受照顾者筛选 -->
      <v-select
        v-model="selectedRecipientFilter"
        :items="recipientFilterItems"
        item-text="text"
        item-value="value"
        label="Filter by recipient"
        dense outlined hide-details
        style="max-width: 260px"
        @change="onFilterChange"
      />

      <v-btn color="primary" class="ml-3" @click="openDialog()">
        <v-icon left>mdi-plus</v-icon> Add Reminder
      </v-btn>
    </div>

    <!-- 按受照顾者分组的列表 -->
    <v-card class="mb-6">
      <v-card-title class="py-3">Existing Reminders</v-card-title>
      <v-divider></v-divider>

      <div v-if="loading" class="py-10 text-center">
        <v-progress-circular indeterminate color="primary" />
      </div>

      <div v-else>
        <v-expansion-panels multiple accordion>
          <v-expansion-panel
            v-for="group in groupedAndFiltered"
            :key="group.recipientId"
          >
            <v-expansion-panel-header>
              <div class="d-flex align-center w-100">
                <strong>{{ group.recipientName }}</strong>
                <v-chip small class="ml-2" color="primary" text-color="white">{{ group.items.length }}</v-chip>
              </div>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-list two-line>
                <template v-if="group.items.length">
                  <v-list-item v-for="rem in group.items" :key="rem.id">
                    <v-list-item-content>
                      <v-list-item-title>
                        {{ rem.medTitle }} <span class="grey--text">({{ rem.dosageText }})</span>
                      </v-list-item-title>
                      <v-list-item-subtitle>
                        {{ rem.repeatType }} |
                        {{ niceDate(rem.startDate) }}
                        <span v-if="rem.endDate"> - {{ niceDate(rem.endDate) }}</span>
                        <span v-if="Array.isArray(rem.timePoints) && rem.timePoints.length">
                          • {{ rem.timePoints.join(', ') }}
                        </span>
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
                </template>

                <template v-else>
                  <div class="py-6 text-center grey--text">
                    No reminders for {{ group.recipientName }}
                  </div>
                </template>
              </v-list>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>

        <!-- 当没有任何分组或全为空 -->
        <div v-if="groupedAndFiltered.length === 0" class="py-10 text-center grey--text">
          No data. Click “Add Reminder” to create one.
        </div>
      </div>
    </v-card>

    <!-- 表单对话框 -->
    <v-dialog v-model="dialog" max-width="640px">
      <v-card>
        <v-card-title>{{ editing ? 'Edit Reminder' : 'New Reminder' }}</v-card-title>
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
              multiple chips clearable
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

    <!-- 复用的返回首页按钮（可保留顶部Back按钮二选一） -->
    <BackToHomeButton />
  </v-container>
</template>

<script>
import api from "@/service/api";
import BackToHomeButton from "@/components/BackToHomeButton.vue";

export default {
  name: "ReminderFormView",
  components: { BackToHomeButton },
  data() {
    return {
      loading: false,
      reminders: [],
      careRecipients: [],
      selectedRecipientFilter: 'ALL',  // ALL 或具体ID
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
      snackbar: { show: false, text: "" },
    };
  },
  computed: {
    // 顶部筛选下拉的数据源
    recipientFilterItems() {
      const base = [{ text: "All recipients", value: "ALL" }];
      const others = this.careRecipients.map(r => ({ text: r.fullName, value: r.id }));
      return base.concat(others);
    },
    // 按 careRecipientId 分组后的数据，并应用筛选
    groupedAndFiltered() {
      // 建立 id->name 映射
      const idName = this.careRecipients.reduce((m, r) => {
        m[r.id] = r.fullName;
        return m;
      }, {});

      // 初始化分组容器（只收集有数据的收件人；筛选时只保留目标）
      const groupsMap = {};

      this.reminders.forEach(rem => {
        const rid = rem.careRecipientId;
        // 先按照筛选过滤
        if (this.selectedRecipientFilter !== 'ALL' && String(this.selectedRecipientFilter) !== String(rid)) {
          return;
        }
        if (!groupsMap[rid]) {
          groupsMap[rid] = { recipientId: rid, recipientName: idName[rid] || `Recipient #${rid}`, items: [] };
        }
        groupsMap[rid].items.push(rem);
      });

      // 输出为数组并按名字排序（可选）
      return Object.values(groupsMap).sort((a, b) => a.recipientName.localeCompare(b.recipientName));
    },
  },
  methods: {
    goBack() {
      this.$router.push({ name: "LoginHomePage" });
    },
    niceDate(d) {
      if (!d) return "";
      // 支持 YYYY-MM-DD 或 ISO
      try {
        const dt = new Date(d);
        if (isNaN(dt.getTime())) return d;
        return dt.toISOString().slice(0, 10);
      } catch {
        return d;
      }
    },
    onFilterChange() {
      // 分组是 computed，会自动刷新；这里保留可扩展
    },
    async fetchReminders() {
      try {
        this.loading = true;
        const res = await api.get("/reminders");
        // 如果后端 timePoints 是 CSV 字符串，这里可转数组：
        this.reminders = (res.data || []).map(r => ({
          ...r,
          timePoints: Array.isArray(r.timePoints)
            ? r.timePoints
            : (typeof r.timePoints === 'string' && r.timePoints.length
                ? r.timePoints.split(',').map(s => s.trim())
                : []),
        }));
      } catch (err) {
        this.showSnackbar("⚠️ Failed to fetch reminders");
        console.error(err);
      } finally {
        this.loading = false;
      }
    },
    async fetchCareRecipients() {
      try {
        const res = await api.get("/care-recipients");
        this.careRecipients = res.data || [];
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
        careRecipientId: this.selectedRecipientFilter !== 'ALL' ? this.selectedRecipientFilter : null,
        repeatType: "",
        startDate: "",
        endDate: "",
        timePoints: [],
        active: true,
      };
      this.dialog = true;
      this.fetchCareRecipients();
    },
    editReminder(rem) {
      this.editing = true;
      // 深拷贝，避免直接引用
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
    showSnackbar(message) {
      this.snackbar.text = message;
      this.snackbar.show = true;
    },
  },
  mounted() {
    this.fetchCareRecipients();
    this.fetchReminders();
  },
};
</script>

<style scoped>
/* 小幅美化，贴合你当前整体风格 */
</style>
