<template>
  <v-container class="vital-page">
    <v-card class="elevated-card">
      <v-card-title class="py-4">
        <!-- ...原有标题与筛选控件保持不变... -->
        <div class="title-wrap">
          <div class="page-title">Vital Signs Trend</div>
          <div class="page-subtitle" v-if="selectedRecipientName">
            Showing data for: <strong>{{ selectedRecipientName }}</strong>
          </div>
        </div>
        <v-spacer></v-spacer>

        <!-- Care recipient -->
        <v-select
          v-model="selectedRecipient"
          :items="recipients"
          item-text="fullName"
          item-value="id"
          label="Care recipient"
          outlined dense hide-details
          class="control"
          attach="body"
          :menu-props="{ bottom: true, offsetY: true, maxHeight: 320, contentClass: 'elevated-menu' }"
          @change="fetchVitals"
        />
        <!-- Time range -->
        <v-select
          v-model="selectedRange"
          :items="rangeItems"
          item-text="label"
          item-value="days"
          label="Time range"
          outlined dense hide-details
          class="control ml-3"
          attach="body"
          :menu-props="{ bottom: true, offsetY: true, maxHeight: 320, contentClass: 'elevated-menu' }"
          @change="fetchVitals"
        />
      </v-card-title>

      <v-card-text style="height: 440px" class="px-6 pb-6 pt-0">
        <div v-if="loading" class="loading-wrap">
          <v-progress-circular indeterminate size="28" width="3" color="primary"/>
          <span class="ml-2">Loading data…</span>
        </div>

        <line-chart
          v-else-if="chartData"
          :chart-data="chartData"
          :options="chartOptions"
          style="height:100%"
        />
        <div v-else class="empty-wrap">
          <v-icon large color="grey lighten-1" class="mr-2">mdi-chart-line-variant</v-icon>
          No data in the selected range
        </div>
      </v-card-text>

      <!-- ✅ 复用的返回首页按钮 -->
      <BackToHomeButton />
    </v-card>
  </v-container>
</template>

<script>
import axios from "axios";
import { Line, mixins } from "vue-chartjs";
import BackToHomeButton from "@/components/BackToHomeButton.vue"; // ← 若已全局注册可删除

const { reactiveProp } = mixins;

const LineChart = {
  name: "LineChart",
  extends: Line,
  mixins: [reactiveProp],
  props: ["options"],
  mounted() { this.renderChart(this.chartData, this.options); },
  watch: { chartData: { deep: true, handler(n){ this.renderChart(n, this.options); } } }
};

export default {
  name: "VitalSignsView",
  components: { LineChart, BackToHomeButton }, // ← 若全局注册可去掉 BackToHomeButton
  data() {
    return {
      recipients: [],
      selectedRecipient: null,
      selectedRange: 7,
      rangeItems: [
        { label: "Last 7 days", days: 7 },
        { label: "Last 14 days", days: 14 },
        { label: "Last 30 days", days: 30 },
        { label: "Last 90 days", days: 90 },
      ],
      chartData: null,
      loading: false,
      chartOptions: {
        responsive: true, maintainAspectRatio: false,
        legend: { display: true, labels: { boxWidth: 18, usePointStyle: true } },
        tooltips: { mode: "index", intersect: false },
        hover: { mode: "nearest", intersect: false },
        scales: {
          xAxes: [{ gridLines: { color: "rgba(0,0,0,0.05)" }, ticks: { maxRotation: 0, autoSkip: true } }],
          yAxes: [{ gridLines: { color: "rgba(0,0,0,0.06)" }, ticks: { beginAtZero: false } }]
        },
        elements: { line: { tension: 0.3, borderWidth: 2 }, point: { radius: 2 } },
      },
    };
  },
  computed: {
    selectedRecipientName() {
      const r = this.recipients.find(x => x.id === this.selectedRecipient);
      return r ? r.fullName : null;
    }
  },
  async mounted() {
    await this.fetchRecipients();
    if (this.selectedRecipient) await this.fetchVitals();
  },
  methods: {
    async fetchRecipients() {
      try {
        const res = await axios.get("http://localhost:8081/api/care-recipients");
        this.recipients = res.data || [];
        if (this.recipients.length > 0) {
          const preferred = this.recipients.find(r => r.id === 3);
          this.selectedRecipient = preferred ? preferred.id : this.recipients[0].id;
        }
      } catch (e) { console.error("Error fetching recipients:", e); }
    },
    async fetchVitals() {
      if (!this.selectedRecipient) return;
      try {
        this.loading = true;
        const res = await axios.get(
          `http://localhost:8081/api/vital-signs/${this.selectedRecipient}?days=${this.selectedRange}`
        );
        const data = res.data || [];
        if (data.length === 0) { this.chartData = null; return; }
        const labels = data.map(i => new Date(i.recordedAt).toLocaleDateString());
        this.chartData = {
          labels,
          datasets: [
            { label: "Temperature (°C)", data: data.map(i => i.temperature), borderColor: "#e53935", backgroundColor: "transparent", fill: false },
            { label: "Heart Rate (bpm)",  data: data.map(i => i.heartRate),  borderColor: "#1e88e5", backgroundColor: "transparent", fill: false },
            { label: "Systolic BP (mmHg)", data: data.map(i => i.systolicBp), borderColor: "#43a047", backgroundColor: "transparent", fill: false },
            { label: "Diastolic BP (mmHg)", data: data.map(i => i.diastolicBp), borderColor: "#fb8c00", backgroundColor: "transparent", fill: false },
            { label: "SpO₂ (%)",          data: data.map(i => i.spo2),       borderColor: "#8e24aa", backgroundColor: "transparent", fill: false },
          ]
        };
      } catch (e) {
        console.error("Error fetching vitals:", e);
        this.chartData = null;
      } finally { this.loading = false; }
    }
  }
};
</script>

<style scoped>
.vital-page {
  min-height: calc(100vh - 64px);
  padding-top: 96px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
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
.loading-wrap, .empty-wrap {
  height: 100%;
  display: flex; align-items: center; justify-content: center;
  color: #777;
}
.elevated-menu { z-index: 3000 !important; }
</style>
