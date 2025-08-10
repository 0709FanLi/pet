<template>
  <div class="page">
    <el-card>
      <template #header>侦探 · 列表</template>
      <div class="filters">
        <el-input v-model="query.keyword" placeholder="姓名/手机号" clearable />
        <el-select v-model="query.status" clearable placeholder="状态">
          <el-option label="全部" value="" />
          <el-option label="正常" value="active" />
          <el-option label="已下架" value="disabled" />
        </el-select>
        <el-button type="primary" @click="fetchList(1)">搜索</el-button>
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
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{
              row.status === 'active' ? '正常' : '已下架'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orders" label="接单数" width="100" />
        <el-table-column prop="successRate" label="成功率" width="100" />
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button link type="primary">详情</el-button>
            <el-button link>下架</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
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
  const loading = ref(false)
  const list = ref([])
  const total = ref(0)
  const query = reactive({ page: 1, pageSize: 10, keyword: '', status: '' })
  const avatarDefault =
    'https://cube.elemecdn.com/0/88/03b0d41583f24d92e55f75dff792bpng.png'

  const fetchList = async page => {
    if (typeof page === 'number') query.page = page
    loading.value = true
    try {
      const resp = await fetch(
        `http://192.168.1.11:8080/api/admin/detectives?page=${
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

  onMounted(() => fetchList(1))
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
