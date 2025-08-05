<template>
  <div class="modal" v-if="show">
    <div class="modal-content">
      <div class="modal-header">
        <h2>宠物找回平台隐私政策</h2>
        <button class="close-btn" @click="close">✕</button>
      </div>

      <div class="modal-body">
        <div class="privacy-section">
          <p class="intro-text">
            宠物找回平台（以下简称"我们"或"本平台"）深知个人信息对您的重要性，并会全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。
          </p>
        </div>

        <div class="privacy-section">
          <h3>第一条 我们收集的信息</h3>
          <h4>1.1 您主动提供的信息</h4>
          <ul>
            <li><strong>注册信息：</strong>手机号码、验证码等基本身份信息</li>
            <li><strong>个人资料：</strong>用户名、头像、邮箱地址等可选信息</li>
            <li>
              <strong>宠物信息：</strong>宠物照片、宠物名称、品种、特征描述
            </li>
            <li><strong>丢失信息：</strong>丢失地点、丢失时间、详细描述</li>
            <li><strong>联系信息：</strong>联系电话、微信号等联系方式</li>
            <li>
              <strong>支付信息：</strong
              >悬赏金额、支付方式（不包含银行卡密码等敏感信息）
            </li>
          </ul>

          <h4>1.2 我们自动收集的信息</h4>
          <ul>
            <li>
              <strong>设备信息：</strong>设备型号、操作系统、唯一设备标识符
            </li>
            <li><strong>网络信息：</strong>IP地址、网络类型、运营商信息</li>
            <li><strong>使用信息：</strong>访问时间、使用功能、页面浏览记录</li>
            <li><strong>位置信息：</strong>GPS定位（仅在用户授权后收集）</li>
            <li><strong>日志信息：</strong>系统日志、错误报告、性能数据</li>
          </ul>
        </div>

        <div class="privacy-section">
          <h3>第二条 我们如何使用您的信息</h3>
          <p>2.1 <strong>基本服务提供：</strong></p>
          <ul>
            <li>创建和管理您的账户</li>
            <li>发布和展示宠物丢失信息</li>
            <li>处理您的服务请求</li>
            <li>提供客户支持服务</li>
          </ul>

          <p>2.2 <strong>服务改进与优化：</strong></p>
          <ul>
            <li>分析用户使用习惯，优化产品功能</li>
            <li>进行数据统计和分析</li>
            <li>开发新的服务功能</li>
            <li>提高系统安全性和稳定性</li>
          </ul>

          <p>2.3 <strong>安全保障：</strong></p>
          <ul>
            <li>验证用户身份，防止欺诈行为</li>
            <li>检测和预防安全威胁</li>
            <li>调查违法违规行为</li>
            <li>确保平台正常运行</li>
          </ul>

          <p>2.4 <strong>法律合规：</strong></p>
          <ul>
            <li>遵守适用的法律法规</li>
            <li>配合司法机关调查</li>
            <li>维护公共安全和利益</li>
          </ul>
        </div>

        <div class="privacy-section">
          <h3>第三条 我们如何共享您的信息</h3>
          <p>我们不会向第三方出售、出租或以其他方式披露您的个人信息，除非：</p>

          <p>3.1 <strong>经您明确同意：</strong></p>
          <ul>
            <li>在您明确同意的情况下，我们会与指定的第三方分享您的信息</li>
            <li>发布宠物丢失信息时，部分信息将公开展示以便寻找</li>
          </ul>

          <p>3.2 <strong>服务提供商：</strong></p>
          <ul>
            <li>云存储服务商：保存您上传的图片和数据</li>
            <li>短信服务商：发送验证码和通知信息</li>
            <li>支付服务商：处理支付相关业务</li>
            <li>地图服务商：提供位置相关服务</li>
          </ul>

          <p>3.3 <strong>法律要求：</strong></p>
          <ul>
            <li>司法机关或行政机关基于法定程序要求</li>
            <li>为维护平台或其他用户的合法权益</li>
            <li>应对紧急情况，保护用户或公众安全</li>
          </ul>
        </div>

        <div class="privacy-section">
          <h3>第四条 我们如何保护您的信息</h3>
          <p>4.1 <strong>技术安全措施：</strong></p>
          <ul>
            <li>使用SSL加密传输保护数据传输安全</li>
            <li>采用AES加密算法保护敏感数据存储</li>
            <li>实施访问控制，限制数据访问权限</li>
            <li>定期进行安全漏洞扫描和修复</li>
            <li>部署防火墙和入侵检测系统</li>
          </ul>

          <p>4.2 <strong>管理安全措施：</strong></p>
          <ul>
            <li>建立数据安全管理制度</li>
            <li>对员工进行数据保护培训</li>
            <li>实施数据访问审计和监控</li>
            <li>定期评估和更新安全措施</li>
          </ul>

          <p>4.3 <strong>数据备份与恢复：</strong></p>
          <ul>
            <li>定期备份重要数据</li>
            <li>建立灾难恢复机制</li>
            <li>确保数据的完整性和可用性</li>
          </ul>
        </div>

        <div class="privacy-section">
          <h3>第五条 您的权利</h3>
          <p>
            5.1
            <strong>访问权：</strong>您有权了解我们收集、使用您个人信息的情况
          </p>
          <p>
            5.2 <strong>更正权：</strong>您有权要求我们更正或更新您的个人信息
          </p>
          <p>
            5.3
            <strong>删除权：</strong
            >在特定情况下，您有权要求我们删除您的个人信息
          </p>
          <p>5.4 <strong>撤回同意：</strong>您有权撤回对个人信息处理的同意</p>
          <p>5.5 <strong>投诉举报：</strong>您有权向有关监管部门投诉举报</p>

          <p><strong>如需行使上述权利，请通过以下方式联系我们：</strong></p>
          <ul>
            <li>客服电话：400-123-4567</li>
            <li>客服邮箱：privacy@petfinder.com</li>
            <li>在线客服：登录平台后联系在线客服</li>
          </ul>
        </div>

        <div class="privacy-section">
          <h3>第六条 Cookie和类似技术</h3>
          <p>6.1 我们使用Cookie和类似技术来提供更好的用户体验：</p>
          <ul>
            <li>记住您的登录状态</li>
            <li>保存您的偏好设置</li>
            <li>分析网站使用情况</li>
            <li>提供个性化内容</li>
          </ul>

          <p>6.2 您可以通过浏览器设置管理Cookie：</p>
          <ul>
            <li>大多数浏览器允许您查看、删除或拒绝Cookie</li>
            <li>禁用Cookie可能影响某些功能的正常使用</li>
          </ul>
        </div>

        <div class="privacy-section">
          <h3>第七条 第三方服务</h3>
          <p>7.1 我们的服务可能包含第三方提供的服务或链接</p>
          <p>7.2 第三方服务有其独立的隐私政策，我们不对其承担责任</p>
          <p>7.3 建议您在使用第三方服务前仔细阅读其隐私政策</p>
        </div>

        <div class="privacy-section">
          <h3>第八条 儿童隐私保护</h3>
          <p>
            8.1 我们的服务主要面向成年用户，不会主动收集14岁以下儿童的个人信息
          </p>
          <p>8.2 如果我们发现收集了儿童的个人信息，将立即删除相关信息</p>
          <p>
            8.3 如果您是儿童的监护人，发现儿童向我们提供了个人信息，请联系我们
          </p>
        </div>

        <div class="privacy-section">
          <h3>第九条 跨境传输</h3>
          <p>9.1 您的个人信息主要存储在中华人民共和国境内</p>
          <p>9.2 如需跨境传输，我们将严格按照法律法规要求进行</p>
          <p>9.3 确保境外接收方提供充分的数据保护水平</p>
        </div>

        <div class="privacy-section">
          <h3>第十条 数据保留</h3>
          <p>10.1 我们仅在为实现本政策所述目的所需的期间内保留您的个人信息</p>
          <p>10.2 一般情况下，我们会在您注销账户后合理期间内删除相关信息</p>
          <p>10.3 法律法规另有规定的，从其规定</p>
        </div>

        <div class="privacy-section">
          <h3>第十一条 政策更新</h3>
          <p>11.1 我们可能适时更新本隐私政策</p>
          <p>11.2 重大变更会通过站内消息、邮件等方式通知您</p>
          <p>11.3 继续使用我们的服务即表示您接受更新后的政策</p>
        </div>

        <div class="privacy-section">
          <h3>第十二条 联系我们</h3>
          <p>
            如果您对本隐私政策有任何疑问、意见或建议，请通过以下方式联系我们：
          </p>
          <ul>
            <li>
              <strong>客服电话：</strong>400-123-4567（工作时间：9:00-18:00）
            </li>
            <li><strong>客服邮箱：</strong>privacy@petfinder.com</li>
            <li>
              <strong>通讯地址：</strong>北京市朝阳区xxx路xxx号xxx大厦xxx室
            </li>
            <li><strong>数据保护官：</strong>dpo@petfinder.com</li>
          </ul>
          <p>我们将在15个工作日内回复您的请求。</p>
        </div>

        <div class="privacy-footer">
          <p><strong>政策生效日期：2023年12月1日</strong></p>
          <p><strong>最后更新日期：2023年12月1日</strong></p>
          <p><strong>版本号：V1.0</strong></p>
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
    background: #f0fdf4;
    border-left: 4px solid #10b981;
    padding: 15px;
    margin-bottom: 20px;
    font-weight: 500;
    color: #065f46;
    border-radius: 0 6px 6px 0;
  }

  .privacy-section {
    margin-bottom: 25px;
  }

  .privacy-section h3 {
    color: #1f2937;
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 12px 0;
    padding-bottom: 8px;
    border-bottom: 2px solid #e5e7eb;
  }

  .privacy-section h4 {
    color: #374151;
    font-size: 15px;
    font-weight: 600;
    margin: 15px 0 8px 0;
  }

  .privacy-section p {
    margin: 8px 0;
    color: #374151;
    text-align: justify;
  }

  .privacy-section ul {
    margin: 8px 0;
    padding-left: 20px;
  }

  .privacy-section li {
    margin: 4px 0;
    color: #374151;
  }

  .privacy-section strong {
    color: #1f2937;
    font-weight: 600;
  }

  .privacy-footer {
    background: #f9fafb;
    padding: 15px;
    border-radius: 8px;
    text-align: center;
    margin-top: 20px;
  }

  .privacy-footer p {
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
    background: #10b981;
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
    background: #059669;
  }

  .agree-btn:active {
    background: #047857;
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

    .privacy-section h3 {
      font-size: 15px;
    }

    .privacy-section h4 {
      font-size: 14px;
    }
  }
</style>
