<template>
  <div class="config-page">
    <el-card>
      <template #header>
        <span>TCO阈值参数配置</span>
      </template>
      <el-form :model="configForm" label-width="150px">
        <el-form-item label="残值警戒线(%)">
          <el-input-number v-model="configForm.residualThreshold" :min="0" :max="50" />
          <el-text class="ml-2" type="info">设备残值低于此阈值进入待鉴定</el-text>
        </el-form-item>
        <el-form-item label="修复警戒线(%)">
          <el-input-number v-model="configForm.repairThreshold" :min="0" :max="100" />
          <el-text class="ml-2" type="info">单次维修费用占购置成本超过此阈值进入待鉴定</el-text>
        </el-form-item>
        <el-form-item label="寿命警戒线(%)">
          <el-input-number v-model="configForm.lifeThreshold" :min="50" :max="100" />
          <el-text class="ml-2" type="info">设备使用年限超过设计寿命此比例进入待鉴定</el-text>
        </el-form-item>
        <el-form-item label="故障次数警戒线">
          <el-input-number v-model="configForm.faultThreshold" :min="1" :max="10" />
          <el-text class="ml-2" type="info">累计故障次数超过此阈值进入待鉴定</el-text>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listConfigs, updateConfig } from '@/api/config'

const configForm = reactive({
  residualThreshold: 20,
  repairThreshold: 30,
  lifeThreshold: 80,
  faultThreshold: 3
})

onMounted(async () => {
  try {
    const configs = await listConfigs()
    configs.forEach(config => {
      if (configForm.hasOwnProperty(config.configKey)) {
        configForm[config.configKey] = parseFloat(config.configValue)
      }
    })
  } catch (error) {
    console.error('加载配置失败:', error)
  }
})

async function handleSave() {
  try {
    await Promise.all([
      updateConfig({ configKey: 'residualThreshold', configValue: String(configForm.residualThreshold) }),
      updateConfig({ configKey: 'repairThreshold', configValue: String(configForm.repairThreshold) }),
      updateConfig({ configKey: 'lifeThreshold', configValue: String(configForm.lifeThreshold) }),
      updateConfig({ configKey: 'faultThreshold', configValue: String(configForm.faultThreshold) })
    ])
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error('保存失败:', error)
  }
}
</script>

<style scoped>
.config-page { padding: 0; }
.ml-2 { margin-left: 10px; }
</style>