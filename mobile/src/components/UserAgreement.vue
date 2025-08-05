<template>
  <div class="modal" v-if="show">
    <div class="modal-content">
      <div class="modal-header">
        <h2>宠物找回平台用户协议</h2>
        <button class="close-btn" @click="close">✕</button>
      </div>

      <div class="modal-body">
        <div class="agreement-section">
          <p class="intro-text">
            欢迎使用宠物找回平台（以下简称"本平台"或"我们"）。本协议是您（以下简称"用户"）与宠物找回平台运营方之间达成的法律协议。请在使用本平台服务前仔细阅读本协议的全部条款。通过注册、登录或使用本平台的任何服务，即表示您已阅读、理解并同意接受本协议的全部条款和条件。
          </p>
        </div>

        <div class="agreement-section">
          <h3>第一条 服务内容与范围</h3>
          <p>1.1 本平台为宠物主人提供丢失宠物信息发布服务，包括但不限于：</p>
          <ul>
            <li>宠物丢失信息发布与展示</li>
            <li>宠物照片上传与管理</li>
            <li>丢失地点定位与描述</li>
            <li>悬赏金额设置与管理</li>
            <li>联系方式发布与保护</li>
            <li>信息分享与传播</li>
          </ul>
          <p>
            1.2 本平台不直接提供宠物找寻服务，而是作为信息发布和传播的平台。
          </p>
          <p>
            1.3 平台保留根据实际情况调整、变更、暂停或终止部分或全部服务的权利。
          </p>
        </div>

        <div class="agreement-section">
          <h3>第二条 用户注册与账户管理</h3>
          <p>
            2.1
            用户需使用真实、有效的手机号码进行注册，并确保联系方式的及时性和有效性。
          </p>
          <p>
            2.2
            用户应妥善保管账户信息，不得将账户借给他人使用，因用户过失导致的账户安全问题由用户承担。
          </p>
          <p>2.3 每个手机号码仅可注册一个账户，重复注册的账户平台有权注销。</p>
          <p>
            2.4
            用户账户仅限本人使用，禁止转让、出售或以其他方式提供给第三方使用。
          </p>
        </div>

        <div class="agreement-section">
          <h3>第三条 用户行为规范与义务</h3>
          <p>
            3.1 用户承诺发布的宠物丢失信息真实、准确、完整，不得发布虚假信息。
          </p>
          <p>3.2 用户不得发布以下内容：</p>
          <ul>
            <li>虚假的宠物丢失信息</li>
            <li>非宠物类的物品找寻信息</li>
            <li>涉及色情、暴力、恐怖、政治敏感等不良内容</li>
            <li>侵犯他人知识产权或隐私权的内容</li>
            <li>包含广告推销、商业宣传的内容</li>
            <li>其他违法违规或不当的内容</li>
          </ul>
          <p>
            3.3
            用户应当文明使用平台，不得恶意举报、辱骂他人或进行其他影响平台秩序的行为。
          </p>
          <p>3.4 用户发现宠物后，应及时更新发布状态，避免无效信息继续传播。</p>
        </div>

        <div class="agreement-section">
          <h3>第四条 平台权利与义务</h3>
          <p>
            4.1
            平台有权对用户发布的内容进行审核，对不符合规定的内容有权拒绝发布、删除或屏蔽。
          </p>
          <p>
            4.2
            平台有权根据国家法律法规要求，配合有关部门进行调查，并提供必要的用户信息。
          </p>
          <p>
            4.3
            平台承诺保护用户的个人隐私信息，不会未经授权向第三方泄露用户信息。
          </p>
          <p>
            4.4
            平台努力维护服务的稳定性和安全性，但不对因不可抗力、黑客攻击、系统故障等造成的服务中断承担责任。
          </p>
          <p>
            4.5
            平台有权对违反协议的用户采取警告、限制功能、暂停服务、注销账户等措施。
          </p>
        </div>

        <div class="agreement-section">
          <h3>第五条 费用与支付</h3>
          <p>5.1 用户在本平台发布宠物丢失信息需要支付一定的服务费用。</p>
          <p>5.2 费用标准以平台公布的价格为准，平台保留调整价格的权利。</p>
          <p>5.3 用户支付的费用用于信息发布、平台维护和技术支持等服务成本。</p>
          <p>5.4 除平台明确承诺的情况外，已支付的费用不予退还。</p>
        </div>

        <div class="agreement-section">
          <h3>第六条 知识产权</h3>
          <p>6.1 平台的Logo、商标、软件、界面设计等知识产权归平台所有。</p>
          <p>
            6.2
            用户上传的宠物照片及相关内容的知识产权归用户所有，但用户授权平台为提供服务之目的使用。
          </p>
          <p>6.3 未经平台许可，用户不得复制、传播、修改平台的受保护内容。</p>
        </div>

        <div class="agreement-section">
          <h3>第七条 免责声明</h3>
          <p>7.1 平台仅提供信息发布平台，不对宠物找回的最终结果承担责任。</p>
          <p>
            7.2 平台不对因用户发布信息不准确、不完整而造成的任何损失承担责任。
          </p>
          <p>
            7.3 平台不对用户与第三方之间的纠纷承担责任，但可提供必要的协助。
          </p>
          <p>
            7.4
            因不可抗力（如自然灾害、政府行为、网络故障等）造成的服务中断，平台不承担责任。
          </p>
        </div>

        <div class="agreement-section">
          <h3>第八条 协议变更与终止</h3>
          <p>8.1 平台有权根据实际情况修改本协议，修改后的协议将在平台公布。</p>
          <p>8.2 协议修改后，用户继续使用平台服务即视为接受修改后的协议。</p>
          <p>
            8.3
            用户可随时停止使用平台服务，平台也可根据本协议约定终止为用户提供服务。
          </p>
          <p>8.4 协议终止后，双方之前产生的权利义务关系仍受本协议约束。</p>
        </div>

        <div class="agreement-section">
          <h3>第九条 争议解决</h3>
          <p>9.1 因本协议引起的争议，双方应首先通过友好协商解决。</p>
          <p>9.2 协商不成的，任何一方均可向平台所在地人民法院提起诉讼。</p>
          <p>9.3 本协议的签订、执行和解释均适用中华人民共和国法律。</p>
        </div>

        <div class="agreement-section">
          <h3>第十条 其他条款</h3>
          <p>
            10.1
            本协议构成双方关于协议事项的完整协议，取代此前双方的口头或书面约定。
          </p>
          <p>10.2 本协议部分条款被认定无效的，不影响其他条款的效力。</p>
          <p>
            10.3 平台联系方式：客服电话 400-123-4567，客服邮箱
            service@petfinder.com。
          </p>
          <p>10.4 本协议最终解释权归宠物找回平台所有。</p>
        </div>

        <div class="agreement-footer">
          <p><strong>协议生效日期：2023年12月1日</strong></p>
          <p><strong>最后更新日期：2023年12月1日</strong></p>
        </div>
      </div>

      <div class="modal-actions">
        <button class="agree-btn" @click="close">我已阅读并同意</button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      show: Boolean,
    },
    methods: {
      close() {
        this.$emit('close')
      },
    },
  }
