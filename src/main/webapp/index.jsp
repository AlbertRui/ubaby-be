<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<body>
<h2>Hello World!</h2>

<h1>SpringMVC上传文件</h1>
<form method="post" action="${pageContext.request.contextPath}/manage/product/upload.do" name="form1" enctype="multipart/form-data">
    <input type="file" name="uploadFile"/>
    <input type="submit" value="SpringMVC上传文件"/>
</form>

<h1>富文本上传</h1>
<form method="post" action="${pageContext.request.contextPath}/manage/product/upload.do" name="form1" enctype="multipart/form-data">
    <input type="file" name="richTextUpload"/>
    <input type="submit" value="富文本上传"/>
</form>

</body>
</html>
