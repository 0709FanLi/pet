<template>
  <div class="l-send-btn" @click="handleClick">
    <template v-if="isLoading">
      <l-img w='36px' h='36px' src="/static/images/image_chat/send_stop.png" />
    </template>
    <template v-else>
      <div v-if="isCanSend">
        <div v-if="isChat">
          <l-img w='36px' h='36px' src="/static/images/image_chat/send_active.png" />
        </div>
        <div v-else>
          <l-img w='36px' h='36px' src="/static/images/image_chat/send_active_session.png" />
        </div>
      </div>
      <div v-else>
        <l-img w='36px' h='36px' src="/static/images/image_chat/send_button.png" />
      </div>
    </template>
  </div>
</template>

<script setup>
import LImg from '@/components/l-img.vue';

import { useRouter } from 'vue-router';
import { useUserStore} from "../store/modules/user";
import { getStorage, setStorage} from "../utils/storage.js";
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
import { guestMessageLimit } from "../utils/util.js";

const router = useRouter();
const userStore = useUserStore();
const { t } = useI18n();

/**
 * 发送按钮组件
 * @props
 * @property {boolean} isCanSend - 是否允许发送，true时按钮可点击，false时为禁用态
 * @property {boolean} isLoading - 是否处于分析/发送中，true时显示loading/停止图标
 * @property {boolean} isChat - 是否为聊天主场景，true显示主图标，false显示会话图标
 *
 * @emits onClickSend - 允许发送且未分析时点击触发，通知父级"发送"
 * @emits onClickStop - 允许发送且分析中时点击触发，通知父级"停止分析/发送"
 */
const props = defineProps({
  /**
   * 是否允许发送，true时按钮可点击
   */
  isCanSend: {
    type: Boolean,
    default: false
  },
  /**
   * 是否处于分析/发送中，true时显示loading/停止图标
   */
  isLoading: {
    type: Boolean,
    default: false
  },
  /**
   * 是否为聊天主场景，true显示主图标，false显示会话图标
   */
  isChat: {
    type: Boolean,
    default: true
  }
});
/**
 * @event onClickSend 允许发送且未分析时点击触发
 * @event onClickStop 允许发送且分析中时点击触发
 */

const isLoggedIn = computed(() => userStore.isLoggedIn);
const emit = defineEmits(['onClickSend', 'onClickStop']);

/**
 * 按钮点击事件处理
 * isCanSend && isLoading=true 触发onClickStop
 * isCanSend && isLoading=false 触发onClickSend
 */

// 消息数量的存储键名
const GUEST_MESSAGE_KEY = 'guest_message_count';

// 获取未登录用户发送的消息数量
const getGuestMessageCount = () => {
    return getStorage(GUEST_MESSAGE_KEY) || 0;
};

// 增加未登录用户发送的消息数量
const increaseGuestMessageCount = () => {
    const count = getGuestMessageCount();
    setStorage(GUEST_MESSAGE_KEY, count + 1);
    return count + 1;
};

// 访客可发送的最大消息数
const MAX_GUEST_MESSAGES = 3;

const handleClick = (e) => {
  // 使用封装的方法处理消息限制
  const willSend = props.isCanSend && !props.isLoading;
  const canContinue = guestMessageLimit.handleMessageLimit({
    isChat: props.isChat,
    isLoggedIn: isLoggedIn.value,
    willSend: willSend,
    message: t('limitSendMessage', {count: 1})
  });

  // 如果不允许继续，直接返回
  if (props.isChat && !isLoggedIn.value && !canContinue) {
    return;
  }

  // 处理原有的发送逻辑
  if (!props.isCanSend && props.isLoading) {
    console.log('onClickStop');
    emit('onClickStop', e);
  } else if (props.isCanSend && !props.isLoading) {
    console.log('onClickSend');
    emit('onClickSend', e);
  } else if (!props.isCanSend) {
    console.log('Not can send', props.isCanSend);
  }
}
</script>


<style scoped lang="scss">
.l-send-btn {
  display: flex;
  width: 36px;
  height: 36px;
  padding: 8px 10px;
  justify-content: center;
  align-items: center;
  border-radius: 9999px;
  transition: background 0.2s;
}
</style>