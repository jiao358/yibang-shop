import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { taskApi } from '@/api/task'

export const useTaskStore = defineStore('task', () => {
  // 状态
  const tasks = ref([])
  const currentTask = ref(null)
  const recommendedTasks = ref([])
  const total = ref(0)
  const loading = ref(false)

  // 计算属性
  const availableTasks = computed(() => 
    tasks.value.filter(task => task.status === 'available')
  )
  
  const inProgressTasks = computed(() => 
    tasks.value.filter(task => task.status === 'in_progress')
  )
  
  const completedTasks = computed(() => 
    tasks.value.filter(task => task.status === 'completed')
  )

  // 方法
  const getTasks = async (params = {}) => {
    try {
      loading.value = true
      const response = await taskApi.getTasks(params)
      
      if (params.page === 1) {
        tasks.value = response.tasks
      } else {
        tasks.value = [...tasks.value, ...response.tasks]
      }
      
      total.value = response.total
      return response
    } catch (error) {
      console.error('获取任务列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getTaskDetail = async (taskId) => {
    try {
      const response = await taskApi.getTaskDetail(taskId)
      currentTask.value = response.task
      return response
    } catch (error) {
      console.error('获取任务详情失败:', error)
      throw error
    }
  }

  const claimTask = async (taskId) => {
    try {
      const response = await taskApi.claimTask(taskId)
      
      // 更新任务状态
      const taskIndex = tasks.value.findIndex(task => task.id === taskId)
      if (taskIndex !== -1) {
        tasks.value[taskIndex].status = 'claimed'
      }
      
      return response
    } catch (error) {
      console.error('领取任务失败:', error)
      throw error
    }
  }

  const completeTask = async (taskId, data) => {
    try {
      const response = await taskApi.completeTask(taskId, data)
      
      // 更新任务状态
      const taskIndex = tasks.value.findIndex(task => task.id === taskId)
      if (taskIndex !== -1) {
        tasks.value[taskIndex].status = 'completed'
        tasks.value[taskIndex].progress = 100
      }
      
      return response
    } catch (error) {
      console.error('完成任务失败:', error)
      throw error
    }
  }

  const getRecommendedTasks = async () => {
    try {
      const response = await taskApi.getRecommendedTasks()
      recommendedTasks.value = response.tasks
      return response
    } catch (error) {
      console.error('获取推荐任务失败:', error)
      throw error
    }
  }

  const getTaskStats = async () => {
    try {
      const response = await taskApi.getTaskStats()
      return response
    } catch (error) {
      console.error('获取任务统计失败:', error)
      throw error
    }
  }

  const updateTaskProgress = async (taskId, progress) => {
    try {
      const response = await taskApi.updateTaskProgress(taskId, { progress })
      
      // 更新任务进度
      const taskIndex = tasks.value.findIndex(task => task.id === taskId)
      if (taskIndex !== -1) {
        tasks.value[taskIndex].progress = progress
      }
      
      return response
    } catch (error) {
      console.error('更新任务进度失败:', error)
      throw error
    }
  }

  const abandonTask = async (taskId) => {
    try {
      const response = await taskApi.abandonTask(taskId)
      
      // 更新任务状态
      const taskIndex = tasks.value.findIndex(task => task.id === taskId)
      if (taskIndex !== -1) {
        tasks.value[taskIndex].status = 'available'
        tasks.value[taskIndex].progress = 0
      }
      
      return response
    } catch (error) {
      console.error('放弃任务失败:', error)
      throw error
    }
  }

  const getTaskHistory = async (params = {}) => {
    try {
      const response = await taskApi.getTaskHistory(params)
      return response
    } catch (error) {
      console.error('获取任务历史失败:', error)
      throw error
    }
  }

  const clearTasks = () => {
    tasks.value = []
    currentTask.value = null
    total.value = 0
  }

  return {
    // 状态
    tasks,
    currentTask,
    recommendedTasks,
    total,
    loading,
    
    // 计算属性
    availableTasks,
    inProgressTasks,
    completedTasks,
    
    // 方法
    getTasks,
    getTaskDetail,
    claimTask,
    completeTask,
    getRecommendedTasks,
    getTaskStats,
    updateTaskProgress,
    abandonTask,
    getTaskHistory,
    clearTasks
  }
})
