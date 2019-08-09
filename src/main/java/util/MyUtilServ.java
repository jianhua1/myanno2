package util;

import anno.Controller;
import anno.RequestMapping;
import view.View;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MyUtilServ extends HttpServlet {

    Map<String,Class> map=new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req,resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuffer requestURL = req.getRequestURL();
        String contextPath = req.getContextPath()+"/";
        String requestURI = req.getRequestURI();
        requestURI = requestURI.replaceFirst(contextPath, "");
        requestURI=requestURI.substring(0,requestURI.lastIndexOf("."));
        if(map.containsKey(requestURI)){
            Class aClass = map.get(requestURI);
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                boolean annotationPresent = declaredMethod.isAnnotationPresent(RequestMapping.class);
                if(annotationPresent){
                    try {
                        String value = declaredMethod.getAnnotation(RequestMapping.class).value();
                        if(value!=null && requestURI.equals(value)){
                            Object invoke = null;
                            try {
                                invoke = declaredMethod.invoke(aClass.newInstance());
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            }
                            if(invoke!=null){
                                View view=(View)invoke;
                                if(view.getDispatchAction().equals("forward")){
                                    req.getRequestDispatcher(view.getUrl()).forward(req,resp);
                                }else if(view.getDispatchAction().equals("redirect")){
                                    resp.sendRedirect(req.getContextPath()+view.getUrl());
                                }else{
                                    req.getRequestDispatcher(view.getUrl()).forward(req,resp);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String myspring = config.getInitParameter("basePackage");
        String[] packs = myspring.split(",");
        for (String pack : packs) {
            pack=pack.replace(".","/");
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Enumeration<URL> resources = contextClassLoader.getResources(pack);
                while (resources.hasMoreElements()){
                    URL url = resources.nextElement();
                    if("file".equals(url.getProtocol())){
                        String file1 = url.getFile();
                        File file = new File(URLDecoder.decode(file1,"utf-8"));
                        if(file.isDirectory()){
                            String[] fileList = file.list();
                            for (String s : fileList) {
                                try {
                                    String substring = s.substring(0, s.length() - 6);
                                    Class<?> aClass = Class.forName(pack+"."+substring);
                                    boolean annotationPresent = aClass.isAnnotationPresent(Controller.class);
                                    if(annotationPresent){
                                        Method[] declaredMethods = aClass.getDeclaredMethods();
                                        for (Method declaredMethod : declaredMethods) {
                                            boolean annotationPresent1 = declaredMethod.isAnnotationPresent(RequestMapping.class);
                                            if(annotationPresent1){
                                                String value = declaredMethod.getAnnotation(RequestMapping.class).value();
                                                map.put(value,aClass);
                                            }
                                        }
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