</script>

<style scoped>
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    padding: 20px;
    box-sizing: border-box;
  }

  .modal-content {
    background: white;
    border-radius: 12px;
    max-width: 90%;
    max-height: 90%;
    width: 100%;
    display: flex;
    flex-direction: column;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #e5e7eb;
    background: #f9fafb;
    border-radius: 12px 12px 0 0;
  }

  .modal-header h2 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 20px;
    color: #6b7280;
    cursor: pointer;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: background 0.2s;
  }

  .close-btn:hover {
    background: #e5e7eb;
  }

  .modal-body {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    font-size: 14px;
    line-height: 1.6;
  }

  .intro-text {
    background: #f0f9ff;
    border-left: 4px solid #0ea5e9;
    padding: 15px;
    margin-bottom: 20px;
    font-weight: 500;
    color: #1e40af;
    border-radius: 0 6px 6px 0;
  }

  .agreement-section {
    margin-bottom: 25px;
  }

  .agreement-section h3 {
    color: #1f2937;
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 12px 0;
    padding-bottom: 8px;
    border-bottom: 2px solid #e5e7eb;
  }

  .agreement-section p {
    margin: 8px 0;
    color: #374151;
    text-align: justify;
  }

  .agreement-section ul {
    margin: 8px 0;
    padding-left: 20px;
  }

  .agreement-section li {
    margin: 4px 0;
    color: #374151;
  }

  .agreement-footer {
    background: #f9fafb;
    padding: 15px;
    border-radius: 8px;
    text-align: center;
    margin-top: 20px;
  }

  .agreement-footer p {
    margin: 5px 0;
    color: #6b7280;
    font-size: 13px;
  }

  .modal-actions {
    padding: 20px;
    border-top: 1px solid #e5e7eb;
    background: #f9fafb;
    border-radius: 0 0 12px 12px;
  }

  .agree-btn {
    width: 100%;
    background: #3b82f6;
    color: white;
    border: none;
    padding: 12px 20px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s;
  }

  .agree-btn:hover {
    background: #2563eb;
  }

  .agree-btn:active {
    background: #1d4ed8;
  }

  /* 移动端适配 */
  @media (max-width: 768px) {
    .modal {
      padding: 10px;
    }

    .modal-content {
      max-width: 100%;
      max-height: 95%;
    }

    .modal-header {
      padding: 15px;
    }

    .modal-header h2 {
      font-size: 16px;
    }

    .modal-body {
      padding: 15px;
      font-size: 13px;
    }

    .agreement-section h3 {
      font-size: 15px;
    }
  }
</style>
