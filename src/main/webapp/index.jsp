<html>
<body>
<h2>Hello World!</h2>

<h1>SpringMVC上传文件</h1>
<form action="${pageContext.request.contextPath}/manage/product/upload.do" name="form1" enctype="multipart/form-data">
    <input type="file" name="uploadFile"/>
    <input type="submit" value="SpringMVC上传文件"/>
</form>

</body>
</html>
