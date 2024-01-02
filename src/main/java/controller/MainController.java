package controller;

import entity.Folder;
import service.FolderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 这个类主要是用来控制主页访问
@WebServlet(urlPatterns = "/")     // 注解
public class MainController extends HttpServlet {   // 主要是用来接收请求和处理相应
    @Override   // 保证子类重新父类方法的时候不会出现不一致的问题
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 在请求经过后台的时候，后台将页面需要的数据从数据库中查到，再将数据传输给页面，页面进行解释
        FolderService folderService=new FolderService();
        String sql="select * from folder";
        List<Folder> list=folderService.getFolderAll(sql);
        request.setAttribute("list",list);
        // 跳转到自定义主页
        request.getRequestDispatcher("view/index.jsp").forward(request,response);
    }
}