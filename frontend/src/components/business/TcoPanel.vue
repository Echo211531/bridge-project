<template>
  <div class="tco-panel">
    <el-row :gutter="20">
      <!-- 维修方案 -->
      <el-col :span="8">
        <el-card class="tco-card repair-card">
          <template #header>
            <div class="card-header">
              <span>维修方案</span>
              <el-tag type="success" v-if="recommendation === 'repair'">推荐</el-tag>
            </div>
          </template>
          <div class="tco-info">
            <div class="tco-value">
              TCO: <span class="amount">{{ tcoRepair?.toFixed(2) || 0 }}</span> 元
            </div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="当前修复费">
                {{ currentRepairCost?.toFixed(2) || 0 }} 元
              </el-descriptions-item>
              <el-descriptions-item label="未来保养费">
                {{ futureMaintainCost?.toFixed(2) || 0 }} 元
              </el-descriptions-item>
              <el-descriptions-item label="未来维修费">
                {{ futureRepairCost?.toFixed(2) || 0 }} 元
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 更换方案 -->
      <el-col :span="8">
        <el-card class="tco-card replace-card">
          <template #header>
            <div class="card-header">
              <span>更换方案</span>
              <el-tag type="success" v-if="recommendation === 'replace'">推荐</el-tag>
            </div>
          </template>
          <div class="tco-info">
            <div class="tco-value">
              TCO: <span class="amount">{{ tcoReplace?.toFixed(2) || 0 }}</span> 元
            </div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="新设备价">
                {{ newDevicePrice?.toFixed(2) || 0 }} 元
              </el-descriptions-item>
              <el-descriptions-item label="未来保养费">
                {{ newFutureMaintainCost?.toFixed(2) || 0 }} 元
              </el-descriptions-item>
              <el-descriptions-item label="残值抵扣">
                -{{ salvageValue?.toFixed(2) || 0 }} 元
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 结论 -->
      <el-col :span="8">
        <el-card class="tco-card conclusion-card">
          <template #header>
            <span>决策结论</span>
          </template>
          <div class="conclusion-info">
            <div class="diff">
              TCO差额: <span>{{ tcoDifference?.toFixed(2) || 0 }}</span> 元
            </div>
            <el-alert
              :title="recommendationName"
              :description="decisionReason"
              :type="recommendation === 'repair' ? 'success' : 'warning'"
              show-icon
              :closable="false"
            />
            <div class="threshold-info">
              <p v-if="belowResidualThreshold">
                <el-icon color="#e6a23c"><Warning /></el-icon>
                残值低于警戒线
              </p>
              <p v-if="aboveRepairThreshold">
                <el-icon color="#e6a23c"><Warning /></el-icon>
                修复费超过警戒线
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * TCO决策面板组件（三栏决策面板）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { Warning } from '@element-plus/icons-vue'

defineProps({
  tcoRepair: Number,
  tcoReplace: Number,
  currentRepairCost: Number,
  futureMaintainCost: Number,
  futureRepairCost: Number,
  newDevicePrice: Number,
  newFutureMaintainCost: Number,
  salvageValue: Number,
  recommendation: String,
  recommendationName: String,
  tcoDifference: Number,
  decisionReason: String,
  belowResidualThreshold: Boolean,
  aboveRepairThreshold: Boolean
})
</script>

<style scoped>
.tco-panel {
  padding: 0;
}

.tco-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tco-value {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

.amount {
  font-size: 24px;
  color: #409eff;
}

.repair-card .amount {
  color: #67c23a;
}

.replace-card .amount {
  color: #e6a23c;
}

.conclusion-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.diff {
  font-size: 16px;
  color: #909399;
}

.threshold-info {
  margin-top: 8px;
}

.threshold-info p {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 4px 0;
  color: #e6a23c;
}
</script>