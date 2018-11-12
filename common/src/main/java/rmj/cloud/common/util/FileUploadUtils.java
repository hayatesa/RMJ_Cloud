package rmj.cloud.common.util;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileUploadUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadUtils.class);

    public static final String SUCCESS = "success";
    public static final String REASON = "reason";
    public static final String FOLDER_PATH = "folderPath";
    public static final String FILE_NAMES = "fileNames";
    private static final String SEPARATOR = "/";
    private static final String TEMP = "pic";
    private static final String PDF_TEMP = "pdf";
    private static final String DOT = ".";

    /**
     * 保存文件
     *
     * @param folderPath
     * @param imgs
     * @return 返回Map
     * {
     * key:SUCCESS
     * value: Boolean 成功与否
     * }
     * <p>
     * {
     * key:reason
     * value: '对应失败的原因'
     * }
     */
    public static Map<String, Object> saveImgFile(String absolutePath, String folderPath, List<InputStream> imgs) {
        Assert.notNull(absolutePath, "absolutePath can not null");
        Assert.notNull(folderPath, "folderPath can not null");

        Map<String, Object> pathMap = new HashMap<String, Object>();
        if (CollectionUtils.isEmpty(imgs)) {
            pathMap.put(SUCCESS, false);
            pathMap.put(REASON, "上传图片集合为空");
            return pathMap;
        }
        //创建实际操作文件夹
        pathMap.put(FOLDER_PATH, folderPath);

        int index = 0;
        File fileList = new File(absolutePath + folderPath);
        if (fileList.isDirectory()) {
            File[] fileNames = fileList.listFiles();
            if (CollectionUtils.isNotEmpty(Arrays.asList(fileNames))) {
                for (File directory : fileNames) {
                    if (!directory.isDirectory()) {
                        index++;
                    }
                }
            }

        }

        StringBuilder fileNames = new StringBuilder();

        //进行文件写入
        for (InputStream is : imgs) {
            File f = null;
            FileOutputStream fos = null;
            try {
                BufferedImage image = ImageIO.read(is);
                String fileName = ++index + ".jpg";
                fileNames.append(fileName);
                fileNames.append(";");
                f = new File(absolutePath + folderPath + FolderGetter.FILE_SEPARATOR + fileName);
                fos = new FileOutputStream(absolutePath + folderPath + FolderGetter.FILE_SEPARATOR + fileName);
                f.createNewFile();
                ImageIO.write(image, "jpg", fos);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                pathMap.put(SUCCESS, false);
                pathMap.put(REASON, "图片上传失败");
                return pathMap;
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info(FILE_NAMES + ": {}", fileNames);
        pathMap.put(FILE_NAMES, fileNames.toString().substring(0, fileNames.toString().lastIndexOf(";")));
        pathMap.put(FOLDER_PATH, folderPath);
        pathMap.put(SUCCESS, true);
        return pathMap;
    }

    /**
     * 上传临时文件
     *
     * @param tempFolderPath
     * @param imgIs
     * @param imgFileName
     * @return
     */
    public static Map<String, Object> saveTempImgFile(String absolutePath, String tempFolderPath, InputStream imgIs,
            String imgFileName) {
        Assert.notNull(imgFileName, "imgFileName can not null");
        Assert.notNull(tempFolderPath, "tempFolderPath can not null");
        String fileType = StringUtils.substring(imgFileName, imgFileName.lastIndexOf('.') + 1);
        Assert.isTrue(StringUtils.isNotBlank(fileType), "imgFileName get file type error");

        Map<String, Object> pathMap = new HashMap<String, Object>();
        if (imgIs == null) {
            pathMap.put(SUCCESS, false);
            pathMap.put(REASON, "临时图片为空");
            return pathMap;
        }
        //创建实际操作临时文件夹
        pathMap.put(FOLDER_PATH, tempFolderPath);

        File f = null;
        FileOutputStream fos = null;
        try {
            BufferedImage image = ImageIO.read(imgIs);
            f = new File(absolutePath + tempFolderPath + FolderGetter.FILE_SEPARATOR + imgFileName);
            fos = new FileOutputStream(absolutePath + tempFolderPath + FolderGetter.FILE_SEPARATOR + imgFileName);
            f.createNewFile();
            ImageIO.write(image, fileType, fos);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            pathMap.put(SUCCESS, false);
            pathMap.put(REASON, "图片上传失败");
            return pathMap;
        } finally {
            try {
                if (imgIs != null) {
                    imgIs.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LOG.info(FILE_NAMES + ": {}", imgFileName);
        pathMap.put(FILE_NAMES, imgFileName);
        pathMap.put(FOLDER_PATH, tempFolderPath);
        pathMap.put(SUCCESS, true);
        return pathMap;
    }

    /**
     * 保留文件转移到新的文件夹
     *
     * @param absolutePath    操作服务器路径
     * @param newFolderPath   相对路径
     * @param reservedPicPath 要移动的路径 相对路径，以 ; 分隔
     * @return 新的文件名
     * 样例：
     * 1.jpg;2.jpg;
     */
    public static String reservedImgToNewFolder(String absolutePath, String newFolderPath, String reservedPicPath)
            throws IOException {
        Assert.notNull(absolutePath, "absolutePath is not null");
        Assert.notNull(newFolderPath, "folderPath is not null");

        StringBuilder fileNames = new StringBuilder();

        int index = 0;

        if (StringUtils.isNotEmpty(reservedPicPath)) {
            String[] reservedArr = reservedPicPath.split(";");
            for (String reservedPath : reservedArr) {
                File file = new File(absolutePath + reservedPath);
                if (!file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    continue;
                }
                String fileType = StringUtils.substring(file.getName(), file.getName().lastIndexOf('.'));

                String fileName = ++index + fileType;
                fileNames.append(fileName);
                fileNames.append(";");
                String descFilePath = absolutePath + newFolderPath;
                FileUtils.moveFileToDirectory(file, new File(descFilePath), true);
                File descFile = new File(descFilePath + FolderGetter.FILE_SEPARATOR + file.getName());
                if (descFile.exists()) {
                    descFile.renameTo(new File(descFilePath + FolderGetter.FILE_SEPARATOR + fileName));
                }
            }
        }

        return fileNames.toString();
    }

    /**
     * 临时文件转移到正式文件夹
     *
     * @param absolutePath
     * @param folderPath
     * @param tempFolderPath
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map tempToFolderPath(String absolutePath, String folderPath, String tempFolderPath)
            throws IOException {
        Assert.notNull(absolutePath, "absolutePath is not null");
        Assert.notNull(folderPath, "folderPath is not null");
        Assert.notNull(tempFolderPath, "tempFolderPath is not null");

        Map<String, Object> pathMap = new HashMap<String, Object>();
        int index = 0;
        StringBuilder fileNames = new StringBuilder();

        File tempFolder = new File(absolutePath + tempFolderPath);
        File folder = new File(absolutePath + folderPath);

        for (File tempFile : tempFolder.listFiles()) {
            FileUtils.moveFileToDirectory(tempFile, folder, true);
        }

        //删除临时文件夹
        FileUtils.deleteQuietly(tempFolder);
        String folderTemp = folder.getPath() + FolderGetter.FILE_SEPARATOR + FolderGetter.TEMP;
        FileUtils.deleteQuietly(new File(folderTemp));

        //移动到新文件 并进行重命名
        File[] files = folder.listFiles();
        if (CollectionUtils.isNotEmpty(Arrays.asList(files))) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    String fileType = StringUtils.substring(name, name.lastIndexOf('.'));

                    String fileName = ++index + fileType;
                    fileNames.append(fileName);
                    fileNames.append(";");
                    file.renameTo(new File(file.getParent() + FolderGetter.FILE_SEPARATOR + fileName));
                }
            }
        } else {
            pathMap.put(SUCCESS, true);
            pathMap.put(REASON, "temp 文件夹没有对应的文件需要移动");
        }

        LOG.info(FILE_NAMES + ": {}", fileNames);
        pathMap.put(FILE_NAMES, fileNames.toString().substring(0, fileNames.toString().lastIndexOf(";")));
        pathMap.put(FOLDER_PATH, folderPath);
        pathMap.put(SUCCESS, true);
        return pathMap;
    }

    /**
     * 正式文件夹移到临时文件转
     *
     * @param absolutePath
     * @param folderPath
     * @return
     */
    public static void folderPathToTemp(String absolutePath, String folderPath, String tempFolderPath)
            throws IOException {
        Assert.notNull(absolutePath, "absolutePath is not null");
        Assert.notNull(folderPath, "folderPath is not null");
        Assert.notNull(tempFolderPath, "tempFolderPath is not null");

        File tempFolder = new File(absolutePath + tempFolderPath);
        File folder = new File(absolutePath + folderPath);
        FileUtils.copyDirectory(folder, tempFolder);

        //删除临时文件夹下的 临时文件夹
        FileUtils.deleteDirectory(
                new File(tempFolder.getPath() + FolderGetter.FILE_SEPARATOR + FolderGetter.TEMP));
    }

    /**
     * 如果文件名不存在，则删除文件夹下的所有文件
     * 如果文件名存在，则删除文件夹的文件
     *
     * @param absolutePath 文件服务器的路径
     * @param path         文件夹路径（可以是folderPath ， tempFolderPath）
     * @param fileNames    文件名
     */
    public static void deleteFile(String absolutePath, String path, String... fileNames) throws IOException {
        Assert.notNull(absolutePath, "absolutePath is not null");
        Assert.notNull(path, "path is not null");
        if (CollectionUtils.isNotEmpty(Arrays.asList(fileNames))) {
            for (String fileName : fileNames) {
                new File(absolutePath + path + FolderGetter.FILE_SEPARATOR + fileName).delete();
            }
        } else {
            FileUtils.deleteDirectory(new File(absolutePath + path));
        }
    }

    /**
     * 生成Html文件
     *
     * @param absolutePath
     * @param folderPath
     * @param htmlContent
     * @return 返回相对html路径
     */
    public static String createHtmlFile(String absolutePath, String folderPath, String htmlFileName, String htmlContent,
            String vmPath) throws IOException {
        Assert.notNull(absolutePath, "absolutePath can not be null");
        Assert.notNull(folderPath, "folderPath can not be null");

        String htmlFilePath = folderPath + FolderGetter.FILE_SEPARATOR + htmlFileName;
        Map<String, String> map = Maps.newHashMap();
        map.put("body", htmlContent);
        final String resHtmlContent = VelocityUtils.velocityToString("storetemplate.vm", map, vmPath);
        FileUtils.write(new File(absolutePath + htmlFilePath), resHtmlContent, "UTF-8", false);

        return htmlFilePath;
    }

    /**
     * 根据浏览器进行文件名称转换
     * @param userAgent
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String convertFileName(String userAgent, String fileName) throws UnsupportedEncodingException {
        if (userAgent.toLowerCase().indexOf("firefox") > 0) {
            fileName = new String(fileName.getBytes(), "ISO8859-1"); // firefox浏览器
        } else if (userAgent.toLowerCase().indexOf("msie") > 0 || userAgent.toLowerCase().indexOf("like gecko") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
            fileName = fileName.replace("+", "%20");
        } else if (userAgent.toLowerCase().indexOf("chrome") > 0) {
            fileName = new String(fileName.getBytes(), "ISO8859-1");// 谷歌
        }
        return fileName;
    }

    /**
     * 创建临时文件夹
     *
     * @param rootUrl 临时目录所在的根目录
     * @return 返回在rootUrl目录下的相对路径 比如：TEMP/20170707/
     */
    public static String mkdirTempFolder(String rootUrl) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String folderUrl = TEMP + SEPARATOR + sdf.format(new Date()) + SEPARATOR;
        File f = new File(rootUrl + SEPARATOR + folderUrl);
        if (!f.exists())
            f.mkdirs();

        return folderUrl;
    }

    /**
     * 创建PDF临时文件夹
     *
     * @param rootUrl 临时目录所在的根目录
     * @return 返回在rootUrl目录下的相对路径 比如：TEMP/20170707/
     */
    public static String mkdirPDFTempFolder(String rootUrl) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String folderUrl = PDF_TEMP + SEPARATOR + sdf.format(new Date()) + SEPARATOR;
        File f = new File(rootUrl + SEPARATOR + folderUrl);
        if (!f.exists())
            f.mkdirs();

        return folderUrl;
    }

    public static void main(String[] args) throws IOException {
        //        String ap = "C:/etc/lifetouch/userfiles";

        //                String folderPath1 = FolderGetter
        //                        .getFolderPath("C:/etc/lifetouch/userfiles", "cm", FolderGetter.uuid(), "_NOTICE", true);
        //                System.out.println("folderPath1:"+folderPath1);
        //
        //                String folderPath2 = FolderGetter
        //                        .getFolderPath("C:/etc/lifetouch/userfiles", "cm", FolderGetter.uuid(), "_NOTICE", false);
        //                System.out.println("folderPath2:"+folderPath2);
        //
        //                String folderPath3 = FolderGetter
        //                        .getFolderPath("C:/etc/lifetouch/userfiles", "cm", null, "_NOTICE", false);
        //                System.out.println("folderPath:3"+folderPath3);
        //
        //                System.out.println("getDBFolderPath:"+FolderGetter.getDBFolderPath("C:/etc/lifetouch/userfiles", folderPath1));
        //
        //                String tempFolderPath1 = FolderGetter
        //                        .getTempFolderPath(ap,folderPath1,false);
        //                System.out.println("tempFolderPath1:"+tempFolderPath1);
        //
        //                String tempFolderPath2 = FolderGetter
        //                        .getTempFolderPath(ap,folderPath2,true);
        //                System.out.println("tempFolderPath2:"+tempFolderPath2);
        //
        //                String tempFolderPath3 = FolderGetter
        //                        .getTempFolderPath(ap,folderPath3,true);
        //                System.out.println("tempFolderPath3:"+tempFolderPath3);

        //                List<InputStream> isList = Lists.newArrayList();
        //                isList.add(new FileInputStream("C:\\Users\\Public\\Pictures\\Sample Pictures\\1448071981933706.jpg"));
        //                isList.add(new FileInputStream("C:\\Users\\Public\\Pictures\\Sample Pictures\\1448071981933706.jpg"));
        //                isList.add(new FileInputStream("C:\\Users\\Public\\Pictures\\Sample Pictures\\1448071981933706.jpg"));
        //                isList.add(new FileInputStream("C:\\Users\\Public\\Pictures\\Sample Pictures\\1448071981933706.jpg"));
        //                Map fileMap = FileUploadUtils.saveImgFile(ap,"/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639",isList);
        //                System.out.println(fileMap);

        //                Map fileMap2 = FileUploadUtils.saveTempImgFile(ap,"/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639/_TEMP/20180123",new FileInputStream("C:\\Users\\Public\\Pictures\\Sample Pictures\\1448071981933706.jpg"),"temp.jpg");
        //                System.out.println(fileMap2);

        //                FileUploadUtils.tempToFolderPath(ap,"/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639","/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639/_TEMP/20180123");
        //                FileUploadUtils.folderPathToTemp(ap,
        //                        "/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639",
        //                        "/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639/_TEMP/20180123");

        //                FileUploadUtils.deleteFile(ap,"/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639/_TEMP/20180123","1.jpg","2.jpg");
        //                FileUploadUtils.deleteFile("C:/etc/lifetouch/userfiles","/cm/c225bca37f904a10837c1630e5e4e7c3/_NOTICE/d09e46c2db6c4625b670f33c41f37639/_TEMP/20180123");

        //        FileUploadUtils.createHtmlFile("C:/etc/lifetouch/userfiles","/cm/872a2b68999448f8935141a7c621eeaf/_NOTICE/_TEMP/20180123/68dfaa0382fe45599287ce8acd9c7312" ,"index.html","咕咕咕咕大大大12312dafefeawe——5");
    }
}
