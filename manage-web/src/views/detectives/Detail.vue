<template>
  <div class="page">
    <el-page-header @back="back" content="侦探详情" />
    <div class="gap" />
    <el-row :gutter="12">
      <el-col :span="16">
        <el-card class="block">
          <template #header>基本信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{
              d.realName
            }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{
              d.phone
            }}</el-descriptions-item>
            <el-descriptions-item label="城市">{{
              d.city
            }}</el-descriptions-item>
            <el-descriptions-item label="团队规模">{{
              d.teamSize || '-'
            }}</el-descriptions-item>
            <el-descriptions-item label="从业经验(年)">{{
              d.experienceYears || '-'
            }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag
                :type="
                  d.status === 'approved'
                    ? 'success'
                    : d.status === 'rejected'
                    ? 'danger'
                    : 'info'
                "
              >
                {{
                  d.status === 'approved'
                    ? '通过'
                    : d.status === 'rejected'
                    ? '不通过'
                    : '下架/待审'
                }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{
              d.createdAt
            }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card class="block">
          <template #header>设备与服务</template>
          <div class="kv">
            设备：{{ parseArr(d.devices).join('、') || '-' }}
          </div>
          <div class="kv">
            服务区域：{{ parseArr(d.serviceAreas).join('、') || '-' }}
          </div>
          <div class="kv">
            可服务时间：{{ parseArr(d.availableTimes).join('、') || '-' }}
          </div>
        </el-card>
        <el-card class="block">
          <template #header>简介</template>
          <el-input
            v-model="d.bio"
            type="textarea"
            :autosize="{ minRows: 5 }"
            readonly
          />
        </el-card>
        <el-card class="block">
          <template #header>证件与照片</template>
          <div class="images">
            <el-image
              v-if="d.idCardFront"
              :src="resolve(d.idCardFront)"
              fit="cover"
              style="width: 160px; height: 120px"
            />
            <el-image
              v-if="d.idCardBack"
              :src="resolve(d.idCardBack)"
              fit="cover"
              style="width: 160px; height: 120px"
            />
            <el-image
              v-for="(p, idx) in parseArr(d.devicePhotos)"
              :key="idx"
              :src="resolve(p)"
              fit="cover"
              style="width: 160px; height: 120px"
            />
            <el-image
              v-for="(p, idx) in parseArr(d.certificates)"
              :key="'c' + idx"
              :src="resolve(p)"
              fit="cover"
              style="width: 160px; height: 120px"
            />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="block">
          <template #header>审核操作</template>
          <el-radio-group v-model="action">
            <el-radio-button label="approved">通过</el-radio-button>
            <el-radio-button label="rejected">不通过</el-radio-button>
            <el-radio-button label="disabled">下架</el-radio-button>
          </el-radio-group>
          <div v-if="action === 'rejected'" class="mt">
            <el-input
              v-model="reason"
              type="textarea"
              :rows="4"
              placeholder="请填写不通过原因"
            />
          </div>
          <div class="op">
            <el-button @click="back">返回</el-button>
            <el-button type="primary" :loading="submitting" @click="submit"
              >提交</el-button
            >
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'

  const BASE = 'http://192.168.1.18:8080'
  const route = useRoute()
  const router = useRouter()
  const id = route.params.id
  const d = ref({})
  const action = ref('approved')
  const reason = ref('')
  const submitting = ref(false)

  const back = () => router.back()
  const resolve = p => (p && p.startsWith('/uploads/') ? `${BASE}${p}` : p)
  const parseArr = str => {
    try {
      const a = JSON.parse(str || '[]')
      return Array.isArray(a) ? a : []
    } catch (e) {
      return []
    }
  }

  const load = async () => {
    const resp = await fetch(`${BASE}/api/admin/detectives/${id}`).then(r =>
      r.json()
    )
    const data = resp?.data || resp
    d.value = data
  }

  const submit = async () => {
    if (submitting.value) return
    if (action.value === 'rejected' && !reason.value.trim()) {
      ElMessage.warning('请填写不通过原因')
      return
    }
    submitting.value = true
    try {
      const url = `${BASE}/api/admin/detectives/${id}/${action.value}`
      await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body:
          action.value === 'rejected'
            ? JSON.stringify({ reason: reason.value })
            : undefined,
      })
      ElMessage.success('已提交')
      router.replace('/detectives/list')
    } catch (e) {
      ElMessage.error('提交失败')
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
  .kv {
    margin: 6px 0;
  }
  .mt {
    margin-top: 10px;
  }
  .op {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 10px;
  }
</style>
