<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    /*获取项目的根路径*/
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://" + request.getServerName()+":"+request.getServerPort()+path;
    /*basePath就是得到的跟路径类似于：http://localhost:8081/pinduoduo/*/
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<%=basePath%>/css/index.css">
    <script src="<%=basePath%>/js/jquery-3.7.1.js"></script>
    <title>Title</title>
</head>
<body>
    <div class="top">
        <div class="person">个人文件夹</div>
    </div>
    <div class="content">
        <div class="contentLeft">
            <div class="head">
                <div class="fileList">
                    <img class="fileImg" src="<%=basePath%>/img/file.png" alt="">
                    <span>文件夹列表</span>
                </div>
                <input class="search" type="text" placeholder="按 / 搜索">
            </div>
            <table class="table">
                <thead>
                    <tr class="row">
                        <th class="col_2">Name</th>
                        <th class="col_1">Last Update</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${requestScope.list}" var="Folder">
                        <tr class="row colorRow">
                            <td class="col_2">
                                <a href="<%=basePath%>/toDownload?fileName=${Folder.fileName}">${Folder.fileName}</a>
                            </td>
                            <td class="col_1 saveDate">${Folder.saveDate}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="contentRight">
            <div class="uploadText">上传文件</div>
            <form class="uploadFile" onsubmit="return false">
                <div class="inputText">
                    <span>fileName:</span>
                    <input type="text" name="newFileName" placeholder="自定义文件名"/>
                </div>
                <input class="select" type="file" name="fileName"/>
                <input class="btn" type="submit" value="提交"/>
            </form>
        </div>
    </div>
</body>
<script>
    var date=$(".saveDate");
    function initDate(length){
        if (length<0){
            return;
        }
        $(date[length]).text($(date[length]).text().replace(".0",""));
        initDate(--length);
    }
    initDate(date.length-1);
    $(".btn").click(function (){
        var formData = new FormData();
        formData.append("newFileName", $("input[name='newFileName']").val());
        formData.append("fileName", $(".select")[0].files[0]);
        console.log(formData.get("newFileName"))
        console.log(formData.get("fileName"))
        if (formData.get("fileName").name===undefined){
            alert("请先选择要提交的文件！");
            return;
        }
        $.ajax({
            type:"POST",
            url: "<%=basePath%>/toUpload",
            data: formData,
            enctype:"multipart/form-data",
            contentType:false,
            processData:false,
            success:function (result){
                location.reload();
            }
        })
    })
</script>
</html>