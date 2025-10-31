<template>
  <v-container class="vital-page">
    <v-card class="elevated-card">
      <!-- 顶部标题与筛选 -->
      <v-card-title class="align-center justify-space-between py-4 px-6">
        <div class="header-left">
          <div class="page-title">{{ $t('vital.pageTitle') }}</div>
          <div class="page-subtitle" v-if="selectedRecipientName">
            {{ $t('vital.showingData') }} <strong>{{ selectedRecipientName }}</strong>
          </div>
        </div>

        <div class="header-controls d-flex align-center">
          <v-select
            v-model="selectedRecipient"
            :items="recipients"
            item-text="fullName"
            item-value="id"
            :label="$t('vital.recipientLabel')"
            outlined dense hide-details
            class="control"
            attach="body"
            :menu-props="{ bottom: true, offsetY: true, maxHeight: 320, contentClass: 'elevated-menu' }"
            @change="fetchVitals"
          />
          <v-select
            v-model="selectedRange"
            :items="localizedRangeItems"
            item-text="label"
            item-value="days"
            :label="$t('vital.rangeLabel')"
            outlined dense hide-details
            class="control ml-3"
            attach="body"
            :menu-props="{ bottom: true, offsetY: true, maxHeight: 320, contentClass: 'elevated-menu' }"
            @change="fetchVitals"
          />
          <v-btn color="primary" class="ml-3" @click="openDialog">
            <v-icon left>mdi-plus</v-icon>
            {{ $t('vital.addRecord') }}
          </v-btn>
        </div>
      </v-card-title>

      <!-- 图表展示区 -->
      <v-card-text class="chart-area px-6 pb-8">
        <div v-if="loading" class="loading-wrap">
          <v-progress-circular indeterminate size="28" width="3" color="primary"/>
          <span class="ml-2">{{ $t('vital.loading') }}</span>
        </div>

        <line-chart
          v-else-if="chartData"
          :chart-data="chartData"
          :options="chartOptions"
          class="chart"
        />

        <div v-else class="empty-wrap">
          <v-icon size="40" color="grey lighten-1" class="mr-2">mdi-chart-line-variant</v-icon>
          <div>{{ $t('vital.noData') }}</div>
        </div>
      </v-card-text>

      <!-- 底部按钮 -->
      <v-divider></v-divider>
      <div class="footer px-6 py-4 d-flex justify-end">
        <BackToHomeButton />
      </div>
    </v-card>

    <!-- 添加体征对话框 -->
    <v-dialog v-model="dialog" max-width="520px" persistent>
      <v-card>
        <v-card-title>{{ $t('vital.addRecord') }}</v-card-title>
        <v-card-text>
          <v-form ref="formRef" v-model="valid">
            <v-text-field v-model.number="form.temperature" :label="$t('vital.temperature')" type="number" dense outlined />
            <v-text-field v-model.number="form.heartRate" :label="$t('vital.heartRate')" type="number" dense outlined />
            <v-text-field v-model.number="form.systolicBp" :label="$t('vital.systolic')" type="number" dense outlined />
            <v-text-field v-model.number="form.diastolicBp" :label="$t('vital.diastolic')" type="number" dense outlined />
            <v-text-field v-model.number="form.spo2" :label="$t('vital.spo2')" type="number" dense outlined />
            <v-text-field v-model.number="form.weight" :label="$t('vital.weight')" type="number" dense outlined />
            <v-text-field v-model.number="form.bloodGlucose" :label="$t('vital.bloodGlucose')" type="number" dense outlined />
            <v-slider
              v-model.number="form.painLevel"
              :label="$t('vital.painLevel')"
              thumb-label ticks max="10"
            ></v-slider>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer/>
          <v-btn text @click="dialog=false">{{ $t('vital.cancel') }}</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveVital">{{ $t('vital.save') }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import api from "@/service/api";
import { Line, mixins } from "vue-chartjs";
import BackToHomeButton from "@/components/BackToHomeButton.vue";
const { reactiveProp } = mixins;

const LineChart = {
  extends: Line,
  mixins: [reactiveProp],
  props: ["options"],
  mounted() { this.renderChart(this.chartData, this.options); },
  watch: { chartData: { deep: true, handler(n){ this.renderChart(n, this.options); } } }
};

