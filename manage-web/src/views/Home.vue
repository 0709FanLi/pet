<template>
  <div class="layout">
    <el-container>
      <el-aside :width="collapsed ? '64px' : '220px'" class="sider light">
        <div class="brand" @click="toggle">
          <svg class="brand-logo" viewBox="0 0 64 64" aria-label="logo">
            <defs>
              <linearGradient id="g1" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0%" stop-color="#5B8FF9" />
                <stop offset="100%" stop-color="#61D5FF" />
              </linearGradient>
            </defs>
            <g fill="url(#g1)">
              <circle cx="20" cy="18" r="6" />
              <circle cx="32" cy="14" r="6" />
              <circle cx="44" cy="18" r="6" />
              <circle cx="26" cy="30" r="7" />
              <circle cx="38" cy="30" r="7" />
              <ellipse cx="32" cy="44" rx="16" ry="12" />
            </g>
          </svg>
          <span class="brand-text" v-show="!collapsed">管理后台</span>
        </div>
        <el-scrollbar class="sider-scroll">
          <el-menu
            :default-active="activeMenu"
            class="menu"
            router
            unique-opened
            :collapse="collapsed"
          >
            <el-sub-menu index="audit">
              <template #title>
                <el-icon><document-checked /></el-icon>
                <span v-show="!collapsed">启事审核</span>
              </template>
              <el-menu-item index="/audit/pending">待审核列表</el-menu-item>
              <el-menu-item index="/audit/history">审核历史</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="ops">
              <template #title>
                <el-icon><promotion /></el-icon>
                <span v-show="!collapsed">运营位管理</span>
              </template>
              <el-menu-item index="/ops/pin">置顶/推荐</el-menu-item>
              <el-menu-item index="/ops/block">屏蔽管理</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="detectives">
              <template #title>
                <el-icon><user /></el-icon>
                <span v-show="!collapsed">侦探管理</span>
              </template>
              <el-menu-item index="/detectives/list">侦探列表</el-menu-item>
              <el-menu-item index="/detectives/rejected"
                >未通过列表</el-menu-item
              >
            </el-sub-menu>

            <el-sub-menu index="orders">
              <template #title>
                <el-icon><tickets /></el-icon>
                <span v-show="!collapsed">订单管理</span>
              </template>
              <el-menu-item index="/orders/list">订单列表</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="stats">
              <template #title>
                <el-icon><data-analysis /></el-icon>
                <span v-show="!collapsed">数据统计</span>
              </template>
              <el-menu-item index="/stats/overview">数据概览</el-menu-item>
              <el-menu-item index="/stats/details">详细统计</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="settings">
              <template #title>
                <el-icon><setting /></el-icon>
                <span v-show="!collapsed">系统设置</span>
              </template>
              <el-menu-item index="/settings/base">基础配置</el-menu-item>
              <el-menu-item index="/settings/review">审核配置</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-icon class="trigger" @click="toggle"
              ><fold v-if="!collapsed" /><expand v-else
            /></el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item v-for="(m, idx) in crumbs" :key="idx">{{
                m
              }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-avatar size="small">A</el-avatar>
            <span class="user" title="Admin">Admin</span>
          </div>
        </el-header>
        <el-main class="content">
          <div class="page-wrap">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
  import { computed, ref } from 'vue'
  import { useRoute } from 'vue-router'
  import {
    DocumentChecked,
    Promotion,
    User,
    Tickets,
    DataAnalysis,
    Setting,
    Fold,
    Expand,
  } from '@element-plus/icons-vue'

  const route = useRoute()
  const activeMenu = computed(() => route.path)
  const collapsed = ref(false)
  const toggle = () => {
    collapsed.value = !collapsed.value
  }
  const crumbs = computed(() => {
    const segs = route.path.split('/').filter(Boolean)
    if (segs.length === 0) return ['概览']
    const map = {
      audit: '启事审核',
      ops: '运营位管理',
      detectives: '侦探管理',
      orders: '订单管理',
      stats: '数据统计',
      settings: '系统设置',
      pending: '待审核',
      history: '审核历史',
      pin: '置顶/推荐',
      block: '屏蔽管理',
      list: '列表',
      overview: '数据概览',
      details: '详细统计',
      base: '基础配置',
      review: '审核配置',
    }
    return segs.map(s => map[s] || s)
  })
</script>

<style scoped>
  .layout {
    height: 100vh;
    width: 100vw;
  }
  .sider {
    background: #0f172a;
    color: #cbd5e1;
    display: flex;
    flex-direction: column;
  }
  /* 浅色侧边栏样式 */
  .sider.light {
    background: #ffffff;
    color: #303133;
    border-right: 1px solid #eef0f3;
  }
  .sider.light .brand-text {
    color: #303133;
  }
  .sider.light .menu {
    background: #ffffff;
  }
  .sider.light :deep(.el-menu-item),
  .sider.light :deep(.el-sub-menu__title) {
    color: #303133;
  }
  .sider.light :deep(.is-active) {
    background: #f0f6ff;
    color: #1f77ff;
  }
  .brand {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
  }
  .brand-logo {
    width: 28px;
    height: 28px;
    border-radius: 6px;
  }
  .brand-text {
    font-weight: 600;
    font-size: 14px;
    color: #e2e8f0;
  }
  .menu {
    border-right: none;
    background: transparent;
  }
  .sider-scroll {
    flex: 1;
  }
  .header {
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  }
  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .trigger {
    cursor: pointer;
  }
  .title {
    font-weight: 600;
  }
  .content {
    background: #fff;
    padding: 16px;
    height: calc(100vh - 56px);
  }
  .page-wrap {
    min-height: 100%;
  }
</style>
