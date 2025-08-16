<template>
  <div class="page">
    <el-page-header @back="goBack" content="启事审核详情" />
    <div class="gap" />
    <el-row :gutter="12">
      <el-col :span="16">
        <el-card class="block">
          <template #header>启事信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="标题">{{
              detail.title
            }}</el-descriptions-item>
            <el-descriptions-item label="发布人"
              >{{ detail.user?.username }} /
              {{ detail.user?.id }}</el-descriptions-item
            >
            <el-descriptions-item label="手机号">{{
              detail.user?.phoneNumber
            }}</el-descriptions-item>
            <el-descriptions-item label="城市/地点">{{
              detail.city
            }}</el-descriptions-item>
            <el-descriptions-item label="悬赏">{{
              detail.reward
            }}</el-descriptions-item>
            <el-descriptions-item label="丢失时间">{{
              detail.lostTime
            }}</el-descriptions-item>
            <el-descriptions-item label="发布时间">{{
              detail.createdAt
            }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card class="block">
          <template #header>图片</template>
          <div class="images">
            <el-image
              v-for="(img, idx) in images"
              :key="idx"
              :src="resolve(img)"
              fit="cover"
              style="width: 160px; height: 120px"
              :preview-src-list="images.map(resolve)"
              :initial-index="idx"
            />
            <div v-if="images.length === 0" class="empty">暂无图片</div>
          </div>
        </el-card>
        <el-card class="block">
          <template #header>文本描述</template>
          <el-input
            v-model="detail.content"
            type="textarea"
            :autosize="{ minRows: 6 }"
            readonly
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="block">
          <template #header>审核操作</template>
          <el-form label-position="top">
            <el-form-item label="结果">
              <el-radio-group v-model="form.result">
                <el-radio label="approved">通过</el-radio>
                <el-radio label="rejected">不通过</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="不通过原因" v-if="form.result === 'rejected'">
              <el-input v-model="form.reason" type="textarea" :rows="4" />
            </el-form-item>
            <div class="op">
              <el-button @click="goBack">返回</el-button>
              <el-button
                type="primary"
                :loading="submitting"
                :disabled="submitting"
                @click="submit"
                >提交审核</el-button
              >
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'

  const route = useRoute()
  const router = useRouter()
  const id = route.params.id
  const detail = ref({ user: {} })
  const images = ref([])
  const form = ref({ result: 'approved', reason: '' })
  const submitting = ref(false)

  const BASE = 'http://192.168.1.18:8080'
  const goBack = () => router.back()
  const resolve = p => (p && p.startsWith('/uploads/') ? `${BASE}${p}` : p)

  const load = async () => {
    const resp = await fetch(`${BASE}/api/admin/notices/${id}`).then(r =>
      r.json()
    )
    const data = resp?.data || resp
    detail.value = data
    images.value = Array.isArray(data?.images) ? data.images : []
  }

  const submit = async () => {
    if (submitting.value) return
    if (form.value.result === 'rejected' && !form.value.reason.trim()) {
      ElMessage.warning('请填写不通过原因')
      return
    }
    const url = `${BASE}/api/admin/notices/${id}/${
      form.value.result === 'approved' ? 'approve' : 'reject'
    }`
    submitting.value = true
    try {
      await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body:
          form.value.result === 'approved'
            ? undefined
            : JSON.stringify({ reason: form.value.reason }),
      })
      ElMessage.success(
        form.value.result === 'approved'
          ? '审核提交成功（通过）'
          : '审核提交成功（不通过）'
      )
      router.replace('/audit/pending')
    } catch (e) {
      ElMessage.error('提交失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  }

  onMounted(load)
</script>

<style scoped>
  .gap {
    height: 8px;
  }
  .block {
    margin-bottom: 12px;
  }
  .images {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  .empty {
    color: #999;
  }
  .op {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
</style>
