package rmj.cloud.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FolderGetter {

    public static final String FILE_SEPARATOR = "/";

    public static final String TEMP = "_TEMP";

    /**
     * 根据小区ID、FunctionType生成 folderPath
     *
     * @param cmInfoId
     * @param functionType
     * @return
     * 已过时，创建文件夹时，需要加多一个父文件夹
     */
    @Deprecated
    public static String getFolderURL(String cmInfoId, String functionType) {
        if (StringUtils.isBlank(cmInfoId)) {
            throw new RuntimeException("cmInfoId 为空");
        }
        return cmInfoId + FILE_SEPARATOR + functionType + FILE_SEPARATOR + uuid() + FILE_SEPARATOR;
    }

    /**
     * 根据项目，实体id（小区ID/店铺ID等）、FunctionType生成 folderPath
     *
     * @param projectName 项目名： cm  store
     * @param entityId
     * @param functionType
     * @return
     */
    public static String getFolderURL(String projectName, String entityId, String functionType) {
        if (StringUtils.isBlank(projectName)) {
            throw new RuntimeException("projectName 为空");
        }
        if (StringUtils.isBlank(entityId)) {
            throw new RuntimeException("entityId 为空");
        }
        return projectName + FILE_SEPARATOR + entityId + FILE_SEPARATOR + functionType + FILE_SEPARATOR + uuid()
                + FILE_SEPARATOR;
    }

    /**
     * 根据参数生成没有UUID的folderPath
     *
     * @params 参数
     * @return
     */
    public static String getNonUuidFolderByParams(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            throw new RuntimeException("params 为空");
        }
        StringBuilder folder = new StringBuilder();
        for (String param : params) {
            if (param == null)
                continue;
            if (StringUtils.isEmpty(StringUtils.trim(param)))
                continue;
            folder.append(param);
            folder.append(FILE_SEPARATOR);
        }
        return folder.toString();
    }

    /**
     * 根据参数生成有UUID的folderPath
     *
     * @params 参数
     * @return
     */
    public static String getFolderByParams(String... params) {
        return getNonUuidFolderByParams(params) + uuid() + FILE_SEPARATOR;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 根据参数生成 二维码图片 存放的目录
     * @param absolutePath
     * @param cmInfoId
     * @param functionType
     * @return
     */
    public static String getFolderPathAndCreateMkdirsForQrCode(String absolutePath, String cmInfoId,
            String functionType) {
        if (StringUtils.isBlank(cmInfoId)) {
            throw new RuntimeException("cmInfoId 为空");
        }
        String path = absolutePath + FILE_SEPARATOR + cmInfoId + FILE_SEPARATOR + functionType;
        return createMkdirs(path);
    }

    public static String createMkdirs(String path) {
        File dir = new File(path);

        if (dir.exists()) {// 判断目录是否存在
            return path;
        }

        if (dir.mkdirs()) {// 创建目标目录
            return path;
        } else {
            throw new RuntimeException("path 目录创建失败");
        }
    }

    /**
     * 创建对应目录文件夹
     * 能进行真实操作的文件夹路径
     * @param absolutePath 服务器路径
     * @param moduleTypePath 大模块名
     * @param id 对应模块id 可以为空，若为空则变追加
     * @param functionType 功能名
     * @param endWithUUID 结尾是否需要UUID
     * @return
     * 样例：
    商户申请	商户申请资料上传	/etc/lifetouch/userfiles	/shop/biz/_BIZ_APP/{UUID}
    店铺详情草稿	编辑店铺详情	/etc/lifetouch/userfiles	/shop/store/{storeId}/_INFO/_DRFT
    店铺详情草稿-图片	存放店铺详情图片文件	/etc/lifetouch/userfiles	/shop/store/{storeId}/_INFO/_DRFT/_IMG
    店铺详情草稿-媒体	存放店铺详情媒体文件	/etc/lifetouch/userfiles	/shop/store/{storeId}/_INFO/_DRFT/_MEDIA
    店铺详情上线	店铺详情（只读）	/etc/lifetouch/userfiles	/shop/store/{storeId}/_INFO/_ONLINE
    店铺详情上线-图片	存放店铺详情图片文件（只读）	/etc/lifetouch/userfiles	/shop/store/{storeId}/_INFO/_ONLINE/_IMG
    店铺详情上线-媒体	存放店铺详情媒体文件（只读）	/etc/lifetouch/userfiles	/shop/store/{storeId}/_INFO/_ONLINE_MEDIA

     * absolutePath/moduleTypePath/id/functionType/endWithUUID
     */
    public static String getFolderPath(String absolutePath, String moduleTypePath, String id, String functionType,
            Boolean endWithUUID) {
        Assert.notNull(absolutePath, "absolutePath can not be null");
        Assert.notNull(moduleTypePath, "moduleTypePath can not be null");
        Assert.notNull(functionType, "functionType can not be null");
        Assert.notNull(endWithUUID, "endWithUUID can not be null");

        StringBuilder path = new StringBuilder();
        path.append(absolutePath);
        path.append(FILE_SEPARATOR).append(moduleTypePath.toLowerCase());
        if (StringUtils.isNoneBlank(id)) {
            path.append(FILE_SEPARATOR).append(id);
        }
        path.append(FILE_SEPARATOR).append(functionType);
        if (endWithUUID) {
            path.append(FILE_SEPARATOR).append(uuid());
        }

        //返回对应的相对路径
        return getDBFolderPath(absolutePath, createMkdirs(path.toString()));
    }

    /**
     * 生成保存到db的路径 相对路径
     * 不能进行真实操作的文件夹路径，但这个是存到数据库的路径
     *
     * @param absolutePath
     * @param folderPath
     * @return
     *
     * 样例：
    商户申请	商户申请资料上传 /shop/biz/_BIZ_APP/{UUID}
    店铺详情草稿	编辑店铺详情 /shop/store/{storeId}/_INFO/_DRFT
    店铺详情草稿-图片	存放店铺详情图片文件 /shop/store/{storeId}/_INFO/_DRFT/_IMG
    店铺详情草稿-媒体	存放店铺详情媒体文件 /shop/store/{storeId}/_INFO/_DRFT/_MEDIA
    店铺详情上线	店铺详情（只读） /shop/store/{storeId}/_INFO/_ONLINE
    店铺详情上线-图片	存放店铺详情图片文件（只读） /shop/store/{storeId}/_INFO/_ONLINE/_IMG
    店铺详情上线-媒体	存放店铺详情媒体文件（只读） /shop/store/{storeId}/_INFO/_ONLINE_MEDIA
     */
    public static String getDBFolderPath(String absolutePath, String folderPath) {
        Assert.notNull(absolutePath, "moduleTypePath can not be null");
        Assert.notNull(folderPath, "functionType can not be null");

        return StringUtils.remove(folderPath, absolutePath);
    }

    /**
     * 获取临时目录的操作路径
     * 能进行真实操作的文件夹路径
     * @param folderPath
     * @return
     *
    absolutePath /shop/biz/_BIZ_APP/_TEMP/[yyyymmdd]/{UUID}
    absolutePath /shop/store/{storeId}/_INFO/_DRFT/_TEMP/[yyyymmdd]
    absolutePath /shop/store/{storeId}/_INFO/_DRFT/_TEMP/[yyyymmdd]
    absolutePath /shop/store/{storeId}/_INFO/_DRFT/_TEMP/[yyyymmdd]
     */
    public static String getTempFolderPath(String absolutePath, String folderPath, Boolean endWithUUID) {
        Assert.notNull(absolutePath, "absolutePath can not be null");
        Assert.notNull(folderPath, "folderPath can not be null");

        StringBuilder path = new StringBuilder(folderPath);
        path.append(FILE_SEPARATOR).append(TEMP);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String timeString = simpleDateFormat.format(new Date());
        path.append(FILE_SEPARATOR).append(timeString);

        if (endWithUUID) {
            path.append(FILE_SEPARATOR).append(uuid());
        }
        return StringUtils.remove(createMkdirs(absolutePath + path.toString()), absolutePath);
    }

    public static void main(String[] args) throws IOException {
        //        String folderPath1 = FolderGetter.getFolderPath("C:/etc/lifetouch/userfiles", "cm", uuid(), "_NOTICE", true);
        //        System.out.println(folderPath1);
        //
        //        String folderPath2 = FolderGetter.getFolderPath("C:/etc/lifetouch/userfiles", "cm", uuid(), "_NOTICE",
        //                false);
        //        System.out.println(folderPath2);
        //
        //        String folderPath3 = FolderGetter.getFolderPath("C:/etc/lifetouch/userfiles", "cm", null, "_NOTICE", false);
        //        System.out.println(folderPath3);
        //
        //        System.out.println(FolderGetter.getDBFolderPath("C:/etc/lifetouch/userfiles", folderPath1));

        //        String tempFolderPath1 = FolderGetter.getTempFolderPath(folderPath1, false);
        //        System.out.println(tempFolderPath1);
        //
        //        String tempFolderPath2 = FolderGetter.getTempFolderPath(folderPath2, true);
        //        System.out.println(tempFolderPath2);
        //
        //        String tempFolderPath3 = FolderGetter.getTempFolderPath(folderPath3, true);
        //        System.out.println(tempFolderPath3);

    }
}
