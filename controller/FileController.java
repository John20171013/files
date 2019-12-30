package com.example.myfiles.controller;


import com.example.myfiles.domain.Browse;
import com.example.myfiles.other.Face;
import com.example.myfiles.other.MyFile;
import com.example.myfiles.other.Now;
import com.example.myfiles.other.Ocr;
import com.example.myfiles.repositories.BrowseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Controller
public class FileController {

    static String path1 = "C://inetpub/wwwroot/MyFiles/MyDirs/";
    //    static String path1 = "E://myfiles/";
    @Autowired
    private BrowseRepository browseRepository;

    @PostMapping("/getDirs")  //获取当前路径的文件夹
    @ResponseBody
    public List<String> getDirs(@RequestParam("filesUrl") String filesUrl) throws InterruptedException {
        Thread.sleep(200);
        File f = new File(path1+filesUrl);
        if(f.exists()){
            List<String> myDirs= new ArrayList<String>();
            File files[] = f.listFiles() ;	// 列出全部内容
            for(int i=0;i<files.length;i++){
                if(files[i].isDirectory()){
                    myDirs.add(files[i].getName());
                }
            }
            return myDirs;

        }

        return null;
    }
    @PostMapping("/addDir")  //在当前路径下新建文件夹
    @ResponseBody
    public int addDir(@RequestParam("filesUrl") String filesUrl,
                      @RequestParam("dirName") String dirName){  //参数需'/'结尾表示文件夹
        File f = new File(path1+filesUrl+dirName);
        if(!f.exists()) {
            f.mkdir();
            MyFile.createDir(filesUrl+dirName);
        }else return 1;
        return 0;
    }
    @PostMapping("/removeDir")  //在当前路径下新建文件夹
    @ResponseBody
    public int removeDir(@RequestParam("url") String url){  //参数需'/'结尾表示文件夹
        File f = new File(path1+url);
        if(f.exists()) {
            if(MyFile.deleteDir(url)==0){
                deletefiles(path1+url);
                f.delete();
                return 0;
            }
        }
        return 1;
    }
    @PostMapping("/moveFile")  //移动文件，可重命名
    @ResponseBody
    public int moveFile(@RequestParam("path1") String path1,
                        @RequestParam("path2") String path2){

        return MyFile.moveFiles(path1,path2);
    }


    @PostMapping("/uploadFiles")
    @ResponseBody
    public String uploadFiles(@RequestParam("filesUrl") String filesUrl,
                              @RequestParam("file") MultipartFile file,
                              Browse browse,
                              HttpSession session) throws Exception{
        String fileName = file.getOriginalFilename(); //获取原始路径
        fileName = filesUrl +fileName.substring(fileName.lastIndexOf("\\")+1); //获取文件&后缀名
        addOperation(browse,session,"上传【"+fileName+"】");
//        File myDir = new File(path1+"\\MyFiles");
//
//        String fileName = file.getOriginalFilename(); //获取原始路径
//
//        fileName = fileName.substring(fileName.lastIndexOf("\\")+1); //获取文件&后缀名
//
//        String suffixName = fileName.substring(fileName.lastIndexOf(".")); //获取后缀名
//
//        String trueName = fileName.substring(0,fileName.lastIndexOf(".")); //获取文件名
//
//        /*if(fileName.lastIndexOf(".")==-1){
//            suffixName = "";
//            trueName = fileName;
//        }else{
//            suffixName = fileName.substring(fileName.lastIndexOf(".")); //获取后缀名
//            trueName = fileName.substring(0,fileName.lastIndexOf(".")); //获取文件名
//        }*/
//
//
//        if(!myDir.exists()){
//            myDir.mkdir();
//        }
//
//        File myFile = new File(path1+"\\MyFiles\\"+checkName(trueName,suffixName));
//
//        try {
//            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(myFile));
//            out.write(file.getBytes());
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            return "";
//        }
//        return fileName;
        return MyFile.uploadFile(file,filesUrl);
    }
    @PostMapping("/findAllFiles")
    @ResponseBody
    public List<MyFile> findAllFiles(@RequestParam("filesUrl") String filesUrl,
                                     @RequestParam("sortType") String sortType){
        MyFile.sortType = sortType;
        return MyFile.findAllFiles(filesUrl);
    }

