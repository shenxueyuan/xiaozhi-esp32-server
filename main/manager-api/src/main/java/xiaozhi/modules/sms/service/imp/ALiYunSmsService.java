package xiaozhi.modules.sms.service.imp;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.exception.ErrorCode;
import xiaozhi.common.exception.RenException;
import xiaozhi.common.redis.RedisKeys;
import xiaozhi.common.redis.RedisUtils;
import xiaozhi.modules.alert.dto.SmsAlertDTO;
import xiaozhi.modules.sms.service.SmsService;
import xiaozhi.modules.sys.service.SysParamsService;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class ALiYunSmsService implements SmsService {
    private final SysParamsService  sysParamsService;
    private final RedisUtils redisUtils;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public void sendVerificationCodeSms(String phone, String VerificationCode) {
        Client client = createClient();
        String SignName = sysParamsService.getValue(Constant.SysMSMParam
                .ALIYUN_SMS_SIGN_NAME.getValue(),true);
        String TemplateCode = sysParamsService.getValue(Constant.SysMSMParam
                .ALIYUN_SMS_SMS_CODE_TEMPLATE_CODE.getValue(),true);
        try {
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(SignName)
                    .setTemplateCode(TemplateCode)
                    .setPhoneNumbers(phone)
                    .setTemplateParam(String.format("{\"code\":\"%s\"}", VerificationCode));
            RuntimeOptions runtime = new RuntimeOptions();
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            log.info("发送短信响应的requestID: {}", sendSmsResponse.getBody().getRequestId());
        } catch (Exception e) {
            // 如果发送失败了退还这次发送数
            String todayCountKey = RedisKeys.getSMSTodayCountKey(phone);
            redisUtils.delete(todayCountKey);
            // 错误 message
            log.error(e.getMessage());
            throw new RenException(ErrorCode.SMS_SEND_FAILED);
        }

    }

    @Override
    public void sendAlertSms(String phone, SmsAlertDTO alertDTO) {
        if (isValid(phone)) {
            Client client = createClient();
            String SignName = sysParamsService.getValue(Constant.SysMSMParam
                .ALIYUN_SMS_SIGN_NAME.getValue(),true);
            String TemplateCode = sysParamsService.getValue(Constant.SysMSMParam
                .ALIYUN_SMS_ALERT_TEMPLATE_CODE.getValue(),true);
            try {
                SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(SignName)
                    .setTemplateCode(TemplateCode)
                    .setPhoneNumbers(phone)
                    .setTemplateParam(String.format("{\"device\":\"%s\"}", alertDTO.getMacAddress()));
                RuntimeOptions runtime = new RuntimeOptions();
                SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
                log.info("发送短信告警通知的requestID: {}，手机号：{}", sendSmsResponse.getBody().getRequestId(), phone);
            } catch (Exception e) {
                log.error("短信告警通知失败 {}", e.getMessage());
                throw new RenException("短信告警通知失败");
            }
        } else {
            log.info("{}不符合手机号规则，不发送短信告警通知", phone);
        }
    }

    /**
     * 校验手机号是否有效
     * @param phoneNumber 待校验的手机号
     * @return 如果手机号有效返回 true，否则返回 false
     */
    public boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        String phone = phoneNumber.replace("+86", "");
        // 使用正则表达式进行匹配
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 创建阿里云连接
     * @return 返回连接对象
     */
    private Client createClient(){
        String ACCESS_KEY_ID = sysParamsService.getValue(Constant.SysMSMParam
                .ALIYUN_SMS_ACCESS_KEY_ID.getValue(),true);
        String ACCESS_KEY_SECRET = sysParamsService.getValue(Constant.SysMSMParam
                .ALIYUN_SMS_ACCESS_KEY_SECRET.getValue(),true);
        try {
            Config config = new Config()
                    .setAccessKeyId(ACCESS_KEY_ID)
                    .setAccessKeySecret(ACCESS_KEY_SECRET);
            // 配置 Endpoint。中国站请使用dysmsapi.aliyuncs.com
            config.endpoint = "dysmsapi.aliyuncs.com";
            return new Client(config);
        }catch (Exception e){
            // 错误 message
            log.error(e.getMessage());
            throw new RenException(ErrorCode.SMS_CONNECTION_FAILED);
        }
    }
}
