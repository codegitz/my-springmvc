# my-springmvc
手写一个简易版的基于servlet的MVC框架，实现类为ExtDispatcherServlet，基础httpservlet
- 自定义了@ExtController和@ExtRequestMapping注解
- `ClassUtil.getClasses("springmvc.controller")`实现了扫描指定路径下的所有包
- `findClassExistMvcAnnotation( List<Class<?>> classes)`对所有的类进行扫描，把存在@ExtController和@ExtRequestMapping的类加入mvc容器里
- `handlerMapping()`进行路径和要调用的方法进行匹配映射，先获取请求路径，然后找到相应的方法和对象，利用反射机制实例化对象，invoke对应的方法，获得返回结果
- 将获得的结果回写到页面
