<template>
  <div class="page">
    <el-card>
      <template #header>侦探 · 列表</template>
      <div class="filters">
        <el-input v-model="query.keyword" placeholder="姓名/手机号" clearable />
        <el-select v-model="query.status" clearable placeholder="状态">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="通过" value="approved" />
          <el-option label="不通过" value="rejected" />
          <el-option label="下架" value="disabled" />
        </el-select>
        <el-button type="primary" @click="fetchList(1)">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
      <el-table :data="list" height="560" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="28" :src="row.avatar || avatarDefault" />
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === 'approved'
                  ? 'success'
                  : row.status === 'rejected'
                  ? 'danger'
                  : 'info'
              "
            >
              {{
                row.status === 'approved'
                  ? '通过'
                  : row.status === 'rejected'
                  ? '不通过'
                  : '下架'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orders" label="接单数" width="100" />
        <el-table-column prop="successRate" label="成功率" width="100" />
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button link type="primary" @click="view(row)">详情</el-button>
            <el-button
              link
              type="success"
              @click="approve(row)"
              :disabled="row.status === 'approved'"
              >通过</el-button
            >
            <el-button link type="warning" @click="reject(row)"
              >不通过</el-button
            >
            <el-button
              link
              type="info"
              @click="disableDetective(row)"
              :disabled="row.status === 'disabled'"
              >下架</el-button
            >
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>
      <el-dialog v-model="review.visible" title="不通过原因" width="420px">
        <el-input
          v-model="review.reason"
          type="textarea"
          :rows="5"
          placeholder="请填写不通过原因"
        />
        <template #footer>
          <el-button @click="review.visible = false">取消</el-button>
          <el-button type="primary" @click="submitReject">提交</el-button>
        </template>
      </el-dialog>
      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, jumper"
          :page-size="query.pageSize"
          :total="total"
          :current-page="query.page"
          @current-change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  const loading = ref(false)
  const list = ref([])
  const total = ref(0)
  const query = reactive({
    page: 1,
    pageSize: 10,
    keyword: '',
    status: 'pending',
  })
  const avatarDefault =
    'https://cube.elemecdn.com/0/88/03b0d41583f24d92e55f75dff792bpng.png'
  const router = useRouter()

  const fetchList = async page => {
    if (typeof page === 'number') query.page = page
    loading.value = true
    try {
      const resp = await fetch(
        `http://192.168.1.18:8080/api/admin/detectives?page=${
          query.page
        }&pageSize=${query.pageSize}&keyword=${encodeURIComponent(
          query.keyword || ''
        )}&status=${encodeURIComponent(query.status || '')}`
      ).then(r => r.json())
      const data = resp?.data || resp
      list.value = data?.list || []
      total.value = data?.total || 0
    } catch (e) {
      console.error('[detectives] fetch error:', e)
    } finally {
      loading.value = false
    }
  }

  const post = async (url, body) => {
    const resp = await fetch(url, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: body ? JSON.stringify(body) : undefined,
    })
    return resp.json()
  }

  const approve = async row => {
    await post(
      `http://192.168.1.18:8080/api/admin/detectives/${row.id}/approve`
    )
    fetchList()
  }
  const reject = async row => {
    review.id = row.id
    review.visible = true
    review.reason = ''
  }
  const disableDetective = async row => {
    await post(
      `http://192.168.1.18:8080/api/admin/detectives/${row.id}/disable`
    )
    fetchList()
  }
  const view = row => {
    router.push(`/detectives/detail/${row.id}`)
  }

  onMounted(() => fetchList(1))

  // 审核弹框
  const review = reactive({ visible: false, id: null, reason: '' })
  const submitReject = async () => {
    if (!review.id) return
    if (!review.reason.trim()) {
      return window.alert('请填写不通过原因')
    }
    await post(
      `http://192.168.1.18:8080/api/admin/detectives/${review.id}/reject`,
      { reason: review.reason }
    )
    review.visible = false
    fetchList()
  }

  const reset = () => {
    query.keyword = ''
    query.status = 'pending'
    fetchList(1)
  }
</script>

<style scoped>
  .page {
    padding: 12px;
  }
  .filters {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
  }
  .pager {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }
</style>
