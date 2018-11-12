package rmj.cloud.common.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OSSUtil {

    private static final Logger log = LoggerFactory.getLogger(OSSUtil.class);

    public static final String HTTPS_PREFIX = "https://";
    public static final String DEFAULT_EXPIRED_SECONDS = "3600";
    public static final String DOT = ".";
    public static final int LIST_MAX_KEYS = 100;

    private String ossHost;
    private String ossAccessHost;
    private String ossBucketName;
    private String ossAccessKeyId;
    private String ossAccessKeySecret;
    private String expirationSeconds; //默认一个小时 3600s
    private String OSS_BASE_PATH; //访问的base path

    public String getOssAccessKeyId() {
        return ossAccessKeyId;
    }

    public OSSUtil(String ossHost, String ossAccessHost, String ossBucketName, String ossAccessKeyId,
            String ossAccessKeySecret) {
        this(ossHost, ossAccessHost, ossBucketName, ossAccessKeyId, ossAccessKeySecret, DEFAULT_EXPIRED_SECONDS);
    }

    public OSSUtil(String ossHost, String ossAccessHost, String ossBucketName, String ossAccessKeyId,
            String ossAccessKeySecret, String expirationSeconds) {
        this.ossHost = ossHost;
        this.ossAccessHost = ossAccessHost;
        this.ossBucketName = ossBucketName;
        this.ossAccessKeyId = ossAccessKeyId;
        this.ossAccessKeySecret = ossAccessKeySecret;
        this.expirationSeconds = StringUtils.isEmpty(StringUtils.trimToEmpty(expirationSeconds))
                ? DEFAULT_EXPIRED_SECONDS : expirationSeconds;
        OSS_BASE_PATH = HTTPS_PREFIX + ossBucketName + DOT + ossAccessHost + "/";
    }

    /**
     * 根据空间名和文件名获取签名后的url
     * @param bucketName
     * @param fileKey
     * @return
     */
    public URL generatePresignedUrl(String bucketName, String fileKey) {
        if (StringUtils.isEmpty(fileKey))
            return null;

        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossAccessHost, ossAccessKeyId, ossAccessKeySecret);
        try {
            bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
                    fileKey);
            // 设置URL过期时间为1小时。
            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            Date expiration = Date
                    .from(now.plusSeconds(new Long(expirationSeconds)).atZone(ZoneId.systemDefault()).toInstant());
            generatePresignedUrlRequest.setExpiration(expiration);
            // 生成签名URL。
            return ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 根据文件名获取签名url
     * @param fileKey
     * @return
     */
    public URL generatePresignedUrl(String fileKey) {
        return generatePresignedUrl(null, fileKey);
    }

    /**
     * 上传本地文件到OSS
     *
     * @param fileKey 文件名
     * @param file    本地文件
     * @return
     */
    public boolean uploadLocalFile(String fileKey, File file) {
        return uploadLocalFile(null, fileKey, file, null);
    }

    /**
     * 流式上传到OSS
     *
     * @param fileKey     文件名
     * @param inputStream 流
     * @return
     */
    public boolean uploadInputStream(String fileKey, InputStream inputStream) {
        return uploadInputStream(null, fileKey, inputStream, null);
    }

    /**
     * 上传本地文件到OSS，并接受OSS服务器回调
     *
     * @param fileKey     文件名
     * @param file        本地文件
     * @param callbackMap 回调参数  key: callbackUrl  value:自定义参数
     * @return
     */
    public boolean uploadLocalFile(String fileKey, File file, Map<String, Map<String, String>> callbackMap) {
        return uploadLocalFile(null, fileKey, file, callbackMap);
    }

    /**
     * 流式上传到OSS
     *
     * @param fileKey     文件名
     * @param inputStream 流
     * @param callbackMap 回调参数  key: callbackUrl  value:自定义参数
     * @return
     */
    public boolean uploadInputStream(String fileKey, InputStream inputStream,
            Map<String, Map<String, String>> callbackMap) {
        return uploadInputStream(null, fileKey, inputStream, callbackMap);
    }

    /**
     * 删除文件
     *
     * @param fileKey
     * @return
     */
    public boolean deleteFile(String fileKey) {
        if (StringUtils.isEmpty(fileKey))
            return true;
        return deleteFile(null, fileKey);
    }

    public boolean uploadLocalFile(String bucketName, String fileKey, File file,
            Map<String, Map<String, String>> callBackMap) {
        if (StringUtils.isEmpty(fileKey) || !file.exists()) {
            return true;
        }
        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossHost, ossAccessKeyId, ossAccessKeySecret);
        try {
            bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file);
            putObjectRequest
                    .<PutObjectRequest> withProgressListener(new OSSObjectProgressListener(OSSTransferType.Upload));
            putCallBackInfo(putObjectRequest, callBackMap);
            ossClient.putObject(putObjectRequest);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    public boolean uploadInputStream(String bucketName, String fileKey, InputStream inputStream,
            Map<String, Map<String, String>> callBackMap) {
        if (StringUtils.isEmpty(fileKey) || inputStream == null) {
            return true;
        }
        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossHost, ossAccessKeyId, ossAccessKeySecret);
        try {
            bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, inputStream);
            putObjectRequest
                    .<PutObjectRequest> withProgressListener(new OSSObjectProgressListener(OSSTransferType.Upload));
            putCallBackInfo(putObjectRequest, callBackMap);
            ossClient.putObject(putObjectRequest);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    private void putCallBackInfo(PutObjectRequest putObjectRequest, Map<String, Map<String, String>> callBackMap) {
        if (MapUtils.isEmpty(callBackMap)) {
            return;
        }
        String callbackUrl = callBackMap.keySet().iterator().next();
        Callback callback = new Callback();
        callback.setCallbackUrl(callbackUrl);
        callback.setCallbackHost(ossHost);
        callback.setCallbackBody("{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}}");
        callback.setCalbackBodyType(Callback.CalbackBodyType.JSON);
        callback.setCallbackVar(callBackMap.get(callbackUrl));
        putObjectRequest.setCallback(callback);
    }

    /**
     * 下载OSS对象到本地文件
     * @param fileKey  OSS对象名
     * @param localFileName 本地文件路径
     * @return
     */
    public File downloadToLocalFile(String fileKey, String localFileName) {
        return downloadToLocalFile(null, fileKey, localFileName);
    }

    /**
     * 下载一个空间里面的对象到本地文件
     * @param bucketName    空间名称
     * @param fileKey  OSS对象名
     * @param localFileName 本地文件路径
     * @return
     */
    public File downloadToLocalFile(String bucketName, String fileKey, String localFileName) {
        if (StringUtils.isAnyEmpty(fileKey, localFileName)) {
            return null;
        }
        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossHost, ossAccessKeyId, ossAccessKeySecret);
        File file = null;
        try {
            bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
            //DB保存的是全路径，需要截取为相对路径
            String basePath = getOssBasePath(bucketName);
            if (fileKey.startsWith(basePath)) {
                fileKey = fileKey.substring(fileKey.indexOf(basePath) + basePath.length() + 1);
            }
            file = new File(localFileName);
            if (!file.exists()) {
                File folder = new File(localFileName.substring(0, localFileName.lastIndexOf("/")));
                if (!folder.exists()) {
                    folder.mkdirs();
                }
            }
            ossClient.getObject(new GetObjectRequest(bucketName, fileKey), file);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return file;
    }

    /**
     * 删除一个空间里的一个对象
     * @param bucketName 空间名
     * @param fileKey  对象名
     * @return
     */
    public boolean deleteFile(String bucketName, String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return true;
        }
        if (fileKey.startsWith(OSS_BASE_PATH)) {
            fileKey = fileKey.replace(OSS_BASE_PATH, "");
        }
        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossHost, ossAccessKeyId, ossAccessKeySecret);
        bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
        try {
            //DB保存的是全路径，需要截取为相对路径
            String basePath = getOssBasePath(bucketName);
            if (fileKey.startsWith(basePath)) {
                fileKey = fileKey.substring(fileKey.indexOf(basePath) + basePath.length() + 1);
            }
            log.info("Start to delete " + fileKey + " from OSS");
            ossClient.deleteObject(bucketName, fileKey);
            log.info("Deleted " + fileKey + " from OSS successfully");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 拷贝OSS对象
     * @param sourceFileKey
     * @param descFileKey
     */
    public void copyObject(String sourceFileKey, String descFileKey) {
        copyObject(null, sourceFileKey, descFileKey);
    }

    /**
     * 拷贝OSS对象
     * @param bucketName
     * @param sourceFileKey
     * @param destFileKey
     */
    public void copyObject(String bucketName, String sourceFileKey, String destFileKey) {
        if (StringUtils.isAnyEmpty(sourceFileKey, destFileKey)) {
            return;
        }
        if (sourceFileKey.startsWith(OSS_BASE_PATH)) {
            sourceFileKey = sourceFileKey.replace(OSS_BASE_PATH, "");
        }
        if (destFileKey.startsWith(OSS_BASE_PATH)) {
            destFileKey = destFileKey.replace(OSS_BASE_PATH, "");
        }
        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossHost, ossAccessKeyId, ossAccessKeySecret);
        try {
            bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
            //DB保存的是全路径，需要截取为相对路径
            String basePath = getOssBasePath(bucketName);
            if (sourceFileKey.startsWith(basePath)) {
                sourceFileKey = sourceFileKey.substring(sourceFileKey.indexOf(basePath) + basePath.length() + 1);
            }
            if (destFileKey.startsWith(basePath)) {
                destFileKey = destFileKey.substring(destFileKey.indexOf(basePath) + basePath.length() + 1);
            }
            if (sourceFileKey.equals(destFileKey)) {
                return;
            }
            // 拷贝文件。
            ossClient.copyObject(bucketName, sourceFileKey, bucketName, destFileKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 获取一个指定目录下的所有文件
     * @param folder
     * @return
     */
    public List<String> getAllFileByFolder(String folder) {
        return getAllFileByFolder(null, folder);
    }

    /**
     * 获取一个空间里指定目录下的所有文件
     * @param bucketName
     * @param folder
     * @return
     */
    public List<String> getAllFileByFolder(String bucketName, String folder) {
        if (StringUtils.isEmpty(folder)) {
            return Lists.newArrayList();
        }
        OSSClient ossClient = new OSSClient(HTTPS_PREFIX + ossHost, ossAccessKeyId, ossAccessKeySecret);
        List<String> fileNameList = Lists.newArrayList();
        try {
            bucketName = StringUtils.isEmpty(bucketName) ? ossBucketName : bucketName;
            String nextMarker = null;
            ObjectListing objectListing;
            do {
                objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withPrefix(folder)
                        .withMarker(nextMarker).withMaxKeys(LIST_MAX_KEYS));
                List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    fileNameList.add(s.getKey());
                }
                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return fileNameList;
    }

    enum OSSTransferType {
        Download, Upload
    }

    /**
     * 上传或下载监听器
     */
    static class OSSObjectProgressListener implements ProgressListener {
        private long bytesTransferred = 0;
        private long totalBytes = -1;
        private boolean succeed = false;
        private OSSTransferType transferType;

        public OSSObjectProgressListener(OSSTransferType transferType) {
            this.transferType = transferType;
        }

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            long bytes = progressEvent.getBytes();
            ProgressEventType eventType = progressEvent.getEventType();
            switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                log.info("Start to {}......", this.transferType.name().toLowerCase());
                break;
            case REQUEST_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                log.info(this.totalBytes + " bytes in total will be uploaded to OSS");
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesTransferred += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesTransferred * 100.0 / this.totalBytes);
                    log.info(bytes + " bytes have been written at this time, upload progress: " + percent + "%("
                            + this.bytesTransferred + "/" + this.totalBytes + ")");
                } else {
                    log.info(bytes + " bytes have been written at this time, upload ratio: unknown" + "("
                            + this.bytesTransferred + "/...)");
                }
                break;
            case RESPONSE_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                log.info(this.totalBytes + " bytes in total will be downloaded to a local file");
                break;
            case RESPONSE_BYTE_TRANSFER_EVENT:
                this.bytesTransferred += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesTransferred * 100.0 / this.totalBytes);
                    log.info(bytes + " bytes have been read at this time, download progress: " + percent + "%("
                            + this.bytesTransferred + "/" + this.totalBytes + ")");
                } else {
                    log.info(bytes + " bytes have been read at this time, download ratio: unknown" + "("
                            + this.bytesTransferred + "/...)");
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                log.info("Succeed to {}, " + this.bytesTransferred + " bytes have been transferred in total",
                        this.transferType.name().toLowerCase());
                break;
            case TRANSFER_FAILED_EVENT:
                log.info("Failed to {}, " + this.bytesTransferred + " bytes have been transferred",
                        this.transferType.name().toLowerCase());
                break;
            default:
                break;
            }
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

    private String getOssBasePath(String bucketName) {
        return HTTPS_PREFIX + bucketName + DOT + ossAccessHost;
    }

}
