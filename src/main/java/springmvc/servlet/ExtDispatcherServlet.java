package springmvc.servlet;

import org.apache.commons.lang.StringUtils;
import springmvc.annotation.ExtController;
import springmvc.annotation.ExtRequestMapping;
import springmvc.utils.ClassUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 自定义前端控制器
 * @author: Codegitz
 * @create: 2020-05-22 23:40
 **/
public class ExtDispatcherServlet extends HttpServlet {

    //存放容器bean
    private ConcurrentHashMap<String,Object> springMvcBeans = new ConcurrentHashMap<>();

    //存放URL-->类映射关系
    private ConcurrentHashMap<String,Object> urlBeans = new ConcurrentHashMap<>();

    //存放URL-->方法名称映射关系
    private ConcurrentHashMap<String,String> urlMethod = new ConcurrentHashMap<>();


    @Override
    public void init() throws ServletException {
        //扫包，获取当前包下的所有类
        List<Class<?>> classes = ClassUtil.getClasses("springmvc.controller");
        //将扫包范围里面有ExtController注解的类加入到容器中
        try {
            findClassExistMvcAnnotation(classes);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //将URL映射地址和方法进行关联
        handlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取请求的URL
        String requestURI = req.getRequestURI();
        if (StringUtils.isEmpty(requestURI)){
            return;
        }
        //2.从map集合中获取控制对象
        Object object = urlBeans.get(requestURI);
        if (object == null){
            resp.getWriter().println("404 not found");
        }

        //3.使用URL地址获得方法名称
        String methodName = urlMethod.get(requestURI);
        if (methodName == null){
            resp.getWriter().println("404 not found");
        }
        //4.使用反射机制获取方法
        String resultPage = null;
        try {
            //5.获得返回结果
            resultPage = (String) object.getClass().getMethod(methodName).invoke(object);
            resp.getWriter().println(resultPage+"第一次写");
        } catch (NoSuchMethodException e) {
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //6.调用视图渲染器渲染给页面展示
//        extResourceViewResolver(resultPage,req,resp);
    }

    private void extResourceViewResolver(String resultPage, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String prefix = "/";
        String suffix = ".jsp";
        req.getRequestDispatcher(prefix+resultPage+suffix).forward(req,resp);
    }

    /**
     * 将URL映射地址和方法进行关联
     */
    public void handlerMapping(){
        //遍历容器类，判断类上是否有URL映射注解
        for (Map.Entry<String,Object> mvcBean : springMvcBeans.entrySet()){
            //获取对象
            Object mvcBeanValue = mvcBean.getValue();
            Class<?> classInfo = mvcBeanValue.getClass();
            ExtRequestMapping extRequestMapping = classInfo.getDeclaredAnnotation(ExtRequestMapping.class);
            String baseURL = "/my_mvc_war";
            if (extRequestMapping != null){
                baseURL = baseURL + extRequestMapping.value();
            }
            //判断类上所有的方法是否有注解
            Method[] declaredMethods = classInfo.getDeclaredMethods();
            for (Method method : declaredMethods){
                ExtRequestMapping methodAnnotation = method.getDeclaredAnnotation(ExtRequestMapping.class);
                if (methodAnnotation != null){
                    String methodUrl = baseURL + methodAnnotation.value();
                    urlBeans.put(methodUrl,mvcBeanValue);
                    urlMethod.put(methodUrl,method.getName());
                }

            }
        }

    }

    /**
     * @description 查找存在注解的类，加入容器管理
     * @param classes
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void findClassExistMvcAnnotation( List<Class<?>> classes) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        for (Class<?> classInfo : classes){
            ExtController extController = classInfo.getDeclaredAnnotation(ExtController.class);
            if (extController != null){
                String beanId = ClassUtil.toLowerCaseFirstOne(classInfo.getSimpleName());
                Object bean = ClassUtil.newInstance(classInfo);
                springMvcBeans.put(beanId,bean);
            }
        }

    }
}
