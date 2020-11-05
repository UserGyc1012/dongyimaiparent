package com.dongyimai.manager.controller;


import com.dongyimai.entity.Result;
import com.dongyimai.util.FastDFSClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
//    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL = "http://192.168.188.146/";//文件服务器地址
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //1.取文件的拓展名
        String originalFilename=file.getOriginalFilename();
//        System.out.println("fullName : " + originalFilename);
        String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        //2.创建一个FastDFS的客户端
        try {
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
            //3.执行上传处理
            String path=fastDFSClient.uploadFile(file.getBytes(),extName);
            //4.拼接返回的url和ip地址，拼接成完整的url；
            //2.3 拼接返回地址 http://192.168.188.146/group1/M00/02/04/dsafidsafdsajf.sh
            String url=FILE_SERVER_URL+path;
//            System.out.println("url : " + url);
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }

}
