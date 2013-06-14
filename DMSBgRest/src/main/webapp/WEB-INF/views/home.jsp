<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.7.2.js"></script>
<script type="text/javascript" language="javascript">
function callAjax(url,type,datatype,data,contentType,onBeforeSend,onError,onSuccess)
{
	
	$.ajax({
		beforeSend:onBeforeSend, 
        complete: function() {  }, 
        type: type,
        url: url,
        data:data,
        crossDomain:false,
        contentType: contentType,
        dataType: datatype,
        async: false,
        success: onSuccess,
        error: onError
    });
}
	//document ready
	function onError(){
		console.log("error");
	}
	function onSuccess(result){
		console.log("success",result);
	}
function f1(arg0,arg1){
	alert(arg0+arg1);
}	
$(function() {
//http://ajax.cdnjs.com/ajax/libs/json2/20110223/json2.js
//http://87.246.38.9:8080/GpsMobileWebServices/0.1/loginservice/en
	var data = {username:'admin',password:'sikira4e',account:'wip'};
	console.log(JSON.stringify(data));
	callAjax('http://localhost:8080/MongoDBProject/safari','GET','json','JSONRequest='+JSON.stringify(data),'application/json',null,onError,onSuccess);
});
</script>
</body>
</html>
