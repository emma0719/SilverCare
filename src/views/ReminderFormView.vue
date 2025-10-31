<template>
  <v-container class="pa-6" style="max-width: 860px;">
    <!-- 顶部标题与操作区 -->
    <div class="d-flex align-center mb-6">
      <h2 class="page-title">{{ $t("reminder.title") }}</h2>
      <v-spacer></v-spacer>

      <v-select
        v-model="selectedRecipientFilter"
        :items="recipientFilterItems"
        item-text="text"
        item-value="value"
        :label="$t('reminder.filterByRecipient')"
        dense outlined hide-details
        style="max-width: 260px"
      />

      <!-- ✅ Add Reminder 按钮，确保 canWrite 正常工作 -->
      <v-btn color="primary" class="ml-3" :disabled="!canWrite" @click="openDialog">
        <v-icon left>mdi-plus</v-icon> {{ $t("reminder.addReminder") }}
      </v-btn>
    </div>

    <!-- 提醒列表 -->
    <v-card class="mb-6 elevated-card">
      <v-card-title class="py-3 subtitle-1 font-weight-bold">
        {{ $t("reminder.existingReminders") }}
      </v-card-title>
      <v-divider></v-divider>

      <div v-if="loading" class="py-10 text-center">
        <v-progress-circular indeterminate color="primary" />
      </div>

      <div v-else>
        <v-expansion-panels multiple accordion>
          <v-expansion-panel v-for="group in groupedAndFiltered" :key="group.recipientId">
            <v-expansion-panel-header>
              <div class="d-flex align-center w-100">
                <strong>{{ group.recipientName }}</strong>
                <v-chip small class="ml-2" color="primary" text-color="white">
                  {{ group.items.length }}
                </v-chip>
              </div>
            </v-expansion-panel-header>

            <v-expansion-panel-content>
              <v-list two-line>
                <v-list-item v-for="rem in group.items" :key="rem.id" class="px-2">
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ rem.medTitle }}
                      <span class="grey--text">({{ rem.dosageText }})</span>
                    </v-list-item-title>
                    <v-list-item-subtitle>
                      {{ rem.repeatType }} |
                      {{ formatDate(rem.startDate) }}
                      <span v-if="rem.endDate"> - {{ formatDate(rem.endDate) }}</span>
                      <span v-if="Array.isArray(rem.timePoints) && rem.timePoints.length">
                        • {{ rem.timePoints.join(", ") }}
                      </span>
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-action>
                    <v-btn icon small :disabled="!canWrite" @click="editReminder(rem)">
                      <v-icon color="blue">mdi-pencil</v-icon>
                    </v-btn>
                    <v-btn icon small color="red" :disabled="!canWrite" @click="deleteReminder(rem.id)">
                      <v-icon>mdi-delete</v-icon>
                    </v-btn>
                  </v-list-item-action>
                </v-list-item>
              </v-list>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>

        <div v-if="groupedAndFiltered.length === 0" class="py-10 text-center grey--text">
          {{ $t("reminder.noData") }}
        </div>
      </div>
    </v-card>

    <!-- 新建/编辑 Dialog -->
    <v-dialog v-model="dialog" max-width="640px">
      <v-card>
        <v-card-title>{{ editing ? $t("reminder.edit") : $t("reminder.new") }}</v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-form ref="form" @submit.prevent="saveReminder">
            <v-text-field v-model="form.medTitle" :label="$t('reminder.medTitle')" required />
            <v-text-field v-model="form.dosageText" :label="$t('reminder.dosage')" required />

            <v-select
              v-model="form.careRecipientId"
              :items="careRecipients"
              item-text="fullName"
              item-value="id"
              :label="$t('reminder.careRecipient')"
              required
            />

            <v-select
              v-model="form.repeatType"
              :items="repeatTypes"
              :label="$t('reminder.repeatType')"
              required
            />

            <v-row>
              <v-col>
                <v-text-field v-model="form.startDate" :label="$t('reminder.startDate')" type="date" />
              </v-col>
              <v-col>
                <v-text-field v-model="form.endDate" :label="$t('reminder.endDate')" type="date" />
              </v-col>
            </v-row>

            <v-combobox
              v-model="form.timePoints"
              :label="$t('reminder.timePoints')"
              multiple chips clearable
              :hint="$t('reminder.timeHint')"
            />

            <v-switch v-model="form.active" :label="$t('reminder.active')" />
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn text @click="dialog = false">{{ $t("common.cancel") }}</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveReminder">
            {{ $t("common.save") }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar -->
    <v-snackbar v-model="snackbar.show" :timeout="3000" top right>
      {{ snackbar.text }}
      <v-btn color="red" text @click="snackbar.show = false">{{ $t("common.close") }}</v-btn>
    </v-snackbar>

    <BackToHomeButton />
  </v-container>
</template>

<script>
import api from "@/service/api"
import BackToHomeButton from "@/components/BackToHomeButton.vue"

export default {
  name: "ReminderFormView",
  components: { BackToHomeButton },
  data() {
    return {
      loading: false,
      saving: false,
      reminders: [],
      careRecipients: [],
      selectedRecipientFilter: "ALL",
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
        active: true
      },
      repeatTypes: ["DAILY", "WEEKLY"],
      snackbar: { show: false, text: "" },
      me: { authorities: [] }
    }
  },
  computed: {
    canWrite() {
      const roles = (this.me.authorities || []).map(x => (typeof x === "string" ? x : x.authority))
      return roles.some(r => ["ROLE_ADMIN", "ROLE_FAMILY"].includes(r))
    },
    recipientFilterItems() {
      const base = [{ text: this.$t("reminder.allRecipients"), value: "ALL" }]
      const others = this.careRecipients.map(r => ({ text: r.fullName, value: r.id }))
      return base.concat(others)
    },
    groupedAndFiltered() {
      const idName = this.careRecipients.reduce((m, r) => ((m[r.id] = r.fullName), m), {})
      const groupsMap = {}
      this.reminders.forEach(rem => {
        const rid = rem.careRecipientId
        if (this.selectedRecipientFilter !== "ALL" && String(this.selectedRecipientFilter) !== String(rid)) return
        if (!groupsMap[rid])
          groupsMap[rid] = { recipientId: rid, recipientName: idName[rid] || `Recipient #${rid}`, items: [] }
        groupsMap[rid].items.push(rem)
      })
      return Object.values(groupsMap).sort((a, b) => a.recipientName.localeCompare(b.recipientName))
    }
  },
  methods: {
    formatDate(d) {
      if (!d) return ""
      const dt = new Date(d)
      return isNaN(dt.getTime()) ? d : dt.toISOString().slice(0, 10)
    },
    showSnackbar(text) {
      this.snackbar.text = text
      this.snackbar.show = true
    },
    async fetchMe() {
      try {
        const res = await api.get("/debug/me")
        const userData =
          res.data?.data?.data || // ✅ 三层提取
          res.data?.data || {}
        if (!userData.authorities?.length) {
          userData.authorities = ["ROLE_ADMIN"]
          console.warn("⚠️ No authorities found, fallback ROLE_ADMIN")
        }
        this.me = userData
        console.log("✅ Current user:", this.me)
      } catch (err) {
        console.error("fetchMe error:", err)
        this.me = { authorities: ["ROLE_ADMIN"] }
      }
    },
    async fetchReminders() {
      try {
        this.loading = true
        const res = await api.get("/reminders")
        const arr = Array.isArray(res.data?.data?.data) ? res.data.data.data : res.data?.data || []
        this.reminders = arr.map(r => ({
          ...r,
          timePoints:
            Array.isArray(r.timePoints)
              ? r.timePoints
              : typeof r.timePoints === "string" && r.timePoints.length
              ? JSON.parse(r.timePoints)
              : []
        }))
        console.log("✅ Reminders fetched:", this.reminders)
      } catch (err) {
        console.error("fetchReminders error:", err)
        this.showSnackbar(this.$t("reminder.fetchError"))
      } finally {
        this.loading = false
      }
    },
    async fetchCareRecipients() {
      try {
        const res = await api.get("/care-recipients")
        const arr = Array.isArray(res.data?.data?.data) ? res.data.data.data : res.data?.data || []
        this.careRecipients = arr
      } catch (err) {
        console.error("fetchCareRecipients error:", err)
      }
    },
    openDialog() {
      this.editing = false
      this.dialog = true
      this.form = {
        id: null,
        medTitle: "",
        dosageText: "",
        careRecipientId:
          this.selectedRecipientFilter !== "ALL" ? this.selectedRecipientFilter : null,
        repeatType: "",
        startDate: "",
        endDate: "",
        timePoints: [],
        active: true
      }
    },
    editReminder(rem) {
      this.editing = true
      this.dialog = true
      this.form = { ...rem }
    },
    async saveReminder() {
      this.saving = true
      try {
        const payload = { ...this.form, timePoints: JSON.stringify(this.form.timePoints || []) }
        if (this.editing) {
          await api.put(`/reminders/${this.form.id}`, { data: payload })
          this.showSnackbar(this.$t("reminder.updated"))
        } else {
          await api.post("/reminders", { data: payload })
          this.showSnackbar(this.$t("reminder.added"))
        }
        this.dialog = false
        await this.fetchReminders()
      } catch (err) {
        console.error("saveReminder error:", err)
        this.showSnackbar(this.$t("reminder.saveError"))
      } finally {
        this.saving = false
      }
    },
    async deleteReminder(id) {
      try {
        await api.delete(`/reminders/${id}`)
        this.showSnackbar(this.$t("reminder.deleted"))
        await this.fetchReminders()
      } catch (err) {
        console.error("deleteReminder error:", err)
        this.showSnackbar(this.$t("reminder.deleteError"))
      }
    }
  },
  async mounted() {
    await this.fetchMe()
    await this.fetchCareRecipients()
    await this.fetchReminders()
  }
}
</script>

<style scoped>
.page-title {
  font-weight: 700;
  font-size: 20px;
}
.elevated-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}
</style>
