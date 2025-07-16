<template>
    <div class="not-found-container">
        <!-- Header部分：logo和应用名称 -->
        <div class="header-section">
            <div class="logo-section">
                <l-img :src="appLogo" w="50px" h="50px" />
                <span class="app-name">{{ appTitle }}</span>
            </div>
        </div>

        <!-- Main内容部分 -->
        <div class="main-section">
            <div class="content-wrapper">
                <h1 class="error-title">{{ t('pageNotFound') }}</h1>
                <p class="error-description">
                    {{ t('pageNotFoundDescription') }}
                </p>
                <div
                    class="return-button"
                    @click="clickReturnHome"
                >
                    {{ t('returnToHomepage') }}
            </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import LImg from '@/components/l-img.vue';
import LButton from '@/components/l-button.vue';

const router = useRouter();
const { t } = useI18n();

// 从环境变量获取应用标题和logo
const appTitle = ref(import.meta.env.VITE_APP_TITLE);
const appLogo = ref(import.meta.env.VITE_APP_LOGO);

// 返回首页
const clickReturnHome = () => {
    router.push('/');
};
</script>

<style scoped lang="scss">
.not-found-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    width: 100%;
    box-sizing: border-box;
    background: linear-gradient(180deg, #FFFFFF 0%, #F5F5F7 100%);
}

.header-section {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 66px;
    padding: 8px 0;
    background: var(--bg-primary);
}

.logo-section {
    display: flex;
    align-items: center;
    gap: 14px;
}

.app-name {
    font-weight: 500;
    font-size: 22px;
    line-height: 26px;
    letter-spacing: 0.05em;
    text-transform: uppercase;
    color: var(--text-title);
}

.main-section {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 40px;
    background: var(--bg-primary);
}

.content-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    max-width: 609px;
    text-align: center;
}

.error-title {
    font-weight: 600;
    font-size: 30px;
    line-height: 36px;
    color: var(--text-title);
    margin: 0 0 30px 0;
}

.error-description {
    font-weight: 400;
    font-size: 16px;
    line-height: 21px;
    color: var(--text-description);
    margin: 0 0 60px 0;
    max-width: 100%;
}

.return-button {
    padding: 15px 20px;
    border-radius: 9999px;
    background-color: var(--bg-secondary);
    border: 1px solid var(--bg-secondary);
    font-weight: 500;
    font-size: 16px;
    line-height: 19px;
    color: var(--text-title);
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
        background-color: var(--text-title);
        color: var(--bg-secondary);
    }
}

/* 响应式设计 */
@media (max-width: 768px) {
    .main-section {
        padding: 0 20px;
    }

    .error-title {
        font-size: 24px;
        line-height: 30px;
    }

    .error-description {
        font-size: 14px;
        line-height: 18px;
        margin-bottom: 40px;
    }

    .app-name {
        font-size: 18px;
    }
}

@media (max-width: 480px) {
    .logo-section {
        gap: 10px;
    }

    .app-name {
        font-size: 16px;
    }

    .error-title {
        font-size: 20px;
        line-height: 26px;
        margin-bottom: 20px;
    }

    .error-description {
        margin-bottom: 30px;
    }
}
</style>