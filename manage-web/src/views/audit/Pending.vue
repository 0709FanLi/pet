<template>
  <div class="page">
    <el-card>
      <template #header>启事 · 待审核</template>
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
        <el-button type="primary">搜索</el-button>
      </div>
      <el-empty v-if="rows.length === 0" description="暂无待审核启事" />
      <el-table v-else :data="rows" height="520" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题/摘要" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="reward" label="悬赏" width="100" />
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default>
            <el-button type="primary" link>审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
  import { ref } from 'vue'
  const query = ref({ keyword: '', user: '', city: '' })
  const cities = ref(['北京', '上海', '广州', '深圳'])
  const rows = ref([])
</script>

<style scoped>
  .filters {
    display: flex;
    gap: 8px;
    margin-bottom: 10px;
  }
</style>
