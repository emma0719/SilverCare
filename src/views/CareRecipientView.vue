<template>
  <v-container class="page">
    <v-card class="elevated-card">
      <v-card-title class="py-4 px-6 d-flex align-center justify-space-between">
        <div>
          <div class="page-title">{{ $t('care.title') }}</div>
          <div class="page-subtitle">{{ $t('care.subtitle') }}</div>
        </div>
        <div class="d-flex align-center">
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            :label="$t('care.search')"
            hide-details dense outlined class="mr-3 control"
          />
          <v-btn color="primary" dark @click="openDialog" :disabled="!canWrite">
            <v-icon left>mdi-plus</v-icon>
            {{ $t('care.newRecipient') }}
          </v-btn>
        </div>
      </v-card-title>

      <v-card-text>
        <v-data-table
          :headers="localizedHeaders"
          :items="recipients"
          :search="search"
          dense
          :loading="loading"
          class="data-table"
        >
          <template v-slot:[`item.active`]="{ item }">
            <v-chip :color="item.active ? 'green' : 'grey'" text-color="white" small>
              {{ item.active ? $t('care.active') : $t('care.inactive') }}
            </v-chip>
          </template>

          <template v-slot:[`item.actions`]="{ item }">
            <v-btn icon small :disabled="!canWrite" @click="openEdit(item)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon small color="red" :disabled="!canWrite" @click="confirmDelete(item)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </template>

          <template v-slot:no-data>
            <div class="empty-wrap">
              <v-icon size="40" color="grey lighten-1" class="mr-2">mdi-account-multiple-outline</v-icon>
              {{ $t('care.noData') }}
            </div>
          </template>
        </v-data-table>
      </v-card-text>

      <div class="footer px-6 py-4 d-flex justify-end">
        <BackToHomeButton />
      </div>
    </v-card>

    <!-- 新建/编辑 Dialog -->
    <v-dialog v-model="dialog" max-width="520px" persistent>
      <v-card>
        <v-card-title class="headline">
          {{ editing ? $t('care.editRecipient') : $t('care.newRecipient') }}
        </v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field v-model="form.fullName" :label="$t('care.fullName')" :rules="[r.required]" outlined dense />
            <v-text-field v-model="form.phoneNumber" :label="$t('care.phoneNumber')" outlined dense />
            <v-text-field v-model.number="form.age" :label="$t('care.age')" type="number" min="0" outlined dense />
            <v-text-field v-model="form.address" :label="$t('care.address')" outlined dense />
            <v-switch v-model="form.active" :label="$t('care.active')" class="mt-2" />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="closeDialog">{{ $t('care.cancel') }}</v-btn>
          <v-btn color="primary" :loading="saving" :disabled="!valid" @click="save">
            {{ $t('care.save') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除确认 Dialog -->
    <v-dialog v-model="dialogDelete" max-width="420px">
      <v-card>
        <v-card-title class="headline">{{ $t('care.deleteTitle') }}</v-card-title>
        <v-card-text>
          {{ $t('care.deleteConfirm') }}
          <strong>{{ toDelete?.fullName }}</strong>?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="dialogDelete=false">{{ $t('care.cancel') }}</v-btn>
          <v-btn color="red" dark :loading="deleting" @click="doDelete">
            {{ $t('care.delete') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import api from "@/service/api";
import BackToHomeButton from "@/components/BackToHomeButton.vue";

export default {
  name: "CareRecipientView",
  components: { BackToHomeButton },
  data() {
    return {
      recipients: [],
      loading: false,
      saving: false,
      deleting: false,
      dialog: false,
      dialogDelete: false,
      editing: false,
      search: "",
      valid: false,
      toDelete: null,
      form: { id: null, fullName: "", phoneNumber: "", age: null, address: "", active: true },
      r: { required: v => !!v || "Required" },
      me: { authorities: [] }
    };
  },
  computed: {
    localizedHeaders() {
      return [
        { text: this.$t('care.name'), value: "fullName" },
        { text: this.$t('care.phone'), value: "phoneNumber" },
        { text: this.$t('care.age'), value: "age", align: "end", width: 80 },
        { text: this.$t('care.address'), value: "address" },
        { text: this.$t('care.status'), value: "active", width: 110 },
        { text: this.$t('care.actions'), value: "actions", sortable: false, align: "end", width: 120 },
      ];
    },
    canWrite() {
      const roles = (this.me.authorities || []).map(x => (typeof x === "string" ? x : x.authority));
      return roles.some(r => ["ROLE_ADMIN", "ROLE_FAMILY"].includes(r));
    }
  },
  async mounted() {
    await this.fetchMe();
    await this.fetchRecipients();
  },
  methods: {
    async fetchMe() {
      try {
        const res = await api.get("/debug/me");
        const userData = res.data?.data || {};
        // fallback
        if (!userData.authorities || userData.authorities.length === 0) {
          userData.authorities = ["ROLE_ADMIN"];
          console.warn("⚠️ No roles found, using fallback ROLE_ADMIN for debugging");
        }
        this.me = userData;
        console.log("✅ Current user:", this.me);
      } catch (e) {
        console.warn("fetchMe failed:", e);
        this.me = { authorities: ["ROLE_ADMIN"] };
      }
    },
    async fetchRecipients() {
      try {
        this.loading = true;
        const res = await api.get("/care-recipients");
        this.recipients =
          Array.isArray(res.data?.data?.data)
            ? res.data.data.data
            : res.data?.data || [];
        console.log("✅ Recipients fetched:", this.recipients);
      } catch (e) {
        console.error("fetchRecipients error:", e);
      } finally {
        this.loading = false;
      }
    },
    openDialog() {
      this.editing = false;
      this.dialog = true;
      this.form = { id: null, fullName: "", phoneNumber: "", age: null, address: "", active: true };
    },
    openEdit(item) {
      this.editing = true;
      this.dialog = true;
      this.form = { ...item };
    },
    closeDialog() {
      this.dialog = false;
    },
    async save() {
      if (!this.$refs.form?.validate()) return;
      this.saving = true;
      try {
        if (this.editing && this.form.id)
          await api.put(`/care-recipients/${this.form.id}`, this.form);
        else
          await api.post("/care-recipients", this.form);
        this.dialog = false;
        await this.fetchRecipients();
      } catch (e) {
        console.error("save recipient error:", e);
      } finally {
        this.saving = false;
      }
    },
    confirmDelete(item) {
      this.toDelete = item;
      this.dialogDelete = true;
    },
    async doDelete() {
      if (!this.toDelete) return;
      this.deleting = true;
      try {
        await api.delete(`/care-recipients/${this.toDelete.id}`);
        this.dialogDelete = false;
        await this.fetchRecipients();
      } catch (e) {
        console.error("delete error:", e);
      } finally {
        this.deleting = false;
      }
    }
  }
};
</script>

<style scoped>
.page {
  min-height: calc(100vh - 64px);
  padding-top: 96px;
  display: flex;
  justify-content: center;
}
.elevated-card {
  width: 100%;
  max-width: 1100px;
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(0,0,0,0.06);
  background-color: #fff;
}
.page-title { font-weight: 700; font-size: 20px; }
.page-subtitle { color: #777; font-size: 13px; }
.control { width: 240px; }
.data-table { padding: 0 16px 8px; }
.empty-wrap {
  padding: 40px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #777;
}
.footer { background-color: #fafafa; }
</style>
