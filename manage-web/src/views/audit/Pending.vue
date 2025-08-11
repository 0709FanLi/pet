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
        <el-button type="primary" @click="fetchList(1)">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
      <el-empty
        v-if="rows.length === 0 && !loading"
        description="暂无待审核启事"
      />
      <el-table v-else :data="rows" height="520" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题/摘要" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="reward" label="悬赏" width="100" />
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" link @click="openReview(row)"
              >审核</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <el-dialog
        v-model="review.visible"
        :title="`审核 · #${review.id || ''}`"
        width="520px"
        class="review-dialog"
      >
        <div class="review-header">
          <div class="title">{{ review.title || '启事审核' }}</div>
          <el-tag type="info">待审核</el-tag>
        </div>
        <el-divider />
        <el-form
          ref="reviewFormRef"
          :model="review"
          :rules="rules"
          label-position="top"
          class="review-form"
        >
          <el-form-item label="审核结果">
            <el-radio-group v-model="review.result">
              <el-radio-button label="approved">通过</el-radio-button>
              <el-radio-button label="rejected">不通过</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="不通过原因"
            prop="reason"
            v-show="review.result === 'rejected'"
          >
            <el-input
              v-model="review.reason"
              type="textarea"
              :rows="5"
              placeholder="请填写不通过原因（必填）"
              maxlength="300"
              show-word-limit
            />
          </el-form-item>
          <el-alert
            v-if="review.result === 'rejected'"
            title="不通过必须填写原因，将展示给提交人"
            type="warning"
            show-icon
            :closable="false"
          />
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="review.visible = false">取消</el-button>
            <el-button type="primary" @click="submitReview">提交</el-button>
          </div>
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
  const loading = ref(false)
  const rows = ref([])
  const total = ref(0)
  const query = reactive({
    keyword: '',
    user: '',
    city: '',
    page: 1,
    pageSize: 10,
  })
  const cities = ref([])

  const fetchList = async page => {
    if (typeof page === 'number') query.page = page
    loading.value = true
    try {
      const kw = [query.keyword, query.user].filter(Boolean).join(' ')
      const url = `http://192.168.1.11:8080/api/admin/notices/pending?page=${
        query.page
      }&pageSize=${query.pageSize}&keyword=${encodeURIComponent(
        kw
      )}&city=${encodeURIComponent(query.city || '')}`
      const resp = await fetch(url).then(r => r.json())
      const data = resp?.data || resp
      rows.value = data?.list || []
      total.value = data?.total || 0
    } catch (e) {
      console.error('[audit/pending] fetch error:', e)
    } finally {
      loading.value = false
    }
  }

  const review = reactive({
    visible: false,
    id: null,
    title: '',
    result: 'approved',
    reason: '',
  })
  const reviewFormRef = ref(null)
  const openReview = row => {
    review.id = row.id
    review.title = row.title
    review.result = 'approved'
    review.reason = ''
    review.visible = true
  }
  const rules = {
    reason: [
      {
        validator: (_r, v, cb) => {
          if (review.result === 'rejected' && !String(v || '').trim()) {
            cb(new Error('请填写不通过原因'))
          } else cb()
        },
        trigger: 'blur',
      },
    ],
  }

  const submitReview = async () => {
    if (!review.id) return
    // 表单校验（在不通过时生效）
    if (review.result === 'rejected' && reviewFormRef.value) {
      try {
        await reviewFormRef.value.validate()
      } catch (e) {
        return
      }
    }
    const base = 'http://192.168.1.11:8080/api/admin/notices'
    const url = `${base}/${review.id}/${
      review.result === 'approved' ? 'approve' : 'reject'
    }`
    try {
      await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body:
          review.result === 'approved'
            ? undefined
            : JSON.stringify({ reason: review.reason }),
      }).then(r => r.json())
      review.visible = false
      fetchList()
    } catch (e) {
      console.error('[audit/pending] submitReview error:', e)
    }
  }

  const loadCities = async () => {
    try {
      const resp = await fetch(
        'http://192.168.1.11:8080/api/admin/notices/cities'
      ).then(r => r.json())
      cities.value = resp?.data || resp || []
    } catch (e) {
      console.error('[audit/pending] loadCities error:', e)
    }
  }

  const reset = () => {
    query.keyword = ''
    query.user = ''
    query.city = ''
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
  .review-dialog :deep(.el-dialog__header) {
    padding-bottom: 4px;
  }
  .review-header {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .review-header .title {
    font-weight: 600;
    font-size: 16px;
  }
  .review-form :deep(.el-form-item__label) {
    font-weight: 500;
  }
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
</style>
