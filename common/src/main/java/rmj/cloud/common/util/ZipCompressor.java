package rmj.cloud.common.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipCompressor {

    private static Logger LOG = LoggerFactory.getLogger(ZipCompressor.class);

    static final int BUFFER = 8192;

    public static void main(String[] args) {
        //压缩结果
        ZipCompressor zipCompressor = new ZipCompressor();
        //需要压缩的文件目录
        zipCompressor.compress("D:/qrCode");
    }

    private File zipFile;

    //有保存的目录路径
    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
    }

    public ZipCompressor() {
    }

    /**
     * 被压缩文件
     * @param srcPathName
     * @return
     */
    public InputStream compress(String srcPathName) {
        File file = new File(srcPathName);
        if (!file.exists())
            throw new RuntimeException(srcPathName + "不存在！");
        try {

            OutputStream outputStream;
            if (zipFile != null) {
                outputStream = new FileOutputStream(zipFile);
            } else {
                outputStream = new ByteArrayOutputStream();
            }
            CheckedOutputStream cos = new CheckedOutputStream(outputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir);
            out.close();

            //如果写到ByteArrayOutputStream中,则有返回
            if (outputStream instanceof ByteArrayOutputStream) {
                return new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compress(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            LOG.debug("ZipCompressor 压缩：" + basedir + file.getName());
            this.compressDirectory(file, out, basedir);
        } else {
            LOG.debug("ZipCompressor 压缩：" + basedir + file.getName());
            this.compressFile(file, out, basedir);
        }
    }

    /** 压缩一个目录 */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /** 压缩一个文件 */
    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解压zip格式文件
     *
     * @param originFile zip文件。
     * @param targetDir  要解压到的目标路径。
     * @return 如果目标文件不是zip文件则返回false。
     * @throws Exception 如果发生错误。
     */
    public static boolean decompressZip(File originFile, String targetDir) throws Exception {
        if (!targetDir.endsWith(File.separator)) {
            targetDir += File.separator;
        }
        ZipFile zipFile = new ZipFile(originFile);
        ZipEntry zipEntry;
        Enumeration<ZipEntry> entry = (Enumeration<ZipEntry>) zipFile.entries();
        while (entry.hasMoreElements()) {
            zipEntry = entry.nextElement();
            String fileName = zipEntry.getName();
            File outputFile = new File(targetDir + fileName);
            if (zipEntry.isDirectory()) {
                forceMkdirs(outputFile);
                continue;
            } else if (!outputFile.getParentFile().exists()) {
                forceMkdirs(outputFile.getParent());
            }
            OutputStream outputStream = new FileOutputStream(outputFile);
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            int len;
            byte[] buffer = new byte[8192];
            while (-1 != (len = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        zipFile.close();
        return true;
    }

    public static File forceMkdirs(String pathName) {
        return forceMkdirs(new File(pathName));
    }

    public static File forceMkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        return file;
    }

}
