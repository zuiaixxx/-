<script setup>
import { ref, onMounted } from 'vue'

// 模拟公告数据
const announcements = ref([
  { 
    id: 1, 
    title: '关于**系统上线的通知', 
    content: '各位校园师生：\n\n我校**系统已正式上线！该系统旨在方便师生们报修校园物流设施，提高维修效率。从今日起，所有校园物流设施的保修都将通过该系统进行。请各位师生积极使用，如有问题可随时反馈。\n\n感谢各位的配合！', 
    author: '校园物流管理中心',
    date: '2023-09-01',
    important: true
  },
  { 
    id: 2, 
    title: '保修流程操作指南', 
    content: '为了帮助大家更好地使用**系统，现发布《保修流程操作指南》。\n\n1. 登录系统：使用校园统一身份认证登录\n2. 填写信息：详细描述故障情况，上传相关照片\n3. 提交申请：确认信息无误后提交\n4. 等待处理：后台会及时安排维修人员\n5. 查看进度：可在个人中心查看维修进度\n\n请各位按照流程操作，以确保您的报修能够得到及时处理。', 
    author: '系统管理员',
    date: '2023-09-05',
    important: false
  },
  { 
    id: 3, 
    title: '十月份物流设施定期维护通知', 
    content: '为确保校园物流设施的正常运行，我们将于10月15日至10月20日对校内所有物流设施进行定期维护。\n\n维护期间，部分区域的物流设施可能会暂时无法使用。我们将尽量减少对大家日常使用的影响。\n\n如有特殊需求，请提前联系物流管理中心。', 
    author: '物业管理部门',
    date: '2023-10-08',
    important: true
  },
  { 
    id: 4, 
    title: '**系统功能更新公告', 
    content: '亲爱的用户：\n\n我们很高兴地通知您，**系统已完成新一轮的功能更新，新增以下功能：\n\n1. 紧急报修功能：增加紧急报修通道，提高处理优先级\n2. 维修进度实时查询：可实时了解维修进展\n3. 满意度评价：完成维修后可进行评价\n4. 历史记录查询：查看历史报修记录更方便\n\n我们期待您的使用反馈！', 
    author: '系统开发团队',
    date: '2023-10-12',
    important: false
  },
  { 
    id: 5, 
    title: '关于提高报修信息准确性的倡议', 
    content: '各位用户：\n\n近期我们发现部分报修信息描述不够准确，导致维修人员无法准确判断故障原因，影响维修效率。\n\n在此，我们倡议大家在提交报修申请时：\n\n1. 清晰描述故障现象\n2. 提供物流设施的具体位置\n3. 上传清晰的故障照片\n4. 留下准确的联系方式\n\n感谢大家的配合！', 
    author: '物流维修团队',
    date: '2023-10-18',
    important: false
  }
])

// 当前查看的公告
const currentAnnouncement = ref(null)
// 控制公告详情对话框的显示
const dialogVisible = ref(false)

// 查看公告详情
const viewAnnouncementDetail = (announcement) => {
  currentAnnouncement.value = announcement
  dialogVisible.value = true
}
</script>

<template>
  <div class="announcement-container">
    <h1>系统公告</h1>
    
    <!-- 公告列表 -->
    <el-table
      :data="announcements"
      style="width: 100%"
      row-key="id"
      @row-click="viewAnnouncementDetail"
    >
      <el-table-column label="重要" width="80" align="center">
        <template #default="scope">
          <el-icon v-if="scope.row.important" style="color: #F56C6C">
            <Star />
          </el-icon>
        </template>
      </el-table-column>
      
      <el-table-column prop="title" label="标题">
        <template #default="scope">
          <span :class="{ 'important-announcement': scope.row.important }">
            {{ scope.row.title }}
          </span>
        </template>
      </el-table-column>
      
      <el-table-column prop="author" label="发布者" width="180"></el-table-column>
      
      <el-table-column prop="date" label="发布日期" width="160"></el-table-column>
      
      <el-table-column label="操作" width="120" align="center">
        <template #default="scope">
          <el-button 
            link 
            type="primary" 
            size="small"
            @click.stop="viewAnnouncementDetail(scope.row)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentAnnouncement?.title"
      width="60%"
    >
      <div class="announcement-info">
        <span>发布者：{{ currentAnnouncement?.author }}</span>
        <span>发布日期：{{ currentAnnouncement?.date }}</span>
      </div>
      
      <div class="announcement-content">
        <pre>{{ currentAnnouncement?.content }}</pre>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.announcement-container {
  padding: 20px;
}

h1 {
  margin-bottom: 20px;
  font-size: 24px;
}

.important-announcement {
  font-weight: bold;
  color: #F56C6C;
}

.announcement-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
}

.announcement-content {
  padding: 20px;
  background-color: #f8f8f8;
  border-radius: 4px;
  line-height: 1.5;
}

pre {
  white-space: pre-wrap;
  font-family: inherit;
}
</style> 