export default {
  name: "VitalSignsView",
  components: { LineChart, BackToHomeButton },
  data() {
    return {
      recipients: [],
      selectedRecipient: null,
      selectedRange: 7,
      chartData: null,
      loading: false,
      dialog: false,
      valid: false,
      saving: false,
      form: {
        temperature: null, heartRate: null, systolicBp: null, diastolicBp: null,
        spo2: null, weight: null, bloodGlucose: null, painLevel: 0
      },
      chartOptions: {
        responsive: true, maintainAspectRatio: false,
        legend: { display: true, labels: { boxWidth: 18, fontSize: 12 } },
        scales: {
          xAxes: [{ ticks: { autoSkip: true }, gridLines: { color: "rgba(0,0,0,0.05)" } }],
          yAxes: [{ gridLines: { color: "rgba(0,0,0,0.05)" } }]
        },
        elements: { line: { tension: 0.35, borderWidth: 2 }, point: { radius: 2 } },
      },
    };
  },
  computed: {
    selectedRecipientName() {
      const r = Array.isArray(this.recipients)
        ? this.recipients.find(x => x.id === this.selectedRecipient)
        : null;
      return r ? r.fullName : null;
    },
    localizedRangeItems() {
      return [
        { label: this.$t('vital.range7'), days: 7 },
        { label: this.$t('vital.range14'), days: 14 },
        { label: this.$t('vital.range30'), days: 30 },
      ];
    }
  },
  async mounted() {
    await this.fetchRecipients();
  },
  methods: {
    async fetchRecipients() {
      try {
        const res = await api.get("/care-recipients");
        const list = Array.isArray(res.data?.data?.data)
          ? res.data.data.data
          : [];
        this.recipients = list;
        console.log("✅ Recipients response:", this.recipients);
        if (this.recipients.length > 0) {
          this.selectedRecipient = this.recipients[0].id;
          await this.fetchVitals();
        }
      } catch (e) {
        console.error("❌ fetchRecipients error:", e);
      }
    },
    async fetchVitals() {
      if (!this.selectedRecipient) return;
      this.loading = true;
      try {
        const res = await api.get(`/vital-signs/${this.selectedRecipient}`, { params: { days: this.selectedRange } });
        const data = Array.isArray(res.data?.data?.data)
          ? res.data.data.data
          : [];
        console.log("✅ Vitals response:", data);
        if (data.length === 0) {
          this.chartData = null;
          return;
        }
        const labels = data.map(i => new Date(i.recordedAt).toLocaleDateString());
        this.chartData = {
          labels,
          datasets: [
            { label: this.$t('vital.temperature'), data: data.map(i => i.temperature), borderColor: "#e53935", fill: false },
            { label: this.$t('vital.heartRate'), data: data.map(i => i.heartRate), borderColor: "#1e88e5", fill: false },
            { label: this.$t('vital.systolic'), data: data.map(i => i.systolicBp), borderColor: "#43a047", fill: false },
            { label: this.$t('vital.diastolic'), data: data.map(i => i.diastolicBp), borderColor: "#fb8c00", fill: false },
            { label: this.$t('vital.spo2'), data: data.map(i => i.spo2), borderColor: "#8e24aa", fill: false },
          ]
        };
      } catch (e) {
        console.error("❌ fetchVitals error:", e);
      } finally {
        this.loading = false;
      }
    },
    openDialog() {
      this.dialog = true;
      this.form = { temperature: null, heartRate: null, systolicBp: null, diastolicBp: null, spo2: null, weight: null, bloodGlucose: null, painLevel: 0 };
    },
    async saveVital() {
      if (!this.selectedRecipient) return;
      this.saving = true;
      try {
        const payload = { ...this.form, careRecipientId: this.selectedRecipient };
        await api.post("/vital-signs", payload);
        this.dialog = false;
        await this.fetchVitals();
      } catch (e) {
        console.error("❌ save vital error:", e);
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.vital-page {
  min-height: calc(100vh - 64px);
  padding-top: 96px;
  display: flex;
  justify-content: center;
}
.elevated-card {
  width: 100%;
  max-width: 1100px;
  border-radius: 18px;
  box-shadow: 0 6px 24px rgba(0,0,0,0.05);
  background-color: #fff;
}
.page-title {
  font-weight: 700;
  font-size: 20px;
}
.page-subtitle {
  color: #777;
  font-size: 13px;
}
.chart {
  height: 420px;
}
.loading-wrap, .empty-wrap {
  height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #777;
}
.footer { background-color: #fafafa; }
.elevated-menu { z-index: 3000 !important; }
</style>
