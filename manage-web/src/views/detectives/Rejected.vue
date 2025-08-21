<template>
  <div class="page">
    <el-card>
      <template #header>侦探 · 未通过</template>
      <div class="filters">
        <el-input v-model="query.keyword" placeholder="姓名/手机号" clearable />
        <el-button type="primary" @click="fetchList(1)">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
      <el-table :data="list" height="560" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="reviewedAt" label="审核时间" width="180" />
        <el-table-column prop="reason" label="拒绝原因" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="toDetail(row)"
              >详情</el-button
            >
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无未通过记录" />
        </template>
      </el-table>
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
  const query = reactive({ page: 1, pageSize: 10, keyword: '' })
  const router = useRouter()

  const fetchList = async page => {
    if (typeof page === 'number') query.page = page
    loading.value = true
    try {
      const url = `http://192.168.1.10:8080/api/admin/detectives?page=${
        query.page
      }&pageSize=${query.pageSize}&keyword=${encodeURIComponent(
        query.keyword || ''
      )}&status=rejected`
      const resp = await fetch(url).then(r => r.json())
      const data = resp?.data || resp
      list.value = data?.list || []
      total.value = data?.total || 0
    } catch (e) {
      console.error('[detectives/rejected] fetch error:', e)
    } finally {
      loading.value = false
    }
  }

  const toDetail = row => {
    router.push(`/detectives/detail/${row.id}`)
  }

  const reset = () => {
    query.keyword = ''
    fetchList(1)
  }

  onMounted(() => fetchList(1))
</script>
<style scoped>
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
