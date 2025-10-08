<template>
  <div class="welcome" :class="themeClass">
    <el-container style="height: 100%;">
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
        <div class="login-box" @keyup.enter="login">
          <div class="login-header">
            <div class="login-icon-wrapper">
              <div class="login-icon">👋</div>
            </div>
            <div class="login-title-group">
              <div class="login-text">{{ $t("login.title") }}</div>
              <div class="login-welcome">{{ $t("login.welcome") }}</div>
            </div>

            <!-- 语言切换下拉菜单 -->
            <el-dropdown trigger="click" class="title-language-dropdown"
              @visible-change="handleLanguageDropdownVisibleChange">
              <span class="el-dropdown-link">
                <span class="current-language-text">{{ currentLanguageText }}</span>
                <i class="el-icon-arrow-down el-icon--right" :class="{ 'rotate-down': languageDropdownVisible }"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="changeLanguage('zh_CN')">
                  {{ $t("language.zhCN") }}
                </el-dropdown-item>
                <el-dropdown-item @click.native="changeLanguage('zh_TW')">
                  {{ $t("language.zhTW") }}
                </el-dropdown-item>
                <el-dropdown-item @click.native="changeLanguage('en')">
                  {{ $t("language.en") }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>

            <!-- WiFi联网教程按钮 -->
            <el-tooltip :content="$t('wifiGuide.title')" placement="bottom">
              <el-button
                type="primary"
                icon="el-icon-connection"
                circle
                class="wifi-guide-btn"
                @click="showWifiGuide"
              ></el-button>
            </el-tooltip>
          </div>
          <div style="padding: 0 30px">
            <!-- 用户名登录 -->
            <template v-if="!isMobileLogin">
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/username.png" />
                <el-input v-model="form.username" :placeholder="$t('login.usernamePlaceholder')" />
              </div>
            </template>

            <!-- 手机号登录 -->
            <template v-else>
              <div class="input-box">
                <div style="display: flex; align-items: center; width: 100%">
                  <el-select v-model="form.areaCode" style="width: 220px; margin-right: 10px">
                    <el-option v-for="item in mobileAreaList" :key="item.key" :label="`${item.name} (${item.key})`"
                      :value="item.key" />
                  </el-select>
                  <el-input v-model="form.mobile" :placeholder="$t('login.mobilePlaceholder')" />
                </div>
              </div>
            </template>

            <div class="input-box">
              <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
              <el-input v-model="form.password" :placeholder="$t('login.passwordPlaceholder')" type="password"
                show-password />
            </div>
            <div style="
                display: flex;
                align-items: center;
                margin-top: 20px;
                width: 100%;
                gap: 10px;
              ">
              <div class="input-box" style="width: calc(100% - 130px); margin-top: 0">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/shield.png" />
                <el-input v-model="form.captcha" :placeholder="$t('login.captchaPlaceholder')" style="flex: 1" />
              </div>
              <div class="captcha-container">
                <img loading="lazy" v-if="captchaUrl" :src="captchaUrl" alt="验证码"
                  :style="isMobile ? 'width: 100px; height: 38px; cursor: pointer;' : 'width: 150px; height: 40px; cursor: pointer;'" @click="fetchCaptcha" />
              </div>
            </div>
            <div style="
                font-weight: 400;
                font-size: 14px;
                text-align: left;
                color: #5778ff;
                display: flex;
                justify-content: space-between;
                margin-top: 20px;
              ">
              <div v-if="allowUserRegister" style="cursor: pointer" @click="goToRegister">
                {{ $t("login.register") }}
              </div>
              <div style="cursor: pointer" @click="goToForgetPassword" v-if="enableMobileRegister">
                {{ $t("login.forgetPassword") }}
              </div>
            </div>
          </div>
          <div class="login-btn" @click="login">{{ $t("login.login") }}</div>

          <!-- 登录方式切换按钮 -->
          <div class="login-type-container" v-if="enableMobileRegister">
            <div style="display: flex; gap: 10px">
              <el-tooltip :content="$t('login.mobileLogin')" placement="bottom">
                <el-button :type="isMobileLogin ? 'primary' : 'default'" icon="el-icon-mobile" circle
                  @click="switchLoginType('mobile')"></el-button>
              </el-tooltip>
              <el-tooltip :content="$t('login.usernameLogin')" placement="bottom">
                <el-button :type="!isMobileLogin ? 'primary' : 'default'" icon="el-icon-user" circle
                  @click="switchLoginType('username')"></el-button>
              </el-tooltip>
            </div>
          </div>
          <div style="font-size: 14px; color: #979db1">
            {{ $t("login.agreeTo") }}
            <div style="display: inline-block; color: #5778ff; cursor: pointer">
              {{ $t("login.userAgreement") }}
            </div>
            {{ $t("login.and") }}
            <div style="display: inline-block; color: #5778ff; cursor: pointer">
              {{ $t("login.privacyPolicy") }}
            </div>
          </div>
        </div>
      </el-main>
      <el-footer>
        <version-footer />
      </el-footer>
    </el-container>

    <!-- WiFi联网教程对话框 -->
    <wifi-guide-dialog :visible.sync="wifiGuideVisible" />
  </div>
</template>

<script>
import Api from "@/apis/api";
import VersionFooter from "@/components/VersionFooter.vue";
import WifiGuideDialog from "@/components/WifiGuideDialog.vue";
import i18n, { changeLanguage } from "@/i18n";
import { getUUID, goToPage, showDanger, showSuccess, validateMobile } from "@/utils";
import { mapState } from "vuex";

export default {
  name: "login",
  components: {
    VersionFooter,
    WifiGuideDialog,
  },
  computed: {
    ...mapState({
      allowUserRegister: (state) => state.pubConfig.allowUserRegister,
      enableMobileRegister: (state) => state.pubConfig.enableMobileRegister,
      mobileAreaList: (state) => state.pubConfig.mobileAreaList,
    }),
    isMobile() {
      return this.mobileDeviceDetected;
    },
    themeClass() {
      return `theme-${this.currentTheme}`;
    },
    // 获取当前语言
    currentLanguage() {
      return i18n.locale || "zh_CN";
    },
    // 获取当前语言显示文本
    currentLanguageText() {
      const currentLang = this.currentLanguage;
      switch (currentLang) {
        case "zh_CN":
          return this.$t("language.zhCN");
        case "zh_TW":
          return this.$t("language.zhTW");
        case "en":
          return this.$t("language.en");
        default:
          return this.$t("language.zhCN");
      }
    }
  },
  data() {
    return {
      activeName: "username",
      form: {
        username: "",
        password: "",
        captcha: "",
        captchaId: "",
        areaCode: "+86",
        mobile: "",
      },
      captchaUuid: "",
      captchaUrl: "",
      isMobileLogin: false,
      mobileDeviceDetected: false,
      currentTheme: localStorage.getItem('backgroundTheme') || 'flow',
      wifiGuideVisible: false,
      languageDropdownVisible: false,
    }
  },
  mounted() {
    this.fetchCaptcha();
    this.$store.dispatch("fetchPubConfig").then(() => {
      // 根据配置决定默认登录方式
      this.isMobileLogin = this.enableMobileRegister;
    });
  },
  methods: {
    checkDeviceType() {
      this.mobileDeviceDetected = isMobileDevice();
    },
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
    // 切换语言
    changeLanguage(lang) {
      changeLanguage(lang);
      this.languageDropdownVisible = false;
      this.$message.success({
        message: this.$t('message.success'),
        showClose: true
      });
    },
    // 监听语言下拉菜单的可见状态变化
    handleLanguageDropdownVisibleChange(visible) {
      this.languageDropdownVisible = visible;
    },
    // 显示WiFi联网教程
    showWifiGuide() {
      this.wifiGuideVisible = true;
    },
    fetchCaptcha() {
      if (this.$store.getters.getToken) {
        if (this.$route.path !== "/home") {
          this.$router.push("/home");
        }
      } else {
        this.captchaUuid = getUUID();

        Api.user.getCaptcha(this.captchaUuid, (res) => {
          if (res.status === 200) {
            const blob = new Blob([res.data], { type: res.data.type });
            this.captchaUrl = URL.createObjectURL(blob);
          } else {
            showDanger("验证码加载失败，点击刷新");
          }
        });
      }
    },

    // 切换语言下拉菜单的可见状态变化
    handleLanguageDropdownVisibleChange(visible) {
      this.languageDropdownVisible = visible;
    },

    // 切换语言
    changeLanguage(lang) {
      changeLanguage(lang);
      this.languageDropdownVisible = false;
      this.$message.success({
        message: this.$t("message.success"),
        showClose: true,
      });
    },

    // 切换登录方式
    switchLoginType(type) {
      this.isMobileLogin = type === "mobile";
      // 清空表单
      this.form.username = "";
      this.form.mobile = "";
      this.form.password = "";
      this.form.captcha = "";
      this.fetchCaptcha();
    },

    // 封装输入验证逻辑
    validateInput(input, messageKey) {
      if (!input.trim()) {
        showDanger(this.$t(messageKey));
        return false;
      }
      return true;
    },

    async login() {
      if (this.isMobileLogin) {
        // 手机号登录验证
        if (!validateMobile(this.form.mobile, this.form.areaCode)) {
          showDanger(this.$t('login.requiredMobile'));
          return;
        }
        // 拼接手机号作为用户名
        this.form.username = this.form.areaCode + this.form.mobile;
      } else {
        // 用户名登录验证
        if (!this.validateInput(this.form.username, 'login.requiredUsername')) {
          return;
        }
      }

      // 验证密码
      if (!this.validateInput(this.form.password, 'login.requiredPassword')) {
        return;
      }
      // 验证验证码
      if (!this.validateInput(this.form.captcha, 'login.requiredCaptcha')) {
        return;
      }

      this.form.captchaId = this.captchaUuid;
      Api.user.login(
        this.form,
        ({ data }) => {
          showSuccess(this.$t('login.loginSuccess'));
          this.$store.commit("setToken", JSON.stringify(data.data));
          goToPage("/home");
        },
        (err) => {
          // 直接使用后端返回的国际化消息
          let errorMessage = err.data.msg || "登录失败";

          showDanger(errorMessage);
          if (
            err.data != null &&
            err.data.msg != null &&
            err.data.msg.indexOf("图形验证码") > -1 || err.data.msg.indexOf("Captcha") > -1
          ) {
            this.fetchCaptcha();
          }
        }
      );

      // 重新获取验证码
      setTimeout(() => {
        this.fetchCaptcha();
      }, 1000);
    },

    goToRegister() {
      goToPage("/register");
    },
    goToForgetPassword() {
      goToPage("/retrieve-password");
    },
  },
};
</script>
<style lang="scss" scoped>
@import "./auth.scss";

/* WiFi教程按钮样式 */
.wifi-guide-btn {
  margin-left: 10px;
  
  &:hover {
    transform: scale(1.1);
    transition: all 0.3s ease;
  }
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .wifi-guide-btn {
    width: 36px !important;
    height: 36px !important;
    padding: 0 !important;
  }
}

.login-type-container {
  margin: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-language-dropdown {
  margin-left: auto;
}

.current-language-text {
  margin-left: 4px;
  margin-right: 4px;
  font-size: 12px;
  color: #3d4566;
}

.language-dropdown {
  margin-left: auto;
}

.rotate-down {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

.el-icon-arrow-down {
  transition: transform 0.3s ease;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover,
  &:focus {
    background: linear-gradient(135deg, #3A7A8A 0%, #6BB6AA 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(74, 144, 164, 0.4);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(74, 144, 164, 0.4);
  }
}
</style>
