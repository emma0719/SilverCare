<template>
  <v-app>
    <v-main>
      <v-container class="page">
        <v-card class="elevated-card">
          <v-card-title class="py-4">
            <div class="title-wrap">
              <div class="page-title">Care Recipients</div>
              <div class="page-subtitle">Manage the recipients you are responsible for</div>
            </div>
            <v-spacer></v-spacer>

            <v-text-field
              v-model="search"
              append-icon="mdi-magnify"
              label="Search"
              hide-details
              dense
              outlined
              class="mr-3 control"
            />
            <v-btn color="primary" dark @click="openCreate">
              <v-icon left>mdi-plus</v-icon> New Recipient
            </v-btn>
          </v-card-title>

          <v-data-table
            :headers="headers"
            :items="recipients"
            :search="search"
            :loading="loading"
            class="data-table"
            dense
          >
            <!-- ✅ legacy slot 写法，避免 eslint 对 v-slot 的点号误判 -->
            <template slot="item.active" slot-scope="{ item }">
              <v-chip :color="item.active ? 'green' : 'grey'" text-color="white" small>
                {{ item.active ? 'Active' : 'Inactive' }}
              </v-chip>
            </template>

            <template slot="item.actions" slot-scope="{ item }">
              <v-btn icon small @click="openEdit(item)" :title="'Edit ' + item.fullName">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon small color="red" @click="confirmDelete(item)" :title="'Delete ' + item.fullName">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </template>

            <template slot="no-data">
              <div class="empty-wrap">
                <v-icon large color="grey lighten-1" class="mr-2">mdi-account-multiple-outline</v-icon>
                No recipients yet. Click “New Recipient” to add one.
              </div>
            </template>
          </v-data-table>

          <!-- 复用的返回首页按钮组件 -->
          <BackToHomeButton />
        </v-card>
      </v-container>

      <!-- 创建/编辑 Dialog -->
      <v-dialog v-model="dialog" max-width="520px" persistent>
        <v-card>
          <v-card-title class="headline">
            {{ editing ? 'Edit Recipient' : 'New Recipient' }}
          </v-card-title>
          <v-card-text>
            <v-form ref="form" v-model="valid">
              <v-text-field v-model="form.fullName" label="Full name" :rules="[r.required]" outlined dense />
              <v-text-field v-model="form.phoneNumber" label="Phone number" outlined dense />
              <v-text-field v-model.number="form.age" label="Age" type="number" min="0" outlined dense />
              <v-text-field v-model="form.address" label="Address" outlined dense />
              <v-switch v-model="form.active" label="Active" class="mt-2" />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn text @click="closeDialog">Cancel</v-btn>
            <v-btn color="primary" :loading="saving" :disabled="!valid" @click="save">Save</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- 删除确认 Dialog -->
      <v-dialog v-model="dialogDelete" max-width="420px">
        <v-card>
          <v-card-title class="headline">Delete recipient</v-card-title>
          <v-card-text>
            Are you sure you want to delete
            <strong>{{ toDelete?.fullName }}</strong>?
            This action cannot be undone.
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn text @click="dialogDelete=false">Cancel</v-btn>
            <v-btn color="red" dark :loading="deleting" @click="doDelete">Delete</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

    </v-main>
  </v-app>
</template>

<script>
import axios from "axios";
import BackToHomeButton from "@/components/BackToHomeButton.vue";

export default {
  name: "CareRecipientView",
  components: { BackToHomeButton },
  data() {
    return {
      loading: false,
      saving: false,
      deleting: false,
      search: "",
      recipients: [],
      headers: [
        { text: "Name", value: "fullName" },
        { text: "Phone", value: "phoneNumber" },
        { text: "Age", value: "age", align: "end", width: 90 },
        { text: "Address", value: "address" },
        { text: "Status", value: "active", width: 110 },
        { text: "Actions", value: "actions", sortable: false, align: "end", width: 120 },
      ],
      dialog: false,
      dialogDelete: false,
      editing: false,
      valid: false,
      form: {
        id: null,
        fullName: "",
        phoneNumber: "",
        age: null,
        address: "",
        active: true,
      },
      toDelete: null,
      r: { required: v => !!v || "Required" }
    };
  },
  async mounted() {
    await this.fetchRecipients();
  },
  methods: {
    api() {
      const token = localStorage.getItem("token");
      return axios.create({
        baseURL: "http://localhost:8081",
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
    },
    async fetchRecipients() {
      try {
        this.loading = true;
        const { data } = await this.api().get("/api/care-recipients");
        this.recipients = Array.isArray(data) ? data : [];
      } catch (e) {
        console.error("fetch recipients error:", e);
        this.$toast && this.$toast.error("Failed to load recipients");
      } finally {
        this.loading = false;
      }
    },
    openCreate() {
      this.editing = false;
      this.form = { id: null, fullName: "", phoneNumber: "", age: null, address: "", active: true };
      this.dialog = true;
    },
    openEdit(item) {
      this.editing = true;
      this.form = { ...item };
      this.dialog = true;
    },
    closeDialog() {
      this.dialog = false;
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.resetValidation(); });
    },
    async save() {
      if (!this.$refs.form || !this.$refs.form.validate()) return;
      this.saving = true;
      try {
        if (this.editing && this.form.id) {
          await this.api().put(`/api/care-recipients/${this.form.id}`, this.form);
        } else {
          const body = { ...this.form }; delete body.id;
          await this.api().post("/api/care-recipients", body);
        }
        this.dialog = false;
        await this.fetchRecipients();
      } catch (e) {
        console.error("save error:", e);
        this.$toast && this.$toast.error("Save failed");
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
        await this.api().delete(`/api/care-recipients/${this.toDelete.id}`);
        this.dialogDelete = false;
        this.toDelete = null;
        await this.fetchRecipients();
      } catch (e) {
        console.error("delete error:", e);
        this.$toast && this.$toast.error("Delete failed");
      } finally {
        this.deleting = false;
      }
    },
  },
};
</script>

<style scoped>
.page {
  min-height: calc(100vh - 64px);
  padding-top: 96px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
.elevated-card {
  width: 100%;
  max-width: 1100px;
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(0,0,0,0.06);
  overflow: hidden;
}
.title-wrap { display: flex; flex-direction: column; }
.page-title { font-weight: 700; font-size: 18px; letter-spacing: .3px; }
.page-subtitle { color: #666; font-size: 13px; margin-top: 2px; }
.control { width: 240px; }
.data-table { padding: 0 16px 8px; }
.empty-wrap {
  padding: 40px 0;
  display: flex; justify-content: center; align-items: center;
  color: #777;
}
</style>
