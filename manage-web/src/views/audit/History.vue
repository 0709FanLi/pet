<template>
  <div class="page">
    <el-card>
      <template #header>启事 · 审核历史</template>
      <div class="filters">
        <el-input
          v-model="query.keyword"
          placeholder="标题/关键词"
          clearable
          style="width: 220px"
        />
        <el-input
          v-model="query.user"
          placeholder="发布人/手机号"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="query.city"
          clearable
          placeholder="城市"
          style="width: 160px"
        >
          <el-option v-for="c in cities" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select
          v-model="query.result"
          clearable
          placeholder="结果"
          style="width: 140px"
        >
          <el-option label="全部" value="" />
          <el-option label="通过" value="approved" />
          <el-option label="不通过" value="rejected" />
        </el-select>
        <el-button type="primary" @click="fetchList(1)">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
      <el-table :data="rows" height="520" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题/摘要" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === 'approved' ? 'success' : 'danger'">{{
              row.result === 'approved' ? '通过' : '不通过'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewer" label="审核人" width="120" />
        <el-table-column prop="reviewedAt" label="审核时间" width="180" />
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
  const rows = ref([])
  const total = ref(0)
  const query = reactive({
    keyword: '',
    user: '',
    city: '',
    result: '',
    page: 1,
    pageSize: 10,
  })
  const cities = ref([])

  const fetchList = async page => {
    if (typeof page === 'number') query.page = page
    loading.value = true
    try {
      const kw = [query.keyword, query.user].filter(Boolean).join(' ')
      const url = `http://192.168.1.11:8080/api/admin/notices/history?page=${
        query.page
      }&pageSize=${query.pageSize}&keyword=${encodeURIComponent(
        kw
      )}&city=${encodeURIComponent(
        query.city || ''
      )}&result=${encodeURIComponent(query.result || '')}`
      const resp = await fetch(url).then(r => r.json())
      const data = resp?.data || resp
      rows.value = data?.list || []
      total.value = data?.total || 0
    } catch (e) {
      console.error('[audit/history] fetch error:', e)
    } finally {
      loading.value = false
    }
  }

  const loadCities = async () => {
    try {
      const resp = await fetch(
        'http://192.168.1.11:8080/api/admin/notices/cities'
      ).then(r => r.json())
      cities.value = resp?.data || resp || []
    } catch (e) {
      console.error('[audit/history] loadCities error:', e)
    }
  }

  const reset = () => {
    query.keyword = ''
    query.user = ''
    query.city = ''
    query.result = ''
    fetchList(1)
  }

  onMounted(() => {
    loadCities()
    fetchList(1)
  })
</script>
<style scoped>
  .filters {
    display: flex;
    gap: 8px;
    margin-bottom: 10px;
  }
  .pager {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }
</style>
