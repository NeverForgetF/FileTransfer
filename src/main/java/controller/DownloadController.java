package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@WebServlet(urlPatterns = "/toDownload")
public class DownloadController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // 1.接受要下载的文件名
//        String fileName=req.getParameter("fileName");
//        String url="/saveFile/"+fileName;
//        System.out.println(url);
//        // 将响应内容设置成二进制流
//        resp.setContentType("application/octet-stream");
//        // 指定编码
//        fileName= URLEncoder.encode(fileName,"utf-8");
//        // 以弹窗的形式展示给用户
//        resp.addHeader("Content-Disposition","attachment;filename="+fileName);
//        req.getRequestDispatcher(url).forward(req,resp);
//        // 清空缓冲区
//        resp.flushBuffer();
        // 接受要下载的文件名
        String fileName = request.getParameter("fileName");
        String filePath = getServletContext().getRealPath("/saveFile/") + File.separator + fileName;

        // 设置响应内容类型
        response.setContentType("application/octet-stream");

        // 设置响应头，指定编码和文件名
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));

        // 使用 try-with-resources 自动关闭流
        try (InputStream inputStream = new FileInputStream(filePath);
             OutputStream outputStream = response.getOutputStream()) {

            // 将文件内容写入响应流
            byte[] buffer = new byte[1024*1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("File download failed. Server error: " + e.getMessage());
        }
    }
}
