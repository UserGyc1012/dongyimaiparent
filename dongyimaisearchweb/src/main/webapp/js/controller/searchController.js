app.controller('searchController',function($scope,$location,searchService){
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,
		'pageSize':10,'sortField':'','sort':''};//搜索对象
	//搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;
				//调用分页查询方法
				buildPageLabel();
			}
		);		
	}
	$scope.addSearchItem=function (key,value){
		if (key=='category'||key=='brand'||key=='price'){//如果点击的是分类或者是品牌
			$scope.searchMap[key]=value;//品牌=华为
		}else{
			$scope.searchMap.spec[key]=value;//网络=移动3g
		}
		$scope.search();//执行搜索
	}
	//移除符和搜索条件
	$scope.removeSearchItem=function (key){
		if (key=='category'||key=='brand'||key=='price'){//如果点击的是分类或者是品牌
			$scope.searchMap[key]="";//品牌=""
		}else{
			delete $scope.searchMap.spec[key];//移除属性 注意：delete 操作符用于删除对象的某个属性。
		}
		$scope.search();//执行搜索
	}
	//构建分页标签
var	buildPageLabel=function (){
		//定义一个页码的数组
	$scope.pageLabel=[];
	var firstPage=1;

	var lastPage=$scope.resultMap.totalPages;//查询回来的最后一页
	var endPage=lastPage;

$scope.firstDot=false;
$scope.lastDot=false;

	//业务自己设定，每页显示5个页码
	if (lastPage>5){
		if ($scope.searchMap.pageNo<=3){
			endPage=5;
			$scope.lastDot=true;
		}else if ($scope.searchMap.pageNo>=(lastPage-2)){
         firstPage=lastPage-4;
			$scope.firstDot=true;
		}else{
			//处于在中间部分
			firstPage=$scope.searchMap.pageNo-2;
			endPage=$scope.searchMap.pageNo+2;
			$scope.firstDot=true;
			$scope.lastDot=true;
		}
	}
	for (var i=firstPage;i<=endPage;i++){
		$scope.pageLabel.push(i);
	}
}
//根据页码查询
$scope.queryByPage=function (pageNo){
		if (pageNo<1){
			pageNo=1;
		}else if (pageNo>=$scope.resultMap.totalPages){
			pageNo=$scope.resultMap.totalPages;
		}
	$scope.searchMap.pageNo=parseInt(pageNo);
		$scope.search();
}
//设置排序规则
	$scope.sortSearch=function (sortField,sort){
       $scope.searchMap.sortField=sortField;
       $scope.searchMap.sort=sort;//给searchMap搜索对象中的sort条件等于html页面传过来的搜索排序方式
		$scope.search();//然后调用search（）方法。
	}
//判断关键字是不是品牌，如果是关键字就隐藏品牌
	$scope.keywordsIsBrand=function (){
		for (var i=0;i<$scope.resultMap.brandList.length;i++){
			if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				//如果包含
				return true;
			}
		}
		return false;
	}
	//接受查询的字符串斌查询
	$scope.loadkeywords=function (){
		$scope.searchMap.keywords=$location.search()['keywords'];//从contentController.js中获取search（）的参数赋值给其keywords
		$scope.search();
	}
});