<template>
  <div class="system-config-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">系统配置</h1>
      <div class="page-actions">
        <el-button type="primary" @click="handleBatchSave" :loading="saving">
          <el-icon><Check /></el-icon>
          保存全部
        </el-button>
      </div>
    </div>

    <!-- 配置分组 -->
    <el-row :gutter="24">
      <el-col :span="6">
        <el-card class="group-card" shadow="never">
          <template #header>
            <span>配置分组</span>
          </template>
          <el-menu
            :default-active="activeGroup"
            @select="handleGroupSelect"
            class="group-menu"
          >
            <el-menu-item
              v-for="group in configGroups"
              :key="group"
              :index="group"
            >
              {{ getGroupLabel(group) }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card class="config-card" shadow="never" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>{{ getGroupLabel(activeGroup) }}</span>
              <el-button type="text" @click="handleRefresh">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>

          <div class="config-list">
            <div 
              v-for="config in currentConfigs" 
              :key="config.key"
              class="config-item"
            >
              <div class="config-header">
                <div class="config-key">{{ config.key }}</div>
                <div class="config-description">{{ config.description }}</div>
              </div>
              
              <div class="config-control">
                <el-input
                  v-if="getConfigType(config.key) === 'text'"
                  v-model="config.value"
                  placeholder="请输入配置值"
                  @change="handleConfigChange(config)"
                />
                
                <el-input-number
                  v-else-if="getConfigType(config.key) === 'number'"
                  v-model="config.value"
                  placeholder="请输入数值"
                  @change="handleConfigChange(config)"
                />
                
                <el-switch
                  v-else-if="getConfigType(config.key) === 'boolean'"
                  v-model="config.value"
                  @change="handleConfigChange(config)"
                />
                
                <el-select
                  v-else-if="getConfigType(config.key) === 'select'"
                  v-model="config.value"
                  placeholder="请选择"
                  @change="handleConfigChange(config)"
                >
                  <el-option
                    v-for="option in getConfigOptions(config.key)"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                
                <el-input
                  v-else
                  v-model="config.value"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入配置值"
                  @change="handleConfigChange(config)"
                />
              </div>

              <div class="config-actions">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="handleSaveConfig(config)"
                  :loading="config.saving"
                >
                  保存
                </el-button>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="handleResetConfig(config)"
                >
                  重置
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Refresh } from '@element-plus/icons-vue'
import { systemApi } from '@/api/system'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const activeGroup = ref('basic')
const configGroups = ref(['basic', 'payment', 'notification', 'security'])
const allConfigs = ref([])
const originalConfigs = ref({})

// 当前分组的配置
const currentConfigs = computed(() => {
  return allConfigs.value.filter(config => config.group === activeGroup.value)
})

// 加载配置分组
const loadConfigGroups = async () => {
  try {
    // TODO: Redis缓存 - 键名: bk.system:config-groups, TTL: 1小时
    const response = await systemApi.getConfigGroups()
    if (response.code === 200) {
      configGroups.value = response.data || configGroups.value
    }
  } catch (error) {
    console.error('加载配置分组失败:', error)
  }
}

// 加载系统配置
const loadSystemConfigs = async (group) => {
  try {
    loading.value = true
    
    // TODO: Redis缓存 - 键名: bk.system:configs:{group}, TTL: 30分钟
    const response = await systemApi.getSystemConfigs(group)
    
    if (response.code === 200) {
      const configs = response.data || []
      
      // 为每个配置项添加保存状态
      configs.forEach(config => {
        config.saving = false
        config.changed = false
        // 保存原始值用于重置
        originalConfigs.value[config.key] = { ...config }
      })
      
      // 更新或添加到全局配置列表
      allConfigs.value = allConfigs.value.filter(c => c.group !== group).concat(configs)
    }
  } catch (error) {
    console.error('加载系统配置失败:', error)
    ElMessage.error('加载系统配置失败')
  } finally {
    loading.value = false
  }
}

// 分组选择
const handleGroupSelect = (group) => {
  activeGroup.value = group
  if (!currentConfigs.value.length) {
    loadSystemConfigs(group)
  }
}

// 配置变化
const handleConfigChange = (config) => {
  config.changed = true
}

