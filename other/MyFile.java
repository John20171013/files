package com.example.myfiles.other;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Copy;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyFile implements Comparable<MyFile> {
    public static String sortType;
    private String name;
    private long size;
    private long time;
    private String fileUrl;
    // 1 初始化用户身份信息(secretId, secretKey)
    static COSCredentials cred = new BasicCOSCredentials("AKIDHQO9g1H4kALHxMlO2PXP1NUKuGQKbPaV", "oNhmbATZQ2QbXEguufARUIrBIBpEOK1T");
    // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    static ClientConfig clientConfig = new ClientConfig(new com.qcloud.cos.region.Region("ap-shanghai"));
    // 3 生成cos客户端
    static COSClient cosclient = new COSClient(cred, clientConfig);
    // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
    static String bucketName = "junbaba-1253232442";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.getTime().toLocaleString();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public String getFileUrl(){
        return fileUrl;
    }
    public MyFile() {
    }



    public int compareTo(MyFile myFile){	// 覆写compareTo()方法，实现排序规则的应用
            switch (sortType){
                case "nameUp":{
                    return this.name.toLowerCase().compareTo(myFile.name.toLowerCase());
                }case "nameDown":{
                    return -this.name.toLowerCase().compareTo(myFile.name.toLowerCase());
                }case "sizeUp":{
                    if(this.size>myFile.size){ //正序排列
                        return 1 ;
                    }else if(this.size<myFile.size){
                        return -1 ;
                    }else{
                        return 0 ;
                    }
                }case "sizeDown":{
                    if(this.size>myFile.size){ //倒序排列
                        return -1 ;
                    }else if(this.size<myFile.size){
                        return 1 ;
                    }else{
                        return 0 ;
                    }
                }case "timeUp":{
                    if(this.time>myFile.time){ //正序排列
                        return 1 ;
                    }else if(this.time<myFile.time){
                        return -1 ;
                    }else{
                        return 0 ;
                    }
                }case "timeDown":{
                    if(this.time>myFile.time){ //倒序排列
                        return -1 ;
                    }else if(this.time<myFile.time){
                        return 1 ;
                    }else{
                        return 0 ;
                    }
                }default:return 0;
            }
    }
    public static String uploadFile(MultipartFile file,String filesUrl) throws Exception{


        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口
// 大文件上传请参照 API 文档高级 API 上传
        String fileName = file.getOriginalFilename(); //获取原始路径

        File localFile = new File(fileName);
// 指定要上传到 COS 上的路径
        String key = filesUrl + fileName.substring(fileName.lastIndexOf("\\")+1); //获取文件&后缀名;

        ObjectMetadata objectMetadata = new ObjectMetadata();
// 设置输入流长度
        objectMetadata.setContentLength(file.getSize());
        PutObjectResult putObjectResult = cosclient.putObject(bucketName, key, file.getInputStream(), objectMetadata);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getOriginalFilename());
        String etag = putObjectResult.getETag();
// 关闭输入流...

        //Date expiration = new Date(new Date().getTime() + 5 * 60 * 10000);
        //System.out.println(cosclient.generatePresignedUrl(bucketName,key,expiration)); //获取url
        cosclient.shutdown();
        return "aaa";
    }
    public static void createDir(String fileUrl){
        String key = fileUrl;
// 目录对象即是一个/结尾的空文件，上传一个长度为 0 的 byte 流
        InputStream input = new ByteArrayInputStream(new byte[0]);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0);

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, key, input, objectMetadata);
        PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
    }

    public static List<MyFile> findAllFiles(String filesUrl){
        List<MyFile> allFiles = new ArrayList<MyFile>();
// 获取 bucket 下成员（设置 delimiter）
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
// 设置 list 的 prefix, 表示 list 出来的文件 key 都是以这个 prefix 开始
        listObjectsRequest.setPrefix(filesUrl);
// 设置 delimiter 为/, 即获取的是直接成员，不包含目录下的递归子成员
        listObjectsRequest.setDelimiter("/");
// 设置 marker, (marker 由上一次 list 获取到, 或者第一次 list marker 为空)
        listObjectsRequest.setMarker("");
// 设置最多 list 100 个成员,（如果不设置, 默认为 1000 个，最大允许一次 list 1000 个 key）
        //listObjectsRequest.setMaxKeys(100);

        ObjectListing objectListing = cosclient.listObjects(listObjectsRequest);
// 获取下次 list 的 marker
        String nextMarker = objectListing.getNextMarker();
// 判断是否已经 list 完, 如果 list 结束, 则 isTruncated 为 false, 否则为 true
        boolean isTruncated = objectListing.isTruncated();
        List<COSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        String fileName="";
        Date expiration = new Date(new Date().getTime() + 5 * 60 * 10000);
        for (COSObjectSummary cosObjectSummary : objectSummaries) {
            MyFile myFile = new MyFile();

            String key = cosObjectSummary.getKey();
            fileName = key.substring(key.lastIndexOf("/")+1);   // 文件名
            if(fileName.length()<1){
                continue;
            }
            myFile.setName(fileName);
            // 获取文件长度
            myFile.setSize(cosObjectSummary.getSize());
            // 获取文件ETag
            //System.out.println(cosObjectSummary.getETag());
            // 获取最后修改时间
            myFile.setTime(cosObjectSummary.getLastModified().getTime());
            myFile.getTime();
            // 获取文件的存储类型
            //System.out.println(cosObjectSummary.getStorageClass());

            myFile.setFileUrl(String.valueOf(cosclient.generatePresignedUrl(bucketName,key,expiration)));
            //System.out.println(String.valueOf(cosclient.generatePresignedUrl(bucketName,key,expiration)));
            allFiles.add(myFile);
        }
        Collections.sort(allFiles);
        cosclient.shutdown();

        return allFiles;
    }

    public static String deleteFile(String fileName,String filesUrl){
        File downFile = new File("C://inetpub/wwwroot/MyFiles/recycle bin/"+fileName);
        //File downFile = new File("E:\\MyFiles\\recycle bin\\"+fileName);
// 指定要下载的文件所在的 bucket 和路径
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filesUrl+fileName);
        ObjectMetadata downObjectMeta = cosclient.getObject(getObjectRequest, downFile);
        cosclient.deleteObject(bucketName, filesUrl+fileName);
        return fileName;

    }


    public static void before_jieya(String path,String fileUrl){
        File downFile = new File(path+fileUrl);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileUrl);
        ObjectMetadata downObjectMeta = cosclient.getObject(getObjectRequest, downFile);
    }

    public static int deleteDir(String url){
        if(findAllFiles(url).size()<=0){
            cosclient.deleteObject(bucketName, url);
            return 0;
        }
        return 1;
    }

    public static int moveFiles(String srcPath,String deskPath){
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
// 传入一个 threadpool, 若不传入线程池, 默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosclient, threadPool);
// .....(提交上传下载请求, 如下文所属)

        // 要拷贝的 bucket region, 支持跨园区拷贝
        Region srcBucketRegion = new Region("ap-shanghai");
// 源 bucket, bucket 的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String srcBucketName = bucketName;
// 要拷贝的源文件
        String srcKey = srcPath;
// 目的 bucket, bucket 的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String destBucketName = bucketName;
// 要拷贝的目的文件
        String destKey = deskPath;

// 生成用于获取源文件信息的 srcCOSClient

        COSClient srcCOSClient = new COSClient(cred, new ClientConfig(srcBucketRegion));
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(srcBucketRegion, srcBucketName,
                srcKey, destBucketName, destKey);
        try {

            Copy copy = transferManager.copy(copyObjectRequest, srcCOSClient, null);
            // 返回一个异步结果 copy, 可同步的调用 waitForCopyResult 等待 copy 结束, 成功返回 CopyResult, 失败抛出异常.
            CopyResult copyResult = copy.waitForCopyResult();
        } catch (Exception e) {
            return 1;
        }finally {
            cosclient.deleteObject(bucketName, srcKey);
            // 关闭 TransferManger
            transferManager.shutdownNow();
        }
        return 0;
    }

    //public static
//    @Override
//    public String toString() {
//        return "MyFile{" +
//                "name='" + name + '\'' +
//                ", size=" + size +
//                ", time=" + time +
//                '}';
//    }
}
