 //商品类目控制层 
app.controller('itemCatController' ,function($scope,$controller,typeTemplateService   ,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId=$scope.parentId;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					$scope.searchEntity={parentId:$scope.entity.parentId};//为下一级目录传父级id
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
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	$scope.parentId=0;
	//搜索
	$scope.search=function(page,rows){
		$scope.parentId=$scope.searchEntity.parentId;
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//通过上一级id查找子id信息
	$scope.findByParentId=function (parentId){
		itemCatService.findByParentId(parentId).success(
			function (response){
				$scope.list=response;
			}

		)
	}
	//1.定义类目级别，人为设定，只显示三级目录
	$scope.grade=1;
	$scope.setGrade=function (value){
		$scope.grade=value;
	}
	//2.查询数据，设定二级目录，和三级目录名称
	$scope.selectList=function (entity){
		if ($scope.grade==1){//如果存在一级目录，那么二级目录就为空三级目录也为空
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if ($scope.grade==2){
			$scope.entity_1=entity;
			$scope.entity_2=null;
		}
		if ($scope.grade==3){
			$scope.entity_2=entity;
		}
		$scope.searchEntity={parentId:entity.id};//为下一级目录传父级id
		$scope.reloadList();//从新调用分页查询。
	}

	$scope.typeTemplateList={data:[]};//模板列表
	//读取模板列表
	$scope.findtypeTemplateList=function(){
		typeTemplateService.selectOptionList().success(
			function(response){
				$scope.typeTemplateList={data:response};
			}
		);
	}
    
});	