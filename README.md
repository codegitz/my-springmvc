# my-springmvc
手写一个简易版的基于servlet的MVC框架
- 自定义了@ExtController和@ExtRequestMapping注解
- 实现了扫描指定路径下的所有包，对存在@ExtController的类加入到mvc容器里
- 对mvc容器里的类进行扫描，对存在@ExtRequestMapping的类和方法进行路径匹配映射
- 对请求路径解析，获取请求路径，找到相应的方法和对象，利用反射机制实例化对象，invoke对应的方法，获得返回结果
- 获得的结果回写到页面
