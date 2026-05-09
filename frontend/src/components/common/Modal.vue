<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :before-close="handleClose"
  >
    <slot></slot>
    <template #footer v-if="showFooter">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 通用弹窗组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { computed } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  title: String,
  width: {
    type: String,
    default: '600px'
  },
  showFooter: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'close'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

function handleClose() {
  emit('close')
  emit('update:modelValue', false)
}

function handleConfirm() {
  emit('confirm')
}
</script>