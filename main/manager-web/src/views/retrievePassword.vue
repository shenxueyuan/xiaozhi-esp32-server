<template>
  <div class="welcome" :class="themeClass" @keyup.enter="retrievePassword">
    <el-container style="height: 100%;">
      <!-- 保持相同的头部 -->
      <el-header>
        <div style="display: flex;align-items: center;justify-content: space-between;margin-top: 15px;margin-left: 20px;margin-right: 20px;gap: 10px;">
          <div class="app-title" style="font-size: 24px; font-weight: 700; letter-spacing: 1px; transition: all 0.3s ease;">AI小新-智控台</div>
          <div class="theme-selector">
            <el-dropdown @command="changeBackgroundTheme" trigger="hover">
              <div class="theme-button">
                🎨 <i class="el-icon-arrow-down"></i>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="flow" :class="{'active-theme': currentTheme === 'flow'}">
                  🌊 流动渐变
                </el-dropdown-item>
                <el-dropdown-item command="bubbles" :class="{'active-theme': currentTheme === 'bubbles'}">
                  ☁️ 优雅光晕
                </el-dropdown-item>
                <el-dropdown-item command="particles" :class="{'active-theme': currentTheme === 'particles'}">
                  ✨ 粒子星空
                </el-dropdown-item>
                <el-dropdown-item command="geometric" :class="{'active-theme': currentTheme === 'geometric'}">
                  🔷 现代几何
                </el-dropdown-item>
                <el-dropdown-item command="breathing" :class="{'active-theme': currentTheme === 'breathing'}">
                  💫 呼吸光晕
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main style="position: relative; display: flex; align-items: center; justify-content: center;">
        <form @submit.prevent="retrievePassword">
          <div class="login-box">
            <!-- 修改标题部分 -->
            <div class="login-header">
              <div class="login-icon-wrapper">
                <div class="login-icon">🔐</div>
              </div>
              <div class="login-title-group">
                <div class="login-text">{{ $t('retrievePassword.title') }}</div>
                <div class="login-welcome">{{ $t('retrievePassword.subtitle') }}</div>
              </div>
            </div>

            <div style="padding: 0 30px;">
              <!-- 手机号输入 -->
              <div class="input-box">
                <div class="mobile-input-container" style="display: flex; align-items: center; width: 100%;">
                  <el-select v-model="form.areaCode" :style="isMobile ? 'width: 90px; margin-right: 8px;' : 'width: 220px; margin-right: 10px;'">
                    <el-option v-for="item in mobileAreaList" :key="item.key" :label="`${item.name} (${item.key})`"
                      :value="item.key" />
                  </el-select>
                  <el-input v-model="form.mobile" :placeholder="$t('retrievePassword.mobilePlaceholder')" style="flex: 1;" />
                </div>
              </div>

              <div style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;">
                <div class="input-box" style="width: calc(100% - 130px); margin-top: 0;">
                  <img loading="lazy" alt="" class="input-icon" src="@/assets/login/shield.png" />
                  <el-input v-model="form.captcha" :placeholder="$t('retrievePassword.captchaPlaceholder')" style="flex: 1;" />
                </div>
                <div class="captcha-container">
                  <img loading="lazy" v-if="captchaUrl" :src="captchaUrl" alt="验证码"
                    :style="isMobile ? 'width: 100px; height: 38px; cursor: pointer;' : 'width: 150px; height: 40px; cursor: pointer;'" @click="fetchCaptcha" />
                </div>
              </div>

              <!-- 手机验证码 -->
              <div style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;">
                <div class="input-box" style="width: calc(100% - 130px); margin-top: 0;">
                  <img loading="lazy" alt="" class="input-icon" src="@/assets/login/phone.png" />
                  <el-input v-model="form.mobileCaptcha" :placeholder="$t('retrievePassword.mobileCaptchaPlaceholder')" style="flex: 1;" maxlength="6" />
                </div>
                <el-button type="primary" class="send-captcha-btn" :disabled="!canSendMobileCaptcha"
                  @click="sendMobileCaptcha">
                  <span>
                    {{ countdown > 0 ? `${countdown}${$t('register.secondsLater')}` : $t('retrievePassword.getMobileCaptcha') }}
                  </span>
                </el-button>
              </div>

              <!-- 新密码 -->
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
                <el-input v-model="form.newPassword" :placeholder="$t('retrievePassword.newPasswordPlaceholder')" type="password" show-password />
              </div>

              <!-- 确认新密码 -->
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
                <el-input v-model="form.confirmPassword" :placeholder="$t('retrievePassword.confirmNewPasswordPlaceholder')" type="password" show-password />
              </div>

              <!-- 修改底部链接 -->
              <div style="font-weight: 400;font-size: 14px;text-align: left;color: #5778ff;margin-top: 20px;">
                <div style="cursor: pointer;" @click="goToLogin">{{ $t('retrievePassword.goToLogin') }}</div>
              </div>
            </div>

            <!-- 修改按钮文本 -->
            <div class="login-btn" @click="retrievePassword">{{ $t('retrievePassword.resetButton') }}</div>

            <!-- 保持相同的协议声明 -->
            <div style="font-size: 14px;color: #979db1;">
              {{ $t('retrievePassword.agreeTo') }}
              <div style="display: inline-block;color: #5778FF;cursor: pointer;">{{ $t('register.userAgreement') }}</div>
              {{ $t('login.and') }}
              <div style="display: inline-block;color: #5778FF;cursor: pointer;">{{ $t('register.privacyPolicy') }}</div>
            </div>
          </div>
        </form>
      </el-main>

      <!-- 保持相同的页脚 -->
      <el-footer>
        <version-footer />
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import Api from '@/apis/api';
import VersionFooter from '@/components/VersionFooter.vue';
import { getUUID, goToPage, showDanger, showSuccess, validateMobile, isMobileDevice, sm2Encrypt } from '@/utils';
import { mapState } from 'vuex';
import i18n from '@/i18n';