// 保存单个配置
const handleSaveConfig = async (config) => {
  try {
    config.saving = true
    
    // TODO: 危险配置项变更确认 - 如数据库连接、支付配置等
    if (isDangerousConfig(config.key)) {
      await ElMessageBox.confirm(
        `"${config.key}" 是重要配置项，修改后可能影响系统运行，确定要保存吗？`,
        '危险操作确认',
        {
          confirmButtonText: '确定保存',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    }
    
    await systemApi.updateSystemConfig(config.key, {
      value: config.value,
      description: config.description
    })
    
    config.changed = false
    originalConfigs.value[config.key] = { ...config }
    ElMessage.success('配置保存成功')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存配置失败:', error)
      ElMessage.error('保存配置失败')
    }
  } finally {
    config.saving = false
  }
}

// 重置单个配置
const handleResetConfig = (config) => {
  const original = originalConfigs.value[config.key]
  if (original) {
    config.value = original.value
    config.changed = false
  }
}

// 批量保存
const handleBatchSave = async () => {
  try {
    const changedConfigs = currentConfigs.value.filter(config => config.changed)
    if (changedConfigs.length === 0) {
      ElMessage.warning('没有需要保存的配置')
      return
    }
    
    await ElMessageBox.confirm(
      `确定要保存 ${changedConfigs.length} 个配置项的修改吗？`,
      '批量保存确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    saving.value = true
    
    const configMap = {}
    changedConfigs.forEach(config => {
      configMap[config.key] = {
        value: config.value,
        description: config.description
      }
    })
    
    // TODO: 配置灰度发布 - 重要配置先在测试环境验证再发布到生产
    await systemApi.batchUpdateConfigs(configMap)
    
    // 更新本地状态
    changedConfigs.forEach(config => {
      config.changed = false
      originalConfigs.value[config.key] = { ...config }
    })
    
    ElMessage.success('批量保存成功')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量保存失败:', error)
      ElMessage.error('批量保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 刷新当前分组配置
const handleRefresh = () => {
  loadSystemConfigs(activeGroup.value)
}

// 工具函数
const getGroupLabel = (group) => {
  const groupMap = {
    basic: '基础配置',
    payment: '支付配置',
    notification: '通知配置',
    security: '安全配置',
    cache: '缓存配置',
    upload: '上传配置'
  }
  return groupMap[group] || group
}

const getConfigType = (key) => {
  // 根据配置键名判断输入类型
  if (key.includes('enable') || key.includes('switch') || key.endsWith('_flag')) {
    return 'boolean'
  }
  if (key.includes('count') || key.includes('size') || key.includes('limit') || key.includes('timeout')) {
    return 'number'
  }
  if (key.includes('type') || key.includes('mode') || key.includes('level')) {
    return 'select'
  }
  if (key.includes('config') || key.includes('json') || key.includes('template')) {
    return 'textarea'
  }
  return 'text'
}

const getConfigOptions = (key) => {
  // 根据配置键名返回选项
  const optionsMap = {
    log_level: [
      { label: 'DEBUG', value: 'debug' },
      { label: 'INFO', value: 'info' },
      { label: 'WARN', value: 'warn' },
      { label: 'ERROR', value: 'error' }
    ],
    cache_type: [
      { label: 'Redis', value: 'redis' },
      { label: '内存', value: 'memory' },
      { label: '文件', value: 'file' }
    ]
  }
  return optionsMap[key] || []
}

const isDangerousConfig = (key) => {
  const dangerousKeys = [
    'database_url', 'redis_url', 'jwt_secret', 'wechat_app_secret',
    'payment_merchant_id', 'payment_api_key'
  ]
  return dangerousKeys.some(dangerousKey => key.includes(dangerousKey))
}

// 生命周期
onMounted(() => {
  loadConfigGroups()
  loadSystemConfigs(activeGroup.value)
})
</script>

<style lang="scss" scoped>
.system-config-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-lg;

    .page-title {
      font-size: $font-size-h2;
      font-weight: $font-weight-semibold;
      color: $text-color-primary;
      margin: 0;
    }
  }

  .group-card {
    .group-menu {
      border-right: none;

      :deep(.el-menu-item) {
        border-radius: $border-radius-base;
        margin: 2px 0;

        &:hover {
          background-color: $primary-1;
        }

        &.is-active {
          background-color: $primary-1;
          color: $primary-color;
          font-weight: $font-weight-medium;
        }
      }
    }
  }

  .config-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: $font-size-h4;
      font-weight: $font-weight-medium;
      color: $text-color-primary;
    }

    .config-list {
      .config-item {
        padding: $spacing-lg;
        border-bottom: 1px solid $border-color-split;

        &:last-child {
          border-bottom: none;
        }

        .config-header {
          margin-bottom: $spacing-md;

          .config-key {
            font-size: $font-size-base;
            font-weight: $font-weight-medium;
            color: $text-color-primary;
            margin-bottom: $spacing-xs;
          }

          .config-description {
            font-size: $font-size-sm;
            color: $text-color-secondary;
          }
        }

        .config-control {
          margin-bottom: $spacing-md;

          :deep(.el-input),
          :deep(.el-input-number),
          :deep(.el-select),
          :deep(.el-textarea) {
            width: 100%;
          }
        }

        .config-actions {
          display: flex;
          justify-content: flex-end;
          gap: $spacing-sm;
        }
      }
    }
  }
}
</style>
