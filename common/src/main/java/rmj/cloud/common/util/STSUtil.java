package rmj.cloud.common.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STSUtil {

    private static final Logger LOG = LoggerFactory.getLogger(STSUtil.class);

    private String stsEndpoint;
    private String stsRoleArn;
    private String stsDuration;
    private String ossAccessKeyId;
    private String ossAccessKeySecret;

    public STSUtil(String stsEndpoint, String stsRoleArn, String stsDuration, String ossAccessKeyId,
            String ossAccessKeySecret) {
        this.stsEndpoint = stsEndpoint;
        this.stsRoleArn = stsRoleArn;
        this.stsDuration = stsDuration;
        this.ossAccessKeyId = ossAccessKeyId;
        this.ossAccessKeySecret = ossAccessKeySecret;
    }

    public AssumeRoleResponse.Credentials generateSTSToken() {
        AssumeRoleResponse.Credentials token = null;
        try {
            // 添加endpoint（直接使用STS endpoint，前两个参数留空，无需添加region ID）
            DefaultProfile.addEndpoint("", "", "Sts", stsEndpoint);
            // 构造default profile（参数留空，无需添加region ID）
            IClientProfile profile = DefaultProfile.getProfile("", ossAccessKeyId, ossAccessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(stsRoleArn);
            request.setRoleSessionName("session-name");
            request.setDurationSeconds(Long.parseLong(stsDuration));
            final AssumeRoleResponse response = client.getAcsResponse(request);
            token = response.getCredentials();
        } catch (ClientException e) {
            LOG.error("Failed：");
            LOG.error("Error code: " + e.getErrCode());
            LOG.error("Error message: " + e.getErrMsg());
            LOG.error("RequestId: " + e.getRequestId());
        }
        return token;
    }
}