// 导入语言切换功能
import { changeLanguage } from '@/i18n';

export default {
  name: 'retrieve',
  components: {
    VersionFooter
  },
  computed: {
    ...mapState({
      allowUserRegister: state => state.pubConfig.allowUserRegister,
      mobileAreaList: state => state.pubConfig.mobileAreaList,
      sm2PublicKey: state => state.pubConfig.sm2PublicKey
    }),
    // 获取当前语言
    currentLanguage() {
      return i18n.locale || "zh_CN";
    },
    // 根据当前语言获取对应的xiaozhi-ai图标
    xiaozhiAiIcon() {
      const currentLang = this.currentLanguage;
      switch (currentLang) {
        case "zh_CN":
          return require("@/assets/xiaozhi-ai.png");
        case "zh_TW":
          return require("@/assets/xiaozhi-ai_zh_TW.png");
        case "en":
          return require("@/assets/xiaozhi-ai_en.png");
        case "de":
          return require("@/assets/xiaozhi-ai_de.png");
        case "vi":
          return require("@/assets/xiaozhi-ai_vi.png");
        default:
          return require("@/assets/xiaozhi-ai.png");
      }
    },
    canSendMobileCaptcha() {
      return this.countdown === 0 && validateMobile(this.form.mobile, this.form.areaCode);
    },
    themeClass() {
      return `theme-${this.currentTheme}`;
    },
    isMobile() {
      return this.mobileDeviceDetected;
    }
  },
  data() {
    return {
      form: {
        areaCode: '+86',
        mobile: '',
        captcha: '',
        captchaId: '',
        mobileCaptcha: '',
        newPassword: '',
        confirmPassword: ''
      },
      captchaUrl: '',
      countdown: 0,
      timer: null,
      currentTheme: localStorage.getItem('backgroundTheme') || 'flow',
      mobileDeviceDetected: false
    }
  },
  mounted() {
    this.fetchCaptcha();
    this.checkDeviceType();
  },
  methods: {
    changeBackgroundTheme(theme) {
      this.currentTheme = theme;
      localStorage.setItem('backgroundTheme', theme);
      this.$message.success(`已切换到${this.getThemeName(theme)}主题`);
    },
    getThemeName(theme) {
      const names = {
        flow: '流动渐变',
        bubbles: '优雅光晕',
        particles: '粒子星空',
        geometric: '现代几何',
        breathing: '呼吸光晕'
      };
      return names[theme] || '未知主题';
    },
    checkDeviceType() {
      this.mobileDeviceDetected = isMobileDevice();
    },
    // 复用验证码获取方法
    fetchCaptcha() {
      this.form.captchaId = getUUID();
      Api.user.getCaptcha(this.form.captchaId, (res) => {
        if (res.status === 200) {
          const blob = new Blob([res.data], { type: res.data.type });
          this.captchaUrl = URL.createObjectURL(blob);

        } else {
          console.error('验证码加载异常:', error);
          showDanger(this.$t('register.captchaLoadFailed'));
        }
      });
    },

    // 封装输入验证逻辑
    validateInput(input, message) {
      if (!input.trim()) {
        showDanger(message);
        return false;
      }
      return true;
    },

    // 发送手机验证码
    sendMobileCaptcha() {
      if (!validateMobile(this.form.mobile, this.form.areaCode)) {
        showDanger(this.$t('retrievePassword.inputCorrectMobile'));
        return;
      }

      // 验证图形验证码
      if (!this.validateInput(this.form.captcha, this.$t('retrievePassword.captchaRequired'))) {
        this.fetchCaptcha();
        return;
      }

      // 清除可能存在的旧定时器
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }

      // 开始倒计时
      this.countdown = 60;
      this.timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          clearInterval(this.timer);
          this.timer = null;
        }
      }, 1000);

      // 调用发送验证码接口
      Api.user.sendSmsVerification({
        phone: this.form.areaCode + this.form.mobile,
        captcha: this.form.captcha,
        captchaId: this.form.captchaId
      }, (res) => {
        showSuccess(this.$t('retrievePassword.captchaSendSuccess'));
      }, (err) => {
        showDanger(err.data.msg || this.$t('register.captchaSendFailed'));
        this.countdown = 0;
        this.fetchCaptcha();
      });
    },

    // 修改逻辑
    retrievePassword() {
      // 验证逻辑
      if (!validateMobile(this.form.mobile, this.form.areaCode)) {
        showDanger(this.$t('retrievePassword.inputCorrectMobile'));
        return;
      }
      if (!this.form.captcha) {
        showDanger(this.$t('retrievePassword.captchaRequired'));
        return;
      }
      if (!this.form.mobileCaptcha) {
        showDanger(this.$t('retrievePassword.mobileCaptchaRequired'));
        return;
      }
      if (this.form.newPassword !== this.form.confirmPassword) {
        showDanger(this.$t('retrievePassword.passwordsNotMatch'));
        return;
      }

      // 加密密码
      let encryptedPassword;
      try {
        // 拼接图形验证码和新密码进行加密
        const captchaAndPassword = this.form.captcha + this.form.newPassword;
        encryptedPassword = sm2Encrypt(this.sm2PublicKey, captchaAndPassword);
      } catch (error) {
        console.error("密码加密失败:", error);
        showDanger(this.$t('sm2.encryptionFailed'));
        return;
      }

      Api.user.retrievePassword({
        phone: this.form.areaCode + this.form.mobile,
        password: encryptedPassword,
        code: this.form.mobileCaptcha,
        captchaId: this.form.captchaId
      }, (res) => {
        showSuccess(this.$t('retrievePassword.passwordUpdateSuccess'));
        goToPage('/login');
      }, (err) => {
        showDanger(err.data.msg || this.$t('message.error'));
        if (err.data != null && err.data.msg != null && (err.data.msg.indexOf('图形验证码') > -1 || err.data.msg.indexOf('Captcha') > -1)) {
          this.fetchCaptcha()
        }
      });
    },

    goToLogin() {
      goToPage('/login')
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }
}
</script>

<style lang="scss" scoped>
@import './auth.scss';

.send-captcha-btn {
  margin-right: -5px;
  min-width: 100px;
  height: 40px;
  line-height: 40px;
  border-radius: 8px;
  font-size: 14px;
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  border: none;
  padding: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(74, 144, 164, 0.3);

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #3A7A8A 0%, #6BB6AA 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(74, 144, 164, 0.4);
  }

  &:disabled {
    background: #c0c4cc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

/* 移动端发送验证码按钮优化 */
@media screen and (max-width: 768px) {
  .send-captcha-btn {
    min-width: 90px;
    height: 38px;
    line-height: 38px;
    font-size: 12px;
    border-radius: 6px;
  }
}
</style>