    @PostMapping("/recycle_bin")
    @ResponseBody
    public List<MyFile> recycle_bin(@RequestParam("sortType") String sortType){
        File f = new File("C://inetpub/wwwroot/MyFiles/recycle bin") ;		// 实例化File类的对象
        List<MyFile> allFiles = new ArrayList<MyFile>();
        MyFile.sortType = sortType;
        File files[] = f.listFiles() ;	// 列出全部内容
        for(int i=0;i<files.length;i++){
            if (!files[i].isDirectory()){
                MyFile myFile = new MyFile();
                myFile.setName(files[i].getName());
                myFile.setSize(files[i].length());
                myFile.setTime(files[i].lastModified());
                myFile.setFileUrl("http://www.junbaba.top:8888/MyFiles/recycle bin/"+myFile.getName());
                allFiles.add(myFile);
            }
        }
        Collections.sort(allFiles);
        return allFiles;
    }
    @PostMapping("/deleteAllRbFiles")
    @ResponseBody
    public void deleteAllRbFiles(){
        deletefiles("C://inetpub/wwwroot/MyFiles/recycle bin");
    }

    public void deletefiles(String url){
        File f = new File(url) ;
        File files[] = f.listFiles() ;	// 列出全部内容
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    @PostMapping("/deleteFile")
    @ResponseBody
    public String deleteFile(@RequestParam("filesUrl") String filesUrl,
                             @RequestParam("fileName") String fileName){
//        File f = new File(path1+File.separator+"MyFiles") ;		// 实例化File类的对象
//
//        File files[] = f.listFiles() ;	// 列出全部内容
//        int i;
//        for(i=0;i<files.length;i++){
//            if(files[i].getName().equals(fileName)){
//                f = new File(path1+File.separator+"MyFiles"+File.separator+fileName);
//                File f2 = new File(path1+File.separator+"MyFiles"+File.separator+"recycle bin"+File.separator+fileName);
//
//                FileInputStream input = null ;		// 准备好输入流对象，读取源文件
//                FileOutputStream out = null ;		// 准备好输出流对象，写入目标文件
//                if(f2.exists()) f2.delete();
//                try{
//                    input = new FileInputStream(f) ;
//                    out = new FileOutputStream(f2) ;
//                    FileChannel inChannel = input.getChannel();
//                    WritableByteChannel outChannel = out.getChannel();
//                    inChannel.transferTo(0, inChannel.size(), outChannel);
//                    inChannel.close();
//                    outChannel.close();
//                    input.close();
//                    out.close();
//                    files[i].delete();
//                }catch(Exception e){
//                    return "";
//                }
//                break;
//            }
//        }
//        return i>=files.length?"":fileName;
        return MyFile.deleteFile(fileName,filesUrl);
    }

    @PostMapping("/readFile")
    @ResponseBody
    public String readFile(@RequestParam("fileUrl") String fileUrl){
//        String fileContent = "";
//        File f = new File(fileUrl);
//        InputStream inputStream = null;
//        inputStream = new FileInputStream(f);
//        byte b[] = new byte[(int)f.length()] ;
//        for(int i=0;i<b.length;i++){
//            b[i] = (byte)inputStream.read() ;		// 读取内容
//        }
//        inputStream.close() ;						// 关闭输出流\
//        System.out.println("内容为：" + new String(b)) ;
//        return fileContent;
        int HttpResult; // 服务器返回的状态
        String fileContent = new String();

        try
        {
            URL url =new URL(fileUrl); // 创建URL
            URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
            urlconn.connect();
            HttpURLConnection httpconn =(HttpURLConnection)urlconn;
            HttpResult = httpconn.getResponseCode();
            if(HttpResult != HttpURLConnection.HTTP_OK) // 不等于HTTP_OK说明连接不成功
                System.out.print("无法连接到");
            else
            {
                int filesize = urlconn.getContentLength(); // 取数据长度
                filesize = filesize>=10250?10250:filesize;
                InputStreamReader isReader = new InputStreamReader(urlconn.getInputStream());

                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer buffer = new StringBuffer();
                String line; // 用来保存每行读取的内容
                line = reader.readLine(); // 读取第一行
                while (line != null&&buffer.length()<=filesize) { // 如果 line 为空说明读完了
                    buffer.append(line); // 将读到的内容添加到 buffer 中
                    buffer.append("\n"); // 添加换行符
                    line = reader.readLine(); // 读取下一行
                }
                fileContent = buffer.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    @PostMapping("/jieya")
    @ResponseBody
    public List<String> jieya(@RequestParam("fileUrl") String fileUrl,
                              Browse browse,
                              HttpSession session)throws Exception{

        MyFile.before_jieya(path1,fileUrl);
        List<String> list = new ArrayList<>();
        String suffix = fileUrl.substring(fileUrl.lastIndexOf(".")+1).toLowerCase();
        File f = new File(path1+fileUrl);
        if(suffix.equals("zip")){
            addOperation(browse,session,"在线解压zip");
            ZipFile zip = null;
            try {
                zip = new ZipFile(f, Charset.forName("GBK"));
                for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    if(!entry.isDirectory())list.add(entry.getName());
                }
            } catch (IOException e) {
                System.out.println("-----zip-GBK失败------");
            }finally {
                zip.close();
            }
        }else if(suffix.equals("rar")){
            list.add("rar无法解压");
//            addOperation(browse,session,"在线解压rar");
//            Archive archive = null;
//            try{
//                archive = new Archive(f);
//                FileHeader fh = archive.nextFileHeader();
//                while(fh!=null){
//                    if(!fh.isDirectory())list.add(fh.getFileNameString());
//                    fh = archive.nextFileHeader();
//                }
//            }catch (Exception e){
//                System.out.println("-----rar解压出错------");
//            }finally {
//                archive.close();
//            }
        }else{
            addOperation(browse,session,"在线解压失败");
        }
        f.delete();
        return list;
    }

    @PostMapping("/getOcrGeneral")
    @ResponseBody
    public String getOcrGeneral(@RequestParam("fileUrl") String fileUrl,
                                Browse browse,
                                HttpSession session){
        addOperation(browse,session,"文字识别");
        return new Ocr().getOcrGeneral(fileUrl);
    }

    @PostMapping("/isFace")  //人脸识别
    @ResponseBody
    public String isFace(@RequestParam("fileUrl") String fileUrl,
                         Browse browse,
                         HttpSession session){
        addOperation(browse,session,"人脸识别");
        return new Face().faceFaceIdentify(fileUrl);
    }

    @PostMapping("/getFaceDetect")  //人脸检测
    @ResponseBody
    public String getFaceDetect(@RequestParam("fileUrl") String fileUrl,
                                Browse browse,
                                HttpSession session){
        addOperation(browse,session,"人脸检测");
        return new Face().faceDetect(fileUrl);
    }

    @PostMapping("/addDownloadOperation")
    @ResponseBody
    public void addDownloadOperation(@RequestParam("file") String file,
                                     Browse browse,
                                     HttpSession session){
        addOperation(browse,session,"下载【"+file+"】");
    }

    /*public String checkName(String trueName,String suffixName){
        String fileName=trueName+suffixName;
        File myFile = new File(path1+"\\MyFiles\\"+fileName);
        if(myFile.exists()){
            fileName = checkName(trueName+" - 副本",suffixName);
        }
        return fileName;
    }*/

    public void addOperation(Browse browse,HttpSession session,String operate){
        browse.setDate(Now.getNowTime("yyyy年MM月dd日"));
        browse.setTime(Now.getNowTime("HH:mm:ss"));
        browse.setUserid(session.getAttribute("myfiles_user").toString());
        browse.setOperation(operate);
        browseRepository.save(browse);
    }
}
