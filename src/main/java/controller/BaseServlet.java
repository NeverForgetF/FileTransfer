package controller; /**
 * @program: 1904Project
 * @description:
 * @author: DY
 * @create: 2021-03-30 09:16
 **/

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName BaseServlet
 * @Description 一个基于反射实现的通用Servlet
 * @Since 1.8
 * @Version 1.0
 */
/*反射，通过反射的方式调用类里面的方法*/
public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");

        // 获取请求中method,也就是需要调用的方法的名称
        String methodName = req.getParameter("method");

        if (methodName != null) {
            Method method = null;

            try {
                // 创建一个metho对象：Java 的反射机制获得的指定类中指定方法的对象；
                method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();

                throw new RuntimeException("未找到Controller方法:" + methodName);
            }

            String invoke = null;

            try {
                // 使用method反射机制，动态调用一个类中的某一个方法
                invoke = (String) method.invoke(this, req, resp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();

                throw new RuntimeException("方法：" + methodName + " 访问异常！");
            } catch (InvocationTargetException e) {
                e.printStackTrace();

                throw new RuntimeException("方法：" + methodName + " 执行异常！");
            }

            if (invoke!=null && invoke!="") {
                if (invoke.startsWith("forward:")) {
                    req.getRequestDispatcher(invoke.split(":")[1]).forward(req, resp);
                } else if (invoke.startsWith("redirect:")) {
                    resp.sendRedirect(invoke.split(":")[1]);
                }
            }
        }
    }
}
