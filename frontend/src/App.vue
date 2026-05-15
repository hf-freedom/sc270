<template>
  <el-container style="height: 100vh">
    <el-header style="background: #007bff; color: white; display: flex; align-items: center; justify-content: space-between">
      <h2 style="margin: 0">客服坐席智能分配系统</h2>
      <el-button type="primary" @click="refreshData" :icon="Refresh">刷新数据</el-button>
    </el-header>
    
    <el-container>
      <el-aside width="350px" style="background: #f5f5f5; padding: 20px">
        <el-card shadow="hover" style="margin-bottom: 20px">
          <template #header>
            <div class="card-header">
              <span>用户咨询入口</span>
            </div>
          </template>
          <el-form :model="sessionForm" label-width="80px" size="small">
            <el-form-item label="用户ID">
              <el-input v-model="sessionForm.userId" />
            </el-form-item>
            <el-form-item label="用户名">
              <el-input v-model="sessionForm.userName" />
            </el-form-item>
            <el-form-item label="VIP用户">
              <el-switch v-model="sessionForm.isVip" />
            </el-form-item>
            <el-form-item label="问题类型">
              <el-select v-model="sessionForm.problemType" style="width: 100%">
                <el-option label="咨询" value="CONSULT" />
                <el-option label="投诉" value="COMPLAINT" />
                <el-option label="技术支持" value="TECHNICAL" />
                <el-option label="账单问题" value="BILLING" />
                <el-option label="退款" value="REFUND" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
            <el-form-item label="问题描述">
              <el-input type="textarea" v-model="sessionForm.problemDesc" :rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="createSession" style="width: 100%">提交咨询</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统概览</span>
            </div>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="坐席总数">{{ stats.totalAgents }}</el-descriptions-item>
            <el-descriptions-item label="在线坐席">
              <el-tag type="success">{{ stats.onlineAgents }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="忙碌坐席">
              <el-tag type="warning">{{ stats.busyAgents }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="离线坐席">
              <el-tag type="danger">{{ stats.offlineAgents }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="平均负载">{{ stats.averageLoad?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="排队数量">
              <el-badge :value="stats.queueLength" class="item" />
            </el-descriptions-item>
            <el-descriptions-item label="VIP排队">
              <el-tag type="warning">{{ stats.vipQueueLength }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理中会话">{{ stats.processingSessions }}</el-descriptions-item>
            <el-descriptions-item label="超时次数">
              <el-tag type="danger">{{ stats.timeoutCount }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>分配日志</span>
              <el-button size="mini" text @click="assignmentLogs = []">清空</el-button>
            </div>
          </template>
          <div style="max-height: 300px; overflow-y: auto; font-size: 12px">
            <div v-if="assignmentLogs.length === 0" style="color: #999; text-align: center; padding: 20px">
              暂无分配记录
            </div>
            <div v-for="(log, index) in assignmentLogs" :key="index" style="padding: 8px 0; border-bottom: 1px solid #eee; color: #666">
              {{ log }}
            </div>
          </div>
        </el-card>
      </el-aside>

      <el-main style="padding: 20px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="坐席状态" name="agents">
            <el-row :gutter="20">
              <el-col :span="8" v-for="agent in agents" :key="agent.agentId">
                <el-card shadow="hover" style="margin-bottom: 20px">
                  <template #header>
                    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center">
                      <span>{{ agent.agentName }}</span>
                      <el-tag :type="getAgentStatusType(agent.status)">
                        {{ getAgentStatusText(agent.status) }}
                      </el-tag>
                    </div>
                  </template>
                  <el-descriptions :column="1" size="small">
                    <el-descriptions-item label="当前负载">
                      <el-progress 
                        :percentage="(agent.currentLoad / agent.maxCapacity) * 100" 
                        :status="agent.currentLoad >= agent.maxCapacity ? 'exception' : ''"
                      />
                      <div style="margin-top: 5px">
                        <span :style="{ color: agent.currentLoad >= agent.maxCapacity ? '#f56c6c' : '#67c23a', fontWeight: 'bold' }">
                          {{ agent.currentLoad }}/{{ agent.maxCapacity }}
                        </span>
                        <el-tag v-if="agent.currentLoad >= agent.maxCapacity" size="small" type="danger" style="margin-left: 10px">
                          已满载
                        </el-tag>
                        <el-tag v-else-if="agent.currentLoad > 0" size="small" type="success" style="margin-left: 10px">
                          可接待
                        </el-tag>
                        <el-tag v-else size="small" style="margin-left: 10px">
                          空闲
                        </el-tag>
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item label="技能组">
                      <el-tag size="small" v-for="gid in agent.skillGroupIds" :key="gid" style="margin: 2px">{{ gid }}</el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                  <div style="margin-top: 10px">
                    <el-button v-if="agent.status === 'OFFLINE'" size="small" type="success" @click="setAgentOnline(agent.agentId)">上线</el-button>
                    <el-button v-if="agent.status === 'ONLINE' || agent.status === 'BUSY'" size="small" type="danger" @click="setAgentOffline(agent.agentId)">下线</el-button>
                  </div>
                  <div v-if="agent.activeSessionIds.length > 0" style="margin-top: 10px">
                    <div style="font-weight: bold; margin-bottom: 5px">活跃会话:</div>
                    <div v-for="sid in agent.activeSessionIds" :key="sid" style="padding: 5px; background: #f0f0f0; margin: 5px 0; border-radius: 4px; font-size: 12px">
                      {{ sid.substring(0, 8) }}...
                      <el-button size="mini" type="primary" @click="responseSession(sid)" style="margin-left: 10px">响应</el-button>
                      <el-button size="mini" type="danger" @click="closeSession(sid)" style="margin-left: 5px">关闭</el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="等待队列" name="queue">
            <el-table :data="queueSessions" border stripe>
              <el-table-column prop="priority" label="优先级" width="100" sortable>
                <template #default="{ row }">
                  <el-tag :type="row.priority > 50 ? 'danger' : row.priority > 30 ? 'warning' : 'info'">
                    {{ row.priority }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="user.userName" label="用户" width="140">
                <template #default="{ row }">
                  <span>{{ row.user?.userName }}</span>
                  <el-tag v-if="row.vip" type="warning" size="small" style="margin-left: 5px">VIP</el-tag>
                  <el-tag v-if="row.mergedSessionIds?.length > 0" type="info" size="small" style="margin-left: 5px">
                    合并{{ row.mergedSessionIds.length }}条
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="problemType" label="问题类型" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.problemType === 'COMPLAINT' ? 'danger' : 'info'" size="small">
                    {{ getProblemTypeText(row.problemType) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip />
              <el-table-column prop="skillGroupId" label="技能组" width="100" />
              <el-table-column prop="createTime" label="入队时间" width="180">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="所有会话" name="sessions">
            <el-table :data="allSessions" border stripe>
              <el-table-column prop="sessionId" label="会话ID" width="150">
                <template #default="{ row }">
                  {{ row.sessionId.substring(0, 12) }}...
                </template>
              </el-table-column>
              <el-table-column prop="user.userName" label="用户" width="140">
                <template #default="{ row }">
                  {{ row.user?.userName }}
                  <el-tag v-if="row.mergedSessionIds?.length > 0" type="info" size="small" style="margin-left: 5px">
                    合并{{ row.mergedSessionIds.length }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="problemType" label="问题类型" width="100">
                <template #default="{ row }">
                  {{ getProblemTypeText(row.problemType) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getSessionStatusType(row.status)" size="small">
                    {{ getSessionStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="assignedAgentId" label="坐席" width="100" />
              <el-table-column prop="priority" label="优先级" width="80" />
              <el-table-column label="超时" width="80">
                <template #default="{ row }">
                  <el-tag v-if="row.timeoutCount > 0" type="danger" size="small">{{ row.timeoutCount }}次</el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button v-if="row.status === 'ASSIGNED' || row.status === 'PROCESSING'" size="small" type="primary" @click="responseSession(row.sessionId)">响应</el-button>
                  <el-button v-if="row.status !== 'CLOSED' && row.status !== 'QUALITY_CHECK'" size="small" type="danger" @click="closeSession(row.sessionId)">关闭</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="质检列表" name="quality">
            <el-table :data="qualitySessions" border stripe>
              <el-table-column prop="sessionId" label="会话ID" width="150" />
              <el-table-column prop="user.userName" label="用户" width="100" />
              <el-table-column prop="problemType" label="问题类型" width="120">
                <template #default="{ row }">
                  {{ getProblemTypeText(row.problemType) }}
                </template>
              </el-table-column>
              <el-table-column prop="problemDesc" label="问题描述" />
              <el-table-column prop="assignedAgentId" label="处理坐席" width="100" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

const activeTab = ref('agents')
const stats = ref({})
const agents = ref([])
const allSessions = ref([])
const queueSessions = ref([])
const qualitySessions = ref([])

const sessionForm = reactive({
  userId: 'u' + Math.floor(Math.random() * 1000),
  userName: '用户' + Math.floor(Math.random() * 100),
  isVip: false,
  phone: '',
  email: '',
  problemType: 'CONSULT',
  problemDesc: ''
})

const refreshData = async () => {
  try {
    const [statsRes, agentsRes, sessionsRes, queueRes, qualityRes] = await Promise.all([
      axios.get('/api/stats'),
      axios.get('/api/agent/all'),
      axios.get('/api/session/all'),
      axios.get('/api/session/queue'),
      axios.get('/api/session/quality-check')
    ])
    
    stats.value = statsRes.data.data || {}
    agents.value = agentsRes.data.data || []
    allSessions.value = sessionsRes.data.data || []
    queueSessions.value = queueRes.data.data || []
    qualitySessions.value = qualityRes.data.data || []
  } catch (e) {
    console.error('刷新数据失败', e)
  }
}

const assignmentLogs = ref([])

const createSession = async () => {
  try {
    const res = await axios.post('/api/session/create', sessionForm)
    const session = res.data.data
    
    let logMsg = `[${new Date().toLocaleTimeString()}] 用户「${session.user.userName}」提交「${getProblemTypeText(session.problemType)}」`
    
    if (session.mergedSessionIds && session.mergedSessionIds.length > 0) {
      logMsg += ` | 自动合并历史会话 ${session.mergedSessionIds.length} 条`
    }
    
    if (session.status === 'ASSIGNED') {
      const agent = agents.value.find(a => a.agentId === session.assignedAgentId)
      logMsg += ` → 分配给坐席「${agent?.agentName || session.assignedAgentId}」`
    } else if (session.status === 'QUEUING') {
      logMsg += ` → 进入等待队列`
    }
    
    assignmentLogs.value.unshift(logMsg)
    if (assignmentLogs.value.length > 20) assignmentLogs.value.pop()
    
    sessionForm.problemDesc = ''
    sessionForm.userId = 'u' + Math.floor(Math.random() * 1000)
    sessionForm.userName = '用户' + Math.floor(Math.random() * 100)
    await refreshData()
  } catch (e) {
    console.error('创建会话失败', e)
  }
}

const responseSession = async (sessionId) => {
  try {
    await axios.post(`/api/session/response/${sessionId}`)
    await refreshData()
  } catch (e) {
    console.error('响应失败', e)
  }
}

const closeSession = async (sessionId) => {
  try {
    await axios.post(`/api/session/close/${sessionId}`)
    await refreshData()
  } catch (e) {
    console.error('关闭失败', e)
  }
}

const setAgentOnline = async (agentId) => {
  try {
    await axios.post(`/api/agent/online/${agentId}`)
    await refreshData()
  } catch (e) {
    console.error('上线失败', e)
  }
}

const setAgentOffline = async (agentId) => {
  try {
    await axios.post(`/api/agent/offline/${agentId}`)
    await refreshData()
  } catch (e) {
    console.error('下线失败', e)
  }
}

const getAgentStatusType = (status) => {
  const map = { ONLINE: 'success', BUSY: 'warning', OFFLINE: 'danger', AWAY: 'info' }
  return map[status] || 'info'
}

const getAgentStatusText = (status) => {
  const map = { ONLINE: '在线', BUSY: '忙碌', OFFLINE: '离线', AWAY: '离开' }
  return map[status] || status
}

const getProblemTypeText = (type) => {
  const map = { CONSULT: '咨询', COMPLAINT: '投诉', TECHNICAL: '技术支持', BILLING: '账单问题', REFUND: '退款', OTHER: '其他' }
  return map[type] || type
}

const getSessionStatusType = (status) => {
  const map = { QUEUING: 'info', ASSIGNED: 'warning', PROCESSING: 'primary', TRANSFERRED: 'warning', CLOSED: 'success', QUALITY_CHECK: 'danger' }
  return map[status] || 'info'
}

const getSessionStatusText = (status) => {
  const map = { QUEUING: '排队中', ASSIGNED: '已分配', PROCESSING: '处理中', TRANSFERRED: '已转派', CLOSED: '已关闭', QUALITY_CHECK: '质检中' }
  return map[status] || status
}

const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  return new Date(timestamp).toLocaleString('zh-CN')
}

onMounted(() => {
  refreshData()
  setInterval(refreshData, 5000)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}
.card-header {
  font-weight: bold;
}
</style>
