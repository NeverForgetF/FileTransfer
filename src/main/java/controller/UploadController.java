package controller;

import com.alibaba.fastjson.JSON;
import com.jspsmart.upload.File;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;
import entity.Folder;
import service.FolderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/toUpload")
public class UploadController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 上传文件
            SmartUpload smartUpload=new SmartUpload();
            // 获得pageContext对象
            JspFactory jspFactory = JspFactory.getDefaultFactory();
            PageContext pageContext = jspFactory.getPageContext(this, req, resp, null, false, 1024*1024, true);
            smartUpload.initialize(pageContext);
            smartUpload.setCharset("utf-8");
            // 实现文件数据的上传
            smartUpload.upload();
            File file=smartUpload.getFiles().getFile(0);
            // 获取自定义文件名
            String newFileName=smartUpload.getRequest().getParameter("newFileName");
            // 得到文件的基本信息
            String fileName=file.getFileName();
            String type=file.getContentType();
            System.out.println("type="+type);
            String url="saveFile/";
            if (!newFileName.equals("")){
                fileName=newFileName+"."+fileName.split("\\.")[1];
            }
            url=url+fileName;
            // 将文件保存到指定目录
            file.saveAs(url,SmartUpload.SAVE_VIRTUAL);
            req.setAttribute("fileName",fileName);
            System.out.println("newFileName="+newFileName);
            // 存入数据库
            FolderService folderService = new FolderService();
            String sql1="insert into folder values(null,?,?)";
            String sql2="update folder set fileName=?,saveDate=? where id=?";
            List<Folder> folders=folderService.getFolderAll("select * from folder");
            boolean flag=true;
            for (Folder folder : folders) {
                if (fileName.equals(folder.getFileName())){
                    String id= String.valueOf(folder.getId());
                    folderService.updateFolder(sql2,fileName,new Folder().getTime(),id);
                    flag=false;
                    break;
                }
            }
            if (flag){
                folderService.insertFolder(sql1,fileName, new Folder().getTime());
            }
            PrintWriter printWriter=resp.getWriter();
            printWriter.write(JSON.toJSONString(fileName));
//            req.getRequestDispatcher("index.jsp").forward(req,resp);
        } catch (SmartUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
