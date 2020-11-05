 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller ,specificationService,brandService ,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.brandIds=  JSON.parse($scope.entity.brandIds);//转换品牌列表
				$scope.entity.specIds=  JSON.parse($scope.entity.specIds);//转换规格列表
				$scope.entity.customAttributeItems= JSON.parse($scope.entity.customAttributeItems);//转换扩展属性
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	$scope.brandList={data:[]}//品牌列表
	//读取品牌列表
	$scope.findBrandList=function (){
		brandService.selectOptionList().success(
			function (response){
				$scope.brandList={data:response};
			}
		);
	}
$scope.specList={data:[]};//规格列表
	//读取规格列表
	$scope.findSpecList=function (){
		specificationService.selectOptionList().success(
			function (response){
				$scope.specList={data:response};
			}
		);
	}
	//定义同时初始化品牌、规格列表数据
	$scope.initSelect=function(){
		$scope.findSpecList();
		$scope.findBrandList();
	}
	//新增扩展属性行
	$scope.addTableRow=function(){
		$scope.entity.customAttributeItems.push({});
	}
		//删除扩展属性行
	$scope.deleTableRow=function(index){
		$scope.entity.customAttributeItems.splice(index,1);//删除
	}
	//提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
	//格式转换的方法
	$scope.jsonToString = function(jsonString,key){
		//1、jsonString 是 json格式的字符串 key 是 键
		// 1.1、json格式转换
		var json = JSON.parse(jsonString);
		//1.2、最终要返回的字符串
		var result = '';
		//2、循环
		for(var i=0;i<json.length;i++){
			//2.1 如果不是最后一个值 则在后面的数据拼接 , 仅是让第一个值前面无，
			if(i>0){
				result += ",";
			}
			result += json[i][key];
		}
		return result;
	}

});	