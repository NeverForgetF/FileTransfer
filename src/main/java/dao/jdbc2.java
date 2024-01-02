package dao;

import com.alibaba.druid.pool.DruidDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * @program: studyJBDC
 * @description:
 * @author: DY
 * @create: 2022-08-02 20:49
 **/

public class jdbc2 {
    private static String url="";
    private static String username="";
    private static String password="";
    private static int maxActive=0;
    private static DruidDataSource druidDataSource;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    public static Connection connection;
    // 类在初始化的时候从配置文件中读取配置信息
    static {
        // try with()语快  jdk7的新特性
        // 在try执行结束后，（）内的资源对象会被自动关闭
        try(InputStream inputStream = jdbc2.class.getResourceAsStream("/jdbc.properties")) {
            // 将properties文件，读取成一个输入流对象
            // 读取流对象，从流对象中获取文件信息
            Properties properties = new Properties();
            properties.load(inputStream);
            // 从properties对象中读取文件信息
            url = properties.getProperty("jdbc.url");
            username = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");
            maxActive = Integer.parseInt(properties.getProperty("jdbc.maxActive"));
            druidDataSource = new DruidDataSource();
            // 给连接池对象设置基本信息
            druidDataSource.setUrl(url);
            druidDataSource.setUsername(username);
            druidDataSource.setPassword(password);
            druidDataSource.setMaxActive(maxActive);
            connection = druidDataSource.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    //   insert的封装，只针对于，表的主键id是自增长的
    public static int doInsert(Connection connection,String sql, Object... prams){
        // 执行sql后生成的id值
        int id = 0;
        try {
            //  创建数据库连接
            //getCon();
            // 预加载sql语句，添加一个关于设置主键id的变量
            preparedStatement  = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            // 将sql中？占位符跟参数进行替换
            for (int i = 0; i < prams.length; i++) {
                preparedStatement.setObject(i+1,prams[i]);
            }
            // 获取执行sql后，数据库中数据受影响的行数
            int count =  preparedStatement.executeUpdate();
            // 在执行sql后，将插入数据库的数据的主键id作为结果接受到
            if (count>0){
                // 在sql语句执行成功后，再获取结果集，生成的id是放在这个结果集中
                resultSet = preparedStatement.getGeneratedKeys();
                while (resultSet.next()){
                    // 再从该结果集中获取到生成的主键id
                    id  = resultSet.getInt(1);
                }
                System.out.println("执行成功！");
            }else {
                System.out.println("执行失败！");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        /*finally {
            jdbc3.close(connection,preparedStatement,resultSet);
        }*/
        return id;
    }
    //  封装更新，删除和插入（其中插入语句的主键id是不自动增长,这个id就需要后台自己生成）
    public static int doUpdate(Connection connection,String sql,Object... prams){
        // 设置一个变量，来记录执行sql语句后对数据库影响的行数
        int count = 0;
        try {
            //  创建数据库连接
            //getCon();
            // 预加载sql语句，添加一个关于设置主键id的变量
            preparedStatement  = connection.prepareStatement(sql);
            // 将sql中？占位符跟参数进行替换
            for (int i = 0; i < prams.length; i++) {
                preparedStatement.setObject(i+1,prams[i]);
            }
            // 获取执行sql后，数据库中数据受影响的行数
            count =  preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }
    // 查询封装：selectOne
    public static Map<String,Object> selectOne(String sql, Object... prams){
        Map<String,Object> resultMap = new HashMap<>();

        try {
            //  创建数据库连接
            //getCon();
            // 预加载sql语句，添加一个关于设置主键id的变量，防止sql注入
            preparedStatement  = connection.prepareStatement(sql);
            // 将sql中？占位符跟参数进行替换
            for (int i = 0; i < prams.length; i++) {
                preparedStatement.setObject(i+1,prams[i]);
            }
            // 获取到查询结果集
            resultSet = preparedStatement.executeQuery();
            // ResultSetMetaData:获取查询到的结果集对象中，关于表的列名（表字段名）信息
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            // 获取查询结果的所有的列（字段名称）的个数
            int count = resultSetMetaData.getColumnCount();
            // 循环结构集
            while (resultSet.next()){
                // 再次循环，获取每一条信息中的每一个字段
                for (int i = 1; i <= count; i++) {
                    String k = resultSetMetaData.getColumnName(i);
                    Object v = resultSet.getObject(i);
                    // 将获取到的k和v放入到map中
                    resultMap.put(k,v);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultMap;
    }
    // 查询返回一个实体类对象
    public static <T> T selectOne(Class<T> tClass,String sql,Object... prams) throws InstantiationException, IllegalAccessException {
        Map<String,Object> resultMap = new HashMap<>();

        try {
            //  创建数据库连接
            //getCon();
            // 预加载sql语句，添加一个关于设置主键id的变量
            preparedStatement  = connection.prepareStatement(sql);
            // 将sql中？占位符跟参数进行替换
            for (int i = 0; i < prams.length; i++) {
                preparedStatement.setObject(i+1,prams[i]);
            }
            // 获取到查询结果集
            resultSet = preparedStatement.executeQuery();
            // ResultSetMetaData:获取查询到的结果集对象中，关于表的列名（表字段名）信息
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            // 循环结构集
            while (resultSet.next()){
                // 再次循环，获取每一条信息中的每一个列
                for (int i = 1; i <= count; i++) {
                    String k = resultSetMetaData.getColumnName(i);
                    Object v = resultSet.getObject(i);
                    // 将获取到的k和v放入到map中
                    resultMap.put(k,v);
                }
            }
            System.out.println("获取到的列数是"+count);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return mapToBean(tClass,resultMap);
    }
    // 查询封装：selectList
    public static List<Map<String,Object>> selectList (String sql, Object... prams){
        List<Map<String,Object>> resultList = new ArrayList<>();
        try {
            //  创建数据库连接
            //getCon();
            // 预加载sql语句，添加一个关于设置主键id的变量
            preparedStatement  = connection.prepareStatement(sql);
            // 将sql中？占位符跟参数进行替换
            for (int i = 0; i < prams.length; i++) {
                preparedStatement.setObject(i+1,prams[i]);
            }
            // 获取到查询结果集
            resultSet = preparedStatement.executeQuery();
            // ResultSetMetaData:获取查询到的结果集对象中，关于表的列名（表字段名）信息
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            // 循环结构集,while每循环一次就是一条数据
            while (resultSet.next()){
                // 再次循环，获取每一条信息中的每一个列
                // 此处的map表示多条数据中的一条数据
                Map<String,Object> map = new HashMap<>();
                for (int i = 1; i <= count; i++) {
                    String k = resultSetMetaData.getColumnName(i);
                    Object v = resultSet.getObject(i);
                    // 将获取到的k和v放入到map中
                    map.put(k,v);
                }
                // 将已经添加好的map放入到list集合中
                resultList.add(map);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return resultList;
    }
    public static <T> List<T> selectList (Class<T> tClass, String sql, Object... prams){
        List<T> resultList = new ArrayList<>();
        try {
            //  创建数据库连接
            //getCon();
            // 预加载sql语句，添加一个关于设置主键id的变量
            preparedStatement  = connection.prepareStatement(sql);
            // 将sql中？占位符跟参数进行替换
            for (int i = 0; i < prams.length; i++) {
                preparedStatement.setObject(i+1,prams[i]);
            }
            // 获取到查询结果集
            resultSet = preparedStatement.executeQuery();
            // ResultSetMetaData:获取查询到的结果集对象中，关于表的列名（表字段名）信息
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            // 循环结构集,while每循环一次就是一条数据
            while (resultSet.next()){
                // 再次循环，获取每一条信息中的每一个列
                // 此处的map表示多条数据中的一条数据
                Map<String,Object> map = new HashMap<>();
                T t = tClass.newInstance();
                for (int i = 1; i <= count; i++) {
                    String k = resultSetMetaData.getColumnName(i);
                    Object v = resultSet.getObject(i);
                    // 将获取到的k和v放入到map中
                    map.put(k,v);
                }
                t = mapToBean(tClass,map);
                // 将已经添加好的map放入到list集合中
                resultList.add(t);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return resultList;
    }
    // 第一个参数：tClass  需要转换的bean对象的字节码文件
    // 第二个参数：map   需要被转为bean对象的集合
    public static <T> T mapToBean (Class<T> tClass,Map<String,Object> map) throws IllegalAccessException, InstantiationException {
        // 根据传来的tClass找到该类的字节码文件
        T t = tClass.newInstance();
        // 核心：将map转为bean
        map.forEach((k,v)->{
            // 将map中的value放入到bean对象中，也就是T对象中
            try {
                // 根据map中的key去bean对象中找到对应属性Field对象
                Field field = tClass.getDeclaredField(k);
                // 先判断field，类的属性对应是否为null
                if (Objects.nonNull(field)){
                    // 不管属性是否为私有都暴力释放
                    field.setAccessible(true);
                    // 再将对应map中value设置给t对象
                    field.set(t,v);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return t;
    }
}

